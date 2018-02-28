/**
 * 
 */
package services.classes;

/**
 * @author tsimonfine
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
