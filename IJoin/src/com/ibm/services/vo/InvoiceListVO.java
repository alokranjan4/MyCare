/**
 * 
 */
package com.ibm.services.vo;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author IBM_ADMIN
 *
 */
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class InvoiceListVO {
	
	@JsonProperty("BillingAccountNumber")private String billingAccountNumber;
	@JsonProperty("ChannelType")private String channelType;
	@JsonProperty("ErrorCode")private String errorCode;
	@JsonProperty("ErrorDescription")private String errorDescription;
	@JsonProperty("Status")private String status;
	@JsonProperty("Msisdn")private String msisdn;
	
	@JsonProperty("InvoicesList")private List<InvoiceVO> invoicesList;

	/**
	 * @return the billingAccountNumber
	 */
	public String getBillingAccountNumber() {
		return billingAccountNumber;
	}

	/**
	 * @param billingAccountNumber the billingAccountNumber to set
	 */
	public void setBillingAccountNumber(String billingAccountNumber) {
		this.billingAccountNumber = billingAccountNumber;
	}

	/**
	 * @return the channelType
	 */
	public String getChannelType() {
		return channelType;
	}

	/**
	 * @param channelType the channelType to set
	 */
	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return the errorDescription
	 */
	public String getErrorDescription() {
		return errorDescription;
	}

	/**
	 * @param errorDescription the errorDescription to set
	 */
	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	/**
	 * @return the invoicesList
	 */
	public List<InvoiceVO> getInvoicesList() {
		return invoicesList;
	}

	/**
	 * @param invoicesList the invoicesList to set
	 */
	public void setInvoicesList(List<InvoiceVO> invoicesList) {
		this.invoicesList = invoicesList;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "InvoiceListVO [billingAccountNumber=" + billingAccountNumber + ", channelType=" + channelType
				+ ", errorCode=" + errorCode + ", errorDescription=" + errorDescription + ", status=" + status
				+ ", msisdn=" + msisdn + ", invoicesList=" + invoicesList + "]";
	}
	
	

}
