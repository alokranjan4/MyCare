package com.ibm.ijoin.service;

import java.util.List;
import java.util.Map;

public interface GenericService {
	Map<String, Object> retrievePacks(String points);
	Map<String, Object> retrieveApplication(String serviceType);
	Map<String, Object> uploadImage(String login_id, String msisdn, String cust_id, String id_img);
	Map<String, Object> retrievedetails(String login_id, String msisdn);
	Map<String, Object> orderdetails(String rownum1,String rownum2, String searchKey);
	Map<String, Object> getOrderDetails(String asString);
	Map<String, Object> updateOrder(String id, String msisdn, String act_status,String icc_id, String order_status, String loginID);
	Map<String, Object> etobeeServiceCreate(String msisdn, String email_id, String ship_addr, String city, String state,String country, String postcode, String order_id);
	Map<String, Object> trackDeliveryStatus(String orderId);
	Map<String, Object> cancelOrder(String orderId);
	Map<String, Object> updateDeliveryStatus(String orderId);
	Map<String, Object> getScore(String asString, String asString2, String asString3);
	Map<String, Object> updateProfile(String email,String name, String cust_img, String id_img, String gender,String id_number, String dob, String address, String religion, String marital_status);
	Map<String, Object> loginUser(String userid, String password, String social_id);
	Map<String, Object> registerUser(String email, String password, String social_id, String source);
	Map<String, Object> retrieveOrder(String emailId);
	Map<String, Object> userProfile(String asString);
	Map<String, Object> userImage(String id);
	Map<String, Object> fetchPack(String asString);
	Map<String, Object> forgotPwd(String email);
	Map<String, Object> autoLogin(String msisdn);
	Map<String, Object> getLocation(String col, String val, String where);
	Map<String, Object> createOrder(String login_id, String ship_addr, String city, String district, String state,
			String country, String postcode, String pkg_name, String amount, String msisdn, String name);
	Map<String, Object> orderStatus(String oid);
	Map<String, Object> validateUser(String uid, String pwd);
	Map<String, Object> sendOTP(String msisdn, String msg);
	Map<String, Object> orderSearchDetails(String string, String string2, String searchKey);
	Map<String, Object> getGallery(String col, String val, String where);
	void updloadPDF(String prefix62, String fname);
	Map<String, Object> getImage(String image);
}
