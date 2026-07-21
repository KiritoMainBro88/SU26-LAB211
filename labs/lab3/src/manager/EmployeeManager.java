package manager;

import file.EmployeeFileHelper;
import file.IFileReadWrite;
import java.util.ArrayList;
import java.util.List;
import model.Employee;
import model.EmployeeStatus;
import model.PayrollCalculable;
import utils.LogUtils;
import validation.EmployeeValidator;

/** Business layer for employee CRUD, searching, payroll, and persistence. */
public class EmployeeManager implements IEmployeeManager {

    private final List<Employee> employeeList;
    private final IFileReadWrite<Employee> fileHelper;

    public EmployeeManager() {
        this(new EmployeeFileHelper());
    }

    public EmployeeManager(IFileReadWrite<Employee> fileHelper) {
        if (fileHelper == null) {
            throw new IllegalArgumentException("File helper cannot be null.");
        }
        this.fileHelper = fileHelper;
        this.employeeList = new ArrayList<Employee>();
    }

    @Override
    public List<Employee> getAllEmployees() {
        return copyEmployees(employeeList);
    }

    @Override
    public List<Employee> getActiveEmployees() {
        List<Employee> result = new ArrayList<Employee>();
        for (Employee employee : employeeList) {
            if (employee.isActive()) {
                result.add(copyEmployee(employee));
            }
        }
        return result;
    }

    @Override
    public Employee getEmployeeById(String employeeId) {
        Employee employee = findByCodeInternal(employeeId);
        return employee == null ? null : copyEmployee(employee);
    }

    @Override
    public boolean containsEmployeeId(String employeeId) {
        return findByCodeInternal(employeeId) != null;
    }

    @Override
    public void addEmployee(Employee employee) throws Exception {
        EmployeeValidator.validateNewEmployee(employee, employeeList);
        employeeList.add(copyEmployee(employee));
    }

    @Override
    public boolean updateEmployee(Employee updatedEmployee) {
        if (updatedEmployee == null || updatedEmployee.getCode() == null) {
            return false;
        }
        Employee currentEmployee = findByCodeInternal(updatedEmployee.getCode());
        if (currentEmployee == null) {
            return false;
        }
        try {
            Employee candidate = new Employee(currentEmployee.getCode(), currentEmployee.getName(),
                    updatedEmployee.getRole(), updatedEmployee.getBaseSalary(),
                    currentEmployee.getWorkingDays(), updatedEmployee.getBonus(),
                    updatedEmployee.getStatus());
            EmployeeValidator.validateEmployee(candidate);
            currentEmployee.setRole(candidate.getRole());
            currentEmployee.setBaseSalary(candidate.getBaseSalary());
            currentEmployee.setBonus(candidate.getBonus());
            currentEmployee.setStatus(candidate.getStatus());
            return true;
        } catch (Exception exception) {
            LogUtils.logError("Invalid employee update for ID " + updatedEmployee.getCode(), exception);
            return false;
        }
    }

    /**
     * Performs a soft delete by changing an active employee
     * to inactive while retaining the record for history and search.
     */
    @Override
    public boolean removeEmployee(String employeeId) {
        Employee employee = findByCodeInternal(employeeId);
        if (employee == null || !employee.isActive()) {
            return false;
        }
        employee.setStatus(EmployeeStatus.INACTIVE);
        return true;
    }

    @Override
    public List<Employee> searchByAttribute(String attribute, String query) {
        List<Employee> result = new ArrayList<Employee>();
        if (attribute == null || query == null) {
            return result;
        }
        String cleanQuery = query.trim().toLowerCase(java.util.Locale.ROOT);
        String cleanAttribute = attribute.trim().toLowerCase(java.util.Locale.ROOT);
        if (cleanQuery.isEmpty()) {
            return result;
        }
        for (Employee employee : employeeList) {
            if (isMatched(employee, cleanAttribute, cleanQuery)) {
                result.add(copyEmployee(employee));
            }
        }
        return result;
    }

    private boolean isMatched(Employee employee, String attribute, String query) {
        String codeText = employee.getCode().toLowerCase(java.util.Locale.ROOT);
        String nameText = employee.getName().toLowerCase(java.util.Locale.ROOT);
        String roleText = employee.getRole().getDisplayName().toLowerCase(java.util.Locale.ROOT);
        String statusText = employee.getStatus().getDisplayName().toLowerCase(java.util.Locale.ROOT);
        switch (attribute) {
            case "id":
            case "code":
                return codeText.equals(query);
            case "name":
                return nameText.contains(query);
            case "role":
                return roleText.equals(query);
            case "status":
                return statusText.equals(query);
            default:
                return false;
        }
    }

    @Override
    public double getTotalSalaryByMonth() {
        double total = 0;
        for (Employee employee : employeeList) {
            if (employee.isActive()) {
                PayrollCalculable payrollItem = employee;
                total += payrollItem.calculateSalary();
            }
        }
        return total;
    }

    @Override
    public boolean saveToFile() {
        try {
            return fileHelper.write(getAllEmployees());
        } catch (Exception exception) {
            LogUtils.logError("Cannot save employee data to file", exception);
            return false;
        }
    }

    /** Replaces in-memory data only after the complete read and validation operation succeeds. */
    @Override
    public boolean loadFromFile() {
        try {
            List<Employee> loaded = fileHelper.read();
            if (loaded == null) {
                return false;
            }
            List<Employee> validated = new ArrayList<Employee>();
            for (Employee employee : loaded) {
                EmployeeValidator.validateNewEmployee(employee, validated);
                validated.add(copyEmployee(employee));
            }
            employeeList.clear();
            employeeList.addAll(validated);
            return true;
        } catch (Exception exception) {
            LogUtils.logError("Cannot load employee data from file", exception);
            return false;
        }
    }

    private Employee findByCodeInternal(String code) {
        if (code == null) {
            return null;
        }
        String normalizedCode = code.trim();
        for (Employee employee : employeeList) {
            if (employee.getCode().equalsIgnoreCase(normalizedCode)) {
                return employee;
            }
        }
        return null;
    }

    private List<Employee> copyEmployees(List<Employee> source) {
        List<Employee> result = new ArrayList<Employee>();
        for (Employee employee : source) {
            result.add(copyEmployee(employee));
        }
        return result;
    }

    private Employee copyEmployee(Employee employee) {
        return new Employee(employee);
    }
}
