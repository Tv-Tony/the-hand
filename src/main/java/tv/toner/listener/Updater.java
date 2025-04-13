package tv.toner.listener;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
public class Updater implements ApplicationListener<ProcessedAngleEvent> {

    private static final Logger log = LoggerFactory.getLogger(Updater.class);

    private final DigitalSmoothFilter fingerOneFilter;
    private final DigitalSmoothFilter fingerTwoFilter;
    private final DigitalSmoothFilter fingerThreeFilter;

    private final AngleFilter fingerOneAngleFilter;
    private final AngleFilter fingerTwoAngleFilter;
    private final AngleFilter fingerThreeAngleFilter;

    private List<Parent> forestRight;
    private PolygonMeshView skinningRight;

    private final SensorManager sensorManager;

    @Autowired
    public Updater(SensorManager sensorManager) {
        this.sensorManager = sensorManager;

        // Sensor filters (1 per sensor)
        this.fingerOneFilter = new DigitalSmoothFilter(20, 10);
        this.fingerTwoFilter = new DigitalSmoothFilter(20, 10);
        this.fingerThreeFilter = new DigitalSmoothFilter(20, 10);

        this.fingerOneAngleFilter = new AngleFilter(20);
        this.fingerTwoAngleFilter = new AngleFilter(20);
        this.fingerThreeAngleFilter = new AngleFilter(20);
    }

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

                double pinkyAngle = 45.0;
                pinkyMetacarpal.rx.setAngle(pinkyAngle);
                pinkyProximal.rx.setAngle(pinkyAngle);

                ((SkinningMesh) skinningRight.getMesh()).update();
            });
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }
}
