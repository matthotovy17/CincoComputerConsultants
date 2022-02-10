/**
 * Author: Matt Hotovy
 * Date: 2/8/2019
 * 
 * This class is for reading data in from the database
 */

package project.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import project.lib.Address;
import project.lib.Consultation;
import project.lib.CorporateCustomer;
import project.lib.Customer;
import project.lib.Equipment;
import project.lib.GovernmentCustomer;
import project.lib.Invoice;
import project.lib.License;
import project.lib.Person;
import project.lib.Product;
import project.sort.Comparator;
import project.sort.CustomerComparator;
import project.sort.LinkedList;
import project.sort.TotalComparator;
import project.sort.TypeThenNameComparator;

public class DatabaseReader {

	// This method queries the DataBase for each person object and returns a Map of
	// those Person objects.
	public static Map<String, Person> getPersonData() {
		Map<String, Person> personMap = new HashMap<String, Person>();
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Makes a query to get all the member variables from the person table in the
		// database.
		String query = "SELECT personKey, personUuid, lastName, firstName, addressKey FROM Person";

		try {
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			// while there is another person in the database continue
			while (rs.next()) {
				int personKey = rs.getInt("personKey");
				String personUuid = rs.getString("personUuid");
				String lastName = rs.getString("lastName");
				String firstName = rs.getString("firstName");
				int addressKey = rs.getInt("addressKey");
				Address address = Address.getAddressByKey(addressKey);
				Person person = new Person(personUuid, lastName, firstName, address);

				// Makes a query to get the list of email's for a particular personKey
				String emailQuery = "SELECT email FROM Email  WHERE personKey = ?";

				PreparedStatement emailPs = null;
				ResultSet emailRs = null;

				try {
					emailPs = conn.prepareStatement(emailQuery);
					emailPs.setInt(1, personKey);
					emailRs = emailPs.executeQuery();
					//while there if a next email in the email array
					while (emailRs.next()) {
						person.addEmail(emailRs.getString("email"));
					}
					emailRs.close();
					emailPs.close();
				} catch (SQLException e) {
					System.out.println("SQLException: ");
					e.printStackTrace();
					throw new RuntimeException(e);
				} finally {
					// Close the connections that were opened in this method.
					ConnectionFactory.closeConnection(emailPs, emailRs);
				}
				personMap.put(personUuid, person);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			// Close the connections that were opened in this method.
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
		return personMap;
	}

//-------------------------------------------------------------------------------------------------	

	// This method queries the DataBase for each Customer object and returns a Map
	// of those Customer Objects.
	public static Map<String, Customer> getCustomerData() {
		Map<String, Customer> customerMap = new HashMap<String, Customer>();
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		String query = "SELECT customerUuid, customerType, customerName, personKey, addressKey FROM  Customer";

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
				Customer customer = null;

				// Determines the type of customer to put the data into
				if (customerType.equals("G")) {
					customer = new GovernmentCustomer(customerUuid, primaryContact, customerName, address);
				} else {
					customer = new CorporateCustomer(customerUuid, primaryContact, customerName, address);
				}
				customerMap.put(customerUuid, customer);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			// close the connections that were opened.
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
		return customerMap;
	}

//---------------------------------------------------------------------------------------------------------	

	// This method query's the database for all the product fields and returns a map
	// to products.
	public static Map<String, Product> getProductData() {
		Map<String, Product> productMap = new HashMap<String, Product>();
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// This query selects all of the column from the product table
		String query = "SELECT productKey, productUuid, productType, productName, "
				+ "personKey, hourlyFee, annualLicenseFee, serviceFee, pricePerUnit FROM Product";

		try {
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			// While there are still Products in the database continue
			while (rs.next()) {
				String productUuid = rs.getString("productUuid");
				String productType = rs.getString("productType");
				String productName = rs.getString("productName");
				Product product = null;

				// Determines the type of Product to put the product specific data into.
				if (productType.equals("C")) {
					int personKey = rs.getInt("personKey");
					// get the consultantPerson from the database from the personKey.
					Person consultantPerson = Person.getPersonByKey(personKey);
					double hourlyFee = rs.getDouble("hourlyFee");
					product = new Consultation(productUuid, productName, consultantPerson, hourlyFee);
				} else if (productType.equals("E")) {
					double pricePerUnit = rs.getDouble("pricePerUnit");
					product = new Equipment(productUuid, productName, pricePerUnit);
				} else {
					double annualLicenseFee = rs.getDouble("annualLicenseFee");
					double serviceFee = rs.getDouble("serviceFee");
					product = new License(productUuid, productName, annualLicenseFee, serviceFee);
				}
				productMap.put(productUuid, product);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			// Close the opened connections.
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
		return productMap;
	}

//----------------------------------------------------------------------------------------------	

	// This method takes an invoiceKey, query's the database and returns a list of
	// productData.
	public static List<Product> getProductList(int invoiceKey) {
		List<Product> productList = new ArrayList<Product>();
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// This query selects everything from a particular InvoiceProduct based on the
		// given invoiceKey.
		String query = "SELECT ip.numberOfUnits, ip.billableHours, ip.effectiveBeginDate, ip.effectiveEndDate, "
				+ "ip.productKey FROM InvoiceProduct ip JOIN Invoice i ON ip.invoiceKey = i.invoiceKey WHERE i.invoiceKey = ?";

		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, invoiceKey);
			rs = ps.executeQuery();
			// While there are still more products in this Invoice continue
			while (rs.next()) {
				int productKey = rs.getInt("productKey");
				Product product = Product.getProductByKey(productKey);
				String productType = product.getType();

				// Determines the type of Product to input the given data into the specific type
				// of product.
				if (productType.equals("E")) {
					int numberOfUnits = rs.getInt("numberOfUnits");
					product = new Equipment((Equipment) product, numberOfUnits);
				} else if (productType.equals("C")) {
					int billableHours = rs.getInt("billableHours");
					product = new Consultation((Consultation) product, billableHours);
				} else {
					String beginDate = rs.getString("effectiveBeginDate");
					String endDate = rs.getString("effectiveEndDate");
					int effectiveDays = License.getEffectiveDays(beginDate, endDate);
					product = new License((License) product, effectiveDays);
				}
				productList.add(product);
			}
		} catch (SQLException sqle) {
			throw new RuntimeException(sqle);
		} finally {
			// close the opened connections
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
		return productList;
	}

//----------------------------------------------------------------------------------------------

	// This method query's the database for all invoice data and returns a map of
	// invoice objects.
	public static Map<String, Invoice> getInvoiceData() {
		Map<String, Invoice> invoiceMap = new HashMap<String, Invoice>();
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// A query to get all the columns from the invoice table.
		String query = "SELECT invoiceKey, invoiceUuid, customerKey, personKey FROM Invoice";

		try {
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			// While there are still invoices in the database continue getting the database.
			while (rs.next()) {
				int invoiceKey = rs.getInt("invoiceKey");
				String invoiceUuid = rs.getString("invoiceUuid");
				int customerKey = rs.getInt("customerKey");
				// get the customer that relates to this particular invoice.
				Customer customer = Customer.getCustomerByKey(customerKey);
				int salesPersonKey = rs.getInt("personKey");
				// get the salesPerson that relates to this particular invoice.
				Person salesPerson = Person.getPersonByKey(salesPersonKey);
				// get the product list of the products that correspond to this particular
				// invoice
				List<Product> productList = getProductList(invoiceKey);
				Invoice invoice = new Invoice(invoiceUuid, customer, salesPerson, productList);
				invoiceMap.put(invoiceUuid, invoice);
			}
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			// close the opened connections
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
		return invoiceMap;
	}

//-----------------------------------------------------------------------------------------------------------

	// This method query's the database for all invoice data and prints the summary of the invoices
	public static void getInvoiceDataList(int listSort) {
		LinkedList<Invoice> invoiceList = new LinkedList<Invoice>();
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// A query to get all the columns from the invoice table.
		String query = "SELECT invoiceKey, invoiceUuid, customerKey, personKey FROM Invoice";

		try {
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			// While there are still invoices in the database continue getting the database.
			while (rs.next()) {
				int invoiceKey = rs.getInt("invoiceKey");
				String invoiceUuid = rs.getString("invoiceUuid");
				int customerKey = rs.getInt("customerKey");

				// get the customer that relates to this particular invoice.
				Customer customer = Customer.getCustomerByKey(customerKey);
				int salesPersonKey = rs.getInt("personKey");

				// get the salesPerson that relates to this particular invoice.
				Person salesPerson = Person.getPersonByKey(salesPersonKey);

				// get the product list of the products that correspond to this particular invoice
				List<Product> productList = getProductList(invoiceKey);
				Invoice invoice = new Invoice(invoiceUuid, customer, salesPerson, productList);

				// depending on the given type or sort we call that sorting method.
				Comparator<Invoice> comparator = null;
				if (listSort == 1) {
					comparator = new CustomerComparator<Invoice>();
					invoiceList.insertSorted(invoice, comparator);
				} else if (listSort == 2) {
					comparator = new TotalComparator<Invoice>();
					invoiceList.insertSorted(invoice, comparator);
				} else {
					comparator = new TypeThenNameComparator<Invoice>();
					invoiceList.insertSorted(invoice, comparator);
				}
			}
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			// close the opened connections
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
		//When done creating the linked list print the report.
		Report.printSummaryReport(invoiceList);
	}

}
