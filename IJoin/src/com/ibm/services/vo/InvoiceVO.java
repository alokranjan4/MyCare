/**
 * 
 */
package com.ibm.services.vo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author IBM_ADMIN
 *
 */
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class InvoiceVO {
	@JsonProperty("InvoiceDate")private String invoiceDate;
	@JsonProperty("Amount")private String amount;
	@JsonProperty("InvoiceNumber")private java.lang.String invoiceNumber;
	@JsonProperty("InvoiceNetValue")private long invoiceNetValue;
	@JsonProperty("InvoiceTaxValue")private long invoiceTaxValue;
	@JsonProperty("InvoiceValue")private long invoiceValue;
	@JsonProperty("BillDate")private java.lang.String billDate;
	@JsonProperty("DueDate")private java.lang.String dueDate;
	@JsonProperty("DebtAge")private int debtAge;
	
	public java.lang.String getInvoiceNumber() {
		return invoiceNumber;
	}
	public void setInvoiceNumber(java.lang.String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}
	public long getInvoiceNetValue() {
		return invoiceNetValue;
	}
	public void setInvoiceNetValue(long invoiceNetValue) {
		this.invoiceNetValue = invoiceNetValue;
	}
	public long getInvoiceTaxValue() {
		return invoiceTaxValue;
	}
	public void setInvoiceTaxValue(long invoiceTaxValue) {
		this.invoiceTaxValue = invoiceTaxValue;
	}
	public long getInvoiceValue() {
		return invoiceValue;
	}
	public void setInvoiceValue(long invoiceValue) {
		this.invoiceValue = invoiceValue;
	}
	public java.lang.String getBillDate() {
		return billDate;
	}
	public void setBillDate(java.lang.String billDate) {
		this.billDate = billDate;
	}
	public java.lang.String getDueDate() {
		return dueDate;
	}
	public void setDueDate(java.lang.String dueDate) {
		this.dueDate = dueDate;
	}
	public int getDebtAge() {
		return debtAge;
	}
	public void setDebtAge(int debtAge) {
		this.debtAge = debtAge;
	}
	/**
	 * @return the invoiceDate
	 */
	public String getInvoiceDate() {
		return invoiceDate;
	}
	/**
	 * @param invoiceDate the invoiceDate to set
	 */
	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	/**
	 * @return the amount
	 */
	public String getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(String amount) {
		this.amount = amount;
	}
	@Override
	public String toString() {
		return "InvoiceVO [invoiceDate=" + invoiceDate + ", amount=" + amount + ", invoiceNumber=" + invoiceNumber
				+ ", invoiceNetValue=" + invoiceNetValue + ", invoiceTaxValue=" + invoiceTaxValue + ", invoiceValue="
				+ invoiceValue + ", billDate=" + billDate + ", dueDate=" + dueDate + ", debtAge=" + debtAge + "]";
	}
}
