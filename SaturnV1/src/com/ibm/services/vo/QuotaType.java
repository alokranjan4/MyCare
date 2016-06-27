/**
 * 
 */
package com.ibm.services.vo;

import java.io.Serializable;

/**
 * @author Aadam
 *
 */
public class QuotaType implements Serializable{
	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private String rawInitialQuota;
	private String rawAditionalQuota;
	private String rawUsedQuota;
	private String rawRemainingQuota;
	private String initialQuota;
	private String additionalQuota;
	private String usedQuota;
	private String remainingQuota;
	private String quotaUnit;
	private String benefitType;
	private String expiryDate;
	private String quotaSource;
	private String show;
	private String unlimitedFlag;
	  
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getRawInitialQuota() {
		return rawInitialQuota;
	}
	public void setRawInitialQuota(String rawInitialQuota) {
		this.rawInitialQuota = rawInitialQuota;
	}
	public String getRawAditionalQuota() {
		return rawAditionalQuota;
	}
	public void setRawAditionalQuota(String rawAditionalQuota) {
		this.rawAditionalQuota = rawAditionalQuota;
	}
	public String getRawUsedQuota() {
		return rawUsedQuota;
	}
	public void setRawUsedQuota(String rawUsedQuota) {
		this.rawUsedQuota = rawUsedQuota;
	}
	public String getRawRemainingQuota() {
		return rawRemainingQuota;
	}
	public void setRawRemainingQuota(String rawRemainingQuota) {
		this.rawRemainingQuota = rawRemainingQuota;
	}
	public String getInitialQuota() {
		return initialQuota;
	}
	public void setInitialQuota(String initialQuota) {
		this.initialQuota = initialQuota;
	}
	public String getAdditionalQuota() {
		return additionalQuota;
	}
	public void setAdditionalQuota(String additionalQuota) {
		this.additionalQuota = additionalQuota;
	}
	public String getUsedQuota() {
		return usedQuota;
	}
	public void setUsedQuota(String usedQuota) {
		this.usedQuota = usedQuota;
	}
	public String getRemainingQuota() {
		return remainingQuota;
	}
	public void setRemainingQuota(String remainingQuota) {
		this.remainingQuota = remainingQuota;
	}
	public String getQuotaUnit() {
		return quotaUnit;
	}
	public void setQuotaUnit(String quotaUnit) {
		this.quotaUnit = quotaUnit;
	}
	public String getBenefitType() {
		return benefitType;
	}
	public void setBenefitType(String benefitType) {
		this.benefitType = benefitType;
	}
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	public String getQuotaSource() {
		return quotaSource;
	}
	public void setQuotaSource(String quotaSource) {
		this.quotaSource = quotaSource;
	}
	public String getShow() {
		return show;
	}
	public void setShow(String show) {
		this.show = show;
	}
	public String getUnlimitedFlag() {
		return unlimitedFlag;
	}
	public void setUnlimitedFlag(String unlimitedFlag) {
		this.unlimitedFlag = unlimitedFlag;
	}
	@Override
	public String toString() {
		return "QuotaType [name=" + name + ", description=" + description + ", rawInitialQuota=" + rawInitialQuota
				+ ", rawAditionalQuota=" + rawAditionalQuota + ", rawUsedQuota=" + rawUsedQuota + ", rawRemainingQuota="
				+ rawRemainingQuota + ", initialQuota=" + initialQuota + ", additionalQuota=" + additionalQuota
				+ ", usedQuota=" + usedQuota + ", remainingQuota=" + remainingQuota + ", quotaUnit=" + quotaUnit
				+ ", benefitType=" + benefitType + ", expiryDate=" + expiryDate + ", quotaSource=" + quotaSource
				+ ", show=" + show + ", unlimitedFlag=" + unlimitedFlag + ", getName()=" + getName()
				+ ", getDescription()=" + getDescription() + ", getRawInitialQuota()=" + getRawInitialQuota()
				+ ", getRawAditionalQuota()=" + getRawAditionalQuota() + ", getRawUsedQuota()=" + getRawUsedQuota()
				+ ", getRawRemainingQuota()=" + getRawRemainingQuota() + ", getInitialQuota()=" + getInitialQuota()
				+ ", getAdditionalQuota()=" + getAdditionalQuota() + ", getUsedQuota()=" + getUsedQuota()
				+ ", getRemainingQuota()=" + getRemainingQuota() + ", getQuotaUnit()=" + getQuotaUnit()
				+ ", getBenefitType()=" + getBenefitType() + ", getExpiryDate()=" + getExpiryDate()
				+ ", getQuotaSource()=" + getQuotaSource() + ", getShow()=" + getShow() + ", getUnlimitedFlag()="
				+ getUnlimitedFlag() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
}