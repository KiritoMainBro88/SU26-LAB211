package filehelper;

import java.io.IOException;
import java.util.List;
import model.Club;
import model.Player;

/** Contract for loading and saving player data with club-reference validation. */
public interface IPlayerFileHelper {
    List<Player> loadPlayers(String filePath, List<Club> clubs) throws IOException;
    boolean savePlayers(String filePath, List<Player> players, List<Club> clubs);
}
