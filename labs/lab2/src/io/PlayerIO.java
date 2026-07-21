package io;

import java.util.Scanner;
import manager.IClubManager;
import manager.IPlayerManager;
import model.Player;
import model.Position;
import util.InputUtils;
import util.TablePrinter;
import validation.ValidationUtils;

/** Console input/output for player-related functions. */
public class PlayerIO {

    private final Scanner scanner;
    private final IClubManager clubManager;
    private final IPlayerManager playerManager;

    public PlayerIO(Scanner scanner, IClubManager clubManager, IPlayerManager playerManager) {
        if (scanner == null || clubManager == null || playerManager == null) {
            throw new IllegalArgumentException("Scanner and manager dependencies cannot be null.");
        }
        this.scanner = scanner;
        this.clubManager = clubManager;
        this.playerManager = playerManager;
    }

    public void listPlayersSortedByClubName() {
        TablePrinter.printPlayers(playerManager.getPlayersSortedByClubName(), playerManager);
    }

    public void searchPlayersByPartialName() {
        String keyword = InputUtils.readRequired(scanner, "Enter partial player name: ");
        TablePrinter.printPlayers(playerManager.searchByPartialName(keyword), playerManager);
    }

    public void addNewPlayer() {
        if (clubManager.getAllClubs().isEmpty()) {
            System.out.println("No club is available. Please add a club first.");
            return;
        }
        TablePrinter.printClubs(clubManager.getAllClubs());
        String playerId = readNewPlayerId();
        String clubId = readExistingClubId();
        String playerName = InputUtils.readRequired(scanner, "Enter player name: ");
        Position position = readPosition("Enter position: ");
        int shirtNumber = readNewShirtNumber(clubId, null);
        Player player = new Player(playerId, clubId, playerName, position, shirtNumber);
        System.out.println(playerManager.addPlayer(player)
                ? "Add player successfully!" : "Add player failed!");
    }

    public void removePlayerById() {
        String playerId = InputUtils.readRequired(scanner, "Enter player ID: ");
        if (playerManager.removePlayer(playerId)) {
            System.out.println("Remove player successfully!");
        } else {
            System.out.println("This player does not exist!");
        }
    }

    public void updatePlayerById() {
        String playerId = InputUtils.readRequired(scanner, "Enter player ID: ");
        Player player = playerManager.getPlayerById(playerId);
        if (player == null) {
            System.out.println("This player does not exist!");
            return;
        }
        String playerName = InputUtils.readLine(scanner, "Enter new player name (blank to skip): ");
        String positionText = readOptionalPosition("Enter new position (blank to skip): ");
        String shirtNumberText = readOptionalShirtNumber(player.getClubId(), playerId,
                "Enter new shirt number (blank to skip): ");
        System.out.println(playerManager.updatePlayer(playerId, playerName, positionText, shirtNumberText)
                ? "Update player successfully!" : "Update player failed!");
    }

    public void listPlayersByPosition() {
        Position position = readPosition("Enter position: ");
        TablePrinter.printPlayers(playerManager.filterByPosition(position), playerManager);
    }

    private String readNewPlayerId() {
        while (true) {
            String playerId = InputUtils.readRequired(scanner, "Enter player ID (Pxxxx): ").toUpperCase();
            if (!ValidationUtils.isValidPlayerId(playerId)) {
                System.out.println("Invalid player ID format!");
            } else if (playerManager.containsId(playerId)) {
                System.out.println("This player ID already exists!");
            } else {
                return playerId;
            }
        }
    }

    private String readExistingClubId() {
        while (true) {
            String clubId = InputUtils.readRequired(scanner, "Enter existing club ID: ").toUpperCase();
            if (clubManager.getClubById(clubId) != null) {
                return clubId;
            }
            System.out.println("This club does not exist!");
        }
    }

    private Position readPosition(String message) {
        while (true) {
            Position position = Position.parse(InputUtils.readRequired(scanner, message));
            if (position != null) {
                return position;
            }
            System.out.println("Position must be Goalkeeper, Defender, Midfielder, Forward, or Winger!");
        }
    }

    private String readOptionalPosition(String message) {
        while (true) {
            String positionText = InputUtils.readLine(scanner, message);
            if (ValidationUtils.isBlank(positionText) || ValidationUtils.isValidPosition(positionText)) {
                return positionText;
            }
            System.out.println("Position must be Goalkeeper, Defender, Midfielder, Forward, or Winger!");
        }
    }

    private int readNewShirtNumber(String clubId, String ignoredPlayerId) {
        while (true) {
            String numberText = InputUtils.readRequired(scanner, "Enter shirt number: ");
            if (!ValidationUtils.isValidShirtNumber(numberText)) {
                System.out.println("Shirt number must be an integer from 1 to 99!");
                continue;
            }
            int shirtNumber = Integer.parseInt(numberText);
            if (playerManager.isShirtNumberUsed(clubId, shirtNumber, ignoredPlayerId)) {
                System.out.println("This shirt number already exists in this club!");
            } else {
                return shirtNumber;
            }
        }
    }

    private String readOptionalShirtNumber(String clubId, String ignoredPlayerId, String message) {
        while (true) {
            String numberText = InputUtils.readLine(scanner, message);
            if (ValidationUtils.isBlank(numberText)) {
                return numberText;
            }
            if (!ValidationUtils.isValidShirtNumber(numberText)) {
                System.out.println("Shirt number must be an integer from 1 to 99!");
                continue;
            }
            int shirtNumber = Integer.parseInt(numberText);
            if (playerManager.isShirtNumberUsed(clubId, shirtNumber, ignoredPlayerId)) {
                System.out.println("This shirt number already exists in this club!");
            } else {
                return numberText;
            }
        }
    }
}
