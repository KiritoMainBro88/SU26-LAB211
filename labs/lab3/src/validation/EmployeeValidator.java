package validation;

import java.util.List;
import model.Employee;
import model.EmployeeStatus;
import model.Role;

/** Central validation rules for Employee data. */
public final class EmployeeValidator {

    private static final String ID_PATTERN = "E\\d{3}";

    private EmployeeValidator() {
    }

    public static boolean isValidIdFormat(String code) {
        return code != null && code.trim().toUpperCase().matches(ID_PATTERN);
    }

    public static void validateIdFormat(String code) throws Exception {
        if (!isValidIdFormat(code)) {
            throw new Exception("Employee ID must be E followed by 3 digits, for example E001.");
        }
    }

    public static void validateName(String name) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Employee name cannot be empty.");
        }
    }

    public static Role parseRole(String roleText) throws Exception {
        Role role = Role.parse(roleText);
        if (role == null) {
            throw new Exception("Role must be Developer, Tester, Manager or HR.");
        }
        return role;
    }

    public static void validateBaseSalary(double baseSalary) throws Exception {
        if (!Double.isFinite(baseSalary) || baseSalary <= 0) {
            throw new Exception("Base salary must be a positive number.");
        }
    }

    public static void validateWorkingDays(int workingDays) throws Exception {
        if (workingDays < 0 || workingDays > 26) {
            throw new Exception("Working days must be from 0 to 26.");
        }
    }

    public static void validateBonus(double bonus) throws Exception {
        if (!Double.isFinite(bonus) || bonus < 0) {
            throw new Exception("Bonus must be greater than or equal to 0.");
        }
    }

    public static EmployeeStatus parseStatus(String statusText) throws Exception {
        EmployeeStatus status = EmployeeStatus.parse(statusText);
        if (status == null) {
            throw new Exception("Status must be active or inactive.");
        }
        return status;
    }

    public static boolean isDuplicateId(String code, List<Employee> employees) {
        if (code == null || employees == null) {
            return false;
        }
        for (Employee employee : employees) {
            if (employee != null && employee.getCode() != null
                    && employee.getCode().equalsIgnoreCase(code.trim())) {
                return true;
            }
        }
        return false;
    }

    public static void validateEmployee(Employee employee) throws Exception {
        if (employee == null) {
            throw new Exception("Employee object cannot be null.");
        }
        validateIdFormat(employee.getCode());
        validateName(employee.getName());
        if (employee.getRole() == null) {
            throw new Exception("Employee role is invalid.");
        }
        validateBaseSalary(employee.getBaseSalary());
        validateWorkingDays(employee.getWorkingDays());
        validateBonus(employee.getBonus());
        if (employee.getStatus() == null) {
            throw new Exception("Employee status is invalid.");
        }
    }

    public static void validateNewEmployee(Employee employee, List<Employee> employees) throws Exception {
        if (employees == null) {
            throw new Exception("Employee list cannot be null.");
        }
        validateEmployee(employee);
        if (isDuplicateId(employee.getCode(), employees)) {
            throw new Exception("Duplicate Employee ID '" + employee.getCode() + "'.");
        }
    }
}
