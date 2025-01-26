package tv.toner;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tv.toner.socket.ClientHandler;

public class Start extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/open-view.fxml"));

        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.setTitle("3D Hand Viewer");
        primaryStage.show();

        // Start the socket server in a separate thread
        new Thread(this::startSocketServer).start();
    }

    private void startSocketServer() {
        int port = 12344; // Port to listen on

        try (ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName("127.0.0.1"));) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Connected to Arduino client");

                // Handle client connection
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main (String[] args) {
        launch(args);
    }
}