package net.orpiske.mpt.main.actions;

import net.orpiske.mpt.reports.ReportGenerator;
import net.orpiske.mpt.reports.plotter.HdrPlotterWrapper;
import net.orpiske.mpt.reports.plotter.PlotterWrapperFactory;
import net.orpiske.mpt.reports.processors.DiskCleaner;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportAction extends Action {
    private static final String DEFAULT_TIME_UNIT = "1000";
    private CommandLine cmdLine;

    private String directory;
    private boolean clean;


    private static class HdrPlotterWrapperFactory implements PlotterWrapperFactory<HdrPlotterWrapper> {
        private static final Logger logger = LoggerFactory.getLogger(HdrPlotterWrapperFactory.class);
        private String unitRate;

        public HdrPlotterWrapperFactory(final String unitRate) {
            this.unitRate = unitRate;

            logger.info("Creating a custom HDR Plotter Factory");
        }

        @Override
        public HdrPlotterWrapper newPlotterWrapper() {
            HdrPlotterWrapper ret = new HdrPlotterWrapper(unitRate);

            return ret;
        }
    }

    private HdrPlotterWrapperFactory hdrPlotterWrapperFactory;

    public ReportAction(String[] args) {
        processCommand(args);
    }

    protected void processCommand(String[] args) {
        CommandLineParser parser = new DefaultParser();

        Options options = new Options();

        options.addOption("h", "help", false, "prints the help");
        options.addOption("d", "directory", true, "the directory to generate the report");
        options.addOption("l", "log-level", true, "the log level to use [trace, debug, info, warn]");
        options.addOption("C", "clean", false, "clean the report directory after processing");
        options.addOption("r", "unit-rate", false, "unit-rate to use [default: 1000]");

        try {
            cmdLine = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            help(options, -1);
        }

        if (cmdLine.hasOption("help")) {
            help(options, 0);
        }

        directory = cmdLine.getOptionValue('d');
        if (directory == null) {
            System.err.println("The input directory is a required option");
            help(options, 1);
        }

        String logLevel = cmdLine.getOptionValue('l');
        if (logLevel != null) {
            configureLogLevel(logLevel);
        }

        clean = cmdLine.hasOption('C');

        String timeUnit = cmdLine.getOptionValue('r');
        if (timeUnit == null) {
            timeUnit = DEFAULT_TIME_UNIT;
        }

        hdrPlotterWrapperFactory = new HdrPlotterWrapperFactory(timeUnit);
    }

    public int run() {
        try {
            ReportGenerator reportGenerator = new ReportGenerator(directory);

            if (clean) {
                reportGenerator.getPostProcessors().add(new DiskCleaner());
            }

            reportGenerator.setHdrPlotterWrapperFactory(hdrPlotterWrapperFactory);
            reportGenerator.generate();
            System.out.println("Report generated successfully");
            return 0;
        }
        catch (Exception e) {
            System.err.println("Unable to generate the performance test reports");
            e.printStackTrace();
            return 1;
        }
    }
}
