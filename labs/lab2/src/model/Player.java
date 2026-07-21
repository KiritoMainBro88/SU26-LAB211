package model;

/** Domain entity for a football player. */
public class Player extends BaseEntity {

    private final String clubId;
    private String playerName;
    private Position position;
    private int shirtNumber;

    public Player(String playerId, String clubId, String playerName,
            Position position, int shirtNumber) {
        super(playerId);
        this.clubId = clubId;
        this.playerName = playerName;
        this.position = position;
        this.shirtNumber = shirtNumber;
    }

    public Player(Player other) {
        this(other.getId(), other.getClubId(), other.getPlayerName(),
                other.getPosition(), other.getShirtNumber());
    }

    public String getClubId() {
        return clubId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getShirtNumber() {
        return shirtNumber;
    }

    public void setShirtNumber(int shirtNumber) {
        this.shirtNumber = shirtNumber;
    }

    @Override
    public String toString() {
        String positionName = position == null ? "" : position.getDisplayName();
        return String.format("%-8s | %-8s | %-25s | %-12s | %5d",
                getId(), clubId == null ? "" : clubId,
                playerName == null ? "" : playerName, positionName, shirtNumber);
    }
}
