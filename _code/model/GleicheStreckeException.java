package model;

public class GleicheStreckeException extends Exception {
	
    //Parameterless Constructor
    public GleicheStreckeException() {}

    //Constructor that accepts a message
    public GleicheStreckeException(String message)
    {
       super(message);
    }
}
