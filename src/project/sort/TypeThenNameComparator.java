/**
 * Author: Matt Hotovy
 * Date: 4/19/2019
 * 
 * This class is a comparator for the linkedList
 */
package project.sort;

import project.lib.Customer;
import project.lib.Invoice;
import project.lib.Person;

public class TypeThenNameComparator<T> extends Comparator<Invoice>{
	
	//Comparator for the customer type then salesPerson name
	public int compare(Invoice one, Invoice two) {
		Customer newCustomer = one.getCustomer();
		Customer currentCustomer = two.getCustomer();
		String newType = newCustomer.getType();
		String currentType = currentCustomer.getType();
		
		//if the types are the same then compare by the salesperson name.
		if(newType.compareTo(currentType) == 0) {
			Person newPerson = one.getSalesPerson();
			Person currentPerson = two.getSalesPerson();
			String newName = newPerson.getLastName();
			String currentName = currentPerson.getLastName();
			return newName.compareTo(currentName);
		} else {
			return newType.compareTo(currentType);
		}
	
	}
}
