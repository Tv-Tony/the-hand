package tv.toner;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import tv.toner.socket.ClientHandler;

@SpringBootApplication
public class TheHand extends Application {

        private static final Logger log = LogManager.getLogger(TheHand.class);

    private static ConfigurableApplicationContext springContext;


    public static void main(String[] args) {
        // Start Spring Boot application
        springContext = SpringApplication.run(TheHand.class, args);

        // Start JavaFX application
        launch(args);
    }

    @Override
    public void start(javafx.stage.Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/open-view.fxml"));

        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.setTitle("3D Hand Viewer");
        primaryStage.show();

        // Start the socket server in a separate thread
        new Thread(this::startSocketServer).start();
    }

    @Override
    public void stop() {
        // Clean up Spring when the JavaFX window closes
        springContext.close();
    }

    private void startSocketServer() {
        int port = 12344; // Port to listen on

//        GloveListener listener = new GloveListener();
//        listener.startListening();

        try (ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName("127.0.0.1"));) {
            log.info("Server is listening on port {}", port);

            while (true) {
                Socket socket = serverSocket.accept();
                log.info("Connected to Arduino client");
                new Thread(new ClientHandler(socket)).start(); // Handle client connection
            }
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

//    public static void main (String[] args) {
//        launch(args);
//    }
}
