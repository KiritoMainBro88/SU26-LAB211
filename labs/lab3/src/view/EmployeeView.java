package view;

import java.util.List;
import java.util.Locale;
import manager.IEmployeeManager;
import model.Employee;

/** Presents employee and payroll data in fixed-width tables. */
public class EmployeeView {

    private final IEmployeeManager employeeManager;

    public EmployeeView(IEmployeeManager employeeManager) {
        if (employeeManager == null) {
            throw new IllegalArgumentException("Employee manager cannot be null.");
        }
        this.employeeManager = employeeManager;
    }

    public void displayAllEmployees() {
        displayEmployees(employeeManager.getAllEmployees());
    }

    public void displayEmployees(List<Employee> employees) {
        if (employees == null || employees.isEmpty()) {
            System.out.println("No employee found.");
            return;
        }
        System.out.println("+-------------+----------------------+------------+-------------+--------------+------------+----------+");
        System.out.println("| Employee ID | Name                 | Role       | Base Salary | Working Days | Bonus      | Status   |");
        System.out.println("+-------------+----------------------+------------+-------------+--------------+------------+----------+");
        for (Employee employee : employees) {
            System.out.printf("| %-11s | %-20s | %-10s | %11s | %12d | %10s | %-8s |%n",
                    employee.getCode(), shorten(employee.getName(), 20),
                    employee.getRole().getDisplayName(), formatNumber(employee.getBaseSalary(), 11),
                    employee.getWorkingDays(), formatNumber(employee.getBonus(), 10),
                    employee.getStatus().getDisplayName());
        }
        System.out.println("+-------------+----------------------+------------+-------------+--------------+------------+----------+");
    }

    public void displayPayrollTable(List<Employee> activeEmployees, double grandTotal) {
        if (activeEmployees == null || activeEmployees.isEmpty()) {
            System.out.println("No active employee found for payroll calculation.");
            return;
        }
        System.out.println("+-------------+----------------------+------------+-------------+--------------+------------+--------------+");
        System.out.println("| Employee ID | Name                 | Role       | Base Salary | Working Days | Bonus      | Total Salary |");
        System.out.println("+-------------+----------------------+------------+-------------+--------------+------------+--------------+");
        for (Employee employee : activeEmployees) {
            System.out.printf("| %-11s | %-20s | %-10s | %11s | %12d | %10s | %12s |%n",
                    employee.getCode(), shorten(employee.getName(), 20),
                    employee.getRole().getDisplayName(), formatNumber(employee.getBaseSalary(), 11),
                    employee.getWorkingDays(), formatNumber(employee.getBonus(), 10),
                    formatNumber(employee.calculateSalary(), 12));
        }
        System.out.println("+-------------+----------------------+------------+-------------+--------------+------------+--------------+");
        System.out.printf("| GRAND TOTAL PAYROLL:                                                              | %12s |%n",
                formatNumber(grandTotal, 12));
        System.out.println("+-------------+----------------------+------------+-------------+--------------+------------+--------------+");
    }


    private String formatNumber(double value, int width) {
        String fixed = String.format(Locale.ROOT, "%.2f", value);
        if (fixed.length() <= width) {
            return fixed;
        }
        return String.format(Locale.ROOT, "%.1e", value);
    }

    private String shorten(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
}
