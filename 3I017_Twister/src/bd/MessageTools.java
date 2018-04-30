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

import com.mongodb.BasicDBList;
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

	public static String addMessage(String key, String content) throws SQLException, InvalidKeyException, UnknownHostException {
		int id_user = bd.SessionTools.getUserByKey(key);
		return addMessage(id_user, content);
	}
	
	public static String addMessage(int id_user, String content) throws UnknownHostException {
		DBCollection message = ConnectionTools.getMongoCollection("message");
		BasicDBObject query = new BasicDBObject();
		query.put("user_id", id_user);
		query.put("content", content);
		GregorianCalendar c = new GregorianCalendar();
		query.put("date", c.getTime());
		query.put("comments", new BasicDBList());
		message.insert(query);
		
		ObjectId last_id = (ObjectId) query.get("_id");
		
		return last_id.toString();
	}
	
	public static String addComment(String key, String content, String parent) throws SQLException, InvalidKeyException, UnknownHostException {
		int id_user = bd.SessionTools.getUserByKey(key);
		return addComment(id_user, content, parent);
	}
	
	public static String addComment(int id_user, String content, String parent) throws UnknownHostException {
		DBCollection message = ConnectionTools.getMongoCollection("message");
		BasicDBObject query = new BasicDBObject("_id", new ObjectId(parent));
		DBCursor msg = message.find(query);
		
		if(msg.hasNext()){
			DBObject document = msg.next();
			
			BasicDBObject newMessage = new BasicDBObject();
			newMessage.put("user_id", id_user);
			newMessage.put("content", content);
			GregorianCalendar c = new GregorianCalendar();
			newMessage.put("date", c.getTime());
			newMessage.put("comments", new BasicDBList());
			newMessage.put("parent", parent);
			
			message.insert(newMessage);
			
			ObjectId last_id = (ObjectId) newMessage.get("_id");
			
			BasicDBList comments = (BasicDBList) document.get("comments");
			comments.add(last_id);
			
			message.update(query, new BasicDBObject("$set", new BasicDBObject("comments", comments)));
			
			return last_id.toString();
		}
		
		return "";
	}
	
	public static void removeMessage(String key, String id_message) throws UnknownHostException, SQLException, InvalidKeyException{
		Integer id_user = bd.SessionTools.getUserByKey(key);
		DBCollection message = ConnectionTools.getMongoCollection("message");
		BasicDBObject query = new BasicDBObject("_id", new ObjectId(id_message));
		
		DBCursor msg = message.find(query);
		
		if(msg.hasNext()){
			DBObject document = msg.next();
			
			if(id_user.equals((Integer) document.get("user_id")))
				removeMessage(id_message);
		}
	}
	
	public static void removeMessage(String id_message) throws UnknownHostException {
		DBCollection message = ConnectionTools.getMongoCollection("message");
		BasicDBObject query = new BasicDBObject("_id", new ObjectId(id_message));
		
		DBCursor msg = message.find(query);
		
		if(msg.hasNext()){
			DBObject document = msg.next();
			
			String parent = (String) document.get("parent");
			if(parent!=null){
				//remove comment in parent
				BasicDBObject queryParent = new BasicDBObject("_id", new ObjectId(parent));
				DBCursor msgParent = message.find(queryParent);
				
				if(msgParent.hasNext()){
					DBObject documentParent = msgParent.next();
					
					BasicDBList comments = (BasicDBList) documentParent.get("comments");
					comments.remove(new ObjectId(id_message));
					
					message.update(queryParent, new BasicDBObject("$set", new BasicDBObject("comments", comments)));
				}
			}
		}
		
		message.remove(query);
	}
	
	public static JSONObject getMessageById(String key, String id_message) throws UnknownHostException, JSONException, SQLException, InvalidKeyException {
		bd.SessionTools.getUserByKey(key);
		return getMessageById(id_message, true);
	}
	
	public static JSONObject getMessageById(String id_message) throws UnknownHostException, JSONException, SQLException {
		return getMessageById(id_message, true);
	}
	
	public static JSONObject getMessageById(String id_message, boolean escParent) throws UnknownHostException, JSONException, SQLException {
		DBCollection message = ConnectionTools.getMongoCollection("message");
		BasicDBObject query = new BasicDBObject("_id",new ObjectId(id_message));
		DBCursor msg = message.find(query);
		
		JSONObject json = new JSONObject();
		JSONObject auteur = new JSONObject();
		DBObject document = msg.next();
		auteur.put("user_id", document.get("user_id"));
		auteur.put("login", bd.UserTools.getLoginUser(Integer.parseInt(document.get("user_id").toString())));
		json.put("author", auteur);
		json.put("message_id", document.get("_id"));
		json.put("content", document.get("content"));
		json.put("date", document.get("date"));
		
		JSONArray comments = new JSONArray();
		BasicDBList commentIds = (BasicDBList) document.get("comments");
		if(commentIds!=null){
			for (Object one_comment : commentIds) {
				ObjectId message_id = (ObjectId) one_comment;
				comments.put(getMessageById(message_id.toString(),false));
			}
		}
		json.put("comments", comments);
		
		if(document.get("parent")!=null){
			String idparent = (String) document.get("parent");
			json.put("parent", idparent);
			if(escParent){
				JSONObject message_parent = getMessageById(idparent, false);
				json.put("parent_author", message_parent.get("author"));
			}
		}
		
		return json;
	}

	public static JSONArray getMessagesUser(int id_user) throws UnknownHostException, JSONException, SQLException {
		return getMessagesUser(id_user, 0);
	}
	
	public static JSONArray getMessagesUser(String key, int max_value) throws SQLException, InvalidKeyException, UnknownHostException, JSONException {
		int id_user = bd.SessionTools.getUserByKey(key);
		return getMessagesUser(id_user, max_value);
	}
	
	public static JSONArray getMessagesUser(int id_user, int max_value) throws UnknownHostException, JSONException, SQLException{
		DBCollection message = ConnectionTools.getMongoCollection("message");
		BasicDBObject query = new BasicDBObject("user_id",id_user);
		DBCursor msg = message.find(query);
		msg.sort(new BasicDBObject("date",-1));
		JSONArray userMessages = new JSONArray();
		
		while(msg.hasNext()){
			JSONObject json = new JSONObject();
			JSONObject auteur = new JSONObject();
			DBObject document = msg.next();
			auteur.put("user_id", document.get("user_id"));
			auteur.put("login", bd.UserTools.getLoginUser(Integer.parseInt(document.get("user_id").toString())));
			json.put("author", auteur);
			json.put("message_id", document.get("_id"));
			json.put("content", document.get("content"));
			json.put("date", document.get("date"));
			
			JSONArray comments = new JSONArray();
			BasicDBList commentIds = (BasicDBList) document.get("comments");
			if(commentIds!=null){
				for (Object one_comment : commentIds) {
					ObjectId message_id = (ObjectId) one_comment;
					comments.put(getMessageById(message_id.toString(),false));
				}
			}
			json.put("comments", comments);
			
			if(document.get("parent")!=null){
				String idparent = (String) document.get("parent");
				json.put("parent", idparent);
				JSONObject message_parent = getMessageById(idparent, false);
				json.put("parent_author", message_parent.get("author"));
			}
			
			userMessages.put(json);
		}
		
		return userMessages;
	}
	
	public static JSONArray getMessagesUsers(int[] usersId) throws UnknownHostException, JSONException, SQLException {
		return getMessagesUsers(usersId, 0);
	}
	
	public static JSONArray getMessagesUsers(int[] usersId, int max_value) throws UnknownHostException, JSONException, SQLException {
		DBCollection message = ConnectionTools.getMongoCollection("message");
		BasicDBObject query = new BasicDBObject();
		query.put("user_id", new BasicDBObject("$in", usersId));
		DBCursor msg = message.find(query);
		msg.sort(new BasicDBObject("date",-1)).limit(max_value);
		JSONArray userMessages = new JSONArray();
		
		while(msg.hasNext()){
			JSONObject json = new JSONObject();
			JSONObject auteur = new JSONObject();
			DBObject document = msg.next();
			auteur.put("user_id", document.get("user_id"));
			auteur.put("login", bd.UserTools.getLoginUser(Integer.parseInt(document.get("user_id").toString())));
			json.put("author", auteur);
			json.put("message_id", document.get("_id"));
			json.put("content", document.get("content"));
			json.put("date", document.get("date"));
			
			JSONArray comments = new JSONArray();
			BasicDBList commentIds = (BasicDBList) document.get("comments");
			if(commentIds!=null){
				for (Object one_comment : commentIds) {
					ObjectId message_id = (ObjectId) one_comment;
					comments.put(getMessageById(message_id.toString(),false));
				}
			}
			json.put("comments", comments);
			
			if(document.get("parent")!=null){
				String idparent = (String) document.get("parent");
				json.put("parent", idparent);
				JSONObject message_parent = getMessageById(idparent, false);
				json.put("parent_author", message_parent.get("author"));
			}
			
			userMessages.put(json);
		}
		
		return userMessages;
	}
	
	public static JSONArray getAllMessages(String key, int max_value) throws UnknownHostException, JSONException, SQLException, InvalidKeyException {
		bd.SessionTools.getUserByKey(key);
		return getAllMessages(max_value);
	}
	
	public static JSONArray getAllMessages(int max_value) throws UnknownHostException, JSONException, SQLException {
		DBCollection message = ConnectionTools.getMongoCollection("message");
		DBCursor msg = message.find();
		msg.sort(new BasicDBObject("date",-1)).limit(max_value);
		JSONArray userMessages = new JSONArray();
		
		while(msg.hasNext()){
			JSONObject json = new JSONObject();
			JSONObject auteur = new JSONObject();
			DBObject document = msg.next();
			
			if(document.get("parent")!=null) continue;
			
			auteur.put("user_id", document.get("user_id"));
			auteur.put("login", bd.UserTools.getLoginUser(Integer.parseInt(document.get("user_id").toString())));
			json.put("author", auteur);
			json.put("message_id", document.get("_id"));
			json.put("content", document.get("content"));
			json.put("date", document.get("date"));
			
			JSONArray comments = new JSONArray();
			BasicDBList commentIds = (BasicDBList) document.get("comments");
			if(commentIds!=null){
				for (Object one_comment : commentIds) {
					ObjectId message_id = (ObjectId) one_comment;
					comments.put(getMessageById(message_id.toString(),false));
				}
			}
			json.put("comments", comments);
			
			userMessages.put(json);
		}
		
		return userMessages;
	}
	
	public static JSONArray getMessagesFriend(String key, int max_value) throws UnknownHostException, JSONException, SQLException, InvalidKeyException {
		int id_user = bd.SessionTools.getUserByKey(key);
		return MessageTools.getMessagesFriend(id_user, max_value);
	}
	
	public static JSONArray getMessagesFriend(int id_user, int max_value) throws UnknownHostException, JSONException, SQLException {
		int[] friendsID = bd.FriendTools.getFriends(id_user);
		return bd.MessageTools.getMessagesUsers(friendsID, max_value);
	}
	
	public static void main(String[] args) {
		try {
			System.out.println("Ajout d'un message (affichage par ordre de date décroissante) : ");
			MessageTools.addMessage(1, "Ce message va être supprimé");
			
			JSONArray array = MessageTools.getMessagesUser(1);
			System.out.println(array);
			
			//Get message id
			JSONObject obj = array.getJSONObject(0);
			String message_id = obj.optString("message_id");
			
			System.out.println("Ajout d'un comment (affichage par ordre de date décroissante) : ");
			MessageTools.addComment(1, "ce message est cool !", message_id);
			
			array = MessageTools.getMessagesUser(1);
			System.out.println(array);
			
			MessageTools.removeMessage(message_id);
			obj = array.getJSONObject(0);
			message_id = obj.optString("message_id");
			
			System.out.println("Suppression du dernier message ajouté (affichage par ordre de date décroissante) : ");
			MessageTools.removeMessage(message_id);
			array = MessageTools.getMessagesUser(1);
			System.out.println(array);
		} catch (UnknownHostException | JSONException | SQLException e) {
			e.printStackTrace();
		}
	}
}
