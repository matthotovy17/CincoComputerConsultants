/**
 * Author: Chloe Galinsky, Matt Hotovy
 * Date: 2/8/2019
 * 
 * This class is for printing the reports
 */

package project.utils;

import java.util.Map;

import project.lib.Address;
import project.lib.Country;
import project.lib.Customer;
import project.lib.Invoice;
import project.lib.Person;
import project.lib.Product;
import project.lib.State;
import project.sort.LinkedList;

public class Report {

	// this method takes an invoiceList and prints the executive summary report to
	// the standard output.
	public static void printSummaryReport(LinkedList<Invoice> invoiceList) {
		StringBuilder sb = new StringBuilder();
		//create the outline for the executive summary
		System.out.println("Executive Summary Report \n=========================");
		sb.append(String.format("%-12s %-40s %-35s %-20s %-15s %-15s %-15s \n%s %13s %43s %32s %17s %15s %15s",
				"Invoice", "Customer", "SalesPerson", "Subtotal", "Fees", "Taxes", "Total", "--------", "----------",
				"-------------", "----------", "--------", "-------", "-------"));
		System.out.println(sb);

		double sumSubTotal = 0.00, sumFees = 0.00, sumTaxes = 0.00, sumTotal = 0.00, sumComplianceFee = 0.0;
		
		//go through each invoice in the invoice linkedList
		for(Invoice invoice : invoiceList) {
			Customer customer = invoice.getCustomer();
			Person salesPerson = invoice.getSalesPerson();
			double subTotal = 0.00, serviceFees = 0.00, taxes = 0.00, total = 0.00, complianceFee = 0.00;

			// Goes through each product in the invoice and adds the up the Transaction data for each invoice.
			for (Product product : invoice.getProductList()) {
				subTotal += product.getSubTotal();
				complianceFee = customer.getComplianceFee();
				serviceFees += product.getServiceFee();
				taxes += Transaction.getTaxes(product, customer);
				total += Transaction.getTotal(product, customer);
			}

			// Add up all the subTotals for each invoice for Totals output
			sumSubTotal += subTotal;
			serviceFees += complianceFee;
			sumFees += serviceFees;
			sumTaxes += taxes;
			sumTotal += total;
			total += serviceFees;

			printIndiv(invoice, customer, salesPerson, subTotal, serviceFees, taxes, total);
		}
		printTotals(sumSubTotal, sumFees, sumComplianceFee, sumTaxes, sumTotal);
	}

//----------------------------------------------------------------------------------------------------------------------------------------

	// This method takes all 4 maps that are created and prints the detailed summary
	// report of each invoice and prints it to the standard output
	public static void printDetailedReport(Map<String, Invoice> invoiceMap) {
		System.out.println("Individual Invoice Detail Reports \n=================================");

		// reads through each invoice instance and gets the data needed for the detailed
		// report.
		for (Map.Entry<String, Invoice> entry : invoiceMap.entrySet()) {
			double sumSubTotal = 0.00, sumFees = 0.00, sumTaxes = 0.00, sumTotal = 0.00, sumComplianceFee = 0.00;

			String key = entry.getKey();
			Invoice invoice = invoiceMap.get(key);
			Customer customer = invoice.getCustomer();
			Person primaryContact = customer.getPrimaryContact();
			Address address = customer.getAddress();
			State state = address.getState();
			Country country = state.getCountry();
			Person salesPerson = invoice.getSalesPerson();
			double subTotal = 0.00, taxes = 0.00, complianceFee = 0.00, fees = 0.00;

			printDetailIndiv(invoice, salesPerson, customer, primaryContact, address, state, country);

			// reads through the product list for the invoice and gets the Transaction data
			// for each invoice
			for (Product product : invoice.getProductList()) {
				subTotal = product.getSubTotal();
				complianceFee = customer.getComplianceFee();
				taxes = Transaction.getTaxes(product, customer);
				fees = product.getServiceFee();
				
				StringBuilder sb = new StringBuilder();
				sb.append(String.format("(%d %s $%.2f%-6s)", product.getProductData(), product.getUnitsString(), product.getProductPrice(), product.getPerUnit()));
				
				//print out each product that is in the Invoice along with transaction data.
				System.out.printf("%-8s %-35s %-50s $%10.2f  $%10.2f\n", product.getProductUuid(),
						product.getProductName(), sb, product.getServiceFee(), subTotal);
				sumSubTotal += subTotal;
				sumFees += fees;
				sumTaxes += taxes;
			}

			sumComplianceFee = complianceFee;
			sumFees += complianceFee;
			sumTotal = sumFees + sumTaxes + sumSubTotal;

			// print formatting for totals of each invoice
			System.out.println(
					"=======================================================================\t\t\t\t===========================");
			System.out.printf("%-95s $%10.2f  $%10.2f\n", "SUB-TOTALS", sumFees, sumSubTotal);
			System.out.printf("%-108s $%10.2f\n", "COMPLIANCE FEE", sumComplianceFee);
			System.out.printf("%-108s $%10.2f\n", "TAXES", sumTaxes);
			System.out.printf("%-108s $%10.2f\n", "TOTAL", sumTotal);
			System.out.printf("\n\n");
		}
	}

//-------------------------------------------------------------------------------------------------------------------------------

	// This method prints out the individual invoices for the summary report method.
	public static void printIndiv(Invoice invoice, Customer customer, Person salesPerson, double subTotal,
			double serviceFees, double taxes, double total) {
		StringBuilder sb = new StringBuilder();

		// print formatting
		subTotal = Math.round(subTotal * 100.00) / 100.00;
		serviceFees = Math.round(serviceFees * 100.00) / 100.00;
		taxes = Math.round(taxes * 100.00) / 100.00;
		total = Math.round(total * 100.00) / 100.00;
		sb.append(String.format("%-12s %-40s %-29s $%13.2f $%16.2f $%14.2f $%14.2f", invoice.getInvoiceUuid(),
				customer.getCustomerName(), salesPerson.getName(), subTotal, serviceFees, taxes, total));

		System.out.println(sb);
	}

//------------------------------------------------------------------------------------------------------------------------------------

	// This method is used to print out the sum of each column in the Executive
	// summary report method.
	public static void printTotals(double sumSubTotal, double sumFees, double sumComplianceFee, double sumTaxes,
			double sumTotal) {
		StringBuilder sb = new StringBuilder();
		sb.append("===================================================================="
				+ "====================================================================================\n");

		sumComplianceFee = Math.round(sumComplianceFee * 100.00) / 100.00;
		sumSubTotal = Math.round(sumSubTotal * 100.00) / 100.00;
		sumFees = Math.round(sumFees * 100.00) / 100.00;
		sumTaxes = Math.round(sumTaxes * 100.00) / 100.00;
		sumTotal = Math.round(sumTotal * 100.00) / 100.00;
		sb.append(String.format("%-83s $%13.2f $%16.2f $%14.2f $%14.2f\n\n\n", "TOTALS", sumSubTotal,
				sumFees + sumComplianceFee, sumTaxes, sumTotal + sumFees));

		System.out.println(sb);
	}

//--------------------------------------------------------------------------------------------------------------------------	

	//This method prints out the individual data for each invoice.
	public static void printDetailIndiv(Invoice invoice, Person salesPerson, Customer customer, Person primaryContact, Address address,
			State state, Country country) {
		System.out.println("Invoice " + invoice.getInvoiceUuid());
		System.out.println("=================");
		System.out.println("Salesperson: " + salesPerson.getName());
		System.out.println("Customer Info:");
		System.out.printf("  %s (%s)\n", customer.getCustomerName(), customer.getCustomerUuid());
		System.out.printf("  %s\n", primaryContact.getName());
		System.out.printf("  %s\n  %s %s %s %s\n", address.getStreet(), address.getCity(), state.getStateName(),
				address.getZip(), country.getCountryName());
		System.out.println("----------------------------------------");
		System.out.printf("%-13s %-86s %-12s %s\n%-12s %-86s %-12s %s\n", "Code", "Item", "Fees", "Total", "-----",
				"------", "------", "-------");
	}

}
