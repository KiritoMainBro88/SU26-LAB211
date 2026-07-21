package utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Writes runtime errors to logs/error.log without terminating the console application. */
public final class LogUtils {

    private static final String LOG_FOLDER = "logs";
    private static final String LOG_FILE = "error.log";

    private LogUtils() {
    }

    public static void logError(String location, Exception exception) {
        File folder = new File(LOG_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(folder, LOG_FILE);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file, true));
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            writer.write("[" + time + "] " + location + " - "
                    + exception.getClass().getSimpleName() + ": " + exception.getMessage());
            writer.newLine();
        } catch (IOException ignored) {
            // Do not rethrow: logging must not terminate the console application.
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {
                    // Ignore close error.
                }
            }
        }
    }
}
