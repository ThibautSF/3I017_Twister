/**
 * 
 */
package services.classes;

/**
 * @author tsimonfine
 *
 */
public class InvalidKeyException extends Exception { //TODO RuntimeException ????
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public InvalidKeyException() {
		this("La clé est invalide ! Déconnexion.");
	}

	/**
	 * @param message
	 */
	public InvalidKeyException(String message) {
		super(message);
	}

}
