package manager;

import java.util.List;

/** Common manager operations shared by club and player modules. */
public interface IManager<T> {
    List<T> getItems();
    boolean isChanged();
    T findById(String id);
    boolean containsId(String id);
    void replaceAll(List<T> newItems);
}
