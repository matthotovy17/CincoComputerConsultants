/**
 * Author: Matt Hotovy
 * Date: 2/8/2019
 * 
 * This object is for the person data
 */

package project.lib;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import project.utils.ConnectionFactory;

public class Person {

	private String personUuid;
	private String lastName;
	private String firstName;
	private Address address;
	private List<String> emails = new ArrayList<String>();

	public Person(String personUuid, String lastName, String firstName, Address address) {
		this.personUuid = personUuid;
		this.lastName = lastName;
		this.firstName = firstName;
		this.address = address;
	}

	public Person() {
	}

	public String getPersonUuid() {
		return personUuid;
	}

	public String getName() {
		return this.lastName + ", " + this.firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public Address getAddress() {
		return address;
	}

	public List<String> getEmails() {
		return emails;
	}

	public void addEmail(String email) {
		this.emails.add(email);
	}

	public String toString() {
		return "Person Uuid = " + this.personUuid + ", Last Name = " + this.lastName + ", First Name = "
				+ this.firstName + ", Address = " + this.address + ", Emails = " + this.emails;
	}

//--------------------------------------------------------------------------------------------------

	// This method takes a personKey, query's the database and returns the
	// corresponding person
	public static Person getPersonByKey(int personKey) {
		Person person = null;
		Connection conn = ConnectionFactory.getConnection();
		
		String query = "SELECT personUuid, lastName, firstName, addressKey FROM Person WHERE personKey = ?";

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, personKey);
			rs = ps.executeQuery();
			if (rs.next()) {
				String personUuid = rs.getString("personUuid");
				String lastName = rs.getString("lastName");
				String firstName = rs.getString("firstName");
				int addressKey = rs.getInt("addressKey");
				Address address = Address.getAddressByKey(addressKey);
				person = new Person(personUuid, lastName, firstName, address);
			} else {
				throw new IllegalStateException("No such Person in database with id = " + personKey);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		String emailQuery = "SELECT email FROM Email e WHERE personKey = ?";

		try {
			ps = conn.prepareStatement(emailQuery);
			ps.setInt(1, personKey);
			rs = ps.executeQuery();
			while (rs.next()) {
				person.addEmail(rs.getString("email"));
			}
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
		return person;
	}

//---------------------------------------------------------------------------

	// This method query's the database and returns a list of all the persons in the database.
	public static List<Person> getAllPersons() {
		List<Person> persons = new ArrayList<>();
		Connection conn = ConnectionFactory.getConnection();

		String query = "SELECT personKey, personUuid, lastName, firstName, addressKey FROM Person";

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			while (rs.next()) {
				String personUuid = rs.getString("personUuid");
				String lastName = rs.getString("lastName");
				String firstName = rs.getString("firstName");
				int addressKey = rs.getInt("addressKey");
				Address address = Address.getAddressByKey(addressKey);
				Person person = new Person(personUuid, lastName, firstName, address);
				persons.add(person);
			}
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
		return persons;
	}

}
