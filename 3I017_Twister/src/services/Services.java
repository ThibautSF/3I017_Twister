/**
 * 
 */
package services;

import java.net.UnknownHostException;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import services.classes.Commons;
import services.classes.InvalidKeyException;

/**
 * @author Thibaut SIMON-FINE
 *
 */
public class Services {
	
	public static JSONObject search(String key, String query) throws JSONException, SQLException, InvalidKeyException, NumberFormatException, UnknownHostException {
		//1 - Clés null/vide
		if (key == null || key == "")
			return AnswerJSON.defaultJSONError("Erreur de clé", 4);
		
		//2 - Clé valide
		int id_user = bd.SessionTools.getUserByKey(key);
		
		query = Commons.getOnlyStrings(query).toLowerCase();
		
		//3 - Chercher parmi les users
		JSONArray json_users = bd.UserTools.searchUsers(id_user, query);
		
		//4 - Chercher parmi les messages
		JSONArray json_messages = bd.MessageTools.searchMessages(id_user,query);
		
		JSONObject json = AnswerJSON.defaultJSONAccept();
		json.put("users", json_users);
		json.put("messages", json_messages);
		
		return json;
	}

}
