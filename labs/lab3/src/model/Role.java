package model;

/** Allowed employee roles from the assignment constraints. */
public enum Role {
    DEVELOPER("Developer"),
    TESTER("Tester"),
    MANAGER("Manager"),
    HR("HR");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Role parse(String text) {
        if (text == null) {
            return null;
        }
        String normalizedText = text.trim();
        for (Role role : values()) {
            if (role.displayName.equalsIgnoreCase(normalizedText)
                    || role.name().equalsIgnoreCase(normalizedText)) {
                return role;
            }
        }
        return null;
    }
}
