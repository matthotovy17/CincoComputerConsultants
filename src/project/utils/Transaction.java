/**
 * Author: Chloe Galinsky, Matt Hotovy
 * Date: 2/8/2019
 * 
 * This class performs the operations on the invoices
 */

package project.utils;

import project.lib.Customer;
import project.lib.Invoice;
import project.lib.Product;

public class Transaction {

	// returns the taxes
	public static double getTaxes(Product product, Customer customer) {
		return ((product.getTaxRate() * customer.getSalesTax()) * product.getSubTotal());
	}

	// returns the total
	public static double getTotal(Product product, Customer customer) {
		return (getTaxes(product, customer) + product.getSubTotal());
	}
	
	// returns the total by a given invoice.
	public static double totalByProductList(Invoice invoice) {
		double total = 0.00;
		Customer customer = invoice.getCustomer();
		for (Product product : invoice.getProductList()) {
			total += Transaction.getTotal(product, customer);
		}
		return total;
	}
}
