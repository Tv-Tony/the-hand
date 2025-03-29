package tv.toner.socket;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.core.config.plugins.util.ResolverUtil;
import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.javafx.experiments.importers.maya.Joint;
import com.javafx.experiments.shape3d.PolygonMeshView;
import com.javafx.experiments.shape3d.SkinningMesh;

import javafx.application.Platform;
import javafx.scene.Parent;
import tv.toner.dto.JointStruct;
import tv.toner.dto.Mpu6050;
import tv.toner.dto.TestStruct;
import tv.toner.dummy.JointDef;
import tv.toner.event.GloveEvent;
import tv.toner.utils.EMAFilter;
import tv.toner.utils.MpuUtils;

@Component
public class Updater implements ApplicationListener<GloveEvent> {
    private static final Logger log = LoggerFactory.getLogger(Updater.class);
    private static final AtomicReference<Mpu6050> lastHandPosition = new AtomicReference<>(null);

    Triplet<Double, Double, Double> firstAngle = null;

    // Custom ThreadFactory to name the thread
    private static final ThreadFactory threadFactory = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("Hand-Updater-Thread");
            return thread;
        }
    };
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, threadFactory);

    private List<Parent> forestRight;
    private PolygonMeshView skinningRight;

    public Updater() {
    }

//    public Updater(List<Parent> forestRight/*, PolygonMeshView skinningRight*/) {
//        Updater.forestRight = forestRight;
////        Updater.skinningRight = skinningRight;
//    }

//        @EventListener
//    public void handleGloveDataEvent(GloveEvent event) {
//            log.info("Data Received{}", event.toString());
//        lastHandPosition.set(event.getData());
//    }

    public void updateHand() {
        Mpu6050 latestValue = lastHandPosition.get();

        Triplet<Double, Double, Double> latestAngle = MpuUtils.angles(latestValue);

        Joint middleMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.MIDDLE_METACARPAL.getBonePattern());
        Joint indexMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.INDEX_METACARPAL.getBonePattern());
        Joint ringMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.RING_METACARPAL.getBonePattern());
        Joint pinkyMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.PINKY_METACARPAL.getBonePattern());

        Joint middleProximal = (Joint) forestRight.get(0).lookup(JointDef.MIDDLE_PROXIMAL.getBonePattern());
        Joint indexProximal = (Joint) forestRight.get(0).lookup(JointDef.INDEX_PROXIMAL.getBonePattern());
        Joint ringProximal = (Joint) forestRight.get(0).lookup(JointDef.RING_PROXIMAL.getBonePattern());
        Joint pinkyProximal = (Joint) forestRight.get(0).lookup(JointDef.PINKY_PROXIMAL.getBonePattern());

        // Check if there's new data and handle null cases
//        if (latestValue != null && (lastHandPosition == null || !lastHandPosition.equals(latestValue))) {
            Platform.runLater(() -> {

                middleMetacarpal.rx.setAngle((Double) latestAngle.getValue(0));
//                middleMetacarpal.ry.setAngle(latestValue.getAngleY());

                indexMetacarpal.rx.setAngle((Double) latestAngle.getValue(0));
//                indexMetacarpal.ry.setAngle(latestValue.getAngleY());

                ringMetacarpal.rx.setAngle((Double) latestAngle.getValue(0));
//                ringMetacarpal.ry.setAngle(latestValue.getAngleY());

                pinkyMetacarpal.rx.setAngle((Double) latestAngle.getValue(0));
//                pinkyMetacarpal.ry.setAngle(latestValue.getAngleY());

                middleProximal.rx.setAngle((Double) latestAngle.getValue(0));
//                middleProximal.ry.setAngle(latestValue.getAngleY());

                indexProximal.rx.setAngle((Double) latestAngle.getValue(0));
//                indexProximal.ry.setAngle(latestValue.getAngleY());

                ringProximal.rx.setAngle((Double) latestAngle.getValue(0));
//                ringProximal.ry.setAngle(latestValue.getAngleY());

                pinkyProximal.rx.setAngle((Double) latestAngle.getValue(0));
//                pinkyProximal.ry.setAngle(latestValue.getAngleY());
                log.warn("WE ARE GOOD BABY");
//                lastHandPosition = latestValue; // Update lastHandPosition with the new value
                ((SkinningMesh) skinningRight.getMesh()).update();
            });
//        }
    }

    public void startUpdating() {
        scheduler.scheduleAtFixedRate(this::updateHand, 0, 50, TimeUnit.MILLISECONDS);
    }

    // Call this method in your main application to start periodic updates
    public void initialize(List<Parent> forestRight, PolygonMeshView skinningRight) {
        this.forestRight = forestRight;
        this.skinningRight = skinningRight;
        startUpdating();
    }

    @Override
    public void onApplicationEvent(GloveEvent event) {
//        log.info("Data Received{}", event.toString());
//        lastHandPosition.set(event.getData());

        Mpu6050 latestValue = EMAFilter.applyFilter(event.getData(), 0.2, lastHandPosition.get());

        if (firstAngle == null)
            this.firstAngle = MpuUtils.angles(latestValue);

        Triplet<Double, Double, Double> latestAngle = MpuUtils.angles(latestValue);

        log.info("Angles before filter: {}", latestValue);
        log.info("Filtered angles: {}", event.getData());

        Joint middleMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.MIDDLE_METACARPAL.getBonePattern());
        Joint indexMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.INDEX_METACARPAL.getBonePattern());
        Joint ringMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.RING_METACARPAL.getBonePattern());
        Joint pinkyMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.PINKY_METACARPAL.getBonePattern());

        Joint middleProximal = (Joint) forestRight.get(0).lookup(JointDef.MIDDLE_PROXIMAL.getBonePattern());
        Joint indexProximal = (Joint) forestRight.get(0).lookup(JointDef.INDEX_PROXIMAL.getBonePattern());
        Joint ringProximal = (Joint) forestRight.get(0).lookup(JointDef.RING_PROXIMAL.getBonePattern());
        Joint pinkyProximal = (Joint) forestRight.get(0).lookup(JointDef.PINKY_PROXIMAL.getBonePattern());

        // Check if there's new data and handle null cases
//        if (latestValue != null && (lastHandPosition == null || !lastHandPosition.equals(latestValue))) {
        Platform.runLater(() -> {

            middleMetacarpal.rx.setAngle((Double) latestAngle.getValue(0) - firstAngle.getValue0());
//                middleMetacarpal.ry.setAngle(latestValue.getAngleY());

            indexMetacarpal.rx.setAngle((Double) latestAngle.getValue(0) - firstAngle.getValue0());
//                indexMetacarpal.ry.setAngle(latestValue.getAngleY());

            ringMetacarpal.rx.setAngle((Double) latestAngle.getValue(0) - firstAngle.getValue0());
//                ringMetacarpal.ry.setAngle(latestValue.getAngleY());

            pinkyMetacarpal.rx.setAngle((Double) latestAngle.getValue(0) - firstAngle.getValue0());
//                pinkyMetacarpal.ry.setAngle(latestValue.getAngleY());

            middleProximal.rx.setAngle((Double) latestAngle.getValue(0) - firstAngle.getValue0());
//                middleProximal.ry.setAngle(latestValue.getAngleY());

            indexProximal.rx.setAngle((Double) latestAngle.getValue(0) - firstAngle.getValue0());
//                indexProximal.ry.setAngle(latestValue.getAngleY());

            ringProximal.rx.setAngle((Double) latestAngle.getValue(0) - firstAngle.getValue0());
//                ringProximal.ry.setAngle(latestValue.getAngleY());

            pinkyProximal.rx.setAngle((Double) latestAngle.getValue(0) - firstAngle.getValue0());
//                pinkyProximal.ry.setAngle(latestValue.getAngleY());
            log.warn("WE ARE GOOD BABY");
            lastHandPosition.set(latestValue); // Update lastHandPosition with the new value
            ((SkinningMesh) skinningRight.getMesh()).update();
        });
    }
}
