package tv.toner.socket;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.javafx.experiments.shape3d.PolygonMeshView;
import com.javafx.experiments.shape3d.SkinningMesh;

import javafx.application.Platform;
import javafx.scene.Parent;
import tv.toner.dto.JointStruct;

public class Updater {
    private static final Logger log = LoggerFactory.getLogger(Updater.class);
    private static volatile List<JointStruct> lastHandPosition = null;

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
        List<JointStruct> latestValue = ClientHandler.getLatestValue();

        // Check if there's new data and handle null cases
        if (latestValue != null && (lastHandPosition == null || !lastHandPosition.equals(latestValue))) {
            Platform.runLater(() -> {

                HandRefresher.refresh(forestRight, latestValue, skinningRight); // Update joints and mesh

                lastHandPosition = latestValue; // Update lastHandPosition with the new value
                ((SkinningMesh) skinningRight.getMesh()).update();
            });
        }
    }

    public static void startUpdating() {
        scheduler.scheduleAtFixedRate(Updater::updateHand, 0, 16, TimeUnit.MILLISECONDS);
    }

    // Call this method in your main application to start periodic updates
    public static void initialize(List<Parent> forestRight, PolygonMeshView skinningRight) {
        new Updater(forestRight, skinningRight);
        startUpdating();
    }
}
