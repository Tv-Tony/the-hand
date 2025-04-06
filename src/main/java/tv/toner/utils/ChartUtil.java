package tv.toner.utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tv.toner.defs.SeriesDefs;

/**
 * This is a utility for plotting the arduino mpu6050 data for analysis, in the future we will have to expand on this
 * class allowing for multiple charts since we will have multiple sensors. We will also add a filtered data series
 *
 * @author Antonin Vychodil <a_vychodil [at] utb.cz>
 *
 */
@Component
@NoArgsConstructor
public class ChartUtil {

    private final Map<SeriesDefs, XYChart.Series<Number, Number>> seriesMap = new LinkedHashMap<>();

    @Getter
    private LineChart<Number, Number> chart;

    private final long startTime = System.currentTimeMillis();;
    private final double chartWindowSize = 60.0;  // display a 60-second window

    private long lastAxisUpdate = 0;
    private final long axisUpdateInterval = 100; // update every 100ms

    private void initializeNewSeries(SeriesDefs key) {
        if (!seriesMap.containsKey(key)) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(key.getDisplayName());
            seriesMap.put(key, series);

            if (chart != null) {
                Platform.runLater(() -> chart.getData().add(series));
            }
        }
    }


    /**
     * Initializes the chart by adding the data series and storing a reference.
     */
    public void initializeChart(LineChart<Number, Number> chart, List<SeriesDefs> seriesKeys) {
        this.chart = chart;
        seriesMap.clear();
        seriesKeys.forEach(this::initializeNewSeries);
    }

    /**
     * Main method to update a specific data series by name.
     * Call this from your listener or data processor.
     */
    public void updateChartData(SeriesDefs key, double newValue) {
        double elapsedSec = (System.currentTimeMillis() - startTime) / 1000.0;
        updateChart(key, newValue, elapsedSec);
    }

    /**
     * Updates the specified series on the JavaFX thread.
     * Maintains a rolling window and updates X-axis bounds.
     */
    private void updateChart(SeriesDefs key, double value, double elapsedSec) {
        if (chart == null || !seriesMap.containsKey(key)) return;

        Platform.runLater(() -> {
            XYChart.Series<Number, Number> series = seriesMap.get(key);
            series.getData().add(new XYChart.Data<>(elapsedSec, value));

            double cutoffTime = elapsedSec - (chartWindowSize / 2);
            while (!series.getData().isEmpty() &&
                    series.getData().get(0).getXValue().doubleValue() < cutoffTime) {
                series.getData().remove(0);
            }

            long now = System.currentTimeMillis();
            if (now - lastAxisUpdate > axisUpdateInterval) {
                NumberAxis xAxis = (NumberAxis) chart.getXAxis();
                double lowerBound = Math.max(elapsedSec - (chartWindowSize / 2), 0.0);
                double upperBound = elapsedSec + (chartWindowSize / 2);

                xAxis.setLowerBound(lowerBound);
                xAxis.setUpperBound(upperBound);
                xAxis.setTickUnit(10);
                xAxis.requestLayout();

                lastAxisUpdate = now;
            }
        });
    }
}