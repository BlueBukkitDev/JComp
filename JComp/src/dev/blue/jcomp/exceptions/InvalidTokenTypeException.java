package dev.blue.jcomp.exceptions;

/**
 *An exception thrown if a token that is lexxed is not valid. Validity may be determined 
 *by position, spelling, length, or any combination thereof. 
 **/
public class InvalidTokenTypeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidTokenTypeException(String message) {
		super(message);
	}
}
