/**
 * 
 */
package com.ibm.services.vo;

/**
 * @author IBM_ADMIN
 *
 */
public class LoginVO {
	
	private String msisdn;
	private String password;
	private String channelType;
	private String errorCode;
	private String errorDescription;
	
	private String status;
	private String lastLoginDate;
	private String authenticationFlag;
	private String unsuccessfulAttemps;
	private String changePasswordFlag;
	private String userid;
	private String firstName;
	private String middleName;
	private String lastName;
	/**
	 * @return the msisdn
	 */
	public String getMsisdn() {
		return msisdn;
	}
	/**
	 * @param msisdn the msisdn to set
	 */
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
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
	/**
	 * @return the lastLoginDate
	 */
	public String getLastLoginDate() {
		return lastLoginDate;
	}
	/**
	 * @param lastLoginDate the lastLoginDate to set
	 */
	public void setLastLoginDate(String lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
	/**
	 * @return the authenticationFlag
	 */
	public String getAuthenticationFlag() {
		return authenticationFlag;
	}
	/**
	 * @param authenticationFlag the authenticationFlag to set
	 */
	public void setAuthenticationFlag(String authenticationFlag) {
		this.authenticationFlag = authenticationFlag;
	}
	/**
	 * @return the unsuccessfulAttemps
	 */
	public String getUnsuccessfulAttemps() {
		return unsuccessfulAttemps;
	}
	/**
	 * @param unsuccessfulAttemps the unsuccessfulAttemps to set
	 */
	public void setUnsuccessfulAttemps(String unsuccessfulAttemps) {
		this.unsuccessfulAttemps = unsuccessfulAttemps;
	}
	/**
	 * @return the changePasswordFlag
	 */
	public String getChangePasswordFlag() {
		return changePasswordFlag;
	}
	/**
	 * @param changePasswordFlag the changePasswordFlag to set
	 */
	public void setChangePasswordFlag(String changePasswordFlag) {
		this.changePasswordFlag = changePasswordFlag;
	}
	/**
	 * @return the userid
	 */
	public String getUserid() {
		return userid;
	}
	/**
	 * @param userid the userid to set
	 */
	public void setUserid(String userid) {
		this.userid = userid;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return the middleName
	 */
	public String getMiddleName() {
		return middleName;
	}
	/**
	 * @param middleName the middleName to set
	 */
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	@Override
	public String toString() {
		return "LoginVO [msisdn=" + msisdn + " channelType=" + channelType + ", errorCode="
				+ errorCode + ", errorDescription=" + errorDescription + ", status=" + status + ", lastLoginDate="
				+ lastLoginDate + ", authenticationFlag=" + authenticationFlag + ", unsuccessfulAttemps="
				+ unsuccessfulAttemps + ", changePasswordFlag=" + changePasswordFlag + ", userid=" + userid
				+ ", firstName=" + firstName + ", middleName=" + middleName + ", lastName=" + lastName + "]";
	}
	
}
