package datei_operationen;

/**
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta), Gerit Wagner
 */

public class SyntaxException extends Exception {
	
	private static final long serialVersionUID = 1L;

    public SyntaxException () {}

    public SyntaxException (String message) {
       super(message);
    }
}