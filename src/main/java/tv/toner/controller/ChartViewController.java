package tv.toner.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.util.StringConverter;
import tv.toner.utils.ChartUtil;

/**
 * This controller is responsible for displaying a chart in one of the tabs
 *
 * @author Antonin Vychodil <a_vychodil [at] utb.cz>
 *
 */
@Component
public class ChartViewController implements Initializable {

    @FXML
    public NumberAxis xAxis;

    @FXML
    public NumberAxis yAxis;

    @FXML
    private LineChart<Number, Number> lineChart;

    private final ChartUtil chartUtil;

    @Autowired
    public ChartViewController(ChartUtil chartUtil) {
        this.chartUtil = chartUtil;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chartProperties();
        initXAxis();
        initYAxis();

        chartUtil.initializeChart(lineChart); // Initialize the chart and store the reference in ChartUtil
    }

    private void chartProperties() {
        lineChart.setTitle("Accelerometer Data (60-second Rolling Window)");
        lineChart.setAnimated(true);        // Disable animations for real-time updates
        lineChart.setCreateSymbols(false);   // Draw only lines for clarity
    }

    /**
     * X-axis initialization
     */
    private void initXAxis() {
        this.xAxis = (NumberAxis) lineChart.getXAxis();
        xAxis.setLabel("Time (s)");
        xAxis.setAutoRanging(false);         // We'll manage the range manually
        xAxis.setForceZeroInRange(false);    // Allow non-zero lower bound after 30s
        xAxis.setTickUnit(5);                // Major tick every 5 seconds
        xAxis.setMinorTickCount(4);          // 4 minor ticks (approx. 1 sec intervals)
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
    private void initYAxis() {
        this.yAxis = (NumberAxis) lineChart.getYAxis();
        yAxis.setLabel("Accelerometer Value");
        yAxis.setAutoRanging(false); // Fixed Y-axis range
        yAxis.setLowerBound(5000);
        yAxis.setUpperBound(30000);
        yAxis.setTickUnit(1000);
    }
}
