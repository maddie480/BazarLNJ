package ovh.maddie480;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MediaConverter {
    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        HttpURLConnection source = (HttpURLConnection) new URI(System.getenv("PSYCHO_KILLER_SRC")).toURL().openConnection();
        source.setRequestProperty("User-Agent", "Maddie-Media-Converter/1.0 (+https://github.com/maddie480/BazarLNJ/tree/main/PsychoKiller/MediaConverter-src)");

        Path tmp = Paths.get("/tmp/thing");
        Path output = Paths.get("output");

        try (ZipInputStream zis = new ZipInputStream(source.getInputStream())) {
            ZipEntry e;

            while ((e = zis.getNextEntry()) != null) {
                if (e.isDirectory()) continue;

                try (OutputStream os = Files.newOutputStream(tmp)) {
                    byte[] buf = new byte[256];
                    int length;
                    while ((length = zis.read(buf)) != -1) {
                        os.write(buf, 0, length);
                    }
                }

                boolean hasVideo = false;
                boolean hasAudio = false;
                boolean hasNoDuration = false;

                Process p = new ProcessBuilder("ffprobe", tmp.toAbsolutePath().toString())
                        .inheritIO()
                        .redirectError(ProcessBuilder.Redirect.PIPE)
                        .start();

                try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.contains("Video: ")) hasVideo = true;
                        if (line.contains("Audio: ")) hasAudio = true;
                        if (line.contains("Duration: N/A")) hasNoDuration = true;
                    }
                }

                String format;
                if (hasVideo) {
                    if (hasNoDuration) {
                        format = "png";
                    } else {
                        format = "mp4";
                    }
                } else if (hasAudio) {
                    format = "wav";
                } else {
                    Files.delete(tmp);
                    continue;
                }

                Path outputFile = output.resolve(e.getName() + "." + format);
                Files.createDirectories(outputFile.getParent());

                ProcessBuilder pb;

                if (format.equals("mp4")) {
                    pb = new ProcessBuilder("ffmpeg",
                            "-i", tmp.toAbsolutePath().toString(),
                            "-filter:v", "setpts=12*PTS",
                            "-r", "5",
                            outputFile.toAbsolutePath().toString());
                } else {
                    pb = new ProcessBuilder("ffmpeg",
                            "-i", tmp.toAbsolutePath().toString(),
                            outputFile.toAbsolutePath().toString());
                }

                p = pb.inheritIO().start();
                p.waitFor();

                Files.delete(tmp);

                if (p.exitValue() != 0 && Files.exists(outputFile)) {
                    Files.delete(outputFile);
                }
            }
        }
    }
}
