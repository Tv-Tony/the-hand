package tv.toner.dto;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JointDTO {

    private String name;  // To hold the name from JSON
    private Joint joint;  // This will be the nested joint object

    // Getter and setter methods for all fields

    public static class Joint {
        private double jox;  // Rotation along X-axis
        private double joy;  // Rotation along Y-axis
        private double joz;  // Rotation along Z-axis

        // Getter and setter methods for jox, joy, joz
    }
}
