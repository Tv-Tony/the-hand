package tv.toner.defs;

import javafx.scene.chart.XYChart;
import lombok.Getter;

@Getter
public enum SeriesDefs {

    /**
     * Series For Accelerometer X Data
     */
    FINGER_ONE("Accelerometer One Raw Data"),
    FINGER_TWO("Accelerometer Two Raw Data"),
    FINGER_THREE("Accelerometer Three Raw Data"),

    /**
     * Series For Accelerometer Pitch Data
     */
    ANGLE_DATA_FINGER_ONE("Finger One Raw Data"),
    ANGLE_DATA_FINGER_TWO("Finger Two Raw Data"),
    ANGLE_DATA_FINGER_THREE("Finger Three Raw Data")

    ;

    private final String displayName;

    SeriesDefs(String displayName) {
        this.displayName = displayName;
    }
}
