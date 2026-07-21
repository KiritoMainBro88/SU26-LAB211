package filehelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import model.Club;
import util.CsvUtils;
import util.LogUtils;
import validation.ValidationUtils;

/** Reads and writes club data using the assignment text-file format. */
public class ClubFileHelper implements IClubFileHelper {

    @Override
    public List<Club> loadClubs(String filePath) throws IOException {
        if (ValidationUtils.isBlank(filePath)) {
            throw new IOException("Club file path cannot be empty.");
        }
        List<Club> clubs = new ArrayList<Club>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (ValidationUtils.isBlank(line)) {
                    throw new IOException("Blank club line " + lineNumber + " is invalid.");
                }
                clubs.add(parseClub(line, clubs, lineNumber));
            }
        }
        return clubs;
    }

    private Club parseClub(String line, List<Club> existingClubs, int lineNumber) throws IOException {
        List<String> parts = CsvUtils.parseLine(line);
        if (parts.size() != 4) {
            throw new IOException("Invalid club line " + lineNumber + ": " + line);
        }
        String clubId = parts.get(0).trim().toUpperCase();
        String clubName = parts.get(1).trim();
        String sponsorBrand = parts.get(2).trim();
        String budgetText = parts.get(3).trim();
        if (!ValidationUtils.isValidClubId(clubId)
                || ValidationUtils.isDuplicateClubId(existingClubs, clubId)
                || ValidationUtils.isBlank(clubName)
                || ValidationUtils.isBlank(sponsorBrand)
                || !ValidationUtils.isPositiveDouble(budgetText)) {
            throw new IOException("Invalid club data at line " + lineNumber + ": " + line);
        }
        return new Club(clubId, clubName, sponsorBrand, Double.parseDouble(budgetText));
    }

    @Override
    public boolean saveClubs(String filePath, List<Club> clubs) {
        if (ValidationUtils.isBlank(filePath) || !isValidClubList(clubs)) {
            return false;
        }
        Path target = Paths.get(filePath);
        Path temp = null;
        try {
            temp = createTempSibling(target, "clubs-");
            try (BufferedWriter writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
                for (Club club : clubs) {
                    writer.write(String.format("%s, %s, %s, %s",
                            CsvUtils.escape(club.getId()), CsvUtils.escape(club.getClubName()),
                            CsvUtils.escape(club.getSponsorBrand()), formatBudget(club.getBudget())));
                    writer.newLine();
                }
            }
            replaceTarget(temp, target);
            return true;
        } catch (IOException exception) {
            LogUtils.logError("Cannot save clubs", exception);
            deleteQuietly(temp);
            return false;
        }
    }

    private boolean isValidClubList(List<Club> clubs) {
        if (clubs == null) {
            return false;
        }
        List<Club> checked = new ArrayList<Club>();
        for (Club club : clubs) {
            if (club == null
                    || !ValidationUtils.isValidClubId(club.getId())
                    || ValidationUtils.isDuplicateClubId(checked, club.getId())
                    || ValidationUtils.isBlank(club.getClubName())
                    || ValidationUtils.isBlank(club.getSponsorBrand())
                    || !Double.isFinite(club.getBudget())
                    || club.getBudget() <= 0) {
                return false;
            }
            checked.add(club);
        }
        return true;
    }

    private String formatBudget(double budget) {
        return budget == (long) budget ? String.valueOf((long) budget) : String.valueOf(budget);
    }

    private Path createTempSibling(Path target, String prefix) throws IOException {
        Path absoluteTarget = target.toAbsolutePath();
        Path parent = absoluteTarget.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
            return Files.createTempFile(parent, prefix, ".tmp");
        }
        return Files.createTempFile(prefix, ".tmp");
    }

    private void replaceTarget(Path temp, Path target) throws IOException {
        try {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ignored) {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void deleteQuietly(Path path) {
        if (path != null) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException exception) {
                LogUtils.logError("Cannot delete temporary club file", exception);
            }
        }
    }
}
