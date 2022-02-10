/**
 * Author: Matt Hotovy
 * Date: 4/19/2019
 * 
 * This object is for the invoice data
 */

package project.lib;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import project.utils.ConnectionFactory;
import project.utils.DatabaseReader;

public class Invoice {

	private String invoiceUuid;
	private Customer customer;
	private Person salesPerson;
	private List<Product> productList = new ArrayList<Product>();

	//Constructor
	public Invoice(String invoiceUuid, Customer customer, Person salesPerson, List<Product> productList) {
		this.invoiceUuid = invoiceUuid;
		this.customer = customer;
		this.salesPerson = salesPerson;
		this.productList = productList;
	}
	
	public Invoice(String invoiceUuid, Customer customer, Person salesPerson) {
		this.invoiceUuid = invoiceUuid;
		this.customer = customer;
		this.salesPerson = salesPerson;
	}

	public String getInvoiceUuid() {
		return invoiceUuid;
	}

	public Customer getCustomer() {
		return customer;
	}
	
	//method to return a customers name by a particular invoice.
	public String getCustomerName() {
		return this.customer.getCustomerName();
	}

	public Person getSalesPerson() {
		return salesPerson;
	}
	
	//method returns an invoices productList
	public List<Product> getProductList() {
		return productList;
	}
	
	//method to add a product to the invoices productList
	public void addProduct(Product p) {
		this.productList.add(p);
	}
	
	@Override
	public String toString() {
		return "invoiceUuid = "+this.invoiceUuid+", Customer = "+this.customer+", SalesPerson = "+this.salesPerson;
	}
	
//--------------------------------------------------------------------------------------------------
	
	// This method takes an invoiceKey and query's the database and returns the corresponding invoice.
	public static Invoice getInvoiceByKey(int invoiceKey) {
		Invoice invoice = null;
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String query = "SELECT invoiceKey, invoiceUuid, customerKey, personKey "
					 + "FROM Invoice WHERE invoiceKey = ?";
		
		// this try/catch executes the query from above to get the invoice.
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, invoiceKey);
			rs = ps.executeQuery();
			// "if" executes if the above query finds an invoice in the database.
			if(rs.next()) {
				String invoiceUuid = rs.getString("invoiceUuid");
				int customerKey = rs.getInt("customerKey");
				int personKey = rs.getInt("personKey");
				Customer customer = Customer.getCustomerByKey(customerKey);
				Person salesPerson = Person.getPersonByKey(personKey);
				invoice = new Invoice(invoiceUuid, customer, salesPerson);
				
				//Inner query to retrieve the productKey to store the product list in invoice
				String plQuery = "SELECT productKey FROM InvoiceProduct WHERE invoiceKey = ?";
				
				PreparedStatement ps2 = null;
				ResultSet rs2 = null;
				
				//This try/catch executes the query from above to return a list of products on a given invoiceKey.
				try {
					ps2 = conn.prepareStatement(plQuery);
					ps2.setInt(1, invoiceKey);
					rs2 = ps2.executeQuery();
					// "if" executes if the corresponding product is in the database
					while(rs2.next()) {
						int productKey = rs2.getInt("productKey");
						Product product = Product.getProductByKey(productKey);
						invoice.addProduct(product);
					}
				} catch (SQLException e) {
					System.out.println("SQLException: ");
					e.printStackTrace();
					throw new RuntimeException(e);
				} finally {
					//Close the connections opened in this method.
					ConnectionFactory.closeConnection(ps2, rs2);
				}
			} else {
				throw new IllegalStateException("No such invoice in database with id = " + invoiceKey);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			//Close the connections opened in this method.
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
		return invoice;
	}
	
//-----------------------------------------------------------------------------------
	
	//This method query's the database and returns a list of all the invoices in the database.
	public static List<Invoice> getAllInvoices() {
		List<Invoice> invoiceList = new ArrayList<Invoice>();
		Connection conn = ConnectionFactory.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String query = "SELECT invoiceKey, invoiceUuid, customerKey, personKey FROM Invoice";
		
		//This try/catch executes the query from above to retrieve all invoices in the DB
		try {
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			//while there are still invoices in the database continue
			while (rs.next()) {
				int invoiceKey = rs.getInt("invoiceKey"); 
				String invoiceUuid = rs.getString("invoiceUuid");
				int customerKey = rs.getInt("customerKey");
				int personKey = rs.getInt("personKey");
				
				//Get the corresponding customer, salesperson, and productList from the database
				Customer customer = Customer.getCustomerByKey(customerKey);
				Person salesPerson = Person.getPersonByKey(personKey);
				List<Product> productList = DatabaseReader.getProductList(invoiceKey);
				
				Invoice invoices = new Invoice(invoiceUuid, customer, salesPerson, productList);
				invoiceList.add(invoices);
			}
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			//Close the connections that were opened in this method.
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
		return invoiceList;
	}

}
