/**
 * 
 */
package bd;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

/**
 * @author tsimonfine
 *
 */
public class ConnectionTools {
	private static Database database;
	
	public static Connection getMySQLConnection() throws SQLException {
		if(!DBStatic.mysql_pooling){
			return(DriverManager.getConnection("jdbc:mysql://" + DBStatic.mysql_host + "/" + DBStatic.mysql_db, DBStatic.mysql_username, DBStatic.mysql_password));
		} else {
			if(database==null){
				database = new Database("jdbc/db");
			}
			return(database.getConnection());
		}
	}
	
	/**
	 * @param nom_collection
	 * @return
	 * @throws UnknownHostException
	 */
	public static DBCollection getMongoCollection(String nom_collection) throws UnknownHostException{
		Mongo m = new Mongo(DBStatic.mongo_host);
		DB db = m.getDB(DBStatic.mongo_db);
		return db.getCollection(nom_collection);
	}
	
	public static void main(String[] args) {
		try {
			Connection c = ConnectionTools.getMySQLConnection();
			
			if(c.isValid(1000))
				System.out.println("Ça marche");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
