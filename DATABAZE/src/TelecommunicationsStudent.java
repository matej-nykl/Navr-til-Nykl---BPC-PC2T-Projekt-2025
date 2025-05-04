import java.util.HashMap;
import java.util.Map;

public class TelecommunicationsStudent extends Student {
    
	private static final long serialVersionUID = 1L;

	public TelecommunicationsStudent(String firstName, String lastName, int birthYear) 
	{
        super(firstName, lastName, birthYear);
    }

    
    public void performSkill() 
    {
        System.out.println("Morse for " + firstName + " " + lastName + ":");
        System.out.println(textToMorse(firstName + " " + lastName));
    }

    private String textToMorse(String text) 
    {
        Map<Character, String> morseCode = new HashMap<>();
        	morseCode.put('A', ".-");
        	morseCode.put('B', "-...");
        	morseCode.put('C', "-.-.");
        	morseCode.put('D', "-..");
        	morseCode.put('E', ".");
        	morseCode.put('F', "..-.");
        	morseCode.put('G', "--.");
        	morseCode.put('H', "....");
        	morseCode.put('I', "..");
        	morseCode.put('J', ".---");
        	morseCode.put('K', "-.-");
        	morseCode.put('L', ".-..");
        	morseCode.put('M', "--");
        	morseCode.put('N', "-.");
        	morseCode.put('O', "---");
        	morseCode.put('P', ".--.");
        	morseCode.put('Q', "--.-");
        	morseCode.put('R', ".-.");
        	morseCode.put('S', "...");
        	morseCode.put('T', "-");
        	morseCode.put('U', "..-");
        	morseCode.put('V', "...-");
        	morseCode.put('W', ".--");
        	morseCode.put('X', "-..-");
        	morseCode.put('Y', "-.--");
        	morseCode.put('Z', "--..");
        	morseCode.put(' ', "/");

        StringBuilder morse=new StringBuilder();
        for (char c : text.toUpperCase().toCharArray()) 
        {
            morse.append(morseCode.getOrDefault(c, "")).append(" ");
        }
        return morse.toString();
    }
}