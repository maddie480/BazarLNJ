package ovh.maddie480;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Un tunnel TCP pour rediriger le trafic qui vient d'Internet vers la VM, et ainsi contourner le problème
 * "comment je fais pour ouvrir ma VM sur Internet pour jouer à Glacier avec mes copains".
 */
public class TCPTunnel {

    public static void main(String[] args) throws IOException {
        String ip = JOptionPane.showInputDialog("Destination :");
        int port = Integer.parseInt(JOptionPane.showInputDialog("Port :"));

        try (ServerSocket socket = new ServerSocket(port)) {
            while (true) {
                Socket client = socket.accept();

                try {
                    Socket target = new Socket(ip, port);

                    startCopyThread(client, target);
                    startCopyThread(target, client);

                } catch (IOException e) {
                    System.err.println("Could not open connection to target!");
                    e.printStackTrace();

                    client.close();
                }
            }
        }
    }

    private static void startCopyThread(Socket source, Socket destination) {
        new Thread(() -> {
            try (InputStream is = source.getInputStream();
                 OutputStream os = destination.getOutputStream()) {

                byte[] buffer = new byte[256];

                while (true) {
                    int read = is.read(buffer);
                    if (read == -1) break;
                    os.write(buffer, 0, read);
                }

                System.err.println("Stream closed.");

            } catch (IOException e) {
                System.err.println("Stream died!");
                e.printStackTrace();
            }
        }).start();
    }
}
