package utils;

import java.util.ArrayList;
import java.util.List;

/** Minimal CSV escaping and parsing for the employee text-file format. */
public final class CsvUtils {

    private CsvUtils() {
    }

    public static List<String> parseLine(String line) throws Exception {
        if (line == null) {
            throw new Exception("CSV line cannot be null.");
        }
        List<String> fields = new ArrayList<String>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int index = 0; index < line.length(); index++) {
            char ch = line.charAt(index);
            if (ch == '"') {
                if (inQuotes && index + 1 < line.length() && line.charAt(index + 1) == '"') {
                    current.append('"');
                    index++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                fields.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        if (inQuotes) {
            throw new Exception("Unclosed quoted CSV field.");
        }
        fields.add(current.toString().trim());
        return fields;
    }

    public static String escape(String value) {
        String text = value == null ? "" : value;
        if (text.indexOf(',') < 0 && text.indexOf('"') < 0
                && text.indexOf('\n') < 0 && text.indexOf('\r') < 0) {
            return text;
        }
        return '"' + text.replace("\"", "\"\"") + '"';
    }
}
