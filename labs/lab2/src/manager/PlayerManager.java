package manager;

import filehelper.IPlayerFileHelper;
import filehelper.PlayerFileHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import model.Club;
import model.Player;
import model.Position;
import util.LogUtils;
import validation.ValidationUtils;

/** Business operations and persistence coordination for football players. */
public class PlayerManager extends AbstractManager<Player> implements IPlayerManager {

    private final IClubManager clubManager;
    private final IPlayerFileHelper fileHelper;

    public PlayerManager(IClubManager clubManager) {
        this(clubManager, new PlayerFileHelper());
    }

    public PlayerManager(IClubManager clubManager, IPlayerFileHelper fileHelper) {
        if (clubManager == null || fileHelper == null) {
            throw new IllegalArgumentException("Club manager and player file helper cannot be null.");
        }
        this.clubManager = clubManager;
        this.fileHelper = fileHelper;
    }

    @Override
    public List<Player> getAllPlayers() {
        return getItems();
    }

    @Override
    public Player getPlayerById(String playerId) {
        return findById(playerId);
    }

    @Override
    public boolean addPlayer(Player player) {
        if (!isValidPlayer(player) || containsId(player.getId())) {
            return false;
        }
        if (clubManager.getClubById(player.getClubId()) == null) {
            return false;
        }
        if (isShirtNumberUsed(player.getClubId(), player.getShirtNumber(), null)) {
            return false;
        }
        mutableItems().add(copyOf(player));
        setChanged(true);
        return true;
    }

    @Override
    public boolean removePlayer(String playerId) {
        Player player = findMutableById(playerId);
        if (player == null) {
            return false;
        }
        mutableItems().remove(player);
        setChanged(true);
        return true;
    }

    @Override
    public boolean updatePlayer(String playerId, String playerName,
            String positionText, String shirtNumberText) {
        Player player = findMutableById(playerId);
        if (player == null) {
            return false;
        }

        Position newPosition = null;
        if (!ValidationUtils.isBlank(positionText)) {
            newPosition = Position.parse(positionText);
            if (newPosition == null) {
                return false;
            }
        }

        Integer newShirtNumber = null;
        if (!ValidationUtils.isBlank(shirtNumberText)) {
            if (!ValidationUtils.isValidShirtNumber(shirtNumberText)) {
                return false;
            }
            newShirtNumber = Integer.valueOf(shirtNumberText.trim());
            if (isShirtNumberUsed(player.getClubId(), newShirtNumber.intValue(), playerId)) {
                return false;
            }
        }

        boolean updated = false;
        if (!ValidationUtils.isBlank(playerName)) {
            String newName = playerName.trim();
            if (!newName.equals(player.getPlayerName())) {
                player.setPlayerName(newName);
                updated = true;
            }
        }
        if (newPosition != null && newPosition != player.getPosition()) {
            player.setPosition(newPosition);
            updated = true;
        }
        if (newShirtNumber != null && newShirtNumber.intValue() != player.getShirtNumber()) {
            player.setShirtNumber(newShirtNumber.intValue());
            updated = true;
        }
        if (updated) {
            setChanged(true);
        }
        return true;
    }

    @Override
    public boolean isShirtNumberUsed(String clubId, int shirtNumber, String ignoredPlayerId) {
        if (ValidationUtils.isBlank(clubId) || shirtNumber < 1 || shirtNumber > 99) {
            return false;
        }
        for (Player player : mutableItems()) {
            boolean sameClub = player.getClubId().equalsIgnoreCase(clubId.trim());
            boolean sameNumber = player.getShirtNumber() == shirtNumber;
            boolean ignored = ignoredPlayerId != null
                    && player.getId().equalsIgnoreCase(ignoredPlayerId.trim());
            if (sameClub && sameNumber && !ignored) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Player> searchByPartialName(String keyword) {
        List<Player> result = new ArrayList<Player>();
        if (ValidationUtils.isBlank(keyword)) {
            return result;
        }
        String normalizedKeyword = keyword.trim().toLowerCase(Locale.ROOT);
        for (Player player : mutableItems()) {
            if (player.getPlayerName().toLowerCase(Locale.ROOT).contains(normalizedKeyword)) {
                result.add(copyOf(player));
            }
        }
        return result;
    }

    @Override
    public List<Player> filterByPosition(Position position) {
        List<Player> result = new ArrayList<Player>();
        if (position == null) {
            return result;
        }
        for (Player player : mutableItems()) {
            if (player.getPosition() == position) {
                result.add(copyOf(player));
            }
        }
        return result;
    }

    @Override
    public List<Player> getPlayersSortedByClubName() {
        List<Player> result = getAllPlayers();
        Collections.sort(result, new Comparator<Player>() {
            @Override
            public int compare(Player first, Player second) {
                String firstClubName = getClubName(first.getClubId());
                String secondClubName = getClubName(second.getClubId());
                int byClubName = firstClubName.compareToIgnoreCase(secondClubName);
                if (byClubName != 0) {
                    return byClubName;
                }
                if (first.getClubId().equalsIgnoreCase(second.getClubId())) {
                    return Integer.compare(first.getShirtNumber(), second.getShirtNumber());
                }
                return first.getClubId().compareToIgnoreCase(second.getClubId());
            }
        });
        return result;
    }

    @Override
    public String getClubName(String clubId) {
        Club club = clubManager.getClubById(clubId);
        return club == null ? "" : club.getClubName();
    }

    @Override
    public boolean loadFromFile(String filePath) {
        try {
            replaceAll(fileHelper.loadPlayers(filePath, clubManager.getAllClubs()));
            return true;
        } catch (IOException | IllegalArgumentException exception) {
            LogUtils.logError("Cannot load player data", exception);
            return false;
        }
    }

    @Override
    public boolean saveToFile(String filePath) {
        boolean saved = fileHelper.savePlayers(filePath, getAllPlayers(), clubManager.getAllClubs());
        if (saved) {
            setChanged(false);
        }
        return saved;
    }

    @Override
    public void replaceAll(List<Player> newItems) {
        if (newItems == null) {
            throw new IllegalArgumentException("Replacement data cannot be null.");
        }
        List<Player> validated = new ArrayList<Player>();
        for (Player player : newItems) {
            if (!isValidPlayer(player)
                    || ValidationUtils.isDuplicatePlayerId(validated, player.getId())
                    || clubManager.getClubById(player.getClubId()) == null
                    || ValidationUtils.isDuplicateShirtNumber(validated, player.getClubId(),
                            player.getShirtNumber(), null)) {
                throw new IllegalArgumentException("Replacement player data is invalid.");
            }
            validated.add(player);
        }
        super.replaceAll(validated);
    }

    @Override
    protected Player copyOf(Player player) {
        return new Player(player);
    }

    private boolean isValidPlayer(Player player) {
        return player != null
                && ValidationUtils.isValidPlayerId(player.getId())
                && !ValidationUtils.isBlank(player.getClubId())
                && !ValidationUtils.isBlank(player.getPlayerName())
                && player.getPosition() != null
                && player.getShirtNumber() >= 1
                && player.getShirtNumber() <= 99;
    }
}
