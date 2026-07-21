package manager;

import java.util.List;
import model.Employee;

/** Business contract for employee management and payroll operations. */
public interface IEmployeeManager {
    List<Employee> getAllEmployees();
    List<Employee> getActiveEmployees();
    Employee getEmployeeById(String employeeId);
    boolean containsEmployeeId(String employeeId);
    void addEmployee(Employee employee) throws Exception;
    boolean updateEmployee(Employee updatedEmployee);
    boolean removeEmployee(String employeeId);
    List<Employee> searchByAttribute(String attribute, String query);
    double getTotalSalaryByMonth();
    boolean loadFromFile();
    boolean saveToFile();
}
