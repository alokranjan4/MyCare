package com.ibm.indo.service;

import java.util.Map;

/**
 * @author Aadam
 *
 */
public interface LDAPService {
	Map<String,Object> fetchUserDetails(String key, String value);
	Map<String,Object> getUser(String msisdn);
	String getUserTypeFromLDAP(String msisdn);
}