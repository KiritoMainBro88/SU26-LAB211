import businessobject.StudentManagement;
import core.entities.Student;
import dataobject.MountainDAO;
import dataobject.StudentDAO;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Lab1DeepVerification {
    private static int passed=0, failed=0;
    private static void check(String name, boolean ok){ if(ok){passed++; System.out.println("PASS - "+name);}else{failed++; System.out.println("FAIL - "+name);} }
    private static void expectIAE(String name, Runnable r){ try{r.run(); check(name,false);}catch(IllegalArgumentException ex){check(name,true);}catch(Exception ex){check(name,false);} }
    public static void main(String[] args) throws Exception {
        java.nio.file.Path registrationPath = Paths.get("registrations.dat");
        byte[] originalRegistration = Files.exists(registrationPath)
                ? Files.readAllBytes(registrationPath) : null;
        try {
            Files.deleteIfExists(registrationPath);
            expectIAE("null StudentDAO rejected", () -> new StudentManagement(null, new MountainDAO()));
            expectIAE("null MountainDAO rejected", () -> new StudentManagement(new StudentDAO(), null));
            StudentManagement m = new StudentManagement(); m.loadData();
            check("null ID rejected", !m.createStudent(null,"Valid Name","0901234567","a@b.com","MT01",6000000));
            check("null name rejected", !m.createStudent("SE123456",null,"0901234567","a@b.com","MT01",6000000));
            check("null phone rejected", !m.createStudent("SE123456","Valid Name",null,"a@b.com","MT01",6000000));
            check("null email rejected", !m.createStudent("SE123456","Valid Name","0901234567",null,"MT01",6000000));
            check("null mountain rejected", !m.createStudent("SE123456","Valid Name","0901234567","a@b.com",null,6000000));
            check("NaN base fee rejected", !m.createStudent("SE123456","Valid Name","0901234567","a@b.com","MT01",Double.NaN));
            check("Infinity base fee rejected", !m.createStudent("SE123456","Valid Name","0901234567","a@b.com","MT01",Double.POSITIVE_INFINITY));
            check("zero base fee rejected", !m.createStudent("SE123456","Valid Name","0901234567","a@b.com","MT01",0));
            check("negative base fee rejected", !m.createStudent("SE123456","Valid Name","0901234567","a@b.com","MT01",-1));
            check("invalid campus prefix rejected", !m.createStudent("AB123456","Valid Name","0901234567","a@b.com","MT01",6000000));
            check("non-digit student suffix rejected", !m.createStudent("SE12345A","Valid Name","0901234567","a@b.com","MT01",6000000));
            check("invalid 10-digit phone prefix rejected", !m.createStudent("HE123456","Valid Name","0123456789","a@b.com","MT01",6000000));
            check("invalid email rejected", !m.createStudent("HE123456","Valid Name","0901234567","invalid-email","MT01",6000000));
            check("unknown mountain rejected", !m.createStudent("HE123456","Valid Name","0901234567","a@b.com","MT99",6000000));
            check("valid boundary name length 2", m.createStudent("SE123456","An","0901234567","a@b.com","MT01",6000000));
            check("valid boundary name length 20", m.createStudent("HE123456","12345678901234567890","0901234568","c@d.com","MT02",6000000));
            check("name length 21 rejected", !m.createStudent("DE123456","123456789012345678901","0901234569","e@f.com","MT03",6000000));
            check("duplicate ID case insensitive", !m.createStudent("se123456","Another","0901234568","b@c.com","MT01",6000000));
            Student before=m.getStudentById("SE123456");
            check("NaN update fee rejected", !m.updateStudent("SE123456","","","","",Double.NaN));
            check("failed update preserves fee", Math.abs(m.getStudentById("SE123456").getTuitionFee()-before.getTuitionFee())<0.01);
            Student beforeBlankUpdate = m.getStudentById("SE123456");
            check("blank update succeeds", m.updateStudent("SE123456","","","","",null));
            Student afterBlankUpdate = m.getStudentById("SE123456");
            check("blank update preserves name", beforeBlankUpdate.getName().equals(afterBlankUpdate.getName()));
            check("blank update preserves phone", beforeBlankUpdate.getPhone().equals(afterBlankUpdate.getPhone()));
            check("blank update preserves email", beforeBlankUpdate.getEmail().equals(afterBlankUpdate.getEmail()));
            check("blank update preserves mountain", beforeBlankUpdate.getMountainCode().equals(afterBlankUpdate.getMountainCode()));
            check("blank update preserves fee", Math.abs(beforeBlankUpdate.getTuitionFee()-afterBlankUpdate.getTuitionFee())<0.01);
            List<Student> external=m.getAllStudents(); external.get(0).setName("MUTATED");
            check("returned student object defensive copy", !"MUTATED".equals(m.getStudentById("SE123456").getName()));
            check("null search returns empty", m.searchByName(null).isEmpty());
            check("invalid campus filter returns empty", m.filterByCampus("XX").isEmpty());
            check("null delete rejected", !m.deleteStudent(null));

            // malformed serialized list must not partially replace current memory
            List<Object> invalid = new ArrayList<Object>();
            invalid.add(new Student("HE123456","Good Name","0901234567","x@y.com","MT01",6000000));
            invalid.add("not-a-student");
            try(ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream("registrations.dat"))){out.writeObject(invalid);}        
            StudentDAO dao = new StudentDAO();
            dao.add(new Student("CE123456","Keep Me","0901234567","keep@x.com","MT01",6000000));
            check("mixed serialized list rejected", !dao.loadStudents());
            check("mixed serialized list preserves memory", dao.getStudentById("CE123456") != null);

        } finally {
            restoreFile(registrationPath, originalRegistration);
        }
        System.out.println("RESULT: "+passed+" passed, "+failed+" failed");
        if(failed>0) System.exit(1);
    }

    private static void restoreFile(java.nio.file.Path path, byte[] original) throws Exception {
        if (original == null) {
            Files.deleteIfExists(path);
        } else {
            Files.write(path, original);
        }
    }
}
