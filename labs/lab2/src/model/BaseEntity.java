package model;

/** Base type for entities whose identity is a case-insensitive ID. */
public abstract class BaseEntity implements Identifiable {

    private final String id;

    protected BaseEntity(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        BaseEntity other = (BaseEntity) object;
        return id != null && other.id != null && id.equalsIgnoreCase(other.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.toUpperCase(java.util.Locale.ROOT).hashCode();
    }
}
