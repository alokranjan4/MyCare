package com.ibm.ijoin.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.ijoin.service.GenericService;
import com.ibm.ijoin.service.HttpConnService;
import com.ibm.ijoin.util.BlowFish;
import com.ibm.ijoin.util.DBUtil;
import com.ibm.ijoin.util.IndoUtil;
/*
 * 
 * Author Alok Ranjan
 * 
 */
@Service
public class GenericServiceImpl implements GenericService{
	@Autowired
	DBUtil dbUtil;
	@Autowired
	HttpConnService httpConn;
	private static Logger log = Logger.getLogger("ijoinLogger");
	
	@Override
	public Map<String, Object> retrieveApplication(String serviceType) {
		log.info("GenericServiceImpl.getApplication() - START");
		Map<String, Object> map=new HashMap<String, Object>();	
		List<Map<String, Object>> app = null;
		try{
			if(StringUtils.isEmpty(serviceType)){
				app = dbUtil.getData("SELECT * from ijoin_type", new Object[]{});
			}else{
				app = dbUtil.getData("SELECT * from ijoin_type where serviceType=?", new Object[]{serviceType});
			}
			map.put("ApplicationList", app);
			map.put("Status", "SUCCESS");
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Ijoin-1001", "No Data Found",0);
			log.error("Ijoin-1001- GenericServiceImpl.retrieveApplication() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.retrieveApplication() - END");
		}
		return map;
	}
	@Override
	public Map<String, Object> retrievePacks(String points) {
		log.info("GenericServiceImpl.retrievePacks() - START");
		Map<String, Object> map=new HashMap<String, Object>();	
		ArrayList<Object> list= new ArrayList<>();
		String pack_name="";
		try{
			if(null!= points || points !=""){
				if(StringUtils.isNumeric(points)){
					int point= Integer.parseInt(points);
					List<Map<String, Object>> app = dbUtil.getData("select package from ijoin_recomended_packs where min_val<=? and max_val>=?", new Object[]{point,point});
					pack_name = app.get(0).get("package").toString();
					List<Map<String, Object>> applist = dbUtil.getData("SELECT * from ijoin_packs where pack_name_en=?", new Object[]{pack_name});
					for (Map<String, Object> data : applist) {
						list.add(data);
					}
					map.put("Packs", list);
					map.put("Status", "SUCCESS");
				}else{
					IndoUtil.populateErrorMap(map, "Ijoin-1002", "No Data Found",0);
					map.put("Status", "FAILTURE");
				}
			}
			}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Ijoin-1002", "No Data Found",0);
			log.error("Ijoin-1002- GenericServiceImpl.retrievePacks() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.retrievePacks - END");
		}
	return map;
	}
	
	@Override
	public Map<String, Object> registerUser( String email,String password,String social_id,String source) {
		log.info("GenericServiceImpl.registerUser() - START");
		Map<String, Object> map=new HashMap<String, Object>();	
		int order = IndoUtil.randInt(111111, 999999);
		try{
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			int ct=0;
				if(!StringUtils.isEmpty(email)){
						list=dbUtil.getData("select * from ijoin_user where userid=?", new Object[]{email});
					if(list.size()>0){
						map.put("Status", "FAILURE");
						IndoUtil.populateErrorMap(map, "Ijoin-1002", "Email already registered.",0);
						return map;
					}else{
						ct = dbUtil.saveData("insert into ijoin_user(userid,password,social_id,cust_source,last_LoginDate,reg_date) VALUES(?,?,?,?,sysdate,sysdate)", new Object[]{email,password,social_id,source});
						if(ct>0){
							dbUtil.saveData("insert into ijoin_order (order_id,order_date,order_status,delivery_status,delivery_date) values(?,SYSDATE,?,?,SYSDATE)", new Object[]{order,"DRAFT","CREATED"});
						}
					}
				  if(ct>0){
						map.put("last_LoginDate",IndoUtil.parseDate(new Date(), "dd-MM-yyyy"));
						map.put("REG_DATE",IndoUtil.parseDate(new Date(), "dd-MM-yyyy")); 	 
						map.put("Status", "SUCCESS");
						try{
							ct = dbUtil.saveData("insert into ijoin_user_images(userid) VALUES(?)", new Object[]{email});
							ct = dbUtil.saveData("insert into ijoin_user_profile(userid) VALUES(?)", new Object[]{email});
						}catch(Exception ce){}
					}else{
						IndoUtil.populateErrorMap(map, "Ijoin-1003", "No Data Found",0);
						map.put("Status", "FAILURE");
					}
				}else{
					IndoUtil.populateErrorMap(map, "Ijoin-1003", "No Data Found",0);
					map.put("Status", "FAILURE");
				 }	
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Ijoin-1003", "User Registration Failed",0);
			log.error("Ijoin-1003- GenericServiceImpl.registerUser() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.registerUser - END");
		}
		return map;	
	}
	
	@Override
	public java.util.Map<String,Object> updateProfile(String email, String name, String cust_img, String id_img, String gender, String id_number, String dob, String address, String religion, String marital_status) {
	Map<String, Object> map=new HashMap<String, Object>();	
	log.info("GenericServiceImpl.updateProfile- start");
		try{
			int ct=0;
			int order = IndoUtil.randInt(111111, 999999);
			if(!StringUtils.isEmpty(email)){
			  List<Map<String, Object>> list=dbUtil.getData("select * from IJOIN_USER_PROFILE where userid=?", new Object[]{email});
			  List<Map<String, Object>> list1=dbUtil.getData("select * from IJOIN_ORDER where login_id=?", new Object[]{email});
			   if(null!=list && list.size()>0){
				   if(!StringUtils.isEmpty(cust_img)||!StringUtils.isEmpty(id_img)){
					   ct=dbUtil.saveData("update IJOIN_USER_IMAGES set CUST_IMG=?,ID_IMG=? where userid=?",new Object[]{cust_img,id_img,email});   
				   }if(ct>0){
				   		ct = dbUtil.saveData("update IJOIN_USER_PROFILE  set name=?,GENDER=?,ID_NUMBER=?,DOB=to_date(?,'dd-mm-yyyy'),ADDRESS=? , religion =?,marital_status=? where userid=?"
								   , new Object[]{name,gender,id_number,dob,address,religion,marital_status,email});
				   			map.put("Status", "SUCCESS");
				   	}else{
				   		IndoUtil.populateErrorMap(map, "Ijoin-1024", "Failed to Update Profile",0);
				   	}
			   }else{
				   	ct = dbUtil.saveData("INSERT ALL into IJOIN_USER_PROFILE (NAME,GENDER,ID_NUMBER,DOB,ADDRESS,reg_date,last_loginDate,religion,marital_status,USERID) values(?,?,?,?,?,sysdate,sysdate,?,?,?) into IJOIN_USER_IMAGES(CUST_IMG,ID_IMG,USERID) values(?,?,?) select * from dual", new Object[]{name,gender,id_number,dob,address,religion,marital_status,email,cust_img,id_img,email});   
				   	 if(ct>0){
				   		 	map.put("Status", "SUCCESS");
				   	 }else{
				   		IndoUtil.populateErrorMap(map, "Ijoin-1024", "Failed to Update Profile",0);
				     }
			   	}
			   if(null!=list1 && list1.size()>0 && ct>0){
				   ct=dbUtil.saveData("update IJOIN_ORDER set delivery_status=?,order_status=?,delivery_date=SYSDATE where login_id=?",new Object[]{"VERIFIED","DRAFT",email});  
			   }else{
				   ct=dbUtil.saveData("insert into IJOIN_ORDER(order_id,delivery_status,order_status,login_id,delivery_date,order_date) values(?,?,?,?,SYSDATE,SYSDATE)",new Object[]{order,"VERIFIED","DRAFT",email});
			   }
			}else{
				IndoUtil.populateErrorMap(map, "Ijoin-1004", "Failed to Update Profile",0);
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Ijoin-1004", "Failed to Update Profile",0);
			log.error("Ijoin-2051- GenericServiceImpl.updateProfile() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.updateProfile- END");
		}
		log.info("GenericServiceImpl.updateProfile- end");
		return map;	
	} 

	@Override
	public Map<String, Object> uploadImage(String login_id,String msisdn1,String cust_img,String id_img) {
		Map<String, Object> map=new HashMap<String, Object>();
		Map<String, Object> map1=new HashMap<String, Object>();
		String msisdn =  IndoUtil.prefix62(msisdn1);
		int ct = 0;
		try{
			List<Map<String, Object>> list = dbUtil.getData("select * from ijoin_user where userid=? or msisdn=?",new Object[]{login_id,msisdn});
			for (Map<String, Object> data1 : list) {
				map1 = (Map<String, Object>) data1;
			}
			if(map1.get("login_id").toString().equals(login_id) || map1.get("msisdn").toString().equals(msisdn)) {
				ct =dbUtil.saveData("update ijoin_user SET cust_img=?,id_img=? where userid=? or msisdn=?",new Object[]{cust_img,id_img,login_id,msisdn});
			}		
			else {
				log.info("Insert Stmt");
				ct = dbUtil.saveData("insert into ijoin_user(userid,msisdn,cust_img,id_img) VALUES(?,?,?,?)", new Object[]{login_id,msisdn,cust_img,id_img});
			}
			if(ct>0){
				map.put("Status","SUCCESS");
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Ijoin-1005", "Failed to Upload Image .",0);
			log.error("Ijoin-1005- GenericServiceImpl.uploadImage() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.uploadImage - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> retrievedetails(String login_id, String msisdn1) {
		Map<String, Object> map=new HashMap<String, Object>();
		String msisdn =  IndoUtil.prefix62(msisdn1);
		try{
			List<Map<String, Object>> list =dbUtil.getData("select * from ijoin_user where userid = ? or msisdn= ?", new Object[]{login_id,msisdn});
			if(list.size()>0){
				map.put("Details", list);
				map.put("Status", "SUCCESS");
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Ijoin-1006", "No Data Found .",0);
			log.error("Ijoin-1006- GenericServiceImpl.retrievedetails() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.retrievedetails - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> getOrderDetails(String ORDER_ID) {
		Map<String, Object> map=new HashMap<String, Object>();
		List<Map<String, Object>> list = null;
		log.info("GenericServiceImpl.getOrderDetails() ORDER_ID :"+ORDER_ID+":");
		try{
			list =dbUtil.getData("select a.*,b.* from ijoin_order a,ijoin_user_profile b where a.ORDER_ID =? and a.login_id=b.userid", new Object[]{ORDER_ID});
			log.info("GenericServiceImpl.getOrderDetails() size "+list.size());
			if(list.size()>0){
				map.put("Details", list);
				map.put("Status", "SUCCESS");
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Ijoin-1007", "No Data Found",0);
			log.error("Ijoin-1007- GenericServiceImpl.getOrderDetails() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.getOrderDetails - END");
		}
		return map;
	}
	
	@Override
	public Map<String, Object> orderdetails(String rownum1,String rownum2, String searchKey ) {
		Map<String, Object> map=new HashMap<String, Object>();
		int count =0;
		try{
			count =Integer.parseInt(dbUtil.getData("select count(*) count from ijoin_order a,IJOIN_USER b where a.login_id= b.userid", new Object[]{}).get(0).get("COUNT").toString());
			List<Map<String, Object>> list =dbUtil.getData("select * from ( select a.ORDER_ID,a.INVOICE,b.MSISDN,a.order_status,a.DELIVERY_STATUS, rownum r from ijoin_order a,IJOIN_USER b where a.login_id= b.userid ORDER BY a.order_date DESC) where r >?and r <=?", new Object[]{rownum1,rownum2});
			List<List<Object>> rows = new ArrayList<List<Object>>();
			for(Map<String,Object> m : list){
				List<Object> row = new ArrayList<Object>();
				row.add(m.get("ORDER_ID"));
				row.add(m.get("INVOICE"));
				row.add(m.get("MSISDN"));
				row.add(m.get("ORDER_STATUS"));
				row.add(m.get("DELIVERY_STATUS"));
				rows.add(row);
			}
			if(list.size()>0){
				map.put("recordsTotal", count);
				map.put("recordsFiltered", count);
				map.put("data", rows);
				map.put("Status", "SUCCESS");
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Ijoin-1008", "No Data Found",0);
			log.error("Ijoin-1008- GenericServiceImpl.orderdetails() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.orderdetails - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> updateOrder(String order_id,String msisdn, String act_status, String icc_id,String order_status,String agentID) {
		Map<String, Object> map=new HashMap<String, Object>();
		Map<String, Object> location=null;
		List list=null;
		List locations_list=new ArrayList<>();
		String login_id = "";
		String ship_addr ="";
		String city ="";
		String province ="";
		String postcode ="";
		String alt_num= "";
		int ct = 0;
		try{
			list = dbUtil.getData("select a.*,b.* from ijoin_order a, IJOIN_USER_PROFILE b where order_id=? and a.LOGIN_ID=b.userid", new Object[]{order_id});
			Map<String, Object> map1=(Map<String, Object>) list.get(0);
			String user_id=(String) map1.get("USERID");
			log.info("Login ID ---------------"+ user_id);
			if(null!= map1.get("LOGIN_ID")){
				login_id =(String) map1.get("LOGIN_ID");
			}
			if(null!= map1.get("SHIP_ADDRESS")){
				ship_addr =(String)map1.get("SHIP_ADDRESS");
			}
			if(null!= map1.get("POSTAL_CODE")){
				postcode =(String)map1.get("POSTAL_CODE");
			}
			if(null!= map1.get("ALT_NUMBER")){
				alt_num =(String)map1.get("ALT_NUMBER");
			}
			log.info("postal_code -------  " + postcode);
			locations_list = dbUtil.getData("select * from ijoin_location where postal_code=? ", new Object[]{postcode});
			location=(Map<String, Object>) locations_list.get(0);
			if(null!= location.get("PROVINCE")){
				province =(String) location.get("PROVINCE");
			}
			if(null!= location.get("CITY")){
				city =(String)location.get("CITY");
			}

			if (!StringUtils.isEmpty(msisdn)) {
				ct = dbUtil.saveData("update IJOIN_USER set msisdn=? where userid=?",new Object[] { msisdn, user_id });
			}
			if (ct > 0) {
				
				ct = dbUtil.saveData("update IJOIN_ORDER  set order_status=?,act_status=?,icc_id=?,agentID=?,delivery_date=SYSDATE,act_date=SYSDATE,delivery_status=? where login_id=?",new Object[] {"IN PROGRESS",act_status,icc_id,agentID,"ORDERED",login_id});
				if(ct>0){
					Map<String, Object> status= etobeeServiceCreate(alt_num, login_id, ship_addr, city, province, "Indonesia", postcode,order_id);
					if(IndoUtil.isSuccess(status)){
						map.put("Status", "SUCCESS");
						sendOTP(msisdn,"Message, link : http://testmobileagent.indosatooredoo.com:8080/iJoin_Indosat/SourceCode/index.html#/welcome/"+BlowFish.encrypt(user_id));
					}
				}else{
					IndoUtil.populateErrorMap(map, "Ijoin-1009", "Failed to update order",0);
				}
			}else{
				IndoUtil.populateErrorMap(map, "Ijoin-1009", "Failed to update order",0);
			}
		/*	if(count>0){
				Map<String, Object> status= etobeeServiceCreate(alt_num, login_id, ship_addr, city, province, "Indonesia", postcode,order_id);
				if(IndoUtil.isSuccess(status)){
					map.put("Status", "SUCCESS");
				}
			}*/
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Ijoin-1024", "Failed to update order",0);
			log.info("Ijoin-2051- GenericServiceImpl.updateOrder() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.updateOrder - END");
		}
		return map;
	}
	
	@Override
	public Map<String, Object> createOrder( String login_id, String ship_addr,String city,String district,String state,String country,String postcode,String pkg_name,String amount,String msisdn,String name) {
		int order = IndoUtil.randInt(111111, 999999);
		int track = IndoUtil.randInt(11111111, 99999999);
		String order_status="DRAFT";
		String address = ship_addr+","+city+","+district+","+state+","+country;
		Map<String, Object> map=new HashMap<String, Object>();
		try{
			int ct=0;
			List<Map<String, Object>> list = dbUtil.getData("select * from ijoin_order where login_id=?", new Object[]{login_id});
			if(null!=list && list.size()>0){
				order = Integer.parseInt(list.get(0).get("ORDER_ID").toString());
				ct =dbUtil.saveData("update IJOIN_ORDER set ship_address=?,invoice=?,order_date=SYSDATE,delivery_status=?,pl_name=?,amount=?,order_status=?,alt_number=?,cust_name=?,postal_code=?,delivery_date=SYSDATE,act_date=SYSDATE where login_id=?", new Object[]{address,order,"DELIVERYSET",pkg_name,amount,order_status,msisdn,name,postcode,login_id});
			}else{
				ct =dbUtil.saveData("insert into IJOIN_ORDER (order_id,ship_address,invoice,order_date,delivery_status,login_id,pl_name,amount,order_status,alt_number,cust_name,postal_code,delivery_date,act_date) values(?,?,?,SYSDATE,?,?,?,?,?,?,?,?,SYSDATE,SYSDATE)", new Object[]{order,address,order,"DELIVERYSET",login_id,pkg_name,amount,order_status,msisdn,name,postcode});
			}
			if(ct>0){
				sendOTP(msisdn, "Order placed successfully. Order id is: "+order);
				map.put("OrderId", order);
				map.put("Status", "SUCCESS");
			}else{
				map.put("Status", "FAILURE");
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Ijoin-1010", "",0);
			log.error("Ijoin-1010- GenericServiceImpl.createOrder() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.createOrder - END");
		}
		return map;
	}
	
	@Override
	public Map<String,Object> etobeeServiceCreate(String msisdn, String email_id, String ship_addr, String city, String state, String country, String postcode, String order_id) {
		HttpEntity entity = null;
		CloseableHttpClient  client = null;
		HttpPost request = null;
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			client = httpConn.getHttpClient();
			JsonObject jMain = orderObject(msisdn, email_id, ship_addr, city, state, country, postcode, order_id);
			String input = jMain.toString();
			log.info(input);
			StringEntity se = new StringEntity(input);
		    se.setContentType("application/json");
		    request = new HttpPost("http://api.staging.etobee.com/api/create_order");
		    //request = new HttpPost("http://api.etobee.com:3001/api/create_order");
		    request.setEntity(se);
		    String authString = "indosat@etobee.com" + ":" + "indosat123";
		    //String authString = "tommy.dinuri@indosatooredoo.com" + ":" + "IndosatOoredoo123";
			log.info("auth string: " + authString);
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			String authStringEnc = new String(authEncBytes);
			log.info("GenericServiceImpl.etobeeServiceCreate() authStringEnc "+authStringEnc);
		    request.addHeader("Authorization", "Basic "+authStringEnc);
		    request.addHeader("Content-Type", "application/json");
		    HttpResponse response = client.execute(request);
		    int code = response.getStatusLine().getStatusCode();
		    log.info("Etobee Response Code" +code);
		    log.info(Arrays.toString(request.getAllHeaders()));
		    if(code==200){
		     entity = response.getEntity();
		     String content = EntityUtils.toString(entity);
		     log.info("GenericServiceImpl.etobeeServiceCreate() success "+content);
		     JsonObject jObj = (new JsonParser()).parse(content).getAsJsonObject();
		     if(jObj.has("status") && jObj.has("order_number") ){
		    	dbUtil.saveData("update ijoin_order set tracking_num=? where order_id=?", new Object[]{jObj.get("order_number").getAsString(),order_id});
		    	map.put("Status", "SUCCESS");
		     }
		    }else{
		     entity = response.getEntity();
		     String content = EntityUtils.toString(entity);
		     log.info("GenericServiceImpl.etobeeServiceCreate() failed "+content);
		    }
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Ijoin-1011", "Failed To create order on Etobee Server",0);
			log.error("Ijoin-1011- GenericServiceImpl.etobeeServiceCreate() ce "+IndoUtil.getFullLog(ce));
		}finally{
	    	  log.info("***********Closing Streams********");
	    	  try {
	    		  	EntityUtils.consumeQuietly(entity);
		    		  	if(null!=request){
		    		  		request.releaseConnection();
		    		  	}if(null!=client){
		    		  		client.close();
		    		  	}
					} catch (IOException e) {
					log.info("Ijoin-1011- Exception Occured "+e);
				}
	      } 
		return map;
	}
	private static JsonObject orderObject(String msisdn, String email_id, String ship_addr, String city, String state, String country, String postcode, String order_id){
		JsonObject jMain = new JsonObject();
		jMain.addProperty("select_driver", false);
		jMain.addProperty("web_order_id", order_id);
		
		JsonObject jSender = new JsonObject();
		jSender.addProperty("name", "INDOSAT");
		jSender.addProperty("mobile", "+6285000000");
		jSender.addProperty("email", "care@indosatooredoo.com");
		jMain.add("sender", jSender);
		
		JsonObject jOrigin = new JsonObject();
		jOrigin.addProperty("address", "Gedung Indosat");
		jOrigin.addProperty("city", "Jakarta");
		jOrigin.addProperty("state", "Jakarta");
		jOrigin.addProperty("country", "Indonesia");
		jOrigin.addProperty("postcode", "11410");
		jMain.add("origin", jOrigin);
		jMain.addProperty("origin_comments", "");
		
		JsonObject jrecipient = new JsonObject();
		jrecipient.addProperty("name", "Prakash");
		jrecipient.addProperty("mobile", msisdn);
		jrecipient.addProperty("email", email_id);
		jMain.add("recipient", jrecipient);
		
		JsonObject jdestination = new JsonObject();
		jdestination.addProperty("address",ship_addr);
		jdestination.addProperty("city", city);
		jdestination.addProperty("state", state);
		jdestination.addProperty("country",country);
		jdestination.addProperty("postcode", postcode);
		jMain.add("destination", jdestination);
		
		JsonObject jpackage = new JsonObject();
		jpackage.addProperty("quantity", 1);
		jpackage.addProperty("transaction_value", 0);
		jpackage.addProperty("insurance", false);
		jpackage.addProperty("photo", "");
		jpackage.addProperty("size", "Motorcycle");
		jpackage.addProperty("weight", 1);
		jpackage.addProperty("volume", 0.1);
		jpackage.addProperty("note", "Sim Card Package");
		jpackage.addProperty("width", 1);
		jpackage.addProperty("height", 1);
		jpackage.addProperty("length", 1);
		jpackage.addProperty("locker_dropoff", false);
		jMain.add("package", jpackage);			
		jMain.addProperty("merchant_id", "");
		jMain.addProperty("paid_by_parent", true);
		jMain.addProperty("isCOD",false);
		jMain.addProperty("pickup_type", "regular");
		jMain.addProperty("pickup_time", 1475402400);
		jMain.addProperty("destination_comments", "Call 123 if nobody is in");
		
		return jMain;
	}
	@Override
	public Map<String, Object> trackDeliveryStatus(String order_id){
		HttpEntity entity = null;
		CloseableHttpClient  client = null;
		HttpPost request = null;
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			client = httpConn.getHttpClient();
			JsonObject jMain = new JsonObject();
			jMain.addProperty("order_number", order_id);
			//jMain.addProperty("web_order_id", "12345678");
			
			String input = jMain.toString();
			log.info(input);
			StringEntity se = new StringEntity(input);
		    se.setContentType("application/json");
		    request = new HttpPost("http://api.staging.etobee.com/api/get_order_status");
		   // request = new HttpPost("//http://api.etobee.com:3001/api/get_order_status");
		    request.setEntity(se);
		    String authString = "indosat@etobee.com" + ":" + "indosat123";
			log.info("auth string: " + authString);
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			String authStringEnc = new String(authEncBytes);
		    request.addHeader("Authorization", "Basic "+authStringEnc);
		    request.addHeader("Content-Type", "application/json");
		    HttpResponse response = client.execute(request);
		    int code = response.getStatusLine().getStatusCode();
		    log.info("Etobee Response Code" +code);
		    log.info(Arrays.toString(request.getAllHeaders()));
		    
		    if(code==200){
		     entity = response.getEntity();
		     String content = EntityUtils.toString(entity);
		     JsonObject jObj = (new JsonParser()).parse(content).getAsJsonObject();
		     if(jObj.has("status") && jObj.has("order_status") ){
		    	dbUtil.saveData("update ijoin_order set delivery_status=? where tracking_num=?", new Object[]{jObj.get("order_status").getAsString(),order_id}); 
		     }
		     map.put("Status", "SUCCESS");
		     log.info("GenericServiceImpl.trackDeliveryStatus() success "+content);
		    }else{
		     entity = response.getEntity();
		     String content = EntityUtils.toString(entity);
		     log.info("GenericServiceImpl.trackDeliveryStatus() failed "+content);
		    }
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Ijoin-1012", "Unable to Track Order, Order not Found",0);
			log.error("Ijoin-1012- GenericServiceImpl.trackDeliveryStatus() ce "+IndoUtil.getFullLog(ce));
		}finally{
	    	  log.info("***********Closing Streams********");
	    	  try {
	    		  	EntityUtils.consumeQuietly(entity);
		    		  	if(null!=request){
		    		  		request.releaseConnection();
		    		  	}if(null!=client){
		    		  		client.close();
		    		  	}
					} catch (IOException e) {
					log.info("Ijoin-1012- Exception Occured "+e);
				}
	      } 
		return map;
	}
	
	@Override
	public Map<String, Object> cancelOrder(String order_id){
		HttpEntity entity = null;
		CloseableHttpClient  client = null;
		HttpPost request = null;
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			client = httpConn.getHttpClient();
			JsonObject jMain = new JsonObject();
			jMain.addProperty("order_number", order_id);
			//jMain.addProperty("web_order_id", "12345678");
			
			String input = jMain.toString();
			log.info(input);
			StringEntity se = new StringEntity(input);
		    se.setContentType("application/json");
		    request = new HttpPost("http://api.staging.etobee.com/api/cancel_order");
		    //request = new HttpPost("http://api.etobee.com:3001/api/cancel_order");
		    request.setEntity(se);
		    String authString = "indosat@etobee.com" + ":" + "indosat123";
			log.info("auth string: " + authString);
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			String authStringEnc = new String(authEncBytes);
		    request.addHeader("Authorization", "Basic "+authStringEnc);
		    request.addHeader("Content-Type", "application/json");
		    HttpResponse response = client.execute(request);
		    int code = response.getStatusLine().getStatusCode();
		    log.info("Etobee Response Code" +code);
		    log.info(Arrays.toString(request.getAllHeaders()));
		    
		    if(code==200){
		     entity = response.getEntity();
		     String content = EntityUtils.toString(entity);
		     JsonObject jObj = (new JsonParser()).parse(content).getAsJsonObject();
		     if(jObj.has("status") && jObj.has("order_status") ){
		    	dbUtil.saveData("update ijoin_order set delivery_status=? where tracking_num=?", new Object[]{jObj.get("order_status").getAsString(),order_id}); 
		     }
		     map.put("Status", "SUCCESS");
		     log.info("GenericServiceImpl.updateDeliveryStatus() success "+content);
		    }else{
		     entity = response.getEntity();
		     String content = EntityUtils.toString(entity);
		     log.info("GenericServiceImpl.updateDeliveryStatus() failed "+content);
		    }
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Ijoin-1013", "Unable to Update Order",0);
			log.error("Ijoin-1013- GenericServiceImpl.updateDeliveryStatus() ce "+IndoUtil.getFullLog(ce));
		}finally{
	    	  log.info("***********Closing Streams********");
	    	  try {
	    		  	EntityUtils.consumeQuietly(entity);
		    		  	if(null!=request){
		    		  		request.releaseConnection();
		    		  	}if(null!=client){
		    		  		client.close();
		    		  	}
					} catch (IOException e) {
					log.info("Ijoin-1013- Exception Occured "+e);
				}
	      } 
		return map;
	}
	
	@Override
	public Map<String, Object> updateDeliveryStatus(String order_id){
		Map<String, Object> map=new HashMap<String, Object>();
		String orderStatus=null;
		try{
			Map<String, Object> status = trackDeliveryStatus(order_id);
				if(IndoUtil.isSuccess(status)){
					List<Map<String, Object>> data = dbUtil.getData("select delivery_status from ijoin_order where order_id=?", new Object[]{order_id});
					String etobeeOrderStatus = status.get("order_status").toString();
						log.info(etobeeOrderStatus);
					if(data.size()>0){
						orderStatus = data.get(0).get("delivery_status").toString();
						if(!orderStatus.equals(etobeeOrderStatus)){
							int ct =dbUtil.saveData("update ijoin_order SET delivery_status=? where order_id=?", new Object[]{order_id});
							if(ct>0){
								map.put("Status", "SUCCESS");
							}
						}
					}
				}
				else{
					IndoUtil.populateErrorMap(map, "Ijoin-1014", "Unable to Update Order",0);
					map.put("Status", "FAILURE");
				}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Ijoin-1014", "Unable to Update Order",0);
			log.error("Ijoin-1014- GenericServiceImpl.createOrder() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.createOrder - END");
		}
		return map;
	}
	
	@Override
	public Map<String, Object> getLocation(String col,String val, String where) {
		Map<String, Object> map=new HashMap<String, Object>();
		try{
			if(StringUtils.isEmpty(col)){//all provice
				List<String> list = dbUtil.getSingleCol("select DISTINCT province from ijoin_location",new Object[]{});
				if(list!= null){
					map.put("Province", list);
					map.put("Status","SUCCESS");
				}
				return map;
			}else{
				List<String> list = dbUtil.getSingleCol("select DISTINCT "+col+" from ijoin_location where "+where+"=?",new Object[]{val});
				if(list!= null){
					map.put(col, list);
					map.put("Status","SUCCESS");
				}
				return map;
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Ijoin-1015", "Location not found",0);
			log.error("Ijoin-1015- GenericServiceImpl.uploadImage() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.uploadImage - END");
		}
		return map;
	} 
	
	
	@Override
	public Map<String, Object> autoLogin(String user) {
		log.info("GenericServiceImpl.autoLogin() - START");
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			List<Map<String, Object>> data1 = dbUtil.getData("select * from IJOIN_USER WHERE userid=?",new Object[] { BlowFish.decrypt(user)});
			if (null != data1 && data1.size()>0) {
				data.put("Status", "SUCCESS");
				data1.get(0).remove("PASSWORD");
				data.put("User", data1.get(0));
			} else {
				IndoUtil.populateErrorMap(data, "Ijoin-010", "User not found.", 0);
			}
		} catch (EmptyResultDataAccessException ra) {

		} catch (Exception ce) {
			IndoUtil.populateErrorMap(data, "Ijoin-1016", "User not found.", 0);
			log.error("Ijoin-1016- GenericServiceImpl.autoLogin() " + ce);
		} finally {
			log.info("GenericServiceImpl.autoLogin() - END");
		}
		return data;
	}
	
	@Override
	public Map<String, Object> orderStatus(String oid) {
		log.info("GenericServiceImpl.orderStatus() - START");
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			List<Map<String, Object>> data1 = dbUtil.getData("select * from IJOIN_USER WHERE order_id=?",new Object[] { BlowFish.decrypt(oid)});
			if (null != data1 && data1.size()>0) {
				data.put("Status", "SUCCESS");
				data.put("Order", data1.get(0));
			} else {
				IndoUtil.populateErrorMap(data, "Ijoin-010", "Order not found.", 0);
			}
		} catch (EmptyResultDataAccessException ra) {

		} catch (Exception ce) {
			IndoUtil.populateErrorMap(data, "Ijoin-1017", "Order not found.", 0);
			log.error("Ijoin-1017- GenericServiceImpl.orderStatus() " + ce);
		} finally {
			log.info("GenericServiceImpl.orderStatus() - END");
		}
		return data;
	}
	
	@Override
	public Map<String, Object> getScore(String category,String type,String offerType) {
		Map<String, Object> map=new HashMap<String, Object>();
		Map<String, Object> data=new HashMap<String, Object>();
		Set layer = new HashSet();
		int ct = 0;
		try{
			if(StringUtils.isEmpty(category) && StringUtils.isEmpty(type) && StringUtils.isEmpty(offerType) ){
				List<Map<String, Object>> list = dbUtil.getData("select layer_1,category from ijoin_question order by SEQ asc ",new Object[]{});
				if(list!= null){
					map.put("Question_1",list.get(0).get("LAYER_1"));
					for(int i=0;i<list.size();i++){
						list.get(i).remove("LAYER_1");
					 	Map<String,Object> data1=(Map<String, Object>)list.get(i);
					 	for(Map.Entry<String, Object> entry: data1.entrySet()){
							layer.add(entry.getValue());
						}
					}
					map.put("Category", layer);
					map.put("Status","SUCCESS");
				}
			}
			else if(!StringUtils.isEmpty(category) && StringUtils.isEmpty(type)&& StringUtils.isEmpty(offerType)){
				List<Map<String, Object>> list = dbUtil.getData("select distinct layer_2,type from ijoin_question where category=? ",new Object[]{category});
					if(list!= null){
						map.put("Question_2",list.get(0).get("LAYER_2"));
						for(int i=0;i<list.size();i++){
							list.get(i).remove("LAYER_2");
						 	Map<String,Object> data1=(Map<String, Object>)list.get(i);
						 	for(Map.Entry<String, Object> entry: data1.entrySet()){
								layer.add(entry.getValue());
							}
						}
						map.put("Type", layer);
						map.put("Status","SUCCESS");
					}
			}else if(StringUtils.isEmpty(category) && !StringUtils.isEmpty(type)&& StringUtils.isEmpty(offerType)){
				List<Map<String, Object>> list = dbUtil.getData("select distinct layer_3,offerType from ijoin_question WHERE type=?",new Object[]{type});
				if(list!= null){
					map.put("Question_3",list.get(0).get("LAYER_3"));
					for(int i=0;i<list.size();i++){
						list.get(i).remove("LAYER_3");
					 	Map<String,Object> data1=(Map<String, Object>)list.get(i);
					 	for(Map.Entry<String, Object> entry: data1.entrySet()){
							layer.add(entry.getValue());
						}
					}
					map.put("OfferType", layer);
					map.put("Status","SUCCESS");
				}
			}
				else if(StringUtils.isEmpty(category) && !StringUtils.isEmpty(type)&& !StringUtils.isEmpty(offerType)){
					List<Map<String, Object>> list = dbUtil.getData("select score,product from ijoin_question where offerType=? and type=?",new Object[]{offerType,type});
					if(list!= null){
						map.put("Score", list);
						map.put("Status","SUCCESS");
					}
				}
			}catch(Exception ce){
				IndoUtil.populateErrorMap(map, "Ijoin-1018", "Scores not available",0);
				log.error("Ijoin-1018- GenericServiceImpl.getLocation() ce "+IndoUtil.getFullLog(ce));
			}finally{
				log.info("GenericServiceImpl.getLocation - END");
			}
			return map;
		} 	
	
	
	@Override
	public Map<String, Object> loginUser(String userid, String password, String social_id) {
		Map<String, Object> map= new HashMap<String, Object>();
		List<Map<String, Object>> list= null;
		try{
			if(!StringUtils.isEmpty(userid) && !StringUtils.isEmpty(password) && StringUtils.isEmpty(social_id) ){
				list = dbUtil.getData("select USERID,MSISDN,SOCIAL_ID,CUST_SOURCE,to_char(LAST_LOGINDATE,'dd-mm-yyyy hh:mi') LAST_LOGINDATE,to_char(REG_DATE,'dd-mm-yyyy hh:mi') REG_DATE from ijoin_user where userid=? and password=? ",new Object[]{userid,password});
				if(list.size()> 0 ){
					map.put("Status", "SUCCESS");
					map.put("UserData", list.get(0));
					list.get(0).remove("PASSWORD");
				}else{
					map.put("Status", "FAILURE");
					IndoUtil.populateErrorMap(map, "Ijoin-1019", "Please check the userid and password.",0);
					return map;
				}
			}
			else if(!StringUtils.isEmpty(userid) && StringUtils.isEmpty(password) && !StringUtils.isEmpty(social_id)){
				list = dbUtil.getData("select USERID,MSISDN,SOCIAL_ID,CUST_SOURCE,to_char(LAST_LOGINDATE,'dd-mm-yyyy hh:mi') LAST_LOGINDATE,to_char(REG_DATE,'dd-mm-yyyy hh:mi') REG_DATE from ijoin_user where userid=? and social_id=? ",new Object[]{userid,social_id});
				if(list.size()> 0 ){
					map.put("Status", "SUCCESS");
					map.put("UserData", list.get(0));
					list.get(0).remove("PASSWORD");
				}else{
					map.put("Status", "FAILURE");
					IndoUtil.populateErrorMap(map, "Ijoin-1019", "Please check the userid and password.",0);
				}
			}
			else{
				map.put("Status", "FAILURE");
				IndoUtil.populateErrorMap(map, "Ijoin-1019", "Please check the userid and password.",0);
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Ijoin-1019", "Please check the userid and password.",0);
			log.error("Ijoin-1019- GenericServiceImpl.loginUser() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.loginUser - END");
		}
		return map;
	}
	@Override
	public Map<String, Object> retrieveOrder(String emailId) {
		log.info("GenericServiceImpl.retrieveOrder() - START");
		Map<String, Object> map=new HashMap<String, Object>();	
		List<Map<String, Object>> app = null;
		try{
			app = dbUtil.getData("SELECT ORDER_ID,INVOICE,SHIP_ADDRESS,to_char(ORDER_DATE,'dd-mm-yyyy HH24:mi') ORDER_DATE,TRACKING_NUM,DELIVERY_STATUS,PL_NAME,LOGIN_ID,to_char(DELIVERY_DATE,'dd-mm-yyyy HH24:mi') DELIVERY_DATE,AMOUNT,ORDER_STATUS,AGENTID from ijoin_order where login_id=?", new Object[]{emailId});
			if(app.size()>0){
				map.put("Orders", app);
				map.put("Status", "SUCCESS");
			}else{
				map.put("Status", "Failure");
				IndoUtil.populateErrorMap(map, "Ijoin-1020", "No Data Found",0);
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Ijoin-1020", "No Data Found",0);
			log.error("Ijoin-1020- GenericServiceImpl.retrieveOrder() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.retrieveOrder() - END");
		}
		return map;
	}
	@Override
	public Map<String, Object> userProfile(String id) {
		log.info("GenericServiceImpl.userProfile() - START");
		Map<String, Object> map=new HashMap<String, Object>();	
		List<Map<String, Object>> app = null;
		try{
			app = dbUtil.getData("select a.userid, a.msisdn,b.name,b.id_number, to_char(b.dob,'dd-mm-yyyy') dob,b.address,b.act_date,b.reg_date,c.PL_NAME from IJOIN_USER a,IJOIN_USER_PROFILE b, ijoin_order c where a.userid=b.userid and a.USERID=c.LOGIN_ID and a.userid=?", new Object[]{id});
			if(app.size()>0){
				map.put("Profile", app);
				map.put("Status", "SUCCESS");
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Ijoin-1021", "No Data Found .",0);
			log.error("Ijoin-1021- GenericServiceImpl.userProfile() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.userProfile() - END");
		}
		return map;
	}  
	@Override
	public Map<String, Object> userImage(String id) {
		log.info("GenericServiceImpl.userImage() - START");
		Map<String, Object> map=new HashMap<String, Object>();	
		List<Map<String, Object>> app = null;
		try{
			app = dbUtil.getData("select * from IJOIN_USER_IMAGES where userid=?", new Object[]{id});
			if(app.size()>0){
				map.put("Profile", app);
				map.put("Status", "SUCCESS");
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Ijoin-1022", "Image Not Found",0);
			log.error("Ijoin-1022- GenericServiceImpl.userImage() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.userImage() - END");
		}
		return map;
	}
	@Override
	public Map<String, Object> fetchPack(String id) {
		log.info("GenericServiceImpl.fetchPack() - START");
		Map<String, Object> map=new HashMap<String, Object>();	
		List<Map<String, Object>> app = null;
		try{
			app = dbUtil.getData("select * from IJOIN_PACKS where PACK_NAME_EN=?", new Object[]{id});
			if(app.size()>0){
				map.put("Pack", app);
				map.put("Status", "SUCCESS");
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Ijoin-1023", "No Pack Found",0);
			log.error("Ijoin-1023- GenericServiceImpl.fetchPack() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.fetchPack() - END");
		}
		return map;
	}  
	
	@Override
	public Map<String, Object> forgotPwd(String emailId) {
		log.info("GenericServiceImpl.forgotPwd() - START");
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> app = null;
		try {
			app = dbUtil.getData("SELECT * from IJOIN_USER where userid=?", new Object[] { emailId });
			if (app.size() > 0) {
				int newPwd = IndoUtil.randInt(9999, 9999999);
				int ct = dbUtil.saveData("update IJOIN_USER set password=? where userid=?",
						new Object[] { newPwd, emailId });
				if (ct > 0) {
					map.put("Status", "SUCCESS");
					map.put("newPwd", newPwd);
				} else {
					map.put("Status", "FAILTURE");
					IndoUtil.populateErrorMap(map,"Ijoin-n-1024", "Please provide the proper Input", 0);
				}
			} else {
				IndoUtil.populateErrorMap(map,"Ijoin-n-1024", "Email not registered.", 0);
			}
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(map,"Ijoin-n-1024", "Unable to process the request", 0);
			log.error("Ijoin-1024- GenericServiceImpl.forgotPwd() ce " + IndoUtil.getFullLog(ce));
		} finally {
			log.info("GenericServiceImpl.forgotPwd() - END");
		}
		return map;
	}
	
	@Override
	public Map<String, Object> validateUser(String uid, String pwd) {
		log.info("GenericServiceImpl.validateUser(-).............start.");
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			data = dbUtil.getRow("select * from IJOIN_ADMIN_USERS where userid=? and password=?", new Object[] {uid,pwd});
			String LoginID= data.get("USERID").toString();
			if (data.get("PASSWORD").toString().equals(pwd)) {
				data.put("LoginID",LoginID);
				data.put("Status", "SUCCESS");
				return data;
			}
		} catch (EmptyResultDataAccessException ce) {
			IndoUtil.populateErrorMap(data, "Indo-1025", "Subscriber number not found. Please register.", 0);
		} catch (Exception ce) {
			log.info("Indo-1025- GenericServiceImpl.validateUser() e - " + ce);
			IndoUtil.populateErrorMap(data, "Indo-1025", "Subscriber number not found. Please register.", 0);
		}
		log.info("GenericServiceImpl.validateUser(-).............end. map :" + data);
		return data;
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
		String url = "https://sservin.indosat.com:8443/MTPush?uid=MOBagent&pwd=Magentpwd&serviceid=25250001034004&msisdn="+ msisdn + "&sms=" + msg + "&smstype=0";
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
				IndoUtil.populateErrorMap(data, "Ijoin-1026", "Ijoin-1025", 0);
			}
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(data, "Ijoin-1026", "Ijoin-1026", 0);
			log.error("Ijoin-1026- GenericServiceImpl.sendOTP() " + IndoUtil.getFullLog(ce));
		} finally {
			log.info("GenericServiceImpl.sendOTP() - END");
		}
		return data;
	}
	@Override
	public Map<String, Object> orderSearchDetails(String rownum1, String rownum2, String searchKey) {
		Map<String, Object> map=new HashMap<String, Object>();
		int count =0;
		try{
			/*dbUtil.getData("select * from ijoin_order where order_id=? or invoice=? or msisdn=? or order_status=? or delivery_status=?,",new Object[]{searchKey,searchKey,searchKey,searchKey.searchKey})*/
			searchKey = searchKey.toUpperCase();
			count =Integer.parseInt(dbUtil.getData("select count(*) count from ijoin_order a,IJOIN_USER b where a.login_id= b.userid and (a.order_id=? or a.invoice=? or b.msisdn=? or upper(a.order_status)=? or upper(a.delivery_status)=?)", new Object[]{searchKey,searchKey,searchKey,searchKey,searchKey}).get(0).get("COUNT").toString());
			List<Map<String, Object>> list =dbUtil.getData("select * from (select a.ORDER_ID,a.INVOICE,b.MSISDN,a.order_status,a.DELIVERY_STATUS, rownum r from ijoin_order a,ijoin_user b where a.login_id= b.userid and a.login_id= b.userid and (a.order_id=? or a.invoice=? or b.msisdn=? or upper(a.order_status)=? or upper(a.delivery_status)=?) ORDER BY a.order_date DESC) where r >? and r <=?", 
					new Object[]{searchKey,searchKey,searchKey,searchKey,searchKey,rownum1,rownum2});
			List<List<Object>> rows = new ArrayList<List<Object>>();
			for(Map<String,Object> m : list){
				List<Object> row = new ArrayList<Object>();
				row.add(m.get("ORDER_ID"));
				row.add(m.get("INVOICE"));
				row.add(m.get("MSISDN"));
				row.add(m.get("ORDER_STATUS"));
				row.add(m.get("DELIVERY_STATUS"));
				rows.add(row);
			}
			if(list.size()>0){
				map.put("recordsTotal", count);
				map.put("recordsFiltered", count);
				map.put("data", rows);
				map.put("Status", "SUCCESS");
			}else{
				map.put("recordsTotal", count);
				map.put("recordsFiltered", count);
				map.put("data", new ArrayList<Object>());
				map.put("Status", "SUCCESS");
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Ijoin-1027", "order details Not found",0);
			log.error("Ijoin-1027- GenericServiceImpl.orderdetails() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.orderdetails - END");
		}
		return map;
	}
	@Override
	public Map<String, Object> getGallery(String col, String val, String where) {
		Map<String, Object> map=new HashMap<String, Object>();
		try{
			if(StringUtils.isEmpty(col)){
				List<String> list = dbUtil.getSingleCol("select DISTINCT province from ijoin_gallery",new Object[]{});
				if(list!= null){
					map.put("Province", list);
					map.put("Status","SUCCESS");
				}
				return map;
			}else{
				List<String> list = dbUtil.getSingleCol("select DISTINCT "+col+" from ijoin_gallery where "+where+"=?",new Object[]{val});
				if(list!= null){
					map.put(col, list);
					map.put("Status","SUCCESS");
				}
				return map;
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Ijoin-1028", "Gallery for this province not found",0);
			log.error("Ijoin-1028- GenericServiceImpl.getGallery() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.getGallery - END");
		}
		return map;
	}
	@Override
	public void updloadPDF(String msisdn, String fname) {
		try{
			dbUtil.saveData("insert into ijoin_pdf(msisdn,url) values(?,?)", new Object[]{msisdn,"http://10.128.168.2/pdf/"+fname});
		}catch(Exception ce){
			log.info("GenericServiceImpl.updloadPDF() ce "+IndoUtil.getFullLog(ce));
		}
	}
	
	@Override
	public Map<String, Object> getImage(String image) {
		log.info("GenericServiceImpl.retrievePacks() - START");
		Map<String, Object> map=new HashMap<String, Object>();	
		String loc= "var/image/";
		try{
			if(null!= image || image !=""){
				String image_1 = image;	
				String location= loc.concat(image_1);
				dbUtil.saveData("insert into saturn_test (image) values(?)", new Object[]{location});
				map.put("location",location);
				map.put("Status", "SUCCESS");
			}
			}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Ijoin-1002", "No Data Found",0);
			log.error("Ijoin-1002- GenericServiceImpl.retrievePacks() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.retrievePacks - END");
		}
	return map;
	}
	
}