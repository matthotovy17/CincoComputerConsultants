/**
 * Author: Matt Hotovy
 * Date: 2/8/2019
 * 
 * This object is for the customer data that is read in from the database
 */

package project.lib;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import project.utils.ConnectionFactory;

public abstract class Customer{

	protected String customerUuid;
	protected String customerName;
	protected Person primaryContact;
	protected Address address;

	public Customer(String customerUuid, Person primaryContact, String customerName, Address address) {
		this.customerUuid = customerUuid;
		this.primaryContact = primaryContact;
		this.customerName = customerName;
		this.address = address;
	}

	public String getCustomerUuid() {
		return customerUuid;
	}

	public Person getPrimaryContact() {
		return primaryContact;
	}

	public String getCustomerName() {
		return customerName;
	}

	public Address getAddress() {
		return address;
	}

	public String toString() {
		return "CustomerUuid = " + this.customerUuid + ", PrimaryContact = " + this.primaryContact + ", CustomerName = "
				+ this.customerName + ", " + this.address;
	}
	
	
//----------------------------------------------------------------------------------------------------------

	// This method takes a customer key and query's the database on that key to
	// return the relating customer
	public static Customer getCustomerByKey(int customerKey) {
		Customer customer = null;
		Connection conn = ConnectionFactory.getConnection();
		String query = "SELECT customerUuid, customerType, customerName, personKey, addressKey FROM Customer WHERE customerKey = ?";

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, customerKey);
			rs = ps.executeQuery();
			if (rs.next()) {
				
				String customerType = rs.getString("customerType");
				String customerUuid = rs.getString("customerUuid");
				String customerName = rs.getString("customerName");
				int personKey = rs.getInt("personKey");
				Person person = Person.getPersonByKey(personKey);
				int addressKey = rs.getInt("addressKey");
				Address address = Address.getAddressByKey(addressKey);
				
				if (customerType.equals("G")) {
					customer = new GovernmentCustomer(customerUuid, person, customerName, address);
				} else {
					customer = new CorporateCustomer(customerUuid, person, customerName, address);
				}
			}
			
		} catch (SQLException e) {
			
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
			
		} finally {
			
			ConnectionFactory.closeConnection(conn, ps, rs);
			
		}
		return customer;
	}
	
//--------------------------------------------------------------------------------------------
	
	//This method query's the database to return a list of all the customers in the database.
	public static List<Customer> getAllCustomers() {
		List<Customer> customerList = new ArrayList<Customer>();
		Connection conn = ConnectionFactory.getConnection();

		String query = "SELECT customerUuid, customerType, customerName, personKey, addressKey FROM Customer";

		PreparedStatement ps = null;
		ResultSet rs = null;
		Customer customer = null;

		try {
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			while (rs.next()) {
				String customerUuid = rs.getString("customerUuid");
				String customerType = rs.getString("customerType");
				String customerName = rs.getString("customerName");
				int personKey = rs.getInt("personKey");
				Person primaryContact = Person.getPersonByKey(personKey);
				int addressKey = rs.getInt("addressKey");
				Address address = Address.getAddressByKey(addressKey);
				
				if (customerType.equals("G")) {
					customer = new GovernmentCustomer(customerUuid, primaryContact, customerName, address);
				} else {
					customer = new CorporateCustomer(customerUuid, primaryContact, customerName, address);
				}
				customerList.add(customer);
			}

		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
		return customerList;
	}

	// abstract classes for the subclasses.
	public abstract double getComplianceFee();

	public abstract String getType();

	public abstract double getSalesTax();
}
