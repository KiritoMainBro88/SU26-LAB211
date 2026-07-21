package model;

/** Domain entity for a football club. */
public class Club extends BaseEntity {

    private String clubName;
    private String sponsorBrand;
    private double budget;

    public Club(String clubId, String clubName, String sponsorBrand, double budget) {
        super(clubId);
        this.clubName = clubName;
        this.sponsorBrand = sponsorBrand;
        this.budget = budget;
    }

    public Club(Club other) {
        this(other.getId(), other.getClubName(), other.getSponsorBrand(), other.getBudget());
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getSponsorBrand() {
        return sponsorBrand;
    }

    public void setSponsorBrand(String sponsorBrand) {
        this.sponsorBrand = sponsorBrand;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    @Override
    public String toString() {
        return String.format("%-10s | %-28s | %-14s | %8.2f",
                getId(), clubName == null ? "" : clubName,
                sponsorBrand == null ? "" : sponsorBrand, budget);
    }
}
