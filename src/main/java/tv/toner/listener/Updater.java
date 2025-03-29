//package tv.toner.listener;
//
//
//import java.util.List;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.ThreadFactory;
//import java.util.concurrent.TimeUnit;
//
//import org.apache.logging.log4j.core.config.plugins.util.ResolverUtil;
//import org.javatuples.Triplet;
//import org.javatuples.Tuple;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Component;
//
//import com.javafx.experiments.importers.maya.Joint;
//import com.javafx.experiments.shape3d.PolygonMeshView;
//import com.javafx.experiments.shape3d.SkinningMesh;
//
//import javafx.application.Platform;
//import javafx.scene.Parent;
//import tv.toner.dto.JointStruct;
//import tv.toner.dto.Mpu6050;
//import tv.toner.dto.TestStruct;
//import tv.toner.dummy.JointDef;
//import tv.toner.event.GloveEvent;
//import tv.toner.riggedHand.HandConfig;
//import tv.toner.socket.ClientHandler;
//import tv.toner.utils.MpuUtils;
//
//public class Updater {
//    private static final Logger log = LoggerFactory.getLogger(Updater.class);
//    private static volatile Mpu6050 lastHandPosition = null;
//
//    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//    private static List<Parent> forestRight;
//
//    //    private List<Parent> forestRight = handConfig.getForestRight();
//    private static PolygonMeshView skinningRight ;
//
//    public Updater(List<Parent> forestRight, PolygonMeshView skinningRight /*HandConfig*/ /*handConfig*//*, @Lazy GloveListener gloveListener*/) {
////        this.handConfig = handConfig;
//        this.forestRight = forestRight; /*handConfig.getForestRight();*/
//        this.skinningRight = skinningRight; /*handConfig.getSkinningRight();*/
//
//        // Register the Updater to receive data from GloveListener
////        gloveListener.setDataListener(this::processMpuData);/
//    }
//
//    // Convert MPU6050 data to joint movement
//    public void processMpuData(Mpu6050 data) {
//        if (!data.equals(lastHandPosition))
//            this.lastHandPosition = data;
//    }
//
//    @EventListener
//    public void handleGloveDataEvent(GloveEvent event) {
//        Mpu6050 data = event.getData();
//        processMpuData(data);
//    }
//
////    private void updateHand(Mpu6050 latestValue) {
////        if (latestValue != null && (lastHandPosition == null || !lastHandPosition.equals(latestValue))) {
////            Joint middleMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.MIDDLE_METACARPAL.getBonePattern());
////            Joint indexMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.INDEX_METACARPAL.getBonePattern());
////
////            // Apply angles to joints
////            middleMetacarpal.rx.setAngle(latestValue.getAx());
////            indexMetacarpal.rx.setAngle(latestValue.getAy());
////
////            lastHandPosition = latestValue; // Save the last position
////            ((SkinningMesh) skinningRight.getMesh()).update();
////        }
////    }
//
//    public static void updateHand() {
//        Mpu6050 latestValue = lastHandPosition;
//        Triplet<Double, Double, Double> latestAngle = MpuUtils.angles(lastHandPosition);
//        log.info("Updating Hand");
//        Joint middleMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.MIDDLE_METACARPAL.getBonePattern());
//        Joint indexMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.INDEX_METACARPAL.getBonePattern());
//        Joint ringMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.RING_METACARPAL.getBonePattern());
//        Joint pinkyMetacarpal = (Joint) forestRight.get(0).lookup(JointDef.PINKY_METACARPAL.getBonePattern());
//
//        Joint middleProximal = (Joint) forestRight.get(0).lookup(JointDef.MIDDLE_PROXIMAL.getBonePattern());
//        Joint indexProximal = (Joint) forestRight.get(0).lookup(JointDef.INDEX_PROXIMAL.getBonePattern());
//        Joint ringProximal = (Joint) forestRight.get(0).lookup(JointDef.RING_PROXIMAL.getBonePattern());
//        Joint pinkyProximal = (Joint) forestRight.get(0).lookup(JointDef.PINKY_PROXIMAL.getBonePattern());
//
//        // Check if there's new data and handle null cases
//        if (latestValue != null && (lastHandPosition == null || !lastHandPosition.equals(latestValue))) {
//            Platform.runLater(() -> {
//
//                middleMetacarpal.rx.setAngle((Double) latestAngle.getValue(0));
////                middleMetacarpal.ry.setAngle(latestValue.getAngleY());
//
//                indexMetacarpal.rx.setAngle((Double) latestAngle.getValue(0));
////                indexMetacarpal.ry.setAngle(latestValue.getAngleY());
//
//                ringMetacarpal.rx.setAngle((Double) latestAngle.getValue(0));
////                ringMetacarpal.ry.setAngle(latestValue.getAngleY());
//
//                pinkyMetacarpal.rx.setAngle((Double) latestAngle.getValue(0));
////                pinkyMetacarpal.ry.setAngle(latestValue.getAngleY());
//
//                middleProximal.rx.setAngle((Double) latestAngle.getValue(0));
////                middleProximal.ry.setAngle(latestValue.getAngleY());
//
//                indexProximal.rx.setAngle((Double) latestAngle.getValue(0));
////                indexProximal.ry.setAngle(latestValue.getAngleY());
//
//                ringProximal.rx.setAngle((Double) latestAngle.getValue(0));
////                ringProximal.ry.setAngle(latestValue.getAngleY());
//
//                pinkyProximal.rx.setAngle((Double) latestAngle.getValue(0));
////                pinkyProximal.ry.setAngle(latestValue.getAngleY());
//
//                lastHandPosition = latestValue; // Update lastHandPosition with the new value
//                ((SkinningMesh) skinningRight.getMesh()).update();
//            });
//        }
//    }
//
//    public static void startUpdating() {
//        scheduler.scheduleAtFixedRate(Updater::updateHand, 0, 1, TimeUnit.MILLISECONDS);
//    }
//
//    // Call this method in your main application to start periodic updates
//    public static void initialize(List<Parent> forestRight, PolygonMeshView skinningRight) {
//        new Updater(forestRight, skinningRight);
//        startUpdating();
//    }
//}
