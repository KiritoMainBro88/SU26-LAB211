package businessobject;

import core.entities.Mountain;
import core.entities.StatisticalInfo;
import core.entities.Student;
import dataobject.MountainDAO;
import dataobject.StudentDAO;
import utilities.DataValidation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Coordinates registration business rules between the presentation layer and data access objects. */
public class StudentManagement implements IStudentManagement {

    public static final double DEFAULT_TUITION_FEE = 6000000;
    private static final double DISCOUNT_RATE = 0.35;

    private final StudentDAO studentDAO;
    private final MountainDAO mountainDAO;
    private String lastMessage;

    public StudentManagement() {
        this(new StudentDAO(), new MountainDAO());
    }

    public StudentManagement(StudentDAO studentDAO, MountainDAO mountainDAO) {
        if (studentDAO == null || mountainDAO == null) {
            throw new IllegalArgumentException("DAO dependencies cannot be null.");
        }
        this.studentDAO = studentDAO;
        this.mountainDAO = mountainDAO;
        this.lastMessage = "";
    }

    public void loadData() {
        boolean registrationsLoaded = studentDAO.loadStudents();
        boolean mountainsLoaded = mountainDAO.loadMountains();
        if (registrationsLoaded && mountainsLoaded) {
            lastMessage = "Data loaded successfully.";
        } else if (!mountainsLoaded) {
            lastMessage = "Cannot load MountainList.csv. Existing in-memory data was preserved.";
        } else {
            lastMessage = "Cannot load registrations.dat. Existing in-memory data was preserved.";
        }
    }

    public double calculateTuitionFee(String phone) {
        return calculateTuitionFee(phone, DEFAULT_TUITION_FEE);
    }

    public double calculateTuitionFee(String phone, double baseTuitionFee) {
        double validBaseFee = Double.isFinite(baseTuitionFee) && baseTuitionFee > 0
                ? baseTuitionFee : DEFAULT_TUITION_FEE;
        if (DataValidation.isDiscountPhone(phone)) {
            return validBaseFee * (1 - DISCOUNT_RATE);
        }
        return validBaseFee;
    }

    public boolean createStudent(String id, String name, String phone, String email,
            String mountainCode) {
        return createStudent(id, name, phone, email, mountainCode, DEFAULT_TUITION_FEE);
    }

    public boolean createStudent(String id, String name, String phone, String email,
            String mountainCode, double baseTuitionFee) {
        String normalizedId = DataValidation.normalizeStudentId(id);
        String normalizedMountainCode = MountainDAO.normalizeMountainCode(mountainCode);

        if (!DataValidation.isValidStudentId(normalizedId)) {
            lastMessage = "Student ID is invalid.";
            return false;
        }
        if (!DataValidation.isValidStudentName(name)) {
            lastMessage = "Student name must be from 2 to 20 characters.";
            return false;
        }
        if (!DataValidation.isValidPhone(phone)) {
            lastMessage = "Phone number must contain 10 digits and use a supported Vietnamese prefix.";
            return false;
        }
        if (!DataValidation.isValidEmail(email)) {
            lastMessage = "Email is invalid.";
            return false;
        }
        if (!DataValidation.isValidMountainCodeInput(normalizedMountainCode)
                || !mountainDAO.isValidMountainCode(normalizedMountainCode)) {
            lastMessage = "Mountain code does not exist.";
            return false;
        }
        if (studentDAO.isDuplicatedId(normalizedId)) {
            lastMessage = "Student ID already exists.";
            return false;
        }
        if (!Double.isFinite(baseTuitionFee) || baseTuitionFee <= 0) {
            lastMessage = "Base tuition fee must be a finite positive number.";
            return false;
        }

        Student student = new Student(normalizedId, name.trim(), phone.trim(), email.trim(),
                normalizedMountainCode, calculateTuitionFee(phone, baseTuitionFee));
        boolean added = studentDAO.add(student);
        lastMessage = added ? "New registration added successfully." : "Cannot add registration.";
        return added;
    }

    public boolean updateStudent(String id, String newName, String newPhone,
            String newEmail, String newMountainCode) {
        return updateStudent(id, newName, newPhone, newEmail, newMountainCode, null);
    }

    public boolean updateStudent(String id, String newName, String newPhone,
            String newEmail, String newMountainCode, Double newBaseTuitionFee) {
        String normalizedId = DataValidation.normalizeStudentId(id);
        Student current = studentDAO.getStudentById(normalizedId);
        if (current == null) {
            lastMessage = "This student has not registered yet.";
            return false;
        }

        String finalName = chooseNewValue(newName, current.getName());
        String finalPhone = chooseNewValue(newPhone, current.getPhone());
        String finalEmail = chooseNewValue(newEmail, current.getEmail());
        String finalMountainCode = chooseNewValue(newMountainCode, current.getMountainCode());
        finalMountainCode = MountainDAO.normalizeMountainCode(finalMountainCode);

        if (!DataValidation.isValidStudentName(finalName)) {
            lastMessage = "Student name must be from 2 to 20 characters.";
            return false;
        }
        if (!DataValidation.isValidPhone(finalPhone)) {
            lastMessage = "Phone number must contain 10 digits and use a supported Vietnamese prefix.";
            return false;
        }
        if (!DataValidation.isValidEmail(finalEmail)) {
            lastMessage = "Email is invalid.";
            return false;
        }
        if (!mountainDAO.isValidMountainCode(finalMountainCode)) {
            lastMessage = "Mountain code does not exist.";
            return false;
        }

        double finalTuitionFee = current.getTuitionFee();
        boolean phoneChanged = newPhone != null && !newPhone.trim().isEmpty();
        if (newBaseTuitionFee != null) {
            if (!Double.isFinite(newBaseTuitionFee.doubleValue()) || newBaseTuitionFee.doubleValue() <= 0) {
                lastMessage = "Base tuition fee must be a finite positive number.";
                return false;
            }
            finalTuitionFee = calculateTuitionFee(finalPhone, newBaseTuitionFee.doubleValue());
        } else if (phoneChanged) {
            finalTuitionFee = calculateTuitionFee(finalPhone, getBaseTuitionFee(current));
        }

        Student updated = new Student(current.getStudentId(), finalName.trim(), finalPhone.trim(),
                finalEmail.trim(), finalMountainCode, finalTuitionFee);
        boolean updatedResult = studentDAO.update(updated);
        lastMessage = updatedResult ? "Registration updated successfully." : "Cannot update registration.";
        return updatedResult;
    }

    public boolean deleteStudent(String id) {
        boolean deleted = studentDAO.deleteById(DataValidation.normalizeStudentId(id));
        lastMessage = deleted ? "Registration deleted successfully." : "This student has not registered yet.";
        return deleted;
    }

    public List<Student> getAllStudents() {
        return studentDAO.getAll();
    }

    public List<Student> searchByName(String keyword) {
        return studentDAO.searchByName(keyword);
    }

    public List<Student> filterByCampus(String campusCode) {
        return studentDAO.filterByCampus(campusCode);
    }

    public List<StatisticalInfo> getStatisticsByMountain() {
        Map<String, StatisticalInfo> statisticsMap = new LinkedHashMap<String, StatisticalInfo>();
        List<Student> students = studentDAO.getAll();
        for (Student student : students) {
            String code = student.getMountainCode();
            StatisticalInfo info = statisticsMap.get(code);
            if (info == null) {
                info = new StatisticalInfo(code, 0, 0);
                statisticsMap.put(code, info);
            }
            info.addStudent(student.getTuitionFee());
        }
        return new ArrayList<StatisticalInfo>(statisticsMap.values());
    }

    
    public List<Mountain> getAllMountains() {
        return mountainDAO.getAll();
    }

    public boolean isValidMountainCode(String mountainCode) {
        return mountainDAO.isValidMountainCode(mountainCode);
    }

    public String getMountainNameByCode(String mountainCode) {
        Mountain mountain = mountainDAO.getMountainByCode(mountainCode);
        if (mountain == null) {
            return mountainCode;
        }
        return mountain.getMountain();
    }

    public boolean saveData() {
        boolean saved = studentDAO.saveStudents();
        lastMessage = saved ? "Registration data has been successfully saved to registrations.dat."
                : "Cannot save registration data.";
        return saved;
    }

    public boolean isSaved() {
        return studentDAO.isSaved();
    }

    public boolean isEmpty() {
        return studentDAO.isEmpty();
    }

    public Student getStudentById(String id) {
        return studentDAO.getStudentById(DataValidation.normalizeStudentId(id));
    }

    public String getLastMessage() {
        return lastMessage;
    }

    private String chooseNewValue(String newValue, String oldValue) {
        if (newValue == null || newValue.trim().isEmpty()) {
            return oldValue;
        }
        return newValue;
    }

    private double getBaseTuitionFee(Student student) {
        if (student != null && DataValidation.isDiscountPhone(student.getPhone())) {
            return student.getTuitionFee() / (1 - DISCOUNT_RATE);
        }
        return student == null ? DEFAULT_TUITION_FEE : student.getTuitionFee();
    }
}
