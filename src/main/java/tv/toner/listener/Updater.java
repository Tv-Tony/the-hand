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
import tv.toner.manager.SensorManager;
import tv.toner.utils.EMAFilter;
import tv.toner.utils.MPU6050Utility;
import tv.toner.utils.MpuUtils;

@Component
public class Updater implements ApplicationListener<GloveEvent> {

    private static final Logger log = LoggerFactory.getLogger(Updater.class);
    private static final AtomicReference<Mpu6050> lastHandPosition = new AtomicReference<>(null);

    private List<Parent> forestRight;
    private PolygonMeshView skinningRight;

    Triplet<Double, Double, Double> firstAngle = null;

    private MPU6050Utility utility;

    private final SensorManager sensorManager;

    @Autowired
    public Updater(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
    }

    public void initialize(List<Parent> forestRight, PolygonMeshView skinningRight) {
        this.forestRight = forestRight;
        this.skinningRight = skinningRight;
        this.utility = new MPU6050Utility();
        utility.setCalibrationPoints(33000, 22000, 6000);
    }

    @Override
    public void onApplicationEvent(GloveEvent event) {

        Mpu6050 sensorZero = sensorManager.getLatestData("0");
        Mpu6050 sensorOne = sensorManager.getLatestData("1");

        Mpu6050 latestValue = EMAFilter.applyFilter(sensorZero, 0.2, lastHandPosition.get());

        if (firstAngle == null)
            this.firstAngle = MpuUtils.angles(latestValue);

        Triplet<Double, Double, Double> latestAngle = MpuUtils.angles(latestValue);

        log.debug("Angles before filter: {}", latestValue);
        log.debug("Filtered angles: {}", sensorZero);

        Joint middleMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.MIDDLE_METACARPAL.getBonePattern());
        Joint indexMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.INDEX_METACARPAL.getBonePattern());
        Joint ringMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.RING_METACARPAL.getBonePattern());
        Joint pinkyMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.PINKY_METACARPAL.getBonePattern());

        Joint middleProximal = (Joint) forestRight.get(0).lookup(JointDef.MIDDLE_PROXIMAL.getBonePattern());
        Joint indexProximal = (Joint) forestRight.get(0).lookup(JointDef.INDEX_PROXIMAL.getBonePattern());
        Joint ringProximal = (Joint) forestRight.get(0).lookup(JointDef.RING_PROXIMAL.getBonePattern());
        Joint pinkyProximal = (Joint) forestRight.get(0).lookup(JointDef.PINKY_PROXIMAL.getBonePattern());

        Platform.runLater(() -> {

            middleMetacarpal.rx.setAngle(utility.getXAngleFromRaw(latestValue.getAx()));
//                middleMetacarpal.ry.setAngle(latestValue.getAngleY());

            indexMetacarpal.rx.setAngle(utility.getXAngleFromRaw(latestValue.getAx()));
//                indexMetacarpal.ry.setAngle(latestValue.getAngleY());

            ringMetacarpal.rx.setAngle(utility.getXAngleFromRaw(latestValue.getAx()));
//                ringMetacarpal.ry.setAngle(latestValue.getAngleY());

            pinkyMetacarpal.rx.setAngle(utility.getXAngleFromRaw(latestValue.getAx()));
//                pinkyMetacarpal.ry.setAngle(latestValue.getAngleY());

            middleProximal.rx.setAngle(utility.getXAngleFromRaw(latestValue.getAx()));
//                middleProximal.ry.setAngle(latestValue.getAngleY());

            indexProximal.rx.setAngle(utility.getXAngleFromRaw(latestValue.getAx()));
//                indexProximal.ry.setAngle(latestValue.getAngleY());

            ringProximal.rx.setAngle(utility.getXAngleFromRaw(latestValue.getAx()));
//                ringProximal.ry.setAngle(latestValue.getAngleY());

            pinkyProximal.rx.setAngle(utility.getXAngleFromRaw(latestValue.getAx()));
//                pinkyProximal.ry.setAngle(latestValue.getAngleY());

            lastHandPosition.set(latestValue); // Update lastHandPosition with the new value
            ((SkinningMesh) skinningRight.getMesh()).update();
        });
    }
}
