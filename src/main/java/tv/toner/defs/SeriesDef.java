package tv.toner.defs;

import lombok.Getter;

@Getter
public enum SeriesDef {

    /**
     * Series For Accelerometer X Data
     */
    FINGER_ONE("Accelerometer One Raw Data"),
    FINGER_ONE_FILTERED("Accelerometer One Filtered Data"),

    FINGER_TWO("Accelerometer Two Raw Data"),
    FINGER_TWO_FILTERED("Accelerometer Two Filtered Data"),

    FINGER_THREE("Accelerometer Three Raw Data"),
    FINGER_THREE_FILTERED("Accelerometer Three Filtered Data"),

    /**
     * Series For Accelerometer Pitch Data
     */
    ANGLE_DATA_FINGER_ONE("Finger One Raw Data"),
    ANGLE_DATA_FINGER_ONE_FILTERED("Finger One Filtered Data"),

    ANGLE_DATA_FINGER_TWO("Finger Two Raw Data"),
    ANGLE_DATA_FINGER_TWO_FILTERED("Finger Two Filtered Data"),

    ANGLE_DATA_FINGER_THREE("Finger Three Raw Data"),
    ANGLE_DATA_FINGER_THREE_FILTERED("Finger Three Filtered Data");

    private final String displayName;
    private final ToggleButtonDef  toggleButtonDef;

    SeriesDef(String displayName) {
        this.displayName = displayName;
        this.toggleButtonDef = null;
    }
}
