package util;

import java.util.Scanner;
import validation.ValidationUtils;

/** Console input helpers used by the menu IO classes. */
public final class InputUtils {

    private InputUtils() {
    }

    public static String readLine(Scanner scanner, String message) {
        System.out.print(message);
        if (!scanner.hasNextLine()) {
            System.out.println("\nConsole input closed. Exiting...");
            System.exit(0);
        }
        return scanner.nextLine().trim();
    }

    public static String readRequired(Scanner scanner, String message) {
        while (true) {
            String value = readLine(scanner, message);
            if (!ValidationUtils.isBlank(value)) {
                return value;
            }
            System.out.println("Input cannot be empty!");
        }
    }

    public static int readMenuChoice(Scanner scanner, int minChoice, int maxChoice) {
        while (true) {
            String value = readLine(scanner, "Enter your choice: ");
            try {
                int choice = Integer.parseInt(value);
                if (choice >= minChoice && choice <= maxChoice) {
                    return choice;
                }
            } catch (NumberFormatException ignored) {
                // Invalid numeric input is handled by the common message below.
            }
            System.out.println("Invalid choice! Please enter a number from "
                    + minChoice + " to " + maxChoice + ".");
        }
    }
}
