import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Student implements Serializable {
    private static final long serialVersionUID = 1L;
    protected int id;
    protected String firstName;
    protected String lastName;
    protected int birthYear;
    protected transient List<Integer> grades;  
    protected double average;                 
    protected static int nextId=1;

    
    public Student(String firstName, String lastName, int birthYear) 
    {
        this.id=nextId++;
        this.firstName=firstName;
        this.lastName=lastName;
        this.birthYear=birthYear;
        this.grades=new ArrayList<>();
        this.average=0.0;  
    }

    
    private void updateAverage() 
    {
        if (grades.isEmpty()) 
        {
            this.average=0.0;
        } 
        else 
        {
            double sum=0;
            for (int grade : grades) 
            {
                sum+=grade;
            }
            this.average=sum/grades.size();
        }
    }
    
    
    public void setAverage(double average) 
    {
        this.average=average;
    }
    
    
    public int getId() 
    {
        return id;
    }

    
    public String getFirstName() 
    {
        return firstName;
    }

    
    public String getLastName() 
    {
        return lastName;
    }

    
    public int getBirthYear() 
    {
        return birthYear;
    }

    
    public void addGrade(int grade) 
    {
        if (grade>=1 && grade<=5) 
        {
            grades.add(grade);
            updateAverage();  
        } 
        else 
        {
            throw new IllegalArgumentException("Grade is not in 1 - 5 range");
        }
    }
    
    public double getAverage() 
    {
        return average;  
    }

    public abstract void performSkill();

    
    public String toString() 
    {
        return String.format("ID: %d, Name: %s %s, Birth Year: %d, Average grade: %.2f", 
                id, firstName, lastName, birthYear, getAverage());
    }

    
}