package com.ibm.ijoin.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;

import org.apache.log4j.Logger;
import org.jboss.security.annotation.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.ijoin.service.GenericService;
import com.ibm.ijoin.util.HeaderCoder;
import com.ibm.ijoin.util.IndoUtil;
import com.ibm.ijoin.util.SessionUtil;
import com.ibm.services.vo.LoginVO;
/*
 * 
 * @Author Alok Ranjan
 * 
 * 
 * */

@RestController
@RequestMapping("/service")
@Consumes("application/json")
public class IJoinController {
	
	@Autowired
	GenericService genService;
	
	private static Logger log = Logger.getLogger("ijoinLogger");
	
	@RequestMapping(value = "/retrieveApplication",produces="application/json",consumes="application/json")
	public Map<String, Object> retrieveActivePackage(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering retrieveApplication "+jsonInput);
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			data = genService.retrieveApplication(jObj.get("serviceType").getAsString());
			log.info("-----------------END------------------------"+data.get("Status"));
			return data;
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-218","No Data Found.",0);
			log.info("Indo-218- IJoinController.retrieveApplication() ce "+ce);
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	}
	
	@RequestMapping(value = "/retrievePacks",produces="application/json",consumes="application/json")
	public Map<String, Object> retrievePacks(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering retrievePacks "+jsonInput);
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			String points=jObj.get("points").getAsString();
			if(points.isEmpty()){
				points="0";
			}
			data = genService.retrievePacks(points);
			log.info("-----------------END------------------------"+data.get("Status"));
			return data;
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-218","No Data Found.",0);
			log.info("Indo-218- IJoinController.retrievePacks() ce "+ce);
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	}
	
	@RequestMapping(value = "/registerUser",produces="application/json",consumes="application/json")
	public Map<String, Object> registerUser(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering registerUser "+jsonInput);
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			data = genService.registerUser(jObj.get("email").getAsString(),jObj.get("password").getAsString());
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-218","No Data Found.",0);
			log.info("Indo-218- IJoinController.registerUser() ce "+ce);
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	}
	
	
	@RequestMapping(value = "/updateProfile",produces="application/json",consumes="application/json")
	public Map<String, Object> updateProfile(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering user "+jsonInput);
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			data = genService.updateProfile(jObj.get("email").getAsString(),jObj.get("name").getAsString(),jObj.get("cust_img").getAsString(),jObj.get("id_img").getAsString(),jObj.get("gender").getAsString(),jObj.get("id_number").getAsString(),jObj.get("dob").getAsString(),jObj.get("address").getAsString());
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-218","No Data Found.",0);
			log.info("Indo-218- IJoinController.registerUser() ce "+IndoUtil.getFullLog(ce));
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	}
	
	
	
	
	@RequestMapping(value = "/uploadImage",produces="application/json",consumes="application/json")
	public Map<String, Object> uploadImage(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering uploadImage "+jsonInput);
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			data = genService.uploadImage(jObj.get("login_id").getAsString(),jObj.get("msisdn").getAsString(),jObj.get("cust_img").getAsString(),jObj.get("id_img").getAsString());
			log.info("-----------------END------------------------"+data.get("Status"));
			return data;
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-218","No Data Found.",0);
			log.info("Indo-218- IJoinController.uploadImage() ce "+ce);
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	}
	
	@RequestMapping(value = "/retrievedetails",produces="application/json",consumes="application/json")
	public Map<String, Object> retrievedetails(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering retrievedetails "+jsonInput);
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			data = genService.retrievedetails(jObj.get("login_id").getAsString(),jObj.get("msisdn").getAsString());
			log.info("-----------------END------------------------"+data.get("Status"));
			return data;
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-218","No Data Found.",0);
			log.info("Indo-218- IJoinController.retrievedetails() ce "+ce);
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	}
	
	@RequestMapping(value = "/createOrder",produces="application/json",consumes="application/json")
	public Map<String, Object> createOrder(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering createOrder "+jsonInput);
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			data = genService.createOrder(jObj.get("login_id").getAsString(),jObj.get("ship_addr").getAsString(),jObj.get("district").getAsString(),jObj.get("state").getAsString(),jObj.get("country").getAsString(),jObj.get("postcode").getAsString(),jObj.get("pkg_name").getAsString(),jObj.get("amount").getAsString());
			log.info("-----------------END------------------------"+data.get("Status"));
			return data;
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-218","No Data Found.",0);
			log.info("Indo-218- IjoinController.createOrder() ce "+IndoUtil.getFullLog(ce));
		}
		return data;
	}
	
	@RequestMapping(value = "/etobeeServiceCreate",produces="application/json",consumes="application/json")
	public Map<String, Object> etobeeServiceCreate(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering etobeeServiceCreate "+jsonInput);
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			data = genService.etobeeServiceCreate(jObj.get("msisdn").getAsString(),jObj.get("email_id").getAsString(),jObj.get("ship_addr").getAsString(),jObj.get("city").getAsString(),jObj.get("state").getAsString(),jObj.get("country").getAsString(),jObj.get("postcode").getAsString(),jObj.get("order_id").getAsString());
			log.info("-----------------END------------------------"+data.get("Status"));
			return data;
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-218","No Data Found.",0);
			log.info("Indo-218- IjoinController.etobeeServiceCreate() ce "+ce);
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	}
	
	@RequestMapping(value = "/trackDeliveryStatus",produces="application/json",consumes="application/json")
	public Map<String, Object> trackDeliveryStatus(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering trackDeliveryStatus "+jsonInput);
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			data = genService.trackDeliveryStatus(jObj.get("order_number").getAsString());
			log.info("-----------------END------------------------"+data.get("Status"));
			return data;
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-218","No Data Found.",0);
			log.info("Indo-218- IjoinController.trackDeliveryStatus() ce "+ce);
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	}
	
	@RequestMapping(value = "/cancelOrder",produces="application/json",consumes="application/json")
	public Map<String, Object> cancelOrder(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering cancelOrder "+jsonInput);
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			data = genService.cancelOrder(jObj.get("order_number").getAsString());
			log.info("-----------------END------------------------"+data.get("Status"));
			return data;
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-218","No Data Found.",0);
			log.info("Indo-218- IjoinController.cancelOrder() ce "+ce);
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	}
	
	@RequestMapping(value = "/updateDeliveryStatus",produces="application/json",consumes="application/json")
	public Map<String, Object> updateDeliveryStatus(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering updateDeliveryStatus "+jsonInput);
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			data = genService.updateDeliveryStatus(jObj.get("order_number").getAsString());
			log.info("-----------------END------------------------"+data.get("Status"));
			return data;
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-218","No Data Found.",0);
			log.info("Indo-218- IjoinController.updateDeliveryStatus() ce "+ce);
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	}
	@RequestMapping(value = "/getLocation",produces="application/json",consumes="application/json")
	public Map<String, Object> getLocation(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering getLocation "+jsonInput);
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			data = genService.getLocation(jObj.get("province").getAsString(),jObj.get("districts").getAsString());
			log.info("-----------------END------------------------"+data.get("Status"));
			return data;
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-218","No Data Found.",0);
			log.info("Indo-218- SprintTwo.getLocation() ce "+ce);
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
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
	@RequestMapping(value = "/getScore",produces="application/json",consumes="application/json")
	public Map<String, Object> getScore(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering getScore "+jsonInput);
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			data = genService.getScore(jObj.get("category").getAsString(),jObj.get("type").getAsString(),jObj.get("offerType").getAsString());
			log.info("-----------------END------------------------"+data.get("Status"));
			return data;
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-218","No Data Found.",0);
			log.info("Indo-218- IjoinController.getScore() ce "+ce);
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	} 
	
	@RequestMapping(value ="/userlogin",produces="application/json",consumes="application/json")
	public Map<String, Object> loginUser(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("LoginController.loginUser(-).............start.");
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			data = genService.loginUser(jObj.get("userid").getAsString(),jObj.get("password").getAsString(),jObj.get("social_id").getAsString());
			log.info("-----------------END------------------------"+data.get("Status"));
			return data;
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-218","No Data Found.",0);
			log.info("Indo-218- IjoinController.loginUser() ce "+ce);
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	}

}
