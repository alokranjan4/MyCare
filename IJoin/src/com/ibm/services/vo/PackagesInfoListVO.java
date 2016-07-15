package com.ibm.services.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PackagesInfoListVO {
	
	@JsonProperty("Msisdn")private String msisdn;
	@JsonProperty("ChannelType")private String channelType;
	@JsonProperty("ErrorCode")private String errorCode;
	@JsonProperty("ErrorDescription")private String errorDescription;
	@JsonProperty("Status")private String status;
	
	@JsonProperty("PackagesList")private List<PackagesInfoVO> packagesList;

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getChannelType() {
		return channelType;
	}

	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<PackagesInfoVO> getPackagesList() {
		return packagesList;
	}

	public void setPackagesList(List<PackagesInfoVO> packagesList) {
		this.packagesList = packagesList;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PackagesInfoListVO [msisdn=" + msisdn + ", channelType=" + channelType + ", errorCode=" + errorCode
				+ ", errorDescription=" + errorDescription + ", status=" + status + ", packagesList=" + packagesList
				+ "]";
	}

	

}
