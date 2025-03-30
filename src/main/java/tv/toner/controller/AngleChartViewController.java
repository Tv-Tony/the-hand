package tv.toner.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.util.StringConverter;
import tv.toner.utils.AngleChartDataUtil;

@Controller
public class AngleChartViewController implements Initializable {

    @FXML
    public NumberAxis xAxis;

    @FXML
    public NumberAxis yAxis;

    @FXML
    private LineChart<Number, Number> lineChart;

    private final AngleChartDataUtil angleChartUtil;

    public AngleChartViewController(AngleChartDataUtil angleChartUtil) {
        this.angleChartUtil = angleChartUtil;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chartProperties();
        initXAxis();
        initYAxis();

        angleChartUtil.initializeChart(lineChart); // Initialize the chart and store the reference in ChartUtil
    }

    private void chartProperties() {
        lineChart.setTitle("Angle Data (60-second Rolling Window)");
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
        yAxis.setLabel("Angle In Degrees");
        yAxis.setAutoRanging(false); // Fixed Y-axis range
        yAxis.setLowerBound(-180);
        yAxis.setUpperBound(180);
        yAxis.setTickUnit(5);
    }
}
