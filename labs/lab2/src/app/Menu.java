package app;

import io.ClubIO;
import io.PlayerIO;
import java.util.Collections;
import java.util.Scanner;
import manager.ClubManager;
import manager.IClubManager;
import manager.IPlayerManager;
import manager.PlayerManager;
import model.Club;
import model.Player;
import util.InputUtils;

/** Coordinates the fourteen menu functions required by J1.L.P0036. */
public class Menu {

    private static final String CLUB_FILE = "clubs.txt";
    private static final String PLAYER_FILE = "players.txt";

    private final Scanner scanner;
    private final IClubManager clubManager;
    private final IPlayerManager playerManager;
    private final ClubIO clubIO;
    private final PlayerIO playerIO;

    public Menu() {
        scanner = new Scanner(System.in);
        clubManager = new ClubManager();
        playerManager = new PlayerManager(clubManager);
        clubIO = new ClubIO(scanner, clubManager);
        playerIO = new PlayerIO(scanner, clubManager, playerManager);
    }

    public void run() {
        loadData(false);
        boolean running = true;
        while (running) {
            showMenu();
            int choice = InputUtils.readMenuChoice(scanner, 1, 14);
            running = processChoice(choice);
        }
        scanner.close();
    }

    private void showMenu() {
        System.out.println("\n========== FOOTBALL CLUB & PLAYER MANAGEMENT ==========");
        System.out.println("1. List of all clubs");
        System.out.println("2. Add a new club");
        System.out.println("3. Search for a club by ID");
        System.out.println("4. Update a club by ID");
        System.out.println("5. List all clubs with budget <= input value");
        System.out.println("6. List all players in ascending order of club names");
        System.out.println("7. Search players by partial player name");
        System.out.println("8. Add a new player");
        System.out.println("9. Remove a player with ID");
        System.out.println("10. Update a player with an ID");
        System.out.println("11. List all players by a specific position");
        System.out.println("12. Save data to files");
        System.out.println("13. Load data from files");
        System.out.println("14. Quit program");
    }

    private boolean processChoice(int choice) {
        switch (choice) {
            case 1:
                clubIO.listAllClubs();
                break;
            case 2:
                clubIO.addNewClub();
                break;
            case 3:
                clubIO.searchClubById();
                break;
            case 4:
                clubIO.updateClubById();
                break;
            case 5:
                clubIO.listClubsByBudget();
                break;
            case 6:
                playerIO.listPlayersSortedByClubName();
                break;
            case 7:
                playerIO.searchPlayersByPartialName();
                break;
            case 8:
                playerIO.addNewPlayer();
                break;
            case 9:
                playerIO.removePlayerById();
                break;
            case 10:
                playerIO.updatePlayerById();
                break;
            case 11:
                playerIO.listPlayersByPosition();
                break;
            case 12:
                saveData();
                break;
            case 13:
                loadData(true);
                break;
            case 14:
                return !quitProgram();
            default:
                System.out.println("Invalid choice!");
                break;
        }
        return true;
    }

    /** Clears current data, then performs the strict clubs-first/player-second reload. */
    private boolean loadData(boolean showSuccessMessage) {
        clubManager.replaceAll(Collections.<Club>emptyList());
        playerManager.replaceAll(Collections.<Player>emptyList());

        if (!clubManager.loadFromFile(CLUB_FILE)) {
            System.out.println("Load data failed!");
            return false;
        }
        if (!playerManager.loadFromFile(PLAYER_FILE)) {
            clubManager.replaceAll(Collections.<Club>emptyList());
            playerManager.replaceAll(Collections.<Player>emptyList());
            System.out.println("Load data failed!");
            return false;
        }
        if (showSuccessMessage) {
            System.out.println("Load data successfully!");
        }
        return true;
    }

    private boolean saveData() {
        boolean clubSaved = clubManager.saveToFile(CLUB_FILE);
        boolean playerSaved = playerManager.saveToFile(PLAYER_FILE);
        if (clubSaved && playerSaved) {
            System.out.println("Save data successfully!");
            return true;
        }
        System.out.println("Save data failed!");
        return false;
    }

    /** Returns true only when the program may terminate safely. */
    private boolean quitProgram() {
        if ((clubManager.isChanged() || playerManager.isChanged()) && !saveData()) {
            System.out.println("Exit cancelled because changed data could not be saved.");
            return false;
        }
        System.out.println("Goodbye!");
        return true;
    }
}
