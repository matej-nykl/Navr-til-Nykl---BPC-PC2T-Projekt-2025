import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class StudentFileHandler {
    public static void saveToFile(Student student, String filename) throws IOException {
        if (student == null || filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid input");
        }

        
        Path path = Paths.get(filename);
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) 
        {
            Files.createDirectories(parent);
        }

        
        try (ObjectOutputStream oos = new ObjectOutputStream(
             new BufferedOutputStream(
             new FileOutputStream(filename)
             ))) 
        {
            oos.writeObject(student);
        }
    }

    public static Student loadFromFile(String filename) throws IOException, ClassNotFoundException 
    {
        if (filename == null || filename.trim().isEmpty()) 
        {
            throw new IllegalArgumentException("Filename cannot be empty");
        }

        Path file = Paths.get(filename);
        if (!Files.exists(file)) 
        {
            throw new FileNotFoundException("File does not exist: " + filename);
        }

        try (ObjectInputStream ois = new ObjectInputStream(
             new BufferedInputStream(
             new FileInputStream(filename)))) 
        {
            Student student = (Student) ois.readObject();
            
            
            student.grades = new ArrayList<>();
            
            return student;
        } catch (StreamCorruptedException e) 
        {
            throw new IOException("File is corrupted or not a valid student file", e);
        }
    }
}