package utilities;

import java.util.Scanner;

/** Provides console input helpers through one shared Scanner. */
public class DataInput {

    private static final Scanner SCANNER = new Scanner(System.in);

    public static int getIntegerNumber(String displayMessage) throws Exception {
        System.out.print(displayMessage);
        return getIntegerNumber();
    }

    public static int getIntegerNumber() throws Exception {
        String input = SCANNER.nextLine().trim();
        if (!DataValidation.checkStringWithFormat(input, "\\d{1,10}")) {
            throw new Exception("Data invalid.");
        }
        return Integer.parseInt(input);
    }

    public static String getString(String displayMessage) {
        System.out.print(displayMessage);
        return getString();
    }

    public static String getString() {
        return SCANNER.nextLine();
    }

    
    public static String getRequiredString(String displayMessage, String errorMessage) {
        while (true) {
            String value = getString(displayMessage);
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
            System.out.println(errorMessage);
        }
    }

    public static double getDoubleAllowDefault(String displayMessage, double defaultValue) {
        while (true) {
            System.out.print(displayMessage);
            String input = SCANNER.nextLine().trim();
            if (input.isEmpty()) {
                return defaultValue;
            }
            try {
                double value = Double.parseDouble(input);
                if (Double.isFinite(value) && value > 0) {
                    return value;
                }
                System.out.println("Fee must be greater than 0.");
            } catch (NumberFormatException ignored) {
                System.out.println("Invalid fee. Please enter a number.");
            }
        }
    }

    public static Double getOptionalPositiveDouble(String displayMessage) {
        while (true) {
            System.out.print(displayMessage);
            String input = SCANNER.nextLine().trim();
            if (input.isEmpty()) {
                return null;
            }
            try {
                double value = Double.parseDouble(input);
                if (Double.isFinite(value) && value > 0) {
                    return value;
                }
                System.out.println("Fee must be greater than 0.");
            } catch (NumberFormatException ignored) {
                System.out.println("Invalid fee. Please enter a number.");
            }
        }
    }

    public static boolean getYesNo(String displayMessage) {
        while (true) {
            String answer = getString(displayMessage).trim();
            if (answer.equalsIgnoreCase("Y")) {
                return true;
            }
            if (answer.equalsIgnoreCase("N")) {
                return false;
            }
            System.out.println("Please enter Y or N.");
        }
    }
}
