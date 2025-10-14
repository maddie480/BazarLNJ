package ovh.maddie480;

import org.apache.commons.io.EndianUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class GPExplorer {
    private static class FileInfo {
        public String name;
        public int baseAddress;
    }

    public static void main(String[] args) throws IOException {
        Path input = Paths.get(args[0]);
        Path output = Paths.get(args[1]);
        Files.createDirectories(output);

        FileInfo[] files = getFileList(input);

        for (FileInfo file : files) {
            try {
                ImageIO.write(readImage(input, file), "png", output.resolve(file.name + ".png").toFile());
                System.out.println("Extracted " + file.name);
            } catch (Exception e) {
                System.err.println("Cannot extract " + file.name + ": " + e.getMessage());
            }
        }
    }

    static BufferedImage readImage(Path input, FileInfo file) throws IOException {
        int size = EndianUtils.readSwappedInteger(randomAccess(input, file.baseAddress + 4, 4), 0);
        byte[] data = randomAccess(input, file.baseAddress, size);

        // no clue if this is actually a "mode", but I'll roll with it
        byte mode = randomAccess(input, file.baseAddress + 0xD, 1)[0];
        switch (mode) {
            case 2:
                return extractRawJpeg(data);
            case 0:
                return extractRawBitmap(data, GPExplorer::decodeFromRGB555);
            case 68:
                return extractRawBitmap(data, GPExplorer::decodeFromRGBA8888);
            case -128:
                return extractDeflated(data, GPExplorer::decodeFromRGB555);
            case -124:
                return extractDeflated(data, GPExplorer::decodeFromARGB4444);
            case -60:
                return extractDeflated(data, GPExplorer::decodeFromRGBA8888);
            default:
                throw new IOException("Unsupported mode: " + mode);
        }
    }

    static FileInfo[] getFileList(Path input) throws IOException {
        // 0x34 indicates how many files to expect
        // 0x50 is the start of the pointer table for files
        // pointer+10 has the file name, \0 terminated

        int count = EndianUtils.readSwappedInteger(randomAccess(input, 0x34, 4), 0);
        FileInfo[] result = new FileInfo[count];
        for (int i = 0; i < count; i++) {
            int offset = 0x50 + i * 4;
            FileInfo info = new FileInfo();
            info.baseAddress = EndianUtils.readSwappedInteger(randomAccess(input, offset, 4), 0);
            info.name = new String(randomAccess(input, info.baseAddress + 0x10, -1), StandardCharsets.UTF_8);
            result[i] = info;
        }

        return result;
    }

    private static BufferedImage extractRawJpeg(byte[] data) throws IOException {
        // 0x70 has the file's length
        // 0x74 is the start of the jpeg

        int length = EndianUtils.readSwappedInteger(data, 0x70);
        return ImageIO.read(new ByteArrayInputStream(data, 0x74, length));
    }

    private static BufferedImage extractRawBitmap(byte[] data, Function<byte[], Color[]> decoder) throws IOException {
        // 0x40 and 0x42 have the image dimensions
        // 0x70 is the start of the data

        short width = EndianUtils.readSwappedShort(data, 0x40);
        short height = EndianUtils.readSwappedShort(data, 0x42);

        data = slice(data, 0x70, data.length - 0x70);
        Color[] pixels = decoder.apply(data);
        return pixelsToImage(pixels, width, height);
    }

    private static BufferedImage extractDeflated(byte[] data, Function<byte[], Color[]> decoder) throws IOException {
        // 0x40 and 0x42 have the image dimensions
        // 0x70 has yet another data length
        // 0x74 is the start of the data

        short width = EndianUtils.readSwappedShort(data, 0x40);
        short height = EndianUtils.readSwappedShort(data, 0x42);

        int length = EndianUtils.readSwappedInteger(data, 0x70);
        data = slice(data, 0x74, length - 4); // it's zlib but the checksum is broken, let's just remove it
        data = inflate(data);
        Color[] pixels = decoder.apply(data);
        return pixelsToImage(pixels, width, height);
    }

    private static byte[] inflate(byte[] input) throws IOException {
        Inflater inflater = new Inflater();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            inflater.setInput(input);
            byte[] buffer = new byte[4096];
            while (!inflater.needsInput()) { // zlib will expect the checksum, we're not giving it because it's borked
                int count = inflater.inflate(buffer);
                out.write(buffer, 0, count);
            }
            return out.toByteArray();
        } catch (DataFormatException e) {
            throw new IOException(e);
        } finally {
            inflater.end();
        }
    }

    private static Color[] decodeFromRGB555(byte[] data) {
        // 2 bytes per pixel
        // R, G and B all take 5 bits, the upper bit is wasted

        Color[] converted = new Color[data.length / 2];
        for (int i = 0; i < converted.length; i++) {
            short color = EndianUtils.readSwappedShort(data, i * 2);
            converted[i] = new Color(
                    ((color & 0b0111110000000000) >> 10) / (float) 0b11111,
                    ((color & 0b0000001111100000) >> 5) / (float) 0b11111,
                    (color & 0b0000000000011111) / (float) 0b11111
            );
        }
        return converted;
    }

    private static Color[] decodeFromARGB4444(byte[] data) {
        // 2 bytes per pixel
        // A, R, G and B all take 4 bits

        Color[] converted = new Color[data.length / 2];
        for (int i = 0; i < converted.length; i++) {
            short color = EndianUtils.readSwappedShort(data, i * 2);
            converted[i] = new Color(
                    ((color & 0b0000111100000000) >> 8) / (float) 0b1111,
                    ((color & 0b0000000011110000) >> 4) / (float) 0b1111,
                    (color & 0b0000000000001111) / (float) 0b1111,
                    ((color & 0b1111000000000000) >> 12) / (float) 0b1111
            );
        }
        return converted;
    }


    private static Color[] decodeFromRGBA8888(byte[] data) {
        // 4 bytes per pixel
        // R, G, B, A all take 8 bits

        Color[] converted = new Color[data.length / 4];
        for (int i = 0; i < converted.length; i++) {
            int color = EndianUtils.readSwappedInteger(data, i * 4);
            converted[i] = new Color(color, true);
        }
        return converted;
    }

    private static BufferedImage pixelsToImage(Color[] pixels, int width, int height) throws IOException {
        if (width * height != pixels.length) {
            throw new IOException("Wrong amount of pixels: " + width + " * " + height + " != " + pixels.length);
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, pixels[y * width + x].getRGB());
            }
        }
        return image;
    }


    private static byte[] randomAccess(Path path, long position, int length) throws IOException {
        try (InputStream in = new BufferedInputStream(Files.newInputStream(path))) {
            long toSkip = position;
            while (toSkip > 0) {
                long skipped = in.skip(toSkip);
                if (skipped == 0)
                    throw new IOException("Failed to seek past byte " + (position - toSkip) + ", requested " + position);
                toSkip -= skipped;
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream(length == -1 ? 32 : length);
            for (int i = 0; length == -1 || i < length; i++) {
                int read = in.read();
                if (read == -1) throw new IOException("Failed to read past byte " + i + ", requested " + length);
                if (length == -1 && read == 0) break;
                out.write(read);
            }
            return out.toByteArray();
        }
    }

    private static byte[] slice(byte[] data, int offset, int length) {
        byte[] subset = new byte[length];
        System.arraycopy(data, offset, subset, 0, length);
        return subset;
    }
}
