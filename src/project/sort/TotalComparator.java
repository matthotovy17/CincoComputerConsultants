/**
 * Author: Matt Hotovy
 * Date: 4/19/2019
 * 
 * This class is a comparator for the linked list
 */
package project.sort;

import project.lib.Customer;
import project.lib.Invoice;
import project.lib.Product;
import project.utils.Transaction;

public class TotalComparator<T> extends Comparator<Invoice> {

	//comparator for the totals in each invoice.
	public int compare(Invoice one, Invoice two) {
		double total1 = 0.00;
		Customer customer = one.getCustomer();
		//go through each product in the given invoice.
		for (Product product : one.getProductList()) {
			total1 += Transaction.getTotal(product, customer);
		}

		double total2 = 0.00;
		Customer customer2 = two.getCustomer();
		//go through each product in the given invoice.
		for (Product product : two.getProductList()) {
			total2 += Transaction.getTotal(product, customer2);
		}

		//in descending order return corresponding values
		if (total1 > total2) {
			return -1;
		} else if (total1 < total2) {
			return 1;
		} else {
			return 0;
		}
	}

}
