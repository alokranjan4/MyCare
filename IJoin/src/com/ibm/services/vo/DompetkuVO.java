package com.ibm.services.vo;

public class DompetkuVO {
	
	private String userid;
	private String signature;
	private String firstname;
	private String lastname;
	private String idtype;
	private String idnumber;
	private String address;
	private String idphoto;
	private String dob;
	private String mothername;
	private String gender;
	private String extRef;
	private String agentid;
	private String locationid;
	
	private String msisdn;
	private String profilepic;
	private String to;
	
	
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getIdtype() {
		return idtype;
	}
	public void setIdtype(String idtype) {
		this.idtype = idtype;
	}
	public String getIdnumber() {
		return idnumber;
	}
	public void setIdnumber(String idnumber) {
		this.idnumber = idnumber;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getIdphoto() {
		return idphoto;
	}
	public void setIdphoto(String idphoto) {
		this.idphoto = idphoto;
	}
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	public String getMothername() {
		return mothername;
	}
	public void setMothername(String mothername) {
		this.mothername = mothername;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getExtRef() {
		return extRef;
	}
	public void setExtRef(String extRef) {
		this.extRef = extRef;
	}
	public String getAgentid() {
		return agentid;
	}
	public void setAgentid(String agentid) {
		this.agentid = agentid;
	}
	public String getLocationid() {
		return locationid;
	}
	public void setLocationid(String locationid) {
		this.locationid = locationid;
	}
	@Override
	public String toString() {
		return "DompetkuVO [userid=" + userid + ", signature=" + signature + ", firstname=" + firstname + ", lastname="
				+ lastname + ", idtype=" + idtype + ", idnumber=" + idnumber + ", address=" + address + ", idphoto="
				+ idphoto + ", dob=" + dob + ", mothername=" + mothername + ", gender=" + gender + ", extRef=" + extRef
				+ ", agentid=" + agentid + ", locationid=" + locationid + "]";
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getProfilepic() {
		return profilepic;
	}
	public void setProfilepic(String profilepic) {
		this.profilepic = profilepic;
	}
	/**
	 * @return the to
	 */
	public String getTo() {
		return to;
	}
	/**
	 * @param to the to to set
	 */
	public void setTo(String to) {
		this.to = to;
	}
	
	
	

}
