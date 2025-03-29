package tv.toner.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.javafx.experiments.importers.maya.Joint;
import com.javafx.experiments.shape3d.PolygonMeshView;
import com.javafx.experiments.shape3d.SkinningMesh;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import tv.toner.riggedHand.HandImporter;
import tv.toner.listener.Updater;
import tv.toner.utils.DragSupport;

@Controller
public class TheHandController implements Initializable {

    @FXML
    private SubScene mySubScene;

    @FXML
    private VBox mainVBox;

    @FXML
    private Text coordinateDisplay;

    @FXML
    private TitledPane loggingTab;

    private final Translate translate = new Translate(0, 0, 0);
    private final Translate translateZ = new Translate(0, 0, -1690 /*-1070*/);
    private final Rotate rotateX = new Rotate(-120, 0, 0, 0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(180, 0, 0, 0, Rotate.Y_AXIS);
    private final Translate translateY = new Translate(0, 0, 0);

    private PolygonMeshView skinningRight;
    private List<Parent> forestRight = new ArrayList<>();
    private PolygonMeshView skinningLeft;
    private List<Parent> forestLeft = new ArrayList<>();

    private final Updater updater;

    @Autowired
    public TheHandController(Updater updater) {
        this.updater = updater;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setupRiggedHands();
        setCoordinateDisplay();
        SubScene theSubScene = setupSubScene();

        // Add drag support and other initializations
        dragSupport(theSubScene);
        addResizeListener(loggingTab);
        setPositionOfHands();
        skinRefresh();

        StackPane stackPane = new StackPane(theSubScene, coordinateDisplay, loggingTab);

        // Add the new SubScene to the VBox
        mainVBox.getChildren().set(1, stackPane); // Replace the SubScene directly in the VBox
        System.out.println(mainVBox.getChildren());
    }

    private void addResizeListener(TitledPane titledPane) {
        // Mouse drag variables
        AtomicReference<Double> initialHeight = new AtomicReference<>((double) 0);
        AtomicReference<Double> initialMouseY = new AtomicReference<>((double) 0);

        // Mouse pressed event to capture the initial height and mouse position
        titledPane.setOnMousePressed(event -> {
            initialHeight.set(titledPane.getHeight());
            initialMouseY.set(event.getScreenY());
        });

        // Mouse dragged event to resize the TitledPane
        titledPane.setOnMouseDragged(event -> {
            double deltaY = event.getScreenY() - initialMouseY.get();
            double newHeight = initialHeight.get() + deltaY;
            // Set the new height (ensure it doesn't go below the minimum height)
            titledPane.setPrefHeight(Math.max(newHeight, 100)); // 100 is the minimum height
        });
    }

    private SubScene setupSubScene() {
        // Create a new root and SubScene directly without reassigning mySubScene
        Group root = new Group(
                new Group(skinningLeft, forestLeft.get(0)),
                new Group(skinningRight, forestRight.get(0)),
                new Group(createBackground())
        );

        SubScene antiAliasedSubScene = new SubScene(
                root,
                mySubScene.getWidth(),
                mySubScene.getHeight(),
                true, // Enable depth buffering
                SceneAntialiasing.BALANCED // Enable anti-aliasing
        );

        antiAliasedSubScene.setFill(Color.LIGHTGRAY);
        antiAliasedSubScene.setCamera(cameraSetup());

        // Bind the SubScene's size to the VBox size
        antiAliasedSubScene.widthProperty().bind(mainVBox.widthProperty());
        antiAliasedSubScene.heightProperty().bind(mainVBox.heightProperty().subtract(30));

        Translate centerTranslate = new Translate();
        centerTranslate.xProperty().bind(antiAliasedSubScene.widthProperty().divide(2));
        centerTranslate.yProperty().bind(antiAliasedSubScene.heightProperty().divide(2));

        // Set up transforms and other properties
        antiAliasedSubScene.getRoot().getTransforms().addAll(centerTranslate, translate, translateZ, rotateX, rotateY, translateY);
        antiAliasedSubScene.setPickOnBounds(true); // Ensure the SubScene captures mouse events.

        return antiAliasedSubScene;
    }

    private void setupRiggedHands() {
        /*
        Model downloaded from
        https://github.com/leapmotion/leapjs-rigged-hand/tree/master/src/models
        */
        HandImporter handLeft = new HandImporter("modelLeft.json", true, false);
        handLeft.readModel(50);

        forestLeft = handLeft.getJointForest();
        skinningLeft = handLeft.getSkinningMeshView();

        /*
        Model downloaded from
        https://github.com/leapmotion/leapjs-rigged-hand/blob/master/src/models/hand_models_v1.js
        */
        HandImporter handRight = new HandImporter("modelRight.json", false, false);
        handRight.readModel(50f);

        forestRight = handRight.getJointForest();
        skinningRight = handRight.getSkinningMeshView();

        updater.initialize(forestRight, skinningRight);

        setPositionOfHands();
        skinRefresh();
    }

    public Sphere createBackground() {
        Sphere skySphere = new Sphere(10000);

        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.AQUAMARINE);
        skySphere.setScaleX(-1);
        skySphere.setScaleY(-1);
        skySphere.setScaleZ(-1);
        skySphere.setMaterial(material);

        return skySphere;
    }

    public void dragSupport(SubScene subScene) {
        new DragSupport(subScene, null, MouseButton.SECONDARY, Orientation.VERTICAL, translateZ.zProperty(), -3);
        new DragSupport(subScene, null, Orientation.HORIZONTAL, rotateY.angleProperty());
        new DragSupport(subScene, null, Orientation.VERTICAL, rotateX.angleProperty());
        new DragSupport(subScene, null, MouseButton.MIDDLE, Orientation.HORIZONTAL, translate.xProperty());
        new DragSupport(subScene, null, MouseButton.MIDDLE, Orientation.VERTICAL, translate.yProperty());
    }

    public PerspectiveCamera cameraSetup() {
        PerspectiveCamera perspectiveCamera = new PerspectiveCamera();
        perspectiveCamera.setTranslateZ(-2000);
        perspectiveCamera.setNearClip(0.001);
        perspectiveCamera.setFarClip(10000);

        return perspectiveCamera;
    }

    public void setPositionOfHands() {
        ((Joint)this.forestLeft.get(0)).t.setX(200);
        ((Joint)this.forestRight.get(0)).t.setX(-200);
    }

    public void skinRefresh() {
        ((SkinningMesh)skinningLeft.getMesh()).update();
        ((SkinningMesh)skinningRight.getMesh()).update();
    }

    public void setCoordinateDisplay() {
        final double initialX = rotateX.getAngle();
        final double initialY = rotateY.getAngle();
        final double initialZ = translateZ.getZ();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double deltaX = rotateX.getAngle() - initialX;
                double deltaY = rotateY.getAngle() - initialY;
                double deltaZ = translateZ.getZ() - initialZ;

                deltaX = (deltaX % 360 + 360) % 360;
                deltaY = (deltaY % 360 + 360) % 360;

                coordinateDisplay.setText(String.format("X: %.2f, Y: %.2f, Z: %.2f",
                        deltaX, deltaY, deltaZ));
            }
        };
        timer.start();
    }

    /*
    http://jperedadnr.blogspot.com/2013/06/leap-motion-controller-and-javafx-new.html
    */
    private void matrixRotateNode(boolean right, double alf, double bet, double gam){
        double A11=Math.cos(alf)*Math.cos(gam);
        double A12=Math.cos(bet)*Math.sin(alf)+Math.cos(alf)*Math.sin(bet)*Math.sin(gam);
        double A13=Math.sin(alf)*Math.sin(bet)-Math.cos(alf)*Math.cos(bet)*Math.sin(gam);
        double A21=-Math.cos(gam)*Math.sin(alf);
        double A22=Math.cos(alf)*Math.cos(bet)-Math.sin(alf)*Math.sin(bet)*Math.sin(gam);
        double A23=Math.cos(alf)*Math.sin(bet)+Math.cos(bet)*Math.sin(alf)*Math.sin(gam);
        double A31=Math.sin(gam);
        double A32=-Math.cos(gam)*Math.sin(bet);
        double A33=Math.cos(bet)*Math.cos(gam);

        double d = Math.acos((A11+A22+A33-1d)/2d);
        if(d!=0d){
            double den=2d*Math.sin(d);
            Point3D p= new Point3D((A32-A23)/den,(A13-A31)/den,(A21-A12)/den);
            if(right){
                ((Joint)forestRight.get(0)).rx.setAxis(p);
                ((Joint)forestRight.get(0)).rx.setAngle(Math.toDegrees(d));
            } else {
                ((Joint)forestLeft.get(0)).rx.setAxis(p);
                ((Joint)forestLeft.get(0)).rx.setAngle(Math.toDegrees(d));
            }
        }
    }

//    private Joint getJoint(boolean right, Finger finger, Bone bone){
//        int f = 0,b = 0;
//        String name="";
//        switch(finger.type()){
//            case TYPE_THUMB: name="thumb"; f=0; break;
//            case TYPE_INDEX: name="index"; f=1; break;
//            case TYPE_MIDDLE: name="middle"; f=2; break;
//            case TYPE_RING: name="ring"; f=3; break;
//            case TYPE_PINKY: name="pinky"; f=4; break;
//        }
//        switch(bone.type()){
//            case TYPE_METACARPAL: b=0; break;
//            case TYPE_PROXIMAL: b=1; break;
//            case TYPE_INTERMEDIATE: b=2; break;
//            case TYPE_DISTAL: b=3; break;
//        }
//        String bonePattern1="#Finger_"+Integer.toString(f)+Integer.toString(b-1);
//        String bonePattern2="#"+name+"-"+Integer.toString(b-1);
//        if(right){
//            Joint joint = (Joint)forestRight.get(0).lookup(bonePattern1);
//            if(joint==null){
//                joint = (Joint)forestRight.get(0).lookup(bonePattern2);
//            }
//            return joint;
//        }
//        Joint joint = (Joint)forestLeft.get(0).lookup(bonePattern1);
//        if(joint==null){
//            joint = (Joint)forestLeft.get(0).lookup(bonePattern2);
//        }
//        return joint;
//    }
}
