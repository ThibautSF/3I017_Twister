/**
 * 
 */
package test;

import java.net.UnknownHostException;
import java.sql.SQLException;
import org.json.JSONException;
import services.classes.InvalidKeyException;

/**
 * @author Thibaut SIMON-FINE
 *
 */
public class SearchTest {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.out.println(services.Services.search("8f3356b0790c44ce8b52af172b77bf7d", "No"));
			System.out.println(services.Services.search("8f3356b0790c44ce8b52af172b77bf7d", "message noxi bienvenue"));
		} catch (NumberFormatException | UnknownHostException | JSONException | SQLException | InvalidKeyException e) {
			// TODO Bloc catch généré automatiquement
			e.printStackTrace();
		}
	}

}
