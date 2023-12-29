package ovh.maddie480.shscontrol;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
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
        try (Socket socket = new Socket("gta6-chatcontrol.maddie480.ovh", 11584);
             InputStream is = socket.getInputStream();
             OutputStream os = socket.getOutputStream()) {

            Path communicationsFile = Paths.get(args[0]).resolve("Main/ChatControl.txt");
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
        }
    }
}
