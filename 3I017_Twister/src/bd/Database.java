/**
 * 
 */
package bd;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author Thibaut SIMON-FINE
 *
 */
public class Database {
	private DataSource dataSource;
	
	/**
	 * @param jndiname
	 * @throws SQLException
	 */
	public Database(String jndiname) throws SQLException {
		try {
			dataSource = (DataSource) new InitialContext().lookup("java:comp/env/" + jndiname);
		} catch (NamingException e) {
			// Handle error that it's not configured in JNDI.
			throw new SQLException(jndiname + " is missing in JNDI! : " + e.getMessage());
		}
	}
	
	/**
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
}
