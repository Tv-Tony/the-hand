package tv.toner.listener;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import lombok.NoArgsConstructor;
import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.javafx.experiments.importers.maya.Joint;
import com.javafx.experiments.shape3d.PolygonMeshView;
import com.javafx.experiments.shape3d.SkinningMesh;

import javafx.application.Platform;
import javafx.scene.Parent;
import tv.toner.defs.JointDef;
import tv.toner.entity.Mpu6050;
import tv.toner.filter.AngleFilter;
import tv.toner.filter.DigitalSmoothFilter;
import tv.toner.manager.SensorManager;
import tv.toner.utils.TiltCalculator;

@Component
@NoArgsConstructor
public class Updater implements ApplicationListener<ProcessedAngleEvent> {

    private static final Logger log = LoggerFactory.getLogger(Updater.class);

    private List<Parent> forestRight;
    private PolygonMeshView skinningRight;

    public void initialize(List<Parent> forestRight, PolygonMeshView skinningRight) {
        this.forestRight = forestRight;
        this.skinningRight = skinningRight;
    }

    @Override
    public void onApplicationEvent(ProcessedAngleEvent event) {

        double rollOneFiltered   = event.getProcessedAngleWithKey("0");
        double rollTwoFiltered   = event.getProcessedAngleWithKey("1");
        double rollThreeFiltered = event.getProcessedAngleWithKey("2");

        try {
            Joint middleMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.MIDDLE_METACARPAL.getBonePattern());
            Joint indexMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.INDEX_METACARPAL.getBonePattern());
            Joint ringMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.RING_METACARPAL.getBonePattern());
            Joint pinkyMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.PINKY_METACARPAL.getBonePattern());

            Joint middleProximal = (Joint) forestRight.get(0).lookup(JointDef.MIDDLE_PROXIMAL.getBonePattern());
            Joint indexProximal = (Joint) forestRight.get(0).lookup(JointDef.INDEX_PROXIMAL.getBonePattern());
            Joint ringProximal = (Joint) forestRight.get(0).lookup(JointDef.RING_PROXIMAL.getBonePattern());
            Joint pinkyProximal = (Joint) forestRight.get(0).lookup(JointDef.PINKY_PROXIMAL.getBonePattern());

            Platform.runLater(() -> {

                indexMetacarpal.rx.setAngle(rollOneFiltered);
                indexProximal.rx.setAngle(rollOneFiltered);

                middleMetacarpal.rx.setAngle(rollTwoFiltered);
                middleProximal.rx.setAngle(rollTwoFiltered);

                ringMetacarpal.rx.setAngle(rollThreeFiltered);
                ringProximal.rx.setAngle(rollThreeFiltered);

                pinkyMetacarpal.rx.setAngle(rollThreeFiltered);
                pinkyProximal.rx.setAngle(rollThreeFiltered);

                ((SkinningMesh) skinningRight.getMesh()).update();
            });
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }
}
