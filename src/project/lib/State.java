/**
 * Author: Matt Hotovy
 * Date Created: 2/8/2019
 * Last Updated: 5/23/2019
 * 
 * This object is for the product data
 */
package project.lib;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import project.utils.ConnectionFactory;

public class State {

	private int stateKey;
	private String stateName;
	private Country country;

	public State(int stateKey, String stateName, Country country) {
		this.stateKey = stateKey;
		this.stateName = stateName;
		this.country = country;
	}
	
	//Constructor for reading from csv files from the file reader class
	public State(String stateName, Country country) {
		this.stateName = stateName;
		this.country = country;
	}

	public State() {
	}

	public int getStateKey() {
		return stateKey;
	}

	public String getStateName() {
		return stateName;
	}

	public Country getCountry() {
		return country;
	}

	@Override
	public String toString() {
		return this.stateName +" "+ this.country;
	}
	
//-------------------------------------------------------------
	
	//This method takes a stateKey, query's the database and returns the corresponding state.
	public static State getStateByKey(int stateKey) {
		State state = null;
		Connection conn = ConnectionFactory.getConnection();
		
		String query = "SELECT stateKey, stateName, countryKey FROM State WHERE stateKey = ?";

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, stateKey);
			rs = ps.executeQuery();
			if (rs.next()) {
				String stateName = rs.getString("stateName");
				int countryKey = rs.getInt("countryKey");
				Country country = Country.getCountryByKey(countryKey);
				state = new State(stateKey, stateName, country);
			} else {
				throw new IllegalStateException("No such state in database with id = " + stateKey);
			}
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
		return state;
	}

}
