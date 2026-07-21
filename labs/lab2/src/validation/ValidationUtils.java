package validation;

import java.util.List;
import model.Club;
import model.Player;
import model.Position;

/** Central validation rules for club and player data. */
public final class ValidationUtils {

    public static final String CLUB_ID_PATTERN = "CL-\\d{4}";
    public static final String PLAYER_ID_PATTERN = "P\\d{4}";

    private ValidationUtils() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isValidClubId(String value) {
        return value != null && value.trim().toUpperCase().matches(CLUB_ID_PATTERN);
    }

    public static boolean isValidPlayerId(String value) {
        return value != null && value.trim().toUpperCase().matches(PLAYER_ID_PATTERN);
    }

    public static boolean isFiniteDouble(String value) {
        if (isBlank(value)) {
            return false;
        }
        try {
            return Double.isFinite(Double.parseDouble(value.trim()));
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    public static boolean isPositiveDouble(String value) {
        if (isBlank(value)) {
            return false;
        }
        try {
            double number = Double.parseDouble(value.trim());
            return Double.isFinite(number) && number > 0;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    public static boolean isValidShirtNumber(String value) {
        if (isBlank(value)) {
            return false;
        }
        try {
            int number = Integer.parseInt(value.trim());
            return number >= 1 && number <= 99;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    public static boolean isValidPosition(String value) {
        return Position.parse(value) != null;
    }

    public static boolean isDuplicateClubId(List<Club> clubs, String clubId) {
        if (clubs == null || clubId == null) {
            return false;
        }
        for (Club club : clubs) {
            if (club != null && clubId.trim().equalsIgnoreCase(club.getId())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDuplicatePlayerId(List<Player> players, String playerId) {
        if (players == null || playerId == null) {
            return false;
        }
        for (Player player : players) {
            if (player != null && playerId.trim().equalsIgnoreCase(player.getId())) {
                return true;
            }
        }
        return false;
    }

    public static boolean existsClubId(List<Club> clubs, String clubId) {
        return isDuplicateClubId(clubs, clubId);
    }

    public static boolean isDuplicateShirtNumber(List<Player> players, String clubId, int shirtNumber,
            String ignoredPlayerId) {
        if (players == null || clubId == null) {
            return false;
        }
        for (Player player : players) {
            if (player == null) {
                continue;
            }
            boolean sameClub = clubId.trim().equalsIgnoreCase(player.getClubId());
            boolean sameNumber = player.getShirtNumber() == shirtNumber;
            boolean ignored = ignoredPlayerId != null
                    && ignoredPlayerId.trim().equalsIgnoreCase(player.getId());
            if (sameClub && sameNumber && !ignored) {
                return true;
            }
        }
        return false;
    }
}
