# Unit 7 Programming Assignment: Online Chat Application

## Overview
This is a simple text-based Java TCP chat application. One `ChatServer` accepts multiple clients, assigns each connection a unique user ID, tracks connected users, and broadcasts messages. Each `ChatClient` can send messages and receive messages from other clients.

## Requirements
- Java JDK 17 or later (the code uses modern `PrintWriter` constructors available in current Java versions).
- A terminal or IDE such as VS Code/Cursor.
- The server and clients must be able to reach each other over the network.

## Files
- `src/ChatServer.java` — server, client tracking, unique IDs, and message broadcasting.
- `src/ChatClient.java` — text-based client interface for sending and receiving messages.

## Compile
Open a terminal in the project folder and run:

```bash
javac -d out src/ChatServer.java src/ChatClient.java
```

Windows PowerShell uses the same command.

## Run the server
In terminal window 1:

```bash
java -cp out ChatServer
```

The server listens on port `5000` by default. To choose another port:

```bash
java -cp out ChatServer 6000
```

## Run clients
Open a separate terminal for each participant. In terminal window 2:

```bash
java -cp out ChatClient
```

Open another terminal and run the same command to connect another client. The default host is `localhost`, which is suitable when the server and clients run on the same computer.

To connect from another computer on the same network, use the server computer's LAN IP address and the same port. For example:

```bash
java -cp out ChatClient 192.168.1.20 5000
```

Replace `192.168.1.20` with the actual IP address of the computer running the server. The firewall must allow connections to the chosen port.

## Using the application
1. Start `ChatServer` first.
2. Start two or more instances of `ChatClient`.
3. Type a message in any client terminal and press Enter.
4. The server broadcasts the message to all connected clients.
5. Type `/quit` and press Enter to leave the chat.

## Implementation notes
- `ServerSocket` listens for incoming TCP connections.
- `Socket` represents each client connection.
- `ConcurrentHashMap` stores connected client handlers safely when multiple threads are active.
- `AtomicInteger` assigns a unique increasing ID to each new connection.
- A cached thread pool handles clients concurrently.
- `BufferedReader` and `PrintWriter` exchange UTF-8 line-based messages.
- The client uses a receiver thread so incoming messages can be displayed while the user types.
- Try-with-resources closes client sockets and streams.
- This is an educational example, not a production-ready chat service. Production deployments should add authentication, TLS encryption, rate limiting, input limits, and stronger lifecycle/error handling.

## Screenshot and report submission
The assignment asks for a screenshot of the text-based user interface and a Word or PDF report. Run the server and at least two clients, exchange messages, and take a real screenshot showing the terminals and broadcast messages. Insert the screenshot into your report; do not use a mock screenshot.

Suggested screenshots:
1. Server terminal showing clients joining and a message being broadcast.
2. Two client terminals showing the same message received by both clients.

Include the screenshot(s), a brief explanation of the implementation, and the run instructions in the report. Submit the Java source files, README, and report according to your course's submission area.
