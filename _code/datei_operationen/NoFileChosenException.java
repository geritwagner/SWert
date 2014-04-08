package datei_operationen;

/**
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta), Gerit Wagner
 */

public class NoFileChosenException extends Exception {
	
	private static final long serialVersionUID = 1L;

    public NoFileChosenException () {}

    public NoFileChosenException (String message){
       super(message);
    }
}