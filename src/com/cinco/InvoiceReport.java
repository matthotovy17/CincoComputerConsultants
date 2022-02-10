/**
 * Author: Matt Hotovy
 * Date: 4/19/2019
 * Added to GitHub 02/10/2022
 * 
 * This class runs the invoice program
 */

package com.cinco;

import project.utils.DatabaseReader;

public class InvoiceReport {

	// This is the Driver class for the Program.
	public static void main(String[] args) {

		System.out.println("BY CUSTOMER NAME");
		DatabaseReader.getInvoiceDataList(1);
		System.out.println("BY INVOICE TOTAL");
		DatabaseReader.getInvoiceDataList(2);
		System.out.println("BY CUSTOMER TYPE - SALESPERSON");
		DatabaseReader.getInvoiceDataList(3);

	}
}
