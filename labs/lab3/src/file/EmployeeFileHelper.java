package file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import model.Employee;
import model.EmployeeStatus;
import model.Role;
import utils.CsvUtils;
import utils.LogUtils;
import validation.EmployeeValidator;

/** Reads and writes employees.txt while keeping malformed rows isolated. */
public class EmployeeFileHelper implements IFileReadWrite<Employee> {

    private static final String DEFAULT_FILE_NAME = "employees.txt";
    private final Path configuredPath;

    public EmployeeFileHelper() {
        this.configuredPath = null;
    }

    /**
     * Uses an explicit data path when a caller needs deterministic file selection,
     * for example in verification or when the application is launched elsewhere.
     */
    public EmployeeFileHelper(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee file path cannot be empty.");
        }
        this.configuredPath = Paths.get(filePath);
    }

    private Path resolveDataPath() {
        if (configuredPath != null) {
            return configuredPath;
        }
        String[] candidates = {
            DEFAULT_FILE_NAME,
            "src/file/employees.txt",
            "file/employees.txt"
        };
        for (String candidate : candidates) {
            Path path = Paths.get(candidate);
            if (Files.exists(path)) {
                return path;
            }
        }
        return Paths.get(DEFAULT_FILE_NAME);
    }

    /**
     * Loads valid rows and skips malformed or duplicated rows with a warning.
     * A missing file is treated as a failed load instead of an empty successful load.
     */
    @Override
    public List<Employee> read() throws Exception {
        List<Employee> employees = new ArrayList<Employee>();
        Path path = resolveDataPath();
        if (!Files.exists(path)) {
            throw new IOException("Data file not found: " + path.toString());
        }

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                try {
                    Employee employee = parseEmployeeLine(line, employees);
                    employees.add(employee);
                } catch (Exception exception) {
                    String warning = "Line " + lineNumber + " skipped. Reason: " + exception.getMessage();
                    System.out.println("[Warning] " + warning);
                    LogUtils.logWarning(warning + " | Raw data: " + line);
                }
            }
        }
        return employees;
    }

    private Employee parseEmployeeLine(String line, List<Employee> currentList) throws Exception {
        List<String> fields = CsvUtils.parseLine(line);
        if (fields.size() != 7) {
            throw new Exception("Wrong field count (" + fields.size() + "/7 fields)");
        }

        String code = fields.get(0).trim().toUpperCase();
        String name = fields.get(1).trim();
        Role role = EmployeeValidator.parseRole(fields.get(2).trim());
        double baseSalary = parseDoubleField(fields.get(3).trim(), "Base Salary");
        int workingDays = parseIntField(fields.get(4).trim(), "Working Days");
        double bonus = parseDoubleField(fields.get(5).trim(), "Bonus");
        EmployeeStatus status = EmployeeValidator.parseStatus(fields.get(6).trim());

        Employee employee = new Employee(code, name, role, baseSalary, workingDays, bonus, status);
        EmployeeValidator.validateNewEmployee(employee, currentList);
        return employee;
    }

    private double parseDoubleField(String value, String fieldName) throws Exception {
        try {
            double number = Double.parseDouble(value);
            if (!Double.isFinite(number)) {
                throw new NumberFormatException("Non-finite number");
            }
            return number;
        } catch (NumberFormatException ignored) {
            throw new Exception("Invalid " + fieldName + " '" + value + "'");
        }
    }

    private int parseIntField(String value, String fieldName) throws Exception {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            throw new Exception("Invalid " + fieldName + " '" + value + "'");
        }
    }

    /** Validates all records first, then atomically replaces the target when supported. */
    @Override
    public boolean write(List<Employee> employees) throws Exception {
        validateList(employees);

        Path target = resolveDataPath();
        Path temp = createTempSibling(target);
        try {
            try (BufferedWriter writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
                for (Employee employee : employees) {
                    writer.write(convertToString(employee));
                    writer.newLine();
                }
            }
            replaceTarget(temp, target);
            return true;
        } catch (IOException exception) {
            LogUtils.logError("Cannot write employees to file " + target.toString(), exception);
            deleteQuietly(temp);
            return false;
        }
    }

    private void validateList(List<Employee> employees) throws Exception {
        if (employees == null) {
            throw new Exception("Employee list cannot be null.");
        }
        List<Employee> checked = new ArrayList<Employee>();
        for (Employee employee : employees) {
            EmployeeValidator.validateNewEmployee(employee, checked);
            checked.add(employee);
        }
    }

    private String convertToString(Employee employee) {
        return String.format("%s, %s, %s, %s, %d, %s, %s",
                CsvUtils.escape(employee.getCode()), CsvUtils.escape(employee.getName()),
                CsvUtils.escape(employee.getRole().getDisplayName()),
                formatNumber(employee.getBaseSalary()), employee.getWorkingDays(),
                formatNumber(employee.getBonus()), CsvUtils.escape(employee.getStatus().getDisplayName()));
    }

    private String formatNumber(double value) {
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }

    private Path createTempSibling(Path target) throws IOException {
        Path absoluteTarget = target.toAbsolutePath();
        Path parent = absoluteTarget.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
            return Files.createTempFile(parent, "employees-", ".tmp");
        }
        return Files.createTempFile("employees-", ".tmp");
    }

    private void replaceTarget(Path temp, Path target) throws IOException {
        try {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ignored) {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void deleteQuietly(Path path) {
        if (path != null) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException exception) {
                LogUtils.logError("Cannot delete temporary employee file", exception);
            }
        }
    }
}
