package businessobject;

import core.entities.Mountain;
import core.entities.StatisticalInfo;
import core.entities.Student;
import java.util.List;

/** Defines the business operations required by the Mountain Hiking assignment. */
public interface IStudentManagement {
    void loadData();
    boolean createStudent(String id, String name, String phone, String email, String mountainCode);
    boolean createStudent(String id, String name, String phone, String email, String mountainCode, double baseTuitionFee);
    boolean updateStudent(String id, String newName, String newPhone, String newEmail, String newMountainCode);
    boolean updateStudent(String id, String newName, String newPhone, String newEmail, String newMountainCode, Double newBaseTuitionFee);
    boolean deleteStudent(String id);
    List<Student> getAllStudents();
    List<Student> searchByName(String keyword);
    List<Student> filterByCampus(String campusCode);
    List<StatisticalInfo> getStatisticsByMountain();
    List<Mountain> getAllMountains();
    boolean isValidMountainCode(String mountainCode);
    String getMountainNameByCode(String mountainCode);
    Student getStudentById(String studentId);
    boolean saveData();
    boolean isSaved();
    String getLastMessage();
}
