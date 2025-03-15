package tv.toner.dummy;

public enum JointDef {
    // Thumb
    THUMB_METACARPAL("#Finger_01"),
    THUMB_PROXIMAL("#Finger_02"),
    THUMB_DISTAL("#Finger_03"),

    // Index Finger
    INDEX_METACARPAL("#Finger_10"),
    INDEX_PROXIMAL("#Finger_11"),
    INDEX_INTERMEDIATE("#Finger_12"),
    INDEX_DISTAL("#Finger_13"),

    // Middle Finger
    MIDDLE_METACARPAL("#Finger_20"),
    MIDDLE_PROXIMAL("#Finger_21"),
    MIDDLE_INTERMEDIATE("#Finger_22"),
    MIDDLE_DISTAL("#Finger_23"),

    // Ring Finger
    RING_METACARPAL("#Finger_30"),
    RING_PROXIMAL("#Finger_31"),
    RING_INTERMEDIATE("#Finger_32"),
    RING_DISTAL("#Finger_33"),

    // Pinky Finger
    PINKY_METACARPAL("#Finger_40"),
    PINKY_PROXIMAL("#Finger_41"),
    PINKY_INTERMEDIATE("#Finger_42"),
    PINKY_DISTAL("#Finger_43");

    private final String bonePattern;

    // Constructor to initialize the string representation
    JointDef(String bonePattern) {
        this.bonePattern = bonePattern;
    }

    // Method to get the string representation of the bone
    public String getBonePattern() {
        return this.bonePattern;
    }

    public static String getBonePatternByName(String name) {
        for (JointDef joint : JointDef.values()) {
            if (joint.name().equalsIgnoreCase(name)) {
                return joint.bonePattern;  // Return the bone pattern of the matched enum constant
            }
        }
        return null;  // Return null if no match found
    }
}
