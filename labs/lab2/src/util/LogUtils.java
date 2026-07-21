package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Minimal file logger used for load/save failures. */
public final class LogUtils {

    private static final String ERROR_LOG_PATH = "logs/errors.log";

    private LogUtils() {
    }

    public static void logError(String message, Exception exception) {
        try {
            File logFile = new File(ERROR_LOG_PATH);
            File parentDirectory = logFile.getParentFile();
            if (parentDirectory != null && !parentDirectory.exists()) {
                parentDirectory.mkdirs();
            }
            try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
                writer.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                        + " - " + message);
                if (exception != null) {
                    writer.println(exception.getClass().getName() + ": " + exception.getMessage());
                }
            }
        } catch (IOException ignored) {
            // Logging must not terminate the console application.
        }
    }
}
