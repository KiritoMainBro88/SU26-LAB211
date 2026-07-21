package model;

import java.util.Locale;
import java.util.Objects;

/** Employee entity containing the seven fields defined by the assignment. */
public class Employee implements Identifiable, PayrollCalculable {

    private final String code;
    private String name;
    private Role role;
    private double baseSalary;
    private int workingDays;
    private double bonus;
    private EmployeeStatus status;

    public Employee(String code, String name, Role role,
            double baseSalary, int workingDays, double bonus) {
        this(code, name, role, baseSalary, workingDays, bonus, EmployeeStatus.ACTIVE);
    }

    public Employee(String code, String name, Role role,
            double baseSalary, int workingDays, double bonus, EmployeeStatus status) {
        this.code = code;
        this.name = name;
        this.role = role;
        this.baseSalary = baseSalary;
        this.workingDays = workingDays;
        this.bonus = bonus;
        this.status = status;
    }

    public Employee(Employee other) {
        this(other.getCode(), other.getName(), other.getRole(), other.getBaseSalary(),
                other.getWorkingDays(), other.getBonus(), other.getStatus());
    }

    @Override
    public String getId() {
        return code;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public double getBaseSalary() {
        return baseSalary;
    }

    public int getWorkingDays() {
        return workingDays;
    }

    public double getBonus() {
        return bonus;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public boolean isActive() {
        return status != null && status.isActive();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setBaseSalary(double baseSalary) {
        this.baseSalary = baseSalary;
    }

    public void setWorkingDays(int workingDays) {
        this.workingDays = workingDays;
    }

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }

    /** Calculates salary as base salary multiplied by working days, plus bonus. */
    @Override
    public double calculateSalary() {
        return baseSalary * workingDays + bonus;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Employee)) {
            return false;
        }
        Employee other = (Employee) object;
        return code != null && other.code != null && code.equalsIgnoreCase(other.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code == null ? null : code.toUpperCase(Locale.ROOT));
    }

    @Override
    public String toString() {
        return "Employee{"
                + "code=" + code
                + ", name=" + name
                + ", role=" + (role == null ? "" : role.getDisplayName())
                + ", baseSalary=" + baseSalary
                + ", workingDays=" + workingDays
                + ", bonus=" + bonus
                + ", status=" + (status == null ? "" : status.getDisplayName())
                + '}';
    }
}
