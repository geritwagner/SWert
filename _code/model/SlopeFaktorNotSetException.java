package model;

public class SlopeFaktorNotSetException extends Exception {

	private static final long serialVersionUID = 1L;

	//Parameterless Constructor
    public SlopeFaktorNotSetException() {}

    //Constructor that accepts a message
    public SlopeFaktorNotSetException(String message)
    {
       super(message);
    }
}
