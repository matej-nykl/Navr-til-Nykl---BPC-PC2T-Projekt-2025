import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CybersecurityStudent extends Student {
   
	private static final long serialVersionUID = 1L;

	public CybersecurityStudent(String firstName, String lastName, int birthYear) 
	{
        super(firstName, lastName, birthYear);
    }

    
    public void performSkill() 
    {
        System.out.println("Hash for " + firstName + " " + lastName + ":");
        System.out.println(calculateHash(firstName + " " + lastName));
    }

    private String calculateHash(String input) 
    {
        try 
        {
            MessageDigest digest=MessageDigest.getInstance("SHA-256");
            byte[] hash=digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) 
            {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } 
        catch (NoSuchAlgorithmException e) 
        {
            throw new RuntimeException(e);
        }
    }
}