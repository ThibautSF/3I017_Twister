package test;

import java.net.UnknownHostException;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bd.MessageTools;

public class CreateSampleMessages {

	public static void main(String[] args) {
		
		try {
			MessageTools.addMessage(1, "Voilà mon premier message");
			
			JSONArray array = MessageTools.getMessagesUser(1);
			JSONObject obj = array.getJSONObject(0);
			String message_id = obj.optString("message_id");
			
			Thread.sleep(1000);
			
			MessageTools.addComment(2, "Enfin inscrit !? :)", message_id);
			Thread.sleep(1000);
			MessageTools.addComment(3, "Bienvenue !", message_id);
			
			array = MessageTools.getMessagesUser(3);
			obj = array.getJSONObject(0);
			message_id = obj.optString("message_id");
			
			Thread.sleep(1000);
			
			MessageTools.addComment(1, "Merci :D", message_id);
			Thread.sleep(1000);
			MessageTools.addMessage(1, "Aujourd'hui je me suis inscrit ici");
			Thread.sleep(1000);
			MessageTools.addMessage(2, "J'ai passé ma Fête du Travail à travailler !");
			Thread.sleep(1000);
			
			array = MessageTools.getMessagesUser(2);
			obj = array.getJSONObject(0);
			message_id = obj.optString("message_id");
			
			MessageTools.addComment(1, "Pas cool ça, mais parfois on est bien obligé :/", message_id);
			Thread.sleep(2000);
			MessageTools.addComment(3, "Courrage :)", message_id);
			
			System.out.println("Done");
		} catch (UnknownHostException | JSONException | SQLException | InterruptedException e) {
			// TODO Bloc catch généré automatiquement
			e.printStackTrace();
		}
	}

}
