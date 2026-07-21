package model;

/** Allowed player positions from the assignment constraints. */
public enum Position {
    GOALKEEPER("Goalkeeper"),
    DEFENDER("Defender"),
    MIDFIELDER("Midfielder"),
    FORWARD("Forward"),
    WINGER("Winger");

    private final String displayName;

    Position(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Position parse(String value) {
        if (value == null) {
            return null;
        }
        String normalizedValue = value.trim();
        for (Position position : values()) {
            if (position.displayName.equalsIgnoreCase(normalizedValue)
                    || position.name().equalsIgnoreCase(normalizedValue)) {
                return position;
            }
        }
        return null;
    }
}
