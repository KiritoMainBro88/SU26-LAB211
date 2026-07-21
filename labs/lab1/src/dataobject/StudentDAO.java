package dataobject;

import core.entities.Student;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Set;
import utilities.DataValidation;
import utilities.LogUtils;

/** Manages registration records and persists them in registrations.dat. */
public class StudentDAO {

    private static final String FILE_NAME = "registrations.dat";
    private final List<Student> students;
    private boolean saved;

    public StudentDAO() {
        students = new ArrayList<Student>();
        saved = true;
    }

    public boolean loadStudents() {
        File file = new File(FILE_NAME);
        if (!file.exists() || file.length() == 0) {
            students.clear();
            saved = true;
            return true;
        }

        List<Student> loadedStudents = new ArrayList<Student>();
        Set<String> loadedIds = new HashSet<String>();
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(file))) {
            Object data = input.readObject();
            if (!(data instanceof List<?>)) {
                return false;
            }
            for (Object item : (List<?>) data) {
                if (!(item instanceof Student)) {
                    return false;
                }
                Student student = (Student) item;
                if (!isValidStudentData(student)) {
                    return false;
                }
                String normalizedId = student.getStudentId().trim().toUpperCase(Locale.ROOT);
                if (!loadedIds.add(normalizedId)) {
                    return false;
                }
                loadedStudents.add(copyStudent(student));
            }
            students.clear();
            students.addAll(loadedStudents);
            saved = true;
            return true;
        } catch (EOFException ex) {
            LogUtils.logError("StudentDAO.loadStudents", ex);
            return false;
        } catch (IOException ex) {
            LogUtils.logError("StudentDAO.loadStudents", ex);
            return false;
        } catch (ClassNotFoundException ex) {
            LogUtils.logError("StudentDAO.loadStudents", ex);
            return false;
        }
    }

    public boolean saveStudents() {
        if (!isValidStudentList(students)) {
            return false;
        }
        File target = new File(FILE_NAME).getAbsoluteFile();
        File parent = target.getParentFile();
        File temporary = null;
        try {
            temporary = File.createTempFile("registrations-", ".tmp", parent);
            try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(temporary))) {
                output.writeObject(copyStudents(students));
            }
            moveReplacing(temporary, target);
            saved = true;
            return true;
        } catch (IOException ex) {
            LogUtils.logError("StudentDAO.saveStudents", ex);
            return false;
        } finally {
            if (temporary != null && temporary.exists() && !temporary.delete()) {
                temporary.deleteOnExit();
            }
        }
    }

    public List<Student> getAll() {
        return copyStudents(students);
    }

    public Student getStudentById(String studentId) {
        Student student = findStudentByIdInternal(studentId);
        return student == null ? null : copyStudent(student);
    }

    public boolean isDuplicatedId(String studentId) {
        return findStudentByIdInternal(studentId) != null;
    }

    public boolean add(Student student) {
        if (!isValidStudentData(student) || isDuplicatedId(student.getStudentId())) {
            return false;
        }
        students.add(copyStudent(student));
        saved = false;
        return true;
    }

    public boolean update(Student updatedStudent) {
        if (!isValidStudentData(updatedStudent)) {
            return false;
        }
        ListIterator<Student> iterator = students.listIterator();
        while (iterator.hasNext()) {
            Student current = iterator.next();
            if (current.getStudentId().equalsIgnoreCase(updatedStudent.getStudentId())) {
                iterator.set(copyStudent(updatedStudent));
                saved = false;
                return true;
            }
        }
        return false;
    }

    public boolean deleteById(String studentId) {
        Student student = findStudentByIdInternal(studentId);
        if (student == null) {
            return false;
        }
        students.remove(student);
        saved = false;
        return true;
    }

    public List<Student> searchByName(String keyword) {
        List<Student> result = new ArrayList<Student>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return result;
        }
        String searchKey = keyword.trim().toLowerCase(Locale.ROOT);
        for (Student student : students) {
            if (student.getName().toLowerCase(Locale.ROOT).contains(searchKey)) {
                result.add(copyStudent(student));
            }
        }
        return result;
    }

    public List<Student> filterByCampus(String campusCode) {
        List<Student> result = new ArrayList<Student>();
        if (!DataValidation.isValidCampusCode(campusCode)) {
            return result;
        }
        String campus = campusCode.trim().toUpperCase(Locale.ROOT);
        for (Student student : students) {
            if (student.getStudentId().toUpperCase(Locale.ROOT).startsWith(campus)) {
                result.add(copyStudent(student));
            }
        }
        return result;
    }

    public boolean isEmpty() {
        return students.isEmpty();
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    private Student findStudentByIdInternal(String studentId) {
        if (studentId == null) {
            return null;
        }
        String id = studentId.trim();
        for (Student student : students) {
            if (student.getStudentId().equalsIgnoreCase(id)) {
                return student;
            }
        }
        return null;
    }

    private boolean isValidStudentList(List<Student> data) {
        if (data == null) {
            return false;
        }
        Set<String> ids = new HashSet<String>();
        for (Student student : data) {
            if (!isValidStudentData(student)) {
                return false;
            }
            String normalizedId = student.getStudentId().trim().toUpperCase(Locale.ROOT);
            if (!ids.add(normalizedId)) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidStudentData(Student student) {
        return student != null
                && DataValidation.isValidStudentId(student.getStudentId())
                && DataValidation.isValidStudentName(student.getName())
                && DataValidation.isValidPhone(student.getPhone())
                && DataValidation.isValidEmail(student.getEmail())
                && DataValidation.isValidMountainCodeInput(student.getMountainCode())
                && Double.isFinite(student.getTuitionFee())
                && student.getTuitionFee() > 0;
    }

    private List<Student> copyStudents(List<Student> source) {
        List<Student> copies = new ArrayList<Student>();
        for (Student student : source) {
            copies.add(copyStudent(student));
        }
        return copies;
    }

    private Student copyStudent(Student student) {
        return new Student(student.getStudentId(), student.getName(), student.getPhone(),
                student.getEmail(), student.getMountainCode(), student.getTuitionFee());
    }

    private void moveReplacing(File source, File target) throws IOException {
        try {
            Files.move(source.toPath(), target.toPath(),
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ignored) {
            Files.move(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
