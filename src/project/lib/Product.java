/**
 * Author: Matt Hotovy
 * Date: 2/8/2019
 * 
 * This object is for the product data
 */

package project.lib;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import project.utils.ConnectionFactory;

public abstract class Product {

	protected String productUuid;
	protected String productName;

	public Product(String productUuid, String productName) {
		this.productUuid = productUuid;
		this.productName = productName;
	}
	
	public Product() {

	}

	public String getProductUuid() {
		return productUuid;
	}

	public String getProductName() {
		return productName;
	}

	@Override
	public String toString() {
		return "Product: " + this.productUuid + ", " + this.productName;
	}
	
//------------------------------------------------------------------------------
	
	//This method takes a productKey, query's the database and returns the corresponding product.
	public static Product getProductByKey(int productKey) {
		Product product = null;
		Connection conn = ConnectionFactory.getConnection();
		
		String query = "SELECT productKey, productUuid, productType, productName, personKey, hourlyFee, annualLicenseFee, "
				     + "serviceFee, pricePerUnit FROM Product WHERE productKey = ?";
		
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, productKey);
			rs = ps.executeQuery();
			if (rs.next()) {
				String productUuid = rs.getString("productUuid");
				String productType = rs.getString("productType");
				String productName = rs.getString("productName");
				
				//Determines the type of product.
				if (productType.equals("E")) {
					double pricePerUnit = rs.getDouble("pricePerUnit");
					product = new Equipment(productUuid, productName, pricePerUnit);
				} else if (productType.equals("C")) {
					int consultantPersonKey = rs.getInt("personKey");
					double hourlyFee = rs.getDouble("hourlyFee");
					Person consultantPerson = Person.getPersonByKey(consultantPersonKey);
					product = new Consultation(productUuid, productName, consultantPerson, hourlyFee);
				} else {
					double annualLicenseFee = rs.getDouble("annualLicenseFee");
					double serviceFee = rs.getDouble("serviceFee");
					product = new License(productUuid, productName, annualLicenseFee, serviceFee);
				}
			} else {
				throw new IllegalStateException("No such product in database with id = " + productKey);
			}
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
		return product;
	}
	
//--------------------------------------------------------------------------------------	

	//This method query's the database and returns a list of all the products in the database.
	public static List<Product> getAllProducts() {
		List<Product> products = new ArrayList<Product>();
		Connection conn = ConnectionFactory.getConnection();

		String query = "SELECT productKey, productUuid, productType, productName, personKey, hourlyFee, annualLicenseFee, serviceFee, pricePerUnit FROM Product";

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			while (rs.next()) {
				String productUuid = rs.getString("productUuid");
				String productType = rs.getString("productType");
				String productName = rs.getString("productName");
				Product product = null;
				
				//Determines the type of product.
				if (productType.equals("E")) {
					double pricePerUnit = rs.getDouble("pricePerUnit");
					product = new Equipment(productUuid, productName, pricePerUnit);
				} else if (productType.equals("C")) {
					int salesPersonKey = rs.getInt("personKey");
					double hourlyFee = rs.getDouble("hourlyFee");
					Person salesPerson = Person.getPersonByKey(salesPersonKey);
					product = new Consultation(productUuid, productName, salesPerson, hourlyFee);
				} else {
					double annualLicenseFee = rs.getDouble("annualLicenseFee");
					double serviceFee = rs.getDouble("serviceFee");
					product = new License(productUuid, productName, annualLicenseFee, serviceFee);
				}
				products.add(product);
			}
		} catch (SQLException sqle) {
			throw new RuntimeException(sqle);
		} finally {
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
		return products;
	}
	
//-----------------------------------------------------------------------------------------	
	
	// this method query's the database and returns a list of all the data in the invoiceProduct database table
	public static List<Product> getReportProducts() {
		List<Product> products = new ArrayList<Product>();
		Connection conn = ConnectionFactory.getConnection();

		String query = "SELECT p.productKey, p.productUuid, p.productType, p.name, p.personKey, "
				     + "p.hourlyFee, p.annualLicenseFee, p.serviceFee, p.pricePerUnit,  FROM "
				     + "Product p JOIN InvoiceProduct ip ON p.productKey = ip.productKey "
				     + "JOIN Invoice i ON ip.invoiceKey = i.invoiceKey";

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			while (rs.next()) {
//				int productKey = rs.getInt("productKey");
				String productUuid = rs.getString("productUuid");
				String productType = rs.getString("productType");
				String name = rs.getString("name");
				int personKey = rs.getInt("personKey");
				double hourlyFee = rs.getDouble("hourlyFee");
				double annualLicenseFee = rs.getDouble("annualLicenseFee");
				double serviceFee = rs.getDouble("serviceFee");
				double pricePerUnit = rs.getDouble("pricePerUnit");
				Product pr = null;
				
				//Determines the type of product it is.
				if (productType.equals("E")) {
					pr = new Equipment(productUuid, name, pricePerUnit);
				} else if (productType.equals("C")) {
					Person p = Person.getPersonByKey(personKey);
					pr = new Consultation(productUuid, name, p, hourlyFee);
				} else {
					pr = new License(productUuid, name, annualLicenseFee, serviceFee);
				}
				products.add(pr);
			}
		} catch (SQLException sqle) {
			throw new RuntimeException(sqle);
		} finally {
			ConnectionFactory.closeConnection(conn, ps, rs);
		}
		return products;
	}

	// abstract classes for subclasses
	public abstract String getType();

	public abstract double getTaxRate();

	public abstract double getServiceFee();

//	public abstract double getProductCost();

	public abstract String getUnitsString();

	public abstract String getPerUnit();
	
	public abstract int getProductData();
	
	public abstract double getProductPrice();
	
	public abstract double getSubTotal();
	

}