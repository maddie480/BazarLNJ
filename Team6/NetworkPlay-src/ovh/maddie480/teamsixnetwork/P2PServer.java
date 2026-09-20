package ovh.maddie480.teamsixnetwork;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class P2PServer {
    private static Socket socketWithServer;
    private static final Map<Byte, Connection> connections = new HashMap<>();

    static int getClientCount() {
        synchronized (connections) {
            return 1 + connections.size();
        }
    }

    private static class Connection {
        private Socket socketWithGame;
        private final OutputStream socketOutputStream;
        private final byte clientId;

        public Connection(byte clientId) throws IOException {
            this.socketOutputStream = socketWithServer.getOutputStream();
            this.clientId = clientId;

            System.out.println("[" + clientId + "] New client!");
        }

        public void incomingPacket(byte[] packet) throws IOException {
            ByteArrayInputStream is = new ByteArrayInputStream(packet);

            int opcode = is.read();
            switch (opcode) {
                case 1: { // TCP connect
                    System.out.println("[" + clientId + "] TCP connection open");
                    socketWithGame = new Socket("localhost", 24958);
                    connectionWithGame();
                    break;
                }

                case 2: { // TCP disconnect
                    if (socketWithGame != null) {
                        System.out.println("[" + clientId + "] TCP connection close");
                        socketWithGame.close();
                        socketWithGame = null;
                    }
                    break;
                }

                case 3: { // TCP message
                    int size = is.read();
                    byte[] bytes = new byte[size];
                    int received = is.read(bytes);
                    if (received < size) {
                        throw new IOException("Expected " + size + " bytes, received " + received);
                    }
                    System.out.println("[" + clientId + "] TCP message received, size " + size);
                    socketWithGame.getOutputStream().write(bytes);
                    break;
                }

                default: { // error
                    throw new IOException("Unknown opcode " + opcode);
                }
            }
        }

        private void connectionWithGame() {
            new Thread(() -> {
                try (InputStream is = socketWithGame.getInputStream()) {
                    while (true) {
                        byte[] b = new byte[250];
                        int size = is.read(b);

                        if (size == -1) {
                            throw new IOException("Game disconnected");
                        }

                        System.out.println("[" + clientId + "] Sent TCP packet to client");
                        synchronized (socketWithServer) {
                            socketOutputStream.write(clientId);
                            socketOutputStream.write(size + 2);
                            socketOutputStream.write(3);
                            socketOutputStream.write(size);
                            socketOutputStream.write(b, 0, size);
                            socketOutputStream.flush();
                        }
                    }

                } catch (IOException e) {
                    System.err.println("[" + clientId + "] Connection with game died! " + e);
                }

                try {
                    synchronized (socketWithServer) {
                        socketOutputStream.write(clientId);
                        socketOutputStream.write(1);
                        socketOutputStream.write(2);
                        socketOutputStream.flush();
                    }

                    System.out.println("[" + clientId + "] Connection with game closed");
                    if (socketWithGame != null) {
                        socketWithGame.close();
                        socketWithGame = null;
                    }
                } catch (IOException e) {
                    System.err.println("[" + clientId + "] Error while sending connection close to client! " + e);
                }
            }).start();
        }

        public void udpCaptured(byte[] udp, int size) {
            try {
                System.out.println("[" + clientId + "] Sent UDP packet to client");
                synchronized (socketWithServer) {
                    socketOutputStream.write(clientId);
                    socketOutputStream.write(size + 2);
                    socketOutputStream.write(4);
                    socketOutputStream.write(size);
                    socketOutputStream.write(udp, 0, size);
                }
            } catch (IOException e) {
                System.err.println("[" + clientId + "] Error while sending connection UDP packet to client! " + e);
            }
        }

        public void close() throws IOException {
            System.out.println("[" + clientId + "] Connection closed.");

            if (socketWithGame != null) {
                socketWithGame.close();
                socketWithGame = null;
            }
        }
    }

    static void launch(Socket sock) throws IOException {
        socketWithServer = sock;

        udpCatcher();

        InputStream is = socketWithServer.getInputStream();
        int opcode;
        while ((opcode = is.read()) != -1) {
            if (opcode < 2) {
                // open/close connection
                byte clientId;
                {
                    int clientIdI = is.read();
                    if (clientIdI == -1) throw new IOException("Connection with server closed");
                    clientId = (byte) clientIdI;
                }

                synchronized (connections) {
                    if (opcode == 0) {
                        connections.put(clientId, new Connection(clientId));
                    } else {
                        if (connections.containsKey(clientId)) {
                            connections.get(clientId).close();
                            connections.remove(clientId);
                        }
                    }
                }
            } else {
                // packet received from a client
                byte clientId = (byte) opcode;

                int size = is.read();
                if (size == -1) throw new IOException("Connection closed");

                byte[] content = new byte[size];
                if (is.read(content) != size) throw new IOException("Connection closed");

                synchronized (connections) {
                    if (connections.containsKey(clientId)) {
                        connections.get(clientId).incomingPacket(content);
                    }
                }
            }
        }
    }

    private static void udpCatcher() {
        new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(24958)) {
                while (true) {
                    try {
                        byte[] buffer = new byte[250];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);

                        synchronized (connections) {
                            for (Connection connection : connections.values()) {
                                connection.udpCaptured(packet.getData(), packet.getLength());
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("[server] UDP listener died! " + e);
                    }
                }
            } catch (Exception e) {
                System.out.println("[server] UDP listener died! " + e);
            }
        }).start();
    }
}
