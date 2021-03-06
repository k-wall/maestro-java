package org.maestro.reports.downloaders;

import org.maestro.client.Maestro;
import org.maestro.reports.organizer.Organizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to build the reports downloader
 */

public class DownloaderBuilder {
    private static final Logger logger = LoggerFactory.getLogger(DownloaderBuilder.class);

    private DownloaderBuilder() {}


    /**
     * Build a report downloader
     * @param name The name of the downloader ("broker" or "default")
     * @param maestro Maestro instance (required for "broker")
     * @param baseDir Directory where to save the files
     * @return The reports downloader
     */
    public static ReportsDownloader build(final String name, final Maestro maestro, final String baseDir) {
        if (name != null && name.toLowerCase().equals("broker")) {
            logger.debug("Using the broker report downloader");
            return new BrokerDownloader(maestro, baseDir);
        }

        logger.debug("Using the default (HTTP) report downloader");
        return new DefaultDownloader(baseDir);
    }



    /**
     * Build a report downloader
     * @param name The name of the downloader ("broker" or "default")
     * @param maestro Maestro instance (required for "broker")
     * @param organizer Report directory organizer (must be NodeOrganizer for "broker")
     * @return The reports downloader
     */
    public static ReportsDownloader build(final String name, final Maestro maestro, final Organizer organizer) {
        if (name != null && name.toLowerCase().equals("broker")) {
            return new BrokerDownloader(maestro, organizer);
        }

        return new DefaultDownloader(organizer);
    }
}
