/**
 * Author: Matt Hotovy
 * Date: 2/8/2019
 * 
 * This object is for the country data from the person and address data
 */
package project.lib;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import project.utils.ConnectionFactory;

public class Country {

	private int countryKey;
	private String countryName;

	public Country(int countryKey, String countryName) {
		this.countryKey = countryKey;
		this.countryName = countryName;
	}
	
	//Constructor for reading from csv files in the file reader class
	public Country(String countryName) {
		this.countryName = countryName;
	}

	public Country() {
	}

	public int getCountryKey() {
		return countryKey;
	}

	public String getCountryName() {
		return countryName;
	}

	@Override
	public String toString() {
		return this.countryName;
	}

//-----------------------------------------------------------------------------------

	// This method takes a countryKey and query's the database on that key to return the related country.
	public static Country getCountryByKey(int countryKey) {
		Country country = null;
		Connection conn = ConnectionFactory.getConnection();
		String query = "SELECT countryKey, countryName FROM Country WHERE countryKey = ?";

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, countryKey);
			rs = ps.executeQuery();
			if (rs.next()) {
				String countryName = rs.getString("countryName");
				country = new Country(countryKey, countryName);
			} else {
				throw new IllegalStateException("No such country in database with id = " + countryKey);
			}
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
		return country;
	}

}
