/**
 * 
 */
package com.ibm.indo.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.indo.service.GenericService;
import com.ibm.indo.service.HttpConnService;
import com.ibm.indo.service.ICareService;
import com.ibm.indo.service.PrinceService;
import com.ibm.indo.service.SprintTwoService;
import com.ibm.indo.service.WSSDBService;
import com.ibm.indo.serviceImpl.HttpConnServiceImpl;
import com.ibm.indo.util.IndoServiceProperties;
import com.ibm.indo.util.IndoUtil;
import com.ibm.indo.util.SessionUtil;
import com.ibm.services.vo.ActivityVO;
import com.ibm.services.vo.DompetkuVO;
import com.ibm.services.vo.InvoiceListVO;
import com.ibm.services.vo.InvoiceVO;
import com.ibm.services.vo.LoginVO;

/**
 * @author Aadam
 *
 */
@RestController
@RequestMapping("/service")
@Consumes("application/json")
public class SprintTwo {
	@Autowired
	GenericService genService;
	@Autowired
	private HttpConnService httpConn;
	@Autowired
	PrinceService prince;
	@Autowired
	WSSDBService geneva;
	@Autowired
	ICareService iCare;
	@Autowired
	SprintTwoService sprintTwo;
	
	private static Logger log = Logger.getLogger("saturnLoggerV1");
	IndoServiceProperties confProp=IndoServiceProperties.getInstance();
    Properties prop = confProp.getConfigSingletonObject();
	
	/**
	 * @param jsonInput
	 * @return loginVO
	 */
   /* @RequestMapping(value = "/retrieveDompetkuBalance",produces="application/json",consumes="application/json")
	public Map<String, String> retrieveDompetkuBalance(HttpServletRequest req,@RequestBody String jsonInput) {
		Long d1 = System.currentTimeMillis();
		log.info("-----------------START------------------------");
		log.info("Entering checkDompetkuReg "+jsonInput);
		Map<String, String> data = new HashMap<String,String>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			LoginVO user = SessionUtil.getLoginVO(req);
			if(jObj.get("Msisdn")!=null && !jObj.get("Msisdn").getAsString().isEmpty()){
				data = genService.checkDompetkuReg(jObj.get("Msisdn").getAsString(),user.getUserid());
			}
			log.info("Exiting checkDompetkuReg "+data);
			log.info("-----------------END------------------------");
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-251","No Data Found.");
			log.info("Indo-251- SprintTwo.checkDompetkuReg() ce "+ce);
		}
		log.info("Exiting checkDompetkuReg "+data);
		Long d2 = System.currentTimeMillis();
		log.info("**COMPLETE*** "+(d2-d1)+" MilliSec *********");
		log.info("-----------------END------------------------");
		return data;
	}*/
    @RequestMapping(value = "/managePackage",produces="application/json",consumes="application/json")
	public Map<String, ?> managePackage(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("SprintTwo.managePackage() jsonInput "+jsonInput);
		Map<String, String> data = new HashMap<String,String>();
		String custType="";
		String serviceName="";
		JsonArray packs=null;
		String chargingId="";
		String transactionType="";
		String packType="";
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			if(!SessionUtil.isAuthorised(req, jObj.get("Msisdn").getAsString())){
   				return IndoUtil.populateErrorMap(data, "Saturn-507", "Unauthorised Access.");
   			}
			if(jObj.get("CustType")!=null && !jObj.get("CustType").getAsString().isEmpty()){
				custType=jObj.get("CustType").getAsString();
			}
			if(jObj.get("ServiceName")!=null && jObj.get("ServiceName").isJsonArray()){
				packs=jObj.get("ServiceName").getAsJsonArray();
			}else if(null!=jObj.get("ServiceName")){
				serviceName = jObj.get("ServiceName").getAsString();
			}
			if(jObj.get("ChargingId")!=null && !jObj.get("ChargingId").getAsString().isEmpty()){
				chargingId=jObj.get("ChargingId").getAsString();
			}
			if(jObj.get("TransactionType")!=null && !jObj.get("TransactionType").getAsString().isEmpty()){
				transactionType=jObj.get("TransactionType").getAsString();
			}
			if(jObj.get("PackageType")!=null && !jObj.get("PackageType").getAsString().isEmpty()){
				packType=jObj.get("PackageType").getAsString();
			}
			//
			LoginVO user = SessionUtil.getLoginVO(req);
			if(packType.equalsIgnoreCase("VAS") && jObj.get("TransactionType").getAsString().equalsIgnoreCase("Deactivate")){
				Map<String,Object> data1 = genService.vasDeactivate(jObj.get("Msisdn").getAsString(), serviceName, user.getUserid());
				if(IndoUtil.isSuccess(data1)){
					return data1;
				}else{
					IndoUtil.populateErrorMap(data, "VAS-101", "Failed to deactivate VAS");
				}
			}else if(packType.equalsIgnoreCase("VAS") && jObj.get("TransactionType").getAsString().equalsIgnoreCase("Activate")){
				Map<String,Object> data1 = genService.vasActivate(jObj.get("Msisdn").getAsString(), serviceName, user.getUserid());
				if(IndoUtil.isSuccess(data1)){
					return data1;
				}else{
					IndoUtil.populateErrorMap(data, "VAS-102", "Failed to activate VAS");
				}
			}
			if(packType.equalsIgnoreCase("Package") && jObj.get("TransactionType").getAsString().equalsIgnoreCase("Deactivate")){
				for(JsonElement obj: packs){
					serviceName=obj.getAsString();
					data = genService.activatePackage(jObj.get("Msisdn").getAsString(),custType,serviceName,jObj.get("Param").getAsString(),chargingId,user.getUserid(),transactionType,jObj);
				}
			}else if(packType.equalsIgnoreCase("Package") && jObj.get("TransactionType").getAsString().equalsIgnoreCase("Activate")){
				data = genService.activatePackage(jObj.get("Msisdn").getAsString(),custType,serviceName,jObj.get("Param").getAsString(),chargingId,user.getUserid(),transactionType,jObj);
			}
			/*try{
				PackActivationVO vo= new PackActivationVO();
				vo.setChargingId(chargingId);
				vo.setCustType(custType);
				vo.setMsisdn(jObj.get("Msisdn").getAsString());
				vo.setParam(jObj.get("Param").getAsString());
				vo.setTid(data.get("TID"));
				vo.setUserid(user.getUserid());
				vo.setDescription(data.get("DESC"));
				vo.setServiceName(serviceName);
				Map<String, String> activityData = genService.logPackageActivation(vo);
			}catch(Exception e){
				log.error("Exception occured while saving activate Package request "+e);
				IndoUtil.populateErrorMap(data, "Indo-2016",e.getClass().getSimpleName());
			}*/
			log.info("-----------------END------------------------"+data.get("Status"));
			return data;
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-249","Transaction Failed.");
			log.info("Indo-249- SprintTwo.activate package () ce "+IndoUtil.getFullLog(ce));
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	}
    @RequestMapping(value = "/retrieveUpgradablePackages",produces="application/json",consumes="application/json")
	public Map<String, Object> retrieveUpgradablePackages(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		String custType="";
		log.info("Entering retrieveUpgradablePackages "+jsonInput);
		Map<String, Object> map = new HashMap<String, Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			if(jObj.get("CustType")!=null && !jObj.get("CustType").getAsString().isEmpty()){
				custType=jObj.get("CustType").getAsString();
			}
			String serviceClass = "";
			if(null!=jObj.get("ServiceClass") ){
				serviceClass= jObj.get("ServiceClass").getAsString();
			}
			map = genService.retrieveUpgradablePackages(custType,jObj.get("package_type").getAsString(),serviceClass);
			//log.info("Exiting retrieveUpgradablePackages "+packagesInfoListVO);
			log.info("-----------------END------------------------"+map.get("Status"));
			return map;
		}catch(Exception ce){
			log.info("Indo-247- SprintTwo.retrieveUpgradablePackages() ce "+IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Saturn-1024", "",0);
		}
		log.info("-----------------END------------------------"+map.get("Status"));
		return map;
	}
    @RequestMapping(value = "/retrieveActivePackage",produces="application/json",consumes="application/json")
	public Map<String, Object> retrieveActivePackage(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering retrievePackageDetails "+jsonInput);
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			data = genService.getPackage(jObj.get("msisdn").getAsString());
			log.info("-----------------END------------------------"+data.get("Status"));
			return data;
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-218","No Data Found.",0);
			log.info("Indo-218- SprintTwo.getPackages() ce "+ce);
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	}
    
    @RequestMapping(value = "/retrieveCMSOffers",produces="application/json",consumes="application/json")
	public Map<String, Object> retrieveCMSOffers(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering retrievePackageDetails "+jsonInput);
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			data = genService.getCMSOffers(jObj.get("msisdn").getAsString());
			log.info("-----------------END------------------------"+data.get("Status"));
			return data;
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-218","No Data Found.",0);
			log.info("Indo-218- SprintTwo.getPackages() ce "+ce);
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	}
    
    @RequestMapping(value = "/enrollOffers",produces="application/json",consumes="application/json")
  	public Map<String, Object> enrollOffers(HttpServletRequest req,@RequestBody String jsonInput) {
  		log.info("-----------------START------------------------");
  		log.info("Entering enrollOffers "+jsonInput);
  		Map<String, Object> data = new HashMap<String,Object>();
  		String offerId="";
  		try{
  			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
  			if(jObj.get("OfferId")!=null && !jObj.get("OfferId").getAsString().isEmpty()){
  				offerId=jObj.get("OfferId").getAsString();
			}
  			data = genService.enrollOffers(jObj.get("msisdn").getAsString(),offerId);
  			log.info("-----------------END------------------------"+data.get("Status"));
  			return data;
  		}catch(Exception ce){
  			IndoUtil.populateErrorMap(data, "Indo-218","No Data Found.",0);
  			log.info("Indo-218- SprintTwo.enrollOffers() ce "+ce);
  		}
  		log.info("-----------------END------------------------"+data.get("Status"));
  		return data;
  	}
    
    @RequestMapping(value = "/updateIcareRequestStatus",produces="application/json",consumes="application/xml")
	public Map<String, String> updateIcareRequestStatus(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		String clientIpAddress = req.getRemoteAddr();
		log.info("Request received from IP address : "+clientIpAddress);
		log.info("Entering updatePackageStatus "+jsonInput);
		Map<String, String> data = new HashMap<String,String>();
		String tid="";
		String status="";
		String desc="";
		String smsText="";
		try{
			 Document xmlDoc = Jsoup.parse(jsonInput, "", Parser.xmlParser());
			 if(null!=xmlDoc && null!=xmlDoc.select("TID") && null!=xmlDoc.select("TID").first()){
					tid = xmlDoc.select("TID").first().ownText().trim();
				}
			 if(null!=xmlDoc && null!=xmlDoc.select("STATUS") && null!=xmlDoc.select("STATUS").first()){
				 status = xmlDoc.select("STATUS").first().ownText().trim();
				}
			 if(null!=xmlDoc && null!=xmlDoc.select("DESC") && null!=xmlDoc.select("DESC").first()){
					desc = xmlDoc.select("DESC").first().ownText().trim();
				}
			 if(null!=xmlDoc && null!=xmlDoc.select("SMS_TEXT") && null!=xmlDoc.select("SMS_TEXT").first()){
				 smsText = xmlDoc.select("SMS_TEXT").first().ownText().trim();
				}
			 //log.info("desc is " + desc);
			data = genService.updatePackageTid(tid,status,desc,smsText);
			log.info("-----------------END------------------------"+data.get("Status"));
			return data;
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-250","Status Update Failed.");
			log.info("Indo-250- SprintTwo.status update  ce "+ce);
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	}
    
    
	@RequestMapping(value = "/updatePackageStatus",produces="application/json",consumes="application/xml")
	public Map<String, String> updatePackageStatus(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		String clientIpAddress = req.getRemoteAddr();
		log.info("Request received from IP address : "+clientIpAddress);
		log.info("Entering updatePackageStatus "+jsonInput);
		Map<String, String> data = new HashMap<String,String>();
		String tid="";
		String status="";
		String desc="";
		String smsText="";
		try{
			 Document xmlDoc = Jsoup.parse(jsonInput, "", Parser.xmlParser());
			 if(null!=xmlDoc && null!=xmlDoc.select("TID") && null!=xmlDoc.select("TID").first()){
					tid = xmlDoc.select("TID").first().ownText().trim();
				}
			 if(null!=xmlDoc && null!=xmlDoc.select("STATUS") && null!=xmlDoc.select("STATUS").first()){
				 status = xmlDoc.select("STATUS").first().ownText().trim();
				}
			 if(null!=xmlDoc && null!=xmlDoc.select("DESC") && null!=xmlDoc.select("DESC").first()){
					desc = xmlDoc.select("DESC").first().ownText().trim();
				}
			 if(null!=xmlDoc && null!=xmlDoc.select("SMS_TEXT") && null!=xmlDoc.select("SMS_TEXT").first()){
				 smsText = xmlDoc.select("SMS_TEXT").first().ownText().trim();
				}
			 //log.info("desc is " + desc);
			data = genService.updatePackageTid(tid,status,desc,smsText);
			log.info("-----------------END------------------------"+data.get("Status"));
			return data;
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-250","Status Update Failed.");
			log.info("Indo-250- SprintTwo.status update  ce "+ce);
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	}
	@RequestMapping(value = "/manageUsers",produces="application/json",consumes="application/json")
	public Map<String,Object> manageUsers(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		
		//handle null scenario for this... OTP null means send invlaid OTp response.
		log.info("SprintTwo.manageUsers() jsonInput - "+jsonInput);
		Map<String,Object> map = new HashMap<String,Object>();
		JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
		String sOtp="";
		try{
			log.info("SprintTwo.manageUsers() input "+jObj);
			String action = jObj.get("action").getAsString();
			if(!SessionUtil.isAuthorised(req, jObj.get("primary").getAsString())){
				IndoUtil.populateErrorMap(map, "Saturn-529", "Unauthorized Access.",0);
				return map;
			}
			if(action.equalsIgnoreCase("add")){
				String otp = "";
				if(null!=jObj.get("OTP")){
					otp = jObj.get("OTP").getAsString();
				}else{
					log.info("OTP should be mandatory.");
					IndoUtil.populateErrorMap(map, "Saturn-529", "Invalid OTP.",0);
					return map;
				}
				sOtp= (String) req.getSession().getAttribute("manUserOTP");
				map = genService.addChildNew(jObj.get("primary").getAsString(), jObj.get("secondary").getAsString(),otp,sOtp);
				if(IndoUtil.isSuccess(map) && map.containsKey("TEMP_OTP")){
					String rand = map.get("TEMP_OTP").toString();
					req.getSession().setAttribute("manUserOTP", rand);
					Map<String, Object> parent = genService.getUserProfileByMsisdn(jObj.get("primary").getAsString());
					log.info("Parent data "+parent );
					String id = "";
					if(IndoUtil.isSuccess(parent) && null !=parent.get("data")){
						List<Map<String, Object>> ch = (List<Map<String, Object>>) parent.get("data");
						id = ch.get(0).get("user_id").toString();
						log.info("Parent is "+id);
						parent.clear();
					}
					//genService.sendOTP(jObj.get("secondary").getAsString(), "Akun "+id+" akan menambahkan nomor "+jObj.get("secondary").getAsString()+" di myCare. info kode registrasi "+
					//rand+" %0a PENTING: Mohon kode ini tidak di infokan kepada siapapun!");
					genService.sendOTP(jObj.get("secondary").getAsString(), "PENTING: Mohon tidak untuk diinfokan kepada siapapun! %0a Akun "+id+" akan menambahkan nomor Anda di myCare. %0a Kata kunci tambah nomor myCare: "+
					rand);
				}else if(IndoUtil.isSuccess(map)){
					req.getSession().removeAttribute("manUserOTP");
					//prakash to add code
					log.info("Refreshing child in session ");				
					List<String> msisdnList= SessionUtil.getListMsisdns(req);
					String childMsisdn = IndoUtil.prefix62(jObj.get("secondary").getAsString());
					if(null!=msisdnList){
						msisdnList.add(childMsisdn);
						req.getSession().setAttribute("listMsisdns", msisdnList);
					}
				}
			}else if(action.equalsIgnoreCase("delete")){
				if(!SessionUtil.isAuthorised(req, jObj.get("secondary").getAsString())){
					IndoUtil.populateErrorMap(map, "Saturn-529", "Unauthorized Access.",0);
					return map;
				}
				map = genService.removeChildNew(jObj.get("primary").getAsString(), jObj.get("secondary").getAsString());
			}
		}catch(Exception ce){
			log.error("Saturn-529 SprintTwo.manageUsers() ce"+IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Saturn-529", "Saturn-101",0);
		}finally{
			if(!StringUtils.isEmpty(sOtp)){
				try{
					ActivityVO actVo = new ActivityVO();
					actVo.setMsisdn(jObj.get("primary").getAsString());
					actVo.setActivityType("manageUsers");
					actVo.setText1(jObj.get("secondary").getAsString());
					actVo.setText2(jObj.get("action").getAsString());
					if(map.get("Status")!=null){
						actVo.setText3(map.get("Status").toString());
					}
					actVo.setCommid("CommId-"+IndoUtil.randInt(11111, 99999));
					Map<String, String> activityDataLog = genService.logActivity(actVo);
				}catch(Exception e){
					log.error("SprintTwo.manageUsers() "+IndoUtil.getFullLog(e));
				}
			}
		}
		log.info("-----------------END------------------------"+map.get("Status"));
		return map;
	}
	@RequestMapping(value = "/contactUs",produces="application/json",consumes="application/json")
	public Map<String,Object> contactUs(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("SprintTwo.contactUs() jsonInput - "+jsonInput);
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			log.info("SprintTwo.contactUs() input "+jObj);
			map = sprintTwo.contactUsMenu(jObj);
		}catch(Exception ce){
			log.error("Saturn-530 SprintTwo.contactUs() ce"+IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Saturn-530", "Saturn-101",0);
		}
		return map;
	}
	@RequestMapping(value = "/retrieveStore",produces="application/json",consumes="application/json")
	public Map<String,Object> retrieveStore(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("SprintTwo.retrieveStore() jsonInput - "+jsonInput);
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			log.info("SprintTwo.retrieveStore() input "+jObj);
			map=sprintTwo.retrieveStore();
			map.put("Status", "SUCCESS");
			
		}catch(Exception ce){
			log.error("Saturn-531 SprintTwo.retrieveStore() ce"+IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Saturn-531", "Saturn-101",0);
		}
		return map;
	}
	
	@RequestMapping(value="/retrieveFavorite",produces="application/json",consumes="application/json")
	public Map<String,Object> retrieveFavorite(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("SprintTwo.retrieveFavorite() jsonInput - "+jsonInput);
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			log.info("SprintTwo.retrieveFavorite() input "+jObj);
			/*if(!SessionUtil.isAuthorised(req, jObj.get("msisdn").getAsString())){
   				return IndoUtil.populateErrorMap(map, "Saturn-507", "Unauthorised Access.",0);
   			}	*/
			String msisdn=jObj.get("msisdn").getAsString();
			map=sprintTwo.retriveFavourite(msisdn);
			 if(IndoUtil.isSuccess(map)){
				 map.put("Status", "SUCCESS");
			 }else{
				 map.put("Status","FAILURE");
				 IndoUtil.populateErrorMap(map, "Saturn-532", "No Data Found.",0);
			 }
		}catch(Exception ce){
			log.error("Saturn-532 SprintTwo.retrieveFavorite() ce"+IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Saturn-532", "No Data Found.",0);
		}
		return map;
	}
	@RequestMapping(value= "/addFavorite",produces="application/json",consumes="application/json")
	public Map<String,Object> addFavorite(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("SprintTwo.manageFavorite() jsonInput - " + jsonInput);
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			log.info("SprintTwo.manageFavorite() input " + jObj);
				String msisdn = jObj.get("msisdn").getAsString();
			    String DisplayName = jObj.get("DisplayName").getAsString();
			    String TransactionType = jObj.get("TransactionType").getAsString();
			    String CustType=jObj.get("CustType").getAsString();
                String TransactionData1 = jObj.get("TransactionData1").getAsString();
            	String TransactionData2 = jObj.get("TransactionData2").getAsString();
            	String TransactionData3 = jObj.get("TransactionData3").getAsString();
            	String TransactionData4 = jObj.get("TransactionData4").getAsString();
            	String TransactionData5 = jObj.get("TransactionData5").getAsString();
            	map = sprintTwo.addFavourite(msisdn, TransactionType, 
					  TransactionData1, TransactionData2, TransactionData3, TransactionData4, TransactionData5,DisplayName,CustType);
				if (IndoUtil.isSuccess(map)) {
					map.put("Status", "SUCCESS");
				}else {
					  map.put("Status", "FAILURE");
					IndoUtil.populateErrorMap(map, "Saturn-533", "Saturn-533",0);
				}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Indo-234","unable to add favourite.",0);
			log.info("Indo-234- SprintTwo.manageFavorite() ce "+ce);
		}
		log.info("Exiting serviceRequest - "+map);
		log.info("-----------------END------------------------");
	 	return map;
	}
	
	@RequestMapping(value= "/manageFavorite",produces="application/json",consumes="application/json")
	public Map<String,Object> manageFavorite(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("SprintTwo.arrangeFavorite() jsonInput - " + jsonInput);
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			/*
			 * {"msisdn":"6285770355730","tokenid":"",
			 * "TransactionType":["ActivatePackage-PackageCode#1","ActivatePackage-PackageCode#2","ActivatePackage-PackageCode#3"]}{
			 * 
			 */
			String action="";
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			if(jObj.get("action")!=null){
				action=jObj.get("action").getAsString();
			}
			if(action.equalsIgnoreCase("arrange")){
				log.info("SprintTwo.arrangeFavorite() input " + jObj);
	 			String msisdn = jObj.get("msisdn").getAsString();
			    JsonArray TransactionTypeArray=   jObj.getAsJsonArray("TransactionType");
			    List<Object[]> listObj = new ArrayList<Object[]>();
			    for(JsonElement pack:TransactionTypeArray){
			    	String TransactionType=pack.getAsString();
			    	String temp[] = TransactionType.split("#");
			    	if(null!=temp && temp.length==2){
			    		Object[] obj = new Object[3];
			    		obj[0]=temp[1];
			    		obj[1]=temp[0];
			    		obj[2]=IndoUtil.prefix62(msisdn);
			    		listObj.add(obj);
			    	}
			     }	  
			    map = sprintTwo.arrangeFavourite(listObj);
			}
			if(action.equalsIgnoreCase("delete")){
				log.info("SprintTwo.arrangeFavorite() input " + jObj);
	 			String msisdn = jObj.get("msisdn").getAsString();
			    JsonArray TransactionTypeArray=   jObj.getAsJsonArray("TransactionType");
			    List<Object[]> listObj = new ArrayList<Object[]>();
			    for(JsonElement pack:TransactionTypeArray){
			    	String transactionType=pack.getAsString();
			    	
			    		Object[] obj = new Object[2];
			    		obj[0]=transactionType;
			    		obj[1]=IndoUtil.prefix62(msisdn);
			    		
			    		listObj.add(obj);
			    	
			     }	  
			    map = sprintTwo.deleteFavourite(listObj);
			}
			
		  }catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Indo-234","unable to arrange favourite.",0);
			log.info("Indo-234- SprintTwo.manageFavorite() ce "+ce);
		  }
		log.info("Exiting serviceRequest - "+map);
		log.info("-----------------END------------------------");
	 	return map;
	}
	
	@RequestMapping(value= "/arrangeFavorite",produces="application/json",consumes="application/json")
	public Map<String,Object> arrangeFavorite(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("SprintTwo.arrangeFavorite() jsonInput - " + jsonInput);
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			/*
			 * {"msisdn":"6285770355730","tokenid":"",
			 * "TransactionType":["ActivatePackage-PackageCode#1","ActivatePackage-PackageCode#2","ActivatePackage-PackageCode#3"]}{
			 * 
			 */
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			log.info("SprintTwo.arrangeFavorite() input " + jObj);
	 			String msisdn = jObj.get("msisdn").getAsString();
			    JsonArray TransactionTypeArray=   jObj.getAsJsonArray("TransactionType");
			    List<Object[]> listObj = new ArrayList<Object[]>();
			    for(JsonElement pack:TransactionTypeArray){
			    	String TransactionType=pack.getAsString();
			    	String temp[] = TransactionType.split("#");
			    	if(null!=temp && temp.length==2){
			    		Object[] obj = new Object[3];
			    		obj[0]=temp[1];
			    		obj[1]=temp[0];
			    		obj[2]=IndoUtil.prefix62(msisdn);
			    		listObj.add(obj);
			    	}
			     }	  
			    map = sprintTwo.arrangeFavourite(listObj);
		  }catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Indo-234","unable to arrange favourite.",0);
			log.info("Indo-234- SprintTwo.manageFavorite() ce "+ce);
		  }
		log.info("Exiting serviceRequest - "+map);
		log.info("-----------------END------------------------");
	 	return map;
	}
	@RequestMapping(value = "/serviceRequest",produces="application/json",consumes="application/json")
	public Map<String, Object> serviceRequest(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		 log.info("Entering serviceRequest - "+jsonInput);
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			String custType="";
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			String reqType = jObj.get("RequestType").getAsString();
			 String msisdn=jObj.get("msisdn").getAsString();
			 if(jObj.get("CustType")!=null){
				 custType=jObj.get("CustType").getAsString();
			 }
			JsonObject attMap = jObj.get("AttributeMap").getAsJsonObject();
			String trxId = msisdn + new SimpleDateFormat("ddMMyyyyHHmmss").format(new Date()) + "007";
			if(reqType.equalsIgnoreCase("changeEmail")){
				String newEmail=attMap.get("NewEmail").getAsString();
				String xml ="<?xml version=\"1.0\" encoding=\"UTF-8\"?> <WSSMessage xmlns=\"com/icare/eai/schema/evWSSChangeCustEmail\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"com/icare/eai/schema/evWSSChangeCustEmail evWSSChangeCustEmail.xsd\"><WSSChangeCustEmail><TransactionID>"+trxId+"</TransactionID><ServiceNumber>"+msisdn+"</ServiceNumber><EmailAddress>"+newEmail+"</EmailAddress><ActivityCode>1204010023</ActivityCode><Group>"+custType+"</Group></WSSChangeCustEmail></WSSMessage>";
				data= iCare.serviceRequest(xml,reqType);
			}else if(reqType.equalsIgnoreCase("changeBillDate")){
				String newBillDate=attMap.get("NewBillDate").getAsString();
				String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><WSSMessage xmlns=\"com/icare/eai/schema/evWSSSetBillDate\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"com/icare/eai/schema/evWSSSetBillDate evWSSSetBillDate.xsd\"><WSSSetBillDate><TransactionID>"+trxId+"</TransactionID><ServiceNumber>"+msisdn+"</ServiceNumber><NewBillDate>"+newBillDate+"</NewBillDate><ActivityCode>1204010026</ActivityCode><Group>"+custType+"</Group></WSSSetBillDate></WSSMessage>";
				data= iCare.serviceRequest(xml,reqType);
			}else if(reqType.equalsIgnoreCase("activeEbill")){
				String newEmail=attMap.get("NewEmail").getAsString();
				String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><WSSMessage xmlns=\"com/icare/eai/schema/evWSSRegisterMARS\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"com/icare/eai/schema/evWSSRegisterMARS evWSSRegisterMARS.xsd\"><WSSRegisterMARS><TransactionID>"+trxId+"</TransactionID><ServiceNumber>"+msisdn+"</ServiceNumber><EmailAddress>"+newEmail+"</EmailAddress><ActivityCode>1204020010</ActivityCode><Group>Postpaid</Group></WSSRegisterMARS></WSSMessage>";
				data= iCare.serviceRequest(xml,reqType);
			}else if(reqType.equalsIgnoreCase("deactiveEbill")){
				String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <WSSMessage xmlns=\"com/icare/eai/schema/evWSSUnregisterMARS\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"com/icare/eai/schema/evWSSUnregisterMARS evWSSUnregisterMARS.xsd\"> <WSSUnregisterMARS> <TransactionID>"+trxId+"</TransactionID> <ServiceNumber>"+msisdn+"</ServiceNumber> <ActivityCode>1204020011</ActivityCode> <Group>Postpaid</Group> </WSSUnregisterMARS> </WSSMessage>";
				data= iCare.serviceRequest(xml,reqType);
			}else if(reqType.equalsIgnoreCase("changeAddress")){
				 String addressType="";
				 String addressLine1="";
				 String addressLine2="";
				 String buildingName="";
				 String city="";
				 String province="";
				 String zipCode="";
				if(attMap.get("AddressType")!=null){
					addressType=attMap.get("AddressType").getAsString();
				}if(attMap.get("AddressLine1")!=null){
					addressLine1=attMap.get("AddressLine1").getAsString();
				}if(attMap.get("AddressLine2")!=null){
					addressLine2=attMap.get("AddressLine2").getAsString();
				}if(attMap.get("BuildingName")!=null){
					buildingName=attMap.get("BuildingName").getAsString();
				}if(attMap.get("City")!=null){
					city=attMap.get("City").getAsString();
				}
				if(attMap.get("Province")!=null){
					province=attMap.get("Province").getAsString();
				}
				if(attMap.get("ZipCode")!=null){
					zipCode=attMap.get("ZipCode").getAsString();
				}
				String validFrom = IndoUtil.parseDate(new Date(), "MM/dd/yyyy HH:mm:ss");
				String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?> <WSSMessage xmlns=\"com/icare/eai/schema/evWSSSetPostalAddress\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"com/icare/eai/schema/evWSSSetPostalAddress evWSSSetPostalAddress.xsd \"> <WSSSetPostalAddress> <TransactionID>"+trxId+"</TransactionID> <ServiceNumber>"+msisdn+"</ServiceNumber> <AddressType>"+addressType+"</AddressType> <AddressLine1>"+addressLine1+"</AddressLine1> <AddressLine2>"+addressLine2+"</AddressLine2> <BuildingName>"+buildingName+"</BuildingName><Province>"+province+"</Province> <City>"+city+"</City> <ZipCode>"+zipCode+"</ZipCode> <ValidFrom>"+validFrom+"</ValidFrom> <ActivityCode>1204010029</ActivityCode> <Group>"+custType+"</Group> </WSSSetPostalAddress> </WSSMessage>";
				data=iCare.serviceRequest(xml,reqType);
			}
	
			return data;
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-234","Transaction Failed.",0);
			log.info("Indo-234- SprintTwo.serviceRequest() ce "+ce);
		}
		log.info("Exiting serviceRequest - "+data);
		log.info("-----------------END------------------------");
		return data;
	}
	@RequestMapping(value = "/getBanner",produces="application/json",consumes="application/json")
	public Map<String,Object> getBanner(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("SprintTwo.getBanner() jsonInput - "+jsonInput);
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			log.info("SprintTwo.getBanner() input "+jObj);
			String catType = "";
			if(null!=jObj.get("CategoryType")){
				catType = jObj.get("CategoryType").getAsString();
			}
			map = genService.getBannerImages(jObj.get("ServiceClass").getAsString(),jObj.get("CustType").getAsString(),catType);
		}catch(Exception ce){
			log.error("Saturn-534 SprintTwo.getBanner() ce"+IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Saturn-534", "Saturn-101",0);
		}
		log.info("-----------------END------------------------"+map.get("Status"));
		return map;
	}
	@RequestMapping(value = "/retrieveOffers",produces="application/json",consumes="application/json")
	public Map<String,Object> retrieveOffers(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("SprintTwo.retrieveOffers() jsonInput - "+jsonInput);
		String custType="",lang="";
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			log.info("SprintTwo.retrieveOffers() input "+jObj);
			if(jObj.get("custType")!=null){
				custType=jObj.get("custType").getAsString();
			}
			if(jObj.get("lang")!=null){
				lang=jObj.get("lang").getAsString();
			}
			map = genService.getOffers(jObj.get("offerType").getAsString(),lang);
		}catch(Exception ce){
			log.error("Saturn-534 SprintTwo.retrieveOffers() ce"+IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Saturn-534", "Saturn-101",0);
		}
		return map;
	}
	@RequestMapping(value = "/retrieveInvoice",produces="application/json",consumes="application/json")
	public InvoiceListVO retrieveInvoice(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering retrieveInvoice "+jsonInput);
		InvoiceListVO invoiceListVO = new InvoiceListVO();
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			if(jObj.get("BillingAccountNumber")!=null && jObj.get("BillingAccountNumber").getAsString()!=null && !StringUtil.isBlank(jObj.get("BillingAccountNumber").getAsString())){
				map = iCare.billingInfo(jObj.get("BillingAccountNumber").getAsString());
				List<Map<String, String>> d = (List<Map<String, String>>) map.get("ListOfBillingInfo");
				if(null!=d && d.size()>0){
					List<InvoiceVO> invoice = new ArrayList<InvoiceVO>();
					for(Map<String, String> m : d){
						InvoiceVO vo = new InvoiceVO();
						try{
							vo.setAmount(m.get("InvoiceValue"));
							vo.setDueDate(m.get("DueDate"));
							vo.setBillDate(m.get("BillDate"));
							vo.setInvoiceNumber(m.get("InvoiceNumber"));
							vo.setInvoiceDate(m.get("PeriodOfInvoice"));
							log.info("SprintTwo.retrieveInvoice() InvoiceValue "+m.get("InvoiceValue"));
							vo.setInvoiceValue((Double.valueOf(m.get("InvoiceValue")).longValue()));
							invoice.add(vo);
						}catch(Exception dc){
							log.info("Indo-204- IndoServiceController.retrieveInvoice() for ce "+IndoUtil.getFullLog(dc));
						}
					}
					invoiceListVO.setStatus("SUCCESS");
					invoiceListVO.setInvoicesList(invoice);
				}else{
					invoiceListVO.setStatus("FAILURE");
					invoiceListVO.setErrorCode("Saturn-9000");
					invoiceListVO.setErrorDescription("No data found!");
				}
			}else if(jObj.get("Msisdn")!=null && jObj.get("Msisdn").getAsString()!=null){
				invoiceListVO = sprintTwo.getCorpInvoices(jObj.get("Msisdn").getAsString());
			}
		}catch(Exception ce){
			log.info("Indo-204- IndoServiceController.retrieveInvoice() ce "+IndoUtil.getFullLog(ce));
			invoiceListVO.setStatus("FAILURE");
			invoiceListVO.setErrorCode("Indo-204");
			invoiceListVO.setErrorDescription("No Data Found.");
		}
		log.info("Exiting retrieveInvoice "+invoiceListVO);
		log.info("-----------------END------------------------");
		return invoiceListVO;
	}
	
	@RequestMapping(value = "/retrieveLov",produces="application/json",consumes="application/json")
	public Map<String,Object> retrieveLov(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering retrieveLov "+jsonInput);
		Map<String,Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			String name = "";
			if(null!=jObj.get("Name")){
				name = jObj.get("Name").getAsString();
			}
			return genService.getLov(jObj.get("Type").getAsString(),name);
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-255", "No Data Found",0);
			log.info("Indo-255- IndoServiceController.getBalance() ce "+IndoUtil.getFullLog(ce));
		}
		log.info("-----------------END------------------------");
		return data;
	}
	
	@RequestMapping(value = "/retrievePdf",produces="application/json",consumes="application/json")
	public Map<String,Object> retrievePdf(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering retrieveLov "+jsonInput);
		Map<String,Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			String strInv = "";
			if(null!=jObj.get("InvoiceNumber")){
				strInv = jObj.get("InvoiceNumber").getAsString();
			}
			URL myUrl = new URL("http://10.128.1.66:8181/IsatPdfWebServer/servlet/PdfService?strInv="+strInv);
			URLConnection connection = myUrl.openConnection();
			int sizePdf = connection.getContentLength();
			int maxSize = 1024 * 500;
			InputStream stream = connection.getInputStream();
			byte[] pdf = IOUtils.toByteArray(stream);
			try {
				Properties props1 = new Properties();
				props1.put("mail.smtp.host", "smtpgw.indosatooredoo.com");
				props1.put("mail.smtp.port", "25");
			//	props1.put("mail.smtp.user", false);
			//	props1.put("mail.smtp.password", false);
				props1.put("mail.transport.protocol","smtp"); 
				Session session = Session.getDefaultInstance(props1);
				session.setDebug(true);
				MimeMessage msg = new MimeMessage(session);
				log.info("Msg is  || " + msg);
				InternetAddress addressFrom = new InternetAddress("noreply@indosatooredoo.com");
				log.debug("addressFrom is  || " + addressFrom);
				log.debug("Msg is *** || " + msg);
				addressFrom.setPersonal("INDOSAT");
				DataSource dataSource = new ByteArrayDataSource(pdf, "application/pdf");
		        MimeBodyPart pdfBodyPart = new MimeBodyPart();
		        pdfBodyPart.setDataHandler(new DataHandler(dataSource));
		        pdfBodyPart.setFileName("Bill_Detail.pdf");
				MimeMultipart mimeMultipart = new MimeMultipart();
		        mimeMultipart.addBodyPart(pdfBodyPart);
				msg.setFrom(addressFrom);
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress("ppathak5@in.ibm.com"));
				msg.setSubject("Your Bill");
				msg.setContent(mimeMultipart);
				Transport.send(msg);
				data.put("Status", "SUCCESS");
			} catch (Exception e) {
				data.put("Status", "FAILURE");
				log.error("Eception Occured  In EmailMagr ||" +IndoUtil.getFullLog(e));
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-255", "No Data Found",0);
			log.info("Indo-255- IndoServiceController.getBalance() ce "+IndoUtil.getFullLog(ce));
		}
		log.info("-----------------END------------------------");
		return data;
	}
	@RequestMapping(value = "/appLauncher",produces="application/json",consumes="application/json")
	public Map<String,Object> appLauncher(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering appLauncher "+jsonInput);
		Map<String,Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			String type = "";
			if(null!=jObj.get("type")){
				type = jObj.get("type").getAsString();
			}
			return genService.appLauncher(type);
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-255", "No Data Found",0);
			log.info("Indo-255- IndoServiceController.appLauncher() ce "+IndoUtil.getFullLog(ce));
		}
		log.info("-----------------END------------------------");
		return data;
	}
	@RequestMapping(value = "/sendMessage",produces="application/json",consumes="application/json")
	public Map<String,Object> sendMessage(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering sendMessage "+jsonInput);
		Map<String,Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			String type = "", title="";
			if(null!=jObj.get("type")){
				type = jObj.get("type").getAsString();
			}else{
				type="message";
			}
			if(null!=jObj.get("title")){
				title = jObj.get("title").getAsString();
			}
			return genService.sendMessage(jObj.get("from_msisdn").getAsString(), title, jObj.get("msg").getAsString(), type,jObj.get("to_msisdn").getAsString(),jObj.get("from_date").getAsString());
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-255", "No Data Found",0);
			log.info("Indo-255- IndoServiceController.sendMessage() ce "+IndoUtil.getFullLog(ce));
		}
		log.info("-----------------END------------------------");
		return data;
	}
	@RequestMapping(value = "/getMessages",produces="application/json",consumes="application/json")
	public Map<String,Object> getMessages(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering getMessages "+jsonInput);
		Map<String,Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			String type = "";
			if(null!=jObj.get("type")){
				type = jObj.get("type").getAsString();
			}
			return genService.getMessages(jObj.get("msisdn").getAsString(), type);
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-255", "No Data Found",0);
			log.info("Indo-255- IndoServiceController.getMessages() ce "+IndoUtil.getFullLog(ce));
		}
		log.info("-----------------END------------------------");
		return data;
	}
	/*@RequestMapping(value = "/changeReadStatus",produces="application/json",consumes="application/json")
	public Map<String,Object> changeReadStatus(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering changeReadStatus "+jsonInput);
		Map<String,Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			
			return genService.changeReadStatus(jObj.get("id").getAsString(), jObj.get("status").getAsString());
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-255", "No Data Found",0);
			log.info("Indo-255- IndoServiceController.changeReadStatus() ce "+IndoUtil.getFullLog(ce));
		}
		log.info("-----------------END------------------------");
		return data;
	}*/
	
	
	@RequestMapping(value = "/changeReadStatus",produces="application/json",consumes="application/json")
	public Map<String,Object> changeReadStatus(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering changeReadStatus "+jsonInput);
		Map<String,Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			JsonArray TransactionTypeArray=   jObj.getAsJsonArray("ids");
			log.info("TransactionTypeArray ===========================  " + TransactionTypeArray);
		    List<Object[]> listObj = new ArrayList<Object[]>();
		    for(JsonElement list:TransactionTypeArray){
		    	JsonObject id = list.getAsJsonObject();  	
		    		Object[] obj = new Object[2];
		    		obj[1]=id.get("id").getAsString();
		    		if(null==id.get("flag") || StringUtils.isEmpty(id.get("flag").getAsString())){
		    			obj[0]="N";
		    		}else{
		    			obj[0]=id.get("flag").getAsString();
		    		}
		    		listObj.add(obj);
		     }	
			return genService.changeAllReadStatus(listObj);
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-255", "No Data Found",0);
			log.info("Indo-255- IndoServiceController.changeReadStatus() ce "+IndoUtil.getFullLog(ce));
		}
		log.info("-----------------END------------------------");
		return data;
	} 
	
	
	/*@RequestMapping(value = "/deleteMessage",produces="application/json",consumes="application/json")
	public Map<String,Object> deleteMessage(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering deleteMessage "+jsonInput);
		Map<String,Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			return genService.deleteMessage(jObj.get("id").getAsString());
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-255", "No Data Found",0);
			log.info("Indo-255- IndoServiceController.deleteMessage() ce "+IndoUtil.getFullLog(ce));
		}
		log.info("-----------------END------------------------");
		return data;
	}
	*/
	@RequestMapping(value = "/loadTest",produces="application/json",consumes="application/json")
	public Map<String,Object> loadTest(HttpServletRequest req,@RequestBody String jsonInput) {
		JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
		int ct = Integer.parseInt(jObj.get("ct").getAsString());
		
		for(int i=0;i<=ct;i++){
			load();
		}
		return null;
	}
	
	public static void main(String args[]){
		for(int i=0;i<=1000;i++){
			Thread t = new Thread(new Runnable() {
			    public void run() {
			HttpEntity entity = null;
			CloseableHttpClient  client = null;
			HttpPost request = null;
		      try{
		    	  client = new HttpConnServiceImpl().getHttpClient();
		    	  StringEntity requestEntity = new StringEntity("{\"msisdn\":\"6285693535339\",\"token\":\"rrtfgwefvjhquucvdhwqsdv327\",\"password\":\"D3GAaxjl7moLz95Kg9d07A==\",\"login_id\":\"\"}");
		    	  requestEntity.setContentType("application/json");
			      request = new HttpPost("https://mobileagent.indosatooredoo.com/SaturnV1/service/login");
			      //request.setHeader("Content-Length",""+Integer.toString(urlParameters.getBytes().length));
			      request.setHeader("Content-type", "application/json");
			      request.setHeader("authorization", "cfg2h34n3v54wd9876qtwdbui32r23ufvhcsd");
			      request.setEntity(requestEntity);
			      HttpResponse response = client.execute(request);
			      int statusCode = response.getStatusLine().getStatusCode();
			     System.out.println("SprintTwo.main(...).new Runnable() {...}.run()"+statusCode);
		          entity = response.getEntity();
		          String content = EntityUtils.toString(entity);
		          System.out.println(content);
			}catch(Exception ce){
				System.out.println("SaturnController.test() ce "+IndoUtil.getFullLog(ce));
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
			    }
			});
			t.start();
		}
	}
	
	@Async
	public static void load(){
			Thread t = new Thread(new Runnable() {
			    public void run() {
			HttpEntity entity = null;
			CloseableHttpClient  client = null;
			HttpPost request = null;
		      try{
		    	  client = new HttpConnServiceImpl().getHttpClient();
		    	  StringEntity requestEntity = new StringEntity("{\"msisdn\":\"6285693535339\",\"token\":\"rrtfgwefvjhquucvdhwqsdv327\",\"password\":\"D3GAaxjl7moLz95Kg9d07A==\",\"login_id\":\"\"}");
		    	  requestEntity.setContentType("application/json");
			      request = new HttpPost("https://10.128.168.34:8080/SaturnV1/service/login");
			      //request.setHeader("Content-Length",""+Integer.toString(urlParameters.getBytes().length));
			      request.setHeader("Content-type", "application/json");
			      request.setHeader("authorization", "cfg2h34n3v54wd9876qtwdbui32r23ufvhcsd");
			      request.setEntity(requestEntity);
			      HttpResponse response = client.execute(request);
			      int statusCode = response.getStatusLine().getStatusCode();
			      log.info("SprintTwo.main(...).new Runnable() {...}.run()"+statusCode);
		          entity = response.getEntity();
		          String content = EntityUtils.toString(entity);
		          log.info(content);
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
			    }
			});
			t.start();
	}
	
	@RequestMapping(value = "/4GPlusCheck",produces="application/json",consumes="application/json")
	public Map<String,Object> FourGPlusCheck(HttpServletRequest req,@RequestBody String jsonInput) {
		Map<String,Object> map = new HashMap<String,Object>();
		log.info("SaturnController.4GPlusCheck() jsonInput - "+jsonInput);
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			String msisdn = IndoUtil.prefix62(jObj.get("msisdn").getAsString());;
			String type = "";
			
			if(jObj.get("simType").getAsString().equalsIgnoreCase("USIM")){
				map.put("Status", "SUCCESS");
				map.put("SIMFlag", "Y");
				map.put("DeviceFlag", "Y");
				map.put("Msisdn", msisdn);
			}
			else{
				IndoUtil.populateErrorMap(map, "Saturn-007", "Saturn-109", 0);
			}			
		}catch(Exception ce){
			log.error("Saturn-523 - SaturnController.dashboard() ce"+IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Saturn-523", "Saturn-101",0);
		}
		log.info("-----------------END------------------------"+map.get("Status"));
		return map;
	}
	
	@RequestMapping(value = "/retrieveUsageHistory",produces="application/json",consumes="application/json")
	public Map<String,Object> usageHistory(HttpServletRequest req,@RequestBody String jsonInput) {
    	log.info("-----------------START------------------------");
		log.info("SaturnController.usageHistory() jsonInput - "+jsonInput);
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			log.info("SaturnController.usageHistory() input "+jObj);
			/*if(!SessionUtil.isAuthorised(req, jObj.get("msisdn").getAsString())){
   				return IndoUtil.populateErrorMap(map, "Saturn-507", "Unauthorised Access.",0);
   			}*/
			String strDate =jObj.get("strDate").getAsString();
			String endDate=jObj.get("endDate").getAsString();
			map = iCare.usageHistory(jObj.get("msisdn").getAsString(),strDate,endDate,jObj.get("CustType").getAsString());
	    }catch(Exception ce){
			log.error("Saturn-510 SaturnController.usageHistory() ce"+IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Saturn-510", "Saturn-101",0);
		}
		log.info("-----------------END------------------------");
		return map;
	}
	
	@RequestMapping(value= "/countClick",produces="application/json",consumes="application/json")
	public Map<String,Object> countClick(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("SprintTwo.countClick() jsonInput - " + jsonInput);
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
				log.info("SprintTwo.countClick() input " + jObj);
	 			String msisdn =jObj.get("msisdn").getAsString();
	 			String countClick =jObj.get("clickCount").getAsString();
			     int countClick1=Integer.parseInt(countClick);
			    map = genService.countClick(msisdn, countClick1);
		
		  }catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Indo-234","unable to work countClick service.",0);
			log.info("Indo-234- SprintTwo.countClick() ce "+ce);
		  }
		log.info("Exiting serviceRequest - "+map);
		log.info("-----------------END------------------------");
	 	return map;
	}
	
	
	@RequestMapping(value = "/deleteMessage",produces="application/json",consumes="application/json")
	public Map<String,Object> deleteMessage(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering deleteMessage "+jsonInput);
		Map<String,Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			JsonArray TransactionTypeArray=   jObj.getAsJsonArray("id");
			log.info("TransactionTypeArray ===========================  " + TransactionTypeArray);
		    List<Object[]> listObj = new ArrayList<Object[]>();
		    for(JsonElement list:TransactionTypeArray){
		    	String id = list.getAsString();		    	
		    		Object[] obj = new Object[1];
		    		obj[0]=id;
		    		listObj.add(obj);
		     }	
		    data = genService.deletemultipleMessage(listObj);
			}catch(Exception ce){
				IndoUtil.populateErrorMap(data, "Indo-255", "No Data Found",0);
				log.info("Indo-255- IndoServiceController.deleteMessage() ce "+IndoUtil.getFullLog(ce));
			}
				log.info("-----------------END------------------------");
				return data;
			}	 	
	@RequestMapping(value = "/regDompetku",produces="application/json",consumes="application/json")
	public Map<String, String> regDompetku(HttpServletRequest req,@RequestBody String jsonInput) {
		Long d1 = System.currentTimeMillis();
		log.info("-----------------START------------------------");
		log.info("Entering regDompetku "+jsonInput);
		LoginVO user = SessionUtil.getLoginVO(req);
		Map<String, String> data = new HashMap<String,String>();
		DompetkuVO domVO= new DompetkuVO();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			if(jObj.get("Msisdn")!=null && !jObj.get("Msisdn").getAsString().isEmpty()){
				domVO.setMsisdn(jObj.get("Msisdn").getAsString());
			}
			if(jObj.get("FirstName")!=null && !jObj.get("FirstName").getAsString().isEmpty()){
				domVO.setFirstname(jObj.get("FirstName").getAsString());
			}
			if(jObj.get("LastName")!=null && !jObj.get("LastName").getAsString().isEmpty()){
				domVO.setLastname(jObj.get("LastName").getAsString());
			}
			if(jObj.get("Gender")!=null && !jObj.get("Gender").getAsString().isEmpty()){
				domVO.setGender(jObj.get("Gender").getAsString());
			}
			if(jObj.get("IdType")!=null && !jObj.get("IdType").getAsString().isEmpty()){
				domVO.setIdtype(jObj.get("IdType").getAsString());
			}
			if(jObj.get("IdNumber")!=null && !jObj.get("IdNumber").getAsString().isEmpty()){
				domVO.setIdnumber(jObj.get("IdNumber").getAsString());
			}
			if(jObj.get("Address")!=null && !jObj.get("Address").getAsString().isEmpty()){
				domVO.setAddress(jObj.get("Address").getAsString());
			}
			if(jObj.get("ProfilePic")!=null && !jObj.get("ProfilePic").getAsString().isEmpty()){
				domVO.setProfilepic(jObj.get("ProfilePic").getAsString());
			}
			if(jObj.get("IdPhoto")!=null && !jObj.get("IdPhoto").getAsString().isEmpty()){
				domVO.setIdphoto(jObj.get("IdPhoto").getAsString());
			}
			if(jObj.get("Dob")!=null && !jObj.get("Dob").getAsString().isEmpty()){
				String str = jObj.get("Dob").getAsString();
				str = str.substring(0, 8);
				Date dat = IndoUtil.parseDate(str, "ddMMyyyy");
				if(null==dat){
					IndoUtil.populateErrorMap(data, "Indo-252","Date of Birth is not valid, please enter a valid value \"ddmmyyyy\"");
					return data;
				}
				domVO.setDob(str);
			}
			if(jObj.get("MotherName")!=null && !jObj.get("MotherName").getAsString().isEmpty()){
				domVO.setMothername(jObj.get("MotherName").getAsString());
			}
			if(jObj.get("AgentId")!=null && !jObj.get("AgentId").getAsString().isEmpty()){
				domVO.setAgentid(jObj.get("AgentId").getAsString());
			}else{
				domVO.setAgentid(user.getUserid());
			}
			if(jObj.get("LocationId")!=null && !jObj.get("LocationId").getAsString().isEmpty()){
				domVO.setLocationid(jObj.get("LocationId").getAsString());
			}
			data = genService.regDompetku(domVO);
			log.info("Exiting regDompetku "+data);
			log.info("-----------------END------------------------");
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-252","Dompetku Registration Failed.");
			log.info("Indo-252- IndoServiceController.regDompetku() ce "+IndoUtil.getFullLog(ce));
		}
		log.info("Exiting regDompetku "+data);
		Long d2 = System.currentTimeMillis();
		log.info("**COMPLETE*** "+(d2-d1)+" MilliSec *********");
		log.info("-----------------END------------------------");
		return data;
	}
	
	

	
	@RequestMapping(value = "/checkDompetkuReg",produces="application/json",consumes="application/json")
	public Map<String, String> checkDompetkuReg(HttpServletRequest req,@RequestBody String jsonInput) {
		Long d1 = System.currentTimeMillis();
		log.info("-----------------START------------------------");
		log.info("Entering checkDompetkuReg "+jsonInput);
		Map<String, String> data = new HashMap<String,String>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			String agentId="";
		
				agentId="myCare";
			
			if(jObj.get("Msisdn")!=null && !jObj.get("Msisdn").getAsString().isEmpty()){
				data = genService.checkDompetkuReg(jObj.get("Msisdn").getAsString(),agentId);
			}
			log.info("Exiting checkDompetkuReg "+data);
			log.info("-----------------END------------------------");
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-251","No Data Found.");
			log.info("Indo-251- IndoServiceController.checkDompetkuReg() ce "+ce);
		}
		log.info("Exiting checkDompetkuReg "+data);
		Long d2 = System.currentTimeMillis();
		log.info("**COMPLETE*** "+(d2-d1)+" MilliSec *********");
		log.info("-----------------END------------------------");
		return data;
	}
	@RequestMapping(value = "/retrieveDompetkuBalance",produces="application/json",consumes="application/json")
	public Map<String, String> retrieveDompetkuBalance(HttpServletRequest req,@RequestBody String jsonInput) {
		Long d1 = System.currentTimeMillis();
		log.info("-----------------START------------------------");
		log.info("Entering checkDompetkuReg "+jsonInput);
		Map<String, String> data = new HashMap<String,String>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
		//	LoginVO user = SessionUtil.getLoginVO(req);
			log.info("agent id for dompetku balance check :");


			if(jObj.get("Msisdn")!=null && !jObj.get("Msisdn").getAsString().isEmpty()&&jObj.get("agentId")!=null && !jObj.get("agentId").getAsString().isEmpty()){
				data = genService.checkDompetkuReg(jObj.get("Msisdn").getAsString(),jObj.get("agentId").getAsString());
			}
			log.info("Exiting checkDompetkuReg "+data);
			log.info("-----------------END------------------------");
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-251","No Data Found.");
			log.info("Indo-251- SprintTwo.checkDompetkuReg() ce "+ce);
		}
		log.info("Exiting checkDompetkuReg "+data);
		Long d2 = System.currentTimeMillis();
		log.info("**COMPLETE*** "+(d2-d1)+" MilliSec *********");
		log.info("-----------------END------------------------");
		return data;
	}
	
	@RequestMapping(value = "/dompetkuPay",produces="application/json",consumes="application/json")
	public Map<String, String> dompetkuPay(HttpServletRequest req,@RequestBody String jsonInput) {	
	Long d1 = System.currentTimeMillis();
	log.info("-----------------START------------------------");
	log.info("Entering checkDompetkuReg "+jsonInput);
	String custType="";
	String operator="";
	Map<String, String> data = new HashMap<String,String>();
	try{
		JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
		if(jObj.get("CustType")!=null && !jObj.get("CustType").getAsString().isEmpty()){
			custType=jObj.get("CustType").getAsString();
		}
		if(jObj.get("Operator")!=null && !jObj.get("Operator").getAsString().isEmpty()){
			operator=jObj.get("Operator").getAsString();
		}
		LoginVO user = SessionUtil.getLoginVO(req);
		if(custType!=null && custType.equalsIgnoreCase("prepaid")){
			if(jObj.get("Msisdn")!=null && !jObj.get("Msisdn").getAsString().isEmpty()){
				data = genService.dompetkuRecharge(jObj.get("Msisdn").getAsString(),jObj.get("Amount").getAsString(),jObj.get("TransactionPIN").getAsString(),operator);
			}
		}else if(custType!=null && custType.equalsIgnoreCase("postpaid")){
			if(jObj.get("Msisdn")!=null && !jObj.get("Msisdn").getAsString().isEmpty()){
				data = genService.dompetkuPostpay(jObj.get("Msisdn").getAsString(),jObj.get("Amount").getAsString(),jObj.get("TransactionPIN").getAsString(),operator);
			}
		}
		log.info("Exiting checkDompetkuReg "+data);
		log.info("-----------------END------------------------");
	}catch(Exception ce){
		IndoUtil.populateErrorMap(data, "Indo-251","No Data Found.");
		log.info("Indo-251- IndoServiceController.checkDompetkuReg() ce "+ce);
	}
	log.info("Exiting checkDompetkuReg "+data);
	Long d2 = System.currentTimeMillis();
	log.info("**COMPLETE*** "+(d2-d1)+" MilliSec *********");
	log.info("-----------------END------------------------");
	return data;
}
	
	
	@RequestMapping(value = "/getDenomination",produces="application/json",consumes="application/json")
	public Map<String,Object> getDenomination(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering getDenomination "+jsonInput);
		Map<String,Object> data = new HashMap<String,Object>();
		String type="";
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
		
			return genService.getDenoms(jObj.get("msisdn").getAsString(),type);
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-255", "No Data Found",0);
			log.info("Indo-255- IndoServiceController.getMessages() ce "+IndoUtil.getFullLog(ce));
		}
		log.info("-----------------END------------------------");
		return data;
	}

	@RequestMapping(value= "/logPayment",produces="application/json",consumes="application/json")
	public Map<String,Object> logPayment(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("SprintTwo.logPayment() jsonInput - " + jsonInput);
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			log.info("SprintTwo.logPayment() input " + jObj);
				String msisdn = jObj.get("msisdn").getAsString();
			    String amount = jObj.get("amount").getAsString();
			    String transactionType = jObj.get("transactionType").getAsString();
			    String custType=jObj.get("custType").getAsString();
                String transactionData1 = jObj.get("text1").getAsString();
            	String transactionData2 = jObj.get("text2").getAsString();
            	String transactionData3 = jObj.get("text3").getAsString();
            	String transactionData4 = jObj.get("text4").getAsString();
            	String transactionData5 = jObj.get("text5").getAsString();
            	
            	
            	map = sprintTwo.logPayment(msisdn, transactionType, amount,
					  transactionData1, transactionData2, transactionData3, transactionData4, transactionData5,custType);
				if (IndoUtil.isSuccess(map)) {
					map.put("Status", "SUCCESS");
				}else {
					  map.put("Status", "FAILURE");
					IndoUtil.populateErrorMap(map, "Saturn-533", "Saturn-533",0);
				}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Indo-234","unable to add payment log.",0);
			log.info("Indo-234- SprintTwo.logPayment() ce "+ce);
		}
		log.info("Exiting logPayment - "+map);
		log.info("-----------------END------------------------");
	 	return map;
	}
	@RequestMapping(value= "/retrieveActivity",produces="application/json",consumes="application/json")
	public Map<String,Object> retrieveActivity(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("SprintTwo.retrieveActivity() jsonInput - " + jsonInput);
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
				log.info("SprintTwo.retrieveActivity() input " + jObj);
	 			String msisdn =jObj.get("msisdn").getAsString();
			    map = genService.retrieveActivity(msisdn);
		
		  }catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Indo-234","retrieveActivity failed.",0);
			log.info("Indo-234- SprintTwo.retrieveActivity() ce "+ce);
		  }
		log.info("Exiting retrieveActivity - ");
		log.info("-----------------END------------------------");
	 	return map;
	}@RequestMapping(value= "/retrievePackInfo",produces="application/json",consumes="application/json")
	public Map<String,Object> retrievePackInfo(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("SprintTwo.retrievePackInfo() jsonInput - " + jsonInput);
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
				log.info("SprintTwo.retrievePackInfo() input " + jObj);
	 			String keyword =jObj.get("keyword").getAsString();
			    map = genService.retrievePackInfo(keyword);
		
		  }catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Indo-234","retrievePackInfo failed.",0);
			log.info("Indo-234- SprintTwo.retrievePackInfo() ce "+ce);
		  }
		log.info("Exiting retrievePackInfo - ");
		log.info("-----------------END------------------------");
	 	return map;
	}
	@RequestMapping(value= "/regDevice",produces="application/json",consumes="application/json")
	public Map<String,Object> regDevice(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("SprintTwo.regDevice() jsonInput - " + jsonInput);
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
				log.info("SprintTwo.regDevice() input " + jObj);
	 			String osType =jObj.get("osType")==null?"":jObj.get("osType").getAsString();
	 			String model =jObj.get("model")==null?"":jObj.get("model").getAsString();
	 			String make =jObj.get("make")==null?"":jObj.get("make").getAsString();
			    map = genService.regDevice(jObj.get("msisdn").getAsString(), jObj.get("deviceId").getAsString(), osType,model,make);
		
		  }catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Indo-234","regDevice failed.",0);
			log.info("Indo-234- SprintTwo.regDevice() ce "+ce);
		  }
		log.info("Exiting regDevice - "+map);
		log.info("-----------------END------------------------");
	 	return map;
	}
	@RequestMapping(value= "/sendNotification",produces="application/json",consumes="application/json")
	public Map<String,Object> sendNotification(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("SprintTwo.sendNotification() jsonInput - " + jsonInput);
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
				log.info("SprintTwo.sendNotification() input " + jObj);
	 			String msisdn =jObj.get("msisdn").getAsString();
			    map = genService.sendNotification(msisdn,jObj.get("message").getAsString());
		
		  }catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Indo-234","sendNotification failed.",0);
			log.info("Indo-234- SprintTwo.sendNotification() ce "+ce);
		  }
		log.info("Exiting sendNotification - "+map);
		log.info("-----------------END------------------------");
	 	return map;
	}
	@RequestMapping(value= "/quickSurvey",produces="application/json",consumes="application/json")
	public Map<String,Object> quickSurvey(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("SprintTwo.quickSurvey() jsonInput - " + jsonInput);
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
				log.info("SprintTwo.quickSurvey() input " + jObj);
			    map = genService.quickSurvey();
		  }catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Indo-234","regDevice failed.",0);
			log.info("Indo-234- SprintTwo.quickSurvey() ce "+ce);
		  }
		log.info("Exiting quickSurvey - "+map);
		log.info("-----------------END------------------------");
	 	return map;
	}
}
