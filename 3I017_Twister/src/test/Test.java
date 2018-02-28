/**
 * 
 */
package test;

import java.net.UnknownHostException;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import services.classes.InvalidKeyException;
import services.classes.NumberValueException;

/**
 * @author Thibaut SIMON-FINE
 *
 */
public class Test {

	public static void main(String[] args) {
		String login = "thibaut";
		String pwd = "monMdp";
		String nom = "Lulamoon";
		String prenom = "Trixie";
		int age = 30;
		boolean root = false;
		
		try {
			System.out.println("CreateUser :");
			JSONObject jsonCreateUser = services.Users.createUser(login, pwd, nom, prenom, age);
			System.out.println(jsonCreateUser.toString());
			
			System.out.println("Login :");
			JSONObject jsonLogin = services.Users.login(login, pwd, root);
			System.out.println(jsonLogin.toString());
			
			String key = jsonLogin.getString("key");
			
			System.out.println("Get Messages :");
			JSONObject jsonMessage = services.Messages.listMessage("user", key, 0);
			System.out.println(jsonMessage.toString());
			
			System.out.println("Get Friends :");
			JSONObject jsonFriend = services.Friends.getFriend(key);
			System.out.println(jsonFriend.toString());
			
			System.out.println("Logout :");
			JSONObject jsonLogout = services.Users.logout(key);
			System.out.println(jsonLogout.toString());
		} catch (JSONException | SQLException | UnknownHostException e) {
			e.printStackTrace();
		} catch (NumberValueException e) {
			System.out.println(e.getMessage());
		} catch (InvalidKeyException e) {
			System.out.println(e.getMessage());
		}
	}
}
