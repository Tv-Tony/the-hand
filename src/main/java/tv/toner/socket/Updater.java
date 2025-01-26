package tv.toner.socket;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.javafx.experiments.importers.maya.Joint;
import com.javafx.experiments.shape3d.PolygonMeshView;
import com.javafx.experiments.shape3d.SkinningMesh;

import javafx.application.Platform;
import javafx.scene.Parent;
import tv.toner.dummy.JointDef;

public class Updater {
    private static AtomicInteger lastAngle = new AtomicInteger(-1);

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static List<Parent> forestRight;
    private static PolygonMeshView skinningRight;

    public Updater(List<Parent> forestRight, PolygonMeshView skinningRight) {
        Updater.forestRight = forestRight;
        Updater.skinningRight = skinningRight;
    }

    public static void updateHand() {
        Joint indexMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.INDEX_METACARPAL.getBonePattern());
        Joint indexProximal = (Joint) forestRight.get(0).lookup(JointDef.INDEX_PROXIMAL.getBonePattern());

        Joint middleMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.MIDDLE_METACARPAL.getBonePattern());
        Joint middleProximal = (Joint) forestRight.get(0).lookup(JointDef.MIDDLE_PROXIMAL.getBonePattern());

        // Get the latest value from ClientHandler
        double latestValue = ClientHandler.getLatestValue();

        // If the latest value is different from the stored value, update the joint and mesh
        if (lastAngle.get() != latestValue) {
            // Update the joint's angle
            Platform.runLater(() -> {

                indexMetacarpal.rx.setAngle(latestValue);
                indexProximal.rx.setAngle(latestValue);
                middleMetacarpal.rx.setAngle(latestValue);
                middleMetacarpal.ry.setAngle(latestValue);
                middleProximal.rx.setAngle(latestValue);


//                System.out.println("Updated indexMetacarpal angle to: " + latestValue);


                lastAngle.set((int) latestValue);

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
        startUpdating();
    }
}
