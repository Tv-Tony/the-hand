package tv.toner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@SpringBootApplication
@EnableAspectJAutoProxy
public class TheHand extends Application {

    private static final Logger log = LogManager.getLogger(TheHand.class);

    private static ConfigurableApplicationContext springContext;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        springContext = SpringApplication.run(TheHand.class); // Start Spring Boot
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the main view FXML that includes hand-view and chart-view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main-view.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("The Hand");
            primaryStage.show();
        } catch (Exception e) {
            log.error("Error loading FXML file", e);
        }
    }

    @Override
    public void stop() {
        if (springContext != null) {
            springContext.close();
        }
    }
}