package ovh.maddie480;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TextureEncoder {
    public static void main(String[] args) throws IOException {
        Path input = Paths.get(args[0]);
        Path output = Paths.get(args[1]);

        BufferedImage inputImage;
        try (InputStream is = Files.newInputStream(input)) {
            inputImage = ImageIO.read(is);
        }

        try (OutputStream os = Files.newOutputStream(output)) {
            os.write(new byte[]{
                    0x44, 0x43, 0x32, 0x02, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x02
            });

            writeInt32(inputImage.getWidth(), os);
            writeInt32(inputImage.getHeight(), os);

            os.write(new byte[]{
                    0x01, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00
            });

            writeInt32(inputImage.getWidth(), os);
            writeInt32(inputImage.getHeight(), os);
            writeInt32(inputImage.getWidth() * inputImage.getHeight() * 4, os);

            for (int y = 0; y < inputImage.getHeight(); y++) {
                for (int x = 0; x < inputImage.getWidth(); x++) {
                    Color pixel = new Color(inputImage.getRGB(x, y), true);
                    os.write(pixel.getBlue());
                    os.write(pixel.getGreen());
                    os.write(pixel.getRed());
                    os.write(pixel.getAlpha());
                }
            }
        }
    }

    /**
     * Writes a big-endian int32 into the stream.
     */
    private static void writeInt32(int input, OutputStream os) throws IOException {
        for (int i = 0; i < 4; i++) {
            os.write(input & 0xFF);
            input >>= 8;
        }
    }
}
