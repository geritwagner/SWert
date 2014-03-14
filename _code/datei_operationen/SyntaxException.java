package datei_operationen;

public class SyntaxException extends Exception {
	
	private static final long serialVersionUID = 1L;

	//Parameterless Constructor
    public SyntaxException() {}

    //Constructor that accepts a message
    public SyntaxException(String message)
    {
       super(message);
    }
}
