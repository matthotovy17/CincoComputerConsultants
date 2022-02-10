/**
 * Author: Chloe Galinsky, Matt Hotovy
 * Date: 4/19/2019
 * 
 * This class is for the data persistence of the database.
 */
package project.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import project.utils.ConnectionFactory;

/**
 * This is a collection of utility methods that define a general API for
 * interacting with the database supporting this application.
 *
 */
public class InvoiceData {

	private static InvoiceDataHelper helper;

	/**
	 * Method that removes every person record from the database
	 */
	public static void removeAllPersons() {
		// Must remove invoices, customers, products, and email's first
		removeAllInvoices();
		removeAllCustomers();
		removeAllProducts();
		removeAllEmails();
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query to delete all data in the Person table.
		String query = "DELETE FROM Person";

		try {
			ps = conn.prepareStatement(query);
			ps.executeUpdate();
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
	 * Removes the person record from the database corresponding to the provided
	 * <code>personUuid</code>
	 * 
	 * @param personUuid
	 */
	public static void removePerson(String personUuid) {
		int personKey = helper.findPerson(personUuid);
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// First remove any references to this Person instance.
		helper.removeInvoiceByPersonKey(personKey);
		helper.removeCustomerByPersonKey(personKey);
		helper.removeProductByPersonKey(personKey);
		removeEmail(personUuid);

		// Query to delete a person based on the given personUuid.
		String query = "DELETE FROM Person WHERE personUuid = ?";

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, personUuid);
			ps.executeUpdate();
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
	 * Method to add a person record to the database with the provided data.
	 * 
	 * @param personUuid
	 * @param firstName
	 * @param lastName
	 * @param street
	 * @param city
	 * @param stateName
	 * @param zip
	 * @param countryName
	 */
	public static void addPerson(String personUuid, String firstName, String lastName, String street, String city,
			String stateName, String zip, String countryName) {
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query to find a person on the given data
		String query = "SELECT personKey FROM Person WHERE personUuid = ? AND firstName = ? AND lastName = ?";

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, personUuid);
			ps.setString(2, firstName);
			ps.setString(3, lastName);
			rs = ps.executeQuery();
			// "if" executes if the above query does not find a person.
			if (!rs.next()) {
				// creates an address to put into the person
				int addressKey = helper.addAddress(street, city, zip, stateName, countryName);
				// Inserts into the person table based on the given data.
				String insertPerson = "INSERT INTO Person (personUuid, firstName, lastName, addressKey) values (?, ?, ?, ?)";
				ps = conn.prepareStatement(insertPerson);
				ps.setString(1, personUuid);
				ps.setString(2, firstName);
				ps.setString(3, lastName);
				ps.setInt(4, addressKey);
				ps.executeUpdate();
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		// Close all the connections that were opened up in this method.
		ConnectionFactory.closeConnection(conn, ps, rs);
	}

//-------------------------------------------------------------------------------------------------------------------

	/**
	 * Adds an email record corresponding person record corresponding to the
	 * provided <code>personUuid</code>
	 * 
	 * @param personUuid
	 * @param email
	 */
	public static void addEmail(String personUuid, String email) {
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query to try to find an email
		String query = "SELECT email FROM Email WHERE email = ?";

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, email);
			rs = ps.executeQuery();
			// "if" executes if an email was not already in the database.
			if (!rs.next()) {
				// Find the personKey on the given personUuid.
				int personKey = helper.findPerson(personUuid);
				// query to insert an email with the given personKey.
				String insertEmail = "INSERT INTO Email (email, personKey) values (?, ?)";
				ps = conn.prepareStatement(insertEmail);
				ps.setString(1, email);
				ps.setInt(2, personKey);
				ps.executeUpdate();
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
	}

//-------------------------------------------------------------------------------------------------------------------

	/**
	 * Method that removes an email record from the database corresponding to the
	 * provided the personUuid
	 * 
	 * @param personUuid
	 */
	public static void removeEmail(String personUuid) {
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query deletes an email based on
		String query = "DELETE e FROM Email e LEFT JOIN Person p ON p.personKey = e.personKey WHERE personUuid = ?";

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, personUuid);
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
	 * Method that removes all email records from the database
	 */
	public static void removeAllEmails() {
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query to delete all data in the email table.
		String query = "DELETE FROM Email";

		try {
			ps = conn.prepareStatement(query);
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
	 * Method that removes every customer record from the database
	 */
	public static void removeAllCustomers() {
		// must remove invoices first
		removeAllInvoices();
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query to delete all data from the Customer table
		String query = "DELETE FROM Customer";

		try {
			ps = conn.prepareStatement(query);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			// close all the connections that were opened in this method.
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
	}

//-------------------------------------------------------------------------------------------------------------------

	/**
	 * Removes a particular customer record from the database corresponding to the
	 * provided <code>customerUuid</code>
	 * 
	 * @param customerUuid
	 */
	public static void removeCustomer(String customerUuid) {
		int customerKey = helper.findCustomer(customerUuid);
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Must first remove any Customers that reference this person.
		helper.removeInvoiceByCustomerKey(customerKey);

		// Query to delete the data from a Customer instance in the database based on
		// the customerUuid.
		String query = "DELETE FROM Customer WHERE customerUuid = ?";

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, customerUuid);
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
	 * This method is used to add in a customer to the database, but it must be a
	 * unique customer or else the person is not actually added and outputs a
	 * message.
	 * 
	 * @param customerUuid
	 * @param customerType
	 * @param personUuid
	 * @param customerName
	 * @param street
	 * @param city
	 * @param stateName
	 * @param zip
	 * @param countryName
	 */
	public static void addCustomer(String customerUuid, String customerType, String personUuid, String customerName,
			String street, String city, String stateName, String zip, String countryName) {
		// find the primaryContactKey to put into the new customer.
		int primaryContactKey = helper.findPerson(personUuid);
		// add the new address to the database to use its key in the new customer.
		int addressKey = helper.addAddress(street, city, zip, stateName, countryName);

		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query try's to find a customerKey based on the given parameters.
		String query = "SELECT customerKey FROM Customer WHERE customerUuid = ? AND customerType = ? AND customerName = ?";

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, customerUuid);
			ps.setString(2, customerType);
			ps.setString(3, customerName);
			rs = ps.executeQuery();
			// "if" executes if that customer in not already in the database.
			if (!rs.next()) {
				// Query to insert a customer into the database.
				String insertCustomer = "INSERT INTO Customer (customerUuid, customerType, personKey, customerName, addressKey) values (?, ?, ?, ?, ?)";
				ps = conn.prepareStatement(insertCustomer);
				ps.setString(1, customerUuid);
				ps.setString(2, customerType);
				ps.setInt(3, primaryContactKey);
				ps.setString(4, customerName);
				ps.setInt(5, addressKey);
				ps.executeUpdate();
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
	}

//-------------------------------------------------------------------------------------------------------------------

	/**
	 * Removes all product records from the database
	 */
	public static void removeAllProducts() {
		// must remove invoiceProducts first
		InvoiceData.helper.removeAllInvoiceProducts();
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query to delete all the data from the product table in the database.
		String query = "DELETE FROM Product";

		try {
			ps = conn.prepareStatement(query);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			// Close the connections opened in this method.
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
	}

//-------------------------------------------------------------------------------------------------------------------

	/**
	 * Removes a particular product record from the database corresponding to the
	 * provided <code>productUuid</code>
	 * 
	 * @param assetCode
	 */
	public static void removeProduct(String productUuid) {
		// First have to remove the corresponding InvoiceProducts.
		helper.removeInvoiceProductOnProduct(productUuid);
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query to delete a product instance from the product table
		String query = "DELETE FROM Product WHERE productUuid = ?";

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, productUuid);
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

//-------------------------------------------------------------------------------------------------------------------

	/**
	 * Adds an equipment record to the database with the provided data.
	 * 
	 * @param productUuid
	 * @param productName
	 * @param pricePerUnit
	 */
	public static void addEquipment(String productUuid, String productName, Double pricePerUnit) {
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query to try to find an Equipment productKey in the database with the given
		// data.
		String query = "SELECT productKey FROM Product WHERE productUuid = ? AND productName = ? AND pricePerUnit = ?";

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, productUuid);
			ps.setString(2, productName);
			ps.setDouble(3, pricePerUnit);
			rs = ps.executeQuery();
			// "if" executes if the above query does not find any Equipment with the same
			// data.
			if (!rs.next()) {
				// Query to insert the new Equipment into the database.
				String insertEquipment = "INSERT INTO Product (productUuid, productType, productName, pricePerUnit) values (?, ?, ?, ?)";
				ps = conn.prepareStatement(insertEquipment);
				ps.setString(1, productUuid);
				ps.setString(2, "E");
				ps.setString(3, productName);
				ps.setDouble(4, pricePerUnit);
				ps.executeUpdate();
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
	}

//-------------------------------------------------------------------------------------------------------------------

	/**
	 * Adds an license record to the database with the provided data.
	 * 
	 * @param productUuid
	 * @param productName
	 * @param serviceFee
	 * @param annualLicenseFee
	 */
	public static void addLicense(String productUuid, String productName, double serviceFee, double annualLicenseFee) {
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query to try to find a License Product in the database.
		String query = "SELECT productKey FROM Product WHERE productUuid = ? AND productName = ? AND serviceFee = ? AND annualLicenseFee = ?";

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, productUuid);
			ps.setString(2, productName);
			ps.setDouble(3, serviceFee);
			ps.setDouble(4, annualLicenseFee);
			rs = ps.executeQuery();
			// "if" executes only if the above query does not find a License with the same
			// data in the database
			if (!rs.next()) {
				// Query to insert the new License into the database.
				String insertLicense = "INSERT INTO Product (productUuid, productType, productName, serviceFee, annualLicenseFee) values (?, ?, ?, ?, ?)";
				ps = conn.prepareStatement(insertLicense);
				ps.setString(1, productUuid);
				ps.setString(2, "L");
				ps.setString(3, productName);
				ps.setDouble(4, serviceFee);
				ps.setDouble(5, annualLicenseFee);
				ps.executeUpdate();
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
	}

//-------------------------------------------------------------------------------------------------------------------

	/**
	 * Adds an consultation record to the database with the provided data.
	 * 
	 * @param productUuid
	 * @param productName
	 * @param personUuid
	 * @param hourlyFee
	 */
	public static void addConsultation(String productUuid, String productName, String personUuid, Double hourlyFee) {
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query to try to find a Consultation Product in the database.
		String query = "SELECT productKey FROM Product WHERE productUuid = ? AND productName = ? AND hourlyFee = ?";

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, productUuid);
			ps.setString(2, productName);
			ps.setDouble(3, hourlyFee);
			rs = ps.executeQuery();
			// "if" only executes if the above query does not find a corresponding
			// Consultation.
			if (!rs.next()) {
				// Find the corresponding consultant person for this Consultation.
				int consultantPersonKey = helper.findPerson(personUuid);
				// Query to insert a new Consultation Product into the database.
				String insertConsultation = "INSERT INTO Product (productUuid, productType, productName, personKey, hourlyFee) values (?, ?, ?, ?, ?)";
				ps = conn.prepareStatement(insertConsultation);
				ps.setString(1, productUuid);
				ps.setString(2, "C");
				ps.setString(3, productName);
				ps.setInt(4, consultantPersonKey);
				ps.setDouble(5, hourlyFee);
				ps.executeUpdate();
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		// Close all the connections opened in this method.
		ConnectionFactory.closeConnection(conn, ps, rs);
	}

//-------------------------------------------------------------------------------------------------------------------

	/**
	 * Removes all invoice records from the database
	 */
	public static void removeAllInvoices() {
		// must remove invoiceProducts first
		helper.removeAllInvoiceProducts();

		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query to delete all the data from the Invoice table in the database.
		String query = "DELETE FROM Invoice";

		try {
			ps = conn.prepareStatement(query);
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
	 * Removes the invoice record from the database corresponding to the provided
	 * <code>invoiceUuid</code>
	 * 
	 * @param invoiceUuid
	 */
	public static void removeInvoice(String invoiceUuid) {
		// remove the InvoiceProduct that corresponds with the given invoiceUuid.
		helper.removeInvoiceProductOnInvoice(invoiceUuid);

		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query to delete an Invoice instance from the database on a given invoiceUuid.
		String query = "DELETE FROM Invoice WHERE invoiceUuid = ?";

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, invoiceUuid);
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
	 * Adds an invoice record to the database with the given data.
	 * 
	 * @param invoiceUuid
	 * @param customerUuid
	 * @param personUuid
	 */
	public static void addInvoice(String invoiceUuid, String customerUuid, String personUuid) {
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query to select an Invoice from the database on a given invoiceUuid.
		String query = "SELECT invoiceKey FROM Invoice WHERE invoiceUuid = ?";

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, invoiceUuid);
			rs = ps.executeQuery();
			// "if" executes if the above query does not find an Invoice.
			if (!rs.next()) {
				// Find the customerKey to put into the Invoice instance.
				int customerKey = helper.findCustomer(customerUuid);
				// Find the salesPersonKey from the database to put it into the Invoice.
				int salesPersonKey = helper.findPerson(personUuid);
				// Query to insert a new Invoice instance into the database.
				String insertInvoice = "INSERT INTO Invoice (invoiceUuid, customerKey, personKey) values (?, ?, ?)";
				ps = conn.prepareStatement(insertInvoice);
				ps.setString(1, invoiceUuid);
				ps.setInt(2, customerKey);
				ps.setInt(3, salesPersonKey);
				ps.executeUpdate();
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		// Close all the connections that were opened in the database.
		ConnectionFactory.closeConnection(conn, ps, rs);
	}

//-------------------------------------------------------------------------------------------------------------------

	/**
	 * Adds a particular equipment (corresponding to <code>productUuid</code> to an
	 * invoice corresponding to the provided <code>invoiceUuid</code> with the given
	 * number of units
	 * 
	 * @param invoiceUuid
	 * @param productUuid
	 * @param numberOfUnits
	 */
	public static void addEquipmentToInvoice(String invoiceUuid, String productUuid, int numberOfUnits) {
		// Find the invoiceKey on the given invoiceUuid to put into the Equipment.
		int invoiceKey = helper.findInvoice(invoiceUuid);
		// Find the productKey on the given productUuid to put into the Equipment.
		int productKey = helper.findProduct(productUuid);

		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query to insert a Equipment Product into the InvoiceProduct table.
		String insertEquipmentInInvoiceProduct = "INSERT INTO InvoiceProduct (invoiceKey, productKey, numberOfUnits) values (?, ?, ?)";

		try {
			ps = conn.prepareStatement(insertEquipmentInInvoiceProduct);
			ps.setInt(1, invoiceKey);
			ps.setInt(2, productKey);
			ps.setInt(3, numberOfUnits);
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
	 * Adds a particular equipment (corresponding to <code>productUuid</code> to an
	 * invoice corresponding to the provided <code>invoiceUuid</code> with the given
	 * begin/end dates
	 */
	public static void addLicenseToInvoice(String invoiceUuid, String productUuid, String effectiveBeginDate,
			String effectiveEndDate) {
		// Find the invoiceKey on the given invoiceUuid to put into the License.
		int invoiceKey = helper.findInvoice(invoiceUuid);
		// Find the productKey on the given productUuid to put into the License.
		int productKey = helper.findProduct(productUuid);

		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query to insert a License Product into the InvoiceProduct table.
		String insertLicenseInInvoiceProduct = "INSERT INTO InvoiceProduct (invoiceKey, productKey, effectiveBeginDate, effectiveEndDate) values (?, ?, ?, ?)";

		try {
			ps = conn.prepareStatement(insertLicenseInInvoiceProduct);
			ps.setInt(1, invoiceKey);
			ps.setInt(2, productKey);
			ps.setString(3, effectiveBeginDate);
			ps.setString(4, effectiveEndDate);
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
	 * Adds a particular equipment (corresponding to <code>productUuid</code> to an
	 * invoice corresponding to the provided <code>invoiceUuid</code> with the given
	 * number of billable hours.
	 * 
	 * @param invoiceUuid
	 * @param productUuid
	 * @param billableHours
	 */
	public static void addConsultationToInvoice(String invoiceUuid, String productUuid, double billableHours) {
		// Find the invoiceKey on the given invoiceUuid to put into the Consultation.
		int invoiceKey = helper.findInvoice(invoiceUuid);
		// Find the productKey on the given productUuid to put into the Consultation.
		int productKey = helper.findProduct(productUuid);

		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Query to insert a Consultation Product into the InvoiceProduct table.
		String insertConsultationInInvoiceProduct = "INSERT INTO InvoiceProduct (invoiceKey, productKey, billableHours) values (?, ?, ?)";

		try {
			ps = conn.prepareStatement(insertConsultationInInvoiceProduct);
			ps.setInt(1, invoiceKey);
			ps.setInt(2, productKey);
			ps.setDouble(3, billableHours);
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
}
