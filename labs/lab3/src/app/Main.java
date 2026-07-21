package app;

import io.EmployeeIO;
import java.util.List;
import java.util.Scanner;
import manager.EmployeeManager;
import manager.IEmployeeManager;
import model.Employee;
import utils.LogUtils;
import view.EmployeeView;
import view.View;

/** Entry point and menu coordinator for J1.L.P0037. */
public class Main {

    private final View view;
    private final EmployeeIO employeeIO;
    private final IEmployeeManager employeeManager;
    private final EmployeeView employeeView;
    private boolean changed;

    public Main() {
        view = new View(new Scanner(System.in));
        employeeManager = new EmployeeManager();
        employeeView = new EmployeeView(employeeManager);
        employeeIO = new EmployeeIO(view);
        changed = false;
    }

    public static void main(String[] args) {
        Main application = new Main();
        application.loadInitialData();
        application.runMenuLoop();
    }

    private void runMenuLoop() {
        boolean running = true;
        while (running) {
            try {
                int choice = view.showMainMenu();
                switch (choice) {
                    case 1:
                        manualLoadData();
                        break;
                    case 2:
                        addNewEmployee();
                        break;
                    case 3:
                        updateEmployee();
                        break;
                    case 4:
                        removeEmployee();
                        break;
                    case 5:
                        searchEmployee();
                        break;
                    case 6:
                        calculatePayroll();
                        break;
                    case 7:
                        employeeView.displayAllEmployees();
                        break;
                    case 8:
                        saveData();
                        break;
                    case 9:
                        running = !quitProgram();
                        break;
                    default:
                        view.showMessage("This function is not available.");
                        break;
                }
            } catch (Exception exception) {
                LogUtils.logError("Unexpected menu error", exception);
                view.showMessage("An error occurred. Please try again.");
            }
        }
    }

    private void loadInitialData() {
        if (employeeManager.loadFromFile()) {
            System.out.println("Data loaded successfully on startup.");
        } else {
            System.out.println("Initial data could not be loaded.");
        }
    }

    private void manualLoadData() {
        System.out.println("Reloading data from file...");
        if (employeeManager.loadFromFile()) {
            changed = false;
            System.out.println("Data reloaded successfully!");
        } else {
            System.out.println("Failed to reload data. Current data was preserved.");
        }
    }

    private void addNewEmployee() {
        System.out.println("\n--- Add a New Employee ---");
        try {
            Employee employee = employeeIO.inputNewEmployee(employeeManager);
            employeeManager.addEmployee(employee);
            changed = true;
            System.out.println("Employee added successfully!");
        } catch (Exception exception) {
            LogUtils.logError("Add employee failed", exception);
            System.out.println("Add failed: " + exception.getMessage());
        }
    }

    private void updateEmployee() {
        System.out.println("\n--- Update Employee Information ---");
        String code = employeeIO.inputExistingEmployeeId("Enter Employee ID to update: ");
        Employee employee = employeeManager.getEmployeeById(code);
        if (employee == null) {
            System.out.println("Employee ID " + code + " not found!");
            return;
        }

        System.out.println("Current info: " + employee);
        Employee updatedEmployee = employeeIO.inputUpdatedEmployee(employee);
        if (employeeManager.updateEmployee(updatedEmployee)) {
            changed = true;
            System.out.println("Employee updated successfully!");
        } else {
            System.out.println("Update failed.");
        }
    }

    private void removeEmployee() {
        System.out.println("\n--- Remove an Employee ---");
        String code = employeeIO.inputExistingEmployeeId("Enter Employee ID to remove: ");
        Employee employee = employeeManager.getEmployeeById(code);
        if (employee == null) {
            System.out.println("Employee ID " + code + " not found!");
            return;
        }

        if (!employee.isActive()) {
            System.out.println("Employee ID " + code + " is already inactive.");
            return;
        }

        System.out.println("Found: " + employee);
        if (!view.readYesNo("Set this employee to inactive? (Y/N): ")) {
            System.out.println("Removal cancelled.");
            return;
        }
        if (employeeManager.removeEmployee(code)) {
            changed = true;
            System.out.println("Employee removed successfully (status set to inactive)!");
        } else {
            System.out.println("Failed to remove employee.");
        }
    }

    private void searchEmployee() {
        System.out.println("\n--- Search Employee by Attribute ---");
        System.out.println("1. Search by ID");
        System.out.println("2. Search by Name");
        System.out.println("3. Search by Role");
        System.out.println("4. Search by Status");
        int choice = view.readInt("Enter search option: ", 1, 4);
        String attribute = employeeIO.inputSearchAttribute(choice);
        String query = view.readString("Enter search keyword: ");
        List<Employee> results = employeeManager.searchByAttribute(attribute, query);
        System.out.println("\nSearch Results:");
        employeeView.displayEmployees(results);
    }

    private void calculatePayroll() {
        System.out.println("\n--- Calculate Monthly Payroll (Active Employees) ---");
        List<Employee> activeEmployees = employeeManager.getActiveEmployees();
        double grandTotal = employeeManager.getTotalSalaryByMonth();
        employeeView.displayPayrollTable(activeEmployees, grandTotal);
    }

    private boolean saveData() {
        System.out.println("Saving data to file...");
        if (employeeManager.saveToFile()) {
            changed = false;
            System.out.println("Save data completed successfully!");
            return true;
        }
        System.out.println("Failed to save data.");
        return false;
    }

    /** Returns true only when exiting will not discard an attempted save. */
    private boolean quitProgram() {
        if (changed) {
            System.out.println("You have unsaved changes.");
            boolean saveBeforeExit = view.readYesNo("Do you want to save before exiting? (Y/N): ");
            if (saveBeforeExit && !saveData()) {
                System.out.println("Exit cancelled because data could not be saved.");
                return false;
            }
        }
        System.out.println("Goodbye!");
        return true;
    }
}
