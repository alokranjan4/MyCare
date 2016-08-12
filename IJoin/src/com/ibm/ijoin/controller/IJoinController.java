package com.ibm.ijoin.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.ijoin.service.GenericService;
import com.ibm.ijoin.util.HeaderCoder;
import com.ibm.ijoin.util.IndoUtil;
import com.ibm.ijoin.util.SessionUtil;
import com.ibm.services.vo.LoginVO;
/*
 * 
 * Author Alok Ranjan
 * 
 */

@RestController
@RequestMapping("/service")
@Consumes("application/json")
public class IJoinController {
	
	@Autowired
	GenericService genService;
	
	private static Logger log = Logger.getLogger("ijoinLogger");
	
	@RequestMapping(value = "/getImage",produces="application/json",consumes="application/json")
	public Map<String, Object> getImage(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering retrieveApplication "+jsonInput);
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			data = genService.getImage(jObj.get("image").getAsString());
			log.info("-----------------END------------------------"+data.get("Status"));
			return data;
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-218","No Data Found.",0);
			log.info("Indo-218- IJoinController.retrieveApplication() ce "+IndoUtil.getFullLog(ce));
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	}
	
	
	
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
			log.info("Indo-218- IJoinController.retrieveApplication() ce "+IndoUtil.getFullLog(ce));
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
			log.info("Indo-218- IJoinController.retrievePacks() ce "+IndoUtil.getFullLog(ce));
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	}
	
	@RequestMapping(value = "/registerUser",produces="application/json",consumes="application/json")
	public Map<String, Object> registerUser(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering registerUser"+jsonInput);
		String social_id=null;
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			JsonElement socailid=jObj.get("social_id");
			if(socailid!=null)
					social_id=jObj.get("social_id").getAsString();
			data = genService.registerUser(jObj.get("email").getAsString(),jObj.get("password").getAsString(),social_id,jObj.get("source").getAsString());
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-218","Unable To Register",0);
			log.info("Indo-218- IJoinController.registerUser() ce "+IndoUtil.getFullLog(ce));
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	}
	
	
	@RequestMapping(value ="/updateProfile",produces="application/json",consumes="application/json")
	public Map<String, Object> updateProfile(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		Map<String, Object> data = new HashMap<String,Object>();

		String cust_img=null;
		String id_img=null;
		String id_number=null;
		String name=null;
		String dob=null;
		String gender=null;
		String address=null;
		String marital_status=null;
		String religion=null;
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			if(jObj.get("id_number")!=null && !jObj.get("id_number").getAsString().isEmpty()){
				id_number=jObj.get("id_number").getAsString();
			}
			if(jObj.get("name")!=null && !jObj.get("name").getAsString().isEmpty()){
				name=jObj.get("name").getAsString();
			}
			if(jObj.get("dob")!=null && !jObj.get("dob").getAsString().isEmpty()){
				dob=jObj.get("dob").getAsString();
			}
			if(jObj.get("gender")!=null && !jObj.get("gender").getAsString().isEmpty()){
				gender=jObj.get("gender").getAsString();
			}
			if(jObj.get("address")!=null && !jObj.get("address").getAsString().isEmpty()){
				address=jObj.get("address").getAsString();
			}
			if(jObj.get("id_img")!=null && !jObj.get("id_img").getAsString().isEmpty()){
				id_img=jObj.get("id_img").getAsString();
			}
			if(jObj.get("cust_img")!=null && !jObj.get("cust_img").getAsString().isEmpty()){
				cust_img = jObj.get("cust_img").getAsString();
			}
			if(jObj.get("religion")!=null && !jObj.get("religion").getAsString().isEmpty()){
				religion=jObj.get("religion").getAsString();
			}
			if(jObj.get("marital_status")!=null && !jObj.get("marital_status").getAsString().isEmpty()){
				marital_status=jObj.get("marital_status").getAsString();
			}

			data = genService.updateProfile(jObj.get("email").getAsString(),name,cust_img,id_img,gender,id_number,dob,address,religion,marital_status);
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-218","Unable to Update Profile.",0);
			log.info("Indo-218- IJoinController.updateProfile() ce "+IndoUtil.getFullLog(ce));
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
			IndoUtil.populateErrorMap(data, "Indo-218","Upload Image Failed.",0);
			log.info("Indo-218- IJoinController.uploadImage() ce "+IndoUtil.getFullLog(ce));
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
			log.info("Indo-218- IJoinController.retrievedetails() ce "+IndoUtil.getFullLog(ce));
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	}
	@RequestMapping(value = "/userProfile",produces="application/json",consumes="application/json")
	public Map<String, Object> userProfile(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering userProfile "+jsonInput);
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			data = genService.userProfile(jObj.get("login_id").getAsString());
			log.info("-----------------END------------------------"+data.get("Status"));
			return data;
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-218","No Data Found.",0);
			log.info("Indo-218- IJoinController.userProfile() ce "+IndoUtil.getFullLog(ce));
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	}
	@RequestMapping(value = "/userImage",produces="application/json",consumes="application/json")
	public Map<String, Object> userImage(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering userImage "+jsonInput);
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			data = genService.userImage(jObj.get("login_id").getAsString());
			log.info("-----------------END------------------------"+data.get("Status"));
			return data;
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-218","User-Image Not Found",0);
			log.info("Indo-218- IJoinController.userImage() ce "+IndoUtil.getFullLog(ce));
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	}
	@RequestMapping(value = "/fetchPack",produces="application/json",consumes="application/json")
	public Map<String, Object> fetchPack(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering fetchPack "+jsonInput);
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			data = genService.fetchPack(jObj.get("packName").getAsString());
			log.info("-----------------END------------------------"+data.get("Status"));
			return data;
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-218","No Packs Found.",0);
			log.info("Indo-218- IJoinController.fetchPack() ce "+IndoUtil.getFullLog(ce));
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
			data = genService.createOrder(jObj.get("login_id").getAsString(),jObj.get("ship_addr").getAsString(),jObj.get("city").getAsString(),jObj.get("district").getAsString(),jObj.get("state").getAsString(),jObj.get("country").getAsString(),jObj.get("postcode").getAsString(),jObj.get("pkg_name").getAsString(),jObj.get("amount").getAsString(),jObj.get("msisdn").getAsString(),jObj.get("name").getAsString());
			log.info("-----------------END------------------------"+data.get("Status"));
			return data;
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-218","Unable to create Order.",0);
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
			IndoUtil.populateErrorMap(data, "Indo-218","Unable to create EtobeeOrder.",0);
			log.info("Indo-218- IjoinController.etobeeServiceCreate() ce "+IndoUtil.getFullLog(ce));
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
			log.info("Indo-218- IjoinController.trackDeliveryStatus() ce "+IndoUtil.getFullLog(ce));
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
			IndoUtil.populateErrorMap(data, "Indo-218","Order Cancelleation Failed.",0);
			log.info("Indo-218- IjoinController.cancelOrder() ce "+IndoUtil.getFullLog(ce));
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	}
	
	@RequestMapping(value = "/retrieveOrder",produces="application/json",consumes="application/json")
	public Map<String, Object> retrieveOrder(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering cancelOrder "+jsonInput);
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			data = genService.retrieveOrder(jObj.get("emailId").getAsString());
			log.info("-----------------END------------------------"+data.get("Status"));
			return data;
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-218","No Order Found.",0);
			log.info("Indo-218- IjoinController.retrieveOrder() ce "+IndoUtil.getFullLog(ce));
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
			IndoUtil.populateErrorMap(data, "Indo-218","Unable To Update Delivery Status.",0);
			log.info("Indo-218- IjoinController.updateDeliveryStatus() ce "+IndoUtil.getFullLog(ce));
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	}
	@RequestMapping(value = "/getLocation",produces="application/json",consumes="application/json")
	public Map<String, Object> getLocation(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering getLocation "+jsonInput);
		Map<String, Object> data = new HashMap<String,Object>();
		String city= "", area="", province="", where="",col="", val="";
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			if(jObj.has("city")){
				val=jObj.get("city").getAsString();
				col="districts";
				where="city";
			}if(jObj.has("area")){
				val=jObj.get("area").getAsString();
				col="postal_code";
				where="area";
			}if(jObj.has("province")){
				val=jObj.get("province").getAsString();
				col="city";
				where="province";
			}if(jObj.has("districts")){
				val=jObj.get("districts").getAsString();
				col="area";
				where="districts";
			}if(jObj.has("postal_code")){
				val=jObj.get("postal_code").getAsString();
				col="gallery";
				where="postal_code";
			}
			data = genService.getLocation(col,val,where);
			log.info("-----------------END------------------------"+data.get("Status"));
			return data;
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-218","No Location Found.",0);
			log.info("Indo-218- SprintTwo.getLocation() ce "+IndoUtil.getFullLog(ce));
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
			IndoUtil.populateErrorMap(data, "Indo-218","No Score Found.",0);
			log.info("Indo-218- IjoinController.getScore() ce "+IndoUtil.getFullLog(ce));
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
			log.info("Indo-218- IjoinController.loginUser() ce "+IndoUtil.getFullLog(ce));
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	}
	
	@RequestMapping(value = "/forgotPwd",produces="application/json",consumes="application/json")
	public Map<String, Object> forgotPwd(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering forgotPwd"+jsonInput);
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			data = genService.forgotPwd(jObj.get("email").getAsString());
			if (IndoUtil.isSuccess(data)) {
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
		        msg.setFrom(addressFrom);
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(jObj.get("email").getAsString()));
				msg.setSubject("Your Password");
				msg.setText("Your password is : "+data.get("newPwd"));
				Transport.send(msg);
				data.put("Status", "SUCCESS");
				data.remove("newPwd");
				}
				else{
				 data.put("Status", "FAILTURE");
				}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-218","No Data Found.",0);
			log.info("Indo-218- IJoinController.forgotPwd() ce "+IndoUtil.getFullLog(ce));
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	}
	
	@RequestMapping(value = "/getGallery",produces="application/json",consumes="application/json")
	public Map<String, Object> getGallery(HttpServletRequest req,@RequestBody String jsonInput) {
		log.info("-----------------START------------------------");
		log.info("Entering getGallery "+jsonInput);
		Map<String, Object> data = new HashMap<String,Object>();
		String where="",col="", val="";
		try{
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			if(jObj.has("province")){
				val=jObj.get("province").getAsString();
				col="address_stand";
				where="province";
			}
			data = genService.getGallery(col,val,where);
			log.info("-----------------END------------------------"+data.get("Status"));
			return data;
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-218","No Data Found.",0);
			log.info("Indo-218- SprintTwo.getLocation() ce "+IndoUtil.getFullLog(ce));
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	} 
}
