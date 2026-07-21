package manager;

import java.util.List;
import model.Club;

/** Club-specific business operations required by J1.L.P0036. */
public interface IClubManager extends IManager<Club> {
    List<Club> getAllClubs();
    Club getClubById(String clubId);
    boolean addClub(Club club);
    boolean updateClub(String clubId, String clubName, String sponsorBrand, String budgetText);
    List<Club> filterByBudget(double maxBudget);
    boolean loadFromFile(String filePath);
    boolean saveToFile(String filePath);
}
