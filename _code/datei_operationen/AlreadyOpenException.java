package datei_operationen;

public class AlreadyOpenException extends Exception {

	private static final long serialVersionUID = 1L;

    public AlreadyOpenException() {}

    public AlreadyOpenException(String message)
    {
       super(message);
    }
}