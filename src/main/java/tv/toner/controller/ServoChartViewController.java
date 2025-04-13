package tv.toner.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import tv.toner.defs.ChartKey;
import tv.toner.defs.SeriesDef;
import tv.toner.defs.ToggleButtonDef;
import tv.toner.manager.ChartManager;
import tv.toner.utils.ToggleButtonUtil;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

@Controller
public class ServoChartViewController implements Initializable {
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
    public ServoChartViewController(ChartManager chartManager) {
        this.chartManager = chartManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chartManager.chartProperties(lineChart, "Data Sent To Servo Data (60-second Rolling Window)");
        chartManager.initXAxis(xAxis, "Time (s)", 5, 4);
        chartManager.initYAxis(yAxis, "Servo Data", -20, 200, 20);
        chartManager.registerChart(ChartKey.SERVO_CHART, lineChart, Arrays.asList(
                SeriesDef.FINGER_ONE_SERVO,
                SeriesDef.FINGER_ONE_SERVO_FILTERED,
                SeriesDef.FINGER_TWO_SERVO,
                SeriesDef.FINGER_TWO_SERVO_FILTERED,
                SeriesDef.FINGER_THREE_SERVO,
                SeriesDef.FINGER_THREE_SERVO_FILTERED
        ));

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
