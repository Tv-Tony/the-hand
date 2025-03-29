package tv.toner.riggedHand;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import com.javafx.experiments.shape3d.PolygonMeshView;
import java.util.List;
import javafx.scene.Parent;

import com.javafx.experiments.shape3d.PolygonMeshView;
import com.javafx.experiments.shape3d.SkinningMesh;

import java.util.ArrayList;

//@Configuration
//public class HandConfig {
//
//    private HandImporter loadHandModel(String modelFileName, boolean isLeft) {
//        HandImporter handImporter = new HandImporter(modelFileName, isLeft, false);
//        handImporter.readModel(50f);
//        return handImporter;
//    }
//
//    @Bean
//    public List<Parent> forestRight() {
//        HandImporter handRight = loadHandModel("modelRight.json", false);
//        return handRight.getJointForest();
//    }
//
//    @Bean
//    public PolygonMeshView skinningRight() {
//        HandImporter handRight = loadHandModel("modelRight.json", false);
//        return handRight.getSkinningMeshView();
//    }
//
//    @Bean
//    public List<Parent> forestLeft() {
//        HandImporter handLeft = loadHandModel("modelLeft.json", true);
//        return handLeft.getJointForest();
//    }
//
//    @Bean
//    public PolygonMeshView skinningLeft() {
//        HandImporter handLeft = loadHandModel("modelLeft.json", true);
//        return handLeft.getSkinningMeshView();
//    }
//}
