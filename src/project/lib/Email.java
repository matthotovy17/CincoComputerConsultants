/**
 * Author: Matt Hotovy
 * Date: 2/8/2019
 * 
 * This object is for the email data from the person objects
 */
package project.lib;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import project.utils.ConnectionFactory;

public class Email {

	private Integer emailKey;
	private String email;

	public Email(Integer emailKey, String email) {
		this.emailKey = emailKey;
		this.email = email;
	}

	public Email(String email) {
		this.email = email;
	}

	public Email() {
	}

	public int getEmailKey() {
		return emailKey;
	}

	public String getEmail() {
		return email;
	}

	public String toString() {
		return this.email;
	}

//----------------------------------------------------------------------------------------

	// This method takes an emailKey and query's the database and returns the
	// related email on the given key.
	public static Email getEmailByKey(int emailKey) {
		Email email = null;
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		String query = "SELECT e.emailKey, e.email, e.personKey FROM Email e WHERE emailKey = ?";

		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, emailKey);
			rs = ps.executeQuery();
			if (rs.next()) {
				String emailAddress = rs.getString("email");
				email = new Email(emailKey, emailAddress);
			}
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
		return email;
	}

//-------------------------------------------------------------------------------

	// This method takes a personKey and query's the database and returns a list of
	// email's on a given personKey
	public static List<String> getPersonEmailList(int personKey) {
		List<String> emails = new ArrayList<String>();
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		String query = "SELECT email From Email WHERE personKey = ?";
		
		//this try/catch executes the above query to create a list of email's
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, personKey);
			rs = ps.executeQuery();
			while (rs.next()) {
				String email = rs.getString("email");
				emails.add(email);
			}
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
		return emails;
	}
	
//--------------------------------------------------------------------------------------
	
	public static List<String> getEmailListByUuid(String personUuid) {
		List<String> emails = new ArrayList<String>();
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		String query = "SELECT email From Email WHERE personUuid = ?";
		
		//this try/catch executes the above query to create a list of email's
		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, personUuid);
			rs = ps.executeQuery();
			while (rs.next()) {
				String email = rs.getString("email");
				emails.add(email);
			}
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
		return emails;
	}

}
