package filehelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import model.Club;
import model.Player;
import model.Position;
import util.CsvUtils;
import util.LogUtils;
import validation.ValidationUtils;

/** Reads and writes player data using the assignment text-file format. */
public class PlayerFileHelper implements IPlayerFileHelper {

    @Override
    public List<Player> loadPlayers(String filePath, List<Club> clubs) throws IOException {
        if (ValidationUtils.isBlank(filePath) || clubs == null) {
            throw new IOException("Player file path and club list are required.");
        }
        List<Player> players = new ArrayList<Player>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (ValidationUtils.isBlank(line)) {
                    throw new IOException("Blank player line " + lineNumber + " is invalid.");
                }
                players.add(parsePlayer(line, clubs, players, lineNumber));
            }
        }
        return players;
    }

    private Player parsePlayer(String line, List<Club> clubs, List<Player> existingPlayers, int lineNumber)
            throws IOException {
        List<String> parts = CsvUtils.parseLine(line);
        if (parts.size() != 5) {
            throw new IOException("Invalid player line " + lineNumber + ": " + line);
        }
        String playerId = parts.get(0).trim().toUpperCase();
        String clubId = parts.get(1).trim().toUpperCase();
        String playerName = parts.get(2).trim();
        String positionText = parts.get(3).trim();
        String shirtNumberText = parts.get(4).trim();
        if (!ValidationUtils.isValidPlayerId(playerId)
                || ValidationUtils.isDuplicatePlayerId(existingPlayers, playerId)
                || !ValidationUtils.existsClubId(clubs, clubId)
                || ValidationUtils.isBlank(playerName)
                || !ValidationUtils.isValidPosition(positionText)
                || !ValidationUtils.isValidShirtNumber(shirtNumberText)) {
            throw new IOException("Invalid player data at line " + lineNumber + ": " + line);
        }
        int shirtNumber = Integer.parseInt(shirtNumberText);
        if (ValidationUtils.isDuplicateShirtNumber(existingPlayers, clubId, shirtNumber, null)) {
            throw new IOException("Duplicate shirt number at line " + lineNumber + ": " + line);
        }
        return new Player(playerId, clubId, playerName, Position.parse(positionText), shirtNumber);
    }

    @Override
    public boolean savePlayers(String filePath, List<Player> players, List<Club> clubs) {
        if (ValidationUtils.isBlank(filePath) || !isValidPlayerList(players, clubs)) {
            return false;
        }
        Path target = Paths.get(filePath);
        Path temp = null;
        try {
            temp = createTempSibling(target, "players-");
            try (BufferedWriter writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
                for (Player player : players) {
                    writer.write(String.format("%s, %s, %s, %s, %d",
                            CsvUtils.escape(player.getId()), CsvUtils.escape(player.getClubId()),
                            CsvUtils.escape(player.getPlayerName()),
                            CsvUtils.escape(player.getPosition().getDisplayName()),
                            player.getShirtNumber()));
                    writer.newLine();
                }
            }
            replaceTarget(temp, target);
            return true;
        } catch (IOException exception) {
            LogUtils.logError("Cannot save players", exception);
            deleteQuietly(temp);
            return false;
        }
    }

    private boolean isValidPlayerList(List<Player> players, List<Club> clubs) {
        if (players == null || clubs == null) {
            return false;
        }
        List<Player> checked = new ArrayList<Player>();
        for (Player player : players) {
            if (player == null
                    || !ValidationUtils.isValidPlayerId(player.getId())
                    || ValidationUtils.isDuplicatePlayerId(checked, player.getId())
                    || ValidationUtils.isBlank(player.getClubId())
                    || !ValidationUtils.existsClubId(clubs, player.getClubId())
                    || ValidationUtils.isBlank(player.getPlayerName())
                    || player.getPosition() == null
                    || player.getShirtNumber() < 1
                    || player.getShirtNumber() > 99
                    || ValidationUtils.isDuplicateShirtNumber(checked, player.getClubId(),
                            player.getShirtNumber(), null)) {
                return false;
            }
            checked.add(player);
        }
        return true;
    }

    private Path createTempSibling(Path target, String prefix) throws IOException {
        Path absoluteTarget = target.toAbsolutePath();
        Path parent = absoluteTarget.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
            return Files.createTempFile(parent, prefix, ".tmp");
        }
        return Files.createTempFile(prefix, ".tmp");
    }

    private void replaceTarget(Path temp, Path target) throws IOException {
        try {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ignored) {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void deleteQuietly(Path path) {
        if (path != null) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException exception) {
                LogUtils.logError("Cannot delete temporary player file", exception);
            }
        }
    }
}
