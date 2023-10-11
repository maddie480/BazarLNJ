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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextureDecoder {
    public static void main(String[] args) throws IOException {
        Path input = Paths.get(args[0]);
        Path output = Paths.get(args[1]);

        if (Files.isDirectory(input)) {
            try (Stream<Path> textures = Files.walk(input)) {
                for (Path inputFile : textures.filter(l -> l.getFileName().toString().toLowerCase().endsWith(".dct")).collect(Collectors.toList())) {
                    String fileName = inputFile.getFileName().toString();
                    fileName = fileName.substring(0, fileName.length() - 4) + ".png";
                    Path outputFile = output.resolve(input.relativize(inputFile)).getParent().resolve(fileName);
                    Files.createDirectories(outputFile.getParent());
                    convertTexture(inputFile, outputFile);
                }
            }
        } else {
            convertTexture(input, output);
        }
    }

    private static void convertTexture(Path input, Path output) throws IOException {
        try (InputStream is = Files.newInputStream(input)) {
            is.skip(0x13);
            BufferedImage result = new BufferedImage(readInt32(is), readInt32(is), BufferedImage.TYPE_INT_ARGB);
            is.skip(4);

            int format = is.read();
            is.skip(4);

            BufferedImage image = new BufferedImage(readInt32(is), readInt32(is), BufferedImage.TYPE_INT_ARGB);
            is.skip(4);

            switch (format) {
                case 0:
                    readBitmap(is, image, true);
                    break;

                case 5:
                    readBitmap(is, image, false);
                    break;

                case 8:
                    readWeirdTeam6Format(is, image, false);
                    break;

                case 9:
                    readWeirdTeam6Format(is, image, true);
                    break;

                default:
                    throw new IOException("Unknown texture format: 0x" + Integer.toHexString(format));
            }

            result.getGraphics().drawImage(image, 0, 0, null);

            try (OutputStream os = Files.newOutputStream(output)) {
                ImageIO.write(result, "png", os);
            }
        }
    }

    private static void readBitmap(InputStream is, BufferedImage image, boolean hasAlpha) throws IOException {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color c = new Color(is.read(), is.read(), is.read(), hasAlpha ? is.read() : 255);
                image.setRGB(x, y, c.getRGB());
            }
        }
    }

    private static void readWeirdTeam6Format(InputStream is, BufferedImage image, boolean hasAlpha) throws IOException {
        for (int y = 0; y < image.getHeight(); y += 4) {
            for (int x = 0; x < image.getWidth(); x += 4) {
                int[] alphas = new int[16];
                if (hasAlpha) {
                    for (int i = 0; i < 16; i += 2) {
                        int input = is.read();
                        alphas[i] = (input & 0xF) * 255 / 15;
                        alphas[i + 1] = (input >> 4) * 255 / 15;
                    }
                } else {
                    for (int i = 0; i < 16; i++) {
                        alphas[i] = 255;
                    }
                }

                Color c1 = fromRGB565(is.read() + (is.read() << 8));
                Color c2 = fromRGB565(is.read() + (is.read() << 8));

                for (int yi = 0; yi < 4; yi++) {
                    int line = is.read();
                    for (int xi = 0; xi < 4; xi++) {
                        int index = (line >> (xi * 2)) & 0b11;

                        Color c = Color.BLACK;

                        switch (index) {
                            case 0:
                                c = c1;
                                break;
                            case 1:
                                c = c2;
                                break;
                            case 2:
                                c = new Color(
                                        (c1.getRed() * 2 + c2.getRed()) / 3,
                                        (c1.getGreen() * 2 + c2.getGreen()) / 3,
                                        (c1.getBlue() * 2 + c2.getBlue()) / 3
                                );
                                break;
                            case 3:
                                c = new Color(
                                        (c1.getRed() + c2.getRed() * 2) / 3,
                                        (c1.getGreen() + c2.getGreen() * 2) / 3,
                                        (c1.getBlue() + c2.getBlue() * 2) / 3
                                );
                                break;
                        }

                        c = new Color(c.getRed(), c.getGreen(), c.getBlue(), alphas[yi * 4 + xi]);

                        image.setRGB(x + xi, y + yi, c.getRGB());
                    }
                }
            }
        }
    }

    private static Color fromRGB565(int src) {
        return new Color(
                ((src & 0b1111100000000000) >> 11) * 255 / 0b11111,
                ((src & 0b0000011111100000) >> 5) * 255 / 0b111111,
                (src & 0b0000000000011111) * 255 / 0b11111
        );
    }

    /**
     * Reads a big-endian int32 from the stream.
     */
    private static int readInt32(InputStream is) throws IOException {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result += is.read() << (i * 8);
        }
        return result;
    }

    /**
     * Reads an unsigned big-endian int32 from the stream... as a long, since Java ints cannot be unsigned.
     */
    private static long readUnsignedInt32(InputStream is) throws IOException {
        long result = readInt32(is);
        if (result < 0) result += 0x80000000L;
        return result;
    }
}
