package tv.toner.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tv.toner.dto.JointStruct;

public class ClientHandler implements Runnable {

    private static final Logger log = LogManager.getLogger(ClientHandler.class);

    private final Socket socket;
    private static volatile List<JointStruct> latestValue;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {

            String data;
            while ((data = input.readLine()) != null) {
                handleReceivedData(data, output);
            }
        } catch (IOException ex) {
            log.error("Error handling socket input/output: {}", ex.getCause().getMessage());
        } finally {
            closeSocket();
        }
    }

    private void handleReceivedData(String data, PrintWriter output) {
        log.info("Received data from Arduino: {}", data);
        ObjectMapper objectMapper = new ObjectMapper();

        try {
//            latestValue = objectMapper.readValue(data, HandDTO.class);
            List<JointStruct> theLatestValue = objectMapper.readValue(data, objectMapper.getTypeFactory().constructCollectionType(List.class, JointStruct.class));
            output.println("Acknowledged: " + data);  // Acknowledge the data
            latestValue = theLatestValue;
        } catch (JsonProcessingException ex) {
            // If data cannot be parsed, log the error and continue
            log.warn("Unable to parse incoming data. Invalid JSON format: {}", data);
        }
    }

    private void closeSocket() {
        try {
            socket.close();
            log.info("Socket closed");
        } catch (IOException ex) {
            log.error("Error closing socket: ", ex);
        }
    }

    public static List<JointStruct> getLatestValue() {
        return latestValue;
    }
}
