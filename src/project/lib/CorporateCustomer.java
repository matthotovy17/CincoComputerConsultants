/**
 * Author: Matt Hotovy
 * Date: 2/8/2019
 * 
 * This object is for the corporate customer data from the customer data
 */

package project.lib;

public class CorporateCustomer extends Customer {

	public CorporateCustomer(String customerUuid, Person primaryContactUuid, String name, Address address) {
		super(customerUuid, primaryContactUuid, name, address);
	}

	public String getType() {
		return "Corporate";
	}

	public double getComplianceFee() {
		return 0.00;
	}

	public double getSalesTax() {
		return 1.00;
	}

}
