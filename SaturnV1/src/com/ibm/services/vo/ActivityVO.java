/**
 * 
 */
package com.ibm.services.vo;

/**
 * @author ADMINIBM
 *
 */
public class ActivityVO {
	
	private String Msisdn;
	private String Commid;
	private String ActivityType;
	private String UserId;
	private String Created;
	private String imageId;
	private String userType;
	private String iccId;
	private String orderStatus;
	private String serviceName;
	private String description;
	private String smsText;
	private String text1;
	private String text2;
	private String text3;
	private String text4;
	private String text5;
	private String descEn;
	private String descId;
	
	
	
	public ActivityVO(){}
	
	public ActivityVO(String msisdn, String commid, String activityType, String userId, String created,String imageID) {
		super();
		Msisdn = msisdn;
		Commid = commid;
		ActivityType = activityType;
		UserId = userId;
		Created = created;
		imageId = imageID;
	}
	public String getMsisdn() {
		return Msisdn;
	}
	public void setMsisdn(String msisdn) {
		Msisdn = msisdn;
	}
	public String getCommid() {
		return Commid;
	}
	public void setCommid(String commid) {
		Commid = commid;
	}
	public String getActivityType() {
		return ActivityType;
	}
	public void setActivityType(String activityType) {
		ActivityType = activityType;
	}
	public String getUserId() {
		return UserId;
	}
	public void setUserId(String userId) {
		UserId = userId;
	}
	public String getCreated() {
		return Created;
	}
	public void setCreated(String created) {
		Created = created;
	}
	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	/**
	 * @return the iccId
	 */
	public String getIccId() {
		return iccId;
	}

	/**
	 * @param iccId the iccId to set
	 */
	public void setIccId(String iccId) {
		this.iccId = iccId;
	}

	/**
	 * @return the orderStatus
	 */
	public String getOrderStatus() {
		return orderStatus;
	}

	/**
	 * @param orderStatus the orderStatus to set
	 */
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

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
	 * @return the smsText
	 */
	public String getSmsText() {
		return smsText;
	}

	/**
	 * @param smsText the smsText to set
	 */
	public void setSmsText(String smsText) {
		this.smsText = smsText;
	}

	/**
	 * @return the text1
	 */
	public String getText1() {
		return text1;
	}

	/**
	 * @param text1 the text1 to set
	 */
	public void setText1(String text1) {
		this.text1 = text1;
	}

	/**
	 * @return the text2
	 */
	public String getText2() {
		return text2;
	}

	/**
	 * @param text2 the text2 to set
	 */
	public void setText2(String text2) {
		this.text2 = text2;
	}

	/**
	 * @return the text3
	 */
	public String getText3() {
		return text3;
	}

	/**
	 * @param text3 the text3 to set
	 */
	public void setText3(String text3) {
		this.text3 = text3;
	}

	/**
	 * @return the text4
	 */
	public String getText4() {
		return text4;
	}

	/**
	 * @param text4 the text4 to set
	 */
	public void setText4(String text4) {
		this.text4 = text4;
	}

	/**
	 * @return the text5
	 */
	public String getText5() {
		return text5;
	}

	/**
	 * @param text5 the text5 to set
	 */
	public void setText5(String text5) {
		this.text5 = text5;
	}

	public String getDescEn() {
		return descEn;
	}

	public void setDescEn(String descEn) {
		this.descEn = descEn;
	}

	public String getDescId() {
		return descId;
	}

	public void setDescId(String descId) {
		this.descId = descId;
	}

	@Override
	public String toString() {
		return "ActivityVO [Msisdn=" + Msisdn + ", Commid=" + Commid + ", ActivityType=" + ActivityType + ", UserId="
				+ UserId + ", Created=" + Created + ", imageId=" + imageId + ", userType=" + userType + ", iccId="
				+ iccId + ", orderStatus=" + orderStatus + ", serviceName=" + serviceName + ", description="
				+ description + ", smsText=" + smsText + ", text1=" + text1 + ", text2=" + text2 + ", text3=" + text3
				+ ", text4=" + text4 + ", text5=" + text5 + ", descEn=" + descEn + ", descId=" + descId + "]";
	}
}