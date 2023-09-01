package ovh.maddie480;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class P2PServer {
    private static Set<Connection> connections = new HashSet<>();
    private static AtomicInteger clientIdInc = new AtomicInteger(1);

    private static class Connection implements Closeable {
        private final Socket socketWithClient;
        private final OutputStream socketOutputStream;
        private Socket socketWithGame;
        private final Semaphore mutex;
        private final int clientId;

        public Connection(Socket socket) throws IOException {
            this.socketWithClient = socket;
            this.socketOutputStream = socket.getOutputStream();
            this.mutex = new Semaphore(1);
            this.clientId = clientIdInc.getAndIncrement();

            System.out.println("[" + clientId + "] New client!");

            connections.add(this);

            new Thread(() -> {
                try (InputStream is = socket.getInputStream()) {
                    int opcode;
                    while ((opcode = is.read()) != -1) {
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
                    System.out.println("[" + clientId + "] End of input stream");
                    close();

                } catch (IOException e) {
                    System.err.println("[" + clientId + "] Connection died!");
                    e.printStackTrace();

                    try {
                        close();
                    } catch (IOException e2) {
                        System.err.println("[" + clientId + "] Cleanup after crash failed");
                        e2.printStackTrace();
                    }
                }
            }).start();
        }

        private void connectionWithGame() {
            new Thread(() -> {
                try (InputStream is = socketWithGame.getInputStream()) {
                    while (true) {
                        byte[] b = new byte[255];
                        int size = is.read(b);

                        if (size == -1) {
                            throw new IOException("Game disconnected");
                        }

                        System.out.println("[" + clientId + "] Sent TCP packet to client");
                        mutex.acquireUninterruptibly();
                        socketOutputStream.write(3);
                        socketOutputStream.write(size);
                        socketOutputStream.write(b, 0, size);
                        mutex.release();
                    }

                } catch (IOException e) {
                    System.err.println("[" + clientId + "] Connection with game died!");
                    e.printStackTrace();
                }

                try {
                    mutex.acquireUninterruptibly();
                    socketOutputStream.write(2);
                    mutex.release();

                    System.out.println("[" + clientId + "] Connection with game closed");
                    if (socketWithGame != null) {
                        socketWithGame.close();
                        socketWithGame = null;
                    }
                } catch (IOException e) {
                    System.err.println("[" + clientId + "] Error while sending connection close to client!");
                    e.printStackTrace();
                }
            }).start();
        }

        public void udpCaptured(byte[] udp, int size) {
            try {
                System.out.println("[" + clientId + "] Sent UDP packet to client");
                mutex.acquireUninterruptibly();
                socketOutputStream.write(4);
                socketOutputStream.write(size);
                socketOutputStream.write(udp, 0, size);
                mutex.release();
            } catch (IOException e) {
                System.err.println("[" + clientId + "] Error while sending connection UDP packet to client!");
                e.printStackTrace();
            }
        }

        @Override
        public void close() throws IOException {
            if (!connections.contains(this)) return;

            System.out.println("[" + clientId + "] Connection closed.");
            socketOutputStream.close();
            socketWithClient.close();
            if (socketWithGame != null) {
                socketWithGame.close();
                socketWithGame = null;
            }

            connections.remove(this);
        }
    }

    public static void main(String[] args) {
        udpCatcher();

        try (ServerSocket socket = new ServerSocket(4480)) {
            while (true) {
                try {
                    new P2PServer.Connection(socket.accept());
                } catch (IOException e) {
                    System.out.println("[server] Connection could not be established!");
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            System.out.println("[server] Connection listener died!");
            e.printStackTrace();
        }
    }

    private static void udpCatcher() {
        new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(24958)) {
                while (true) {
                    try {
                        byte[] buffer = new byte[255];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);

                        for (P2PServer.Connection connection : connections) {
                            connection.udpCaptured(packet.getData(), packet.getLength());
                        }
                    } catch (IOException e) {
                        System.out.println("[server] UDP listener died!");
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                System.out.println("[server] UDP listener died!");
                e.printStackTrace();
            }
        }).start();
    }
}
