import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple multi-client TCP chat server.
 * Each connected client is handled by a separate worker thread.
 */
public class ChatServer {
    private static final int DEFAULT_PORT = 5000;
    private static final AtomicInteger NEXT_USER_ID = new AtomicInteger(1);
    private static final Map<Integer, ClientHandler> CLIENTS = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port. Using default port " + DEFAULT_PORT + ".");
                port = DEFAULT_PORT;
            }
        }

        ExecutorService clientPool = Executors.newCachedThreadPool();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Chat server started on port " + port + ".");
            System.out.println("Waiting for clients... Press Ctrl+C to stop.");

            while (true) {
                Socket socket = serverSocket.accept();
                int userId = NEXT_USER_ID.getAndIncrement();
                ClientHandler handler = new ClientHandler(socket, userId);
                CLIENTS.put(userId, handler);
                clientPool.execute(handler);
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        } finally {
            clientPool.shutdown();
        }
    }

    /** Sends a message to every currently connected client. */
    private static void broadcast(String message) {
        for (ClientHandler client : CLIENTS.values()) {
            client.sendMessage(message);
        }
        System.out.println(message);
    }

    /** Handles incoming messages from one client. */
    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private final int userId;
        private PrintWriter output;

        ClientHandler(Socket socket, int userId) {
            this.socket = socket;
            this.userId = userId;
        }

        @Override
        public void run() {
            try (Socket clientSocket = socket;
                 BufferedReader input = new BufferedReader(new InputStreamReader(
                         clientSocket.getInputStream(), StandardCharsets.UTF_8));
                 PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true,
                         StandardCharsets.UTF_8)) {
                output = writer;
                sendMessage("Connected successfully. Your user ID is User-" + userId + ".");
                broadcast("User-" + userId + " joined the chat.");

                String message;
                while ((message = input.readLine()) != null) {
                    if (message.equalsIgnoreCase("/quit")) {
                        break;
                    }
                    if (!message.isBlank()) {
                        broadcast("User-" + userId + ": " + message);
                    }
                }
            } catch (IOException e) {
                System.out.println("Connection with User-" + userId
                        + " ended: " + e.getMessage());
            } finally {
                CLIENTS.remove(userId);
                broadcast("User-" + userId + " left the chat.");
            }
        }

        /** PrintWriter is used to send one line of text to this client. */
        synchronized void sendMessage(String message) {
            if (output != null) {
                output.println(message);
            }
        }
    }
}
