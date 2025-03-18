
package tv.toner.socket;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.javafx.experiments.importers.maya.Joint;
import com.javafx.experiments.shape3d.PolygonMeshView;
import com.javafx.experiments.shape3d.SkinningMesh;

import javafx.scene.Parent;
import tv.toner.dto.HandDTO;
import tv.toner.dto.JointStruct;
import tv.toner.dummy.JointDef;

public class HandRefresher {

    private static final Logger log = LoggerFactory.getLogger(HandRefresher.class);

    public static void refresh(List<Parent> forestRight, List<JointStruct> jointStruc, PolygonMeshView skinningRight) {
        jointStruc.forEach(joint -> {
            try {
                String bonePattern = JointDef.getBonePatternByName(joint.getName());
                Joint tempJoint = (Joint) forestRight.get(0).lookup(bonePattern);
                tempJoint.rx.setAngle(joint.getJoint().getJox());
                tempJoint.ry.setAngle(joint.getJoint().getJoy());
                tempJoint.rz.setAngle(joint.getJoint().getJoz());
                ((SkinningMesh) skinningRight.getMesh()).update();
            } catch (Exception e) {
                log.warn("Error Setting Values: {}", e.getMessage());
            }
        });
    }



//     Todo This is complete garbage code but for now is the simulation implementation

    /**
     * In the future once we understand how the data will be sent to us from the Accelerometers after we receive
     * them in the mail and create the glove, then we can hypothesize what data is relevant and how to process it.
     *
     * Probable example would be sending the data like key, there is the name of the joint and the values from there,
     * we have it as a list and then can process it better. For example  we can process it via parralel stream that way
     * we use our threads effectivly
     *
     */
    public static void refresh(List<Parent> forestRight, HandDTO handDTO) {
        // Thumb
        Joint thumbMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.THUMB_METACARPAL.getBonePattern());
        Joint thumbProximal = (Joint) forestRight.get(0).lookup(JointDef.THUMB_PROXIMAL.getBonePattern());
        Joint thumbDistal = (Joint) forestRight.get(0).lookup(JointDef.THUMB_DISTAL.getBonePattern());

        // Index Finger
        Joint indexMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.INDEX_METACARPAL.getBonePattern());
        Joint indexProximal = (Joint) forestRight.get(0).lookup(JointDef.INDEX_PROXIMAL.getBonePattern());
        Joint indexIntermediate = (Joint) forestRight.get(0).lookup(JointDef.INDEX_INTERMEDIATE.getBonePattern());
        Joint indexDistal = (Joint) forestRight.get(0).lookup(JointDef.INDEX_DISTAL.getBonePattern());

        // Middle Finger
        Joint middleMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.MIDDLE_METACARPAL.getBonePattern());
        Joint middleProximal = (Joint) forestRight.get(0).lookup(JointDef.MIDDLE_PROXIMAL.getBonePattern());
        Joint middleIntermediate = (Joint) forestRight.get(0).lookup(JointDef.MIDDLE_INTERMEDIATE.getBonePattern());
        Joint middleDistal = (Joint) forestRight.get(0).lookup(JointDef.MIDDLE_DISTAL.getBonePattern());

        // Ring Finger
        Joint ringMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.RING_METACARPAL.getBonePattern());
        Joint ringProximal = (Joint) forestRight.get(0).lookup(JointDef.RING_PROXIMAL.getBonePattern());
        Joint ringIntermediate = (Joint) forestRight.get(0).lookup(JointDef.RING_INTERMEDIATE.getBonePattern());
        Joint ringDistal = (Joint) forestRight.get(0).lookup(JointDef.RING_DISTAL.getBonePattern());

        // Pinky Finger
        Joint pinkyMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.PINKY_METACARPAL.getBonePattern());
        Joint pinkyProximal = (Joint) forestRight.get(0).lookup(JointDef.PINKY_PROXIMAL.getBonePattern());
        Joint pinkyIntermediate = (Joint) forestRight.get(0).lookup(JointDef.PINKY_INTERMEDIATE.getBonePattern());
        Joint pinkyDistal = (Joint) forestRight.get(0).lookup(JointDef.PINKY_DISTAL.getBonePattern());

//        // Setting Thumb Joint values
//        thumbMetacarpal.t.setX(handDTO.getThumbMetacarpal().getTranslateX());
//        thumbMetacarpal.t.setY(handDTO.getThumbMetacarpal().getTranslateY());
//        thumbMetacarpal.t.setZ(handDTO.getThumbMetacarpal().getTranslateZ());
//        thumbMetacarpal.jox.setAngle(handDTO.getThumbMetacarpal().getJox());
//        thumbMetacarpal.joy.setAngle(handDTO.getThumbMetacarpal().getJoy());
//        thumbMetacarpal.joz.setAngle(handDTO.getThumbMetacarpal().getJoz());
//        thumbMetacarpal.rx.setAngle(handDTO.getThumbMetacarpal().getRotateX());
//        thumbMetacarpal.ry.setAngle(handDTO.getThumbMetacarpal().getRotateY());
//        thumbMetacarpal.rz.setAngle(handDTO.getThumbMetacarpal().getRotateZ());
//
//        thumbProximal.t.setX(handDTO.getThumbProximal().getTranslateX());
//        thumbProximal.t.setY(handDTO.getThumbProximal().getTranslateY());
//        thumbProximal.t.setZ(handDTO.getThumbProximal().getTranslateZ());
//        thumbProximal.jox.setAngle(handDTO.getThumbProximal().getJox());
//        thumbProximal.joy.setAngle(handDTO.getThumbProximal().getJoy());
//        thumbProximal.joz.setAngle(handDTO.getThumbProximal().getJoz());
//        thumbProximal.rx.setAngle(handDTO.getThumbProximal().getRotateX());
//        thumbProximal.ry.setAngle(handDTO.getThumbProximal().getRotateY());
//        thumbProximal.rz.setAngle(handDTO.getThumbProximal().getRotateZ());
//
//        thumbDistal.t.setX(handDTO.getThumbDistal().getTranslateX());
//        thumbDistal.t.setY(handDTO.getThumbDistal().getTranslateY());
//        thumbDistal.t.setZ(handDTO.getThumbDistal().getTranslateZ());
//        thumbDistal.jox.setAngle(handDTO.getThumbDistal().getJox());
//        thumbDistal.joy.setAngle(handDTO.getThumbDistal().getJoy());
//        thumbDistal.joz.setAngle(handDTO.getThumbDistal().getJoz());
//        thumbDistal.rx.setAngle(handDTO.getThumbDistal().getRotateX());
//        thumbDistal.ry.setAngle(handDTO.getThumbDistal().getRotateY());
//        thumbDistal.rz.setAngle(handDTO.getThumbDistal().getRotateZ());
//
//        // Setting Index Finger Joint values
//        indexMetacarpal.t.setX(handDTO.getIndexMetacarpal().getTranslateX());
//        indexMetacarpal.t.setY(handDTO.getIndexMetacarpal().getTranslateY());
//        indexMetacarpal.t.setZ(handDTO.getIndexMetacarpal().getTranslateZ());
//        indexMetacarpal.jox.setAngle(handDTO.getIndexMetacarpal().getJox());
//        indexMetacarpal.joy.setAngle(handDTO.getIndexMetacarpal().getJoy());
//        indexMetacarpal.joz.setAngle(handDTO.getIndexMetacarpal().getJoz());
//        indexMetacarpal.rx.setAngle(handDTO.getIndexMetacarpal().getRotateX());
//        indexMetacarpal.ry.setAngle(handDTO.getIndexMetacarpal().getRotateY());
//        indexMetacarpal.rz.setAngle(handDTO.getIndexMetacarpal().getRotateZ());
//
//        indexProximal.t.setX(handDTO.getIndexProximal().getTranslateX());
//        indexProximal.t.setY(handDTO.getIndexProximal().getTranslateY());
//        indexProximal.t.setZ(handDTO.getIndexProximal().getTranslateZ());
//        indexProximal.jox.setAngle(handDTO.getIndexProximal().getJox());
//        indexProximal.joy.setAngle(handDTO.getIndexProximal().getJoy());
//        indexProximal.joz.setAngle(handDTO.getIndexProximal().getJoz());
//        indexProximal.rx.setAngle(handDTO.getIndexProximal().getRotateX());
//        indexProximal.ry.setAngle(handDTO.getIndexProximal().getRotateY());
//        indexProximal.rz.setAngle(handDTO.getIndexProximal().getRotateZ());
//
//        indexIntermediate.t.setX(handDTO.getIndexIntermediate().getTranslateX());
//        indexIntermediate.t.setY(handDTO.getIndexIntermediate().getTranslateY());
//        indexIntermediate.t.setZ(handDTO.getIndexIntermediate().getTranslateZ());
//        indexIntermediate.jox.setAngle(handDTO.getIndexIntermediate().getJox());
//        indexIntermediate.joy.setAngle(handDTO.getIndexIntermediate().getJoy());
//        indexIntermediate.joz.setAngle(handDTO.getIndexIntermediate().getJoz());
//        indexIntermediate.rx.setAngle(handDTO.getIndexIntermediate().getRotateX());
//        indexIntermediate.ry.setAngle(handDTO.getIndexIntermediate().getRotateY());
//        indexIntermediate.rz.setAngle(handDTO.getIndexIntermediate().getRotateZ());
//
//        indexDistal.t.setX(handDTO.getIndexDistal().getTranslateX());
//        indexDistal.t.setY(handDTO.getIndexDistal().getTranslateY());
//        indexDistal.t.setZ(handDTO.getIndexDistal().getTranslateZ());
//        indexDistal.jox.setAngle(handDTO.getIndexDistal().getJox());
//        indexDistal.joy.setAngle(handDTO.getIndexDistal().getJoy());
//        indexDistal.joz.setAngle(handDTO.getIndexDistal().getJoz());
//        indexDistal.rx.setAngle(handDTO.getIndexDistal().getRotateX());
//        indexDistal.ry.setAngle(handDTO.getIndexDistal().getRotateY());
//        indexDistal.rz.setAngle(handDTO.getIndexDistal().getRotateZ());
//
//        // Setting Middle Finger Joint values
//        middleMetacarpal.t.setX(handDTO.getMiddleMetacarpal().getTranslateX());
//        middleMetacarpal.t.setY(handDTO.getMiddleMetacarpal().getTranslateY());
//        middleMetacarpal.t.setZ(handDTO.getMiddleMetacarpal().getTranslateZ());
//        middleMetacarpal.jox.setAngle(handDTO.getMiddleMetacarpal().getJox());
//        middleMetacarpal.joy.setAngle(handDTO.getMiddleMetacarpal().getJoy());
//        middleMetacarpal.joz.setAngle(handDTO.getMiddleMetacarpal().getJoz());
//        middleMetacarpal.rx.setAngle(handDTO.getMiddleMetacarpal().getRotateX());
//        middleMetacarpal.ry.setAngle(handDTO.getMiddleMetacarpal().getRotateY());
//        middleMetacarpal.rz.setAngle(handDTO.getMiddleMetacarpal().getRotateZ());
//
//        middleProximal.t.setX(handDTO.getMiddleProximal().getTranslateX());
//        middleProximal.t.setY(handDTO.getMiddleProximal().getTranslateY());
//        middleProximal.t.setZ(handDTO.getMiddleProximal().getTranslateZ());
//        middleProximal.jox.setAngle(handDTO.getMiddleProximal().getJox());
//        middleProximal.joy.setAngle(handDTO.getMiddleProximal().getJoy());
//        middleProximal.joz.setAngle(handDTO.getMiddleProximal().getJoz());
//        middleProximal.rx.setAngle(handDTO.getMiddleProximal().getRotateX());
//        middleProximal.ry.setAngle(handDTO.getMiddleProximal().getRotateY());
//        middleProximal.rz.setAngle(handDTO.getMiddleProximal().getRotateZ());
//
//        middleIntermediate.t.setX(handDTO.getMiddleIntermediate().getTranslateX());
//        middleIntermediate.t.setY(handDTO.getMiddleIntermediate().getTranslateY());
//        middleIntermediate.t.setZ(handDTO.getMiddleIntermediate().getTranslateZ());
//        middleIntermediate.jox.setAngle(handDTO.getMiddleIntermediate().getJox());
//        middleIntermediate.joy.setAngle(handDTO.getMiddleIntermediate().getJoy());
//        middleIntermediate.joz.setAngle(handDTO.getMiddleIntermediate().getJoz());
//        middleIntermediate.rx.setAngle(handDTO.getMiddleIntermediate().getRotateX());
//        middleIntermediate.ry.setAngle(handDTO.getMiddleIntermediate().getRotateY());
//        middleIntermediate.rz.setAngle(handDTO.getMiddleIntermediate().getRotateZ());
//
//        middleDistal.t.setX(handDTO.getMiddleDistal().getTranslateX());
//        middleDistal.t.setY(handDTO.getMiddleDistal().getTranslateY());
//        middleDistal.t.setZ(handDTO.getMiddleDistal().getTranslateZ());
//        middleDistal.jox.setAngle(handDTO.getMiddleDistal().getJox());
//        middleDistal.joy.setAngle(handDTO.getMiddleDistal().getJoy());
//        middleDistal.joz.setAngle(handDTO.getMiddleDistal().getJoz());
//        middleDistal.rx.setAngle(handDTO.getMiddleDistal().getRotateX());
//        middleDistal.ry.setAngle(handDTO.getMiddleDistal().getRotateY());
//        middleDistal.rz.setAngle(handDTO.getMiddleDistal().getRotateZ());
//
//        // Setting Ring Finger Joint values
//        ringMetacarpal.t.setX(handDTO.getRingMetacarpal().getTranslateX());
//        ringMetacarpal.t.setY(handDTO.getRingMetacarpal().getTranslateY());
//        ringMetacarpal.t.setZ(handDTO.getRingMetacarpal().getTranslateZ());
//        ringMetacarpal.jox.setAngle(handDTO.getRingMetacarpal().getJox());
//        ringMetacarpal.joy.setAngle(handDTO.getRingMetacarpal().getJoy());
//        ringMetacarpal.joz.setAngle(handDTO.getRingMetacarpal().getJoz());
//        ringMetacarpal.rx.setAngle(handDTO.getRingMetacarpal().getRotateX());
//        ringMetacarpal.ry.setAngle(handDTO.getRingMetacarpal().getRotateY());
//        ringMetacarpal.rz.setAngle(handDTO.getRingMetacarpal().getRotateZ());
//
//        ringProximal.t.setX(handDTO.getRingProximal().getTranslateX());
//        ringProximal.t.setY(handDTO.getRingProximal().getTranslateY());
//        ringProximal.t.setZ(handDTO.getRingProximal().getTranslateZ());
//        ringProximal.jox.setAngle(handDTO.getRingProximal().getJox());
//        ringProximal.joy.setAngle(handDTO.getRingProximal().getJoy());
//        ringProximal.joz.setAngle(handDTO.getRingProximal().getJoz());
//        ringProximal.rx.setAngle(handDTO.getRingProximal().getRotateX());
//        ringProximal.ry.setAngle(handDTO.getRingProximal().getRotateY());
//        ringProximal.rz.setAngle(handDTO.getRingProximal().getRotateZ());
//
//        ringIntermediate.t.setX(handDTO.getRingIntermediate().getTranslateX());
//        ringIntermediate.t.setY(handDTO.getRingIntermediate().getTranslateY());
//        ringIntermediate.t.setZ(handDTO.getRingIntermediate().getTranslateZ());
//        ringIntermediate.jox.setAngle(handDTO.getRingIntermediate().getJox());
//        ringIntermediate.joy.setAngle(handDTO.getRingIntermediate().getJoy());
//        ringIntermediate.joz.setAngle(handDTO.getRingIntermediate().getJoz());
//        ringIntermediate.rx.setAngle(handDTO.getRingIntermediate().getRotateX());
//        ringIntermediate.ry.setAngle(handDTO.getRingIntermediate().getRotateY());
//        ringIntermediate.rz.setAngle(handDTO.getRingIntermediate().getRotateZ());
//
//        ringDistal.t.setX(handDTO.getRingDistal().getTranslateX());
//        ringDistal.t.setY(handDTO.getRingDistal().getTranslateY());
//        ringDistal.t.setZ(handDTO.getRingDistal().getTranslateZ());
//        ringDistal.jox.setAngle(handDTO.getRingDistal().getJox());
//        ringDistal.joy.setAngle(handDTO.getRingDistal().getJoy());
//        ringDistal.joz.setAngle(handDTO.getRingDistal().getJoz());
//        ringDistal.rx.setAngle(handDTO.getRingDistal().getRotateX());
//        ringDistal.ry.setAngle(handDTO.getRingDistal().getRotateY());
//        ringDistal.rz.setAngle(handDTO.getRingDistal().getRotateZ());
//
//        // Setting Pinky Finger Joint values
//        pinkyMetacarpal.t.setX(handDTO.getPinkyMetacarpal().getTranslateX());
//        pinkyMetacarpal.t.setY(handDTO.getPinkyMetacarpal().getTranslateY());
//        pinkyMetacarpal.t.setZ(handDTO.getPinkyMetacarpal().getTranslateZ());
//        pinkyMetacarpal.jox.setAngle(handDTO.getPinkyMetacarpal().getJox());
//        pinkyMetacarpal.joy.setAngle(handDTO.getPinkyMetacarpal().getJoy());
//        pinkyMetacarpal.joz.setAngle(handDTO.getPinkyMetacarpal().getJoz());
//        pinkyMetacarpal.rx.setAngle(handDTO.getPinkyMetacarpal().getRotateX());
//        pinkyMetacarpal.ry.setAngle(handDTO.getPinkyMetacarpal().getRotateY());
//        pinkyMetacarpal.rz.setAngle(handDTO.getPinkyMetacarpal().getRotateZ());
//
//        pinkyProximal.t.setX(handDTO.getPinkyProximal().getTranslateX());
//        pinkyProximal.t.setY(handDTO.getPinkyProximal().getTranslateY());
//        pinkyProximal.t.setZ(handDTO.getPinkyProximal().getTranslateZ());
//        pinkyProximal.jox.setAngle(handDTO.getPinkyProximal().getJox());
//        pinkyProximal.joy.setAngle(handDTO.getPinkyProximal().getJoy());
//        pinkyProximal.joz.setAngle(handDTO.getPinkyProximal().getJoz());
//        pinkyProximal.rx.setAngle(handDTO.getPinkyProximal().getRotateX());
//        pinkyProximal.ry.setAngle(handDTO.getPinkyProximal().getRotateY());
//        pinkyProximal.rz.setAngle(handDTO.getPinkyProximal().getRotateZ());
//
//        pinkyIntermediate.t.setX(handDTO.getPinkyIntermediate().getTranslateX());
//        pinkyIntermediate.t.setY(handDTO.getPinkyIntermediate().getTranslateY());
//        pinkyIntermediate.t.setZ(handDTO.getPinkyIntermediate().getTranslateZ());
//        pinkyIntermediate.jox.setAngle(handDTO.getPinkyIntermediate().getJox());
//        pinkyIntermediate.joy.setAngle(handDTO.getPinkyIntermediate().getJoy());
//        pinkyIntermediate.joz.setAngle(handDTO.getPinkyIntermediate().getJoz());
//        pinkyIntermediate.rx.setAngle(handDTO.getPinkyIntermediate().getRotateX());
//        pinkyIntermediate.ry.setAngle(handDTO.getPinkyIntermediate().getRotateY());
//        pinkyIntermediate.rz.setAngle(handDTO.getPinkyIntermediate().getRotateZ());
//
//        pinkyDistal.t.setX(handDTO.getPinkyDistal().getTranslateX());
//        pinkyDistal.t.setY(handDTO.getPinkyDistal().getTranslateY());
//        pinkyDistal.t.setZ(handDTO.getPinkyDistal().getTranslateZ());
//        pinkyDistal.jox.setAngle(handDTO.getPinkyDistal().getJox());
//        pinkyDistal.joy.setAngle(handDTO.getPinkyDistal().getJoy());
//        pinkyDistal.joz.setAngle(handDTO.getPinkyDistal().getJoz());
//        pinkyDistal.rx.setAngle(handDTO.getPinkyDistal().getRotateX());
//        pinkyDistal.ry.setAngle(handDTO.getPinkyDistal().getRotateY());
//        pinkyDistal.rz.setAngle(handDTO.getPinkyDistal().getRotateZ());
    }
}
