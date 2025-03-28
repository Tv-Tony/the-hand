package tv.toner.socket;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.core.config.plugins.util.ResolverUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.javafx.experiments.importers.maya.Joint;
import com.javafx.experiments.shape3d.PolygonMeshView;
import com.javafx.experiments.shape3d.SkinningMesh;

import javafx.application.Platform;
import javafx.scene.Parent;
import tv.toner.dto.JointStruct;
import tv.toner.dto.TestStruct;
import tv.toner.dummy.JointDef;

public class Updater {
    private static final Logger log = LoggerFactory.getLogger(Updater.class);
    private static volatile TestStruct lastHandPosition = null;

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

    private static List<Parent> forestRight;
    private static PolygonMeshView skinningRight;

    public Updater(List<Parent> forestRight, PolygonMeshView skinningRight) {
        Updater.forestRight = forestRight;
        Updater.skinningRight = skinningRight;
    }

    public static void updateHand() {
        TestStruct latestValue = ClientHandler.getLatestValue();
        Joint middleMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.MIDDLE_METACARPAL.getBonePattern());
        Joint indexMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.INDEX_METACARPAL.getBonePattern());
        Joint ringMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.RING_METACARPAL.getBonePattern());
        Joint pinkyMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.PINKY_METACARPAL.getBonePattern());

        Joint middleProximal = (Joint) forestRight.get(0).lookup(JointDef.MIDDLE_PROXIMAL.getBonePattern());
        Joint indexProximal = (Joint) forestRight.get(0).lookup(JointDef.INDEX_PROXIMAL.getBonePattern());
        Joint ringProximal = (Joint) forestRight.get(0).lookup(JointDef.RING_PROXIMAL.getBonePattern());
        Joint pinkyProximal = (Joint) forestRight.get(0).lookup(JointDef.PINKY_PROXIMAL.getBonePattern());

        // Check if there's new data and handle null cases
        if (latestValue != null && (lastHandPosition == null || !lastHandPosition.equals(latestValue))) {
            Platform.runLater(() -> {

                middleMetacarpal.rx.setAngle(latestValue.getAngleX());
//                middleMetacarpal.ry.setAngle(latestValue.getAngleY());

                indexMetacarpal.rx.setAngle(latestValue.getAngleX());
//                indexMetacarpal.ry.setAngle(latestValue.getAngleY());

                ringMetacarpal.rx.setAngle(latestValue.getAngleX());
//                ringMetacarpal.ry.setAngle(latestValue.getAngleY());

                pinkyMetacarpal.rx.setAngle(latestValue.getAngleX());
//                pinkyMetacarpal.ry.setAngle(latestValue.getAngleY());

                middleProximal.rx.setAngle(latestValue.getAngleX());
//                middleProximal.ry.setAngle(latestValue.getAngleY());

                indexProximal.rx.setAngle(latestValue.getAngleX());
//                indexProximal.ry.setAngle(latestValue.getAngleY());

                ringProximal.rx.setAngle(latestValue.getAngleX());
//                ringProximal.ry.setAngle(latestValue.getAngleY());

                pinkyProximal.rx.setAngle(latestValue.getAngleX());
//                pinkyProximal.ry.setAngle(latestValue.getAngleY());

                lastHandPosition = latestValue; // Update lastHandPosition with the new value
                ((SkinningMesh) skinningRight.getMesh()).update();
            });
        }
    }

    public static void startUpdating() {
        scheduler.scheduleAtFixedRate(Updater::updateHand, 0, 1, TimeUnit.MILLISECONDS);
    }

    // Call this method in your main application to start periodic updates
    public static void initialize(List<Parent> forestRight, PolygonMeshView skinningRight) {
        new Updater(forestRight, skinningRight);
        startUpdating();
    }
}
