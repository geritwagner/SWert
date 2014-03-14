package datei_operationen;

public class AlreadyOpenException extends Exception {

	private static final long serialVersionUID = 1L;

	//Parameterless Constructor
    public AlreadyOpenException() {}

    //Constructor that accepts a message
    public AlreadyOpenException(String message)
    {
       super(message);
    }
}
