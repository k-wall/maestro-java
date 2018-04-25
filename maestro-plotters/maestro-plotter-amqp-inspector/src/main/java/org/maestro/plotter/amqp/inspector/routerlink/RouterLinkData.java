package org.maestro.plotter.amqp.inspector.routerlink;

import org.maestro.plotter.common.ReportData;
import org.maestro.plotter.common.properties.annotations.PropertyName;
import org.maestro.plotter.common.properties.annotations.PropertyProvider;
import org.maestro.plotter.common.statistics.Statistics;
import org.maestro.plotter.common.statistics.StatisticsBuilder;

import java.time.Instant;
import java.util.*;

@PropertyName(name="routerLink")
public class RouterLinkData implements ReportData {
    public static final String DEFAULT_FILENAME = "routerLink.properties";

    private Set<RouterLinkRecord> recordSet = new TreeSet<>();
    private Statistics countStatistics = null;
    private Statistics deliveredStatistics = null;
    private Statistics undeliveredStatistics = null;
    private Statistics acceptedStatistics = null;
    private Statistics releasedStatistics = null;

    public void add(RouterLinkRecord record) {
        recordSet.add(record);
    }

    public List<Date> getPeriods() {
        List<Date> list = new ArrayList<>(recordSet.size());

        recordSet.forEach(item->list.add(Date.from(item.getTimestamp())));

        return list;
    }

    public Set<RouterLinkRecord> getRecordSet() {
        return new TreeSet<>(recordSet);
    }

    public RouterLinkRecord getAt(final Instant instant) {
        return recordSet.stream().findFirst().filter(record -> record.getTimestamp().equals(instant)).orElse(null);
    }

    /**
     * Number of records
     * @return
     */
    public int getNumberOfSamples() {
        return recordSet.size();
    }

    /**
     * Get the statistics for the delivered messages count
     * @return A Statistics object for the delivered messages count
     */
    @PropertyProvider(name="-delivered")
    public Statistics deliveredStatistics() {
        if (deliveredStatistics == null) {
            deliveredStatistics = StatisticsBuilder.of(recordSet.stream().mapToDouble(RouterLinkRecord::getDeliveryCount));
        }

        return deliveredStatistics;
    }

    /**
     * Get the statistics for the undelivered messages
     * @return A Statistics object for the undelivered messages
     */
    protected Statistics undeliveredStatistics() {
        if (undeliveredStatistics == null) {
            undeliveredStatistics = StatisticsBuilder.of(recordSet.stream().mapToDouble(RouterLinkRecord::getUndeliveredCount));
        }

        return undeliveredStatistics;
    }

    @PropertyProvider(name="-undelivered")
    public double getAddedCount() {
        return undeliveredStatistics().getMax();
    }

    /**
     * Get the statistics for the accepted messages
     * @return A Statistics object for the accepted messages
     */
    protected Statistics acceptedStatistics() {
        if (acceptedStatistics == null) {
            acceptedStatistics = StatisticsBuilder.of(recordSet.stream().mapToDouble(RouterLinkRecord::getAcceptedCount));
        }

        return acceptedStatistics;
    }

    @PropertyProvider(name="-undelivered")
    public double getAcceptedCount() {
        return acceptedStatistics().getMax();
    }

    /**
     * Get the statistics for the expired messages
     * @return A Statistics object for the expired messages
     */
    protected Statistics releasedStatistics() {
        if (releasedStatistics == null) {
            releasedStatistics = StatisticsBuilder.of(recordSet.stream().mapToDouble(RouterLinkRecord::getReleasedCount));
        }

        return releasedStatistics;
    }

    @PropertyProvider(name="-expiredCount")
    public double getReleasedCount() {
        return releasedStatistics().getMax();
    }

}
