/**
 * Author: Matt Hotovy
 * Date: 2/8/2019
 * 
 * This object is for the government customer data from the customer data
 */

package project.lib;

public class GovernmentCustomer extends Customer {
	

	public GovernmentCustomer(String customerUuid, Person primaryContact, String customerName, Address address) {
		super(customerUuid, primaryContact, customerName, address);
	}

	public String getType() {
		return "G";
	}

	// Government customers have a $125 compliance fee per invoice
	public double getComplianceFee() {
		return 125.00;
	}

	// Government customers have no sales tax on any products
	public double getSalesTax() {
		return 0.00;
	}
}
