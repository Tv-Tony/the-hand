package tv.toner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

@SpringBootApplication
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/open-view.fxml"));
            loader.setControllerFactory(springContext::getBean);

            Scene scene = new Scene(loader.load());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            log.error("Error loading FXML file");
        }

    }

    @Override
    public void stop() {
        // Shut down Spring when JavaFX closes
        if (springContext != null) {
            springContext.close();
        }
    }
}