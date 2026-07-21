package presentation;

import businessobject.StudentManagement;
import core.entities.Mountain;
import core.entities.StatisticalInfo;
import core.entities.Student;
import utilities.DataInput;
import utilities.DataValidation;
import java.text.DecimalFormat;
import java.util.List;

/** Handles console interaction for the nine Mountain Hiking functions while delegating business rules. */
public class Menu {

    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0");

    
    public static int getUserChoice() {
        try {
            return DataInput.getIntegerNumber();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public static void manageStudentRegistration(StudentManagement management) {
        boolean stop = false;
        while (!stop) {
            printMainMenu();
            int choice = getUserChoice();
            switch (choice) {
                case 1:
                    newRegistration(management);
                    break;
                case 2:
                    updateRegistration(management);
                    break;
                case 3:
                    displayRegisteredList(management);
                    break;
                case 4:
                    deleteRegistration(management);
                    break;
                case 5:
                    searchParticipantsByName(management);
                    break;
                case 6:
                    filterDataByCampus(management);
                    break;
                case 7:
                    displayStatisticsByMountain(management);
                    break;
                case 8:
                    saveData(management);
                    break;
                case 9:
                    stop = exitProgram(management);
                    break;
                default:
                    System.out.println("Invalid choice. Please choose from 1 to 9.");
                    break;
            }
        }
    }

    private static void printMainMenu() {
        System.out.println("\n========== Mountain Hiking Challenge Registration ==========");
        System.out.println("1. New Registration");
        System.out.println("2. Update Registration Information");
        System.out.println("3. Display Registered List");
        System.out.println("4. Delete Registration Information");
        System.out.println("5. Search Participants by Name");
        System.out.println("6. Filter Data by Campus");
        System.out.println("7. Statistics by Mountain Peak");
        System.out.println("8. Save Data to File");
        System.out.println("9. Exit Program");
        System.out.print("Select an option: ");
    }

    private static void newRegistration(StudentManagement management) {
        System.out.println("\n--- New Registration ---");
        String id = inputNewStudentId(management);
        String name = inputValidName("Name: ");
        String phone = inputValidPhone("Phone: ");
        String email = inputValidEmail("Email: ");
        printMountainList(management);
        String mountainCode = inputValidMountainCode(management, "Mountain code: ");
        double baseFee = DataInput.getDoubleAllowDefault(
                "Base tuition fee (Enter for 6,000,000): ",
                StudentManagement.DEFAULT_TUITION_FEE);

        management.createStudent(id, name, phone, email, mountainCode, baseFee);
        System.out.println(management.getLastMessage());
    }

    private static void updateRegistration(StudentManagement management) {
        System.out.println("\n--- Update Registration Information ---");
        String id = DataInput.getRequiredString("Enter Student ID to update: ",
                "Student ID cannot be empty. Please re-enter.");
        Student current = management.getStudentById(id);
        if (current == null) {
            System.out.println("This student has not registered yet.");
            return;
        }

        System.out.println("Current information:");
        printStudentDetail(current);
        System.out.println("Leave a field blank to keep the old value.");

        String name = inputOptionalValidName("New name: ");
        String phone = inputOptionalValidPhone("New phone: ");
        String email = inputOptionalValidEmail("New email: ");
        printMountainList(management);
        String mountainCode = inputOptionalValidMountainCode(management, "New mountain code: ");
        Double baseFee = DataInput.getOptionalPositiveDouble(
                "New base tuition fee (blank to keep/recalculate automatically): ");

        management.updateStudent(id, name, phone, email, mountainCode, baseFee);
        System.out.println(management.getLastMessage());
    }

    private static void displayRegisteredList(StudentManagement management) {
        System.out.println("\n--- Registered List ---");
        List<Student> students = management.getAllStudents();
        if (students.isEmpty()) {
            System.out.println("No students have registered yet.");
            return;
        }
        printStudentTable(students);
    }

    private static void deleteRegistration(StudentManagement management) {
        System.out.println("\n--- Delete Registration Information ---");
        String id = DataInput.getRequiredString("Enter Student ID to delete: ",
                "Student ID cannot be empty. Please re-enter.");
        Student student = management.getStudentById(id);
        if (student == null) {
            System.out.println("This student has not registered yet.");
            return;
        }

        printStudentDetail(student);
        boolean confirm = DataInput.getYesNo("Are you sure you want to delete this registration? (Y/N): ");
        if (!confirm) {
            System.out.println("Delete cancelled.");
            return;
        }
        management.deleteStudent(id);
        System.out.println(management.getLastMessage());
    }

    private static void searchParticipantsByName(StudentManagement management) {
        System.out.println("\n--- Search Participants by Name ---");
        String keyword = DataInput.getRequiredString("Enter name or part of name: ",
                "Search keyword cannot be empty. Please re-enter.");
        List<Student> result = management.searchByName(keyword);
        if (result.isEmpty()) {
            System.out.println("No one matches the search criteria.");
            return;
        }
        printStudentTable(result);
    }

    private static void filterDataByCampus(StudentManagement management) {
        System.out.println("\n--- Filter Data by Campus ---");
        String campusCode = inputValidCampusCode();
        List<Student> result = management.filterByCampus(campusCode);
        if (result.isEmpty()) {
            System.out.println("No students have registered under this campus.");
            return;
        }
        printStudentTable(result);
    }

    private static void displayStatisticsByMountain(StudentManagement management) {
        System.out.println("\n--- Statistics by Mountain Peak ---");
        List<StatisticalInfo> statistics = management.getStatisticsByMountain();
        if (statistics.isEmpty()) {
            System.out.println("No registration data available for statistics.");
            return;
        }
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-12s | %-25s | %-12s | %-15s%n",
                "Code", "Mountain", "Participants", "Total Cost");
        System.out.println("--------------------------------------------------------------------------------");
        for (StatisticalInfo info : statistics) {
            System.out.printf("%-12s | %-25s | %-12d | %15s%n",
                    info.getMountainCode(),
                    management.getMountainNameByCode(info.getMountainCode()),
                    info.getNumberOfStudents(),
                    MONEY_FORMAT.format(info.getTotalCost()));
        }
        System.out.println("--------------------------------------------------------------------------------");
    }

    private static void saveData(StudentManagement management) {
        management.saveData();
        System.out.println(management.getLastMessage());
    }

    private static boolean exitProgram(StudentManagement management) {
        if (!management.isSaved()) {
            boolean save = DataInput.getYesNo("Data has changed. Do you want to save before exiting? (Y/N): ");
            if (save) {
                boolean saved = management.saveData();
                System.out.println(management.getLastMessage());
                if (!saved) {
                    System.out.println("Exit cancelled because the latest changes were not saved.");
                    return false;
                }
            }
        }
        System.out.println("Good bye!");
        return true;
    }

    private static void printMountainList(StudentManagement management) {
        List<Mountain> mountains = management.getAllMountains();
        if (mountains.isEmpty()) {
            System.out.println("No mountains loaded. Please check MountainList.csv.");
            return;
        }
        System.out.println("\nAvailable mountains:");
        for (Mountain mountain : mountains) {
            System.out.println(mountain.getMountainCode() + " - " + mountain.getMountain()
                    + " (" + mountain.getProvince() + ")");
        }
    }

    private static String inputNewStudentId(StudentManagement management) {
        while (true) {
            String id = DataInput.getRequiredString("Student ID: ",
                    "Student ID cannot be empty. Please re-enter.");
            String normalizedId = DataValidation.normalizeStudentId(id);
            if (!DataValidation.isValidStudentId(normalizedId)) {
                System.out.println("Invalid ID format. Example: SE123456.");
            } else if (management.getStudentById(normalizedId) != null) {
                System.out.println("Student ID already exists.");
            } else {
                return normalizedId;
            }
        }
    }

    private static String inputValidName(String message) {
        while (true) {
            String value = DataInput.getRequiredString(message,
                    "Name cannot be empty. Please re-enter.");
            if (DataValidation.isValidStudentName(value)) {
                return value;
            }
            System.out.println("Invalid name. Name must be from 2 to 20 characters.");
        }
    }

    private static String inputValidPhone(String message) {
        while (true) {
            String value = DataInput.getRequiredString(message,
                    "Phone cannot be empty. Please re-enter.");
            if (DataValidation.isValidPhone(value)) {
                return value;
            }
            System.out.println("Invalid phone. Enter 10 digits with a supported Vietnamese prefix.");
        }
    }

    private static String inputValidEmail(String message) {
        while (true) {
            String value = DataInput.getRequiredString(message,
                    "Email cannot be empty. Please re-enter.");
            if (DataValidation.isValidEmail(value)) {
                return value;
            }
            System.out.println("Invalid email format.");
        }
    }

    private static String inputValidMountainCode(StudentManagement management, String message) {
        while (true) {
            String value = DataInput.getRequiredString(message,
                    "Mountain code cannot be empty. Please re-enter.");
            if (management.isValidMountainCode(value)) {
                return value;
            }
            System.out.println("Mountain code does not exist. Please re-enter.");
        }
    }

    private static String inputOptionalValidName(String message) {
        while (true) {
            String value = DataInput.getString(message);
            if (value.trim().isEmpty() || DataValidation.isValidStudentName(value)) {
                return value;
            }
            System.out.println("Invalid name. Name must be from 2 to 20 characters or blank to skip.");
        }
    }

    private static String inputOptionalValidPhone(String message) {
        while (true) {
            String value = DataInput.getString(message);
            if (value.trim().isEmpty() || DataValidation.isValidPhone(value)) {
                return value;
            }
            System.out.println("Invalid phone. Enter a supported 10-digit Vietnamese number or blank to skip.");
        }
    }

    private static String inputOptionalValidEmail(String message) {
        while (true) {
            String value = DataInput.getString(message);
            if (value.trim().isEmpty() || DataValidation.isValidEmail(value)) {
                return value;
            }
            System.out.println("Invalid email format or blank to skip.");
        }
    }

    private static String inputOptionalValidMountainCode(StudentManagement management, String message) {
        while (true) {
            String value = DataInput.getString(message);
            if (value.trim().isEmpty() || management.isValidMountainCode(value)) {
                return value;
            }
            System.out.println("Mountain code does not exist. Please re-enter or leave blank to skip.");
        }
    }

    
    private static String inputValidCampusCode() {
        while (true) {
            String campusCode = DataInput.getRequiredString(
                    "Enter campus code (SE/HE/DE/QE/CE): ",
                    "Campus code cannot be empty. Please re-enter.");
            if (DataValidation.isValidCampusCode(campusCode)) {
                return campusCode.trim().toUpperCase();
            }
            System.out.println("Invalid campus code. Please enter SE, HE, DE, QE, or CE.");
        }
    }

    private static void printStudentTable(List<Student> students) {
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        System.out.printf("%-12s | %-20s | %-12s | %-25s | %-13s | %12s%n",
                "Student ID", "Name", "Phone", "Email", "Mountain", "Fee");
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        for (Student s : students) {
            System.out.printf("%-12s | %-20s | %-12s | %-25s | %-13s | %12s%n",
                    s.getStudentId(),
                    s.getName(),
                    s.getPhone(),
                    s.getEmail(),
                    s.getMountainCode(),
                    MONEY_FORMAT.format(s.getTuitionFee()));
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------");
    }

    private static void printStudentDetail(Student student) {
        System.out.println("Student ID   : " + student.getStudentId());
        System.out.println("Name         : " + student.getName());
        System.out.println("Phone        : " + student.getPhone());
        System.out.println("Email        : " + student.getEmail());
        System.out.println("Mountain Code: " + student.getMountainCode());
        System.out.println("Fee          : " + MONEY_FORMAT.format(student.getTuitionFee()));
    }
}
