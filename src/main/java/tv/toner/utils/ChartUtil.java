package tv.toner.utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tv.toner.defs.SeriesDef;

/**
 * This is a utility for plotting the arduino mpu6050 data for analysis, in the future we will have to expand on this
 * class allowing for multiple charts since we will have multiple sensors. We will also add a filtered data series
 *
 * @author Antonin Vychodil <a_vychodil [at] utb.cz>
 *
 */
@Slf4j
@Component
@NoArgsConstructor
public class ChartUtil {

    @Getter
    private final Map<SeriesDef, XYChart.Series<Number, Number>> seriesMap = new LinkedHashMap<>();

    @Getter
    private LineChart<Number, Number> chart;

    private final long startTime = System.currentTimeMillis();;
    private final double chartWindowSize = 60.0;  // display a 60-second window

    private long lastAxisUpdate = 0;
    private final long axisUpdateInterval = 100; // update every 100ms

    public void initializeNewSeries(SeriesDef key) {
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
    public void initializeChart(LineChart<Number, Number> chart, List<SeriesDef> seriesKeys) {
        this.chart = chart;
        seriesMap.clear();
        seriesKeys.forEach(this::initializeNewSeries);
    }

    /**
     * Main method to update a specific data series by name.
     * Call this from your listener or data processor.
     */
    public void updateChartData(SeriesDef key, double newValue) {
        double elapsedSec = (System.currentTimeMillis() - startTime) / 1000.0;
        updateChart(key, newValue, elapsedSec);
    }

    /**
     * Updates the specified series on the JavaFX thread.
     * Maintains a rolling window and updates X-axis bounds.
     */
    private void updateChart(SeriesDef key, double value, double elapsedSec) {
        if (chart == null || !seriesMap.containsKey(key)) return;

        try {
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
        } catch (Exception e) {
            log.warn("Error  updating chart data for key: {}", key.getDisplayName());
        }
    }

    /**
     * Removes a data series from the chart and internal map.
     */
    public void removeSeries(SeriesDef key) {
        if (chart == null || !seriesMap.containsKey(key)) return;
        XYChart.Series<?, ?> series = seriesMap.get(key);
        Platform.runLater(() -> {
            chart.getData().remove(series);
            seriesMap.remove(key);
        });
    }
}