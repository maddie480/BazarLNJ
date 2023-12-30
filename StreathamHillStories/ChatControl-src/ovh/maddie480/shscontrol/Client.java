package ovh.maddie480.shscontrol;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 3D GameStudio cannot socket, but it can read numbers from files!
 */
public class Client {
    public static void main(String[] args) {
        new Thread(() -> chatControlThread(args[0])).start();
        new Thread(() -> radioThread(args[0])).start();
    }

    private static void chatControlThread(String gamePath) {
        try (Socket socket = new Socket("gta6-chatcontrol.maddie480.ovh", 11584);
             InputStream is = socket.getInputStream();
             OutputStream os = socket.getOutputStream()) {

            Path communicationsFile = Paths.get(gamePath).resolve("Main/ChatControl.txt");
            if (Files.exists(communicationsFile)) Files.delete(communicationsFile);

            System.out.println("Connection established!");
            os.write(42);
            os.flush();

            while (true) {
                int command = is.read();

                try (Writer writer = Files.newBufferedWriter(communicationsFile, StandardCharsets.UTF_8)) {
                    writer.write(Integer.toString(command));
                }
                System.out.println("Command " + command + " sent to the game");
                while (Files.exists(communicationsFile)) {
                    Thread.sleep(100);
                }
                System.out.println("Command " + command + " processed by the game");

                os.write(command);
                os.flush();
            }

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de la communication avec le serveur :\n" + e.toString());
            System.exit(1);
        }
    }

    private static void radioThread(String gamePath) {
        Path game = Paths.get(gamePath);
        Path radioPleaseFile = game.resolve("Main/RadioPlz.txt");
        Path radioReadyFile = game.resolve("Main/RadioReady.txt");
        Path radioSoundFile = game.resolve("Main/RadioLNJ.mp3");

        while (true) {
            try {
                Thread.sleep(1000);
                if (Files.exists(radioReadyFile)) Files.delete(radioReadyFile);

                if (Files.exists(radioPleaseFile)) {
                    Files.delete(radioPleaseFile);

                    try (Socket socket = new Socket("gta6-chatcontrol.maddie480.ovh", 11585);
                         ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
                         OutputStream os = socket.getOutputStream()) {

                        System.out.println("Starting radio transfer");
                        os.write(43);
                        os.flush();

                        int size = is.readInt();
                        System.out.println("Size: " + size);

                        try (OutputStream fos = Files.newOutputStream(radioSoundFile)) {
                            int written = 0;
                            byte[] buf = new byte[4096];
                            while (written < size) {
                                int read = is.read(buf, 0, Math.min(4096, size - written));
                                fos.write(buf, 0, read);
                                written += read;
                            }
                        }

                        int timeLeft = is.readInt();
                        System.out.println("Time left: " + timeLeft);

                        // ack: the server sends us a random byte, we send it back
                        int ack = is.read();
                        os.write(ack);
                        os.flush();

                        try (BufferedWriter bw = Files.newBufferedWriter(radioReadyFile, StandardCharsets.UTF_8)) {
                            bw.write(Integer.toString(timeLeft));
                        }

                        System.out.println("Radio transfer ended!");
                    }
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
