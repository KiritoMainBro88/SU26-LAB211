package presentation;

import businessobject.StudentManagement;

/** Starts the Mountain Hiking Challenge Registration application. */
public class Program {

    public static void main(String[] args) {
        try {
            StudentManagement studentManagement = new StudentManagement();
            studentManagement.loadData();
            Menu.manageStudentRegistration(studentManagement);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
