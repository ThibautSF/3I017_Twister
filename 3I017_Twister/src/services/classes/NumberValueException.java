/**
 * 
 */
package services.classes;

/**
 * @author Thibaut SIMON-FINE
 *
 */
public class NumberValueException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public NumberValueException() {
		this("Valeur donnée incorrecte");
	}

	/**
	 * @param message
	 */
	public NumberValueException(String message) {
		super(message);
	}

}
