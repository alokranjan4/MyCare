package com.ibm.ijoin.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.ijoin.service.GenericService;
import com.ibm.ijoin.util.IndoUtil;


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
			data = genService.retrievePacks(jObj.get("points").getAsString());
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
			data = genService.regUser(jObj.get("login_id").getAsString(),IndoUtil.prefix62(jObj.get("msisdn").getAsString()),jObj.get("name").getAsString(),jObj.get("email").getAsString(),jObj.get("password").getAsString(),
					jObj.get("cust_img").getAsString(),jObj.get("id_img").getAsString(),jObj.get("gender").getAsString(),jObj.get("id_number").getAsString(),jObj.get("dob").getAsString(),jObj.get("place_of_birth").getAsString(),
					jObj.get("alt_number").getAsString(),jObj.get("maiden_name").getAsString(),jObj.get("address").getAsString(),jObj.get("act_status").getAsString(),jObj.get("act_date").getAsString(),jObj.get("icc_id").getAsString());
			log.info("-----------------END------------------------"+data.get("Status"));
			return data;
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-218","No Data Found.",0);
			log.info("Indo-218- IJoinController.registerUser() ce "+ce);
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
	
}
