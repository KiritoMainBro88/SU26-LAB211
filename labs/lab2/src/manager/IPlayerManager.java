package manager;

import java.util.List;
import model.Player;
import model.Position;

/** Player-specific business operations required by J1.L.P0036. */
public interface IPlayerManager extends IManager<Player> {
    List<Player> getAllPlayers();
    Player getPlayerById(String playerId);
    boolean addPlayer(Player player);
    boolean removePlayer(String playerId);
    boolean updatePlayer(String playerId, String playerName, String positionText, String shirtNumberText);
    boolean isShirtNumberUsed(String clubId, int shirtNumber, String ignoredPlayerId);
    List<Player> searchByPartialName(String keyword);
    List<Player> filterByPosition(Position position);
    List<Player> getPlayersSortedByClubName();
    String getClubName(String clubId);
    boolean loadFromFile(String filePath);
    boolean saveToFile(String filePath);
}
