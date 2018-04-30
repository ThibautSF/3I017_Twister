/**
 * 
 */
package services;

import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import services.classes.InvalidKeyException;
import services.classes.NumberValueException;

/**
 * @author Thibaut SIMON-FINE
 *
 */
public class Users {
	
	/**
	 * Créée un utilisateur (avec les informations minimales)
	 * @param login
	 * @param pwd
	 * @return
	 * @throws JSONException 
	 * @throws SQLException 
	 * @throws NumberValueException 
	 */
	public static JSONObject createUser(String login, String pwd) throws JSONException, SQLException, NumberValueException{
		return createUser(login, pwd, "", "", -1);
	}
	
	/**
	 * Créée un utilisateur
	 * @param login
	 * @param pwd
	 * @param nom
	 * @param prenom
	 * @param age
	 * @return
	 * @throws JSONException 
	 * @throws SQLException 
	 * @throws NumberValueException 
	 */
	public static JSONObject createUser(String login, String pwd, String nom, String prenom, int age) throws JSONException, SQLException, NumberValueException{
		//1 - Verification null/vide
		if (login == null || login == "" || pwd == null || pwd == "")
			return AnswerJSON.defaultJSONError("login et/ou password vide", -1);
		
		//2 - Verification user déjà dans BD
		if (bd.UserTools.userExists(login))
			return AnswerJSON.defaultJSONError("User existe", 102);
		
		//3 - Insertion utilisateur
		bd.UserTools.newUser(login, pwd, nom, prenom, age);
		
		return AnswerJSON.defaultJSONAccept();
	}

	
	public static JSONObject login(String login, String pwd, boolean root) throws JSONException, SQLException {
		//1 - Verification null/vide
		if (login == null || login == "" || pwd == null || pwd == "")
			return AnswerJSON.defaultJSONError("login et/ou password vide", 0);
		
		//2 - Verification user dans BD
		if (!bd.UserTools.userExists(login))
			return AnswerJSON.defaultJSONError("User inconnu", 101);
		
		//3 - Verification couple login/password
		if (!bd.UserTools.checkPassword(login, pwd))
			return AnswerJSON.defaultJSONError("Mot de passe incorrect", 103);
		
		//4 - Génération d'une clé de session
		String key = bd.SessionTools.insertSession(login, root);
		int id;
		try {
			id = bd.SessionTools.getUserByKey(key);
		} catch (InvalidKeyException e) {
			return AnswerJSON.defaultJSONError(e.getMessage(), 101);
		}
		
		JSONObject json = AnswerJSON.defaultJSONAccept();
		json.put("key", key);
		json.put("user_id", id);
		json.put("login", login);
		json.put("isroot", root);
		
		return json;
	}
	
	public static JSONObject login(String key) throws JSONException, SQLException {
		try {
			if(bd.SessionTools.isKeyValid(key)){
				int id = bd.SessionTools.getUserByKey(key);
				String login = bd.UserTools.getLoginUser(id);
				boolean root = bd.SessionTools.isKeyRoot(key);
				
				JSONObject json = AnswerJSON.defaultJSONAccept();
				json.put("key", key);
				json.put("user_id", id);
				json.put("login", login);
				json.put("isroot", root);
				
				return json;
			}
		} catch (InvalidKeyException e) {
			return AnswerJSON.defaultJSONError(e.getMessage(), 101);
		}
		
		return AnswerJSON.defaultJSONError("UNKNOWN ERROR", 0);
	}

	public static JSONObject logout(String key) throws JSONException, SQLException {
		//1 - Clés null/vide
		if (key == null || key == "")
			return AnswerJSON.defaultJSONError("Erreur de clé", 4);
		
		//2 - Suppression
		bd.SessionTools.clearSession(key);
		
		return AnswerJSON.defaultJSONAccept();
	}
}
