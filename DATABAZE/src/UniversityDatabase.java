import java.io.*;
import java.util.*;

public class UniversityDatabase {
    private static Scanner scanner = new Scanner(System.in);
    private static StudentDatabase database = new StudentDatabase();

    public static void main(String[] args) {
        boolean running = true;
        
        while (running) 
        {
            printMenu();
            int choice = getIntInput("Enter number: ");
            scanner.nextLine(); 
            
            try {
                switch (choice) 
                {
                    case 1 -> addNewStudent();
                    case 2 -> addGradeToStudent();
                    case 3 -> removeStudent();
                    case 4 -> findStudentById();
                    case 5 -> performStudentSkill();
                    case 6 -> listAllStudents();
                    case 7 -> showDepartmentAverages();
                    case 8 -> showStudentCounts();
                    case 9 -> saveStudentToFile();
                    case 10 -> loadStudentFromFile();
                    case 0 -> 
                    {
                        running = false;
                        System.out.println("Closing program");
                    }
                    default -> System.out.println("Wrong number.");
                }
            } 
            catch (Exception e) 
            {
                System.out.println("Error: " + e.getMessage());
            }
            
            if (running) 
            {
                System.out.println("\nPress Enter to continue");
                scanner.nextLine();
            }
        }
        
        database.close();
        
    }

    private static void printMenu() 
    {
        System.out.println("\nDatabase Menu:");
        System.out.println("1. Add new student");
        System.out.println("2. Add grade to student");
        System.out.println("3. Remove student");
        System.out.println("4. Find student by ID");
        System.out.println("5. Student´s skill");
        System.out.println("6. List all students");
        System.out.println("7. Program averages");
        System.out.println("8. Number of students in programs");
        System.out.println("9. Save student to file");
        System.out.println("10. Load student from file");
        System.out.println("0. Exit");
    }

    private static int getIntInput(String prompt) 
    {
        while (true) 
        {
            System.out.print(prompt);
            try 
            {
                return scanner.nextInt();
            } 
            catch (InputMismatchException e) 
            {
                System.out.println("Wrong number.");
                scanner.nextLine(); 
            }
        }
    }

    private static void addNewStudent() 
    {
        System.out.println("\nAdd New Student:");
        System.out.println("1. Telecom Student");
        System.out.println("2. Cybersecurity Student");
        int dept = getIntInput("Select program: ");
        scanner.nextLine(); 

        System.out.print("first name: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("last name: ");
        String lastName = scanner.nextLine().trim();

        int birthYear = getIntInput("birth year: ");
        scanner.nextLine(); 

        Student student;
        if (dept == 1) 
        {
            student = new TelecommunicationsStudent(firstName, lastName, birthYear);
        } 
        else if (dept == 2) 
        {
            student = new CybersecurityStudent(firstName, lastName, birthYear);
        } 
        else 
        {
            throw new IllegalArgumentException("Wrong program selection");
        }

        database.addStudent(student);
        System.out.printf("Student added. ID: %d%n", student.getId());
    }

    private static void addGradeToStudent() 
    {
        int id = getIntInput("\nEnter student ID: ");
        int grade = getIntInput("Enter grade (1-5): ");
        scanner.nextLine(); 

        if (database.addGrade(id, grade)) 
        {
            System.out.println("Grade added.");
        } 
        else 
        {
            System.out.println("Student not found.");
        }
    }

    private static void removeStudent() 
    {
        int id = getIntInput("\nEnter student ID: ");
        scanner.nextLine(); 

        if (database.removeStudent(id)) 
        {
            System.out.println("Student removed.");
        } 
        else 
        {
            System.out.println("Student not found.");
        }
    }

    private static void findStudentById() 
    {
        int id = getIntInput("\nEnter student ID: ");
        scanner.nextLine(); 

        Student student = database.findStudentById(id);
        if (student != null) 
        {
            System.out.println("\nStudent Details:");
            System.out.println(student);
        } 
        else 
        {
            System.out.println("Student not found.");
        }
    }

    private static void performStudentSkill() 
    {
        int id = getIntInput("\nEnter student ID: ");
        scanner.nextLine(); 

        Student student = database.findStudentById(id);
        if (student != null) 
        {
            System.out.println("\nProgram special skill:");
            student.performSkill();
        } 
        else 
        {
            System.out.println("Student not found.");
        }
    }

    private static void listAllStudents() 
    {
        System.out.println("\nAll Students:");
        
        List<Student> allStudents = new ArrayList<>(database.getAllStudents());
        allStudents.sort(Comparator.comparing(Student::getLastName));

        System.out.println("\nTelecommunications Students = ");
        allStudents.stream()
                .filter(s -> s instanceof TelecommunicationsStudent)
                .forEach(System.out::println);

        System.out.println("\nCybersecurity Students = ");
        allStudents.stream()
                .filter(s -> s instanceof CybersecurityStudent)
                .forEach(System.out::println);
    }

    private static void showDepartmentAverages() 
    {
        System.out.println("\nProgram Averages = ");
        System.out.printf("Telecommunications: %.2f%n", database.getTelecommunicationsAverage());
        System.out.printf("Cybersecurity: %.2f%n", database.getCybersecurityAverage());
    }

    private static void showStudentCounts() 
    {
        System.out.println("\nNumber of students in programs =");
        System.out.println("Telecommunications: " + database.getTelecommunicationsStudents().size());
        System.out.println("Cybersecurity: " + database.getCybersecurityStudents().size());
    }

    private static void saveStudentToFile() 
    {
        int id = getIntInput("\nEnter student ID: ");
        scanner.nextLine();
        
        System.out.print("Enter filename (or path): ");
        String filename = scanner.nextLine().trim();

        try {
            database.saveToFile(id, filename);
            System.out.println("Student saved to file.");
        } 
        catch (IOException e) 
        {
            System.out.println("Error saving student: " + e.getMessage());
        }
    }

    private static void loadStudentFromFile() {
        System.out.print("\nEnter filename to load: ");
        String filename = scanner.nextLine().trim();

        try 
        {
            Student student = database.loadFromFile(filename);
            database.addStudent(student);
            System.out.printf("Student loaded from file. New ID: %d%n", student.getId());
        } 
        catch (IOException e) 
        {
            System.out.println("File error: " + e.getMessage());
        } 
        catch (ClassNotFoundException e) 
        {
            System.out.println("Invalid student file format.");
        }
    }
}
