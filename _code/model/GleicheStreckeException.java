package model;

public class GleicheStreckeException extends Exception {
	
	private static final long serialVersionUID = 1L;

	//Parameterless Constructor
    public GleicheStreckeException() {}

    //Constructor that accepts a message
    public GleicheStreckeException(String message)
    {
       super(message);
    }
}
