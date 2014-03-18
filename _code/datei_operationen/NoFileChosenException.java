package datei_operationen;

public class NoFileChosenException extends Exception {
	
	private static final long serialVersionUID = 1L;

    public NoFileChosenException() {}

    public NoFileChosenException(String message)
    {
       super(message);
    }
}