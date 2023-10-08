package ovh.maddie480;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LanguageDemaker {
    public static void main(String[] args) throws IOException {
        Path src = Paths.get(args[0]);
        Path dst = Paths.get(args[1]);

        try (InputStream is = Files.newInputStream(src);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(dst), StandardCharsets.UTF_8))) {

            // skip header
            is.skip(0xF);

            // first is the amount of strings
            int stringCount = readInt32(is);

            for (int i = 0; i < stringCount; i++) {
                // then for each string, we have an int32 id, and an int32 length
                int stringId = readInt32(is);
                int stringLength = readInt32(is);

                // and finally, the string itself, with 2 bytes per character
                byte[] rawString = new byte[stringLength * 2];
                is.read(rawString);

                String stringValue = decodeString(flipTheBytes(rawString));

                writer.write(stringId + ";" + stringValue + "\r\n");
            }
        }
    }

    /**
     * Decodes the string, using Erwin's special "subtract the previous character from the current encoded
     * character to get the current decoded character, wrapping around for the first one" algorithm.
     */
    private static String decodeString(byte[] rawString) {
        StringBuilder realString = new StringBuilder();

        if (rawString.length != 0) {
            int previous = toInt16(rawString[rawString.length - 2], rawString[rawString.length - 1]);

            for (int j = 0; j < rawString.length; j += 2) {
                int current = toInt16(rawString[j], rawString[j + 1]);
                int decoded = current - previous;
                realString.append((char) decoded);
                previous = decoded;
            }
        }

        return realString.toString();
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
     * Combines two bytes into an int16. (doesn't work if b2 < 0, but eh)
     */
    private static int toInt16(byte b1, byte b2) {
        int i1 = b1;
        if (i1 < 0) i1 += 256;
        return b2 * 256 + i1;
    }
}
