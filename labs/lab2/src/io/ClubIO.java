package io;

import java.util.Collections;
import java.util.Scanner;
import manager.IClubManager;
import model.Club;
import util.InputUtils;
import util.TablePrinter;
import validation.ValidationUtils;

/** Console input/output for club-related functions. */
public class ClubIO {

    private final Scanner scanner;
    private final IClubManager clubManager;

    public ClubIO(Scanner scanner, IClubManager clubManager) {
        if (scanner == null || clubManager == null) {
            throw new IllegalArgumentException("Scanner and club manager cannot be null.");
        }
        this.scanner = scanner;
        this.clubManager = clubManager;
    }

    public void listAllClubs() {
        TablePrinter.printClubs(clubManager.getAllClubs());
    }

    public void addNewClub() {
        String clubId = readNewClubId();
        String clubName = InputUtils.readRequired(scanner, "Enter club name: ");
        String sponsorBrand = InputUtils.readRequired(scanner, "Enter sponsor brand: ");
        double budget = Double.parseDouble(readPositiveBudget("Enter budget: "));
        Club club = new Club(clubId, clubName, sponsorBrand, budget);
        System.out.println(clubManager.addClub(club) ? "Add club successfully!" : "Add club failed!");
    }

    public void searchClubById() {
        String clubId = InputUtils.readRequired(scanner, "Enter club ID: ");
        Club club = clubManager.getClubById(clubId);
        if (club == null) {
            System.out.println("This club does not exist!");
            return;
        }
        TablePrinter.printClubs(Collections.singletonList(club));
    }

    public void updateClubById() {
        String clubId = InputUtils.readRequired(scanner, "Enter club ID: ");
        if (clubManager.getClubById(clubId) == null) {
            System.out.println("This club does not exist!");
            return;
        }
        String clubName = InputUtils.readLine(scanner, "Enter new club name (blank to skip): ");
        String sponsorBrand = InputUtils.readLine(scanner, "Enter new sponsor brand (blank to skip): ");
        String budgetText = readOptionalBudget("Enter new budget (blank to skip): ");
        System.out.println(clubManager.updateClub(clubId, clubName, sponsorBrand, budgetText)
                ? "Update club successfully!" : "Update club failed!");
    }

    public void listClubsByBudget() {
        double maxBudget = Double.parseDouble(readFiniteBudget("Enter max budget: "));
        TablePrinter.printClubs(clubManager.filterByBudget(maxBudget));
    }

    private String readNewClubId() {
        while (true) {
            String clubId = InputUtils.readRequired(scanner, "Enter club ID (CL-xxxx): ").toUpperCase();
            if (!ValidationUtils.isValidClubId(clubId)) {
                System.out.println("Invalid club ID format!");
            } else if (clubManager.containsId(clubId)) {
                System.out.println("This club ID already exists!");
            } else {
                return clubId;
            }
        }
    }

    private String readFiniteBudget(String message) {
        while (true) {
            String budgetText = InputUtils.readRequired(scanner, message);
            if (ValidationUtils.isFiniteDouble(budgetText)) {
                return budgetText;
            }
            System.out.println("Budget threshold must be a finite real number!");
        }
    }

    private String readPositiveBudget(String message) {
        while (true) {
            String budgetText = InputUtils.readRequired(scanner, message);
            if (ValidationUtils.isPositiveDouble(budgetText)) {
                return budgetText;
            }
            System.out.println("Budget must be a positive real number!");
        }
    }

    private String readOptionalBudget(String message) {
        while (true) {
            String budgetText = InputUtils.readLine(scanner, message);
            if (ValidationUtils.isBlank(budgetText) || ValidationUtils.isPositiveDouble(budgetText)) {
                return budgetText;
            }
            System.out.println("Budget must be a positive real number!");
        }
    }
}
