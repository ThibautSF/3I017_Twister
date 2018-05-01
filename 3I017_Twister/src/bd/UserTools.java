/**
 * 
 */
package bd;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import services.classes.NumberValueException;

/**
 * @author Thibaut SIMON-FINE
 *
 */
public class UserTools {
	
	public static boolean userExists(String login) throws SQLException {
		Connection c = ConnectionTools.getMySQLConnection();
		Statement st = c.createStatement();
		String query = "SELECT * FROM "+DBStatic.TABLE_USER+" WHERE login=\""+login+"\";";
		
		ResultSet rs = st.executeQuery(query);
		
		boolean check = rs.next();
		
		st.close();
		c.close();
		
		return check;
	}
	
	public static boolean userIDExists(int id_user) throws SQLException {
		Connection c = ConnectionTools.getMySQLConnection();
		Statement st = c.createStatement();
		String query = "SELECT * FROM "+DBStatic.TABLE_USER+" WHERE id="+id_user+";";
		
		ResultSet rs = st.executeQuery(query);
		
		boolean check = rs.next();
		
		st.close();
		c.close();
		
		return check;
	}
	
	public static int getIdUser(String login) throws SQLException {
		Connection c = ConnectionTools.getMySQLConnection();
		Statement st = c.createStatement();
		String query = "SELECT id FROM "+DBStatic.TABLE_USER+" WHERE login=\""+login+"\";";
		
		ResultSet rs = st.executeQuery(query);
		
		int id = -1;
		
		if(rs.next())
			id = rs.getInt(1);
		
		st.close();
		c.close();
		
		return id;
	}
	
	public static String getLoginUser(int user_id) throws SQLException {
		Connection c = ConnectionTools.getMySQLConnection();
		Statement st = c.createStatement();
		String query = "SELECT login FROM "+DBStatic.TABLE_USER+" WHERE id=\""+user_id+"\";";
		
		ResultSet rs = st.executeQuery(query);
		
		String login = "";
		
		if(rs.next())
			login = rs.getString(1);
		
		st.close();
		c.close();
		
		return login;
	}

	public static void newUser(String login, String password, String nom, String prenom, int age) throws SQLException, NumberValueException {
		Connection c = ConnectionTools.getMySQLConnection();
		Statement st = c.createStatement();
		
		//Traitement erreurs
		if (age!=-1 && age>122 && age<13) {
			if(age<13) throw new NumberValueException("Vous devez avoir 13 ans pour créer un compte !");
			if(age>122) throw new NumberValueException("Félicitation vous êtes le nouveau doyen de l'humanité !");
		}
		
		//Traitement des champs facultatifs (default NULL)
		String ageStr;
		
		nom = (nom == "") ? "NULL" : "\""+nom+"\"";
		prenom = (prenom == "") ? "NULL" : "\""+prenom+"\"";
		
		ageStr = (age == -1) ? "NULL" : ""+age;
		
		//Création de la query
		String query = "INSERT INTO "+DBStatic.TABLE_USER+" (login, password, nom, prenom, age) "
				+ "VALUES (\""+login+"\", PASSWORD(\""+password+"\"), "+nom+", "+prenom+", "+ageStr+");";
		
		st.executeUpdate(query);
		st.close();
		c.close();
	}
	
	public static boolean checkPassword(String login, String password) throws SQLException {
		Connection c = ConnectionTools.getMySQLConnection();
		Statement st = c.createStatement();
		String query = "SELECT * FROM "+DBStatic.TABLE_USER+" WHERE login=\""+login+"\" AND password=PASSWORD(\""+password+"\");";
		
		ResultSet rs = st.executeQuery(query);
		
		boolean check = rs.next();
		
		st.close();
		c.close();
		
		return check;
	}

	public static JSONArray searchUsers(int user_id, String user_query) throws SQLException, JSONException {
		String[] words = user_query.split(" ");
		
		String regex = "";
		for (int i = 0; i < words.length; i++) {
			if(words[i]!=""){
				regex+=words[i];
				if(i<words.length-1)
					regex+="|";
			}
		}
		
		Connection c = ConnectionTools.getMySQLConnection();
		Statement st = c.createStatement();
		String query = "SELECT id,login FROM "+DBStatic.TABLE_USER+" WHERE LOWER(login) REGEXP \""+regex+"\";";
		
		System.out.println(query);
		
		ResultSet rs = st.executeQuery(query);
		
		JSONArray list_users = new JSONArray();
		
		while(rs.next()){
			JSONObject user = new JSONObject();
			
			int id = rs.getInt(1);
			String login = rs.getString(2);
			
			user.put("id", id);
			user.put("login", login);
			list_users.put(user);
		}
		
		st.close();
		c.close();
		
		return list_users;
	}
	
	
}
