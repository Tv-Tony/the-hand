package tv.toner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HandDTO {

    // Thumb joints
    private JointDTO thumbMetacarpal;
    private JointDTO thumbProximal;
    private JointDTO thumbDistal;

    // Index Finger joints
    private JointDTO indexMetacarpal;
    private JointDTO indexProximal;
    private JointDTO indexIntermediate;
    private JointDTO indexDistal;

    // Middle Finger joints
    private JointDTO middleMetacarpal;
    private JointDTO middleProximal;
    private JointDTO middleIntermediate;
    private JointDTO middleDistal;

    // Ring Finger joints
    private JointDTO ringMetacarpal;
    private JointDTO ringProximal;
    private JointDTO ringIntermediate;
    private JointDTO ringDistal;

    // Pinky Finger joints
    private JointDTO pinkyMetacarpal;
    private JointDTO pinkyProximal;
    private JointDTO pinkyIntermediate;
    private JointDTO pinkyDistal;
}
