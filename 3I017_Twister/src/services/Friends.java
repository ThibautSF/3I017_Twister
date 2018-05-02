/**
 * 
 */
package services;

import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import services.classes.InvalidKeyException;

/**
 * @author Thibaut SIMON-FINE
 *
 */
public class Friends {
	
	public static JSONObject addFriend(String key, int id_friend) throws JSONException, SQLException, InvalidKeyException {
		//1 - Clés null/vide
		if (key == null || key == "")
			return AnswerJSON.defaultJSONError("Erreur de clé", 4);
		
		//2 - Verifier id user friend existe
		if (bd.UserTools.userIDExists(id_friend)){
			//3 - Ajouter ami
			bd.FriendTools.addFriend(key, id_friend);
			
			JSONObject json = AnswerJSON.defaultJSONAccept();
			json.put("login", bd.UserTools.getLoginUser(id_friend));
			
			return json;
		}
		
		//TODO erreur id
		return AnswerJSON.defaultJSONError("ID user non existant", 101);
	}
	
	public static JSONObject removeFriend(String key, int id_friend) throws JSONException, SQLException, InvalidKeyException {
		//1 - Clés null/vide
		if (key == null || key == "")
			return AnswerJSON.defaultJSONError("Erreur de clé", 4);
		
		//2 - Enlever ami
		bd.FriendTools.removeFriend(key, id_friend);
			
		return AnswerJSON.defaultJSONAccept();
	}
	
	public static JSONObject getFriend(String key) throws JSONException, SQLException, InvalidKeyException {
		//1 - Clés null/vide
		if (key == null || key == "")
			return AnswerJSON.defaultJSONError("Erreur de clé", 4);
		
		//2 - Récupérer le message
		JSONObject json = AnswerJSON.defaultJSONAccept();
		
		json.put("friends", bd.FriendTools.listFriend(key));
		json.put("waitApproved", bd.FriendTools.listWaitApproved(key));
		
		return json;
	}
	
	public static JSONObject approveOrNotFriend(String key, int id_friend, String action) throws JSONException, SQLException, InvalidKeyException {
		//1 - Clés null/vide
		if (key == null || key == "")
			return AnswerJSON.defaultJSONError("Erreur de clé", 4);
		
		//2 - Verifier id user friend existe
		if (bd.UserTools.userIDExists(id_friend)){
			JSONObject json;
			//1 - Approuver ou non ami
			if(action.equals("accept")){
				bd.FriendTools.approveFriend(key, id_friend);
				json = AnswerJSON.defaultJSONAccept();
				json.put("login", bd.UserTools.getLoginUser(id_friend));
			} else if (action.equals("refuse")){
				int id_user = bd.SessionTools.getUserByKey(key);
				bd.FriendTools.removeFriend(id_friend, id_user);
				json = AnswerJSON.defaultJSONAccept();
				json.put("login", bd.UserTools.getLoginUser(id_friend));
			} else {
				json = AnswerJSON.defaultJSONError("action inconnue", 100);
			}
			
			return json;
		}
		
		return AnswerJSON.defaultJSONError("ID user non existant", 101);
	}
}
