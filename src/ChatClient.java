import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Text-based chat client. Run one instance for each chat participant.
 */
public class ChatClient {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 5000;

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : DEFAULT_HOST;
        int port = DEFAULT_PORT;

        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port. Using default port " + DEFAULT_PORT + ".");
                port = DEFAULT_PORT;
            }
        }

        try (Socket socket = new Socket(host, port);
             BufferedReader serverInput = new BufferedReader(new InputStreamReader(
                     socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter serverOutput = new PrintWriter(socket.getOutputStream(), true,
                     StandardCharsets.UTF_8);
             Scanner keyboard = new Scanner(System.in, StandardCharsets.UTF_8)) {

            System.out.println("Connected to chat server at " + host + ":" + port + ".");
            System.out.println("Type a message and press Enter. Type /quit to leave.");

            // A separate thread receives broadcasts while the main thread accepts input.
            Thread receiver = new Thread(() -> {
                try {
                    String message;
                    while ((message = serverInput.readLine()) != null) {
                        System.out.println(message);
                    }
                    System.out.println("Server closed the connection.");
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            }, "chat-message-receiver");
            receiver.setDaemon(true);
            receiver.start();

            while (keyboard.hasNextLine()) {
                String message = keyboard.nextLine();
                serverOutput.println(message);
                if (message.equalsIgnoreCase("/quit")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Could not connect to the chat server: " + e.getMessage());
            System.out.println("Make sure ChatServer is running and the host/port are correct.");
        }
    }
}
