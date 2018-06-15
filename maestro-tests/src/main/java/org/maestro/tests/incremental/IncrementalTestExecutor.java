/*
 *  Copyright 2017 Otavio R. Piske <angusyoung@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.maestro.tests.incremental;

import org.maestro.client.Maestro;
import org.maestro.reports.ReportsDownloader;
import org.maestro.tests.AbstractTestExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IncrementalTestExecutor extends AbstractTestExecutor {
    private static final Logger logger = LoggerFactory.getLogger(IncrementalTestExecutor.class);

    private final IncrementalTestProfile testProfile;

    private long coolDownPeriod = 10000;
    private final IncrementalTestProcessor testProcessor;

    public IncrementalTestExecutor(final Maestro maestro, final ReportsDownloader reportsDownloader,
                                   final IncrementalTestProfile testProfile) {
        super(maestro, reportsDownloader);

        this.testProfile = testProfile;
        this.testProcessor = new IncrementalTestProcessor(testProfile, reportsDownloader);
    }

    public boolean run() {
        long repeat = testProfile.getEstimatedCompletionTime();

        try {
            // Clean up the topic
            getMaestro().collect();

            while (!testProcessor.isCompleted()) {
                int numPeers;
                if (testProfile.getManagementInterface() != null) {
                    numPeers = getNumPeers("sender", "receiver", "inspector");
                }
                else {
                    numPeers = getNumPeers("sender", "receiver");
                }

                resolveDataServers();
                processReplies(testProcessor, (int) repeat, numPeers);

                getReportsDownloader().getOrganizer().getTracker().setCurrentTest(testProfile.getTestExecutionNumber());

                testProfile.apply(getMaestro());
                testProcessor.resetNotifications();

                if (testProfile.getInspectorName() != null) {
                    startServices(testProfile.getInspectorName());
                }
                else {
                    startServices();
                }
                processNotifications(testProcessor, repeat, numPeers);

                testProfile.increment();
                if (testProfile.isOverCeiling()) {
                    break;
                }

                testProcessor.increaseFlushWaitSeconds();

                logger.info("Sleeping for {} milliseconds to let the broker catch up", coolDownPeriod);
                Thread.sleep(coolDownPeriod);
            }

            if (testProcessor.isSuccessful()) {
                return true;
            }
        }
        catch (Exception e) {
            logger.error("Error: {}", e.getMessage(), e);
        }

        return false;
    }

    @Override
    public long getCoolDownPeriod() {
        return coolDownPeriod;
    }

    @Override
    public void setCoolDownPeriod(long period) {
        this.coolDownPeriod = period;
    }
}
