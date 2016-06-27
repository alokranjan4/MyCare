/**
 * 
 */
package com.ibm.indo.service;

import java.util.Map;

/**
 * @author Adeeb
 *
 */
public interface WSSDBService {
	Map<String,Object> getUserProfile(String userName);

	Map<String, Object> getData(String table);

	Map<String, Object> getMsisdn(String id);

	Map<String, Object> getUserProfileByMsisdn(String msisdn);

	Map<String, Object> getMsisdnDetails(String msisdn);

	Map<String, Object> regUser(Map<String, String> user);

	Map<String, Object> changepassword(String uid, String newPassword);
	Map<String, Object> removeMsisdn(String msisdn);

	Map<String, Object> addMsisdn(String msisdn,String id);
}
