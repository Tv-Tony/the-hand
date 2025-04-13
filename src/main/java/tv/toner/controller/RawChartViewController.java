package tv.toner.controller;

import java.net.URL;
import java.util.*;

import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import tv.toner.defs.ChartKey;
import tv.toner.defs.SeriesDef;
import tv.toner.defs.ToggleButtonDef;
import tv.toner.manager.ChartManager;
import tv.toner.utils.ToggleButtonUtil;

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

    @FXML
    private VBox toggleBox;

    private final ChartManager chartManager;

    @Autowired
    public RawChartViewController(ChartManager chartManager) {
        this.chartManager = chartManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chartManager.chartProperties(lineChart, "Accelerometer Data (60-second Rolling Window)");
        chartManager.initXAxis(xAxis, "Time (s)", 5, 4);
        chartManager.initYAxis(yAxis, "Accelerometer Value", -30000, 34000, 2000);
        chartManager.registerChart(ChartKey.DATA_CHART, lineChart, Arrays.asList(
                SeriesDef.FINGER_ONE,
                SeriesDef.FINGER_ONE_FILTERED,
                SeriesDef.FINGER_TWO,
                SeriesDef.FINGER_TWO_FILTERED,
                SeriesDef.FINGER_THREE,
                SeriesDef.FINGER_THREE_FILTERED
                )); // Initialize the chart and store the reference in ChartUtil

        addGroupedToggles();
    }

    private void addGroupedToggles() {

        ToggleButtonUtil.registerToggleButtons(Arrays.asList(
                ToggleButtonDef.FINGER_ONE,
                ToggleButtonDef.FINGER_TWO,
                ToggleButtonDef.FINGER_THREE),
                toggleBox,
                chartManager);
    }
}
