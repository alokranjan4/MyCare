package com.ibm.ijoin.service;

import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public interface GenericService {
	Map<String, Object> retrievePacks(String points);
	Map<String, Object> retrieveApplication(String serviceType);
	Map<String, Object> uploadImage(String login_id, String msisdn, String cust_id, String id_img);
	Map<String, Object> retrievedetails(String login_id, String msisdn);
	Map<String, Object> regUser(String login_id, String msisdn, String name, String email, String password,
			String cust_img, String id_img, String gender, String id_number, String dob, String place_of_birth,
			String alt_number, String maiden_name, String address, String act_status, String act_date, String icc_id);
	Map<String, Object> orderdetails(String rownum1,String rownum2);
	Map<String, Object> getOrderDetails(String asString);
	Map<String, Object> updateOrder(String id, String msisdn, String act_status,String icc_id);
	Map<String, Object> getImage(String id);
}
