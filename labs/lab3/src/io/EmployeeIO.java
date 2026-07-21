package io;

import manager.IEmployeeManager;
import model.Employee;
import model.EmployeeStatus;
import model.Role;
import validation.EmployeeValidator;
import view.View;

/** Reads and validates employee input from the console. */
public class EmployeeIO {

    private final View view;

    public EmployeeIO(View view) {
        if (view == null) {
            throw new IllegalArgumentException("View cannot be null.");
        }
        this.view = view;
    }

    public Employee inputNewEmployee(IEmployeeManager manager) {
        if (manager == null) {
            throw new IllegalArgumentException("Employee manager cannot be null.");
        }
        String code = inputNewEmployeeId(manager);
        String name = inputEmployeeName();
        Role role = inputRole("Enter Role (Developer, Tester, Manager, HR): ");
        double baseSalary = inputBaseSalary("Enter Base Salary (>0): ");
        int workingDays = inputWorkingDays("Enter Working Days (0-26): ");
        double bonus = inputBonus("Enter Bonus (>=0): ");
        EmployeeStatus status = inputStatus("Enter Status (active/inactive): ");
        return new Employee(code, name, role, baseSalary, workingDays, bonus, status);
    }

    /** Blank update fields retain their current values. */
    public Employee inputUpdatedEmployee(Employee currentEmployee) {
        if (currentEmployee == null) {
            throw new IllegalArgumentException("Current employee cannot be null.");
        }
        System.out.println("Leave input blank to keep the current value.");
        Role role = inputRoleAllowEmpty(currentEmployee.getRole());
        double baseSalary = inputBaseSalaryAllowEmpty(currentEmployee.getBaseSalary());
        double bonus = inputBonusAllowEmpty(currentEmployee.getBonus());
        EmployeeStatus status = inputStatusAllowEmpty(currentEmployee.getStatus());
        return new Employee(currentEmployee.getCode(), currentEmployee.getName(), role,
                baseSalary, currentEmployee.getWorkingDays(), bonus, status);
    }

    public String inputExistingEmployeeId(String prompt) {
        while (true) {
            String code = view.readString(prompt).toUpperCase();
            try {
                EmployeeValidator.validateIdFormat(code);
                return code;
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    public String inputSearchAttribute(int choice) {
        switch (choice) {
            case 1:
                return "id";
            case 2:
                return "name";
            case 3:
                return "role";
            case 4:
                return "status";
            default:
                return null;
        }
    }

    private String inputNewEmployeeId(IEmployeeManager manager) {
        while (true) {
            String code = view.readString("Enter Employee ID (E001): ").toUpperCase();
            try {
                EmployeeValidator.validateIdFormat(code);
                if (manager.containsEmployeeId(code)) {
                    System.out.println("Employee ID already exists. Please enter another ID.");
                } else {
                    return code;
                }
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    private String inputEmployeeName() {
        while (true) {
            String name = view.readString("Enter Employee Name: ");
            try {
                EmployeeValidator.validateName(name);
                return name;
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    private Role inputRole(String prompt) {
        while (true) {
            try {
                return EmployeeValidator.parseRole(view.readString(prompt));
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    private Role inputRoleAllowEmpty(Role oldValue) {
        while (true) {
            String text = view.readStringAllowEmpty("Enter New Role (" + oldValue.getDisplayName() + "): ");
            if (text.isEmpty()) {
                return oldValue;
            }
            try {
                return EmployeeValidator.parseRole(text);
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    private double inputBaseSalary(String prompt) {
        while (true) {
            double value = view.readDouble(prompt);
            try {
                EmployeeValidator.validateBaseSalary(value);
                return value;
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    private double inputBaseSalaryAllowEmpty(double oldValue) {
        while (true) {
            double value = view.readDouble("Enter New Base Salary (" + String.format("%.2f", oldValue) + "): ", oldValue);
            try {
                EmployeeValidator.validateBaseSalary(value);
                return value;
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    private int inputWorkingDays(String prompt) {
        while (true) {
            int value = view.readInt(prompt);
            try {
                EmployeeValidator.validateWorkingDays(value);
                return value;
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    private double inputBonus(String prompt) {
        while (true) {
            double value = view.readDouble(prompt);
            try {
                EmployeeValidator.validateBonus(value);
                return value;
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    private double inputBonusAllowEmpty(double oldValue) {
        while (true) {
            double value = view.readDouble("Enter New Bonus (" + String.format("%.2f", oldValue) + "): ", oldValue);
            try {
                EmployeeValidator.validateBonus(value);
                return value;
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    private EmployeeStatus inputStatus(String prompt) {
        while (true) {
            try {
                return EmployeeValidator.parseStatus(view.readString(prompt));
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    private EmployeeStatus inputStatusAllowEmpty(EmployeeStatus oldValue) {
        while (true) {
            String text = view.readStringAllowEmpty("Enter New Status (" + oldValue.getDisplayName() + "): ");
            if (text.isEmpty()) {
                return oldValue;
            }
            try {
                return EmployeeValidator.parseStatus(text);
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }
}
