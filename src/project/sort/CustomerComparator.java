/**
 * Author: Matt Hotovy
 * Date: 4/19/2019
 * 
 * This class is used to compare two invoices by customerName
 */
package project.sort;

import project.lib.Customer;
import project.lib.Invoice;

public class CustomerComparator<T> extends Comparator<Invoice>{
	
	//This method takes two invoices by the customerNames
	public int compare(Invoice one, Invoice two) {
		Customer newCustomer = one.getCustomer();
		Customer currentCustomer = two.getCustomer();
		String newName = newCustomer.getCustomerName();
		String currentName = currentCustomer.getCustomerName();
		return newName.compareTo(currentName);
	}
}
