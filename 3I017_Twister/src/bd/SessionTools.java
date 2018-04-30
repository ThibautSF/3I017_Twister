/**
 * 
 */
package bd;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.UUID;

import services.classes.InvalidKeyException;

/**
 * @author Thibaut SIMON-FINE
 *
 */
public class SessionTools {
	public static int SESSION_TIMEOUT = 3600*1000;
	public static boolean SESSION_AUTO_UPDATE = true;

	public static int getUserByKey(String key) throws SQLException, InvalidKeyException {
		if(isKeyValid(key)){
			Connection c = ConnectionTools.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT id_user FROM "+DBStatic.TABLE_SESSION+" WHERE skey=\""+key+"\";";
			
			ResultSet rs = st.executeQuery(query);
			
			if(rs.next()){
				int id = rs.getInt(1);
				
				st.close();
				c.close();
				
				if(SESSION_AUTO_UPDATE) updateSession(key);
				
				return id;
			}
			
			st.close();
			c.close();
		}

		throw new InvalidKeyException("La clé est inconnue !");
	}

	public static void clearPreviousSession(int user_id, boolean deleteRoot) throws SQLException {
		Connection c = ConnectionTools.getMySQLConnection();
		Statement st = c.createStatement();
		String query;
		if(deleteRoot)
			query = "DELETE FROM "+DBStatic.TABLE_SESSION+" WHERE id_user="+user_id+";";
		else
			query = "DELETE FROM "+DBStatic.TABLE_SESSION+" WHERE id_user="+user_id+" AND root=false;";
		
		st.executeUpdate(query);
		st.close();
		c.close();
	}
	
	public static void clearSession(String key) throws SQLException {
		Connection c = ConnectionTools.getMySQLConnection();
		Statement st = c.createStatement();
		String query;
		
		query = "DELETE FROM "+DBStatic.TABLE_SESSION+" WHERE skey=\""+key+"\";";
		
		st.executeUpdate(query);
		st.close();
		c.close();
	}
	
	public static String insertSession(String login, boolean isRoot) throws SQLException {
		//Récupération de l'id user
		int user_id = UserTools.getIdUser(login);
		
		//Suppression des anciennes sessions
		clearPreviousSession(user_id,true);
		
		//Génération clé random, test si existence, regénération tant que la clé existe
		String key = UUID.randomUUID().toString().replace("-", "");
		while (checkKey(key)) {
			key = UUID.randomUUID().toString().replace("-", "");
		}
		
		//Creation de la session
		Connection c = ConnectionTools.getMySQLConnection();
		Statement st = c.createStatement();
		
		String query = "INSERT INTO "+DBStatic.TABLE_SESSION+" (skey, id_user, sdate, root) "
				+ "VALUES ( \""+key+"\", "+user_id+", NOW(), "+isRoot+")";
		
		st.executeUpdate(query);
		
		st.close();
		c.close();
		
		return key;
	}
	
	public static boolean checkKey(String key) throws SQLException {
		Connection c = ConnectionTools.getMySQLConnection();
		Statement st = c.createStatement();
		String query = "SELECT * FROM "+DBStatic.TABLE_SESSION+" WHERE skey=\""+key+"\";";
		
		ResultSet rs = st.executeQuery(query);
		
		boolean check = rs.next();
		
		st.close();
		c.close();
		
		return check;
	}
	
	public static boolean isKeyValid(String key) throws SQLException, InvalidKeyException {
		Connection c = ConnectionTools.getMySQLConnection();
		Statement st = c.createStatement();
		String query = "SELECT * FROM "+DBStatic.TABLE_SESSION+" WHERE skey=\""+key+"\";";
		ResultSet rs = st.executeQuery(query);
		
		if(rs.next()){
			//Session root
			if(rs.getBoolean("root")){
				
				st.close();
				c.close();
				
				return true;
			}
			
			//Session normale (avec timeout)
			Date dt_now = new Date();
			long now = dt_now.getTime();
			long date = rs.getTimestamp("sdate").getTime();
			
			st.close();
			c.close();
			
			if (now-date<SESSION_TIMEOUT)
				return true;
			else
				throw new InvalidKeyException("Clé expirée");
		}
		
		throw new InvalidKeyException();
	}
	
	public static void updateSession(String key) throws SQLException {
		Connection c = ConnectionTools.getMySQLConnection();
		Statement st = c.createStatement();
		
		String query = "UPDATE "+DBStatic.TABLE_SESSION+" SET sdate=CURRENT_TIMESTAMP() WHERE skey=\""+key+"\";";
		
		st.executeUpdate(query);
		
		st.close();
		c.close();
	}
	
	public static boolean isKeyRoot(String key) throws SQLException {
		Connection c = ConnectionTools.getMySQLConnection();
		Statement st = c.createStatement();
		String query = "SELECT * FROM "+DBStatic.TABLE_SESSION+" WHERE skey=\""+key+"\";";
		ResultSet rs = st.executeQuery(query);
		
		if(rs.next()){
			if(rs.getBoolean("root")){
				
				st.close();
				c.close();
				
				return true;
			}
		}
		
		return false;
	}

}
