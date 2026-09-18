package ovh.maddie480.teamsixnetwork;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

public class P2PClient {
    private static Socket socketWithGame;

    static boolean isGameConnected() {
        return socketWithGame != null;
    }

    static void launch(Socket socketWithServer) throws IOException {
        try (InputStream is = socketWithServer.getInputStream();
             OutputStream os = socketWithServer.getOutputStream()) {

            gameConnectionCatcher(os);

            while (true) {
                // we follow the [sender, size, contents...] convention,
                // except the sender always has ID 0
                ByteArrayInputStream iis;
                {
                    int sender = is.read();
                    if (sender == -1) throw new IOException("Connection closed");
                    if (sender != 0) throw new IOException("Unexpected sender");

                    int size = is.read();
                    if (size == -1) throw new IOException("Connection closed");

                    byte[] contents = new byte[size];
                    if (is.read(contents) != size) throw new IOException("Connection closed");

                    iis = new ByteArrayInputStream(contents);
                }

                int opcode = iis.read();

                switch (opcode) {
                    case 2: { // TCP disconnect
                        if (socketWithGame != null) {
                            System.out.println("TCP connection to the game was closed");
                            socketWithGame.close();
                            socketWithGame = null;
                        }
                        break;
                    }

                    case 3: { // TCP message
                        int size = iis.read();
                        byte[] bytes = new byte[size];
                        int received = iis.read(bytes);
                        if (received < size) {
                            throw new IOException("Expected " + size + " bytes, received " + received);
                        }
                        System.out.println("TCP message received, size " + size);
                        socketWithGame.getOutputStream().write(bytes);
                        break;
                    }

                    case 4: { // UDP message
                        int size = iis.read();
                        byte[] bytes = new byte[size];
                        int received = iis.read(bytes);
                        if (received < size) {
                            throw new IOException("Expected " + size + " bytes, received " + received);
                        }
                        System.out.println("UDP message received, size " + size);

                        try (DatagramSocket socket = new DatagramSocket()) {
                            InetAddress broadcast = InetAddress.getByAddress(new byte[]{-1, -1, -1, -1});
                            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, broadcast, 24958);
                            socket.send(packet);
                        }

                        break;
                    }

                    default: { // error
                        throw new IOException("Unknown opcode " + opcode);
                    }
                }
            }
        }
    }

    private static void gameConnectionCatcher(OutputStream serverOutputStream) {
        new Thread(() -> {
            while (true) {
                try (ServerSocket server = new ServerSocket(24958)) {
                    socketWithGame = server.accept();

                    serverOutputStream.write(0); // recipient
                    serverOutputStream.write(1); // size
                    serverOutputStream.write(1); // TCP connect

                    try (InputStream is = socketWithGame.getInputStream()) {
                        while (true) {
                            byte[] b = new byte[250];
                            int size = is.read(b);

                            if (size == -1) {
                                throw new IOException("Game disconnected");
                            }

                            System.out.println("Sent TCP packet to server");
                            serverOutputStream.write(0); // recipient
                            serverOutputStream.write(size + 2); // size
                            serverOutputStream.write(3); // TCP message
                            serverOutputStream.write(size);
                            serverOutputStream.write(b, 0, size);
                        }

                    } catch (IOException e) {
                        System.err.println("Connection with game died! " + e);
                    }

                    serverOutputStream.write(0); // recipient
                    serverOutputStream.write(1); // size
                    serverOutputStream.write(2); // TCP disconnect

                    System.out.println("Connection with game closed");
                    if (socketWithGame != null) {
                        socketWithGame.close();
                        socketWithGame = null;
                    }
                } catch (IOException e) {
                    System.err.println("Game connection listener died! " + e);
                }
            }
        }).start();
    }
}
