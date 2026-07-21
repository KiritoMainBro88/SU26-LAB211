package core.entities;

/** Represents participant count and total tuition for one mountain. */
public class StatisticalInfo {

    private String mountainCode;
    private int numberOfStudents;
    private double totalCost;

    public StatisticalInfo() {
    }

    public StatisticalInfo(String mountainCode, int numberOfStudents, double totalCost) {
        this.mountainCode = mountainCode;
        this.numberOfStudents = numberOfStudents;
        this.totalCost = totalCost;
    }

    public String getMountainCode() {
        return mountainCode;
    }

    public void setMountainCode(String mountainCode) {
        this.mountainCode = mountainCode;
    }

    public int getNumberOfStudents() {
        return numberOfStudents;
    }

    public void setNumberOfStudents(int numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public void addStudent(double fee) {
        numberOfStudents++;
        totalCost += fee;
    }
}
