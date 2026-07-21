package filehelper;

import java.io.IOException;
import java.util.List;
import model.Club;

/** Contract for loading and saving club data in the required text format. */
public interface IClubFileHelper {
    List<Club> loadClubs(String filePath) throws IOException;
    boolean saveClubs(String filePath, List<Club> clubs);
}
