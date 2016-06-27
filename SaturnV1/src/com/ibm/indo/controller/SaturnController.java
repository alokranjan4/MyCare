/**
 * 
 */
package com.ibm.indo.controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.infinispan.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.indo.service.GenericService;
import com.ibm.indo.service.ICareService;
import com.ibm.indo.service.LDAPService;
import com.ibm.indo.service.PrinceService;
import com.ibm.indo.service.WSSDBService;
import com.ibm.indo.util.CacheUtil;
import com.ibm.indo.util.EmailService;
import com.ibm.indo.util.Encryptor;
import com.ibm.indo.util.GenericCache;
import com.ibm.indo.util.HeaderCoder;
import com.ibm.indo.util.IndoServiceProperties;
import com.ibm.indo.util.IndoUtil;
import com.ibm.indo.util.LangProperties;
import com.ibm.indo.util.SessionUtil;
import com.ibm.services.vo.ActivityVO;
import com.ibm.services.vo.LoginVO;

/**
 * @author Aadam
 *
 */
@RestController
@RequestMapping("/service")
@Consumes("application/json")
public class SaturnController {
	@Autowired
	GenericService genService;
	@Autowired
	PrinceService prince;
	@Autowired
	WSSDBService geneva;
	@Autowired
	ICareService iCare;
	@Autowired
	LDAPService ldapService;
	
	private static Logger log = Logger.getLogger("saturnLoggerV1");
	IndoServiceProperties confProp=IndoServiceProperties.getInstance();
    Properties prop = confProp.getConfigSingletonObject();
    public static Properties langProp = LangProperties.getInstance().getConfigSingletonObject();
	/**
	 * @param jsonInput
	 * @return loginVO
	 */
    @RequestMapping(value = "/login",produces="application/json",consumes="application/json")
	public Map<String,Object> login(HttpServletRequest req,@RequestBody String jsonInput) {
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			log.info("-----------------START------------------------");
			SessionUtil.clearUser(req);
			String key = "1123445566666666";
	        String initVector = "fedcba9876543210";
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			log.info("SaturnController.loginNew() input - "+jObj.get("login_id").getAsString()+" - "+jObj.get("msisdn").getAsString());
			//String pwd = Encryptor.decrypt(key, initVector, jObj.get("password").getAsString());
			String pwd = jObj.get("password").getAsString();
			Map<String,Object> data = genService.authenticateUserNew(jObj.get("login_id").getAsString(),jObj.get("msisdn").getAsString(),pwd);
			if(IndoUtil.isSuccess(data)){
				if(data.containsKey("msisdns")){
					List<Map<String, Object>> msisdns = (List<Map<String, Object>>) data.get("msisdns");
					SessionUtil.setMsisdns(req, msisdns);
				}
				LoginVO login = new LoginVO();
				login.setAuthenticationFlag("Y");
				login.setUserid(jObj.get("login_id").getAsString());
				req.getSession().setAttribute("loginVO", login);
				String token = IndoUtil.getAlphaNumeric(64);
				SessionUtil.setToken(req, token);
				data.put("token", token);
				/*Map<String, Object>  prePostdata = iCare.customerProfile(jObj.get("msisdn").getAsString());
				Map<String, Object> profile = (Map<String, Object>) prePostdata.get("UserProfile");
				if(null!=profile && null!=profile.get("CustSegment")){
					log.info("Setting pre/post form Icare in login controller." +profile.get("CustSegment").toString());
					data.put("user_type", profile.get("CustSegment").toString());
				}*/
			}
			log.info("SaturnController.loginNew() -- END "+data.get("Status"));
			return data;
		}catch(Exception ce){
			map.clear();
			IndoUtil.populateErrorMap(map, "Saturn-500", "Saturn-101",0);
			log.error("Saturn-500 SaturnController.loginNew() ce "+IndoUtil.getFullLog(ce));
		}
		log.info("SaturnController.loginNew() -- END "+map.get("Status"));
		return map;
	}
    @RequestMapping(value = "/changePassword",produces="application/json",consumes="application/json")
   	public Map<String,Object> changePassword(HttpServletRequest req,@RequestBody String jsonInput) {
   		Map<String,Object> map = new HashMap<String,Object>();
   		JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
   		try{
   			log.info("-----------------START------------------------");
   			log.info("SaturnController.changePassword() jsonInput - "+jObj.get("login_id").getAsString());
  			String key = "1123445566666666";
	        String initVector = "fedcba9876543210";
			String oldPwd = Encryptor.decrypt(key, initVector, jObj.get("oldPassword").getAsString());
			String newPwd = Encryptor.decrypt(key, initVector, jObj.get("newPassword").getAsString());
   			map = genService.changePasswordNew(jObj.get("login_id").getAsString(), oldPwd, newPwd);
   			if(IndoUtil.isSuccess(map)){
   				req.getSession().invalidate();
   			}
   		}catch(Exception ce){
   			IndoUtil.populateErrorMap(map, "Saturn-501", "Saturn-1034",0);
   			log.error("Saturn-501 SaturnController.changePassword() ce "+IndoUtil.getFullLog(ce));
   		}finally{
   			try{
				ActivityVO actVo = new ActivityVO();
				actVo.setUserId(jObj.get("login_id").getAsString());
				actVo.setActivityType("changePassword");
				actVo.setText2(map.get("Status").toString());
				actVo.setCommid("CommId-"+IndoUtil.randInt(11111, 99999));
				Map<String, String> activityDataLog = genService.logActivity(actVo);
			}catch(Exception e){
				log.error("SaturnController.uploadImage()  "+IndoUtil.getFullLog(e));
			}
   		}
   		log.info("-----------------END------------------------"+map.get("Status"));
   		return map;
   	}
    @RequestMapping(value = "/customerProfile",produces="application/json",consumes="application/json")
   	public Map<String,Object> customerProfile(HttpServletRequest req,@RequestBody String jsonInput) {
   		Map<String,Object> map = new HashMap<String,Object>();
   		try{
   			log.info("-----------------START------------------------");
   			log.info("SaturnController.customerProfile() jsonInput - "+jsonInput);
   			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
   			map = iCare.customerProfile(jObj.get("msisdn").getAsString());
   		}catch(Exception ce){
   			IndoUtil.populateErrorMap(map, "Saturn-5011", "Saturn-101",0);
   			log.error("Saturn-5011 SaturnController.customerProfile() ce "+IndoUtil.getFullLog(ce));
   		}
   		log.info("-----------------END------------------------"+map.get("Status"));
   		return map;
   	}
    @RequestMapping(value = "/retrieveStaticData",produces="application/json",consumes="application/json")
   	public Map<String,Object> retrieveStaticData(HttpServletRequest req,@RequestBody String jsonInput) {
   		Map<String,Object> map = new HashMap<String,Object>();
   		try{
   			log.info("-----------------START------------------------");
   			log.info("SaturnController.retrieveStaticData() jsonInput - "+jsonInput);
   			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
   			String lang = "";
   			if(null!=jObj.get("language")){
   				lang = jObj.get("language").getAsString();
   			}
   			map = genService.getStaticContent(lang);
   		}catch(Exception ce){
   			IndoUtil.populateErrorMap(map, "Saturn-5011", "Saturn-101",0);
   			log.error("Saturn-5011 SaturnController.retrieveStaticData() ce "+IndoUtil.getFullLog(ce));
   		}
   		log.info("-----------------END------------------------"+map.get("Status"));
   		return map;
   	}
    
    @RequestMapping(value = "/forgotPassword",produces="application/json",consumes="application/json")
   	public Map<String,Object> forgotPassword(HttpServletRequest req,@RequestBody String jsonInput) {
   		Map<String,Object> map = new HashMap<String,Object>();
   		JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
   		try{
   			log.info("-----------------START------------------------");
   			log.info("SaturnController.forgotPasswordNew() jsonInput - "+jsonInput);
   			map =  genService.forgotPasswordNew(jObj.get("login_id").getAsString(), IndoUtil.prefix62(jObj.get("msisdn").getAsString()));
   			if(IndoUtil.isSuccess(map)){
   				map.put("Status", "SUCCESS");
   				map.put("Msg_en", langProp.get("Saturn-1030_EN"));
   				map.put("Msg_id", langProp.get("Saturn-1030_ID"));
   				String pwd = map.get("TEMP_PWD").toString();
   				map.remove("TEMP_PWD");
   				//genService.sendOTP(jObj.get("msisdn").getAsString(), "myCare password reset. myCare Username: "+jObj.get("login_id").getAsString()+" New Password: "+pwd+" IMPORTANT: Do not share credentials to others!");
   				genService.sendOTP(jObj.get("msisdn").getAsString(), "Info kata sandi myCare. %0a Username: "+jObj.get("login_id").getAsString()+"%0a Kata sandi baru: "+pwd+"%0a PENTING: Mohon tidak untuk diinfokan kepada siapapun!");
   				boolean sent = EmailService.sendEmailWithHtmlAttachment(map.get("email_id").toString(),"Info kata sandi myCare", "Info kata sandi myCare.\t\n Username: "+jObj.get("login_id").getAsString()+"\t\n Kata sandi baru: "+pwd+" \t\n PENTING: Mohon tidak untuk diinfokan kepada siapapun!", "noreply@indosatooredoo.com", "", "");
   			}else{
   				if(map.get("ErrorCode").toString().equals("Saturn-003-1")){
   					IndoUtil.populateErrorMap(map, "Saturn-502", "Saturn-1029",0);
   					return map;
   				}
   				map.clear();
   				IndoUtil.populateErrorMap(map, "Saturn-502", "Saturn-1022",0);
   				log.info("-----------------END------------------------"+map.get("Status"));
   				return map;
   			}
   			log.info("-----------------END------------------------");
   		}catch(Exception ce){
   			map.clear();
   			IndoUtil.populateErrorMap(map, "Saturn-502", "Saturn-101",0);
   			log.error("Saturn-502 SaturnController.forgotPasswordNew() ce "+IndoUtil.getFullLog(ce));
   		}finally{
   			try{
				ActivityVO actVo = new ActivityVO();
				actVo.setMsisdn(jObj.get("msisdn").getAsString());
				actVo.setActivityType("forgotPassword");
				actVo.setText2(map.get("Status").toString());
				actVo.setCommid("CommId-"+IndoUtil.randInt(11111, 99999));
				Map<String, String> activityDataLog = genService.logActivity(actVo);
			}catch(Exception e){
				log.error("SaturnController.uploadImage()  "+IndoUtil.getFullLog(e));
			}
   		}
   		log.info("-----------------END------------------------"+map.get("Status"));
   		return map;
   	}
    
    @RequestMapping(value = "/registerUser",produces="application/json",consumes="application/json")
   	public Map<String,Object> registerUser(HttpServletRequest req,@RequestBody String jsonInput) {
   		Map<String,Object> map = new HashMap<String,Object>();
   		JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
   		String otp="";
   		try{
   			log.info("-----------------START------------------------");
   			SessionUtil.clearUser(req);
   			log.info("SaturnController.registerUserNew() jsonInput - "+jsonInput);
   			
   			String key = "1123445566666666";
		        String initVector = "fedcba9876543210";
				String passwd = Encryptor.decrypt(key, initVector, jObj.get("password").getAsString());
				
   			String sOtp = (String) req.getSession().getAttribute("regOTP");
   			map = genService.regUserNew(jObj,sOtp,passwd);
   			if(IndoUtil.isSuccess(map) && map.containsKey("TEMP_OTP")){
				req.getSession().setAttribute("regOTP", map.get("TEMP_OTP").toString());
				map.remove("TEMP_OTP");
				otp="OTP";
				return map;
   			}else{
   				req.getSession().removeAttribute("regOTP");
   			}
   			if(IndoUtil.isSuccess(map)){
   				/*String pwd = map.get("TEMP_PWD").toString();
   				map.remove("TEMP_PWD");
   				//password
*/   				
   				map = genService.authenticateUserNew(jObj.get("user_id").getAsString(), jObj.get("msisdn").getAsString(), passwd);
   				if(IndoUtil.isSuccess(map)){
   					if(map.containsKey("msisdns")){
   						List<Map<String, Object>> msisdns = (List<Map<String, Object>>) map.get("msisdns");
   						SessionUtil.setMsisdns(req, msisdns);
   					}
   					LoginVO login = new LoginVO();
   					login.setAuthenticationFlag("Y");
   					login.setUserid(jObj.get("user_id").getAsString());
   					req.getSession().setAttribute("loginVO", login);
   					String token = IndoUtil.getAlphaNumeric(64);
   					SessionUtil.setToken(req, token);
   					map.put("token", token);
   				}	
   				log.info("-----------------END------------------------"+map);
   			}
   			return map;
   		}catch(Exception ce){
   			IndoUtil.populateErrorMap(map, "Saturn-505", "Saturn-101",0);
   			log.error("Saturn-505 SaturnController.registerUserNew() ce "+IndoUtil.getFullLog(ce));
   		}finally{
   			if(!StringUtils.isEmpty(otp)){
	   			try{
					ActivityVO actVo = new ActivityVO();
					actVo.setMsisdn(jObj.get("msisdn").getAsString());
					actVo.setActivityType("registerUser");
					actVo.setText2(map.get("Status").toString());
					if(null!=map.get("user_type")){
						actVo.setText3(map.get("user_type").toString());
					}
					actVo.setCommid("CommId-"+IndoUtil.randInt(11111, 99999));
					Map<String, String> activityDataLog = genService.logActivity(actVo);
				}catch(Exception e){
					log.error("SaturnController.registerUserNew()  "+IndoUtil.getFullLog(e));
				}
   			}
   		}
   		log.info("-----------------END------------------------"+map.get("Status"));
   		return map;
   	}
    @RequestMapping(value = "/getPUK",produces="application/json",consumes="application/json")
	public Map<String,Object> getPUK(HttpServletRequest req,@RequestBody String jsonInput) {
		Map<String,Object> map = new HashMap<String,Object>();
		log.info("-----------------START------------------------");
		log.info("SaturnController.getPUK() jsonInput - "+jsonInput);
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			if(!SessionUtil.isAuthorised(req, jObj.get("msisdn").getAsString())){
   				return IndoUtil.populateErrorMap(map, "Saturn-507", "Unauthorised Access.",0);
   			}
			map =  iCare.getPUK(jObj.get("msisdn").getAsString());
    	}catch(Exception ce){
			log.error("Saturn-508 SaturnController.getPUK() ce"+IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Saturn-508", "Saturn-101",0);
		}
		log.info("-----------------END------------------------"+map.get("Status"));
			return map;
	}
  
    @RequestMapping(value = "/billingInfo",produces="application/json",consumes="application/json")
	public Map<String,Object> billingInfo(HttpServletRequest req,@RequestBody String jsonInput) {
    	log.info("-----------------START------------------------");
		log.info("SaturnController.billingInfo() jsonInput - "+jsonInput);
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			log.info("SaturnController.billingInfo() input "+jObj);
			if(!SessionUtil.isAuthorised(req, jObj.get("msisdn").getAsString())){
   				return IndoUtil.populateErrorMap(map, "Saturn-507", "Unauthorised Access.",0);
   			}
			map = iCare.billingInfo(jObj.get("AccountNumber").getAsString());
		}catch(Exception ce){
			log.error("Saturn-516 SaturnController.billingInfo() ce"+IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Saturn-516", "Saturn-101",0);
		}
		log.info("-----------------END------------------------"+map.get("Status"));
		return map;
	}
	
    @RequestMapping(value = "/dashboard",produces="application/json",consumes="application/json")
	public Map<String,Object> dashboard(HttpServletRequest req,@RequestBody String jsonInput) {
		Map<String,Object> map = new HashMap<String,Object>();
		log.info("SaturnController.dashboard() jsonInput - "+jsonInput);
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			String accNum = "";
			String custType="";
			/*if(!SessionUtil.isAuthorised(req, jObj.get("msisdn").getAsString())){
   				return IndoUtil.populateErrorMap(map, "Saturn-507", "Unauthorised Access.",0);
   			}*/
			String lang = "";
			if(null!=jObj.get("lang")){
				lang = jObj.get("lang").getAsString();
			}
			if(null!=jObj.get("CustType")){
				custType = jObj.get("CustType").getAsString();
			}
			Map<String, Object> ssp = genService.getPackageSSP(jObj.get("msisdn").getAsString(), lang);
		//	log.info("SaturnController.dashboard() data "+ssp);
			if(IndoUtil.isSuccess(ssp)){
				log.info("SSP data success.");
				map.put("PackData", ssp);
				if(custType!=null && custType.equalsIgnoreCase("prepaid")){
					Map<String,Object> preBalance = new HashMap<String,Object>();
					
						Map<String, Object>  icareBalMap = iCare.inquiryPrepaid(jObj.get("msisdn").getAsString());
						log.info("icareBalMap "+icareBalMap);
						if(IndoUtil.isSuccess(icareBalMap)){
							Map<String, Object>  icareBalanceMap = (Map<String, Object>)icareBalMap.get("PrepaidInfo");
							log.info("CardActiveUntil from Icare "+ icareBalanceMap);
							preBalance.put("CardActiveUntil", icareBalanceMap.get("CardActiveUntil"));
							preBalance.put("GracePeriodUntil", icareBalanceMap.get("GracePeriodUntil"));
						}
					
					preBalance.put("Balance", ssp.get("LastBalance"));
					map.put("PrepaidInfo", preBalance);
				}
			}else{
				map.putAll(ssp);
			}
			map.put("Status","SUCCESS");
		}catch(Exception ce){
			log.error("Saturn-523 - SaturnController.dashboard() ce"+IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Saturn-523", "Saturn-101",0);
		}
		log.info("-----------------END------------------------"+map);
		return map;
	}
    
	@RequestMapping(value = "/versionCheck",produces="application/json",consumes="application/json")
	public Map<String, Object> versionCheck(HttpServletRequest req,HttpServletResponse res,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("SaturnController.versionCheck() - "+jsonInput);
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			SessionUtil.clearUser(req);
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			data = genService.versionCheck(jObj.get("currentVersion").getAsString());
			Enumeration<String> en = req.getHeaderNames();
			while(en.hasMoreElements()){
				String head = en.nextElement();
				log.info("SaturnController.versionCheck() "+head+" - "+req.getHeader(head));
			}
			String msisdn  = req.getHeader("msisdn");
			log.info("SaturnController.versionCheck() msisdn "+msisdn);
			if(null!=msisdn && !msisdn.equals("")){
				msisdn = HeaderCoder.decryptSasnHttpHeader("indosat", msisdn);
				log.info("SaturnController.versionCheck() after decrypt "+msisdn);
				Map<String, Object> map = genService.authenticateIndoUserNew(msisdn);
				if(IndoUtil.isSuccess(map)){
					if(map.containsKey("msisdns")){
						List<Map<String, Object>> msisdns = (List<Map<String, Object>>) map.get("msisdns");
						SessionUtil.setMsisdns(req, msisdns);
					}
					map.put("regFlag", "Y");
					data.putAll(map);
					LoginVO login = new LoginVO();
					login.setAuthenticationFlag("Y");
					login.setMsisdn(msisdn);
					req.getSession().setAttribute("loginVO", login);
					String token = IndoUtil.getAlphaNumeric(64);
					SessionUtil.setToken(req, token);
					data.put("token", token);
				}else{
					//log.info("AutoLogin detected but user is not registered.");
					data.put("regFlag", "N");
					data.put("msisdn", msisdn);
				}
			}else{
				data.put("regFlag", "N");
				data.put("msisdn", "");
			}
			log.info("-----------------END------------------------"+data);
			return data;
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Saturn-524","Saturn-101",0);
			log.info("Saturn-524- SaturnServiceController.versionCheck() ce "+ce);
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	}
	@RequestMapping(value = "/uploadImage",produces="application/json",consumes="application/json")
   	public Map<String,Object> uploadImage(HttpServletRequest req,@RequestBody String jsonInput) {
   		Map<String,Object> map = new HashMap<String,Object>();
   		JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
   		try{
   			log.info("-----------------START------------------------");
   			//log.info("SaturnController.uploadImage() jsonInput - "+jsonInput);
   			try{
   				if(!SessionUtil.isAuthorised(req, jObj.get("msisdn").getAsString())){
   	   				return IndoUtil.populateErrorMap(map, "Saturn-507", "Unauthorised Access.",0);
   	   			}
   				map = genService.saveImage(jObj.get("msisdn").getAsString(), jObj.get("profilepic").getAsString());
   			}catch(Exception ce){
   				log.info("SaturnController.uploadImage() ce "+IndoUtil.getFullLog(ce));
   			}
   		}catch(Exception ce){
   			IndoUtil.populateErrorMap(map, "Saturn-5026", "Saturn-101",0);
   			log.error("Saturn-5026 SaturnController.uploadImage() ce "+IndoUtil.getFullLog(ce));
   		}finally{
   			try{
				ActivityVO actVo = new ActivityVO();
				actVo.setMsisdn(jObj.get("msisdn").getAsString());
				actVo.setActivityType("uploadImage");
				actVo.setText2(map.get("Status").toString());
				actVo.setCommid("CommId-"+IndoUtil.randInt(11111, 99999));
				Map<String, String> activityDataLog = genService.logActivity(actVo);
			}catch(Exception e){
				log.error("SaturnController.uploadImage()  "+IndoUtil.getFullLog(e));
			}
   		}
   		log.info("-----------------END------------------------"+map.get("Status"));
   		return map;
   	}
	@RequestMapping(value = "/clearCache",produces="application/json",consumes="application/json")
   	public Map<String,Object> clearCache(HttpServletRequest req,@RequestBody String jsonInput) {
   		Map<String,Object> map = new HashMap<String,Object>();
   		try{
   			log.info("-----------------START------------------------");
   			log.info("SaturnController.clearCache() jsonInput - "+jsonInput);
   			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
   			if(jObj.get("token").getAsString().equals("rrtfgwefvjhquucvdhwqsdv327")){
   				Cache<String, Object>  iCareCache = CacheUtil.getInstance().getEntityCache();
   				Cache<String, Object>  genericCache = GenericCache.getInstance().getEntityCache();
   				log.info("SaturnController.clearCache() iCareCache size "+iCareCache.size());
   				log.info("SaturnController.clearCache() genericCache size "+genericCache.size());
   				genericCache.clear();
   				iCareCache.clear();
   			}
   		}catch(Exception ce){
   			IndoUtil.populateErrorMap(map, "Saturn-5025", "Saturn-101",0);
   			log.error("Saturn-5025 SaturnController.clearCache() ce "+IndoUtil.getFullLog(ce));
   		}
   		log.info("-----------------END------------------------"+map.get("Status"));
   		return map;
   	}
	@RequestMapping(value = "/reload",produces="application/json",consumes="application/json")
	public Map<String,Object> topUp(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("SaturnController.topUp() jsonInput - "+jsonInput);
		JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			log.info("SaturnController.topUp() input "+jObj);
			map = iCare.topUp(jObj.get("msisdn").getAsString(),jObj.get("voucher_code").getAsString());
		}catch(Exception ce){
			log.error("Saturn-527 SaturnController.topUp() ce"+IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Saturn-527", "Saturn-101",0);
		}finally{
   			try{
				ActivityVO actVo = new ActivityVO();
				actVo.setMsisdn(jObj.get("msisdn").getAsString());
				actVo.setActivityType("reload");
				actVo.setText1(jObj.get("voucher_code").getAsString());
				actVo.setText2(map.get("Status").toString());
				actVo.setCommid("CommId-"+IndoUtil.randInt(11111, 99999));
				Map<String, String> activityDataLog = genService.logActivity(actVo);
			}catch(Exception e){
				log.error("SaturnController.topUp()  "+IndoUtil.getFullLog(e));
			}
   		}
		log.info("-----------------END------------------------"+map.get("Status"));
		return map;
	}
	@RequestMapping(value = "/updateProfile",produces="application/json",consumes="application/json")
	public Map<String,Object> updateProfile(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("SaturnController.updateProfile() jsonInput - "+jsonInput);
		Map<String,Object> map = new HashMap<String,Object>();
		JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
		try{
			if(!SessionUtil.isAuthorised(req, jObj.get("msisdn").getAsString())){
   				return IndoUtil.populateErrorMap(map, "Saturn-507", "Unauthorised Access.",0);
   			}
			map = genService.updateProfileNew(jObj.get("login_id").getAsString(),jObj.get("msisdn").getAsString(),jObj.get("name").getAsString());
		}catch(Exception ce){
			log.error("Saturn-528 SaturnController.updateProfile() ce"+IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Saturn-528", "Saturn-101",0);
		}finally{
			try{
				ActivityVO actVo = new ActivityVO();
				actVo.setMsisdn(jObj.get("msisdn").getAsString());
				actVo.setActivityType("updateProfile");
				actVo.setText2("Update");
				actVo.setText3(map.get("Status").toString());
				actVo.setCommid("CommId-"+IndoUtil.randInt(11111, 99999));
				Map<String, String> activityDataLog = genService.logActivity(actVo);
			}catch(Exception e){
				log.error("SaturnController.updateProfile() "+IndoUtil.getFullLog(e));
			}
		}
		log.info("-----------------END------------------------"+map.get("Status"));
		return map;
	}
	@RequestMapping(value = "/retriveImage",produces="application/json",consumes="application/json")
	public Map<String,Object> retriveImage(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("SaturnController.getImage() jsonInput - "+jsonInput);
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			if(!SessionUtil.isAuthorised(req, jObj.get("msisdn").getAsString())){
   				return IndoUtil.populateErrorMap(map, "Saturn-507", "Unauthorised Access.",0);
   			}
			map = genService.getProfImage(IndoUtil.prefix62(jObj.get("msisdn").getAsString()));
		}catch(Exception ce){
			log.error("Saturn-530 SaturnController.retriveImage() ce"+IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Saturn-530", "Saturn-101",0);
		}
		log.info("-----------------END------------------------"+map.get("Status"));
		return map;
	}
	@RequestMapping(value = "/sendOTP",produces="application/json",consumes="application/json")
	public Map<String,Object> sendOTP(HttpServletRequest req,@RequestBody String jsonInput) {
		Map<String,Object> map = new HashMap<String,Object>();
		log.info("-----------------START------------------------");
		log.info("SaturnController.getPUK() jsonInput - "+jsonInput);
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			if(!SessionUtil.isAuthorised(req, jObj.get("msisdn").getAsString())){
   				return IndoUtil.populateErrorMap(map, "Saturn-507", "Unauthorised Access.",0);
   			}
			if(jObj.get("type").getAsString().equalsIgnoreCase("unLock")){
				int otp = IndoUtil.randInt(999999, 999999);
				log.info("SaturnController.validateOTP() "+otp);
				String msisdn = "",id="";
				if(null!=jObj.get("msisdn") && !StringUtils.isEmpty(jObj.get("msisdn").getAsString())){
					msisdn = jObj.get("msisdn").getAsString();
					Map<String, Object> data = genService.getUserProfileByMsisdn(msisdn);
					if(IndoUtil.isSuccess(data)){
						List<Map<String, Object>> user = (List<Map<String, Object>>) data.get("data");
						id = user.get(0).get("id").toString();
					}
				}else{
					if(null!=jObj.get("user_id") && !StringUtils.isEmpty(jObj.get("user_id").getAsString())){
						Map<String, Object> data = genService.getUserProfile(jObj.get("user_id").getAsString());
						if(IndoUtil.isSuccess(data)){
							List<Map<String, Object>> user = (List<Map<String, Object>>) data.get("data");
							msisdn = user.get(0).get("msisdn").toString();
							id = user.get(0).get("id").toString();
						}
					}
				}
			
				if(!StringUtils.isEmpty(msisdn)){
					msisdn = IndoUtil.prefix62(msisdn);
				}
				if(!StringUtils.isEmpty(id)){
					map =  genService.sendOTP(msisdn, "OTP to unlock you account is "+otp);
					req.getSession().setAttribute("unLock_"+id,otp);
					map.put("Status", "SUCCESS");map.put("TEMP_OTP", otp);
				}else{
					IndoUtil.populateErrorMap(map, "Saturn-508", "Saturn-101",0);
				}
			}
    	}catch(Exception ce){
			log.error("Saturn-508 SaturnController.getPUK() ce"+IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Saturn-508", "Saturn-101",0);
		}
		log.info("-----------------END------------------------"+map.get("Status"));
			return map;
	}
	@RequestMapping(value = "/validateOTP",produces="application/json",consumes="application/json")
	public Map<String,Object> validateOTP(HttpServletRequest req,@RequestBody String jsonInput) {
		Map<String,Object> map = new HashMap<String,Object>();
		log.info("-----------------START------------------------");
		log.info("SaturnController.getPUK() jsonInput - "+jsonInput);
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			if(!SessionUtil.isAuthorised(req, jObj.get("msisdn").getAsString())){
   				return IndoUtil.populateErrorMap(map, "Saturn-507", "Unauthorised Access.",0);
   			}
			if(jObj.get("type").getAsString().equalsIgnoreCase("unLock")){
				String msisdn = "",id="";
				if(null!=jObj.get("msisdn") && !StringUtils.isEmpty(jObj.get("msisdn").getAsString())){
					msisdn = jObj.get("msisdn").getAsString();
					Map<String, Object> data = genService.getUserProfileByMsisdn(msisdn);
					if(IndoUtil.isSuccess(data)){
						List<Map<String, Object>> user = (List<Map<String, Object>>) data.get("data");
						id = user.get(0).get("id").toString();
					}
				}
				if(null!=jObj.get("user_id") && !StringUtils.isEmpty(jObj.get("user_id").getAsString())){
					Map<String, Object> data = genService.getUserProfile(jObj.get("user_id").getAsString());
					if(IndoUtil.isSuccess(data)){
						List<Map<String, Object>> user = (List<Map<String, Object>>) data.get("data");
						msisdn = user.get(0).get("msisdn").toString();
						id = user.get(0).get("id").toString();
					}
				}
				if(!StringUtils.isEmpty(msisdn)){
					msisdn = IndoUtil.prefix62(msisdn);
				}
				String uotp = (String)req.getSession().getAttribute("unLock_"+id);
				if(jObj.get("OTP").getAsString().equals(uotp)){
					map = genService.unlockAccount(id);
				}
			}
    	}catch(Exception ce){
			log.error("Saturn-508 SaturnController.getPUK() ce"+IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Saturn-508", "Saturn-101",0);
		}
		log.info("-----------------END------------------------"+map.get("Status"));
			return map;
	}
	/*@RequestMapping(value = "/wssService",produces="application/json",consumes="application/json")
	public Map<String,Object> wssService(HttpServletRequest req,@RequestBody String jsonInput) {
		Map<String,Object> map = new HashMap<String,Object>();
		JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
		log.info("-----------------START------------------------");
		log.info("SaturnController.princeProfile() jsonInput - "+jsonInput);
		try{
			//map = prince.getUserProfile(jObj.get("msisdn").getAsString());
			map = geneva.getData(jObj.get("msisdn").getAsString());
			List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("data");
			for(Map<String,Object> m : list){
				String table = (String) m.get("TABLE_NAME");
				Map<String, Object> w = geneva.getData(table);
				map.put(table, w);
			}
			log.info("SaturnController.princeProfile() data "+map);
		}catch(Exception ce){
			log.error("SaturnController.princeProfile() ce "+IndoUtil.getFullLog(ce));
		}
		log.info("-----------------END------------------------"+map.get("Status"));
		return map;
	}*/
	/*@RequestMapping(value = "/ldapTest",produces="application/json",consumes="application/json")
	public Map<String,Object> ldapTest(HttpServletRequest req,@RequestBody String jsonInput) {
		Map<String,Object> map = new HashMap<String,Object>();
		log.info("-----------------START------------------------");
		log.info("SaturnController.ldapTest() jsonInput - "+jsonInput);
		String type="";
		JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			Properties env = new Properties();
			env.put( Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory" );
			env.put( Context.PROVIDER_URL, "ldap://192.168.31.14:4032/o=subscribers,dc=vas,dc=indosat,dc=com" );
			env.put( Context.SECURITY_PRINCIPAL, "cn=wssldap,cn=Users,dc=vas,dc=indosat,dc=com" );
			env.put( Context.SECURITY_CREDENTIALS, "wssldappwd" );
			String msisdn = jObj.get("msisdn").getAsString();
			try {
				if(msisdn.charAt(0)=='0'){
					msisdn = "62"+msisdn.substring(1);
				}
				String card_type = "";
				// obtain initial directory context using the environment
				DirContext ctx = new InitialDirContext( env );
				SearchControls ctls = new SearchControls();
				ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
				//628155133456
				String filter = "(&(msisdn="+msisdn+"))";
				//com.sun.jndi.ldap.LdapCtx i = (com.sun.jndi.ldap.LdapCtx) ctx.lookup( "msisdn=628155133456" );
				NamingEnumeration e = ctx.search("", filter, ctls);
				if (e.hasMore()) {
					SearchResult entry = (SearchResult) e.next();
					log.info("SaturnController.retrieveLastContactsNew() ldapTest "+entry);
					log.info(entry.getName());
					log.info(entry.getAttributes().get("substype").get());
					card_type = entry.getAttributes().get("card_type").get().toString();
					log.info("SaturnController.ldapTest() card_type-"+card_type);
					Attribute att = entry.getAttributes().get("service");
					String  str = (String) att.get();
					if(null!=str){
					String[] temp = str.split(",");
						if(null!=temp){
							temp=temp[0].split("=");
							map.put("stat", temp[1]);
						}
					}
					if(card_type.equals("01")){
						type= "prepaid";
					}
					else{
						type="postpaid";
					}
				}
				map.put("user_type", type);
				map.put("Status", "SUCCESS");
				log.info("SaturnController.retrieveLastContactsNew() type "+type);
				//System.out.println( "Retrieved i from directory with value: " + i.getAttributes("substype"));
			} catch ( NameAlreadyBoundException nabe ) {
				log.error("SaturnController.retrieveLastContactsNew() nabe "+nabe);
			} catch ( Exception e ) {
				log.error("SaturnController.retrieveLastContactsNew() e "+IndoUtil.getFullLog(e));
			}
		log.info("-----------------END------------------------");
		return map;
	}*/
	/*@RequestMapping(value = "/test", produces="application/json",consumes="application/json")
	public String test(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("SaturnController.test() jsonInput - "+jsonInput);
		JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
		String msisdn = jObj.get("msisdn").getAsString();
		String url = jObj.get("url").getAsString();
		Map<String, String> data = new HashMap<String,String>();
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
	  	try {
			urlParameters.add(new BasicNameValuePair("user_name","test"));
			urlParameters.add(new BasicNameValuePair("password","test"));
		  	urlParameters.add(new BasicNameValuePair("token","461fd77b-1f04-4cf9-a045-49fb07435913\'"));
		  	urlParameters.add(new BasicNameValuePair("bhs", ""));
		  	urlParameters.add(new BasicNameValuePair("time",ds+ time.substring(time.length()-3)));
		} catch (Exception e1) {
			e1.printStackTrace();
			log.error(IndoUtil.getFullLog(e1));
		}
	  	HttpEntity entity = null;
		CloseableHttpClient  client = null;
		HttpPost request = null;
	      try{
	    	  String time= System.currentTimeMillis() + "";
	  		  String ds = IndoUtil.parseDate(new Date(), "YYYYmmddHHmmss");
	    	  String urlParameters = "";
	    	  	urlParameters += "msisdn=" + URLEncoder.encode(msisdn, "UTF-8");
				urlParameters += "user_name=" + URLEncoder.encode("Javier", "UTF-8");
				urlParameters += "&password=" + URLEncoder.encode("multijav", "UTF-8");
				urlParameters += "&token=" + URLEncoder.encode("461fd77b-1f04-4cf9-a045-49fb07435913\'", "UTF-8");
				urlParameters += "&bhs=" + URLEncoder.encode("", "UTF-8");
			    urlParameters += "&time=" + URLEncoder.encode(ds+ time.substring(time.length()-3), "UTF-8");
	    	  
	    	  
	    	  urlParameters += "msisdn=" + URLEncoder.encode("628567171545", "UTF-8");
	    	  urlParameters += "&token=" + URLEncoder.encode("461fd77b-1f04-4cf9-a045-49fb07435913\'", "UTF-8");
	    	  urlParameters += "&user_id=" + URLEncoder.encode("15151", "UTF-8");
	    	  urlParameters += "&username=" + URLEncoder.encode("johndoe", "UTF-8");
	    	  urlParameters += "&time=" + URLEncoder.encode(ds+ time.substring(time.length()-3), "UTF-8");
	    	  
	    	  
	    	  client = httpConn.getHttpClient();
		      request = new HttpPost("https://mycare.indosatooredoo.com/api/v4/"+url);
		      request.setHeader("Content-Type", "application/x-www-form-urlencoded");
		      //request.setHeader("Content-Length",""+Integer.toString(urlParameters.getBytes().length));
		      request.setHeader("Content-Language","en-US");
		      request.setHeader("X-MEN",md5("8471"+urlParameters));
		      request.setHeader("Authorization","Basic YW5kcm86cGVwMl1mb3J0aWZ5");
		      
		      StringEntity se = new StringEntity(urlParameters);
		      se.setContentType("application/x-www-form-urlencoded");
		      request.setEntity(se);
		      HttpResponse response = client.execute(request);
		      int statusCode = response.getStatusLine().getStatusCode();
		      log.info("ReportsController.test() statusCode "+statusCode);
	          entity = response.getEntity();
	          String content = EntityUtils.toString(entity);
	          log.info("ReportsController.test() content "+content);
			log.info("-----------------END------------------------");
			 return content;
		}catch(Exception ce){
			log.info("SaturnController.test() ce "+IndoUtil.getFullLog(ce));
		}finally{
			 try {
					EntityUtils.consume(entity);
					if(null!=request){
					request.releaseConnection();}if(null!=client){
					client.close();}
					} catch (IOException e) {
					e.printStackTrace();
				}
		}
		log.info("SaturnController.test() "+ data);
		log.info("-----------------END------------------------");
		return null;
	}*/
	public final static String md5(String s) {
	    final String MD5 = "MD5";
	    try {
	        // Create MD5 Hash
	        MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();

	        // Create Hex String
	        StringBuilder hexString = new StringBuilder();
	        for (byte aMessageDigest : messageDigest) {
	            String h = Integer.toHexString(0xFF & aMessageDigest);
	            while (h.length() < 2)
	                h = "0" + h;
	            hexString.append(h);
	        }
	        return hexString.toString();

	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	        log.error("SaturnController e"+IndoUtil.getFullLog(e));
	    }
	    return "";
	}
}
