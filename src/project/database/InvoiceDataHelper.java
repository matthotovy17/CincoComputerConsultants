/**
 * Author: Chloe Galinsky, Matt Hotovy
 * Date: 4/19/2019
 * 
 * This class holds helper methods for the invoiceData class
 */
package project.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import project.utils.ConnectionFactory;

public class InvoiceDataHelper {

	/**
	 * Method to add country record to database with the provided data.
	 * 
	 * @param countryName
	 */
	public static int addCountry(String countryName) {
		int countryKey = -1;
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query to select a countryKey by the given countryName.
		String query = "SELECT countryKey FROM Country WHERE countryName = ?";

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, countryName);
			rs = ps.executeQuery();
			// "if" is true only when the above query does not find a country in the
			// dataBase by that countryName.
			if (!rs.next()) {
				// This query inserts a new country based on the given countryName.
				String insertCountry = "INSERT INTO Country (countryName) values (?)";
				ps = conn.prepareStatement(insertCountry);
				ps.setString(1, countryName);
				ps.executeUpdate();
				// Selects the last entered ID from the database which is the country that was
				// just entered
				ps = conn.prepareStatement("SELECT LAST_INSERT_ID()");
				rs = ps.executeQuery();
				rs.next();
				countryKey = rs.getInt("LAST_INSERT_ID()");
			} else {
				// returns the country key if there is already the given countryName in the
				// database.
				countryKey = rs.getInt("countryKey");
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		// Close the connections that were opened in this method.
		ConnectionFactory.closeConnection(conn, ps, rs);
		return countryKey;
	}

//-------------------------------------------------------------------------------------------------------------------

	/**
	 * Method that removes a country from the database corresponding to the provided
	 * countryName
	 * 
	 * @param countryName
	 */
	public static void removeCountry(String countryName) {
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// This query is used to get a countryKey by the given countryName
		String query = "SELECT countryKey FROM Country WHERE countryName = ?";

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, countryName);
			rs = ps.executeQuery();
			// "if" is true if there is a country in the database on the given countryName.
			if (rs.next()) {
				// Delete query to delete a country on the countryKey.
				String deleteCountry = "DELETE FROM Country WHERE countryKey = ?";

				int countryKey = rs.getInt("countryKey");
				ps = conn.prepareStatement(deleteCountry);
				ps.setInt(1, countryKey);
				ps.executeUpdate();
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		// Close all connections that were opened in this method.
		ConnectionFactory.closeConnection(conn, ps, rs);
	}

//-------------------------------------------------------------------------------------------------------------------

	/**
	 * Method to add state record to database with the provided data.
	 * 
	 * @param stateName
	 * @param countryName
	 * @return corresponding stateKey
	 */
	public static int addState(String stateName, String countryName) {
		int stateKey = -1;
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// This query try's to find a stateKey in the database by the given stateName.
		String query = "SELECT stateKey FROM State WHERE stateName = ?";

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, stateName);
			rs = ps.executeQuery();
			// "if" is true only when the query does not find a state by the given
			// stateName.
			if (!rs.next()) {
				int countryKey = addCountry(countryName);
				// This query is used to insert a new state into the database.
				String insertState = "INSERT INTO State (stateName, countryKey) values (?, ?)";

				ps = conn.prepareStatement(insertState);
				ps.setString(1, stateName);
				ps.setInt(2, countryKey);
				ps.executeUpdate();
				// gets the last inserted ID from the database which is the state that was just
				// entered
				ps = conn.prepareStatement("SELECT LAST_INSERT_ID()");
				rs = ps.executeQuery();
				rs.next();
				stateKey = rs.getInt("LAST_INSERT_ID()");
			} else {
				// This else only executes when there is already a state in the database by the
				// given stateName.
				stateKey = rs.getInt("stateKey");
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		// Close the connections the were opened in this method.
		ConnectionFactory.closeConnection(conn, ps, rs);
		return stateKey;
	}

//-------------------------------------------------------------------------------------------------------------------

	/**
	 * Method that removes a state record from the database corresponding to the
	 * provided stateKey
	 * 
	 * @param stateKey
	 */
	public static void removeState(String stateName) {
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// This query is used to select the stateKey by a stateName.
		String query = "SELECT stateKey FROM State WHERE stateName = ?";

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, stateName);
			rs = ps.executeQuery();
			// If there are any states that are in the database with the given stateName
			if (rs.next()) {
				// query to delete a state that is found by the select query.
				String deleteState = "DELETE FROM State WHERE stateKey = ?";

				int stateKey = rs.getInt("stateKey");
				ps = conn.prepareStatement(deleteState);
				ps.setInt(1, stateKey);
				ps.executeUpdate();
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		// Close the connections that have been opened.
		ConnectionFactory.closeConnection(conn, ps, rs);
	}

//-------------------------------------------------------------------------------------------------------------------

	/**
	 * Method to add address record to database with the provided data.
	 * 
	 * @param street
	 * @param city
	 * @param zip
	 * @param stateName
	 * @param countryName
	 */
	public int addAddress(String street, String city, String stateName, String zip, String countryName) {
		int addressKey = -1;
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// This query selects the addressKey based on given parameters.
		String query = "SELECT addressKey FROM Address WHERE street = ? AND city = ? AND zip = ?";

		try {
			ps = conn.prepareStatement(query);
			// give the appropriate variables for above query.
			ps.setString(1, street);
			ps.setString(2, city);
			ps.setString(3, zip);
			rs = ps.executeQuery();
			// "if" is true when there is no address currently in the DB by the given
			// parameters.
			if (!rs.next()) {
				int stateKey = addState(stateName, countryName);
				// This query is to insert a new address based on the previously given
				// parameters
				String insertAddress = "INSERT INTO Address (street, city, stateKey, zip) values (?, ?, ?, ?)";

				ps = conn.prepareStatement(insertAddress);
				ps.setString(1, street);
				ps.setString(2, city);
				ps.setInt(3, stateKey);
				ps.setString(4, zip);
				ps.executeUpdate();
				// This gets the last inserted id from the data base which is the new address
				ps = conn.prepareStatement("SELECT LAST_INSERT_ID()");
				rs = ps.executeQuery();
				rs.next();
				addressKey = rs.getInt("LAST_INSERT_ID()");
			} else {
				// returns the addressKey of the given address since it is already in the
				// database.
				addressKey = rs.getInt("addressKey");
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		// Close the connections made to the database
		ConnectionFactory.closeConnection(conn, ps, rs);
		return addressKey;
	}

// -------------------------------------------------------------------------------------------------------------------

	/**
	 * Method to find a person and return the personKey
	 * 
	 * @param personUuid
	 * @return corresponding addressKey
	 */
	public static int findAddress(String street, String city, String zip) {
		int addressKey = -1;
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query is used to get an addressKey by the given street, city, and zipCode.
		String query = "SELECT addressKey FROM Address WHERE street = ? AND city = ? AND zip = ?";

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, street);
			ps.setString(2, city);
			ps.setString(3, zip);
			rs = ps.executeQuery();
			// "if" is true if the query above successfully gets a specified address from
			// the DB
			if (rs.next()) {
				addressKey = rs.getInt("addressKey");
			}
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			// Close the connections made to the database
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
		return addressKey;
	}

//-------------------------------------------------------------------------------------------------------------------	

	/**
	 * This method is a helper method of the removeAddress method in order to remove
	 * an Address first any Customers that have references to that Address need to
	 * be removed.
	 * 
	 * @param addressKey
	 */
	public static void removeCustomerByAddressKey(int addressKey) {
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query try's to find a customer with the given addressKey
		String customerQuery = "SELECT c.customerUuid FROM Customer c LEFT JOIN Address a WHERE c.addressKey = ?";

		try {
			ps = conn.prepareStatement(customerQuery);
			ps.setInt(1, addressKey);
			rs = ps.executeQuery();
			// "if" is true if a customer is found on the given addressKey
			if (rs.next()) {
				String customerUuid = rs.getString("c.customerUuid");
				// Give the customerUuid to the removeCustomer method to remove that specific
				// customer.
				InvoiceData.removeCustomer(customerUuid);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			// Close the connections made to the database
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
	}

//-------------------------------------------------------------------------------------------------------------------	

	/**
	 * This method is a helper method of the removeAddress method in order to remove
	 * an Address first any Persons that have references to that Address need to be
	 * removed.
	 * 
	 * @param addressKey
	 */
	public static void removePersonByAddressKey(int addressKey) {
		// Error checking was done in the removeAddress method.
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query try's to find a person on the given addressKey from above.
		String personQuery = "SELECT p.personUuid FROM Person p LEFT JOIN Address a WHERE p.addressKey = ?";

		try {
			ps = conn.prepareStatement(personQuery);
			ps.setInt(1, addressKey);
			rs = ps.executeQuery();
			// "if" executes if the above query finds a person
			if (rs.next()) {
				String personUuid = rs.getString("p.personUuid");
				// Give the removePerson method the specific personUuid to remove that person.
				InvoiceData.removePerson(personUuid);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			// Close the connections made to the database
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
	}

//-------------------------------------------------------------------------------------------------------------------	

	/**
	 * Method that removes an address record from the database corresponding to the
	 * provided addressKey
	 * 
	 * @param addressKey
	 */
	public static void removeAddress(String street, String city, String zip) {
		int addressKey = findAddress(street, city, zip);
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// First any references to this Address need to be removed.
		removeCustomerByAddressKey(addressKey);
		removePersonByAddressKey(addressKey);

		// Query deletes an address on the given addressKey from above.
		String query = "DELETE FROM Address WHERE addressKey = ?";

		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, addressKey);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			// Close the connections made to the database
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
	}

//-------------------------------------------------------------------------------------------------------------------

	/**
	 * Method to find a person and return the personKey
	 * 
	 * @param personUuid
	 * @return corresponding personKey
	 */
	public int findPerson(String personUuid) {
		int personKey = -1;
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query try's to find a person by the given personUuid and return its
		// personKey.
		String query = "SELECT personKey FROM Person WHERE personUuid = ?";

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, personUuid);
			rs = ps.executeQuery();
			// "if" executes if above query finds a valid person in the database.
			if (rs.next()) {
				// if person is found, return the key.
				personKey = rs.getInt("personKey");
			}
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			// Close the connections that were opened in this method.
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
		return personKey;
	}

//-------------------------------------------------------------------------------------------------------------------

	/**
	 * Method to find a customer and return the customerKey
	 * 
	 * @param customerUuid
	 * @return corresponding customerKey
	 */
	public int findCustomer(String customerUuid) {
		int customerKey = -1;
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query to find the customerKey by the given customerUuid.
		String query = "SELECT customerKey FROM Customer WHERE customerUuid = ?";

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, customerUuid);
			rs = ps.executeQuery();
			// "if" executes if the above query finds a customer.
			if (rs.next()) {
				customerKey = rs.getInt("customerKey");
			}
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			// Close the connections that were opened in this method.
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
		return customerKey;
	}

//-------------------------------------------------------------------------------------------------------------------

	/**
	 * Method to find a product and return the productKey
	 * 
	 * @param productUuid
	 * @return corresponding productKey
	 */
	public int findProduct(String productUuid) {
		int productKey = -1;
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query to try to find a product by the given productUuid.
		String query = "SELECT productKey FROM Product WHERE productUuid = ?";

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, productUuid);
			rs = ps.executeQuery();
			// "if" executes if the above query finds a corresponding product.
			if (rs.next()) {
				productKey = rs.getInt("productKey");
			}
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			// Close all the connections opened in this method.
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
		return productKey;
	}

//-------------------------------------------------------------------------------------------------------------------

	/**
	 * Method to find a invoice and return the invoiceKey
	 * 
	 * @param invoiceUuid
	 * @return corresponding invoiceKey
	 */
	public int findInvoice(String invoiceUuid) {
		int invoiceKey = -1;
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query to try to find an invoice that is already in the database.
		String query = "SELECT invoiceKey FROM Invoice WHERE invoiceUuid = ?";

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, invoiceUuid);
			rs = ps.executeQuery();
			// "if" executes if the above query found a corresponding Invoice instance
			if (rs.next()) {
				invoiceKey = rs.getInt("invoiceKey");
			}
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			// Close the connections that were opened in this method.
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
		return invoiceKey;
	}

//-------------------------------------------------------------------------------------------------------------------

	/**
	 * This method is a helper method for the removePerson method and is used to
	 * remove an Invoice on a given personKey
	 * 
	 * @param personKey
	 */
	public void removeInvoiceByPersonKey(int personKey) {
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query try's to get an invoice on a given personKey
		String invoiceQuery = "SELECT invoiceUuid FROM Invoice WHERE personKey = ?";

		try {
			ps = conn.prepareStatement(invoiceQuery);
			ps.setInt(1, personKey);
			rs = ps.executeQuery();
			// "if" executes if the above query finds a invoice with the given person key
			if (rs.next()) {
				String invoiceUuid = rs.getString("invoiceUuid");
				// given the removeInvoice method the invoiceUuid to delete before person
				InvoiceData.removeInvoice(invoiceUuid);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			// Close all the connections that were opened up in this method.
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
	}

//--------------------------------------------------------------------------------------------------------------------

	/**
	 * This method is a helper method for the removePerson method and is used to
	 * remove a Customer on a given personKey
	 * 
	 * @param personKey
	 */
	public void removeCustomerByPersonKey(int personKey) {
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query try's to get a customer on the above personKey
		String customerQuery = "SELECT customerUuid FROM Customer WHERE personKey = ?";

		try {
			ps = conn.prepareStatement(customerQuery);
			ps.setInt(1, personKey);
			rs = ps.executeQuery();
			// "if" executes if the above query found a customer
			if (rs.next()) {
				String customerUuid = rs.getString("customerUuid");
				// Give the remove customer method the customerUuid found on the above query.
				InvoiceData.removeCustomer(customerUuid);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			// Close all the connections that were opened up in this method.
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
	}

//--------------------------------------------------------------------------------------------------------------------

	/**
	 * This method is a helper method for the removePerson method and is used to
	 * remove a Product on a given personKey
	 * 
	 * @param personKey
	 */
	public void removeProductByPersonKey(int personKey) {
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query try's to get a product on the given personKey.
		String productQuery = "SELECT productUuid FROM Product WHERE personKey = ?";

		try {
			ps = conn.prepareStatement(productQuery);
			ps.setInt(1, personKey);
			rs = ps.executeQuery();
			// "if" executes if the above query finds a product.
			if (rs.next()) {
				String productUuid = rs.getString("productUuid");
				// Give the remove product method the product that was found.
				InvoiceData.removeProduct(productUuid);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			// Close all the connections that were opened up in this method.
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
	}

//-------------------------------------------------------------------------------------------------------------------

	/**
	 * This method is a helper function for the removeCustomer method and is used to
	 * remove an Invoice on a given customerKey.
	 * 
	 * @param customerKey
	 */
	public void removeInvoiceByCustomerKey(int customerKey) {
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query to find a invoice that has the corresponding customer key.
		String invoiceQuery = "SELECT invoiceUuid FROM Invoice WHERE customerKey = ?";

		try {
			ps = conn.prepareStatement(invoiceQuery);
			ps.setInt(1, customerKey);
			rs = ps.executeQuery();
			// "if" executes if the above query found any invoices with the corresponding
			// customerKey.
			if (rs.next()) {
				String invoiceUuid = rs.getString("invoiceUuid");
				// Call the removeInvoice method to remove the invoice.
				InvoiceData.removeInvoice(invoiceUuid);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			// Close all the connections that were opened in this method.
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
	}

//-------------------------------------------------------------------------------------------------------------------

	/**
	 * This method is a helper method of the removeProduct method and is used to
	 * delete an InvoiceProduct instance based on a productUuid
	 * 
	 * @param productUuid
	 */
	public void removeInvoiceProductOnProduct(String productUuid) {
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query to delete an InvoiceProduct on a given productUuid.
		String query = "DELETE ip FROM InvoiceProduct ip LEFT JOIN Product p ON ip.productKey = p.productKey WHERE p.productUuid = ?";

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, productUuid);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			// Close all the connections that were opened in this method.
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
	}

//-------------------------------------------------------------------------------------------------------------------

	/**
	 * This method is a helper function for the removeInvoice method and is used to
	 * remove an InvoiceProduct on a given invoiceUuid
	 * 
	 * @param invoiceUuid
	 */
	public void removeInvoiceProductOnInvoice(String invoiceUuid) {
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query to delete an IncvoiceProduct on the given invoiceUuid
		String query = "DELETE ip FROM InvoiceProduct ip LEFT JOIN Invoice i ON ip.invoiceKey = i.invoiceKey WHERE i.invoiceUuid = ?";

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, invoiceUuid);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			// Close the connections that were opened in this method.
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
	}

//-------------------------------------------------------------------------------------------------------------------

	/**
	 * Removes all the invoiceProduct records from the database
	 */
	public void removeAllInvoiceProducts() {
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query to delete all the data from the InvoiceProduct table in the database
		String query = "DELETE FROM InvoiceProduct";

		try {
			ps = conn.prepareStatement(query);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			// Close all the connections opened in this method.
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
	}

}
