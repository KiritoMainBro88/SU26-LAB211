package model;

/** Allowed employment states from the assignment constraints. */
public enum EmployeeStatus {
    ACTIVE("active"),
    INACTIVE("inactive");

    private final String displayName;

    EmployeeStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }

    public static EmployeeStatus parse(String text) {
        if (text == null) {
            return null;
        }
        String normalizedText = text.trim();
        for (EmployeeStatus status : values()) {
            if (status.displayName.equalsIgnoreCase(normalizedText)
                    || status.name().equalsIgnoreCase(normalizedText)) {
                return status;
            }
        }
        return null;
    }
}
