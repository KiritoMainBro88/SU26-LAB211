package manager;

import java.util.ArrayList;
import java.util.List;
import model.Identifiable;

/**
 * Reusable base manager for entities identified by a unique ID.
 * Public callers receive copies; subclasses use mutable lookup methods after
 * applying business validation.
 */
public abstract class AbstractManager<T extends Identifiable> implements IManager<T> {

    private final List<T> items;
    private boolean changed;

    protected AbstractManager() {
        items = new ArrayList<T>();
        changed = false;
    }

    protected abstract T copyOf(T item);

    @Override
    public List<T> getItems() {
        List<T> copies = new ArrayList<T>();
        for (T item : items) {
            copies.add(copyOf(item));
        }
        return copies;
    }

    protected List<T> mutableItems() {
        return items;
    }

    protected T findMutableById(String id) {
        if (id == null) {
            return null;
        }
        String normalizedId = id.trim();
        for (T item : items) {
            if (item != null && item.getId() != null
                    && normalizedId.equalsIgnoreCase(item.getId())) {
                return item;
            }
        }
        return null;
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    protected final void setChanged(boolean changed) {
        this.changed = changed;
    }

    @Override
    public T findById(String id) {
        T item = findMutableById(id);
        return item == null ? null : copyOf(item);
    }

    @Override
    public boolean containsId(String id) {
        return findMutableById(id) != null;
    }

    @Override
    public void replaceAll(List<T> newItems) {
        if (newItems == null) {
            throw new IllegalArgumentException("Replacement data cannot be null.");
        }
        List<T> copies = new ArrayList<T>();
        for (T item : newItems) {
            if (item == null) {
                throw new IllegalArgumentException("Replacement data cannot contain null items.");
            }
            copies.add(copyOf(item));
        }
        items.clear();
        items.addAll(copies);
        changed = false;
    }
}
