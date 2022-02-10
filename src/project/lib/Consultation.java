/**
 * Author: Matt Hotovy
 * Date: 2/8/2019
 * 
 * This object is for the consultation data
 */

package project.lib;

public class Consultation extends Product {

	private Person consultantPerson;
	private double hourlyFee;
	private int billableHours;

	public Consultation(String productUuid, String productName, Person consultantPerson, double hourlyFee) {
		super(productUuid, productName);
		this.consultantPerson = consultantPerson;
		this.hourlyFee = hourlyFee;
	}

	// Copy Constructor
	public Consultation(Consultation customer, int billableHours) {
		super(customer.getProductUuid(), customer.getProductName());
		this.consultantPerson = customer.getConsultantPerson();
		this.hourlyFee = customer.getHourlyFee();
		this.billableHours = billableHours;
	}

	public int getBillableHours() {
		return billableHours;
	}

	public String getType() {
		return "C";
	}

	public Person getConsultantPerson() {
		return consultantPerson;
	}

	public double getHourlyFee() {
		return hourlyFee;
	}

	// Returns the tax rate for a Consultation.
	public double getTaxRate() {
		return .0425;
	}

	// Returns the service fee for a Consultation.
	public double getServiceFee() {
		return 150.00;
	}

	public double getSubTotal() {
		return (hourlyFee * billableHours);
	}

	public int getProductData() {
		return billableHours;
	}

	public double getProductPrice() {
		return hourlyFee;
	}

	public String getUnitsString() {
		return "Hours @";
	}

	public String getPerUnit() {
		return "/hour";
	}

}
