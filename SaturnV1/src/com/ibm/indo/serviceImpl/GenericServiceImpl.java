
/**
 * 
 */
package com.ibm.indo.serviceImpl;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.crypto.NoSuchPaddingException;

import org.apache.axis.encoding.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.example.www.GetSubsInfo.CustBillInfo;
import org.example.www.GetSubsInfo.GetSubsInfo;
import org.example.www.GetSubsInfo.GetSubsInfoRequest;
import org.example.www.GetSubsInfo.GetSubsInfoResponse;
import org.example.www.GetSubsInfo.GetSubsInfoSOAPQSServiceLocator;
import org.example.www.GetSubsInfo.GetSubsInfoSOAPStub;
import org.infinispan.Cache;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.indo.service.GenericService;
import com.ibm.indo.service.HttpConnService;
import com.ibm.indo.service.LDAPService;
import com.ibm.indo.service.WSSDBService;
import com.ibm.indo.service.XMLService;
import com.ibm.indo.util.BlowFish;
import com.ibm.indo.util.CacheUtil;
import com.ibm.indo.util.DBUtil;
import com.ibm.indo.util.EmailService;
import com.ibm.indo.util.GenericCache;
import com.ibm.indo.util.IndoServiceProperties;
import com.ibm.indo.util.IndoUtil;
import com.ibm.indo.util.IndoXMLParseUtil;
import com.ibm.indo.util.ServiceTypeSorter;
import com.ibm.indo.util.TripleDES;
import com.ibm.services.vo.ActivityVO;
import com.ibm.services.vo.DompetkuVO;
import com.ibm.services.vo.QuotaType;
import com.ibm.services.vo.ServiceType;

/**
 * @author Aadam
 *
 */
@Service
public class GenericServiceImpl implements GenericService {
	@Autowired
	private DBUtil dbUtil;
	@Autowired
	WSSDBService wssDBService;
	@Autowired
	LDAPService ldapService;
	@Autowired
	HttpConnService httpConn;
	@Autowired
	private XMLService xmlService;

	private static Logger log = Logger.getLogger("saturnLoggerV1");
	IndoServiceProperties confProp = IndoServiceProperties.getInstance();
	Properties prop = confProp.getConfigSingletonObject();

	@Override
	public Map<String, Object> authenticateUserNew(String id, String msisdn, String pwd) {
		log.info("GenericServiceImpl.authenticateUserNew() - START id " + id + "msisdn " + msisdn);
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String qry = "";
			List<Map<String, Object>> list = null;
			Object[] obj = new Object[1];
			if (StringUtils.isEmpty(id) && !StringUtils.isEmpty(msisdn)) {
				String msisdn62 = IndoUtil.prefix62(msisdn);
				String msisdn08 = "";
				if (msisdn.startsWith("62")) {
					msisdn08 = StringUtils.removeStart(msisdn, "62");
				}
				String msisdn0 = "";
				if (msisdn.startsWith("62")) {
					msisdn0 = StringUtils.removeStart(msisdn, "62");
					msisdn0 = "0" + msisdn0;
				}
				if (msisdn.startsWith("8")) {
					msisdn0 = "0" + msisdn0;
				}
				String msisdnN = "";
				if (msisdn.startsWith("62")) {
					msisdnN = StringUtils.removeStart(msisdn, "62");
				}
				if (msisdn.startsWith("08")) {
					msisdnN = StringUtils.removeStart(msisdn, "0");
				}
				log.info("Executning via msisdn");
				qry = "select a.ID as \"id\", b.msisdn as \"msisdn\",a.USERNAME as \"user_id\", a.NAME as \"user_name\", a.NAME as \"full_name\", a.birth_place as \"birth_place\", "
						+ "a.id_number as \"id_number\", a.address  as \"address\", a.email as \"email\", a.birth_date as \"birth_date\", a.ENABLED as \"status\", "
						+ "a.PASSWORDKU, a.ACCOUNT_LOCKED as \"locked\" FROM wss_user a ,WSS_MSISDN b where "
						+ "a.id=b.user_id  and b.user_id= (select user_id from WSS_MSISDN where  msisdn=? or msisdn=? or msisdn=? or msisdn=? or msisdn=?) order by b.id ASC";
				list = dbUtil.getData(qry, new Object[] { msisdn, msisdn0, msisdn62, msisdn08, msisdnN });
				log.info("MSISDN in request query object " + msisdn);
			} else {
				log.info("Executning via id");
				qry = "select a.ID as \"id\", b.msisdn as \"msisdn\",a.USERNAME as \"user_id\", a.NAME as \"user_name\", a.NAME as \"full_name\", a.birth_place as \"birth_place\", "
						+ "a.id_number as \"id_number\", a.address  as \"address\", a.email as \"email\", a.birth_date as \"birth_date\", a.ENABLED as \"status\", "
						+ "a.PASSWORDKU, a.ACCOUNT_LOCKED as \"locked\" FROM wss_user a ,WSS_MSISDN b where "
						+ "a.id=b.user_id  and b.user_id= (select id from wss_user where USERNAME=?) order by b.id ASC";
				obj[0] = id;
				list = dbUtil.getData(qry, obj);
			}
			log.info("MSISDN/id in request query object is " + obj[0]);
			if (null != list && list.size() > 0) {
				int lock = Integer.parseInt(list.get(0).get("locked").toString());
				if (lock > 4) {
					map.clear();
					IndoUtil.populateErrorMap(map, "Saturn-001", "Your account is locked.", 0);
					return map;
				}
				String uid = list.get(0).get("id").toString();
				List<Map<String, Object>> listMsisdns = new ArrayList<Map<String, Object>>();
				boolean vFlag = false;
				String pMsisdn = "";
				String pUserType = "";
				if (null != list.get(0).get("msisdn")) {
					pMsisdn = IndoUtil.prefix62(list.get(0).get("msisdn").toString());
					// to add by prakash user type of parent
				}
				for (Map<String, Object> m : list) {
					Map<String, Object> msisdns = new HashMap<String, Object>();
					String mob = IndoUtil.prefix62(m.get("msisdn").toString());
					String user_type = "";
					if (null != m.get("user_type")) {
						user_type = m.get("user_type").toString();
					}
					msisdns.put("msisdn", mob);
					if (null == user_type || user_type.toString().equals("")) {
						Map<String, Object> ldap = ldapService.getUser(mob);
						if (IndoUtil.isSuccess(ldap) && null != ldap.get("user_type")) {
							user_type = ldap.get("user_type").toString();
							msisdns.put("user_type", ldap.get("user_type").toString());
							if (mob.equals(pMsisdn)) {
								pUserType = ldap.get("user_type").toString();
							}
						}
					} else {
						msisdns.put("user_type", user_type);
					}
					if (!StringUtils.isEmpty(id) && !StringUtils.isEmpty(msisdn)
							&& mob.equals(IndoUtil.prefix62(msisdn))) {
						vFlag = true;
						list.get(0).putAll(msisdns);
						msisdns.put("msisdn", mob);
						msisdns.put("user_type", user_type);
						listMsisdns.add(msisdns);
					} else if (!StringUtils.isEmpty(msisdn) && mob.equals(IndoUtil.prefix62(msisdn))) {
						list.get(0).putAll(msisdns);
						msisdns.put("msisdn", mob);
						msisdns.put("user_type", user_type);
						listMsisdns.add(msisdns);
					} else {
						listMsisdns.add(msisdns);
					}
				}
				if (!StringUtils.isEmpty(id) && !StringUtils.isEmpty(msisdn) && !vFlag) {
					IndoUtil.populateErrorMap(map, "Saturn-001", "Saturn-102", 0);
					return map;
				}
				// if(BlowFish.decrypt(list.get(0).get("PASSWORDKU").toString()).equals(pwd)){
				if (BlowFish.hashed(BlowFish.decrypt(list.get(0).get("PASSWORDKU").toString())).equals(pwd)) {
					list.get(0).remove("PASSWORDKU");
					/*
					 * if(null!=list.get(0).get("msisdn")){ Map<String, Object>
					 * image =
					 * getProfImage(list.get(0).get("msisdn").toString());
					 * if(IndoUtil.isSuccess(image)){ map.putAll(image); } }
					 */
					list.get(0).put("pmsisdn", pMsisdn);
					list.get(0).put("user_type", pUserType);
					list.get(0).put("msisdn", IndoUtil.prefix62(list.get(0).get("msisdn").toString()));
					map.put("data", list.get(0));
					map.put("msisdns", listMsisdns);
					map.put("Status", "SUCCESS");
					dbUtil.saveData("Update wss_user set ACCOUNT_LOCKED=? where id=?", new Object[] { 0, uid });
				} else {
					dbUtil.saveData("Update wss_user set ACCOUNT_LOCKED=? where id=?", new Object[] { lock + 1, uid });
					IndoUtil.populateErrorMap(map, "Saturn-001", "Saturn-102", 0);
				}
				return map;
			} else {
				IndoUtil.populateErrorMap(map, "Saturn-001", "Saturn-102", 0);
			}
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(map, "Saturn-001", "Saturn-101", 0);
			log.error("GenericServiceImpl.authenticateUserNew() ce" + IndoUtil.getFullLog(ce));
		} finally {
			log.info("GenericServiceImpl.authenticateUserNew() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> fetchUserType(String msisdn, String id) {
		// this will update user_type from LDAP if not avail in DB
		log.info("GenericServiceImpl.fetchUserType() START");
		Map<String, Object> ldap = ldapService.getUser(msisdn);
		if (IndoUtil.isSuccess(ldap) && null != ldap.get("user_type")) {
			Map<String, Object> map = updateUserType(msisdn, ldap.get("user_type").toString());
			map.put("user_type", ldap.get("user_type"));
			return map;
		}
		log.info("GenericServiceImpl.fetchUserType() END");
		return ldap;
	}

	@Override
	public Map<String, Object> authenticateIndoUserNew(String msisdn) {
		log.info("GenericServiceImpl.authenticateIndoUserNew() - START msisdn " + msisdn);
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String qry = "";
			String msisdn62 = IndoUtil.prefix62(msisdn);
			String msisdn08 = "";
			if (msisdn.startsWith("62")) {
				msisdn08 = StringUtils.removeStart(msisdn, "62");
			}
			String msisdn0 = "";
			if (msisdn.startsWith("62")) {
				msisdn0 = StringUtils.removeStart(msisdn, "62");
				msisdn0 = "0" + msisdn0;
			}
			if (msisdn.startsWith("8")) {
				msisdn0 = "0" + msisdn0;
			}
			String msisdnN = "";
			if (msisdn.startsWith("62")) {
				msisdnN = StringUtils.removeStart(msisdn, "62");
			}
			if (msisdn.startsWith("08")) {
				msisdnN = StringUtils.removeStart(msisdn, "0");
			}
			if (!StringUtils.isEmpty(msisdn)) {
				log.info("Executning via msisdn");
				qry = "select b.msisdn as \"msisdn\",a.USERNAME as \"user_id\", a.NAME as \"user_name\", a.NAME as \"full_name\", a.birth_place as \"birth_place\", "
						+ "a.id_number as \"id_number\", a.address  as \"address\", a.email as \"email\", a.birth_date as \"birth_date\", a.ENABLED as \"status\", "
						+ "a.PASSWORDKU FROM wss_user a ,WSS_MSISDN b where "
						+ "a.id=b.user_id  and b.user_id= (select user_id from WSS_MSISDN where msisdn=? or msisdn=? or msisdn=? or msisdn=? or msisdn=?) order by b.id ASC";
			} else {
				IndoUtil.populateErrorMap(map, "Saturn-001", "Saturn-102", 0);
				return map;
			}
			List<Map<String, Object>> list = dbUtil.getData(qry,
					new Object[] { msisdn, msisdn0, msisdn62, msisdn08, msisdnN });
			if (null != list && list.size() > 0) {
				List<Map<String, Object>> listMsisdns = new ArrayList<Map<String, Object>>();
				boolean vFlag = false;
				String pMsisdn = "";
				if (null != list.get(0).get("msisdn")) {
					pMsisdn = IndoUtil.prefix62(list.get(0).get("msisdn").toString());
				}
				for (Map<String, Object> m : list) {
					Map<String, Object> msisdns = new HashMap<String, Object>();
					String mob = IndoUtil.prefix62(m.get("msisdn").toString());
					String user_type = "";
					if (null != m.get("user_type")) {
						user_type = m.get("user_type").toString();
					}
					msisdns.put("msisdn", mob);
					if (null == user_type || user_type.toString().equals("")) {
						Map<String, Object> ldap = ldapService.getUser(mob);
						if (IndoUtil.isSuccess(ldap) && null != ldap.get("user_type")) {
							user_type = ldap.get("user_type").toString();
							msisdns.put("user_type", ldap.get("user_type").toString());
						}
					} else {
						msisdns.put("user_type", user_type);
					}
					if (!StringUtils.isEmpty(msisdn) && mob.equals(IndoUtil.prefix62(msisdn))) {
						vFlag = true;
						list.get(0).putAll(msisdns);
						msisdns.put("msisdn", mob);
						msisdns.put("user_type", user_type);
						listMsisdns.add(msisdns);
					} else if (!StringUtils.isEmpty(msisdn) && mob.equals(IndoUtil.prefix62(msisdn))) {
						list.get(0).putAll(msisdns);
						msisdns.put("msisdn", mob);
						msisdns.put("user_type", user_type);
						listMsisdns.add(msisdns);
					} else {
						listMsisdns.add(msisdns);
					}
				}
				if (!StringUtils.isEmpty(msisdn) && !vFlag) {
					IndoUtil.populateErrorMap(map, "Saturn-001", "Saturn-102", 0);
					return map;
				}
				list.get(0).remove("PASSWORDKU");
				/*
				 * if(null!=list.get(0).get("msisdn")){ Map<String, Object>
				 * image = getProfImage(list.get(0).get("msisdn").toString());
				 * if(IndoUtil.isSuccess(image)){ map.putAll(image); } }
				 */
				list.get(0).put("pmsisdn", pMsisdn);
				list.get(0).put("msisdn", IndoUtil.prefix62(list.get(0).get("msisdn").toString()));
				map.put("data", list.get(0));
				map.put("msisdns", listMsisdns);
				map.put("Status", "SUCCESS");
				return map;
			} else {
				IndoUtil.populateErrorMap(map, "Saturn-001", "Saturn-102", 0);
			}
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(map, "Saturn-001", "Saturn-101", 0);
			log.error("GenericServiceImpl.authenticateUserNew() ce" + IndoUtil.getFullLog(ce));
		} finally {
			log.info("GenericServiceImpl.authenticateUserNew() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> changePasswordNew(String id, String oldPwd, String newPwd) {
		log.info("GenericServiceImpl.changePasswordNew() - START");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Map<String, Object>> list = dbUtil.getData("SELECT PASSWORDKU FROM wss_user where USERNAME=?",
					new Object[] { id });
			if (null != list && list.size() > 0) {
				String pwd = list.get(0).get("PASSWORDKU").toString();
				if (oldPwd.equals(BlowFish.decrypt(pwd))) {
					int ct = dbUtil.saveData("Update wss_user set PASSWORDKU=? where USERNAME=?",
							new Object[] { BlowFish.encrypt(newPwd), id });
					if (ct > 0) {
						map.put("Status", "SUCCESS");
					} else {
						IndoUtil.populateErrorMap(map, "Saturn-002", "Saturn-104", 0);
					}
				} else {
					IndoUtil.populateErrorMap(map, "Saturn-002", "Saturn-104", 0);
				}
				return map;
			} else {
				IndoUtil.populateErrorMap(map, "Saturn-002", "Saturn-104", 0);
				return map;
			}
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(map, "Saturn-002", "Saturn-104", 0);
			log.error("GenericServiceImpl.changePasswordNew() ce" + IndoUtil.getFullLog(ce));
		} finally {
			log.info("GenericServiceImpl.changePasswordNew() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> forgotPasswordNew(String id, String msisdn) {
		log.info("GenericServiceImpl.forgotPasswordNew() - START");
		msisdn = IndoUtil.prefix62(msisdn);
		Map<String, Object> map = new HashMap<String, Object>();
		String pwd = IndoUtil.getAlphaNumeric(6);
		try {
			List<Map<String, Object>> data = dbUtil.getData(
					"Select b.msisdn as \"msisdn\", a.email as \"email\" from wss_user a, wss_msisdn b where a.id=b.user_id  and a.USERNAME=? order by b.id ASC",
					new Object[] { id });
			String email = "";
			if (null != data && data.size() > 0) {
				boolean vFlag = false;
				for (Map<String, Object> m : data) {
					if (msisdn.equals(IndoUtil.prefix62(m.get("msisdn").toString()))) {
						if (null != m.get("email")) {
							email = m.get("email").toString();
						}
						vFlag = true;
						break;
					}
				}
				if (vFlag) {
					int ct = dbUtil.saveData("update wss_user set PASSWORDKU=? where USERNAME=?",
							new Object[] { BlowFish.encrypt(pwd), id });
					if (ct > 0) {
						if (!id.contains("@")) {
							map.put("email_id", email);
						} else {
							map.put("email_id", id);
						}
						map.put("Status", "SUCCESS");
						map.put("TEMP_PWD", pwd);
						return map;
					} else {
						IndoUtil.populateErrorMap(map, "Saturn-003-1", "Saturn-1029", 0);
					}
				} else {
					IndoUtil.populateErrorMap(map, "Saturn-003", "UnAuthorised Access!", 0);
				}
			} else {
				IndoUtil.populateErrorMap(map, "Saturn-003-1", "Saturn-1029", 0);
				return map;
			}
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(map, "Saturn-003", "System Error.", 0);
			log.error("GenericServiceImpl.forgotPasswordNew() ce" + IndoUtil.getFullLog(ce));
		} finally {
			log.info("GenericServiceImpl.forgotPasswordNew() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> sendOTP(String msisdn, String msg) {
		log.info("GenericServiceImpl.sendOTP() - START");
		if (msisdn.startsWith("08")) {
			msisdn = msisdn.replaceFirst("08", "628");
		} else if (msisdn.startsWith("8")) {
			msisdn = msisdn.replaceFirst("8", "628");
		}
		log.info("Entering SendSMS for " + msisdn);
		Map<String, Object> data = new HashMap<String, Object>();
		String url = prop.getProperty("SMS_GW_URL") + msisdn + "&sms=" + msg + "&smstype=0";
		Document doc;
		try {
			doc = Jsoup.connect(url).timeout(10000).get();
			log.info("GenericServiceImpl.sendOTP() doc-" + doc.html());
			String status = "";
			String id = "";
			String m = "";
			if (null == doc.select("STATUS")) {
				status = doc.select("status").first().ownText().trim();
			} else {
				status = doc.select("STATUS").first().ownText().trim();
			}
			if (null == doc.select("TRANSID")) {
				id = doc.select("transid").first().ownText().trim();
			} else {
				id = doc.select("TRANSID").first().ownText().trim();
			}
			if (null == doc.select("MSG")) {
				m = doc.select("msg").first().ownText().trim();
			} else {
				m = doc.select("MSG").first().ownText().trim();
			}
			log.info("GenericServiceImpl.sendOTP() id - " + id);
			log.info("GenericServiceImpl.sendOTP() m - " + m);
			if (status.equals("0")) {
				data.put("Status", "SUCCESS");
				log.info("SMS Sent to" + msisdn);
			} else {
				IndoUtil.populateErrorMap(data, "Saturn-004", "Saturn-1025", 0);
			}
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(data, "Saturn-004", "Saturn-1025", 0);
			log.error("Saturn-004- GenericServiceImpl.sendOTP() " + IndoUtil.getFullLog(ce));
		} finally {
			log.info("GenericServiceImpl.sendOTP() - END");
		}
		return data;
	}

	@Override
	public Map<String, Object> regUserNew(JsonObject jObj, String sOtp, String passwd) {
		// log.info("GenericServiceImpl.regUser() - START");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (StringUtils.isEmpty(jObj.get("user_id").getAsString())
					|| StringUtils.isEmpty(jObj.get("msisdn").getAsString())) {
				IndoUtil.populateErrorMap(map, "Saturn-005", "Saturn-1026", 0);
				return map;
			}
			String msisdn = IndoUtil.prefix62(jObj.get("msisdn").getAsString());
			Map<String, Object> ldap = ldapService.getUser(msisdn);
			if (IndoUtil.isSuccess(ldap)) {
				if (null == ldap.get("user_type") || ldap.get("user_type").toString().equals("")) {
					IndoUtil.populateErrorMap(map, "Staurn-005", "Saturn-1031", 0);
					return map;
				}
			} else {
				IndoUtil.populateErrorMap(map, "Staurn-005", "Saturn-1031", 0);
				return map;
			}
			if (null == sOtp || sOtp.equals("")) {
				log.info("GenericServiceImpl.regUserNew() --- Validating");

				Map<String, Object> user = dbUtil.getRow("select count(*) count from WSS_USER where USERNAME=?",
						new Object[] { jObj.get("user_id").getAsString() });
				log.info("GenericServiceImpl.regUserNew() user " + user);
				if (Integer.parseInt(user.get("count").toString()) == 0) {
					user = dbUtil.getRow(
							"select count(*) count from WSS_MSISDN where msisdn=? or msisdn=? or msisdn=? or msisdn=? or msisdn=?",
							IndoUtil.msisdnArray(msisdn));
					log.info("GenericServiceImpl.regUserNew() user1 " + user);
					if (Integer.parseInt(user.get("count").toString()) > 0) {
						IndoUtil.populateErrorMap(map, "Staurn-005", "Saturn-1033", 0);
						return map;
					}
				} else {
					IndoUtil.populateErrorMap(map, "Staurn-005", "Saturn-1032", 0);
					return map;
				}
				String otp = "";
				otp = Integer.toString(IndoUtil.randInt(111111, 999999));
				log.info("GenericServiceImpl.regUserNew() " + otp);
				sendOTP(msisdn,
						"PENTING: Mohon tidak untuk diinfokan kepada siapapun! %0a Kata kunci daftar akun myCare:  "
								+ otp + " %0a http://mycare.indosatooredoo.com");
				map.put("Status", "SUCCESS");
				map.put("TEMP_OTP", otp);
				return map;
			} else {
				if (null == jObj.get("OTP") || jObj.get("OTP").getAsString().equals("")
						|| !jObj.get("OTP").getAsString().equals(sOtp)) {
					map.clear();
					IndoUtil.populateErrorMap(map, "Staurn-005", "Invalid OTP.", 0);
					return map;
				}
				log.info("GenericServiceImpl.regUserNew() --- Registering");

				String user_name = "";
				String full_name = "";
				String birth_place = "";
				String id_number = "";
				String address = "";
				String email = "";
				String birth_date = "";
				email = jObj.get("user_id").getAsString();
				if (null == jObj.get("user_name") || StringUtils.isEmpty(jObj.get("user_name").toString())) {
					String temp[] = jObj.get("user_id").getAsString().split("@");
					user_name = temp[0];
				}
				if (null != jObj.get("full_name")) {
					full_name = jObj.get("full_name").getAsString();
				}
				if (null != jObj.get("birth_place")) {
					birth_place = jObj.get("birth_place").getAsString();
				}
				if (null != jObj.get("id_number")) {
					id_number = jObj.get("id_number").getAsString();
				}
				if (null != jObj.get("address")) {
					address = jObj.get("address").getAsString();
				}
				if (null != jObj.get("email")) {

				}
				if (null != jObj.get("birth_date")) {
					birth_date = jObj.get("birth_date").getAsString();
				}
				Map<String, String> qMap = new HashMap<String, String>();
				qMap.put("msisdn", msisdn);
				qMap.put("user_id", jObj.get("user_id").getAsString());
				qMap.put("user_name", user_name);
				qMap.put("full_name", full_name);
				qMap.put("birth_place", birth_place);
				qMap.put("id_number", id_number);
				qMap.put("address", address);
				qMap.put("email", email);
				// prakash modified to enter user entered password
				qMap.put("pwd", BlowFish.encrypt(passwd));
				qMap.put("reg_date", "SYSDATE");
				qMap.put("user_type", ldap.get("user_type").toString());
				qMap.put("where", "user_id='" + jObj.get("user_id").getAsString() + "' or msisdn='" + msisdn + "'");
				// Map<String, Object> regStatus = wssDBService.regUser(qMap);
				Map<String, Object> row = dbUtil.getRow(
						"SELECT * FROM wss_msisdn WHERE id = ( SELECT MAX(id) FROM wss_msisdn)", new Object[] {});
				int mid = Integer.parseInt(row.get("ID").toString());
				row = dbUtil.getRow("SELECT * FROM wss_user WHERE id = ( SELECT MAX(id) FROM wss_user)",
						new Object[] {});
				int uid = Integer.parseInt(row.get("ID").toString());
				int ct = dbUtil.saveData(
						"INSERT into wss_user(id,PASSWORDKU,PASSWORD_EXPIRED,USERNAME,NAME,VERSION,ACCOUNT_EXPIRED,ACCOUNT_LOCKED,ENABLED,CREATED_ON,MODIFIED_ON) values(?,?,?,?,?,?,?,?,?,sysdate,sysdate)",
						new Object[] { uid + 1, qMap.get("pwd"), "0", qMap.get("user_id"), qMap.get("user_name"), "1",
								"0", "0", "1" });
				if (ct > 0) {
					ct = dbUtil.saveData(
							"INSERT into wss_msisdn(id,MSISDN,REDEMPTION_STATUS,USER_ID,CREATED_ON,VERSION,IS_AUTO_LOGIN,OS_ID) values(?,?,?,?,sysdate,?,?,?)",
							new Object[] { mid + 1, qMap.get("msisdn"), "active", uid + 1, "2", "1", "1" });
					if (ct > 0) {
						map.put("Status", "SUCCESS");
						sendOTP(msisdn,
								"Info akun myCare. %0a Username: " + jObj.get("user_id").getAsString()
										+ " %0a Kata sandi: " + passwd
										+ " %0a PENTING: Mohon tidak untuk diinfokan kepada siapapun! ");
						// boolean sent =
						// EmailService.sendEmailWithHtmlAttachment(email,
						// "Mycare Registration", "Info registrasi myCare
						// Username myCare:
						// "+jObj.get("user_id").getAsString()+" Password:
						// "+pwd+" PENTING: Mohon tidak untuk diinfokan kepada
						// siapapun! "+pwd, "noreply@indosatooredoo.com", "",
						// "");
						boolean sent = EmailService.sendEmailWithHtmlAttachment(email, "Mycare Registration",
								"Info akun myCare. \t\n Username: " + jObj.get("user_id").getAsString()
										+ " \t\n Kata sandi: " + passwd
										+ "\t\n  PENTING: Mohon tidak untuk diinfokan kepada siapapun! ",
								"noreply@indosatooredoo.com", "", "");
						map.put("TEMP_PWD", passwd);
						map.put("user_type", ldap.get("user_type"));
						return map;
					} else {
						dbUtil.saveData("delete from wss_user where ID=?", new Object[] { uid + 1 });
						IndoUtil.populateErrorMap(map, "Saturn-005", "Saturn-101", 0);
					}
				}
			}
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(map, "Saturn-005", "Saturn-101", 0);
			log.error("GenericServiceImpl.regUserNew() ce" + IndoUtil.getFullLog(ce));
		} finally {
			log.info("GenericServiceImpl.regUserNew() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> getStaticContent(String lang) {
		log.info("GenericServiceImpl.getStaticContent() - START");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Cache<String, Object> cache = GenericCache.getInstance().getEntityCache();
			Map<String, Object> cacheMap = (Map<String, Object>) cache.get(lang + "_getStaticContent");
			if (null != cacheMap) {
				return cacheMap;
			} else {
				List<Map<String, Object>> list = null;
				if (lang.equalsIgnoreCase("english")) {
					list = dbUtil.getData("SELECT key, en_value from isat_static_content", new Object[] {});
				} else if (lang.equalsIgnoreCase("bahasa")) {
					list = dbUtil.getData("SELECT key, id_value from isat_static_content", new Object[] {});
				} else {
					list = dbUtil.getData("SELECT * from isat_static_content", new Object[] {});
				}
				if (null != list && list.size() > 0) {
					map.put("Status", "SUCCESS");
					map.put("data", list);
					cache.put(lang + "_getStaticContent", map, 12, TimeUnit.HOURS);
					return map;
				} else {
					IndoUtil.populateErrorMap(map, "Saturn-007", "Saturn-109", 0);
				}
			}
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(map, "Saturn-007", "Saturn-101", 0);
			log.error("GenericServiceImpl.getStaticContent() ce" + IndoUtil.getFullLog(ce));
		} finally {
			log.info("GenericServiceImpl.getStaticContent() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> unlockAccount(String id) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			int ct = dbUtil.saveData("Update WSS_USER set ACCOUNT_LOCKED=? where id=?", new Object[] { "0", id });
			if (ct > 0) {
				data.put("Status", "SUCCESS");
			}
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(data, "Saturn-007", "Saturn-101", 0);
			log.error("GenericServiceImpl.unlockAccount() ce" + IndoUtil.getFullLog(ce));
		} finally {
			log.info("GenericServiceImpl.unlockAccount() - END");
		}
		return data;
	}

	@Override
	public Map<String, Object> getPackageSSP(String msisdn, String lang) {
		log.info("GenericServiceImpl.getPackageSSP() - START");
		msisdn = IndoUtil.prefix62(msisdn);
		Map<String, Object> data = new HashMap<String, Object>();
		GetSubsInfoSOAPQSServiceLocator loc = new GetSubsInfoSOAPQSServiceLocator();
		try {
			Cache<String, Object> cache = CacheUtil.getInstance().getEntityCache();
			Map<String, Object> cacheMap = (Map<String, Object>) cache.get(msisdn + "_getPackageSSP_" + lang);
			// log.info("GenericServiceImpl.getPackageSSP() cacheMap
			// "+cacheMap);
			if (null != cacheMap) {
				return cacheMap;
			} else {
				List<Map<String, Object>> buyExtra = getBuyExtra("", "");
				List<ServiceType> list = new ArrayList<ServiceType>();
				List<ServiceType> list1 = new ArrayList<ServiceType>();
				List<ServiceType> list2 = new ArrayList<ServiceType>();
				/*
				 * // "Total Connections" Pool size AxisProperties.setProperty(
				 * DefaultCommonsHTTPClientProperties.
				 * MAXIMUM_TOTAL_CONNECTIONS_PROPERTY_KEY, "5000"); //
				 * "Connections per host" pool size
				 * AxisProperties.setProperty(DefaultCommonsHTTPClientProperties
				 * .MAXIMUM_CONNECTIONS_PER_HOST_PROPERTY_KEY, "5000"); // max
				 * duration to wait for a connection from the pool
				 * AxisProperties.setProperty(DefaultCommonsHTTPClientProperties
				 * .CONNECTION_POOL_TIMEOUT_KEY, "5000");
				 * 
				 * // Timeout to establish connection in millis
				 * AxisProperties.setProperty(
				 * DefaultCommonsHTTPClientProperties.
				 * CONNECTION_DEFAULT_CONNECTION_TIMEOUT_KEY, "5000");
				 * 
				 * // Timeout "waiting for data" (read timeout)
				 * AxisProperties.setProperty(
				 * DefaultCommonsHTTPClientProperties.
				 * CONNECTION_DEFAULT_SO_TIMEOUT_KEY, "5000");
				 * 
				 * // Instantiate the ServiceLocator only ONCE !!!
				 */ GetSubsInfo subInfo = loc
						.getGetSubsInfoSOAPQSPort(new URL("http://10.147.246.140:8002/PULLHandler/GetSubsInfoWS_PS"));
				GetSubsInfoSOAPStub stub = (GetSubsInfoSOAPStub) subInfo;
				log.info("GenericServiceImpl.getPackageSSP() getTimeout " + stub.getTimeout());
				stub.setTimeout(8000);
				// stub._setProperty("axis.connection.timeout",30000);
				/*
				 * stub._setProperty(org.apache.axis.client.Call.
				 * CONNECTION_TIMEOUT_PROPERTY, 10);
				 * stub._setProperty(org.apache.axis.components.net.
				 * DefaultCommonsHTTPClientProperties.
				 * CONNECTION_DEFAULT_CONNECTION_TIMEOUT_KEY, 10);
				 * stub._setProperty(org.apache.axis.components.net.
				 * DefaultCommonsHTTPClientProperties.
				 * CONNECTION_DEFAULT_SO_TIMEOUT_KEY, 10);
				 */
				log.info("GenericServiceImpl.getPackageSSP() -- requesting");
				GetSubsInfoRequest request = new GetSubsInfoRequest();
				request.setMsisdn(msisdn);
				request.setEid(prop.getProperty("SSP_EID"));
				request.setTid(prop.getProperty("SSP_TID"));
				request.setIMSI("");
				request.setLang(lang.toUpperCase());
				GetSubsInfoResponse response = stub.getQuota(request);
				log.info("GenericServiceImpl.getPackageSSP() -- responded");
				org.example.www.GetSubsInfo.ServiceType[] serviceType = response.getServices();
				for (org.example.www.GetSubsInfo.ServiceType ser : serviceType) {
					List<QuotaType> dList = new ArrayList<QuotaType>();
					List<QuotaType> vList = new ArrayList<QuotaType>();
					List<QuotaType> sList = new ArrayList<QuotaType>();
					List<QuotaType> oList = new ArrayList<QuotaType>();
					QuotaType[] quotas = new QuotaType[ser.getQuotas().length];
					if (null == quotas || quotas.length == 0 && !ser.getServiceType().toLowerCase().startsWith("main")) {
						continue;
					}
					QuotaType[] quotasGB = new QuotaType[quotas.length];
					org.example.www.GetSubsInfo.QuotaType[] respQuotasGB = ser.getQuotas();
					for(int i=0;i<respQuotasGB.length;i++){
						QuotaType type = new QuotaType();
						org.apache.commons.beanutils.BeanUtils.copyProperties(type, respQuotasGB[i]);
						log.info("GenericServiceImpl.getPackageSSP() type "+type);
						quotas[i]=type;
					}
					QuotaType[] quotasT = null;
					int ct = 0;
					QuotaType tempType = null;
					boolean cont = false;
					for (QuotaType type : quotas) {
						DecimalFormat df = new DecimalFormat("0.00");
						log.info("GenericServiceImpl.getPackageSSP() type.getBenefitType "+type.getBenefitType());
						double initialQuota = 0;
						if (!StringUtils.isEmpty(type.getInitialQuota())) {
							initialQuota = Double.parseDouble(type.getInitialQuota());
						}
						if (null != type.getBenefitType() && type.getBenefitType().equalsIgnoreCase("DATATEST")
								&& initialQuota > 999.99) {
							QuotaType qType = type;
							initialQuota = initialQuota / 1000;
							qType.setInitialQuota(df.format(initialQuota));
							double additionalQuota = 0;
							double usedQuota = 0;
							if (!StringUtils.isEmpty(qType.getUsedQuota())) {
								Double.parseDouble(df.format(Double.parseDouble(type.getUsedQuota()) / 1000));
							}
							if (!StringUtils.isEmpty(qType.getAdditionalQuota())) {
								additionalQuota = Double
										.parseDouble(df.format(Double.parseDouble(type.getAdditionalQuota()) / 1000));
								qType.setAdditionalQuota(Double.toString(additionalQuota));
							} else {
								qType.setAdditionalQuota("0");
							}
							qType.setUsedQuota(df.format(usedQuota));
							double remainingQuota = (initialQuota + additionalQuota) - usedQuota;
							qType.setRemainingQuota(df.format(remainingQuota));
							qType.setQuotaUnit("GB");
							tempType = qType;
							quotasGB[ct] = qType;
						} else {
							QuotaType qType = type;
							long additionalQuota = 0;
							long usedQuota = 0;
							long remainingQuota = 0;
							long initialQuota1 = 0;
							if (!StringUtils.isEmpty(type.getInitialQuota())) {
								initialQuota1 = Math.round(Double.parseDouble(type.getInitialQuota()));
								qType.setInitialQuota(Long.toString(initialQuota1));
							} else {
								qType.setInitialQuota("0");
							}
							if (!StringUtils.isEmpty(qType.getUsedQuota())) {
								usedQuota = Math.round(Double.parseDouble(type.getUsedQuota()));
								qType.setUsedQuota(Long.toString(Math.round(usedQuota)));
							} else {
								qType.setUsedQuota("0");
							}
							if (!StringUtils.isEmpty(qType.getAdditionalQuota())) {
								additionalQuota = Math.round(Double.parseDouble(type.getAdditionalQuota()));
								qType.setAdditionalQuota(Long.toString(additionalQuota));
							} else {
								qType.setAdditionalQuota("0");
							}
							if (!StringUtils.isEmpty(qType.getRemainingQuota())) {
								remainingQuota = Math.round(Double.parseDouble(type.getRemainingQuota()));
								qType.setRemainingQuota(Long.toString(remainingQuota));
							} else {
								qType.setRemainingQuota("0");
							}
							if (additionalQuota == 0 && usedQuota == 0 && remainingQuota == 0) {
								continue;
							} else {
								tempType = qType;
								quotasGB[ct] = qType;
							}
						}
						int a = 0;
						for (QuotaType q : quotasGB) {
							if (null != q) {
								a++;
							}
						}
						quotasT = new QuotaType[a];
						if (null == tempType || null == tempType.getBenefitType()
								|| StringUtils.isEmpty(tempType.getBenefitType())) {
							continue;
						}
						if(tempType.getBenefitType().toUpperCase().contains("UNLIMITED")){
							tempType.setUnlimitedFlag("Y");
						}else{
							tempType.setUnlimitedFlag("N");
						}
						if (null != tempType && tempType.getBenefitType().toUpperCase().contains("DATA")) {
							dList.add(tempType);
						} else if (null != tempType && tempType.getBenefitType().toUpperCase().contains("VOICE")) {
							vList.add(tempType);
						} else if (null != tempType && tempType.getBenefitType().toUpperCase().contains("SMS")) {
							sList.add(tempType);
						} else if (null != tempType) {
							oList.add(tempType);
						}
						ct++;
					}
					// if(null==tempType){continue;}
					dList.addAll(vList);
					dList.addAll(sList);
					dList.addAll(oList);
					// if(dList.size()==0){continue;}
					ServiceType service = new ServiceType();
					if (ser.getEndDate() != null) {
						try {
							Date dte = IndoUtil.parseDate(ser.getEndDate(), "yyyyMMdd");
							service.setEndDate(IndoUtil.parseDate(dte, "dd.MM.yyyy"));
						} catch (Exception e) {
							log.error("Saturn 008 - GenericServiceImpl.getPackageSSP()" + e);
						}
					}
					if (ser.getStartDate() != null) {
						try {
							Date dte1 = IndoUtil.parseDate(ser.getStartDate(), "yyyyMMdd");
							service.setStartDate(IndoUtil.parseDate(dte1, "dd.MM.yyyy"));
						} catch (Exception e) {
							log.error("Saturn 008 - GenericServiceImpl.getPackageSSP()" + e);
						}
					}
					String buy = "No";
					if (null != ser.getPackageCode()) {
						for (Map<String, Object> map : buyExtra) {
							if (null != map.get("PACK_CODE")
									&& ser.getPackageCode().equalsIgnoreCase(map.get("PACK_CODE").toString())) {
								buy = "Yes";
							}
						}
					}
					if (null != ser.getServiceName() && (ser.getServiceName().toLowerCase().contains("matrix super")
							|| ser.getServiceName().toLowerCase().contains("matrix max"))) {
						buy = "Yes";
					}
					String cat = getServiceCodeCat(response.getServiceClass());
					if (null != cat && cat.equalsIgnoreCase("Matrix Super Plan")
							&& cat.equalsIgnoreCase("Matrix Max 25")) {
						buy = "Yes";
					}
					service.setBuyExtra(buy);
					service.setPackageCode(ser.getPackageCode());
					service.setPackageName(ser.getServiceDescription());
					// service.setPackageName(ser.getPackageName());
					service.setPackagePeriod(ser.getPackagePeriod());
					service.setPeriodUnit(ser.getPeriodUnit());
					service.setServiceDescription(ser.getServiceDescription());
					service.setServiceName(ser.getServiceName());
					service.setServiceType(ser.getServiceType());
					if (dList.size() != 0) {
						service.setQuotas(dList.toArray(quotasT));
					} else {
						service.setQuotas(new QuotaType[] {});
					}
					// service.setQuotas(dList.toArray(quotasGB));
					log.info("GenericServiceImpl.getPackageSSP() " + ser.getPackageCode() + " - " + msisdn + " - "
							+ ser.getServiceType());
					if (null != ser.getServiceType() && ser.getServiceType().toLowerCase().startsWith("main")) {
						list.add(service);
					} else if (null != ser.getServiceType() && ser.getServiceType().toLowerCase().contains("freedom")) {
						list1.add(service);
					} else if (null != ser.getServiceType()) {
						list2.add(service);
					}

				}
				Collections.sort(list2, new ServiceTypeSorter());
				/*
				 * log.info("GenericServiceImpl.getPackageSSP() - list "+list);
				 * log.info("GenericServiceImpl.getPackageSSP() - list1 "+list1)
				 * ;
				 * log.info("GenericServiceImpl.getPackageSSP() - list2 "+list2)
				 * ;
				 */
				list.addAll(list1);
				list.addAll(list2);
				// String qS = IndoUtil.convertToJSON(listQ);
				String simType = "";
				String lastBal = "";
				CustBillInfo bill = response.getCustBillInfo();
				if (null != bill) {
					String dat = IndoUtil.parseDate(bill.getInvoice_Date(), "dd-MMM-yy", "dd");
					if (null != dat && !StringUtils.isEmpty(dat)) {
						data.put("ProfileBillDate", IndoUtil.parseDate(bill.getInvoice_Date(), "dd-MMM-yy", "dd"));
					} else {
						data.put("ProfileBillDate", "");
					}
					data.put("InvoiceDate", bill.getInvoice_Date());
					data.put("PaymentDueDate", bill.getPayment_Due_Date());
					data.put("Pulsa", bill.getPulsa());
					data.put("TotalTagihan", bill.getTotal_Tagihan());
					data.put("TagihanSKR", bill.getTagihan_SKR());
				}
				if (null != response && null != response.getSimType()) {
					simType = response.getSimType();
				}
				if (null != response && null != response.getCustBalanceInfo()) {
					lastBal = response.getCustBalanceInfo();
				}
				data.put("Status", "SUCCESS");
				data.put("Msisdn", msisdn);
				data.put("CardType", simType);
				data.put("PackagesList", list);
				data.put("ServiceClass", response.getServiceClass());
				data.put("LastBalance", lastBal);
				cache.put(msisdn + "_getPackageSSP_" + lang, data, 30, TimeUnit.SECONDS);
			}
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(data, "Saturn-008", "Saturn-109", 0);
			log.error("Saturn 008--- GenericServiceImpl.getPackageSSP() - " + IndoUtil.getFullLog(ce));
		} finally {
			log.info("GenericServiceImpl.getPackageSSP() - END");
		}
		return data;
	}

	@Override
	public List<Map<String, Object>> getBuyExtra(String packCode, String packcat) {
		log.info("GenericServiceImpl.getBuyExtra() - START");
		List<Map<String, Object>> data = null;
		Cache<String, Object> cache = CacheUtil.getInstance().getEntityCache();
		data = (List<Map<String, Object>>) cache.get("BuyExtra");
		// log.info("GenericServiceImpl.getBuyExtra() getBuyExtra "+data);
		if (null != data) {
			return data;
		} else {
			data = dbUtil.getData("SELECT * FROM saturn_pack_buy_extra", new Object[] {});
			if (null != data && data.size() > 0) {
				cache.put("BuyExtra", data, 5, TimeUnit.MINUTES);
			}
		}
		log.info("GenericServiceImpl.getBuyExtra() - END");
		return data;
	}

	@Override
	public Map<String, Object> updateUserType(String msisdn, String type) {
		log.info("GenericServiceImpl.updateUserType() - START");
		msisdn = IndoUtil.prefix62(msisdn);
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			int ct = dbUtil.saveData("update SATURN_WSS_MSISDN set user_type=? where msisdn=?",
					new Object[] { type, msisdn });
			if (ct > 0) {
				map.put("Status", "SUCCESS");
				return map;
			} else {
				IndoUtil.populateErrorMap(map, "Saturn-009", "Saturn-109", 0);
			}
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(map, "Saturn-009", "Saturn-101", 0);
			log.error("GenericServiceImpl.updateUserType() ce" + IndoUtil.getFullLog(ce));
		} finally {
			log.info("GenericServiceImpl.updateUserType() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> versionCheck(String currentVersion) {
		log.info("GenericServiceImpl.versionCheck() - START");
		if (null == currentVersion || currentVersion.equals("")) {
			currentVersion = "0.0";
		}
		Map<String, Object> data = new HashMap<String, Object>();
		String upgradeType = "NU";
		try {
			Map<String, Object> data1 = new HashMap<String, Object>();
			data1 = dbUtil.getRow("select * from SATURN_APP_VERSION WHERE currentversioncode=?",
					new Object[] { currentVersion });
			if (!data1.isEmpty() && null != data1.get("latestVersionCode")) {
				upgradeType = data1.get("MANDATORYFLAG").toString().trim();
				data.put("Status", "SUCCESS");
				data.put("upgradeType", upgradeType);
				if (data1.get("latestVersionCode") != null) {
					data.put("latestVersionCode", data1.get("latestVersionCode").toString().trim());
				}
				if (data1.get("versionDescription") != null) {
					data.put("versionDescription", data1.get("versionDescription").toString().trim());
				}
				if (data1.get("latestVersionName") != null) {
					data.put("latestVersion", data1.get("latestVersionName").toString().trim());
				}
			} else {
				IndoUtil.populateErrorMap(data, "Saturn-010", "Version Check Failure.", 0);
			}
		} catch (EmptyResultDataAccessException ra) {

		} catch (Exception ce) {
			IndoUtil.populateErrorMap(data, "Saturn-010", "Saturn-101", 0);
			log.error("Saturn-010- GenericServiceImpl.versionCheck() " + ce);
		} finally {
			log.info("GenericServiceImpl.versionCheck() - END");
		}
		data.put("upgradeType", upgradeType);
		return data;
	}

	@Override
	public Map<String, Object> saveImage(String msisdn, String profilepic) {
		log.info("GenericServiceImpl.saveImage() - START");
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			Map<String, String> qMap = new HashMap<String, String>();
			qMap.put("MSISDN", msisdn);
			qMap.put("CUST_IMAGE", profilepic);
			qMap.put("CREATED_ON", "SYSDATE");
			qMap.put("UPDATED_ON", "SYSDATE");
			Map<String, String> qMapUpd = new HashMap<String, String>();
			qMap.put("CUST_IMAGE", profilepic);
			qMap.put("UPDATED_ON", "SYSDATE");
			Map<String, Object> queries = IndoUtil.saveOrUpdateSpring(qMap, qMapUpd, "MSISDN", msisdn,
					"SATURN_PROFILE_PIC");
			int ct = dbUtil.saveORUpdate(queries, true);
			if (ct > 0) {
				data.put("Status", "SUCCESS");
			} else {
				IndoUtil.populateErrorMap(data, "Saturn-011", "Saturn-109", 0);
			}
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(data, "Saturn-012", "Saturn-101", 0);
			log.error("Saturn-012- GenericServiceImpl.saveImage() ce -" + ce.getMessage());
		} finally {
			log.info("GenericServiceImpl.saveImage() - END");
		}
		return data;
	}

	@Override
	public Map<String, Object> getProfImage(String msisdn) {
		log.info("GenericServiceImpl.getProfImage() - START");
		String defImage = "DEFAULT_IMAGE";
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			data = dbUtil.getRow("select CUST_IMAGE as \"cust_image\" from SATURN_PROFILE_PIC WHERE msisdn=?",
					new Object[] { msisdn });
			data.put("Status", "SUCCESS");
		} catch (EmptyResultDataAccessException ce) {
			data = dbUtil.getRow("select CUST_IMAGE as \"cust_image\" from SATURN_PROFILE_PIC WHERE msisdn=?",
					new Object[] { defImage });
			data.put("Status", "SUCCESS");

		} catch (Exception ce) {
			IndoUtil.populateErrorMap(data, "Saturn-013", "Saturn-101", 0);
			log.error("Saturn-013- GenericServiceImpl.getProfImage() ce -" + IndoUtil.getFullLog(ce));
		} finally {
			log.info("GenericServiceImpl.getProfImage() - END");
		}
		return data;
	}

	@Override
	public Map<String, Object> updateProfileNew(String id, String msisdn, String name) {
		log.info("GenericServiceImpl.updateProfile() - START");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			int ct = 0;
			ct = dbUtil.saveData("UPDATE WSS_USER SET NAME=? where USERNAME=?", new Object[] { name, id });
			if (!StringUtils.isEmpty(msisdn) && ct == 0) {
				Object[] objArray = IndoUtil.appendValue(IndoUtil.msisdnArray(msisdn), id);
				List<Map<String, Object>> data = dbUtil.getData(
						"Select a.USERNAME as \"user_id\" from WSS_USER a, WSS_MSISDN b where (b.msisdn=? or b.msisdn=? or b.msisdn=? or b.msisdn=? or b.msisdn=?) and a.username=? and a.id=b.user_id",
						objArray);
				if (null != data && data.size() > 0 && null != data.get(0) && null != data.get(0).get("user_id")) {
					if (id.equals(""))
						ct = dbUtil.saveData("UPDATE WSS_USER SET NAME=? where USERNAME=?",
								new Object[] { name, data.get(0).get("user_id") });
				}
			}
			if (ct > 0) {
				map.put("Status", "SUCCESS");
				return map;
			} else {
				IndoUtil.populateErrorMap(map, "Saturn-014", "Saturn-109", 0);
			}
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(map, "Saturn-014", "Saturn-101", 0);
			log.error("GenericServiceImpl.updateProfile() ce" + IndoUtil.getFullLog(ce));
		} finally {
			log.info("GenericServiceImpl.updateProfile() - END");
		}
		return map;
	}

	/************************************************
	 * SPRINT 2
	 ***************************************************************/
	@Override
	public Map<String, String> activatePackage(String msisdn, String custType, String serviceName, String param,
			String chargingId, String userid, String transactionType, JsonObject jObj) {
		log.info("GenericServiceImpl.activatePackage() - START");
		Map<String, String> data = new HashMap<String, String>();
		String inputXML = "";
		String action = "";
		List<Map<String, Object>> pack = null;
		if (transactionType.equals("Activate")) {
			action = "ADD";
		} else if (transactionType.equals("Deactivate")) {
			action = "ADD";
			pack = dbUtil.getData("SELECT * from saturn_package_act where pack_code=?", new Object[] { serviceName });
			if (pack.size() > 0 && null != pack.get(0).get("SHORT_CODE")
					&& !org.apache.commons.lang3.StringUtils.isEmpty(pack.get(0).get("SHORT_CODE").toString())) {
				param = pack.get(0).get("SHORT_CODE").toString();
				serviceName = pack.get(0).get("KEYWORD").toString();
			} else {
				param = serviceName;
			}
		}
		/*
		 * if(null!=transactionType &&
		 * transactionType.equalsIgnoreCase("Activate")){ action="ADD"; }else
		 * if(null!=transactionType &&
		 * transactionType.equalsIgnoreCase("Deactivate")){ action="DELETE"; }
		 */
		try {
			String url = prop.getProperty("SSP_ACTIVATION_URL");
			String clientId = prop.getProperty("SSP_CLIENTID");
			String uid = prop.getProperty("SSP_UID");
			String pwd = prop.getProperty("SSP_PWD");
			log.info("URL is " + url);
			String transDate = IndoUtil.parseDate(new Date(), "yyyyMMddHHmmssSSS");
			String tid = msisdn + transDate;
			if (chargingId != null && !chargingId.isEmpty()) {
				inputXML = "<CLIENTID>" + clientId + "</CLIENTID>" + "<DATETIME>" + transDate + "</DATETIME>"
						+ "<TRANSID>" + tid + "</TRANSID>" + "<MSISDN>" + msisdn + "</MSISDN>" + "<SERVICENAME>"
						+ serviceName + "</SERVICENAME>" + "<ACTION>" + action + "</ACTION>" + "<SERVICEID></SERVICEID>"
						+ "<IMSI>imsi</IMSI>" + "<SERVICETYPE>" + custType + "</SERVICETYPE>" + "<PARAM>" + param
						+ "</PARAM>" + "<ATTRIBUTES>" + "<KEY>" + "<NAME>TOKEN</NAME>" + "<VALUE>" + chargingId
						+ "</VALUE>" + "</KEY>" + "</ATTRIBUTES>";
			} else {
				inputXML = "<CLIENTID>" + clientId + "</CLIENTID>" + "<DATETIME>" + transDate + "</DATETIME>"
						+ "<TRANSID>" + tid + "</TRANSID>" + "<MSISDN>" + msisdn + "</MSISDN>" + "<SERVICENAME>"
						+ serviceName + "</SERVICENAME>" + "<ACTION>" + action + "</ACTION>" + "<SERVICEID></SERVICEID>"
						+ "<SERVICETYPE></SERVICETYPE>" + "<PARAM>" + param + "</PARAM>"
						+ "<ATTRIBUTES><KEY><NAME></NAME><VALUE></VALUE></KEY></ATTRIBUTES>";
			}
			log.info("input XML before signature" + inputXML);
			String sig = IndoUtil.getMD5(uid + pwd + inputXML.toUpperCase());
			log.info("Signature " + sig);
			String withSig = inputXML.toUpperCase() + "<SIGNATURE>" + sig + "</SIGNATURE>";
			log.info("Post Signature " + withSig);
			String encodeXML = new String(Base64.encode(withSig.getBytes()));
			// log.info("Encoded XML is "+ encodeXML);
			String urlEncodeXML = URLEncoder.encode(encodeXML, "UTF-8");
			log.info("urlEncodeXML XML is " + urlEncodeXML);
			try {
				ActivityVO actVo = new ActivityVO(msisdn, tid, "ManagePackage", userid, "", "");
				if (custType != null) {
					actVo.setUserType(custType);
				}
				actVo.setText1(param);
				actVo.setText2(serviceName);
				actVo.setText3(action);
				if (null != jObj.get("desc_id")) {
					actVo.setDescEn(jObj.get("desc_id").getAsString());
				}
				if (null != jObj.get("desc_en")) {
					actVo.setDescEn(jObj.get("desc_en").getAsString());
				}
				Map<String, String> activityDataLog = logActivity(actVo);
			} catch (Exception e) {
				log.error("GenericServiceImpl.activatePackage() " + IndoUtil.getFullLog(e));
			}
			List<String> att = new ArrayList<String>();
			att.add("TID");
			att.add("TIME");
			att.add("STATUS");
			att.add("DESC");
			Map<String, String> data1 = IndoXMLParseUtil.getAttributes(att, urlEncodeXML, url);
			data1.put("Status", "SUCCESS");
			log.info("Resposne data is " + data1);
			return data1;
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(data, "Saturnn-2059", "Failed To Fetch Data.");
			log.error("Saturn-2059-GenericServiceImpl.activatePackage() " + IndoUtil.getFullLog(ce));
		} finally {
			log.info("GenericServiceImpl.activatePackage() - END");
		}
		return data;
	}

	@Override
	public Map<String, String> updatePackageTid(String tid, String status, String desc, String smsText) {
		log.info("GenericServiceImpl.updatePackageTid() - START");
		Map<String, String> data = new HashMap<String, String>();
		try {
			int res1 = dbUtil.saveData(
					"UPDATE SATURN_ACTIVITY SET ORDER_STATUS=? , DESCRIPTION =? , SMS_TEXT=?, TEXT5=to_char(sysdate,'dd-mm-yyyy hh:mi AM') where COMMID=?",
					new Object[] { status, desc, smsText, tid });
			if (res1 > 0) {
				log.info("Status Updated successfully in activity.");

				Map<String, Object> row = dbUtil.getRow("SELECT * FROM SATURN_ACTIVITY WHERE commid =?",
						new Object[] { tid });
				int ct = dbUtil.saveData(
						"INSERT into saturn_messages(id,title,message,type,read,to_msisdn,from_date,commid) values(saturn_msg_seq.NEXTVAL,?,?,?,?,?,sysdate,?)",
						new Object[] { row.get("activity_type").toString(), row.get("sms_text").toString(), "message",
								"N", row.get("msisdn").toString(), tid });
				if (ct > 0) {
					log.info("Message saved.");
				}

			}
		} catch (Exception ce) {
			log.error("Saturn-2069- GenericServiceImpl.updatePackageTid() " + IndoUtil.getFullLog(ce));
		} finally {
			log.info("GenericServiceImpl.updatePackageTid() - END");
		}
		return data;
	}
	/*
	 * @Override public Map<String, String> checkDompetkuReg(String
	 * msisdn,String agentId) {
	 * log.info("GenericServiceImpl.checkDompetkuReg() - START");
	 * if(msisdn.startsWith("628")){ msisdn = msisdn.replaceFirst("628", "08");
	 * } Map<String, String> data = new HashMap<String, String>(); try{
	 * TripleDES td= new TripleDES(); DateFormat timeFormat = new
	 * SimpleDateFormat("HHmmss"); String dte=timeFormat.format(new Date());
	 * //StringBuilder domPin=new StringBuilder("1nd054t2ois"); //String
	 * initiator="4gmobileagent"; // String myEncryptionKey =
	 * "4yL8GJqTH5EiX0PPC0eT1lRZ"; //String httpsURL =
	 * "https://mapi.dompetku.com/webapi/user_inquiry";//prod url //String query
	 * = "userid=4gmobileagent"; StringBuilder domPin=new
	 * StringBuilder(prop.getProperty("REG_DOMPETKU_PIN")); String
	 * initiator=prop.getProperty("REG_DOMPETKU_INITIATOR"); String
	 * myEncryptionKey = prop.getProperty("REG_DOMPETKU_KEY"); String httpsURL =
	 * prop.getProperty("REG_CHECK_DOMPETKU"); String sigA=dte+domPin; String
	 * sigB=domPin.reverse()+"|"+initiator; String sigC=sigA+"|"+sigB; String
	 * encrypted=td.encrypt(sigC, myEncryptionKey); /* String query =
	 * "userid="+prop.getProperty("REG_DOMPETKU_USERID"); query += "&signature="
	 * + encrypted ; query += "&agentid="+agentId ; query +=
	 * "&to="+msisdn+"&locationid=0"; List<NameValuePair> urlParameters = new
	 * ArrayList<NameValuePair>(); urlParameters.add(new
	 * BasicNameValuePair("userid", prop.getProperty("REG_DOMPETKU_USERID")));
	 * urlParameters.add(new BasicNameValuePair("signature", encrypted));
	 * urlParameters.add(new BasicNameValuePair("agentid", agentId));
	 * urlParameters.add(new BasicNameValuePair("to", msisdn));
	 * urlParameters.add(new BasicNameValuePair("locationid", "0")); HttpEntity
	 * entity = null; CloseableHttpClient client = null; HttpPost request =
	 * null; try{ client = httpConn.getHttpClient(); request = new
	 * HttpPost(httpsURL); request.setEntity(new
	 * UrlEncodedFormEntity(urlParameters)); HttpResponse response =
	 * client.execute(request); int statusCode =
	 * response.getStatusLine().getStatusCode(); if (statusCode == 200 ){ entity
	 * = response.getEntity(); String content = EntityUtils.toString(entity);
	 * log.info(""); log.info("Result is "+content);
	 * log.info("Resp Code:"+statusCode); JSONObject jsonObj = new
	 * JSONObject(content);
	 * if(Integer.valueOf(jsonObj.get("status").toString())==0){
	 * data.put("Status", "SUCCESS"); data.put("TransactionId",
	 * jsonObj.get("trxid").toString()); data.put("Balance",
	 * jsonObj.get("balance").toString()); data.put("Name",
	 * jsonObj.get("name").toString()); data.put("DompetkuStatus", "YES");
	 * }else{ data.put("Status", "SUCCESS"); data.put("ErrorCode",
	 * "Saturn-1016"); data.put("ErrorDescription",
	 * jsonObj.get("msg").toString()); data.put("DompetkuStatus", "NO"); }
	 * return data; }else{ data.put("Status", "Failure"); data.put("ErrorCode",
	 * "Saturn-1016"); data.put("ErrorDescription", "No Data Found."); entity =
	 * response.getEntity(); String content = EntityUtils.toString(entity);
	 * log.info("error dompetku check- "+content); } }catch(IOException e){
	 * IndoUtil.populateErrorMap(data, "Saturn-1023","Saturn-101");
	 * log.error("Saturn-2061- GenericServiceImpl.checkDompetkuReg()- e"
	 * +IndoUtil.getFullLog(e)); }catch(Exception ce){
	 * IndoUtil.populateErrorMap(data, "Saturn-1023","Saturn-101");
	 * log.error("Saturn-2062- GenericServiceImpl.checkDompetkuReg()- ce"
	 * +IndoUtil.getFullLog(ce)); }finally{
	 * log.info("***********Closing Streams********"); try {
	 * EntityUtils.consumeQuietly(entity); if(null!=request){
	 * request.releaseConnection(); }if(null!=client){ client.close(); } } catch
	 * (IOException e) {
	 * log.error("Saturn-2062- GenericServiceImpl.checkDompetkuReg() e "+e); } }
	 * }catch(Exception ce){ IndoUtil.populateErrorMap(data,
	 * "Saturn-1023",ce.getClass().getSimpleName());
	 * log.error("Saturn-2063- GenericServiceImpl.checkDompetkuReg() ce "
	 * +IndoUtil.getFullLog(ce)); }finally{
	 * log.info("GenericServiceImpl.checkDompetkuReg() - END"); } return data; }
	 */

	@Override
	public Map<String, String> dompetkuPay(String msisdn, String amount, String paymentId) {
		log.info("GenericServiceImpl.checkDompetkuReg() - START");
		if (msisdn.startsWith("628")) {
			msisdn = msisdn.replaceFirst("628", "08");
		}
		Map<String, String> data = new HashMap<String, String>();
		try {
			TripleDES td = new TripleDES();
			DateFormat timeFormat = new SimpleDateFormat("HHmmss");
			String dte = timeFormat.format(new Date());
			// StringBuilder domPin=new StringBuilder("1nd054t2ois");
			// String initiator="4gmobileagent";
			// String myEncryptionKey = "4yL8GJqTH5EiX0PPC0eT1lRZ";
			// String httpsURL =
			// "https://mapi.dompetku.com/webapi/user_inquiry";//prod url
			// String query = "userid=4gmobileagent";
			StringBuilder domPin = new StringBuilder(prop.getProperty("REG_DOMPETKU_PIN"));
			String initiator = prop.getProperty("REG_DOMPETKU_INITIATOR");
			String myEncryptionKey = prop.getProperty("REG_DOMPETKU_KEY");
			// String httpsURL = "https://mapi.dompetku.com/webapi/do_payment";
			String httpsURL = prop.getProperty("PAY_DOMPETKU");
			log.info("httpsURL= " + httpsURL);
			String sigA = dte + domPin;
			String sigB = domPin.reverse() + "|" + initiator;
			String sigC = sigA + "|" + sigB;
			String encrypted = td.encrypt(sigC, myEncryptionKey);

			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("userid", prop.getProperty("REG_DOMPETKU_USERID")));
			urlParameters.add(new BasicNameValuePair("signature", encrypted));
			urlParameters.add(new BasicNameValuePair("amount", amount));
			urlParameters.add(new BasicNameValuePair("to", msisdn));
			urlParameters.add(new BasicNameValuePair("extRef", paymentId));
			HttpEntity entity = null;
			CloseableHttpClient client = null;
			HttpPost request = null;
			try {
				client = httpConn.getHttpClient();
				request = new HttpPost(httpsURL);
				request.setEntity(new UrlEncodedFormEntity(urlParameters));
				HttpResponse response = client.execute(request);
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					entity = response.getEntity();
					String content = EntityUtils.toString(entity);
					log.info("");
					log.info("Result is " + content);
					log.info("Resp Code:" + statusCode);
					JSONObject jsonObj = new JSONObject(content);
					if (Integer.valueOf(jsonObj.get("status").toString()) == 0) {
						data.put("Status", "SUCCESS");
						data.put("TransactionId", jsonObj.get("trxid").toString());

					} else {
						data.put("Status", "FAILURE");
						data.put("ErrorCode", "Saturn-1016");
						data.put("ErrorDescription", jsonObj.get("msg").toString());
						data.put("DompetkuStatus", "NO");
					}
					return data;
				} else {
					data.put("Status", "Failure");
					data.put("ErrorCode", "Saturn-1016");
					data.put("ErrorDescription", "No Data Found.");
					entity = response.getEntity();
					String content = EntityUtils.toString(entity);
					log.info("error dompetku check- " + content);
				}
			} catch (IOException e) {
				IndoUtil.populateErrorMap(data, "Saturn-1023", "Saturn-101");
				log.error("Saturn-2061- GenericServiceImpl.checkDompetkuReg()- e" + IndoUtil.getFullLog(e));
			} catch (Exception ce) {
				IndoUtil.populateErrorMap(data, "Saturn-1023", "Saturn-101");
				log.error("Saturn-2062- GenericServiceImpl.checkDompetkuReg()- ce" + IndoUtil.getFullLog(ce));
			} finally {
				log.info("***********Closing Streams********");
				try {
					EntityUtils.consumeQuietly(entity);
					if (null != request) {
						request.releaseConnection();
					}
					if (null != client) {
						client.close();
					}
				} catch (IOException e) {
					log.error("Saturn-2062- GenericServiceImpl.checkDompetkuReg() e " + e);
				}
			}
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(data, "Saturn-1023", ce.getClass().getSimpleName());
			log.error("Saturn-2063- GenericServiceImpl.checkDompetkuReg() ce " + IndoUtil.getFullLog(ce));
		} finally {
			log.info("GenericServiceImpl.checkDompetkuReg() - END");
		}
		return data;
	}

	@Override
	public Map<String, String> checkDompetkuReg(String msisdn, String agentId) {
		log.info("GenericServiceImpl.checkDompetkuReg() - START");
		if (msisdn.startsWith("628")) {
			msisdn = msisdn.replaceFirst("628", "08");
		}
		Map<String, String> data = new HashMap<String, String>();
		try {
			TripleDES td = new TripleDES();
			DateFormat timeFormat = new SimpleDateFormat("HHmmss");
			String dte = timeFormat.format(new Date());
			// StringBuilder domPin=new StringBuilder("1nd054t2ois");
			// String initiator="4gmobileagent";
			// String myEncryptionKey = "4yL8GJqTH5EiX0PPC0eT1lRZ";
			// String httpsURL =
			// "https://mapi.dompetku.com/webapi/user_inquiry";//prod url
			// String query = "userid=4gmobileagent";
			StringBuilder domPin = new StringBuilder(prop.getProperty("REG_DOMPETKU_PIN"));
			String initiator = prop.getProperty("REG_DOMPETKU_INITIATOR");
			String myEncryptionKey = prop.getProperty("REG_DOMPETKU_KEY");
			String httpsURL = prop.getProperty("REG_CHECK_DOMPETKU");
			String sigA = dte + domPin;
			String sigB = domPin.reverse() + "|" + initiator;
			String sigC = sigA + "|" + sigB;
			String encrypted = td.encrypt(sigC, myEncryptionKey);
			/*
			 * String query = "userid="+prop.getProperty("REG_DOMPETKU_USERID");
			 * query += "&signature=" + encrypted ; query += "&agentid="+agentId
			 * ; query += "&to="+msisdn+"&locationid=0";
			 */
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("userid", prop.getProperty("REG_DOMPETKU_USERID")));
			urlParameters.add(new BasicNameValuePair("signature", encrypted));
			urlParameters.add(new BasicNameValuePair("agentid", agentId));
			urlParameters.add(new BasicNameValuePair("to", msisdn));
			urlParameters.add(new BasicNameValuePair("locationid", "0"));
			HttpEntity entity = null;
			CloseableHttpClient client = null;
			HttpPost request = null;
			try {
				client = httpConn.getHttpClient();
				request = new HttpPost(httpsURL);
				request.setEntity(new UrlEncodedFormEntity(urlParameters));
				HttpResponse response = client.execute(request);
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					entity = response.getEntity();
					String content = EntityUtils.toString(entity);
					log.info("");
					log.info("Result is " + content);
					log.info("Resp Code:" + statusCode);
					JSONObject jsonObj = new JSONObject(content);
					if (Integer.valueOf(jsonObj.get("status").toString()) == 0) {
						data.put("Status", "SUCCESS");
						data.put("TransactionId", jsonObj.get("trxid").toString());
						data.put("Balance", jsonObj.get("balance").toString());
						data.put("Name", jsonObj.get("name").toString());
						data.put("DompetkuStatus", "YES");
					} else {
						data.put("Status", "SUCCESS");
						data.put("ErrorCode", "Saturn-1016");
						data.put("ErrorDescription", jsonObj.get("msg").toString());
						data.put("DompetkuStatus", "NO");
					}
					return data;
				} else {
					data.put("Status", "Failure");
					data.put("ErrorCode", "Saturn-1016");
					data.put("ErrorDescription", "No Data Found.");
					entity = response.getEntity();
					String content = EntityUtils.toString(entity);
					log.info("error dompetku check- " + content);
				}
			} catch (IOException e) {
				IndoUtil.populateErrorMap(data, "Saturn-1023", "Saturn-101");
				log.error("Saturn-2061- GenericServiceImpl.checkDompetkuReg()- e" + IndoUtil.getFullLog(e));
			} catch (Exception ce) {
				IndoUtil.populateErrorMap(data, "Saturn-1023", "Saturn-101");
				log.error("Saturn-2062- GenericServiceImpl.checkDompetkuReg()- ce" + IndoUtil.getFullLog(ce));
			} finally {
				log.info("***********Closing Streams********");
				try {
					EntityUtils.consumeQuietly(entity);
					if (null != request) {
						request.releaseConnection();
					}
					if (null != client) {
						client.close();
					}
				} catch (IOException e) {
					log.error("Saturn-2062- GenericServiceImpl.checkDompetkuReg() e " + e);
				}
			}
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(data, "Saturn-1023", ce.getClass().getSimpleName());
			log.error("Saturn-2063- GenericServiceImpl.checkDompetkuReg() ce " + IndoUtil.getFullLog(ce));
		} finally {
			log.info("GenericServiceImpl.checkDompetkuReg() - END");
		}
		return data;
	}

	@Override
	public Map<String, Object> getPackage(String msisdn) {
		log.info("GenericServiceImpl.getPackage() - START");
		Map<String, Object> map = new HashMap<String, Object>();
		msisdn = IndoUtil.prefix62(msisdn);
		log.info("GenericServiceImpl.getPackage() -- START");
		// http://10.18.149.34:8080/mtpush/check_sub.jsp?MSISDN=6285714038605
		try {
			String url = "http://10.18.149.34:8080/mtpush/check_sub.jsp?MSISDN=" + msisdn;
			Document xmlDoc = Jsoup.connect(url).timeout(10000).get();
			log.info("xmDoc- " + xmlDoc.toString());
			map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("item")));
			Object obj = map.get("item");
			// log.info("GenericServiceImpl.getPackage() instanceof
			// "+obj.getClass());
			List<Map<String, String>> data = new ArrayList<Map<String, String>>();
			if (null != obj && obj instanceof List) {
				Map<String, String> offers = new HashMap<String, String>();
				List<String> items = (List<String>) map.get("item");
				for (String str : items) {
					String offer = str.substring(str.indexOf('+') + 1, str.indexOf("&MSISDN"));
					offers.put(offer, str);
				}
				data.add(offers);
				map.remove("item");
			}
			map.put("Offers", data);
			map.put("Status", "SUCCESS");
			return map;
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(map, "Saturn-9015", "Saturn-109", 0);
			log.error("GenericServiceImpl.getPackage() - ce " + IndoUtil.getFullLog(ce));
		} finally {
			log.info("GenericServiceImpl.getPackage() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> enrollOffers(String msisdn, String offerId) {
		log.info("GenericServiceImpl.enrollOffers() - START");
		Map<String, Object> map = new HashMap<String, Object>();
		msisdn = IndoUtil.prefix62(msisdn);
		log.info("GenericServiceImpl.enrollOffers() -- START");
		// http://10.18.149.34:8080/mtpush/check_sub.jsp?MSISDN=6285714038605
		try {
			DateFormat timeFormat = new SimpleDateFormat("HHmmss");
			String dte = timeFormat.format(new Date());
			String extRef = dte + msisdn.substring(msisdn.length() - 4, msisdn.length());

			String url = "http://10.6.16.16:15000/CRMWSC/WSC/EnrollInfo";
			String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><!DOCTYPE CMSResponse SYSTEM \"CMSRequest_Enrolment.dtd\"><CMSRequest> "
					+ "<TransactionID>" + extRef + "</TransactionID><Operation>enrollTargetSub</Operation>"
					+ "<Subscriber><msisdn>" + msisdn + "</msisdn> </Subscriber><CampaignID>" + offerId
					+ "</CampaignID>" + " <System>WSC</System></CMSRequest>";
			log.info("Request " + xml);
			Map<String, Object> data = xmlService.getRawXML(xml, url);
			if (IndoUtil.isSuccess(data)) {
				String response = (String) data.get("xml");
				Document xmlDoc = Jsoup.parse(response, "", Parser.xmlParser());
				map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("OperationStatus")));
				map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("msisdn")));
				map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("RequestStatus")));
				map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("Description")));
				if (null != map.get("OperationStatus")
						&& map.get("OperationStatus").toString().equalsIgnoreCase("SUCCESS")) {
					map.put("Status", "SUCCESS");
				} else {
					IndoUtil.populateErrorMap(map, "Saturn-101", "Saturn-101", 0);
				}
				map.remove("OperationStatus");
				return map;
			}
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(map, "Saturn-9015", "Saturn-109", 0);
			log.error("GenericServiceImpl.getPackage() - ce " + IndoUtil.getFullLog(ce));
		} finally {
			log.info("GenericServiceImpl.getPackage() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> getCMSOffers(String msisdn) {
		log.info("GenericServiceImpl.getCMSOffers() - START");
		Map<String, Object> map = new HashMap<String, Object>();
		msisdn = IndoUtil.prefix62(msisdn);
		log.info("GenericServiceImpl.getCMSOffers() -- START");
		// http://10.18.149.34:8080/mtpush/check_sub.jsp?MSISDN=6285714038605
		try {
			DateFormat timeFormat = new SimpleDateFormat("HHmmss");
			String dte = timeFormat.format(new Date());
			String extRef = dte + msisdn.substring(msisdn.length() - 4, msisdn.length());

			String url = "http://10.6.16.16:15000/CRMWSC/WSC/QueryInfo";
			String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><!DOCTYPE CMSResponse SYSTEM \"CMSRequest.dtd\"><CMSRequest> "
					+ "<TransactionID>" + extRef + "</TransactionID><Operation>querySubInfo</Operation>"
					+ "<Subscriber><msisdn>" + msisdn + "</msisdn> </Subscriber>	"
					+ " <System>WSC</System></CMSRequest>";
			Map<String, Object> data = xmlService.getRawXML(xml, url);
			log.info("Request " + xml);
			if (IndoUtil.isSuccess(data)) {
				String response = (String) data.get("xml");
				log.info("Response " + response);
				Document xmlDoc = Jsoup.parse(response, "", Parser.xmlParser());
				Elements eles = xmlDoc.select("Campaign");
				List<Map<String, String>> d = IndoXMLParseUtil.getParentChildXML(eles);
				if (null != d) {
					List<Map<String, String>> list = new ArrayList<Map<String, String>>();
					for(Map<String, String> m: d){
						Map<String, String> dataMap = new HashMap<String, String>(m);
						try{
							String CampaignDesc[] = dataMap.get("CampaignDesc").split("\\|");
							dataMap.remove("CampaignDesc");
							dataMap.put("Channel", CampaignDesc[0]);
							dataMap.put("OfferId", CampaignDesc[1]);
							dataMap.put("CampaignName_EN", CampaignDesc[2]);
							dataMap.put("Tariff", CampaignDesc[3]);
							dataMap.put("EnrollFlag", CampaignDesc[4]);
							dataMap.put("Description_ID", CampaignDesc[5]);
							dataMap.put("Description_EN", CampaignDesc[6]);
							dataMap.put("Message_ID", CampaignDesc[7]);
							dataMap.put("Message_EN", CampaignDesc[8]);
						}catch(NullPointerException|IndexOutOfBoundsException e)	{	
							log.info("GenericServiceImpl.getCMSOffers() Not enough values");
						}
						list.add(dataMap);
					}
					map.put("Campaigns", list);
					map.put("Status", "SUCCESS");
				} else {
					IndoUtil.populateErrorMap(map, "Saturn-101", "Saturn-101", 0);
				}
				return map;
			}
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(map, "Saturn-9015", "Saturn-109", 0);
			log.error("GenericServiceImpl.getPackage() - ce " + IndoUtil.getFullLog(ce));
		} finally {
			log.info("GenericServiceImpl.getPackage() - END");
		}
		return map;
	}
	@Override
	public Map<String, Object> vasActivate(String msisdn, String id, String userid) {
		log.info("GenericServiceImpl.vasActivate() - START");
		msisdn = IndoUtil.prefix62(msisdn);
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String url = "http://10.234.175.210/isatlp/mycare/?id=" + id + "&msisdn=" + msisdn;
			log.info("GenericServiceImpl.vasActivate() url "+url);
			Document xmlDoc = Jsoup.connect(url).timeout(10000).get();
			log.info("xmDoc- " + xmlDoc.toString());
			map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("STATUS")));
			map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("TRANSID")));
			map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("MSG")));
			map.put("Status", "SUCCESS");

			ActivityVO actVo = new ActivityVO();
			actVo.setActivityType("ActivateVAS");
			if (null != map.get("TRANSID")) {
				actVo.setCommid(map.get("TRANSID").toString());
			}
			actVo.setMsisdn(msisdn);
			actVo.setUserId(userid);
			actVo.setText3("Activate");
			Map<String, String> activityDataLog = logActivity(actVo);
			return map;
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(map, "Saturn-9011", "Saturn-109", 0);
			log.error("GenericServiceImpl.vasActivate() - ce " + IndoUtil.getFullLog(ce));
		} finally {
			log.info("GenericServiceImpl.vasActivate()  - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> vasDeactivate(String msisdn, String id, String userid) {
		log.info("GenericServiceImpl.vasDeactivate() - START");
		msisdn = IndoUtil.prefix62(msisdn);
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String url = "http://10.234.175.210/isatlp/mycare_unreg/?id=" + id + "&msisdn=" + msisdn;
			log.info("GenericServiceImpl.vasDeactivate() url "+url);
			Document xmlDoc = Jsoup.connect(url).timeout(10000).get();
			log.info("GenericServiceImpl.vasDeactivate() xmlDoc " + xmlDoc.toString());
			map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("STATUS")));
			map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("TRANSID")));
			map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("MSG")));
			map.put("Status", "SUCCESS");

			ActivityVO actVo = new ActivityVO();
			actVo.setActivityType("DeactivateVAS");
			if (null != map.get("TRANSID")) {
				actVo.setCommid(map.get("TRANSID").toString());
			}
			actVo.setMsisdn(msisdn);
			actVo.setUserId(userid);
			actVo.setText3("Deactivate");
			Map<String, String> activityDataLog = logActivity(actVo);

			log.info("GenericServiceImpl.vasActivate() Document " + xmlDoc.toString());
			return map;
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(map, "Saturn-9011", "Saturn-109", 0);
			log.error("GenericServiceImpl.vasActivate() - ce " + IndoUtil.getFullLog(ce));
		} finally {
			log.info("GenericServiceImpl.vasDeactivate() - END");
		}
		return map;
	}

	@Override
	public Map<String, String> logActivity(ActivityVO activity) {
		log.info("GenericServiceImpl.logActivity() - START");
		Map<String, String> data = new HashMap<String, String>();
		try {
			int ct = dbUtil.saveData(
					"INSERT INTO SATURN_ACTIVITY(MSISDN,COMMID,ACTIVITY_TYPE,USERID,CREATED_ON,USERTYPE,DESCRIPTION,ORDER_STATUS,TEXT1,TEXT2,TEXT3,TEXT4,TEXT5,DESCRIPTION_EN,DESCRIPTION_ID) values(?,?,?,?,sysdate,?,?,?,?,?,?,?,?,?,?)",
					new Object[] { IndoUtil.validateNumber(activity.getMsisdn()), activity.getCommid(),
							activity.getActivityType(), activity.getUserId(), activity.getUserType(),
							activity.getDescription(), activity.getOrderStatus(), activity.getText1(),
							activity.getText2(), activity.getText3(), activity.getText4(), activity.getText5(),
							activity.getDescEn(), activity.getDescId() });
			if (ct > 0) {
				data.put("Status", "SUCCESS");
			} else {
				IndoUtil.populateErrorMap(data, "Saturn-106", "Activity not saved.");
			}
			return data;
		} catch (Exception e) {
			IndoUtil.populateErrorMap(data, "Saturn-106", "Activity not saved.");
			log.error("Saturn-2015- GenericServiceImpl.logActivity() e1" + e.getMessage());
		} finally {
			log.info("GenericServiceImpl.logActivity() - END");
		}
		return data;
	}

	@Override
	public Map<String, Object> retrieveUpgradablePackages(String custType, String package_type, String serviceClass) {
		log.info("GenericServiceImpl.retrieveUpgradablePackages() - START");
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> vList = new ArrayList<Map<String, Object>>();
		if (null != custType && !custType.equalsIgnoreCase("PREPAID")) {
			custType = getServiceCodeCat(serviceClass);
		}
		try {
			if (package_type.equalsIgnoreCase("VAS Content")) {
				vList = dbUtil.getData(
						"SELECT * from SATURN_VAS_INFO WHERE upper(PACKAGE_CATEGORY)=? AND PACKAGE_GROUP=?",
						new Object[] { custType.toUpperCase(), package_type });
				// log.info("List size from system is "+vList.size());
				map.put("PackagesList", vList);
				map.put("Status", "SUCCESS");
			} else {
				vList = dbUtil.getData(
						"SELECT * from SATURN_PACKAGE_INFO WHERE (upper(PACKAGE_CATEGORY)=? AND PACKAGE_GROUP=?) and sysdate between start_date and end_date",
						new Object[] { custType.toUpperCase(), package_type });
				// log.info("List size from system is "+vList.size());
				map.put("PackagesList", vList);
				map.put("Status", "SUCCESS");
			}
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(map, "Saturn-1024", "", 0);
			log.error("Saturn-2051- GenericServiceImpl.retrieveUpgradablePackages() ce " + IndoUtil.getFullLog(ce));
		} finally {
			log.info("GenericServiceImpl.retrieveUpgradablePackages() - END");
		}
		return map;
	}

	@Override
	public String getServiceCodeCat(String serviceClass) {
		Map<String, String> saturn_pack_sc = null;
		String category = "POSTPAID";
		List<Map<String, Object>> vList = new ArrayList<Map<String, Object>>();
		Cache<String, Object> cache = GenericCache.getInstance().getEntityCache();
		if(null!=cache){
			saturn_pack_sc = ((Map<String, String>) cache.get("saturn_pack_sc"));
		}
		if (null == saturn_pack_sc) {
			saturn_pack_sc = new HashMap<String, String>();
			vList = dbUtil.getData("SELECT * from saturn_pack_sc", new Object[] {});
			for (Map<String, Object> m : vList) {
				String cat = "";
				if (null != m.get("PACK_CAT")) {
					cat = m.get("PACK_CAT").toString();
				}
				String sc = "";
				if (null != m.get("SC")) {
					sc = m.get("SC").toString();
				}
				saturn_pack_sc.put(cat, sc);
			}
			cache.put("saturn_pack_sc", saturn_pack_sc, 1, TimeUnit.MINUTES);
		}
		if (null != saturn_pack_sc && !StringUtils.isEmpty(serviceClass)) {
			for (Map.Entry<String, String> entry : saturn_pack_sc.entrySet()) {
				if (entry.getValue().contains(serviceClass)) {
					category = entry.getKey();
					break;
				}
			}
		}
		if (null != category && category.equalsIgnoreCase("CORPORATE") && !serviceClass.equals("4507")) {
			return "";
		}
		return category;
	}

	public static String[] clean(final String[] v) {
		List<String> list = new ArrayList<String>(Arrays.asList(v));
		list.removeAll(Collections.singleton(null));
		return list.toArray(new String[list.size()]);
	}

	public static void main(String args[]) {
		JsonObject jObj = (new JsonParser()).parse("{\"OTP\":23456}").getAsJsonObject();
		log.info("GenericServiceImpl.main() " + jObj.get("OTP").getAsString());
		/*
		 * double d = Double.parseDouble(""); DecimalFormat df = new
		 * DecimalFormat("0.00"); log.info(df.format(d)); String[] s = new
		 * String[]{"A","B","C",null,null}; String[] a = new
		 * String[]{"A","B","C"}; String[] f = ArrayUtils.addAll(s, a);
		 * 
		 * for(String str: f){ log.info(str); }
		 */
	}

	@Override
	public Map<String, Object> sendEmail(String id, String msg) {
		return null;
	}

	@Override
	public Map<String, Object> getChilds(String msisdn, String userid) {
		log.info("GenericServiceImpl.getChilds() - START");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			msisdn = IndoUtil.prefix62(msisdn);
			String qry = "";
			Object[] obj = new Object[1];
			if (StringUtils.isEmpty(userid) && !StringUtils.isEmpty(msisdn)) {
				qry = "SELECT b.msisdn as \"msisdn\", a.user_id as \"user_id\", a.user_name as \"user_name\", a.full_name as \"full_name\", a.birth_place as \"birth_place\","
						+ "a.id_number as \"id_number\", a.address  as \"address\", a.email as \"email\", a.birth_date as \"birth_date\", a.status as \"status\", "
						+ "b.user_type as \"user_type\",a.LASTLOGINDATE as \"last_login\", a.FAILED_ATTEMPTS,a.PWD FROM SATURN_USERS a ,SATURN_WSS_MSISDN b where  "
						+ "a.user_id=b.user_id  and b.user_id= (select user_id from SATURN_WSS_MSISDN where MSISDN=?)";
				obj[0] = IndoUtil.prefix62(msisdn);
			} else {
				qry = "SELECT b.msisdn as \"msisdn\", a.user_id as \"user_id\", a.user_name as \"user_name\", a.full_name as \"full_name\", a.birth_place as \"birth_place\", "
						+ "a.id_number as \"id_number\", a.address  as \"address\", a.email as \"email\", a.birth_date as \"birth_date\", a.status as \"status\", b.user_type as \"user_type\", "
						+ "a.LASTLOGINDATE as \"last_login\", a.FAILED_ATTEMPTS,a.PWD FROM SATURN_USERS a, SATURN_WSS_MSISDN b where b.user_id=? and a.user_id=b.user_id";
				obj[0] = userid;
			}
			List<Map<String, Object>> list = dbUtil.getData(qry, obj);
			if (null != list && list.size() > 0) {
				List<Map<String, Object>> listMsisdns = new ArrayList<Map<String, Object>>();
				// boolean vFlag = false;
				for (Map<String, Object> m : list) {
					Map<String, Object> msisdns = new HashMap<String, Object>();
					String mob = m.get("msisdn").toString();
					String user_type = "";
					msisdns.put("msisdn", mob);
					if (null == user_type || user_type.toString().equals("")) {
						Map<String, Object> ldap = ldapService.getUser(mob);
						log.info("GenericServiceImpl.authenticateUser() ldap " + ldap);
						if (IndoUtil.isSuccess(ldap) && null != ldap.get("user_type")) {
							user_type = ldap.get("user_type").toString();
							updateUserType(mob, ldap.get("user_type").toString());
							msisdns.put("user_type", ldap.get("user_type").toString());
						}
					} else {
						msisdns.put("user_type", user_type);
					}
					if (!StringUtils.isEmpty(userid) && !StringUtils.isEmpty(msisdn)
							&& mob.equals(IndoUtil.prefix62(msisdn))) {
						// vFlag = true;
						list.get(0).putAll(msisdns);
						msisdns.put("msisdn", mob);
						msisdns.put("user_type", user_type);
						listMsisdns.add(msisdns);
					} else if (!StringUtils.isEmpty(msisdn) && mob.equals(IndoUtil.prefix62(msisdn))) {
						list.get(0).putAll(msisdns);
						msisdns.put("msisdn", mob);
						msisdns.put("user_type", user_type);
						listMsisdns.add(msisdns);
					} else {
						listMsisdns.add(msisdns);
					}
				}
				map.put("data", list.get(0));
				map.put("msisdns", listMsisdns);
				map.put("Status", "SUCCESS");
			}
		} catch (Exception ce) {
			log.error("GenericServiceImpl.getChilds() ce " + IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Staurn-005", "Saturn-101", 0);
		} finally {
			log.info("GenericServiceImpl.getChilds() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> addChildNew(String parent, String child, String otp, String sOtp) {
		log.info("GenericServiceImpl.addChildNew() - START");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Map<String, Object> ldap = ldapService.getUser(IndoUtil.prefix62(child));
			if (IndoUtil.isSuccess(ldap)) {
				if (null == ldap.get("user_type") || ldap.get("user_type").toString().equals("")) {
					IndoUtil.populateErrorMap(map, "Staurn-005", "Saturn-1031", 0);
					return map;
				}
			} else {
				IndoUtil.populateErrorMap(map, "Staurn-005", "Saturn-1031", 0);
				return map;
			}
			Map<String, Object> childDetais = getMsisdnDetails(child);
			if (IndoUtil.isSuccess(childDetais)) {
				IndoUtil.populateErrorMap(map, "Saturn-1035", "Saturn-1035", 0);
				return map;
			}
			boolean add = false;
			if (null != otp && !otp.equals("")) {
				if (otp.equals(sOtp)) {
					add = true;
				} else {
					IndoUtil.populateErrorMap(map, "Saturn-529", "Invalid OTP.", 0);
					return map;
				}
			} else {
				int rand = IndoUtil.randInt(111111, 999999);
				log.info("GenericServiceImpl.addChildNew()   OTP generated. " + rand);
				map.put("TEMP_OTP", Integer.toString(rand));
				map.put("Status", "SUCCESS");
				return map;
			}
			if (add) {
				map = getUserProfileByMsisdn(parent);
				if (IndoUtil.isSuccess(map)) {
					List<Map<String, Object>> ch = (List<Map<String, Object>>) map.get("data");
					String id = ch.get(0).get("user_id").toString();
					map.clear();
					map = addMsisdn(IndoUtil.prefix62(child), id);
					map.put("user_type", ldap.get("user_type"));
					return map;
				}
			}
		} catch (Exception ce) {
			log.error("GenericServiceImpl.addChildNew() ce " + IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Staurn-110", "Saturn-101", 0);
		} finally {
			log.info("GenericServiceImpl.addChildNew() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> removeChildNew(String committer, String child) {// @Note:
																				// remove
																				// from
																				// existing
																				// system
																				// child
																				// tables.
		log.info("GenericServiceImpl.removeChildNew() - START");
		Map<String, Object> map = new HashMap<String, Object>();
		if (IndoUtil.prefix62(committer).equals(IndoUtil.prefix62(child))) {
			IndoUtil.populateErrorMap(map, "Staurn-111", "Cannot remove own number.", 0);
			log.info("GenericServiceImpl.removeChildNew() - END");
			return map;
		}
		try {
			return removeMsisdn(child);
		} catch (Exception ce) {
			log.error("GenericServiceImpl.removeChildNew() ce " + IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Staurn-111", "Saturn-101", 0);
		} finally {
			log.info("GenericServiceImpl.removeChildNew() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> removeChild(String committer, String child) {// @Note:
																			// remove
																			// from
																			// existing
																			// system
																			// child
																			// tables.
		log.info("GenericServiceImpl.removeChild() - START");
		Map<String, Object> map = new HashMap<String, Object>();
		if (IndoUtil.prefix62(committer).equals(IndoUtil.prefix62(child))) {
			IndoUtil.populateErrorMap(map, "Staurn-111", "Cannot delete own number.", 0);
			log.info("GenericServiceImpl.removeChild() - END");
			return map;
		}
		try {
			if (IndoUtil.isSuccess(isRelated(committer, child))) {
				Map<String, Object> wssMap = wssDBService.removeMsisdn(child);
				if (IndoUtil.isSuccess(wssMap)) {
					int ct = dbUtil.saveData("Delete from saturn_wss_msisdn where msisdn=?",
							new Object[] { IndoUtil.prefix62(child) });
					if (ct > 0) {
						ct = dbUtil.saveData(
								"insert into saturn_msisdn_history(msisdn,deleted_by,deleted_on) VALUES(?,?,SYSDATE)",
								new Object[] { IndoUtil.prefix62(child), IndoUtil.prefix62(committer) });
						if (ct > 0) {
							map.put("Status", "SUCCESS");
							return map;
						} else {
							IndoUtil.populateErrorMap(map, "Staurn-111", "Saturn-101", 0);
						}
					} else {
						IndoUtil.populateErrorMap(map, "Staurn-111", "Unable to remove child now. Try again!", 0);
					}
				} else {
					IndoUtil.populateErrorMap(map, "Staurn-111", "Unable to remove child now. Try again!", 0);
				}
			} else {
				IndoUtil.populateErrorMap(map, "Staurn-111", "Numbers given are not related.", 0);
			}
		} catch (Exception ce) {
			log.error("GenericServiceImpl.removeChild() ce " + IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Staurn-111", "Saturn-101", 0);
		} finally {
			log.info("GenericServiceImpl.removeChild() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> isRelated(String firstNum, String secNum) {
		log.info("GenericServiceImpl.isRelated() - START");
		Map<String, Object> map = new HashMap<String, Object>();
		String first = "";
		String sec = "";
		try {
			List<Map<String, Object>> data = dbUtil.getData(
					"select * from saturn_wss_msisdn where user_id=(select user_id from saturn_wss_msisdn where msisdn=?) order by created_on asc",
					new Object[] { IndoUtil.prefix62(firstNum) });
			if (data.size() > 0) {
				int ct = 0;
				for (Map<String, Object> user : data) {
					if (ct == 0) {
						if (user.get("MSISDN").toString().equals(IndoUtil.prefix62(firstNum))) {
							first = "parent";
						} else if (user.get("MSISDN").toString().equals(IndoUtil.prefix62(secNum))) {
							sec = "parent";
						}
						ct++;
						continue;
					}
					if (user.get("MSISDN").toString().equals(IndoUtil.prefix62(firstNum))) {
						first = "child";
					}
					if (user.get("MSISDN").toString().equals(IndoUtil.prefix62(secNum))) {
						sec = "child";
					}
					ct++;
				}
				if (!StringUtils.isEmpty(first) && !StringUtils.isEmpty(sec)) {
					map.put("type", first + "-" + sec);
					map.put("Status", "SUCCESS");
				} else {
					map.put("type", "not related");
					map.put("Status", "FAILURE");
				}
			} else {
				IndoUtil.populateErrorMap(map, "Staurn-111", "This number is not a child or not yet registered.", 0);
			}
		} catch (Exception ce) {
			log.error("GenericServiceImpl.isRelated() ce " + IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Staurn-111", "Saturn-101", 0);
		} finally {
			log.info("GenericServiceImpl.isRelated() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> getBannerImages(String serviceClass, String custType, String catType) {
		Map<String, Object> map = new HashMap<String, Object>();
		log.info("GenericServiceImpl.getBannerImages() - START");
		try {
			String qry = "select PACKAGE_TYPE,PACKAGE_CATEGORY,DESCRIPTION,PACKAGE_CATEGORY_ID,BANNER_NAME,CAT_SEQ from SATURN_PACKAGE_CATEGORY WHERE upper(PACKAGE_TYPE)=? order by CAT_SEQ ASC";
			List<Map<String, Object>> vList = null;
			if (!StringUtils.isEmpty(catType)) {
				if (null != custType && !custType.equalsIgnoreCase("PREPAID")) {
					custType = getServiceCodeCat(serviceClass);
				}
				if (custType.equals("Matrix Super Plan")) {
					vList = dbUtil.getData(qry, new Object[] { "BUYEXTRA_SUPER" });
				} else if (custType.equals("Matrix Max 25")) {
					vList = dbUtil.getData(qry, new Object[] { "BUYEXTRA_MATRIX" });
				} else {
					vList = dbUtil.getData(qry, new Object[] { "BUYEXTRA" });
				}
			} else {
				if (null != custType && !custType.equalsIgnoreCase("PREPAID")) {
					custType = getServiceCodeCat(serviceClass);
				}
				vList = dbUtil.getData(qry, new Object[] { custType.toUpperCase() });
			}
			if (vList != null) {
				log.info("Banner List size is " + vList.size());
			}
			vList = populateImages(vList);

			map.put("BannerList", vList);
			map.put("Status", "SUCCESS");

		} catch (Exception ce) {
			IndoUtil.populateErrorMap(map, "Saturn-1024", "Saturn-101", 0);
			log.error("Saturn-2051- GenericServiceImpl.getBannerImages() ce " + IndoUtil.getFullLog(ce));
		} finally {
			log.info("GenericServiceImpl.getBannerImages() - END");
		}
		return map;
	}

	private List<Map<String, Object>> populateImages(List<Map<String, Object>> vList) {
		Cache<String, Object> cache = GenericCache.getInstance().getEntityCache();
		StringBuilder builder = new StringBuilder();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> m : vList) {
			builder.setLength(0);
			Map<String, Object> map = new HashMap<String, Object>(m);
			if (null != map.get("BANNER_NAME") && map.containsKey("BANNER_NAME")
					&& !StringUtils.isEmpty(map.get("BANNER_NAME").toString())) {
				String cacheMap = (String) cache.get(map.get("BANNER_NAME").toString());
				if (null != cacheMap) {
					builder.append(cacheMap);
					log.info("GenericServiceImpl.populateImages() - fetched from cache");
				} else {
					List<Map<String, Object>> imageList = dbUtil.getData(
							"SELECT * from SATURN_IMAGES where IMAGE_NAME=?",
							new Object[] { m.get("BANNER_NAME").toString() });
					if (null != imageList && imageList.size() > 0) {
						builder.append(imageList.get(0).get("IMAGE").toString());
						if (null != builder && builder.length() > 0) {
							cache.put(map.get("BANNER_NAME").toString(), builder.toString(), 12, TimeUnit.HOURS);
						}
					}
				}
				map.put("BANNER_IMAGE", builder.toString());
			}
			if (null != map.get("BANNER_NAME_ID") && map.containsKey("BANNER_NAME_ID")
					&& !StringUtils.isEmpty(map.get("BANNER_NAME_ID").toString())) {
				String cacheMap = (String) cache.get(map.get("BANNER_NAME").toString());
				if (null != cacheMap) {
					builder.append(cacheMap);
					log.info("GenericServiceImpl.populateImages() - fetched from cache");
				} else {
					List<Map<String, Object>> imageList = dbUtil.getData(
							"SELECT * from SATURN_IMAGES where IMAGE_NAME=?",
							new Object[] { m.get("BANNER_NAME_ID").toString() });
					if (null != imageList && imageList.size() > 0) {
						builder.append(imageList.get(0).get("IMAGE").toString());
						if (null != builder && builder.length() > 0) {
							cache.put(map.get("BANNER_NAME_ID").toString(), builder.toString(), 12, TimeUnit.HOURS);
						}
					}
				}
				map.put("BANNER_IMAGE_ID", builder.toString());
			}
			list.add(map);
		}
		return list;
	}

	@Override
	public Map<String, Object> contactUs(JsonObject jObj) {
		log.info("GenericServiceImpl.contactUs() - START");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			int ct = dbUtil.saveData(
					"INSERT INTO SATURN_USER_INTEREST(msisdn,alternate_msisdn,about,message_title,message_data,commid",
					new Object[] { IndoUtil.getAsString(jObj.get("msisdn")),
							IndoUtil.getAsString(jObj.get("Alternate_msisdn")), IndoUtil.getAsString(jObj.get("about")),
							IndoUtil.getAsString(jObj.get("Message_title")),
							IndoUtil.getAsString(jObj.get("Message_data")), null });
			if (ct > 0) {
				map.put("Status", "SUCCESS");
			} else {
				IndoUtil.populateErrorMap(map, "Saturn-1025", "Saturn-101", 0);
			}
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(map, "Saturn-1025", "Saturn-101", 0);
			log.error("Saturn-2052- GenericServiceImpl.contactUs() " + IndoUtil.getFullLog(ce));
		} finally {
			log.info("GenericServiceImpl.contactUs() - END");
		}
		return map;
	}
	
	@Override
	public Map<String, Object> getOffers(String offerType,String lang) {
		log.info("GenericServiceImpl.getOffers() - START");
		Map<String,Object> map = new HashMap<String,Object>();
		Cache<String, Object>  cache = CacheUtil.getInstance().getEntityCache();
		Map<String,Object> cacheMap = (Map<String, Object>) cache.get("Offer_"+offerType+lang);
		if(null!=cacheMap){
			log.info("GenericServiceImpl.getOffers() - returned from cache");
			return cacheMap;
		}else{
			try{
				if(lang!=null && lang.equalsIgnoreCase("en")){
					List<Map<String, Object>> list = dbUtil.getData("SELECT OFFER_ID,PACKAGE_CODE,TARIFF,OFFER_NAME_EN,BENEFIT_EN,KEYWORD,PARAM,OFFER_LINK,OFFER_TYPE,CUST_TYPE,BANNER_IMAGE_EN  as BANNER_IMAGE,SEQ_VAL,ACTION  FROM SATURN_OFFERS WHERE OFFER_TYPE=?", new Object[]{offerType});
					map.put("Status", "SUCCESS");
					map.put("OfferList", list);
					if(null!=list && list.size()!=0){
						cache.put("Offer_"+offerType+lang,map, 2, TimeUnit.HOURS);
					}
				}else{
					List<Map<String, Object>> list = dbUtil.getData("SELECT OFFER_ID,PACKAGE_CODE,TARIFF,OFFER_NAME_ID,BENEFIT_ID,KEYWORD,PARAM,OFFER_LINK_ID as OFFER_LINK,OFFER_TYPE,CUST_TYPE,BANNER_IMAGE_ID as BANNER_IMAGE,SEQ_VAL,ACTION  FROM SATURN_OFFERS WHERE OFFER_TYPE=?", new Object[]{offerType});
					map.put("Status", "SUCCESS");
					map.put("OfferList", list);
					if(null!=list && list.size()!=0){
						cache.put("Offer_"+offerType+lang,map, 2, TimeUnit.HOURS);
					}
				}
				
			}catch(Exception ce){
				IndoUtil.populateErrorMap(map, "Saturn-1026", "Saturn-101",0);
				log.error("Saturn-2053- GenericServiceImpl.getOffers() "+IndoUtil.getFullLog(ce));
			}finally{
				log.info("GenericServiceImpl.getOffers() - END");
			}
		}		
		return map;
	}
	

	@Override
	public void serviceRequest(String xml) {

	}

	@Override
	public Map<String, Object> getMsisdnDetails(String msisdn) {
		log.info("GenericServiceImpl.getMsisdnDetails() - START");
		Map<String, Object> map = new HashMap<String, Object>();
		String msisdn62 = IndoUtil.prefix62(msisdn);
		String msisdn08 = "";
		if (msisdn.startsWith("62")) {
			msisdn08 = StringUtils.removeStart(msisdn, "62");
		}
		String msisdn0 = "";
		if (msisdn.startsWith("62")) {
			msisdn0 = StringUtils.removeStart(msisdn, "62");
			msisdn0 = "0" + msisdn0;
		}
		if (msisdn.startsWith("8")) {
			msisdn0 = "0" + msisdn0;
		}
		String msisdnN = "";
		if (msisdn.startsWith("62")) {
			msisdnN = StringUtils.removeStart(msisdn, "62");
		}
		if (msisdn.startsWith("08")) {
			msisdnN = StringUtils.removeStart(msisdn, "0");
		}
		log.info("GenericServiceImpl.getMsisdnDetails() msisdn62 " + msisdn62);
		log.info("GenericServiceImpl.getMsisdnDetails() msisdn0 " + msisdn0);
		log.info("GenericServiceImpl.getMsisdnDetails() msisdnN " + msisdnN);
		try {
			List<Map<String, Object>> data = dbUtil.getData(
					"select * from WSS_MSISDN where MSISDN=? or MSISDN=? or MSISDN=? or MSISDN=? or MSISDN=?",
					new Object[] { msisdn, msisdn0, msisdn62, msisdn08, msisdnN });
			if (null != data && data.size() > 0) {
				map.put("msisdn", data.get(0));
				map.put("Status", "SUCCESS");
			} else {
				IndoUtil.populateErrorMap(map, "WSS-003", "Not registered.", 0);
			}
		} catch (Exception ce) {
			log.error("GenericServiceImpl.getMsisdnByNum() ce " + IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "WSS-004", "Internal server error. Unable to get details", 0);
		} finally {
			log.info("GenericServiceImpl.getMsisdnDetails() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> getUserIDByMsisdn(String msisdn) {
		log.info("GenericServiceImpl.getUserProfileByMsisdn() - START");
		Map<String, Object> map = new HashMap<String, Object>();
		String msisdn62 = IndoUtil.prefix62(msisdn);
		String msisdn08 = "";
		if (msisdn.startsWith("62")) {
			msisdn08 = StringUtils.removeStart(msisdn, "62");
		}
		String msisdn0 = "";
		if (msisdn.startsWith("62")) {
			msisdn0 = StringUtils.removeStart(msisdn, "62");
			msisdn0 = "0" + msisdn0;
		}
		if (msisdn.startsWith("8")) {
			msisdn0 = "0" + msisdn0;
		}
		String msisdnN = "";
		if (msisdn.startsWith("08")) {
			msisdnN = StringUtils.removeStart(msisdn, "0");
		}
		try {
			String qry = "select a.USERNAME as \"user_id\", a.NAME as \"user_name\", a.NAME as \"full_name\", a.birth_place as \"birth_place\", "
					+ "a.id_number as \"id_number\", a.address  as \"address\", a.email as \"email\", a.birth_date as \"birth_date\", a.ENABLED as \"status\", "
					+ "a.PASSWORDKU FROM wss_user a ,WSS_MSISDN b where "
					+ "a.id=b.user_id  and b.user_id= (select user_id from WSS_MSISDN where msisdn=? or msisdn=? or msisdn=? or msisdn=? or msisdn=?) order by b.id ASC";
			List<Map<String, Object>> data = dbUtil.getData(qry,
					new Object[] { msisdn, msisdn0, msisdn62, msisdn08, msisdnN });
			log.info("Parent data " + data);
			if (null != data && data.size() > 0) {
				map.put("data", data);
				map.put("Status", "SUCCESS");
			} else {
				IndoUtil.populateErrorMap(map, "WSS-003", "Not registered.", 0);
				return map;
			}
		} catch (Exception ce) {
			log.error("GenericServiceImpl.getMsisdnByNum() ce " + IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "WSS-004", "Internal server error.", 0);
		} finally {
			log.info("GenericServiceImpl.getUserProfileByMsisdn() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> getUserProfileByMsisdn(String msisdn) {
		log.info("GenericServiceImpl.getUserProfileByMsisdn() - START");
		Map<String, Object> map = new HashMap<String, Object>();
		String msisdn62 = IndoUtil.prefix62(msisdn);
		String msisdn08 = "";
		if (msisdn.startsWith("62")) {
			msisdn08 = StringUtils.removeStart(msisdn, "62");
		}
		String msisdn0 = "";
		if (msisdn.startsWith("62")) {
			msisdn0 = StringUtils.removeStart(msisdn, "62");
			msisdn0 = "0" + msisdn0;
		}
		if (msisdn.startsWith("8")) {
			msisdn0 = "0" + msisdn0;
		}
		String msisdnN = "";
		if (msisdn.startsWith("08")) {
			msisdnN = StringUtils.removeStart(msisdn, "0");
		}
		try {
			String qry = "select a.id as \"id\",b.msisdn as \"msisdn\",a.USERNAME as \"user_id\", a.NAME as \"user_name\", a.NAME as \"full_name\", a.birth_place as \"birth_place\", "
					+ "a.id_number as \"id_number\", a.address  as \"address\", a.email as \"email\", a.birth_date as \"birth_date\", a.ENABLED as \"status\", "
					+ "a.PASSWORDKU FROM wss_user a ,WSS_MSISDN b where "
					+ "a.id=b.user_id  and b.user_id= (select user_id from WSS_MSISDN where msisdn=? or msisdn=? or msisdn=? or msisdn=? or msisdn=?) order by b.id ASC";
			List<Map<String, Object>> data = dbUtil.getData(qry,
					new Object[] { msisdn, msisdn0, msisdn62, msisdn08, msisdnN });
			if (null != data && data.size() > 0) {
				map.put("data", data);
				map.put("Status", "SUCCESS");
			} else {
				IndoUtil.populateErrorMap(map, "WSS-003", "Not registered.", 0);
				return map;
			}
		} catch (Exception ce) {
			log.error("GenericServiceImpl.getMsisdnByNum() ce " + IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "WSS-004", "Internal server error.", 0);
		} finally {
			log.info("GenericServiceImpl.getUserProfileByMsisdn() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> getUserProfile(String userName) {
		log.info("GenericServiceImpl.getUserProfile() - START");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String qry = "";
			List<String> obj = new ArrayList<String>();
			if (StringUtil.isNumeric(userName)) {
				qry = "select a.id as \"id\",b.msisdn as \"msisdn\",a.USERNAME as \"user_id\", a.NAME as \"user_name\", a.NAME as \"full_name\", a.birth_place as \"birth_place\", "
						+ "a.id_number as \"id_number\", a.address  as \"address\", a.email as \"email\", a.birth_date as \"birth_date\", a.ENABLED as \"status\", "
						+ "a.PASSWORDKU FROM wss_user a ,WSS_MSISDN b where "
						+ "a.id=b.user_id  and b.user_id= (select id from wss_user where USERNAME=? or ID=?) order by b.id ASC";
				obj.add(userName);
				obj.add(userName);
			} else {
				qry = "select a.id as \"id\",b.msisdn as \"msisdn\",a.USERNAME as \"user_id\", a.NAME as \"user_name\", a.NAME as \"full_name\", a.birth_place as \"birth_place\", "
						+ "a.id_number as \"id_number\", a.address  as \"address\", a.email as \"email\", a.birth_date as \"birth_date\", a.ENABLED as \"status\", "
						+ "a.PASSWORDKU FROM wss_user a ,WSS_MSISDN b where "
						+ "a.id=b.user_id  and b.user_id= (select id from wss_user where USERNAME=?) order by b.id ASC";
				obj.add(userName);
			}
			List<Map<String, Object>> data = dbUtil.getData(qry, obj.toArray());
			if (null != data && data.size() > 0) {
				map.put("Status", "SUCCESS");
				map.put("data", data);
			} else {
				map.clear();
				IndoUtil.populateErrorMap(map, "WSS-000", "No data found.", 0);
			}
		} catch (Exception ce) {
			map.clear();
			IndoUtil.populateErrorMap(map, "WSS-001", "No data found.", 0);
			log.error("GenericServiceImpl.getUserProfile() ce " + IndoUtil.getFullLog(ce));
		} finally {
			log.info("GenericServiceImpl.getUserProfile() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> getMsisdn(String id) {
		log.info("GenericServiceImpl.getMsisdn() - START");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Map<String, Object>> data = dbUtil.getData("select * from WSS_MSISDN where USER_ID=? order by id asc",
					new Object[] { id });
			if (null != data && data.size() > 0) {
				map.put("msisdns", data);
				map.put("Status", "SUCCESS");
				return map;
			}
		} catch (Exception ce) {
			log.error("GenericServiceImpl.getUserProfile() ce " + IndoUtil.getFullLog(ce));
		} finally {
			log.info("GenericServiceImpl.getMsisdn() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> addMsisdn(String msisdn, String id) {
		log.info("GenericServiceImpl.addMsisdn() - START");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Map<String, Object>> data = dbUtil.getData("SELECT * FROM wss_user WHERE USERNAME=?",
					new Object[] { id });
			if (null != data && data.size() > 0) {
				Map<String, Object> row = dbUtil.getRow(
						"SELECT * FROM wss_msisdn WHERE id = ( SELECT MAX(id) FROM wss_msisdn)", new Object[] {});
				int mid = Integer.parseInt(row.get("ID").toString());
				int ct = dbUtil.saveData(
						"INSERT into wss_msisdn(id,MSISDN,REDEMPTION_STATUS,USER_ID,VERSION,CREATED_ON,IS_AUTO_LOGIN,OS_ID) values(?,?,?,?,?,sysdate,?,?)",
						new Object[] { mid + 1, IndoUtil.prefix62(msisdn), "active", data.get(0).get("ID"), "2", "1",
								"1" });
				if (ct > 0) {
					map.put("id", id);
					map.put("Status", "SUCCESS");
				}
			} else {
				IndoUtil.populateErrorMap(map, "WSS-009", "User ID not found", 0);
			}
		} catch (Exception ce) {
			log.error("GenericServiceImpl.regUser() ce " + IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "WSS-005", "Saturn-101", 0);
		} finally {
			log.info("GenericServiceImpl.addMsisdn() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> removeMsisdn(String msisdn) {
		log.info("WSSDBServiceImpl.removeMsisdn() - START");
		Map<String, Object> map = new HashMap<String, Object>();
		String msisdn62 = IndoUtil.prefix62(msisdn);
		String msisdn08 = "";
		if (msisdn.startsWith("62")) {
			msisdn08 = StringUtils.removeStart(msisdn, "62");
		}
		String msisdn0 = "";
		if (msisdn.startsWith("62")) {
			msisdn0 = StringUtils.removeStart(msisdn, "62");
			msisdn0 = "0" + msisdn0;
		}
		if (msisdn.startsWith("8")) {
			msisdn0 = "0" + msisdn0;
		}
		String msisdnN = "";
		if (msisdn.startsWith("08")) {
			msisdnN = StringUtils.removeStart(msisdn, "0");
		}
		try {
			int ct = dbUtil.saveData(
					"delete from wss_msisdn WHERE MSISDN=? or MSISDN=? or MSISDN=? or MSISDN=? or MSISDN=?",
					new Object[] { msisdn, msisdn0, msisdn62, msisdn08, msisdnN });
			if (ct > 0) {
				map.put("Status", "SUCCESS");
			} else {
				IndoUtil.populateErrorMap(map, "WSS-005", "Saturn-101", 0);
			}
		} catch (Exception ce) {
			log.error("WSSDBServiceImpl.removeMsisdn() ce " + IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "WSS-006", "Saturn-101", 0);
		} finally {
			log.info("WSSDBServiceImpl.removeMsisdn() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> getLov(String type, String name) {
		log.info("GenericServiceImpl.getLov() type " + type);
		log.info("GenericServiceImpl.getLov() name " + name);
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			String qry = "";
			Object[] obj = null;
			if (type.equalsIgnoreCase("ZipCode")) {
				if (name.equals("")) {
					qry = "SELECT distinct(Zipcode) as \"Name\" from saturn_location_master";
					obj = new Object[] {};
				} else {
					qry = "SELECT distinct(Zipcode) as \"Name\" from saturn_location_master where city=? order by zipcode";
					obj = new Object[] { name };
				}
			} else if (type.equalsIgnoreCase("District")) {
				if (name.equals("")) {
					qry = "SELECT distinct(District) as \"Name\" from saturn_location_master";
					obj = new Object[] {};
				} else {
					qry = "SELECT distinct(District) as \"Name\" from saturn_location_master where city=? or province=? order by district";
					obj = new Object[] { name, name };
				}
			} else if (type.equalsIgnoreCase("Street")) {
				if (name.equals("")) {
					qry = "SELECT distinct(Street) as \"Name\" from saturn_location_master";
					obj = new Object[] {};
				} else {
					qry = "SELECT distinct(Street) as \"Name\" from saturn_location_master where city=? or district=? order by street";
					obj = new Object[] { name, name };
				}
			} else if (type.equalsIgnoreCase("City")) {
				if (name.equals("")) {
					qry = "SELECT distinct(City) as \"Name\" from saturn_location_master";
					obj = new Object[] {};
				} else {
					qry = "SELECT distinct(City) as \"Name\" from saturn_location_master where province=? order by City";
					obj = new Object[] { name };
				}
			} else if (type.equalsIgnoreCase("4g")) {
				qry = "SELECT * from saturn_4G_data order by lov asc";
				List<Map<String, Object>> data1 = dbUtil.getData(qry, obj);
				// log.info("GenericServiceImpl.getLov() data1 "+data1);
				data.put("Status", "SUCCESS");
				data.put("LovList", data1);
				return data;
			} else {
				qry = "SELECT distinct(Province) as \"Name\" from saturn_location_master order by province";
				obj = new Object[] {};
			}
			List<String> data1 = dbUtil.getSingleCol(qry, obj);
			// log.info("GenericServiceImpl.getLov() data1 "+data1);
			data.put("Status", "SUCCESS");
			data.put("LovList", data1);
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(data, "Indo-1021", "No Data Found.", 0);
			log.info("Indo-2072- Exception Occured getLov " + ce);
		}
		return data;
	}

	@Override
	public Map<String, Object> appLauncher(String type) {
		log.info("GenericServiceImpl.appLauncher() - START");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Map<String, Object>> data = dbUtil
					.getData("select * from SATURN_APPLAUNCHER where type=? order by seq asc", new Object[] { type });
			if (null != data && data.size() > 0) {
				map.put("data", data);
				map.put("Status", "SUCCESS");
				return map;
			}
		} catch (Exception ce) {
			log.error("GenericServiceImpl.appLauncher() ce " + IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Saturn-600", "Saturn-101", 0);
		} finally {
			log.info("GenericServiceImpl.appLauncher() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> sendMessage(String from_msisdn, String title, String msg, String type, String to_msisdn,
			String from_date) {
		log.info("GenericServiceImpl.sendMessage() - START");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			from_msisdn = IndoUtil.prefix62(from_msisdn);
			int ct = dbUtil.saveData("insert into SATURN_MESSAGES values(saturn_msg_seq.NEXTVAL,?,?,?,?,?,?,?)",
					new Object[] { from_msisdn, title, msg, type, "N", to_msisdn, from_date });
			if (ct > 0) {
				map.put("Status", "SUCCESS");
				return map;
			}
		} catch (Exception ce) {
			log.error("GenericServiceImpl.sendMessage() ce " + IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Saturn-600", "Saturn-101", 0);
		} finally {
			log.info("GenericServiceImpl.sendMessage() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> getMessages(String msisdn, String type) {
		log.info("GenericServiceImpl.getMessages() - START");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Map<String, Object>> data = null;
			List<Map<String, Object>> data1 = null;
			msisdn = IndoUtil.prefix62(msisdn);
			if (null == type || type.equals("") || type.equalsIgnoreCase("all")) {
				data = dbUtil.getData("select * from SATURN_MESSAGES where to_msisdn=? and read='Y' order by id asc",
						new Object[] { msisdn });
				data1 = dbUtil.getData("select * from SATURN_MESSAGES where to_msisdn=? and read='N' order by id asc",
						new Object[] { msisdn });
			} else {
				data = dbUtil.getData(
						"select * from SATURN_MESSAGES where to_msisdn=? and type=? and read='Y' order by id asc",
						new Object[] { msisdn, type });
				data1 = dbUtil.getData(
						"select * from SATURN_MESSAGES where to_msisdn=? and type=? and read='N' order by id asc",
						new Object[] { msisdn, type });
			}
			int total = 0, read = 0, unread = 0;
			if (null != data && data.size() > 0) {
				total = data.size();
				read = data.size();
			}
			if (null != data1 && data1.size() > 0) {
				total = total + data1.size();
				unread = data1.size();
			}
			map.put("Status", "SUCCESS");
			map.put("totalCount", total);
			map.put("readCount", read);
			map.put("unreadCount", unread);
			map.put("read", data);
			map.put("unread", data1);
			return map;
		} catch (Exception ce) {
			log.error("GenericServiceImpl.getMessages() ce " + IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Saturn-600", "Saturn-101", 0);
		} finally {
			log.info("GenericServiceImpl.getMessages() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> changeReadStatus(String id, String status) {
		log.info("GenericServiceImpl.changeReadStatus() - START");
		Map<String, Object> map = new HashMap<String, Object>();
		if (null == status || status.equals("")) {
			status = "N";
		}
		try {
			int ct = dbUtil.saveData("update SATURN_MESSAGES set read=? where id=?", new Object[] { status, id });
			if (ct > 0) {
				map.put("Status", "SUCCESS");
				return map;
			} else {
				IndoUtil.populateErrorMap(map, "Saturn-600", "Saturn-101", 0);
			}
		} catch (Exception ce) {
			log.error("GenericServiceImpl.changeReadStatus() ce " + IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Saturn-600", "Saturn-101", 0);
		} finally {
			log.info("GenericServiceImpl.changeReadStatus() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> deleteMessage(String id) {
		log.info("GenericServiceImpl.deleteMessage() - START");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			int ct = dbUtil.saveData("delete from SATURN_MESSAGES where id=?", new Object[] { id });
			if (ct > 0) {
				map.put("Status", "SUCCESS");
				return map;
			} else {
				IndoUtil.populateErrorMap(map, "Saturn-600", "Saturn-101", 0);
			}
		} catch (Exception ce) {
			log.error("GenericServiceImpl.deleteMessage() ce " + IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Saturn-600", "Saturn-101", 0);
		} finally {
			log.info("GenericServiceImpl.deleteMessage() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> countClick(String msisdn, int countClick1) {

		log.info("GenericServiceImpl.countClick() countClick : " + countClick1);
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			String msisdn1 = IndoUtil.prefix62(msisdn);
			int countClick = countClick1;
			List<Map<String, Object>> data1 = dbUtil.getData("SELECT * from saturn_click_count  WHERE msisdn=?",
					new Object[] { msisdn1 });
			log.info("GenericServiceImpl.countClick() data1 : " + data1);

			if (data1.size() > 0) {

				log.info("data not found update operation apply");
				Map<String, Object> map = data1.get(0);
				log.info("map : " + map);
				int count = Integer.parseInt(map.get("CLICK").toString());
				countClick = countClick + count;
				log.info("count click : " + countClick + "  and count : " + count);
				int ct = dbUtil.saveData("UPDATE saturn_click_count SET CLICK=? WHERE MSISDN=?",
						new Object[] { countClick, msisdn1 });
				if (ct > 0) {
					data.put("Status", "SUCCESS");
					data.put("clickCount", countClick);
					data.put("OfferUsedFlag", "N");
					if (countClick >= 1000) {
						data.put("OfferUsedFlag", "Y");
					}
				} else {
					data.put("Status", "FAILTURE");
				}
			} else {
				log.info("data not found insert operation 2 apply");
				// saturn_click_count(msisdn VARCHAR(100), CLICK Number(20))
				int ct = dbUtil.saveData("INSERT into saturn_click_count values(?,?)",
						new Object[] { msisdn1, countClick });
				if (ct > 0) {
					data.put("Status", "SUCCESS");
					data.put("clickCount", countClick);

				} else {
					data.put("Status", "FAILTURE");
				}

			}
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(data, "Indo-1021", "No Data Found.", 0);
			log.info("Indo-2072- Exception Occured getLov " + ce);
		}
		return data;
	}

	@Override
	public Map<String, Object> deletemultipleMessage(List<Object[]> listObj) {

		Map<String, Object> map = new HashMap<String, Object>();
		try {
			log.info("List Object : -" + listObj);
			int[] ct = dbUtil.insertBatch("delete from SATURN_MESSAGES where id=?", listObj);
			if (ct.length > 0) {
				map.put("Status", "SUCCESS");
				return map;
			} else {
				IndoUtil.populateErrorMap(map, "Saturn-600", "Saturn-101", 0);
			}
		} catch (Exception ce) {
			log.error("GenericServiceImpl.deleteMessage() ce " + IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Saturn-600", "Saturn-101", 0);
		} finally {
			log.info("GenericServiceImpl.deleteMessage() - END");
		}
		return map;
	}

	@Override
	public Map<String, String> regDompetku(DompetkuVO dompetkuVO) {
		String msisdn = dompetkuVO.getMsisdn();
		if (msisdn.startsWith("628")) {
			msisdn = msisdn.replaceFirst("628", "08");
		}
		Map<String, String> data = new HashMap<String, String>();
		try {
			TripleDES td = new TripleDES();
			DateFormat timeFormat = new SimpleDateFormat("HHmmss");
			String dte = timeFormat.format(new Date());
			// StringBuilder domPin=new StringBuilder("1nd054t2ois");
			StringBuilder domPin = new StringBuilder(prop.getProperty("REG_DOMPETKU_PIN"));
			// String initiator="4gmobileagent";
			String initiator = prop.getProperty("REG_DOMPETKU_INITIATOR");
			String sigA = dte + domPin;
			// log.info("SigA "+ sigA);
			String sigB = domPin.reverse() + "|" + initiator;
			// log.info("SigB "+ sigB);
			String sigC = sigA + "|" + sigB;
			// log.info("SigC "+ sigC);
			// String myEncryptionKey = "4yL8GJqTH5EiX0PPC0eT1lRZ";
			String myEncryptionKey = prop.getProperty("REG_DOMPETKU_KEY");
			String encrypted = td.encrypt(sigC, myEncryptionKey);
			// String httpsURL = "https://mapi.dompetku.com/webapi/register";
			String httpsURL = prop.getProperty("REGISTER_DOMPETKU");
			log.debug("domp URL is " + httpsURL);
			// String query = "userid=4gmobileagent";
			String query = "userid=" + prop.getProperty("REG_DOMPETKU_USERID");
			query += "&signature=" + encrypted;
			query += "&firstname=" + dompetkuVO.getFirstname();
			query += "&lastname=" + dompetkuVO.getLastname();
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("userid", prop.getProperty("REG_DOMPETKU_USERID")));
			urlParameters.add(new BasicNameValuePair("signature", encrypted));
			urlParameters.add(new BasicNameValuePair("firstname", dompetkuVO.getFirstname()));
			urlParameters.add(new BasicNameValuePair("lastname", dompetkuVO.getLastname()));

			if (dompetkuVO.getGender() != null) {
				if (dompetkuVO.getGender().equalsIgnoreCase("M")) {
					query += "&gender=1";
					urlParameters.add(new BasicNameValuePair("gender", "1"));
				} else if (dompetkuVO.getGender().equalsIgnoreCase("F")) {
					query += "&gender=2";
					urlParameters.add(new BasicNameValuePair("gender", "2"));
				} else {
					query += "&gender=" + dompetkuVO.getGender();
					urlParameters.add(new BasicNameValuePair("gender", dompetkuVO.getGender()));
				}
			}
			if (dompetkuVO.getMsisdn() != null) {
				urlParameters.add(new BasicNameValuePair("msisdn", msisdn));
			}
			if (dompetkuVO.getIdtype() != null) {
				urlParameters.add(new BasicNameValuePair("idtype", dompetkuVO.getIdtype()));
			}
			if (dompetkuVO.getIdnumber() != null) {
				urlParameters.add(new BasicNameValuePair("idnumber", dompetkuVO.getIdnumber()));
			}
			if (dompetkuVO.getAddress() != null) {
				urlParameters.add(new BasicNameValuePair("address", dompetkuVO.getAddress()));
			}

			if (dompetkuVO.getProfilepic() != null) {
				urlParameters.add(new BasicNameValuePair("profilepic", dompetkuVO.getProfilepic()));
			}
			if (dompetkuVO.getIdphoto() != null) {
				urlParameters.add(new BasicNameValuePair("idphoto", dompetkuVO.getIdphoto()));
			}
			if (dompetkuVO.getDob() != null) {
				urlParameters.add(new BasicNameValuePair("dob", dompetkuVO.getDob()));
				// query += "&dob=09081986";
			}
			if (dompetkuVO.getMothername() != null) {
				urlParameters.add(new BasicNameValuePair("mothername", dompetkuVO.getMothername()));
			}
			if (dompetkuVO.getAgentid() != null) {
				urlParameters.add(new BasicNameValuePair("agentid", dompetkuVO.getAgentid()));
			}
			if (dompetkuVO.getLocationid() != null) {
				urlParameters.add(new BasicNameValuePair("locationid", dompetkuVO.getLocationid()));
			}
			log.info("Register Dompetku Input params - " + urlParameters);
			HttpEntity entity = null;
			CloseableHttpClient client = null;
			HttpPost request = null;
			try {
				client = httpConn.getHttpClient();
				request = new HttpPost(httpsURL);
				request.setEntity(new UrlEncodedFormEntity(urlParameters));
				HttpResponse response = client.execute(request);
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					entity = response.getEntity();
					String content = EntityUtils.toString(entity);
					JSONObject jsonObj = new JSONObject(content);
					log.info("");
					log.info("Result is " + content);
					log.info("Resp Code:" + statusCode);
					if (Integer.valueOf(jsonObj.get("status").toString()) == 0) {
						data.put("Status", "SUCCESS");
						data.put("TransactionId", jsonObj.get("trxid").toString());
						data.put("Msg", jsonObj.get("msg").toString());
					} else {
						data.put("Status", "FAILURE");
						data.put("ErrorCode", "Indo-1016");
						data.put("ErrorDescription", jsonObj.get("msg").toString());
					}
					try {
						ActivityVO actVo = new ActivityVO(msisdn, jsonObj.get("trxid").toString(), "RegisterDompetku",
								dompetkuVO.getAgentid(), "", "");
						actVo.setDescription(jsonObj.get("msg").toString());
						actVo.setOrderStatus(jsonObj.get("status").toString());
						Map<String, String> activityDataLog = logActivity(actVo);
					} catch (Exception e) {
						log.error("Indo-2064- Error while inserting activity while registering dompetku " + e);
					}
					return data;
				} else {
					data.put("Status", "FAILURE");
					data.put("ErrorCode", "Indo-1015");
					data.put("ErrorDescription", "No data Found.");
					entity = response.getEntity();
					String content = EntityUtils.toString(entity);
					log.info("error dompetku check- " + content);
				}
			} catch (IOException e) {
				IndoUtil.populateErrorMap(data, "Indo-1023", "Registration Failed.");
				log.info("Indo-2065- Exception IOException " + IndoUtil.getFullLog(e));
			} catch (Exception ce) {
				IndoUtil.populateErrorMap(data, "Indo-1023", "Registration Failed.");
				log.info("Indo-2066- Exception Occured while registering for Dompetku- " + IndoUtil.getFullLog(ce));
			} finally {
				log.info("***********Closing Streams********");
				try {
					EntityUtils.consumeQuietly(entity);
					if (null != request) {
						request.releaseConnection();
					}
					if (null != client) {
						client.close();
					}
				} catch (IOException e) {
					log.info("Indo-2062- Exception Occured " + e);
				}
			}
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(data, "Indo-1023", "Registration Failed.");
			log.info("Indo-2067- Exception Occured while registering for Dompetku " + IndoUtil.getFullLog(ce));
		}
		return data;
	}

	@Override
	public Map<String, Object> changeAllReadStatus(List<Object[]> listObj) {
		log.info("GenericServiceImpl.changeReadStatus() - START");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			int[] ct = dbUtil.insertBatch("update SATURN_MESSAGES set read=? where id=?", listObj);
			if (ct.length > 0) {
				map.put("Status", "SUCCESS");
				return map;
			} else {
				IndoUtil.populateErrorMap(map, "Saturn-600", "Saturn-101", 0);
			}
		} catch (Exception ce) {
			log.error("GenericServiceImpl.changeReadStatus() ce " + IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Saturn-600", "Saturn-101", 0);
		} finally {
			log.info("GenericServiceImpl.changeReadStatus() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> getDenoms(String msisdn, String type) {
		log.info("GenericServiceImpl.getDenoms() - START");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Map<String, Object>> data = null;
			msisdn = IndoUtil.prefix62(msisdn);

			data = dbUtil.getData("select * from SATURN_DENOMINATIONS order by id asc", new Object[] {});
			map.put("Status", "SUCCESS");

			map.put("VoucherList", data);

			return map;
		} catch (Exception ce) {
			log.error("GenericServiceImpl.getMessages() ce " + IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Saturn-600", "Saturn-101", 0);
		} finally {
			log.info("GenericServiceImpl.getMessages() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> retrieveActivity(String msisdn) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Map<String, Object>> data = dbUtil.getData(
					"select MSISDN,COMMID,ACTIVITY_TYPE,USERID,to_char(CREATED_ON,'dd-MM-yyyy hh:mm:ss') CREATED_ON, USERTYPE, ORDER_STATUS,DECODE (order_status,'0', 'SUCCESS','FAILURE') Status,DESCRIPTION,TEXT1,TEXT2, TEXT3,TEXT4,TEXT5,SMS_TEXT,PLANNAME_EN,PLANNAME_ID,PLANDESC_EN, PLANDESC_ID, TARIFF from SATURN_ACTIVITY a where a.created_on > sysdate-30  and a.ACTIVITY_TYPE =? and a.msisdn=? order by a.CREATED_ON desc",
					new Object[] { "ManagePackage",msisdn});
			if (null != data && data.size() > 0) {
				map.put("Status", "SUCCESS");
				map.put("Activity", data);
				return map;
			} else {
				IndoUtil.populateErrorMap(map, "Saturn-600", "Saturn-2001", 0);
			}
		} catch (Exception ce) {
			log.error("GenericServiceImpl.retrieveActivity() ce " + IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Saturn-600", "Saturn-101", 0);
		} finally {
			log.info("GenericServiceImpl.retrieveActivity() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> retrievePackInfo(String keyword) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Map<String, Object>> data = dbUtil.getData(
					"select * from SATURN_PACKAGE_INFO where  (PACKAGE_CODE=? or KEYWORD=?) and sysdate between start_date and end_date",
					new Object[] { keyword, keyword });
			if (null != data && data.size() > 0) {
				map.put("Status", "SUCCESS");
				map.put("Activity", data);
				return map;
			} else {
				IndoUtil.populateErrorMap(map, "Saturn-600", "Saturn-101", 0);
			}
		} catch (Exception ce) {
			log.error("GenericServiceImpl.retrieveActivity() ce " + IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Saturn-600", "Saturn-101", 0);
		} finally {
			log.info("GenericServiceImpl.retrieveActivity() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> regDevice(String msisdn, String deviceId, String osType, String model, String make) {
		Map<String, Object> map = new HashMap<String, Object>();
		msisdn = IndoUtil.prefix62(msisdn);
		try {
			if (!StringUtils.isEmpty(deviceId)) {
				List<Map<String, Object>> data = dbUtil.getData("select * from SATURN_DEVICE_DATA where deviceId=?",
						new Object[] { deviceId });
				if (null != data && data.size() > 0) {
					map.put("Status", "SUCCESS");
					return map;
				} else {
					dbUtil.saveData("delete from SATURN_DEVICE_DATA where msisdn=?", new Object[] { msisdn });
					int ct = dbUtil.saveData(
							"insert into SATURN_DEVICE_DATA(MSISDN,DEVICEID,OSTYPE,MODEL,MAKE) values(?,?,?,?,?)",
							new Object[] { msisdn, deviceId, osType, model, make });
					if (ct > 0) {
						map.put("Status", "SUCCESS");
						return map;
					}
				}
			} else {
				List<Map<String, Object>> data = dbUtil.getData("select * from SATURN_DEVICE_DATA where msisdn=?",
						new Object[] { msisdn });
				if (null != data && data.size() > 0) {
					map.put("Status", "SUCCESS");
					return map;
				} else {
					IndoUtil.populateErrorMap(map, "Saturn-600", "Saturn-101", 0);
				}
			}
		} catch (Exception ce) {
			log.error("GenericServiceImpl.regDevice() ce " + IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Saturn-600", "Saturn-101", 0);
		} finally {
			log.info("GenericServiceImpl.regDevice() - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> sendNotification(String msisdn, String msg) {
		HttpEntity entity = null;
		CloseableHttpClient client = null;
		HttpPost request = null;
		Map<String, Object> map = new HashMap<String, Object>();
		msisdn = IndoUtil.prefix62(msisdn);
		try {
			String deviceId = "";
			List<Map<String, Object>> data = dbUtil.getData("select * from SATURN_DEVICE_DATA where msisdn=?",
					new Object[] { msisdn });
			if (null != data && data.size() > 0) {
				if (null != data.get(0).get("DEVICEID")) {
					deviceId = data.get(0).get("DEVICEID").toString();
				} else {
					IndoUtil.populateErrorMap(map, "Saturn-9000", "Device id not found.", 0);
					return map;
				}
			}
			client = httpConn.getHttpClient();
			StringEntity input = new StringEntity("{\"registration_ids\":[\"" + deviceId
					+ "\"],\"data\":{\"message\":\"" + msg + "\",\"title\":\"App Launcher\"}}");
			input.setContentType("application/json");
			request = new HttpPost("https://android.googleapis.com/gcm/send");
			request.setEntity(input);
			request.addHeader("Authorization", "AIzaSyCJmm-Fpvn3heLgiX6zIu68Px9uxPqVDVM");
			HttpResponse response = client.execute(request);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				entity = response.getEntity();
				String content = EntityUtils.toString(entity);
				map.put("Status", "SUCCESS");
				log.info("GenericServiceImpl.pushNotify() success " + content);
			} else {
				entity = response.getEntity();
				map.put("Status", "FAILURE");
				String content = EntityUtils.toString(entity);
				log.info("GenericServiceImpl.pushNotify() failed " + content);
			}
			return map;
		} catch (Exception as) {
			log.error("Launcher-9004- Exception Occured " + IndoUtil.getFullLog(as));
		} finally {
			log.info("***********Closing Streams********");
			try {
				EntityUtils.consumeQuietly(entity);
				if (null != request) {
					request.releaseConnection();
				}
				if (null != client) {
					client.close();
				}
			} catch (IOException e) {
				log.info("Launcher-2062- Exception Occured " + e);
			}
		}
		return null;
	}

	@Override
	public Map<String, Object> quickSurvey() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {
			List<Map<String, Object>> data = dbUtil.getData(
					"select * from saturn_survey_ques a,saturn_survey_ans b where a.SEQ=b.QUES_SEQ order by b.ques_seq,b.seq asc",
					new Object[] {});
			if (null != data && data.size() > 0) {
				map.put("Status", "SUCCESS");
				for (Map<String, Object> m : data) {
					if (null != m.get("QUES")) {
						List<String> answ = null;
						answ = map.get(m.get("QUES")) == null ? new ArrayList<String>()
								: (List<String>) map.get(m.get("QUES"));
						answ.add(m.get("ANS").toString());
						map.put(m.get("QUES").toString(), answ);
					}
				}
				return map;
			}
		} catch (Exception ce) {
			log.error("GenericServiceImpl.regDevice() ce " + IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Saturn-600", "Saturn-101", 0);
		} finally {
			log.info("GenericServiceImpl.regDevice() - END");
		}
		return map;
	}

	private static String dompetkuPaySignature() throws NoSuchAlgorithmException, NoSuchPaddingException {
		TripleDES td = new TripleDES();
		DateFormat timeFormat = new SimpleDateFormat("HHmmss");
		String dte = timeFormat.format(new Date());
		StringBuilder domPin = new StringBuilder("123456");
		String initiator = "outlet_1";
		String myEncryptionKey = "Th1s_0nLy_F0uR_t3sT1nG__";
		String sigA = dte + domPin;
		String sigB = domPin.reverse() + "|" + initiator;
		String sigC = sigA + "|" + sigB;
		String encrypted = td.encrypt(sigC, myEncryptionKey);
		return encrypted;
	}

	private static String dompetkuPaySignature(String pin, String initiator, String myEncryptionKey)
			throws NoSuchAlgorithmException, NoSuchPaddingException {
		TripleDES td = new TripleDES();
		DateFormat timeFormat = new SimpleDateFormat("HHmmss");
		String dte = timeFormat.format(new Date());
		StringBuilder domPin = new StringBuilder(pin);
		String sigA = dte + domPin;
		String sigB = domPin.reverse() + "|" + initiator;
		String sigC = sigA + "|" + sigB;
		String encrypted = td.encrypt(sigC, myEncryptionKey);
		return encrypted;
	}

	@Override
	public Map<String, String> billPayConfirm(String msisdn, String transid, String amount, String operator) {
		log.info("GenericServiceImpl.checkDompetkuReg() - START");
		if (msisdn.startsWith("628")) {
			msisdn = msisdn.replaceFirst("628", "08");
		}
		Map<String, String> data = new HashMap<String, String>();
		HttpEntity entity = null;
		CloseableHttpClient client = null;
		HttpPost request = null;
		try {
			String httpsURL = "http://114.4.68.19:8181/mfsmw/webapi/billpay";// prod
																				// url
			log.info("URL " + httpsURL);
			String encrypted = dompetkuPaySignature();
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			// urlParameters.add(new BasicNameValuePair("userid",
			// prop.getProperty("REG_DOMPETKU_USERID")));
			urlParameters.add(new BasicNameValuePair("userid", "web_api_test"));
			urlParameters.add(new BasicNameValuePair("signature", encrypted));
			urlParameters.add(new BasicNameValuePair("transid", transid));
			urlParameters.add(new BasicNameValuePair("amount", amount));
			urlParameters.add(new BasicNameValuePair("to", msisdn));
			urlParameters.add(new BasicNameValuePair("target", operator));
			client = httpConn.getHttpClient();
			request = new HttpPost(httpsURL);
			request.setEntity(new UrlEncodedFormEntity(urlParameters));
			HttpResponse response = client.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				entity = response.getEntity();
				String content = EntityUtils.toString(entity);
				log.info("");
				log.info("Result is " + content);
				log.info("Resp Code:" + statusCode);
				JSONObject jsonObj = new JSONObject(content);
				if (Integer.valueOf(jsonObj.get("status").toString()) == 0) {
					data.put("Status", "SUCCESS");
					data.put("TransactionId", jsonObj.get("trxid").toString());

				} else {
					data.put("Status", "FAILURE");
					data.put("dompetkuStatus", jsonObj.get("status").toString());
					data.put("ErrorCode", "Saturn-1016");
					data.put("ErrorDescription", jsonObj.get("msg").toString());
					data.put("DompetkuStatus", "NO");
				}
				return data;
			} else {
				data.put("Status", "Failure");
				data.put("ErrorCode", "Saturn-1016");
				data.put("ErrorDescription", "No Data Found.");
				entity = response.getEntity();
				String content = EntityUtils.toString(entity);
				log.info("error dompetku check- " + content);
			}
		} catch (IOException e) {
			IndoUtil.populateErrorMap(data, "Saturn-1023", "Saturn-101");
			log.error("Saturn-2061- GenericServiceImpl.checkDompetkuReg()- e" + IndoUtil.getFullLog(e));
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(data, "Saturn-1023", "Saturn-101");
			log.error("Saturn-2062- GenericServiceImpl.checkDompetkuReg()- ce" + IndoUtil.getFullLog(ce));
		} finally {
			log.info("***********Closing Streams********");
			try {
				EntityUtils.consumeQuietly(entity);
				if (null != request) {
					request.releaseConnection();
				}
				if (null != client) {
					client.close();
				}
			} catch (IOException e) {
				log.error("Saturn-2062- GenericServiceImpl.checkDompetkuReg() e " + e);
			}
		}
		return data;
	}

	@Override
	public Map<String, String> dompetkuRecharge(String msisdn, String amount, String paymentId, String operator) {
		log.info("GenericServiceImpl.checkDompetkuReg() - START");
		if (msisdn.startsWith("628")) {
			msisdn = msisdn.replaceFirst("628", "08");
		}
		HttpEntity entity = null;
		CloseableHttpClient client = null;
		HttpPost request = null;
		Map<String, String> data = new HashMap<String, String>();
		try {
			String httpsURL = "http://114.4.68.19:8181/mfsmw/webapi/airtime_inquiry";// prod
			log.info("URL " + httpsURL);
			String encrypted = dompetkuPaySignature();
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			// urlParameters.add(new BasicNameValuePair("userid",
			// prop.getProperty("REG_DOMPETKU_USERID")));
			urlParameters.add(new BasicNameValuePair("userid", "web_api_test"));
			urlParameters.add(new BasicNameValuePair("signature", encrypted));
			urlParameters.add(new BasicNameValuePair("amount", amount));
			urlParameters.add(new BasicNameValuePair("to", msisdn));
			urlParameters.add(new BasicNameValuePair("extRef", paymentId));
			client = httpConn.getHttpClient();
			request = new HttpPost(httpsURL);
			request.setEntity(new UrlEncodedFormEntity(urlParameters));
			HttpResponse response = client.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				entity = response.getEntity();
				String content = EntityUtils.toString(entity);
				log.info("");
				log.info("Result is " + content);
				log.info("Resp Code:" + statusCode);
				JSONObject jsonObj = new JSONObject(content);
				if (Integer.valueOf(jsonObj.get("status").toString()) == 0) {
					data.put("Status", "SUCCESS");
					data.put("TransactionId", jsonObj.get("trxid").toString());
					Map<String, String> dataCommit = new HashMap<String, String>();
					dataCommit = airTimeCommit(msisdn, jsonObj.get("trxid").toString(), paymentId, operator);
					if (IndoUtil.isSuccess(data)) {
						data.put("Status", "SUCCESS");
						data.put("TransactionId", jsonObj.get("trxid").toString());
					} else {
						data.put("Status", "FAILURE");
						data.put("DompetkuStatus", dataCommit.get("dompetkuStatus"));
						data.put("ErrorCode", "Saturn-1016");
						data.put("ErrorDescription", jsonObj.get("msg").toString());
					}
				} else {
					data.put("Status", "FAILURE");
					data.put("ErrorCode", "Saturn-1016");
					data.put("ErrorDescription", jsonObj.get("msg").toString());
				}
				return data;
			} else {
				data.put("Status", "Failure");
				data.put("ErrorCode", "Saturn-1016");
				data.put("ErrorDescription", "No Data Found.");
				entity = response.getEntity();
				String content = EntityUtils.toString(entity);
				log.info("error dompetku check- " + content);
			}
		} catch (IOException e) {
			IndoUtil.populateErrorMap(data, "Saturn-1023", "Saturn-101");
			log.error("Saturn-2061- GenericServiceImpl.checkDompetkuReg()- e" + IndoUtil.getFullLog(e));
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(data, "Saturn-1023", "Saturn-101");
			log.error("Saturn-2062- GenericServiceImpl.checkDompetkuReg()- ce" + IndoUtil.getFullLog(ce));
		} finally {
			log.info("***********Closing Streams********");
			try {
				EntityUtils.consumeQuietly(entity);
				if (null != request) {
					request.releaseConnection();
				}
				if (null != client) {
					client.close();
				}
			} catch (IOException e) {
				log.error("Saturn-2062- GenericServiceImpl.checkDompetkuReg() e " + e);
			}
		}
		return data;
	}

	@Override
	public Map<String, String> dompetkuPostpay(String msisdn, String amount, String paymentId, String operator) {
		log.info("GenericServiceImpl.checkDompetkuReg() - START");
		if (msisdn.startsWith("628")) {
			msisdn = msisdn.replaceFirst("628", "08");
		}
		Map<String, String> data = new HashMap<String, String>();
		HttpEntity entity = null;
		CloseableHttpClient client = null;
		HttpPost request = null;
		try {
			String httpsURL = "http://114.4.68.19:8181/mfsmw/webapi/query_billpay";// prod
			log.info("URL " + httpsURL);
			String encrypted = dompetkuPaySignature();
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			// urlParameters.add(new BasicNameValuePair("userid",
			// prop.getProperty("REG_DOMPETKU_USERID")));
			urlParameters.add(new BasicNameValuePair("userid", "web_api_test"));
			urlParameters.add(new BasicNameValuePair("signature", encrypted));
			urlParameters.add(new BasicNameValuePair("amount", amount));
			urlParameters.add(new BasicNameValuePair("to", msisdn));
			urlParameters.add(new BasicNameValuePair("target", operator));
			client = httpConn.getHttpClient();
			request = new HttpPost(httpsURL);
			request.setEntity(new UrlEncodedFormEntity(urlParameters));
			HttpResponse response = client.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				entity = response.getEntity();
				String content = EntityUtils.toString(entity);
				log.info("");
				log.info("Result is " + content);
				log.info("Resp Code:" + statusCode);
				JSONObject jsonObj = new JSONObject(content);
				if (Integer.valueOf(jsonObj.get("status").toString()) == 0) {
					data.put("Status", "SUCCESS");
					data.put("TransactionId", jsonObj.get("trxid").toString());
					Map<String, String> dataCommit = new HashMap<String, String>();
					dataCommit = billPayConfirm(msisdn, jsonObj.get("trxid").toString(), amount, operator);
					if (IndoUtil.isSuccess(data)) {
						data.put("Status", "SUCCESS");
						data.put("TransactionId", jsonObj.get("trxid").toString());
					} else {
						data.put("Status", "FAILURE");
						data.put("DompetkuStatus", dataCommit.get("dompetkuStatus"));
						data.put("ErrorCode", "Saturn-1016");
						data.put("ErrorDescription", jsonObj.get("msg").toString());
					}

				} else {
					data.put("Status", "FAILURE");
					data.put("ErrorCode", "Saturn-1016");
					data.put("ErrorDescription", jsonObj.get("msg").toString());
				}
				return data;
			} else {
				data.put("Status", "Failure");
				data.put("ErrorCode", "Saturn-1016");
				data.put("ErrorDescription", "No Data Found.");
				entity = response.getEntity();
				String content = EntityUtils.toString(entity);
				log.info("error dompetku check- " + content);
			}
		} catch (IOException e) {
			IndoUtil.populateErrorMap(data, "Saturn-1023", "Saturn-101");
			log.error("Saturn-2061- GenericServiceImpl.checkDompetkuReg()- e" + IndoUtil.getFullLog(e));
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(data, "Saturn-1023", "Saturn-101");
			log.error("Saturn-2062- GenericServiceImpl.checkDompetkuReg()- ce" + IndoUtil.getFullLog(ce));
		} finally {
			log.info("***********Closing Streams********");
			try {
				EntityUtils.consumeQuietly(entity);
				if (null != request) {
					request.releaseConnection();
				}
				if (null != client) {
					client.close();
				}
			} catch (IOException e) {
				log.error("Saturn-2062- GenericServiceImpl.checkDompetkuReg() e " + e);
			}
		}
		return data;
	}

	@Override
	public Map<String, String> airTimeCommit(String msisdn, String transid, String paymentId, String operator) {
		log.info("GenericServiceImpl.checkDompetkuReg() - START");
		if (msisdn.startsWith("628")) {
			msisdn = msisdn.replaceFirst("628", "08");
		}
		Map<String, String> data = new HashMap<String, String>();
		try {
			HttpEntity entity = null;
			CloseableHttpClient client = null;
			HttpPost request = null;
			try {
				String httpsURL = "http://114.4.68.19:8181/mfsmw/webapi/airtime_commit";// prod
																						// url
				log.info("URL " + httpsURL);
				String encrypted = dompetkuPaySignature();
				List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
				// urlParameters.add(new BasicNameValuePair("userid",
				// prop.getProperty("REG_DOMPETKU_USERID")));
				urlParameters.add(new BasicNameValuePair("userid", "web_api_test"));
				urlParameters.add(new BasicNameValuePair("signature", encrypted));
				urlParameters.add(new BasicNameValuePair("transid", transid));
				urlParameters.add(new BasicNameValuePair("operatorName", operator));
				urlParameters.add(new BasicNameValuePair("extRef", paymentId));
				client = httpConn.getHttpClient();
				request = new HttpPost(httpsURL);
				request.setEntity(new UrlEncodedFormEntity(urlParameters));
				HttpResponse response = client.execute(request);
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					entity = response.getEntity();
					String content = EntityUtils.toString(entity);
					log.info("");
					log.info("Result is " + content);
					log.info("Resp Code:" + statusCode);
					JSONObject jsonObj = new JSONObject(content);
					if (Integer.valueOf(jsonObj.get("status").toString()) == 0) {
						data.put("Status", "SUCCESS");
						data.put("TransactionId", jsonObj.get("trxid").toString());

					} else {
						data.put("Status", "FAILURE");
						data.put("dompetkuStatus", jsonObj.get("status").toString());
						data.put("ErrorCode", "Saturn-1016");
						data.put("ErrorDescription", jsonObj.get("msg").toString());
						data.put("DompetkuStatus", "NO");
					}
					return data;
				} else {
					data.put("Status", "Failure");
					data.put("ErrorCode", "Saturn-1016");
					data.put("ErrorDescription", "No Data Found.");
					entity = response.getEntity();
					String content = EntityUtils.toString(entity);
					log.info("error dompetku check- " + content);
				}
			} catch (IOException e) {
				IndoUtil.populateErrorMap(data, "Saturn-1023", "Saturn-101");
				log.error("Saturn-2061- GenericServiceImpl.checkDompetkuReg()- e" + IndoUtil.getFullLog(e));
			} catch (Exception ce) {
				IndoUtil.populateErrorMap(data, "Saturn-1023", "Saturn-101");
				log.error("Saturn-2062- GenericServiceImpl.checkDompetkuReg()- ce" + IndoUtil.getFullLog(ce));
			} finally {
				log.info("***********Closing Streams********");
				try {
					EntityUtils.consumeQuietly(entity);
					if (null != request) {
						request.releaseConnection();
					}
					if (null != client) {
						client.close();
					}
				} catch (IOException e) {
					log.error("Saturn-2062- GenericServiceImpl.checkDompetkuReg() e " + e);
				}
			}
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(data, "Saturn-1023", ce.getClass().getSimpleName());
			log.error("Saturn-2063- GenericServiceImpl.checkDompetkuReg() ce " + IndoUtil.getFullLog(ce));
		} finally {
			log.info("GenericServiceImpl.checkDompetkuReg() - END");
		}
		return data;
	}
}