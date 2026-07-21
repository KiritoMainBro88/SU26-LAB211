package core.entities;

import java.io.Serializable;

/** Represents one student registration record without input, validation, or persistence logic. */
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    private String studentId;
    private String name;
    private String phone;
    private String email;
    private String mountainCode;
    private double tuitionFee;

    public Student() {
    }

    public Student(String studentId, String name, String phone, String email,
            String mountainCode, double tuitionFee) {
        this.studentId = studentId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.mountainCode = mountainCode;
        this.tuitionFee = tuitionFee;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMountainCode() {
        return mountainCode;
    }

    public void setMountainCode(String mountainCode) {
        this.mountainCode = mountainCode;
    }

    public double getTuitionFee() {
        return tuitionFee;
    }

    public void setTuitionFee(double tuitionFee) {
        this.tuitionFee = tuitionFee;
    }

    @Override
    public String toString() {
        return "Student{" + "studentId=" + studentId + ", name=" + name
                + ", phone=" + phone + ", email=" + email
                + ", mountainCode=" + mountainCode + ", tuitionFee=" + tuitionFee + '}';
    }
}
