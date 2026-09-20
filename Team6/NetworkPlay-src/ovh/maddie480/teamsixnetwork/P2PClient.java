package ovh.maddie480.teamsixnetwork;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

import static ovh.maddie480.teamsixnetwork.P2PLauncher.readSingleByte;
import static ovh.maddie480.teamsixnetwork.P2PLauncher.unstoppableRead;

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
                    int sender = readSingleByte(is);
                    if (sender != 0) throw new IOException("Unexpected sender");

                    int size = readSingleByte(is);

                    byte[] contents = new byte[size];
                    unstoppableRead(is, contents);

                    iis = new ByteArrayInputStream(contents);
                }

                int opcode = readSingleByte(iis);

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
                        int size = readSingleByte(iis);
                        byte[] bytes = new byte[size];
                        unstoppableRead(iis, bytes);
                        System.out.println("TCP message received, size " + size);
                        socketWithGame.getOutputStream().write(bytes);
                        socketWithGame.getOutputStream().flush();
                        break;
                    }

                    case 4: { // UDP message
                        int size = readSingleByte(iis);
                        byte[] bytes = new byte[size];
                        unstoppableRead(iis, bytes);
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
                    serverOutputStream.flush();

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
                            serverOutputStream.flush();
                        }

                    } catch (IOException e) {
                        System.err.println("Connection with game died! " + e);
                    }

                    serverOutputStream.write(0); // recipient
                    serverOutputStream.write(1); // size
                    serverOutputStream.write(2); // TCP disconnect
                    serverOutputStream.flush();

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
