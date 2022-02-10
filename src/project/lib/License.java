/**
 * Author: Matt Hotovy
 * Date: 2/8/2019
 * 
 * This class is a subclass of product
 */

package project.lib;

import org.joda.time.DateTime;
import org.joda.time.Days;

public class License extends Product {

	private double annualLicenseFee;
	private double serviceFee;
	private int effectiveDays;

	public License(String productUuid, String productName, double annualLicenseFee, double serviceFee) {
		super(productUuid, productName);
		this.annualLicenseFee = annualLicenseFee;
		this.serviceFee = serviceFee;
	}

	// Copy Constructor
	public License(License license, int effectiveDays) {
		super(license.getProductUuid(), license.getProductName());
		this.annualLicenseFee = license.getAnnualLicenseFee();
		this.serviceFee = license.getServiceFee();
		this.effectiveDays = effectiveDays;
	}

	public License() {

	}

	public String getType() {
		return "L";
	}

	public double getAnnualLicenseFee() {
		return annualLicenseFee;
	}

	public double getServiceFee() {
		return serviceFee;
	}

	// Licenses have a 4.25% tax rate
	public double getTaxRate() {
		return .0425;
	}

	public int getEffectiveDays() {
		return effectiveDays;
	}

	// gets the number of days between two ISO dates.
	public static int getEffectiveDays(String beginDate, String endDate) {
		DateTime beginDt = new DateTime(beginDate);
		DateTime endDt = new DateTime(endDate);

		return Days.daysBetween(beginDt, endDt).getDays();
	}

	// returns the cost without external fees or taxes.
	public double getSubTotal() {
		return (annualLicenseFee * (effectiveDays / 365.0));
	}
	
	public int getProductData() {
		return effectiveDays;
	}

	public double getProductPrice() {
		return annualLicenseFee;
	}

	public String getUnitsString() {
		return "Days @";
	}

	public String getPerUnit() {
		return "/year";
	}

}