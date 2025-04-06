package tv.toner.controller;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import tv.toner.defs.ChartKey;
import tv.toner.defs.SeriesDefs;
import tv.toner.manager.ChartManager;

/**
 * This controller is responsible for displaying a chart in one of the tabs
 *
 * @author Antonin Vychodil <a_vychodil [at] utb.cz>
 *
 */
@Component
public class RawChartViewController implements Initializable {

    @FXML
    public NumberAxis xAxis;

    @FXML
    public NumberAxis yAxis;

    @FXML
    private LineChart<Number, Number> lineChart;

    private final ChartManager chartManager;

    @Autowired
    public RawChartViewController(ChartManager chartManager) {
        this.chartManager = chartManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chartManager.chartProperties(lineChart, "Accelerometer Data (60-second Rolling Window)");
        chartManager.initXAxis(xAxis, "Time (s)", 5, 4);
        chartManager.initYAxis(yAxis, "Accelerometer Value", -10000, 34000, 2000);
        chartManager.registerChart(ChartKey.AX_DATA_CHART, lineChart, Arrays.asList(
                        SeriesDefs.FINGER_ONE,
                        SeriesDefs.FINGER_TWO,
                        SeriesDefs.FINGER_THREE
                )); // Initialize the chart and store the reference in ChartUtil
    }
}
