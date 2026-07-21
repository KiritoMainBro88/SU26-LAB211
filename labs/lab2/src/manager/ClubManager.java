package manager;

import filehelper.ClubFileHelper;
import filehelper.IClubFileHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.Club;
import util.LogUtils;
import validation.ValidationUtils;

/** Business operations and persistence coordination for football clubs. */
public class ClubManager extends AbstractManager<Club> implements IClubManager {

    private final IClubFileHelper fileHelper;

    public ClubManager() {
        this(new ClubFileHelper());
    }

    public ClubManager(IClubFileHelper fileHelper) {
        if (fileHelper == null) {
            throw new IllegalArgumentException("Club file helper cannot be null.");
        }
        this.fileHelper = fileHelper;
    }

    @Override
    public List<Club> getAllClubs() {
        return getItems();
    }

    @Override
    public Club getClubById(String clubId) {
        return findById(clubId);
    }

    @Override
    public boolean addClub(Club club) {
        if (!isValidClub(club) || containsId(club.getId())) {
            return false;
        }
        mutableItems().add(copyOf(club));
        setChanged(true);
        return true;
    }

    @Override
    public boolean updateClub(String clubId, String clubName, String sponsorBrand, String budgetText) {
        Club club = findMutableById(clubId);
        if (club == null) {
            return false;
        }
        if (!ValidationUtils.isBlank(budgetText) && !ValidationUtils.isPositiveDouble(budgetText)) {
            return false;
        }

        boolean updated = false;
        if (!ValidationUtils.isBlank(clubName)) {
            String newName = clubName.trim();
            if (!newName.equals(club.getClubName())) {
                club.setClubName(newName);
                updated = true;
            }
        }
        if (!ValidationUtils.isBlank(sponsorBrand)) {
            String newSponsor = sponsorBrand.trim();
            if (!newSponsor.equals(club.getSponsorBrand())) {
                club.setSponsorBrand(newSponsor);
                updated = true;
            }
        }
        if (!ValidationUtils.isBlank(budgetText)) {
            double newBudget = Double.parseDouble(budgetText.trim());
            if (Double.compare(newBudget, club.getBudget()) != 0) {
                club.setBudget(newBudget);
                updated = true;
            }
        }
        if (updated) {
            setChanged(true);
        }
        return true;
    }

    @Override
    public List<Club> filterByBudget(double maxBudget) {
        List<Club> result = new ArrayList<Club>();
        if (!Double.isFinite(maxBudget)) {
            return result;
        }
        for (Club club : mutableItems()) {
            if (club.getBudget() <= maxBudget) {
                result.add(copyOf(club));
            }
        }
        return result;
    }

    @Override
    public boolean loadFromFile(String filePath) {
        try {
            replaceAll(fileHelper.loadClubs(filePath));
            return true;
        } catch (IOException | IllegalArgumentException exception) {
            LogUtils.logError("Cannot load club data", exception);
            return false;
        }
    }

    @Override
    public boolean saveToFile(String filePath) {
        boolean saved = fileHelper.saveClubs(filePath, getAllClubs());
        if (saved) {
            setChanged(false);
        }
        return saved;
    }

    @Override
    public void replaceAll(List<Club> newItems) {
        if (newItems == null) {
            throw new IllegalArgumentException("Replacement data cannot be null.");
        }
        List<Club> validated = new ArrayList<Club>();
        for (Club club : newItems) {
            if (!isValidClub(club) || ValidationUtils.isDuplicateClubId(validated, club.getId())) {
                throw new IllegalArgumentException("Replacement club data is invalid.");
            }
            validated.add(club);
        }
        super.replaceAll(validated);
    }

    @Override
    protected Club copyOf(Club club) {
        return new Club(club);
    }

    private boolean isValidClub(Club club) {
        return club != null
                && ValidationUtils.isValidClubId(club.getId())
                && !ValidationUtils.isBlank(club.getClubName())
                && !ValidationUtils.isBlank(club.getSponsorBrand())
                && Double.isFinite(club.getBudget())
                && club.getBudget() > 0;
    }
}
