package view;

import java.util.Scanner;

/** Console view and reusable input methods. */
public class View {

    private final Scanner scanner;

    public View(Scanner scanner) {
        if (scanner == null) {
            throw new IllegalArgumentException("Scanner cannot be null.");
        }
        this.scanner = scanner;
    }

    public int showMainMenu() {
        while (true) {
            System.out.println("\n========== EMPLOYEE PAYROLL MANAGEMENT SYSTEM ==========");
            String[] options = {
                "Load employee data from file",
                "Add a new employee",
                "Update employee information",
                "Remove an employee by ID",
                "Search employees by attribute",
                "Calculate monthly payroll",
                "Display employee list",
                "Save data to file",
                "Quit program"
            };
            Menu.showMenu(options);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= 1 && value <= 9) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // The common validation message below handles non-numeric input.
            }
            System.out.println("Invalid choice! Please choose a number from 1 to 9.");
        }
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // The common validation message below handles non-numeric input.
            }
            System.out.println("Invalid number, try again.");
        }
    }

    public int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException ignored) {
                System.out.println("Invalid number, try again.");
            }
        }
    }

    public double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException ignored) {
                System.out.println("Invalid number, try again.");
            }
        }
    }

    public double readDouble(String prompt, double oldValue) {
        while (true) {
            try {
                System.out.print(prompt);
                String line = scanner.nextLine().trim();
                return line.isEmpty() ? oldValue : Double.parseDouble(line);
            } catch (NumberFormatException ignored) {
                System.out.println("Invalid number, try again.");
            }
        }
    }

    public String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("Input cannot be empty! Please try again.");
        }
    }

    public String readStringAllowEmpty(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if ("y".equalsIgnoreCase(value)) {
                return true;
            }
            if ("n".equalsIgnoreCase(value)) {
                return false;
            }
            System.out.println("Please enter Y or N.");
        }
    }

    public void showMessage(String message) {
        System.out.println(message);
    }
}
