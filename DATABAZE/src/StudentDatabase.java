import java.io.*;
import java.sql.*;
import java.util.*;

public class StudentDatabase {
    private List<Student> students;
    private Connection dbConnection;

    public StudentDatabase() {
        students = new ArrayList<>();
        connectToDatabase();
        loadFromDatabase();
    }

    private void connectToDatabase() 
    {
        try 
        {
            dbConnection = DriverManager.getConnection("jdbc:sqlite:students.db");
            createTables();
        } 
        catch (SQLException e) 
        {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }

    private void createTables() throws SQLException 
    {
        String createStudentsTable = "CREATE TABLE IF NOT EXISTS students (" +
                "id INTEGER PRIMARY KEY, " +
                "firstName TEXT, " +
                "lastName TEXT, " +
                "birthYear INTEGER, " +
                "type TEXT)";
        
        String createGradesTable = "CREATE TABLE IF NOT EXISTS grades (" +
                "studentId INTEGER, " +
                "grade INTEGER, " +
                "FOREIGN KEY(studentId) REFERENCES students(id))";

        try (Statement stmt = dbConnection.createStatement()) 
        {
            stmt.execute(createStudentsTable);
            stmt.execute(createGradesTable);
        }
    }

    public void addStudent(Student student) 
    {
        students.add(student);
    }

    public boolean addGrade(int studentId, int grade) 
    {
        Student student = findStudentById(studentId);
        if (student != null) 
        {
            student.addGrade(grade);
            return true;
        }
        return false;
    }

    public boolean removeStudent(int studentId) 
    {
        Iterator<Student> iterator = students.iterator();
        while (iterator.hasNext()) 
        {
            Student student = iterator.next();
            if (student.getId() == studentId) 
            {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public Student findStudentById(int studentId) 
    {
        for (Student student : students) 
        {
            if (student.getId() == studentId) 
            {
                return student;
            }
        }
        return null;
    }

    public List<Student> getAllStudents() 
    {
        return new ArrayList<>(students);
    }

    public List<Student> getTelecommunicationsStudents() 
    {
        List<Student> result = new ArrayList<>();
        for (Student student : students) 
        {
            if (student instanceof TelecommunicationsStudent) 
            {
                result.add(student);
            }
        }
        return result;
    }

    public List<Student> getCybersecurityStudents() 
    {
        List<Student> result = new ArrayList<>();
        for (Student student : students) 
        {
            if (student instanceof CybersecurityStudent) 
            {
                result.add(student);
            }
        }
        return result;
    }

    public double getTelecommunicationsAverage() 
    {
        List<Student> telecomStudents = getTelecommunicationsStudents();
        if (telecomStudents.isEmpty()) 
        {
            return 0.0;
        }
        double sum = 0;
        for (Student student : telecomStudents) 
        {
            sum += student.getAverage();
        }
        return sum / telecomStudents.size();
    }

    public double getCybersecurityAverage() 
    {
        List<Student> cyberStudents = getCybersecurityStudents();
        if (cyberStudents.isEmpty()) 
        {
            return 0.0;
        }
        double sum = 0;
        for (Student student : cyberStudents) 
        {
            sum += student.getAverage();
        }
        return sum / cyberStudents.size();
    }

    public void saveToFile(int studentId, String filename) throws IOException 
    {
        Student student = findStudentById(studentId);
        if (student == null) 
        {
            throw new IllegalArgumentException("Student not found");
        }
        try {
            StudentFileHandler.saveToFile(student, filename);
        } catch (IOException e) {
            throw new IOException("Failed to save student to file: " + e.getMessage());
        }
    }

    public Student loadFromFile(String filename) throws IOException, ClassNotFoundException 
    {
        try 
        {
            Student student = StudentFileHandler.loadFromFile(filename);
            
            
            Student newStudent;
            if (student instanceof TelecommunicationsStudent) 
            {
                newStudent = new TelecommunicationsStudent(
                    student.getFirstName(),
                    student.getLastName(),
                    student.getBirthYear()
                );
            } else {
                newStudent = new CybersecurityStudent(
                    student.getFirstName(),
                    student.getLastName(),
                    student.getBirthYear()
                );
            }

            
            newStudent.setAverage(student.getAverage());
            
            return newStudent;
        } catch (ClassCastException e) 
        {
            throw new IOException("Invalid student data in file");
        }
    }



    public void saveToDatabase() 
    {
        try 
        {
            dbConnection.setAutoCommit(false);
            
            try (Statement stmt = dbConnection.createStatement()) 
            {
                stmt.execute("DELETE FROM students");
                stmt.execute("DELETE FROM grades");
            }

            String insertStudentSQL = "INSERT INTO students (id, firstName, lastName, birthYear, type) VALUES (?, ?, ?, ?, ?)";
            String insertGradeSQL = "INSERT INTO grades (studentId, grade) VALUES (?, ?)";

            try (PreparedStatement studentStmt = dbConnection.prepareStatement(insertStudentSQL);
                 PreparedStatement gradeStmt = dbConnection.prepareStatement(insertGradeSQL)) 
            {

                for (Student student : students) 
                {
                    studentStmt.setInt(1, student.getId());
                    studentStmt.setString(2, student.getFirstName());
                    studentStmt.setString(3, student.getLastName());
                    studentStmt.setInt(4, student.getBirthYear());
                    studentStmt.setString(5, (student instanceof TelecommunicationsStudent) ? "telecom" : "cyber");
                    studentStmt.executeUpdate();

                    for (int grade : student.grades) {
                        gradeStmt.setInt(1, student.getId());
                        gradeStmt.setInt(2, grade);
                        gradeStmt.executeUpdate();
                    }
                }
                dbConnection.commit();
            }
        } 
        catch (SQLException e) 
        {
            try 
            {
                dbConnection.rollback();
                System.err.println("Transaction rolled back: " + e.getMessage());
            } 
            catch (SQLException ex) 
            {
                System.err.println("Rollback failed: " + ex.getMessage());
            }
        } 
        finally 
        {
            try 
            {
                dbConnection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Failed to reset auto-commit: " + e.getMessage());
            }
        }
    }

    private void loadFromDatabase() 
    {
        try 
        {
            dbConnection.setAutoCommit(false);
            
            String selectStudentsSQL = "SELECT * FROM students";
            String selectGradesSQL = "SELECT grade FROM grades WHERE studentId = ?";

            try (Statement stmt = dbConnection.createStatement();
                 ResultSet rs = stmt.executeQuery(selectStudentsSQL);
                 PreparedStatement gradeStmt = dbConnection.prepareStatement(selectGradesSQL)) 
            {

                while (rs.next()) 
                {
                    int id = rs.getInt("id");
                    String firstName = rs.getString("firstName");
                    String lastName = rs.getString("lastName");
                    int birthYear = rs.getInt("birthYear");
                    String type = rs.getString("type");

                    Student student;
                    if ("telecom".equals(type)) 
                    {
                        student = new TelecommunicationsStudent(firstName, lastName, birthYear);
                    } 
                    else 
                    {
                        student = new CybersecurityStudent(firstName, lastName, birthYear);
                    }

                    gradeStmt.setInt(1, id);
                    try (ResultSet gradeRs = gradeStmt.executeQuery()) 
                    {
                        while (gradeRs.next()) 
                        {
                            student.addGrade(gradeRs.getInt("grade"));
                        }
                    }

                    students.add(student);
                    Student.nextId = Math.max(Student.nextId, id + 1);
                }
                dbConnection.commit();
            }
        } 
        catch (SQLException e) 
        {
            try 
            {
                dbConnection.rollback();
                System.err.println("Transaction rolled back: " + e.getMessage());
            } 
            catch (SQLException ex) 
            {
                System.err.println("Rollback failed: " + ex.getMessage());
            }
        } 
        finally 
        {
            try 
            {
                dbConnection.setAutoCommit(true);
            } 
            catch (SQLException e) 
            {
                System.err.println("Failed to reset auto-commit: " + e.getMessage());
            }
        }
    }

    public void close() 
    {
        saveToDatabase();
        try 
        {
            if (dbConnection != null) 
            {
                dbConnection.close();
            }
        } 
        catch (SQLException e) {
        	
            System.err.println("Error closing database: " + e.getMessage());
        }
    }
}