/**
 * 
 */
package bd;

import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.GregorianCalendar;

import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import services.classes.InvalidKeyException;

/**
 * @author Thibaut SIMON-FINE
 *
 */
public class MessageTools {

	public static void addMessage(String key, String content) throws SQLException, InvalidKeyException, UnknownHostException {
		int id_user = bd.SessionTools.getUserByKey(key);
		addMessage(id_user, content);
	}
	
	public static void addMessage(int id_user, String content) throws UnknownHostException{
		DBCollection message = ConnectionTools.getMongoCollection("message");
		BasicDBObject query = new BasicDBObject();
		query.put("user_id", id_user);
		query.put("content", content);
		GregorianCalendar c = new GregorianCalendar();
		query.put("date", c.getTime());
		message.insert(query);
	}
	
	public static void removeMessage(String key, String id_message) throws UnknownHostException, SQLException, InvalidKeyException{
		//TODO vérifier utilisateur possède le message !
		bd.SessionTools.getUserByKey(key);
		removeMessage(id_message);
	}
	
	public static void removeMessage(String id_message) throws UnknownHostException{
		DBCollection message = ConnectionTools.getMongoCollection("message");
		BasicDBObject query = new BasicDBObject("_id", new ObjectId(id_message));
		message.remove(query);
	}
	
	public static JSONObject getMessageById(String key, String id_message) throws UnknownHostException, JSONException, SQLException, InvalidKeyException{
		bd.SessionTools.getUserByKey(key);
		return getMessageById(id_message);
	}
	
	public static JSONObject getMessageById(String id_message) throws UnknownHostException, JSONException{
		DBCollection message = ConnectionTools.getMongoCollection("message");
		BasicDBObject query = new BasicDBObject("_id",new ObjectId(id_message));
		DBCursor msg = message.find(query);
		
		JSONObject json = new JSONObject();
		DBObject document = msg.next();
		json.put("user_id", document.get("user_id"));
		json.put("message_id", document.get("_id"));
		json.put("content", document.get("content"));
		
		return json;
	}

	public static JSONArray getMessagesUser(int id_user) throws UnknownHostException, JSONException{
		return getMessagesUser(id_user, 0);
	}
	
	public static JSONArray getMessagesUser(String key, int max_value) throws SQLException, InvalidKeyException, UnknownHostException, JSONException {
		int id_user = bd.SessionTools.getUserByKey(key);
		return getMessagesUser(id_user, max_value);
	}
	
	public static JSONArray getMessagesUser(int id_user, int max_value) throws UnknownHostException, JSONException{
		DBCollection message = ConnectionTools.getMongoCollection("message");
		BasicDBObject query = new BasicDBObject("user_id",id_user);
		DBCursor msg = message.find(query);
		msg.sort(new BasicDBObject("date",-1));
		JSONArray userMessages = new JSONArray();
		
		while(msg.hasNext()){
			JSONObject json = new JSONObject();
			DBObject document = msg.next();
			json.put("message_id", document.get("_id"));
			json.put("content", document.get("content"));
			
			userMessages.put(json);
		}
		
		return userMessages;
	}
	
	public static JSONArray getMessagesUsers(int[] usersId) throws UnknownHostException, JSONException{
		return getMessagesUsers(usersId, 0);
	}
	
	public static JSONArray getMessagesUsers(int[] usersId, int max_value) throws UnknownHostException, JSONException{
		DBCollection message = ConnectionTools.getMongoCollection("message");
		BasicDBObject query = new BasicDBObject();
		query.put("user_id", new BasicDBObject("$in", usersId));
		DBCursor msg = message.find(query);
		msg.sort(new BasicDBObject("date",-1)).limit(max_value);
		JSONArray userMessages = new JSONArray();
		
		while(msg.hasNext()){
			JSONObject json = new JSONObject();
			DBObject document = msg.next();
			json.put("user_id", document.get("user_id"));
			json.put("message_id", document.get("_id"));
			json.put("content", document.get("content"));
			
			userMessages.put(json);
		}
		
		return userMessages;
	}
	
	public static JSONArray getAllMessages(String key, int max_value) throws UnknownHostException, JSONException, SQLException, InvalidKeyException{
		bd.SessionTools.getUserByKey(key);
		return getAllMessages(max_value);
	}
	
	public static JSONArray getAllMessages(int max_value) throws UnknownHostException, JSONException{
		DBCollection message = ConnectionTools.getMongoCollection("message");
		DBCursor msg = message.find();
		msg.sort(new BasicDBObject("date",-1)).limit(max_value);
		JSONArray userMessages = new JSONArray();
		
		while(msg.hasNext()){
			JSONObject json = new JSONObject();
			DBObject document = msg.next();
			json.put("user_id", document.get("user_id"));
			json.put("message_id", document.get("_id"));
			json.put("content", document.get("content"));
			
			userMessages.put(json);
		}
		
		return userMessages;
	}
	
	public static JSONArray getMessagesFriend(String key, int max_value) throws UnknownHostException, JSONException, SQLException, InvalidKeyException{
		int id_user = bd.SessionTools.getUserByKey(key);
		return MessageTools.getMessagesFriend(id_user, max_value);
	}
	
	public static JSONArray getMessagesFriend(int id_user, int max_value) throws UnknownHostException, JSONException, SQLException{
		int[] friendsID = bd.FriendTools.getFriends(id_user);
		return bd.MessageTools.getMessagesUsers(friendsID, max_value);
	}
	
	public static void main(String[] args) {
		try {
			System.out.println("Ajout d'un message (affichage par ordre de date décroissante) : ");
			MessageTools.addMessage(1, "Ce message va être supprimé");
			
			JSONArray array = MessageTools.getMessagesUser(1);
			System.out.println(array);
			
			JSONObject obj = array.getJSONObject(0);
			String message_id = obj.optString("message_id");
			MessageTools.removeMessage(message_id);
			
			System.out.println("Suppression du dernier message ajouté (affichage par ordre de date décroissante) : ");
			array = MessageTools.getMessagesUser(1);
			System.out.println(array);
		} catch (UnknownHostException | JSONException e) {
			e.printStackTrace();
		}
	}
}
