package ovh.maddie480;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

public class P2PClient {
    private static Socket socketWithGame;

    public static void main(String[] args) {
        String ip = JOptionPane.showInputDialog("Veuillez saisir l'adresse du serveur :");

        try (Socket socketWithClient = new Socket(ip, 4480);
             InputStream is = socketWithClient.getInputStream();
             OutputStream os = socketWithClient.getOutputStream()) {

            gameConnectionCatcher(os);

            while (true) {
                int opcode;
                while ((opcode = is.read()) != -1) {
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
                            int size = is.read();
                            byte[] bytes = new byte[size];
                            int received = is.read(bytes);
                            if (received < size) {
                                throw new IOException("Expected " + size + " bytes, received" + received);
                            }
                            System.out.println("TCP message received, size " + size);
                            socketWithGame.getOutputStream().write(bytes);
                            break;
                        }

                        case 4: { // UDP message
                            int size = is.read();
                            byte[] bytes = new byte[size];
                            int received = is.read(bytes);
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
        } catch (IOException e) {
            System.err.println("Connection to server died!");
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Connection failed: " + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private static void gameConnectionCatcher(OutputStream serverOutputStream) {
        new Thread(() -> {
            while (true) {
                try (ServerSocket server = new ServerSocket(24958)) {
                    socketWithGame = server.accept();

                    serverOutputStream.write(1);

                    try (InputStream is = socketWithGame.getInputStream()) {
                        while (true) {
                            byte[] b = new byte[255];
                            int size = is.read(b);

                            if (size == -1) {
                                throw new IOException("Game disconnected");
                            }

                            System.out.println("Sent TCP packet to server");
                            serverOutputStream.write(3);
                            serverOutputStream.write(size);
                            serverOutputStream.write(b, 0, size);
                        }

                    } catch (IOException e) {
                        System.err.println("Connection with game died!");
                        e.printStackTrace();
                    }

                    serverOutputStream.write(2);

                    System.out.println("Connection with game closed");
                    if (socketWithGame != null) {
                        socketWithGame.close();
                        socketWithGame = null;
                    }
                } catch (IOException e) {
                    System.err.println("Game connection listener died!");
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
