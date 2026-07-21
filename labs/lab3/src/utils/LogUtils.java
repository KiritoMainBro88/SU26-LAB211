package utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

/** Writes non-fatal warnings and errors to logs/error.log. */
public final class LogUtils {

    private static final String LOG_FILE = "logs/error.log";

    private LogUtils() {
    }

    public static void logWarning(String message) {
        write("WARNING", message, null);
    }

    public static void logError(String message, Exception exception) {
        write("ERROR", message, exception);
    }

    private static void write(String level, String message, Exception exception) {
        try {
            Path path = Paths.get(LOG_FILE);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                writer.write(LocalDateTime.now() + " [" + level + "] " + message);
                if (exception != null) {
                    writer.write(" | Exception: " + exception.getClass().getSimpleName()
                            + " - " + exception.getMessage());
                }
                writer.newLine();
            }
        } catch (IOException ignored) {
            // Logging failures must not terminate the main application.
        }
    }
}
