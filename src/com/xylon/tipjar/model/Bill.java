package com.xylon.tipjar.model;

import android.widget.Toast;

public class Bill {
	private double billAmt = 0.0;
	private int tipPercent;
	private double tipAmt;
	private double totalAmt;
	private int splitBy; // number of people to split with
	private double taxAmount = 0; // service tax by the county/city

	public double getBillAmt() {
		return billAmt;
	}

	public void setBillAmt(double billAmt) {
		this.billAmt = billAmt;
	}

	public int getTipPercent() {
		return tipPercent;
	}

	public void setTipPercent(int tipPercent) {
		this.tipPercent = tipPercent;
	}

	public double getTipAmt() {
		return tipAmt;
	}

	public void setTipAmt(double tipAmt) {
		this.tipAmt = tipAmt;
	}

	public double getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(double totalAmt) {
		this.totalAmt = totalAmt;
	}

	public int getSplitBy() {
		return splitBy;
	}

	public void setSplitBy(int numPeople) {
		this.splitBy = numPeople ;
	}

	public double getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(double taxAmount) {
		this.taxAmount = taxAmount;
	}

	public void calculateTipFromBillAmount(String billAmtStr) {
		billAmt = 0.0;
		if (!(billAmtStr.equals(""))) {
			billAmt = roundOff(Double.parseDouble(billAmtStr));
			if (billAmt > 0) {
				tipAmt = roundOff(((billAmt * tipPercent) / 100.0) / splitBy);
				totalAmt = (billAmt / splitBy) + tipAmt + taxAmount;
			}
		}
	}

	public void calculateTipFromTotalAmount(String totalAmtStr) {
		if (billAmt == 0.0) {
			// Toast.makeText(getApplicationContext(),
			// "Please enter the bill amount first.", Toast.LENGTH_LONG).show();
		} else {
			totalAmt = roundOff(Double.parseDouble(totalAmtStr));
			if (totalAmt - billAmt < 0.1) {
				// Toast.makeText(getApplicationContext(),
				// "Total Amount entered < Bill amount",
				// Toast.LENGTH_LONG).show();
			} else { 
				double perPersonbillAmt = billAmt/splitBy;
				tipAmt = totalAmt - perPersonbillAmt; // totalAmt is for one person
				tipPercent = (int) Math.ceil((tipAmt * 100) / perPersonbillAmt); // round up to the nearest integer
				if (tipPercent > 100) tipPercent = 100;
			}
		}
	}

	public void calculateTotalFromTipAmount(String tipAmtStr) {
		tipAmt = roundOff(Double.parseDouble(tipAmtStr));
		if (billAmt == 0.0) {
			// Toast.makeText(getApplicationContext(),
			// "Please enter the bill amount first.", Toast.LENGTH_LONG).show();
		}
		if (tipAmt == 0)
			tipPercent = 0;
		if (tipAmt > 0) {
			double perPersonbillAmt = billAmt/splitBy;
			tipPercent = (int) Math.ceil((tipAmt * 100) / perPersonbillAmt);
			totalAmt = roundOff(perPersonbillAmt + tipAmt);
			// round up to the nearest integer
			if (tipPercent > 100)
				tipPercent = 100;
		}
	}
	
	public void removeTaxFromBillAmount(String taxRate) {
		if (taxRate != null) {
			taxAmount = roundOff((Float.parseFloat(taxRate) * billAmt) / 100.0);
			billAmt -= taxAmount;
		}
	}
	
	public void addTaxToBillAmount() {
		billAmt += taxAmount;
		taxAmount = 0;
	}

	private static double roundOff(double a) {
		return (Math.round(a * 100.0) / 100.0);
	}
}
