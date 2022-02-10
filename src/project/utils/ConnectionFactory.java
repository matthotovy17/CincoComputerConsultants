/**
 * Author: Matt Hotovy
 * Date: 4/19/2019
 * 
 * This class is the connection factory to the database
 */
package project.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ConnectionFactory {
	
	//This method makes a connection to our Database.
	public static Connection getConnection() {

		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		Connection conn = null;
		try {
			conn = DriverManager.getConnection(DatabaseInfo.url, DatabaseInfo.username, DatabaseInfo.password);
		} catch (SQLException sqle) {
			throw new RuntimeException(sqle);
		}
		return conn;
	}
	
//------------------------------------------------------------------------------------------------------	
	
	//This method closes the connections we opened to the Database.
	public static void closeConnection(Connection conn, PreparedStatement ps, ResultSet rs) {
		
		try {
			if(rs != null && !rs.isClosed())
				rs.close();
			if(ps != null && !ps.isClosed())
				ps.close();
			if(conn != null && !conn.isClosed())
				conn.close();
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return;
	}
	
//--------------------------------------------------------------------------------------------------
	
	//This method is another method that closes the connections to the Database.
	public static void closeConnection(PreparedStatement ps, ResultSet rs) {
		try {
			if(rs != null && !rs.isClosed()) {
				rs.close();
			}
			if(ps != null && !ps.isClosed()) {
				ps.close();
			}
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return;
	}
}
