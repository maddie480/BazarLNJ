package ovh.maddie480;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LanguageRemaker {
    public static void main(String[] args) throws IOException {
        Path src = Paths.get(args[0]);
        Path dst = Paths.get(args[1]);
        // pass "ints" for most Team 6 games, and "strings" for FlatOut 3, because yes, it uses the same engine apparently
        boolean keysAreStrings = args[2].equals("strings");

        try (Stream<String> input = Files.lines(src, StandardCharsets.UTF_8);
             OutputStream os = Files.newOutputStream(dst)) {

            List<String> lines = input.collect(Collectors.toList());

            // write header
            os.write(new byte[]{0x44, 0x43, 0x32, 0x00, 0x00, 0x00, 0x00, 0x07, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00});

            writeInt32(lines.size(), os);

            for (String line : lines) {
                String[] split = line.split(";", 2);

                if (keysAreStrings) {
                    writeString(split[0], os);
                } else {
                    writeInt32(Integer.parseInt(split[0]), os);
                }

                writeString(split[1], os);
            }
        }
    }

    /**
     * Writes the length of the string, then its encoded contents to the given output stream.
     */
    private static void writeString(String string, OutputStream os) throws IOException {
        writeInt32(string.length(), os);
        if (string.isEmpty()) return;

        byte[] encodedString = flipTheBytes(encodeString(string));
        os.write(encodedString);
    }

    /**
     * Encodes the string into bytes, using Erwin's special "subtract the previous character from the current encoded
     * character to get the current decoded character, wrapping around for the first one" algorithm.
     */
    private static byte[] encodeString(String input) {
        char[] originalString = input.toCharArray();
        byte[] encodedString = new byte[originalString.length * 2];

        int lastValue = 0;
        for (int j = 1; j < originalString.length; j++) {
            lastValue = originalString[j - 1] + originalString[j];
            byte[] value = toInt16(lastValue);
            encodedString[j * 2] = value[0];
            encodedString[j * 2 + 1] = value[1];
        }

        byte[] value = toInt16(lastValue + originalString[0]);
        encodedString[0] = value[0];
        encodedString[1] = value[1];
        return encodedString;
    }

    /**
     * Flips (xor 0xFF) all bytes that are at positions with % 4 < 2. Because yes.
     */
    private static byte[] flipTheBytes(byte[] input) {
        for (int j = 0; j < input.length; j += 4) {
            input[j] ^= -1;
            input[j + 1] ^= -1;
        }

        return input;
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

    /**
     * Splits the given input integer into an int16 (made of 2 bytes).
     */
    private static byte[] toInt16(int input) {
        return new byte[]{
                (byte) (input & 0xFF),
                (byte) ((input >> 8) & 0xFF)
        };
    }
}
