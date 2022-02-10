/**
 * Author: Matt Hotovy
 * Date: 2/8/2019
 * 
 * This object is a subclass of product
 * and stores extra data that is specific to the Equipment object
 */

package project.lib;

public class Equipment extends Product {

	private double pricePerUnit;
	private int numberOfUnits;

	public Equipment(String productUuid, String productName, double pricePerUnit) {
		super(productUuid, productName);
		this.pricePerUnit = pricePerUnit;
	}

	// Copy Constructor
	public Equipment(Equipment e, int numberOfUnits) {
		super(e.getProductUuid(), e.getProductName());
		this.pricePerUnit = e.getPricePerUnit();
		this.numberOfUnits = numberOfUnits;
	}

	public String getType() {
		return "E";
	}

	public double getPricePerUnit() {
		return pricePerUnit;
	}

	public int getProductData() {
		return numberOfUnits;
	}

	public double getProductPrice() {
		return pricePerUnit;
	}

	public double getSubTotal() {
		return (pricePerUnit * numberOfUnits);
	}
	
	// returns the numberOfUnits which comes from the InvoiceProduct table
	public int getNumberOfUnits() {
		return numberOfUnits;
	}

	// returns the tax rate for an equipment product which is 7%.
	public double getTaxRate() {
		return .07;
	}

	// equipment does not have a service fee
	public double getServiceFee() {
		return 0.00;
	}

	public String getUnitsString() {
		return "units @";
	}

	public String getPerUnit() {
		return "/unit";
	}

}
