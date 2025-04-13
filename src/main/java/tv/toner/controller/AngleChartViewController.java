package tv.toner.controller;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

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

    @FXML
    private VBox toggleBox;

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
        chartManager.registerChart(ChartKey.ANGLE_CHART, lineChart, Arrays.asList(
                SeriesDef.ANGLE_DATA_FINGER_ONE,
                SeriesDef.ANGLE_DATA_FINGER_ONE_FILTERED,
                SeriesDef.ANGLE_DATA_FINGER_TWO,
                SeriesDef.ANGLE_DATA_FINGER_TWO_FILTERED,
                SeriesDef.ANGLE_DATA_FINGER_THREE,
                SeriesDef.ANGLE_DATA_FINGER_THREE_FILTERED
        ));

        addGroupedToggles();
    }

    private void addGroupedToggles() {

        ToggleButtonUtil.registerToggleButtons(Arrays.asList(
                        ToggleButtonDef.FINGER_ONE_ANGLE,
                        ToggleButtonDef.FINGER_TWO_ANGLE,
                        ToggleButtonDef.FINGER_THREE_ANGLE),
                toggleBox,
                chartManager);
    }
}
