package tv.toner.manager;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import org.springframework.stereotype.Component;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.util.StringConverter;
import tv.toner.defs.ChartKey;
import tv.toner.defs.SeriesDef;
import tv.toner.utils.ChartUtil;

@Getter
@Component
public class ChartManager {

    private final ConcurrentHashMap<ChartKey, ChartUtil> chartMap = new ConcurrentHashMap<>();

    public void registerChart(ChartKey chartKey, LineChart<Number, Number> chart, List<SeriesDef> seriesKeys) {
        ChartUtil chartUtil = new ChartUtil();
        chartUtil.initializeChart(chart, seriesKeys);
        chartMap.put(chartKey, chartUtil);
    }

    public ChartUtil getChartUtil(ChartKey chartKey) {
        return chartMap.get(chartKey);
    }

    /**
     * Set the chart Title
     */
    public void chartProperties(LineChart<Number, Number> lineChart, String chartTitle) {
        lineChart.setTitle(chartTitle);
        lineChart.setAnimated(true);        // Disable animations for real-time updates
        lineChart.setCreateSymbols(false);   // Draw only lines for clarity
    }

    /**
     * X-axis initialization
     */
    public void initXAxis(NumberAxis xAxis, String xAxisLabel, int tickUnit, int minorTickUnit) {
        xAxis.setLabel(xAxisLabel);
        xAxis.setAutoRanging(false);         // We'll manage the range manually
        xAxis.setForceZeroInRange(false);    // Allow non-zero lower bound after 30s
        xAxis.setTickUnit(tickUnit);                // Major tick every 5 seconds
        xAxis.setMinorTickCount(minorTickUnit);          // 4 minor ticks (approx. 1 sec intervals)
        xAxis.setTickMarkVisible(true);
        xAxis.setTickLabelsVisible(true);

        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return String.format("%.0f", object.doubleValue());
            }
            @Override
            public Number fromString(String string) {
                try {
                    return Double.parseDouble(string);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        });
    }

    /**
     * Y-axis initialization
     */
    public void initYAxis(NumberAxis yAxis, String yAxisLabel, int lowerBound, int upperBound, int tickUnit) {
        yAxis.setLabel(yAxisLabel);
        yAxis.setAutoRanging(false); // Fixed Y-axis range
        yAxis.setLowerBound(lowerBound);
        yAxis.setUpperBound(upperBound);
        yAxis.setTickUnit(tickUnit);
    }
}
