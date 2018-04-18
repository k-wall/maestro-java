/*
 * Copyright 2018 Otavio R. Piske <angusyoung@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.maestro.plotter.common.graph;


import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.internal.chartpart.Chart;
import org.maestro.plotter.common.ReportData;
import org.maestro.plotter.common.exceptions.EmptyDataSet;
import org.maestro.plotter.common.exceptions.IncompatibleDataSet;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * A base class for HDR plotters
 */
public abstract class AbstractPlotter<T extends ReportData> {
    private int outputWidth = 1200;
    private int outputHeight = 700;
    private boolean plotGridLinesVisible = true;

    private ChartProperties chartProperties = new ChartProperties();

    /**
     * Sets the output width for the graph
     * @param outputWidth the width in pixels
     */
    public void setOutputWidth(int outputWidth) {
        this.outputWidth = outputWidth;
    }


    /**
     * Gets the output width
     * @return
     */
    public int getOutputWidth() {
        return outputWidth;
    }

    /**
     * Sets the output height for the graph
     * @param outputHeight the height in pixels
     */
    public void setOutputHeight(int outputHeight) {
        this.outputHeight = outputHeight;
    }


    /**
     * Gets the output height
     * @return
     */
    public int getOutputHeight() {
        return outputHeight;
    }


    /**
     * Sets the the grid lines should be visible
     * @param plotGridLinesVisible true to make the grid lines visible or false otherwise
     */
    public void setPlotGridLinesVisible(boolean plotGridLinesVisible) {
        this.plotGridLinesVisible = plotGridLinesVisible;
    }


    public boolean isPlotGridLinesVisible() {
        return plotGridLinesVisible;
    }

    /**
     * Get the chart properties
     * @return
     */
    public ChartProperties getChartProperties() {
        return chartProperties;
    }

    /**
     * Set the chart properties
     * @param chartProperties
     */
    public void setChartProperties(ChartProperties chartProperties) {
        this.chartProperties = chartProperties;
    }


    protected void validateDataSet(final List<?> xData, final List<?> yData) throws EmptyDataSet, IncompatibleDataSet {
        if (xData == null || xData.size() == 0) {
            throw new EmptyDataSet("The 'X' column data set is empty");
        }

        if (yData == null || yData.size() == 0) {
            throw new EmptyDataSet("The 'Y' column data set is empty");
        }

        if (xData.size() != yData.size()) {
            throw new IncompatibleDataSet("The data set size does not match");
        }
    }

    protected void updateChart(final String title, final String seriesName, final String xTitle, final String yTitle) {
        getChartProperties().setTitle(title);
        getChartProperties().setSeriesName(seriesName);
        getChartProperties().setxTitle(xTitle);
        getChartProperties().setyTitle(yTitle);
    }

    protected void encode(Chart chart, File outputFile) throws IOException {
        BitmapEncoder.saveBitmap(chart, outputFile.getPath(), BitmapEncoder.BitmapFormat.PNG);
    }

    abstract public void plot(final T reportData, final File outputFile) throws IOException, EmptyDataSet, IncompatibleDataSet;
}
