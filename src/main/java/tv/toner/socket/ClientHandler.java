package tv.toner.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;

    private static int latestValue = -1;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {

            String data;
            while ((data = input.readLine()) != null) {
                System.out.println("Received: " + data);

                try {
                    latestValue = Integer.parseInt(data);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format: " + data);
                }

                output.println("Acknowledged: " + data);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Method to get the latest integer value
    public static int getLatestValue() {
        return latestValue;
    }
}
