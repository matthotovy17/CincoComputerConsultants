/**
 * Author: Matt Hotovy
 * Date: 2/8/2019
 * 
 * This object is for the address pertaining to the person and customer
 */

package project.lib;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import project.utils.ConnectionFactory;

public class Address {

	private int addressKey;
	private String street;
	private String city;
	private State state;
	private String zip;
	private Country country;

	//Constructor for reading from the database
	public Address(int addressKey, String street, String city, State state, String zip, Country country) {
		this.addressKey = addressKey;
		this.street = street;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.country = country;
	}
	
	//Constructor for reading from csv files in the file reader class
	public Address(String street, String city, State state, String zip, Country country) {
		this.street = street;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.country = country;
	}

	public Address() {
	}

	@Override
	public String toString() {
		return street + ", " + city + ", " + state + ", " + zip + ", " + country;
	}

	public int getAddressKey() {
		return addressKey;
	}

	public String getStreet() {
		return street;
	}

	public String getCity() {
		return city;
	}

	public State getState() {
		return state;
	}

	public String getZip() {
		return zip;
	}

	public Country getCountry() {
		return country;
	}
	
//--------------------------------------------------------------------------------
	
	//This method takes an addressKey and query's the database for that address, then returns it.
	public static Address getAddressByKey(int addressKey) {
		Address address = null;
		Connection conn = ConnectionFactory.getConnection();
		
		//Query to get all fields from the address table on a specific addressKey
		String query = "SELECT addressKey, street, city, stateKey, zip FROM Address WHERE addressKey = ?";

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, addressKey);
			rs = ps.executeQuery();
			if (rs.next()) {
				String street = rs.getString("street");
				String city = rs.getString("city");
				int stateKey = rs.getInt("stateKey");
				State state = State.getStateByKey(stateKey);
				String zip = rs.getString("zip");
				Country country = state.getCountry();
				address = new Address(addressKey, street, city, state, zip, country);
			} else {
				throw new IllegalStateException("No such address in database with id = " + addressKey);
			}
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
		return address;
	}

}
