/**
 * 
 */
package com.ibm.indo.service;

import java.util.Map;

/**
 * @author Adeeb
 *
 */
public interface ICareService {
	Map<String,Object> customerProfile(String msisdn);
	Map<String,Object> getPUK(String msisdn);
	Map<String,Object> topUpHistory(String msisdn, String startDate, String endDate, String type);
	Map<String,Object> usageHistory(String msisdn, String startDate, String endDate, String type);
	Map<String,Object> getPromo(String msisdn);
	Map<String,Object> getActivePackage(String msisdn);
	Map<String,Object> getActiveProducts(String msisdn);
	Map<String,Object> getPackageQuota(String msisdn);
	Map<String,Object> inquiryPrepaid(String msisdn);
	Map<String,Object> billingInfo(String accountNo);
	Map<String,Object> getSuplementaryPackage(String msisdn);
	Map<String,Object> getSSPDetails(String msisdn);
	Map<String, Object> dedicatedAccountCs3(String msisdn);
	Map<String, Object> dedicatedAccountCs5(String msisdn);
	Map<String, Object> topUp(String msisdn, String code);
	Map<String, Object> customerProfileCorporate(String Msisdn);
	Map<String, Object> corporateMyCareProfile(String Msisdn);
	Map<String, Object> serviceRequest(String xml, String type);
}
