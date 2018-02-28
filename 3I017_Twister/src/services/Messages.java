/**
 * 
 */
package services;

import java.net.UnknownHostException;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import services.classes.InvalidKeyException;

/**
 * @author tsimonfine
 *
 */
public class Messages {
	public static int DEFAULT_MAX_MESSAGE = 20;
	
	public static JSONObject addMessage(String key, String content) throws JSONException, UnknownHostException, SQLException, InvalidKeyException{
		//1 - Clés null/vide
		if (key == null || key == "")
			return AnswerJSON.defaultJSONError("Erreur de clé", 4);
		
		//2 - Ajouter le message
		bd.MessageTools.addMessage(key, content);
		
		return AnswerJSON.defaultJSONAccept();
	}
	
	public static JSONObject removeMessage(String key, String id_message) throws JSONException, UnknownHostException, SQLException, InvalidKeyException{
		//1 - Clés null/vide
		if (key == null || key == "")
			return AnswerJSON.defaultJSONError("Erreur de clé", 4);
		
		//2 - Retirer le message
		bd.MessageTools.removeMessage(key, id_message);
		
		return AnswerJSON.defaultJSONAccept();	
	}
	
	public static JSONObject getMessage(String key, String id_message) throws JSONException, UnknownHostException, SQLException, InvalidKeyException{
		//1 - Clés null/vide
		if (key == null || key == "")
			return AnswerJSON.defaultJSONError("Erreur de clé", 4);
		
		//2 - id_message null/vide
		if (key == null || key == "")
			return AnswerJSON.defaultJSONError("Pas d'id message", 5);
		
		//3 - Récupérer le message
		JSONObject json = AnswerJSON.defaultJSONAccept();
		JSONObject json_message = bd.MessageTools.getMessageById(key,id_message);
		
		json.put("message", json_message);
		
		return json;
	}
	
	public static JSONObject listMessage(String key) throws JSONException, UnknownHostException, SQLException, InvalidKeyException{
		return listMessage("all", key, DEFAULT_MAX_MESSAGE);
	}
	
	public static JSONObject listMessage(String key, int nb_message) throws JSONException, UnknownHostException, SQLException, InvalidKeyException{
		return listMessage("all", key, nb_message);
	}
	
	public static JSONObject listMessage(String type, String key) throws JSONException, UnknownHostException, SQLException, InvalidKeyException{
		return listMessage(type, key, DEFAULT_MAX_MESSAGE);
	}
	
	public static JSONObject listMessage(String type, String key, int nb_message) throws UnknownHostException, JSONException, SQLException, InvalidKeyException{
		return listMessage(type, key, nb_message, -1);
	}
	
	public static JSONObject listMessage(String type, String key, int nb_message, int id_user) throws JSONException, UnknownHostException, SQLException, InvalidKeyException{
		JSONObject json = AnswerJSON.defaultJSONAccept();
		
		switch (type) {
		case "all":
			json.put("messages", bd.MessageTools.getAllMessages(key, nb_message));
			break;
		
		case "user":
			if(bd.UserTools.userIDExists(id_user)){
				//Trigger mise à jour clé session
				bd.SessionTools.getUserByKey(key);
				
				json.put("messages", bd.MessageTools.getMessagesUser(id_user, nb_message));
			} else
				json.put("messages", bd.MessageTools.getMessagesUser(key, nb_message));
			break;
		
		case "friend":
			json.put("messages", bd.MessageTools.getMessagesFriend(key, nb_message));
			break;

		default:
			//TODO error number
			json = AnswerJSON.defaultJSONError("Type non existant", -1);
			break;
		}
		
		return json;
	}
}
