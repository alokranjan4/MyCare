/**
 * 
 */
package com.ibm.services.vo;

import java.util.Arrays;

import org.example.www.GetSubsInfo.QuotaType;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Aadam
 *
 */
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ServiceType {
	
	@JsonProperty("ServiceType")  private java.lang.String serviceType;
	@JsonProperty("ServiceName") private java.lang.String serviceName;
	@JsonProperty("ServiceDescription")  private java.lang.String serviceDescription;
	@JsonProperty("PackageCode")  private java.lang.String packageCode;
	@JsonProperty("PackageName")  private java.lang.String packageName;
	@JsonProperty("StartDate")  private java.lang.String startDate;
	@JsonProperty("EndDate")  private java.lang.String endDate;
	@JsonProperty("PackagePeriod")  private java.lang.String packagePeriod;
	@JsonProperty("PeriodUnit")  private java.lang.String periodUnit;
	@JsonProperty("BuyExtra")  private java.lang.String buyExtra;
	@JsonProperty("Quotas")  private QuotaType[] quotas;
	public QuotaType[] getQuotas() {
		return quotas;
	}
	public void setQuotas(QuotaType[] quotas) {
		this.quotas = quotas;
	}
	public java.lang.String getServiceType() {
		return serviceType;
	}
	public void setServiceType(java.lang.String serviceType) {
		this.serviceType = serviceType;
	}
	public java.lang.String getServiceName() {
		return serviceName;
	}
	public void setServiceName(java.lang.String serviceName) {
		this.serviceName = serviceName;
	}
	public java.lang.String getServiceDescription() {
		return serviceDescription;
	}
	public void setServiceDescription(java.lang.String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}
	public java.lang.String getPackageCode() {
		return packageCode;
	}
	public void setPackageCode(java.lang.String packageCode) {
		this.packageCode = packageCode;
	}
	public java.lang.String getPackageName() {
		return packageName;
	}
	public void setPackageName(java.lang.String packageName) {
		this.packageName = packageName;
	}
	public java.lang.String getStartDate() {
		return startDate;
	}
	public void setStartDate(java.lang.String startDate) {
		this.startDate = startDate;
	}
	public java.lang.String getEndDate() {
		return endDate;
	}
	public void setEndDate(java.lang.String endDate) {
		this.endDate = endDate;
	}
	public java.lang.String getPackagePeriod() {
		return packagePeriod;
	}
	public void setPackagePeriod(java.lang.String packagePeriod) {
		this.packagePeriod = packagePeriod;
	}
	public java.lang.String getPeriodUnit() {
		return periodUnit;
	}
	public void setPeriodUnit(java.lang.String periodUnit) {
		this.periodUnit = periodUnit;
	}
	public java.lang.String getBuyExtra() {
		return buyExtra;
	}
	public void setBuyExtra(java.lang.String buyExtra) {
		this.buyExtra = buyExtra;
	}
	@Override
	public String toString() {
		return "ServiceType [serviceType=" + serviceType + ", serviceName=" + serviceName + ", serviceDescription="
				+ serviceDescription + ", packageCode=" + packageCode + ", packageName=" + packageName + ", startDate="
				+ startDate + ", endDate=" + endDate + ", packagePeriod=" + packagePeriod + ", periodUnit=" + periodUnit
				+ ", buyExtra=" + buyExtra + ", quotas=" + Arrays.toString(quotas) + "]";
	}
}
