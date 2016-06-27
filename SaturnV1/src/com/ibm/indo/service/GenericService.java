/**
 * 
 */
package com.ibm.indo.service;

import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.ibm.services.vo.ActivityVO;
import com.ibm.services.vo.DompetkuVO;

/**
 * @author ADMINIBM
 *
 */
public interface GenericService {
	Map<String, Object> sendOTP(String msisdn, String msg);
	Map<String, Object> getStaticContent(String asString);
	Map<String, Object> getPackageSSP(String msisdn, String lang);
	Map<String, Object> updateUserType(String user_id, String type);
	Map<String, Object> versionCheck(String currentVersion);
	Map<String, Object> saveImage(String msisdn, String profilepic);
	Map<String, Object> getProfImage(String msisdn);
	Map<String, Object> fetchUserType(String msisdn, String id);
	Map<String, Object> sendEmail(String id, String msg);
	Map<String, String> logActivity(ActivityVO activity);
	Map<String, String> activatePackage(String msisdn, String custType, String serviceName, String param,
			String chargingId, String userid,String transactionType, JsonObject jObj);
	Map<String, String> updatePackageTid(String tid, String status, String desc, String smsText);
	Map<String, String> checkDompetkuReg(String msisdn, String agentId);
	Map<String, Object> retrieveUpgradablePackages(String custType, String package_type, String serviceClass);
	Map<String, Object> getPackage(String msisdn);
	Map<String, Object> removeChild(String committer, String child);
	Map<String, Object> isRelated(String firstNum, String secNum);
	Map<String, Object> getChilds(String msisdn, String userid);
	Map<String, Object> vasActivate(String msisdn, String id, String userid);
	Map<String, Object> vasDeactivate(String msisdn, String id, String userid);
	List<Map<String, Object>> getBuyExtra(String packCode, String packcat);
	String getServiceCodeCat(String serviceClass);
	Map<String, Object> getBannerImages(String serviceClass, String cust_type, String catType);
	Map<String, Object> contactUs(JsonObject jObj);
	Map<String, Object> getOffers(String offerType,String lang);
	void serviceRequest(String xml);
	Map<String, Object> authenticateUserNew(String id, String msisdn, String pwd);
	Map<String, Object> changePasswordNew(String id, String oldPwd, String newPwd);
	Map<String, Object> forgotPasswordNew(String id, String msisdn);
	Map<String, Object> regUserNew(JsonObject jObj, String sOtp,String pwd);
	Map<String, Object> addChildNew(String parent, String child, String otp, String sOtp);
	Map<String, Object> getMsisdn(String id);
	Map<String, Object> addMsisdn(String msisdn, String id);
	Map<String, Object> getUserProfile(String userName);
	Map<String, Object> getMsisdnDetails(String msisdn);
	Map<String, Object> getUserProfileByMsisdn(String msisdn);
	Map<String, Object> removeChildNew(String committer, String child);
	Map<String, Object> removeMsisdn(String msisdn);
	Map<String, Object> authenticateIndoUserNew(String msisdn);
	Map<String, Object> updateProfileNew(String id, String msisdn, String name);
	Map<String, Object> getUserIDByMsisdn(String msisdn);
	Map<String, Object> getLov(String type, String name);
	Map<String, Object> appLauncher(String type);
	Map<String, Object> sendMessage(String from_msisdn, String title, String msg, String type,String to_msisdn,String from_date);
	Map<String, Object> getMessages(String msisdn, String type);
	Map<String, Object> deleteMessage(String id);
	Map<String, Object> changeReadStatus(String id, String status);
	Map<String, Object> unlockAccount(String id);
	Map<String,Object> countClick(String msisdn,int countClick);
	public Map<String, Object> deletemultipleMessage(List<Object[]> listObj);
	public Map<String, String> regDompetku(DompetkuVO dompetkuVO);
	Map<String, Object> changeAllReadStatus(List<Object[]> listObj);
	Map<String, Object> getCMSOffers(String msisdn);
	Map<String, Object> enrollOffers(String msisdn,String offerId);
	Map<String, String> dompetkuPay(String msisdn, String amount, String paymentId);
	Map<String, Object> getDenoms(String msisdn, String type);
	Map<String, Object> retrieveActivity(String msisdn);
	Map<String, Object> regDevice(String msisdn, String deviceId, String osType, String model, String make);
	Map<String, Object> sendNotification(String msisdn, String msg);
	Map<String, Object> quickSurvey();
	Map<String, String> dompetkuRecharge(String msisdn, String amount, String paymentId, String operator);
	Map<String, String> dompetkuPostpay(String msisdn, String amount, String paymentId, String operator);
	Map<String, String> billPayConfirm(String msisdn, String transid, String amount, String operator);
	Map<String, String> airTimeCommit(String msisdn, String transid, String paymentId, String operator);
	Map<String, Object> retrievePackInfo(String keyword);
	
}
