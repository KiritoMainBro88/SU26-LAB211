import file.IFileReadWrite;
import io.EmployeeIO;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import manager.EmployeeManager;
import model.Employee;
import model.EmployeeStatus;
import model.Role;
import utils.CsvUtils;
import validation.EmployeeValidator;
import view.View;

public class Lab3DeepVerification {
    private static int passed=0, failed=0;
    private static void check(String n, boolean ok){if(ok){passed++;System.out.println("PASS - "+n);}else{failed++;System.out.println("FAIL - "+n);}}
    private static void expectIAE(String n, Runnable r){try{r.run();check(n,false);}catch(IllegalArgumentException e){check(n,true);}catch(Exception e){check(n,false);}}
    static class Stub implements IFileReadWrite<Employee>{
        List<Employee> readData;
        public List<Employee> read() { return readData; }
        public boolean write(List<Employee> items) { return true; }
    }
    public static void main(String[] args) throws Exception {
        expectIAE("EmployeeManager null helper rejected", () -> new EmployeeManager(null));
        expectIAE("View null scanner rejected", () -> new View(null));
        expectIAE("EmployeeIO null view rejected", () -> new EmployeeIO(null));
        EmployeeIO io=new EmployeeIO(new View(new Scanner("")));
        expectIAE("inputNewEmployee null manager rejected", () -> io.inputNewEmployee(null));
        expectIAE("inputUpdatedEmployee null current rejected", () -> io.inputUpdatedEmployee(null));
        View yesNoView=new View(new Scanner("maybe\nY\n"));
        check("yes/no input rejects other text then accepts Y", yesNoView.readYesNo("") );
        EmployeeIO idIo=new EmployeeIO(new View(new Scanner("BAD\nE001\n")));
        check("existing employee ID input enforces E+3 digits", "E001".equals(idIo.inputExistingEmployeeId("")));

        Stub stub=new Stub(); EmployeeManager m=new EmployeeManager(stub);
        check("null employee rejected", rejectAdd(m,null));
        check("valid employee added", add(m,new Employee("E001","Alice",Role.DEVELOPER,100,0,0,EmployeeStatus.ACTIVE)));
        check("workingDays 0 accepted", m.getEmployeeById("E001")!=null);
        check("workingDays 26 accepted", add(m,new Employee("E002","Bob",Role.TESTER,100,26,0,EmployeeStatus.INACTIVE)));
        check("workingDays 27 rejected", rejectAdd(m,new Employee("E003","Bad",Role.HR,100,27,0,EmployeeStatus.ACTIVE)));
        check("zero salary rejected", rejectAdd(m,new Employee("E003","Bad",Role.HR,0,1,0,EmployeeStatus.ACTIVE)));
        check("Infinity salary rejected", rejectAdd(m,new Employee("E003","Bad",Role.HR,Double.POSITIVE_INFINITY,1,0,EmployeeStatus.ACTIVE)));
        check("negative bonus rejected", rejectAdd(m,new Employee("E003","Bad",Role.HR,100,1,-1,EmployeeStatus.ACTIVE)));
        check("null role rejected", rejectAdd(m,new Employee("E003","Bad",null,100,1,0,EmployeeStatus.ACTIVE)));
        check("null status rejected", rejectAdd(m,new Employee("E003","Bad",Role.HR,100,1,0,null)));
        check("duplicate id case insensitive rejected", rejectAdd(m,new Employee("e001","Dup",Role.HR,100,1,0,EmployeeStatus.ACTIVE)));
        check("null find returns null", m.getEmployeeById(null)==null);
        check("null search attribute returns empty", m.searchByAttribute(null,"x").isEmpty());
        check("null search query returns empty", m.searchByAttribute("name",null).isEmpty());
        check("blank search returns empty", m.searchByAttribute("name","   ").isEmpty());
        check("unknown search attribute returns empty", m.searchByAttribute("nmae","Alice").isEmpty());
        check("delete null false", !m.removeEmployee(null));
        check("update null false", !m.updateEmployee(null));
        Employee updateAttempt=new Employee("E001","ChangedName",Role.MANAGER,200,25,10,EmployeeStatus.INACTIVE);
        check("required update fields accepted", m.updateEmployee(updateAttempt));
        Employee afterUpdate=m.getEmployeeById("E001");
        check("update preserves employee name", "Alice".equals(afterUpdate.getName()));
        check("update preserves working days", afterUpdate.getWorkingDays()==0);
        check("update changes role salary bonus status", afterUpdate.getRole()==Role.MANAGER
                && afterUpdate.getBaseSalary()==200 && afterUpdate.getBonus()==10
                && afterUpdate.getStatus()==EmployeeStatus.INACTIVE);
        check("status can be updated back to active", m.updateEmployee(new Employee(
                "E001","IgnoredName",Role.MANAGER,200,25,10,EmployeeStatus.ACTIVE)));

        Employee external=m.getEmployeeById("E001"); external.setName("MUTATED");
        check("getEmployeeById defensive entity copy", "Alice".equals(m.getEmployeeById("E001").getName()));
        m.getAllEmployees().get(0).setName("MUTATED2");
        check("getAll deep defensive copy", "Alice".equals(m.getEmployeeById("E001").getName()));
        m.getActiveEmployees().get(0).setName("MUTATED3");
        check("active list deep defensive copy", "Alice".equals(m.getEmployeeById("E001").getName()));

        m.addEmployee(new Employee("E003", "Charlie", Role.HR, 150, 10, 5, EmployeeStatus.ACTIVE));
        int sizeBeforeSoftDelete = m.getAllEmployees().size();
        check("soft delete succeeds for active employee", m.removeEmployee("E003"));
        check("soft delete retains employee record", m.getAllEmployees().size() == sizeBeforeSoftDelete);
        check("soft delete changes status to inactive", m.getEmployeeById("E003").getStatus() == EmployeeStatus.INACTIVE);
        check("soft-deleted employee excluded from active list", !containsEmployee(m.getActiveEmployees(), "E003"));
        check("second soft delete is rejected", !m.removeEmployee("E003"));

        stub.readData=new ArrayList<Employee>();
        stub.readData.add(new Employee("E010","Good",Role.HR,100,1,0,EmployeeStatus.ACTIVE));
        stub.readData.add(new Employee("e010","Dup",Role.TESTER,100,1,0,EmployeeStatus.ACTIVE));
        int before=m.getAllEmployees().size();
        check("duplicate loaded IDs reject whole load", !m.loadFromFile());
        check("failed duplicate load preserves memory", m.getAllEmployees().size()==before);
        stub.readData=null;
        check("null read result rejected", !m.loadFromFile());
        check("null read preserves memory", m.getAllEmployees().size()==before);
        check("Role parse null safe", Role.parse(null)==null);
        check("Status parse null safe", EmployeeStatus.parse(null)==null);
        check("new-employee validation rejects null list", rejectsNullEmployeeList());
        String csvName="Doe, \"Jane\"";
        String csvLine="E099, "+CsvUtils.escape(csvName)+", Developer, 100, 1, 0, active";
        java.util.List<String> csvFields=CsvUtils.parseLine(csvLine);
        check("CSV parser preserves comma and quote in employee name", csvFields.size()==7
                && csvName.equals(csvFields.get(1)));

        // Real file-helper verification in an isolated temporary directory.
        java.nio.file.Path verificationDirectory = java.nio.file.Files.createTempDirectory("lab3-deep-");
        java.nio.file.Path employeePath = verificationDirectory.resolve("employees.txt");
        try {
            java.nio.file.Files.write(employeePath, java.util.Arrays.asList(
                    "E010, Good, Developer, 100, 1, 0, active",
                    "malformed-row",
                    "E010, Duplicate, Tester, 100, 1, 0, active",
                    "E011, \"Doe, Jane\", HR, 120.5, 2, 3.25, inactive"),
                    java.nio.charset.StandardCharsets.UTF_8);
            file.EmployeeFileHelper realHelper = new file.EmployeeFileHelper(employeePath.toString());
            java.util.List<Employee> loaded = realHelper.read();
            check("malformed and duplicate file rows are isolated", loaded.size() == 2);
            Employee quoted = null;
            for (Employee employee : loaded) {
                if ("E011".equals(employee.getCode())) {
                    quoted = employee;
                }
            }
            check("quoted comma name parsed from file", quoted != null && "Doe, Jane".equals(quoted.getName()));
            check("decimal salary and bonus parsed from file", quoted != null
                    && Math.abs(quoted.getBaseSalary() - 120.5) < 0.0001
                    && Math.abs(quoted.getBonus() - 3.25) < 0.0001);
            check("real employee file write succeeds", realHelper.write(loaded));
            java.util.List<Employee> roundTrip = realHelper.read();
            check("employee file round trip preserves valid records", roundTrip.size() == 2);
            String beforeInvalidSave = new String(java.nio.file.Files.readAllBytes(employeePath),
                    java.nio.charset.StandardCharsets.UTF_8);
            java.util.List<Employee> invalidSave = new java.util.ArrayList<Employee>();
            invalidSave.add(new Employee("E099", "Bad", Role.HR, 100, 1, Double.NaN,
                    EmployeeStatus.ACTIVE));
            boolean invalidWriteRejected = false;
            try {
                realHelper.write(invalidSave);
            } catch (Exception exception) {
                invalidWriteRejected = true;
            }
            check("invalid employee save rejected", invalidWriteRejected);
            String afterInvalidSave = new String(java.nio.file.Files.readAllBytes(employeePath),
                    java.nio.charset.StandardCharsets.UTF_8);
            check("rejected save preserves previous target file", beforeInvalidSave.equals(afterInvalidSave));
            java.nio.file.Files.deleteIfExists(employeePath);
            boolean missingReadRejected = false;
            try {
                realHelper.read();
            } catch (Exception exception) {
                missingReadRejected = true;
            }
            check("missing employee file is a failed read", missingReadRejected);
        } finally {
            java.nio.file.Files.deleteIfExists(employeePath);
            java.nio.file.Files.deleteIfExists(verificationDirectory);
        }

        Employee interfaceEmployee = m.getEmployeeById("E001");
        check("Employee implements Identifiable", interfaceEmployee instanceof model.Identifiable
                && "E001".equals(((model.Identifiable) interfaceEmployee).getId()));
        check("Employee implements PayrollCalculable", interfaceEmployee instanceof model.PayrollCalculable
                && ((model.PayrollCalculable) interfaceEmployee).calculateSalary() == interfaceEmployee.calculateSalary());
        Employee copiedEmployee = new Employee(interfaceEmployee);
        copiedEmployee.setName("COPY_ONLY");
        check("Employee copy constructor is independent", !"COPY_ONLY".equals(m.getEmployeeById("E001").getName()));

        System.out.println("RESULT: "+passed+" passed, "+failed+" failed");
        if(failed>0) System.exit(1);
    }
    private static boolean add(EmployeeManager m, Employee e){try{m.addEmployee(e);return true;}catch(Exception ex){return false;}}
    private static boolean rejectAdd(EmployeeManager m, Employee e){return !add(m,e);}
    private static boolean containsEmployee(List<Employee> employees, String code){
        for(Employee employee:employees){if(employee!=null && employee.getCode().equalsIgnoreCase(code)){return true;}}
        return false;
    }
    private static boolean rejectsNullEmployeeList(){
        try{EmployeeValidator.validateNewEmployee(new Employee("E050","X",Role.HR,1,0,0,EmployeeStatus.ACTIVE),null);return false;}
        catch(Exception ex){return true;}
    }

}
