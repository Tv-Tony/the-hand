package tv.toner.utils;

import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import tv.toner.defs.ToggleButtonDef;
import tv.toner.manager.ChartManager;

import java.util.List;

public class ToggleButtonUtil {

    public static void registerToggleButtons(List<ToggleButtonDef> toggleButtons, VBox toggleBox, ChartManager chartManager) {
        for (ToggleButtonDef toggleButtonDef : toggleButtons) {
            CheckBox toggle = new CheckBox(toggleButtonDef.getLabel());
            toggle.setSelected(true);
            toggle.setStyle("-fx-font-size: 13px; -fx-text-fill: #333;");

            // Attach an event listener that triggers when the checkbox selection changes.
            toggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
                updateChartSeriesVisibility(toggleButtonDef, newValue, chartManager);
            });
            toggleBox.getChildren().add(toggle);
        }
    }

    public static void updateChartSeriesVisibility(ToggleButtonDef toggleButtonDef, boolean isVisible, ChartManager chartManager) {
        ChartUtil chartUtil = chartManager.getChartUtil(toggleButtonDef.getChartKey());
        if (isVisible) {
            chartUtil.initializeNewSeries(toggleButtonDef.getFilteredSeries());
            chartUtil.initializeNewSeries(toggleButtonDef.getRawSeries());
        } else {
            chartUtil.removeSeries(toggleButtonDef.getFilteredSeries());
            chartUtil.removeSeries(toggleButtonDef.getRawSeries());
        }
    }
}
