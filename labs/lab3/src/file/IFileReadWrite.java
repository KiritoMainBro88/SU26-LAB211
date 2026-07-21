package file;

import java.util.List;

/** Generic persistence contract used by the employee manager. */
public interface IFileReadWrite<T> {
    List<T> read() throws Exception;
    boolean write(List<T> list) throws Exception;
}
