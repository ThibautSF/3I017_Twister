/**
 * 
 */
package bd;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import services.classes.InvalidKeyException;

/**
 * @author tsimonfine
 *
 */
public class FriendTools {
	
	public static void addFriend(String key, int id_friend) throws SQLException, InvalidKeyException{
		int id_user = SessionTools.getUserByKey(key);
		addFriend(id_user, id_friend);
	}
	
	public static void addFriend(int id_user, int id_friend) throws SQLException{
		Connection c = ConnectionTools.getMySQLConnection();
		Statement st = c.createStatement();
		
		String query = "INSERT INTO "+DBStatic.TABLE_FRIEND+" (id_user, id_friend) "
				+ "VALUES ("+id_user+", "+id_friend+")";
		
		st.executeUpdate(query);
		
		st.close();
		c.close();
	}
	
	public static void removeFriend(String key, int id_friend) throws SQLException, InvalidKeyException{
		int id_user = SessionTools.getUserByKey(key);
		removeFriend(id_user, id_friend);
	}

	public static void removeFriend(int id_user, int id_friend) throws SQLException {
		Connection c = ConnectionTools.getMySQLConnection();
		Statement st = c.createStatement();
		
		String query = "DELETE FROM "+DBStatic.TABLE_FRIEND+" WHERE id_user="+id_user+" AND id_friend="+id_friend+";";
		
		st.executeUpdate(query);
		
		st.close();
		c.close();
	}
	
	public static JSONArray listFriend(String key) throws SQLException, InvalidKeyException, JSONException{
		int id_user = SessionTools.getUserByKey(key);
		return listFriend(id_user);
	}

	private static JSONArray listFriend(int id_user) throws SQLException, JSONException {
		Connection c = ConnectionTools.getMySQLConnection();
		Statement st = c.createStatement();
		
		String query = "SELECT U.* FROM "+DBStatic.TABLE_FRIEND+" AS F JOIN "
				+DBStatic.TABLE_USER+" AS U ON F.id_friend=U.id WHERE id_user="+id_user+";";
		
		ResultSet rs = st.executeQuery(query);
		
		JSONArray userFriends = new JSONArray();
		
		while (rs.next()) {
			JSONObject json = new JSONObject();
			
			json.put("user_id", rs.getInt("id"));
			json.put("login", rs.getString("login"));
			
			userFriends.put(json);
		}
		
		return userFriends;
	}

	public static int[] getFriends(int id_user) throws SQLException {
		Connection c = ConnectionTools.getMySQLConnection();
		Statement st = c.createStatement();
		
		String query = "SELECT id_friend FROM "+DBStatic.TABLE_FRIEND+" WHERE id_user="+id_user;
		
		ResultSet rs = st.executeQuery(query);
		
		List<Integer> friends = new ArrayList<Integer>();
		
		while (rs.next()) {
			friends.add(rs.getInt("id_friend"));
		}
		
		int[] friendsArray = friends.stream().mapToInt(Integer::intValue).toArray();
		
		st.close();
		c.close();
		
		return friendsArray;
	}

}
