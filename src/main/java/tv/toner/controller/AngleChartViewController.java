package tv.toner.controller;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import tv.toner.defs.ChartKey;
import tv.toner.defs.SeriesDefs;
import tv.toner.manager.ChartManager;

/**
 * This controller is responsible for displaying angle data in one of the tabs
 *
 * @author Antonin Vychodil <a_vychodil [at] utb.cz>
 *
 */
@Controller
public class AngleChartViewController implements Initializable {

    @FXML
    public NumberAxis xAxis;

    @FXML
    public NumberAxis yAxis;

    @FXML
    private LineChart<Number, Number> lineChart;

    private final ChartManager chartManager;

    @Autowired
    public AngleChartViewController(ChartManager chartManager) {
        this.chartManager = chartManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chartManager.chartProperties(lineChart, "Angle Data (60-second Rolling Window)");
        chartManager.initXAxis(xAxis, "Time (s)", 5, 4);
        chartManager.initYAxis(yAxis, "Angle In Degrees", -180, 180, 5);
        chartManager.registerChart(ChartKey.X_ANGLE_CHART, lineChart, Arrays.asList(
                SeriesDefs.ANGLE_DATA_FINGER_ONE
                ,SeriesDefs.ANGLE_DATA_FINGER_TWO
//                ,SeriesDefs.ANGLE_DATA_FINGER_THREE
        ));
    }
}
