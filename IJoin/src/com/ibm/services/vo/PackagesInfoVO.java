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
public class PackagesInfoVO {
	@JsonProperty("Description") private String description;
	@JsonProperty("Keyword") private String keyword;
	@JsonProperty("Tariff") private String tariff;
	@JsonProperty("Quota") private String quota;
	@JsonProperty("PrepaidFlag") private String prepaidFlag;
	@JsonProperty("PostpaidFlag") private String postpaidFlag;
	@JsonProperty("DayByBalanceParam") private String payByBalanceParam;
	@JsonProperty("DompetkuParam") private String dompetkuParam;
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the keyword
	 */
	public String getKeyword() {
		return keyword;
	}
	/**
	 * @param keyword the keyword to set
	 */
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	/**
	 * @return the tariff
	 */
	public String getTariff() {
		return tariff;
	}
	/**
	 * @param tariff the tariff to set
	 */
	public void setTariff(String tariff) {
		this.tariff = tariff;
	}
	/**
	 * @return the quota
	 */
	public String getQuota() {
		return quota;
	}
	/**
	 * @param quota the quota to set
	 */
	public void setQuota(String quota) {
		this.quota = quota;
	}
	/**
	 * @return the prepaidFlag
	 */
	public String getPrepaidFlag() {
		return prepaidFlag;
	}
	/**
	 * @param prepaidFlag the prepaidFlag to set
	 */
	public void setPrepaidFlag(String prepaidFlag) {
		this.prepaidFlag = prepaidFlag;
	}
	/**
	 * @return the postpaidFlag
	 */
	public String getPostpaidFlag() {
		return postpaidFlag;
	}
	/**
	 * @param postpaidFlag the postpaidFlag to set
	 */
	public void setPostpaidFlag(String postpaidFlag) {
		this.postpaidFlag = postpaidFlag;
	}
	/**
	 * @return the payByBalanceParam
	 */
	public String getPayByBalanceParam() {
		return payByBalanceParam;
	}
	/**
	 * @param payByBalanceParam the payByBalanceParam to set
	 */
	public void setPayByBalanceParam(String payByBalanceParam) {
		this.payByBalanceParam = payByBalanceParam;
	}
	/**
	 * @return the dompetkuParam
	 */
	public String getDompetkuParam() {
		return dompetkuParam;
	}
	/**
	 * @param dompetkuParam the dompetkuParam to set
	 */
	public void setDompetkuParam(String dompetkuParam) {
		this.dompetkuParam = dompetkuParam;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PackagesInfoVO [description=" + description + ", keyword=" + keyword + ", tariff=" + tariff + ", quota="
				+ quota + ", prepaidFlag=" + prepaidFlag + ", postpaidFlag=" + postpaidFlag + ", payByBalanceParam="
				+ payByBalanceParam + ", dompetkuParam=" + dompetkuParam + "]";
	}
	
	
	
	
}