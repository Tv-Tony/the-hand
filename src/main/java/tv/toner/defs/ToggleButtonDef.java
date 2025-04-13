package tv.toner.defs;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ToggleButtonDef {

    FINGER_ONE("Sensor 1", SeriesDef.FINGER_ONE, SeriesDef.FINGER_ONE_FILTERED, ChartKey.DATA_CHART),
    FINGER_TWO("Sensor 2", SeriesDef.FINGER_TWO, SeriesDef.FINGER_TWO_FILTERED, ChartKey.DATA_CHART),
    FINGER_THREE("Sensor 3", SeriesDef.FINGER_THREE, SeriesDef.FINGER_THREE_FILTERED, ChartKey.DATA_CHART),

    FINGER_ONE_ANGLE("Sensor 1", SeriesDef.ANGLE_DATA_FINGER_ONE, SeriesDef.ANGLE_DATA_FINGER_ONE_FILTERED, ChartKey.ANGLE_CHART),
    FINGER_TWO_ANGLE("Sensor 2", SeriesDef.ANGLE_DATA_FINGER_TWO, SeriesDef.ANGLE_DATA_FINGER_TWO_FILTERED, ChartKey.ANGLE_CHART),
    FINGER_THREE_ANGLE("Sensor 3", SeriesDef.ANGLE_DATA_FINGER_THREE, SeriesDef.ANGLE_DATA_FINGER_THREE_FILTERED, ChartKey.ANGLE_CHART);

    private final String label;
    private final SeriesDef rawSeries;
    private final SeriesDef filteredSeries;
    private final ChartKey chartKey;

    ToggleButtonDef(String label, SeriesDef rawSeries, SeriesDef filteredSeries, ChartKey chartKey) {
        this.label = label;
        this.rawSeries = rawSeries;
        this.filteredSeries = filteredSeries;
        this.chartKey = chartKey;
    }
}
