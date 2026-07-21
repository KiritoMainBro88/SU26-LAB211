package dataobject;

import core.entities.Mountain;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import utilities.LogUtils;

/** Loads mountain reference data from MountainList.csv and supports normalized code lookup. */
public class MountainDAO {

    private static final String FILE_NAME = "MountainList.csv";
    private final List<Mountain> mountains;

    public MountainDAO() {
        mountains = new ArrayList<Mountain>();
    }

    public boolean loadMountains() {
        File file = new File(FILE_NAME);
        if (!file.exists() || !file.isFile()) {
            return false;
        }

        List<Mountain> loadedMountains = new ArrayList<Mountain>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                if (firstLine && isHeaderLine(line)) {
                    firstLine = false;
                    continue;
                }
                firstLine = false;
                List<String> columns = parseCsvLine(line);
                if (columns.size() < 4) {
                    continue;
                }
                Iterator<String> iterator = columns.iterator();
                String code = normalizeMountainCode(nextValue(iterator));
                String mountain = nextValue(iterator).trim();
                String province = nextValue(iterator).trim();
                String description = joinRemainingColumns(iterator);
                if (!code.isEmpty() && !mountain.isEmpty()) {
                    loadedMountains.add(new Mountain(code, mountain, province, description));
                }
            }
        } catch (IOException ex) {
            LogUtils.logError("MountainDAO.loadMountains", ex);
            return false;
        }

        mountains.clear();
        mountains.addAll(loadedMountains);
        return true;
    }

    public List<Mountain> getAll() {
        return new ArrayList<Mountain>(mountains);
    }

    public Mountain getMountainByCode(String mountainCode) {
        String normalizedCode = normalizeMountainCode(mountainCode);
        for (Mountain mountain : mountains) {
            if (mountain.getMountainCode().equalsIgnoreCase(normalizedCode)) {
                return mountain;
            }
        }
        return null;
    }

    public boolean isValidMountainCode(String mountainCode) {
        return getMountainByCode(mountainCode) != null;
    }

    public static String normalizeMountainCode(String mountainCode) {
        if (mountainCode == null) {
            return "";
        }
        String code = mountainCode.trim().toUpperCase(Locale.ROOT);
        if (code.isEmpty()) {
            return "";
        }
        if (code.startsWith("MT")) {
            String numberPart = code.substring(2);
            return isDigits(numberPart) ? formatMountainCode(numberPart) : code;
        }
        return isDigits(code) ? formatMountainCode(code) : code;
    }

    private List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<String>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        result.add(current.toString().trim());
        return result;
    }

    private boolean isHeaderLine(String line) {
        String lower = line.toLowerCase(Locale.ROOT);
        return lower.contains("code") && lower.contains("mountain") && lower.contains("province");
    }

    private String joinRemainingColumns(Iterator<String> iterator) {
        StringBuilder description = new StringBuilder();
        while (iterator.hasNext()) {
            if (description.length() > 0) {
                description.append(", ");
            }
            description.append(iterator.next().trim());
        }
        return description.toString();
    }

    private static String nextValue(Iterator<String> iterator) {
        return iterator.hasNext() ? iterator.next() : "";
    }

    private static boolean isDigits(String value) {
        return value != null && !value.isEmpty() && value.matches("\\d+");
    }

    private static String formatMountainCode(String numberText) {
        String significant = numberText.replaceFirst("^0+(?!$)", "");
        return significant.length() == 1 ? "MT0" + significant : "MT" + significant;
    }
}
