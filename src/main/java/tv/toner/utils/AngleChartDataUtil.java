package tv.toner.utils;

import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import lombok.Getter;

/**
 * This is a utility for plotting the arduino mpu6050 data for analysis, in the future we will have to expand on this
 * class allowing for multiple charts since we will have multiple sensors. We will also add a filtered data series
 *
 * @author Antonin Vychodil <a_vychodil [at] utb.cz>
 *
 */
@Component
public class AngleChartDataUtil {

    private final XYChart.Series<Number, Number> rawDataSeries;
    private final XYChart.Series<Number, Number> filteredDataSeries;

    @Getter
    private LineChart<Number, Number> chart;

    private final long startTime = System.currentTimeMillis();
    ;
    private final double chartWindowSize = 60.0;  // display a 60-second window

    private long lastAxisUpdate = 0;
    private final long axisUpdateInterval = 100; // update every 100ms

    public AngleChartDataUtil() {
        rawDataSeries = new XYChart.Series<>();
        rawDataSeries.setName("Accelerometer Raw Data");
        filteredDataSeries = new XYChart.Series<>();
        filteredDataSeries.setName("Accelerometer Filtered Data");
    }

    /**
     * Initializes the chart by adding the data series and storing a reference.
     */
    public void initializeChart(LineChart<Number, Number> chart) {
        this.chart = chart;
        if (!chart.getData().contains(rawDataSeries)) {
            chart.getData().add(rawDataSeries);
        }
        if (!chart.getData().contains(filteredDataSeries)) {
            chart.getData().add(filteredDataSeries);
        }
    }

    /**
     * Main method that is used by the event listener to process the data onto the chart
     */
    public void updateChartData(double newValue) {
        double elapsedSec = (System.currentTimeMillis() - startTime) / 1000.0;
        updateChart(newValue, elapsedSec);
    }

    /**
     * This method is to update the chart in the Java FX thread
     */
    private void updateChart(double value, double elapsedSec) {
        if (chart == null) return;
        Platform.runLater(() -> {
            rawDataSeries.getData().add(new XYChart.Data<>(elapsedSec, value)); // Add the new data point.

            // Remove data points older than half the window (to maintain a rolling window).
            double cutoffTime = elapsedSec - (chartWindowSize / 2);
            while (!rawDataSeries.getData().isEmpty() && rawDataSeries.getData().get(0).getXValue().doubleValue() < cutoffTime) {
                rawDataSeries.getData().remove(0);
            }

            long now = System.currentTimeMillis(); // Update axis bounds only if sufficient time has passed.
            if (now - lastAxisUpdate > axisUpdateInterval) {
                NumberAxis xAxis = (NumberAxis) chart.getXAxis();
                xAxis.tickLabelFillProperty().set(Color.RED);
                double lowerBound = Math.max(elapsedSec - (chartWindowSize / 2), 0.0);
                double upperBound = elapsedSec + (chartWindowSize / 2);

                // Update bounds smoothly.
                xAxis.setLowerBound(lowerBound);
                xAxis.setUpperBound(upperBound);
                xAxis.setTickUnit(10);

                xAxis.requestLayout();  // Request layout update so the tick labels are refreshed.

                lastAxisUpdate = now;
            }
        });
    }

    public void updateFilteredChartData(double newFilteredValue) {
        if (chart == null) return;
        double elapsedSec = (System.currentTimeMillis() - startTime) / 1000.0;
        Platform.runLater(() -> {
            filteredDataSeries.getData().add(new XYChart.Data<>(elapsedSec, newFilteredValue));
            // Optionally, remove old points or update axis bounds similarly
        });
    }
}
