package org.maestro.inspector.amqp;

import org.apache.commons.configuration.AbstractConfiguration;
import org.maestro.common.ConfigurationWrapper;
import org.maestro.common.client.MaestroReceiver;
import org.maestro.common.duration.TestDuration;
import org.maestro.common.duration.TestDurationBuilder;
import org.maestro.common.exceptions.DurationParseException;
import org.maestro.common.inspector.MaestroInspector;
import org.maestro.common.test.InspectorProperties;
import org.maestro.common.worker.TestLogUtils;
import org.maestro.common.worker.WorkerOptions;
import org.maestro.inspector.amqp.writers.ConnectionsInfoWriter;
import org.maestro.inspector.amqp.writers.GeneralInfoWriter;
import org.maestro.inspector.amqp.writers.QDMemoryInfoWriter;
import org.maestro.inspector.amqp.writers.RouteLinkInfoWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.File;
import java.time.LocalDateTime;

/**
 * A class for Interconnect inspector based on AMQP management
 */
public class InterconnectInspector implements MaestroInspector {
    private static final Logger logger = LoggerFactory.getLogger(InterconnectInspector.class);
    private long startedEpochMillis = Long.MIN_VALUE;
    private boolean running = false;
    private String url;
    private String user;
    private String password;
    private File baseLogDir;
    private TestDuration duration;
    private MaestroReceiver endpoint;

    private ConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private MessageProducer messageProducer;
    private MessageConsumer responseConsumer;
    private Destination tempDest;

    private int interval;

    private AbstractConfiguration config = ConfigurationWrapper.getConfig();

    public InterconnectInspector() {
        interval = config.getInteger("inspector.sleep.interval", 5000);
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void setWorkerOptions(final WorkerOptions workerOptions) throws DurationParseException {
        this.duration = TestDurationBuilder.build(workerOptions.getDuration());
    }

    @Override
    public void setBaseLogDir(File baseLogDir) {
        this.baseLogDir = baseLogDir;
    }

    @Override
    public void setEndpoint(MaestroReceiver endpoint) {
        this.endpoint = endpoint;
    }

    public boolean isRunning() {
        return running;
    }

    private void connect() throws JMSException {
        connectionFactory = new org.apache.qpid.jms.JmsConnectionFactory(url);
        Destination queue = new org.apache.qpid.jms.JmsQueue("$management");

        connection = connectionFactory.createConnection();
        connection.start();

        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        messageProducer = session.createProducer(queue);

        tempDest = session.createTemporaryQueue();
        responseConsumer = session.createConsumer(tempDest);
    }

    private void closeConnection() throws JMSException {
        session.close();
        connection.close();
    }

    /**
     * Start inspector
     * @return return code
     * @throws Exception implementation specific
     */
    @Override
    public int start() throws Exception {
        File logDir = TestLogUtils.nextTestLogDir(this.baseLogDir);
        InspectorProperties inspectorProperties = new InspectorProperties();

        RouteLinkInfoWriter routerLinkInfoWriter = new RouteLinkInfoWriter(logDir, "routerLink");
        ConnectionsInfoWriter connectionsInfoWriter = new ConnectionsInfoWriter(logDir, "connections");
        QDMemoryInfoWriter qdMemoryInfoWriter = new QDMemoryInfoWriter(logDir, "qdmemory");
        GeneralInfoWriter generalInfoWriter = new GeneralInfoWriter(logDir, "general");

        try {
            startedEpochMillis = System.currentTimeMillis();
            running = true;

            if (url == null) {
                logger.error("No management interface was given for the test. Therefore, ignoring");
                return 1;
            }

            logger.info("Inspector started");

            connect();

            InterconnectReadData readData = new InterconnectReadData(session,
                    tempDest,
                    responseConsumer,
                    messageProducer);


            while (duration.canContinue(this) && isRunning()) {
                LocalDateTime now = LocalDateTime.now();

                routerLinkInfoWriter.write(now, readData.collectRouterLinkInfo());
                connectionsInfoWriter.write(now, readData.collectConnectionsInfo());
                qdMemoryInfoWriter.write(now, readData.collectMemoryInfo());
                generalInfoWriter.write(now, readData.collectGeneralInfo());

                Thread.sleep(interval);
            }

            TestLogUtils.createSymlinks(this.baseLogDir, false);
            endpoint.notifySuccess("Inspector finished successfully");
            logger.debug("The test has finished and the Artemis inspector is terminating");

            return 0;
        } catch (InterruptedException eie) {
            TestLogUtils.createSymlinks(this.baseLogDir, false);
            endpoint.notifySuccess("Inspector finished successfully");
            throw eie;
        } catch (Exception e) {
            TestLogUtils.createSymlinks(this.baseLogDir, true);
            endpoint.notifyFailure("Inspector failed");
            throw e;
        } finally {
            startedEpochMillis = Long.MIN_VALUE;
            closeConnection();
            routerLinkInfoWriter.close();
            connectionsInfoWriter.close();
            qdMemoryInfoWriter.close();
            generalInfoWriter.close();
        }
    }

    @Override
    public void stop() throws Exception {
        running = false;
    }

    @Override
    public long startedEpochMillis() {
        return startedEpochMillis;
    }
}
