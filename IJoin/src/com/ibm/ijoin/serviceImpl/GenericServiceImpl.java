package com.ibm.ijoin.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.hibernate.validator.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ibm.ijoin.service.GenericService;
import com.ibm.ijoin.service.HttpConnService;
import com.ibm.ijoin.util.Base64Converter;
import com.ibm.ijoin.util.DBUtil;
import com.ibm.ijoin.util.IndoSQLConstants;
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
	
	/*@Autowired
	Base64Converter base64;*/
	
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
			IndoUtil.populateErrorMap(map, "Saturn-1024", "",0);
			log.error("Saturn-2051- GenericServiceImpl.retrieveApplication() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.retrieveApplication() - END");
		}
		return map;
	}
	
	

	@Override
	public Map<String, Object> retrievePacks(String points) {
		log.info("GenericServiceImpl.retrievePacks() - START");
		Map<String, Object> map=new HashMap<String, Object>();	
		Map<String, Object> map1= null;
		ArrayList<Object> list= new ArrayList<>();
		String pack_name="";
		try{
			if(StringUtils.isNumeric(points)){
				int point= Integer.parseInt(points);
				/*
				if(point>=10 && point<=50 ){
					pack_name="Freedom Combo M";
				}else if(point>=51 &&point<=100){
					pack_name="Freedom Combo L";
				}else if(point>=101 &&  point<=199){
					pack_name="Freedom Combo XL";
				}else if(point>=200){
					pack_name="Freedom Combo XXL";
				}*/
				List<Map<String, Object>> app = dbUtil.getData("select package from ijoin_recomended_packs where min_val<=? and max_val>=?", new Object[]{point,point});
				pack_name = app.get(0).get("package").toString();
				List<Map<String, Object>> applist = dbUtil.getData("SELECT * from ijoin_packs where pack_name_en=?", new Object[]{pack_name});
				for (Map<String, Object> data : applist) {
					list.add(data);
				}
				map.put("Packs", list);
				map.put("Status", "SUCCESS");
			}else{
				map.put("error-msg","Please enter the valid input.");
				map.put("Status", "FAILTURE");
			}
			}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Saturn-1024", "",0);
			log.error("Saturn-2051- GenericServiceImpl.retrievePacks() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.retrievePacks - END");
		}
	return map;
	}
	
	@Override
	public Map<String, Object> registerUser( String email,String password) {
		log.info("GenericServiceImpl.regUser() - START");
		Map<String, Object> map=new HashMap<String, Object>();	
		try{
			int ct=0;
				if(!StringUtils.isEmpty(email)){
					List list=dbUtil.getData("select * from ijoin_user where userid=?", new Object[]{email});
					if(list.size()>0){
						map.put("Status", "FAILURE");
						map.put("ErrorDescription", "Email already registered.");
						return map;	
					}
					else{
						ct = dbUtil.saveData("insert into ijoin_user(userid,password) VALUES(?,?)", new Object[]{email,password});
							if(ct>0){
								map.put("Status", "SUCCESS");
							}else{
								map.put("Status", "FAILURE");
							}
					  }
			   }	
			
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Saturn-1024", "",0);
			log.error("Saturn-2051- GenericServiceImpl.registerUser() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.registerUser - END");
		}
		log.info("GenericServiceImpl.registerUser() - START");
		return map;	
	}

	
	@Override
	public java.util.Map<String,Object> updateProfile(String email, String name, String cust_img, String id_img, String gender, String id_number, String dob, String address) {
	Map<String, Object> map=new HashMap<String, Object>();	
		
		try{
			int ct=0;
			if(!StringUtils.isEmpty(email)){
					ct = dbUtil.saveData("update ijoin_user set name=?,CUST_IMG=?,ID_IMG=?,GENDER=?,ID_NUMBER=?,DOB=?,ADDRESS=?,ACT_DATE=sysdate where userid=?"
							, new Object[]{name,cust_img,id_img,gender,id_number,dob,address,email});
			}	
			log.info("update count" + ct);
			map.put("Status", "SUCCESS");
			
			
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Saturn-1024", "",0);
			log.error("Saturn-2051- GenericServiceImpl.updateProfile() ce "+IndoUtil.getFullLog(ce));
		}
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
			IndoUtil.populateErrorMap(map, "Saturn-1024", "",0);
			log.error("Saturn-2051- GenericServiceImpl.uploadImage() ce "+IndoUtil.getFullLog(ce));
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
			IndoUtil.populateErrorMap(map, "Saturn-1024", "",0);
			log.error("Saturn-2051- GenericServiceImpl.retrievedetails() ce "+IndoUtil.getFullLog(ce));
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
			list =dbUtil.getData("select a.*,b.* from ijoin_order a,ijoin_user b where a.ORDER_ID =? and a.login_id=b.login_id", new Object[]{ORDER_ID});
			log.info("GenericServiceImpl.getOrderDetails() size "+list.size());
			if(list.size()>0){
				map.put("Details", list);
				map.put("Status", "SUCCESS");
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Saturn-1024", "",0);
			log.error("Saturn-2051- GenericServiceImpl.getOrderDetails() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.getOrderDetails - END");
		}
		return map;
	}
	
	@Override
	public Map<String, Object> orderdetails(String rownum1,String rownum2 ) {
		Map<String, Object> map=new HashMap<String, Object>();
		int count =0;
		try{
			count =Integer.parseInt(dbUtil.getData("select count(*) count from ijoin_order", new Object[]{}).get(0).get("COUNT").toString());
			List<Map<String, Object>> list =dbUtil.getData("select * from (select a.ORDER_ID,a.INVOICE,b.MSISDN,b.ACT_STATUS,a.DELIVERY_STATUS, rownum r from ijoin_order a,ijoin_user b ORDER BY order_date DESC) where r >=? and r <=?", new Object[]{rownum1,rownum2});
			List<List<Object>> rows = new ArrayList<List<Object>>();
			for(Map<String,Object> m : list){
				List<Object> row = new ArrayList<Object>();
				row.add(m.get("ORDER_ID"));
				row.add(m.get("INVOICE"));
				row.add(m.get("MSISDN"));
				row.add(m.get("ACT_STATUS"));
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
			IndoUtil.populateErrorMap(map, "Saturn-1024", "",0);
			log.error("Saturn-2051- GenericServiceImpl.orderdetails() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.orderdetails - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> updateOrder(String order_id,String msisdn, String act_status, String icc_id) {
		Map<String, Object> map=new HashMap<String, Object>();
		List list=null;
		try{
			list = dbUtil.getData("select login_id from ijoin_order where order_id=? ", new Object[]{order_id});
			Map<String, Object> map1=(Map<String, Object>) list.get(0);
			String loginID=(String) map1.get("LOGIN_ID");
			int count = dbUtil.saveData("update ijoin_user set msisdn=?,act_status=?,icc_id=? where login_id=?",new Object[]{msisdn,act_status,icc_id,loginID} );
			if(count>0){
				map.put("Status", "SUCCESS");
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Saturn-1024", "",0);
			log.info("Saturn-2051- GenericServiceImpl.updateOrder() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.updateOrder - END");
		}
		return map;
	}

	@Override
	public Map<String, Object> validateUser(String uid, String pwd) {
		log.info("GenericServiceImpl.validateUser(-).............start.");
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			
			data = dbUtil.getRow(IndoSQLConstants.GET_USER, new Object[] {uid,uid});

			log.info("db password :" + data.get("USER_PASSWORD").toString() + "     user enter password : " + pwd);
			String LoginID= data.get("USERID").toString();
			if (data.get("USER_PASSWORD").toString().equals(pwd)) {
				data.put("LoginID",LoginID);
				data.put("Status", "SUCCESS");
				return data;
			}
		} catch (EmptyResultDataAccessException ce) {
			IndoUtil.populateErrorMap(data, "Indo-100", "Subscriber number not found. Please register.", 0);
		} catch (Exception ce) {
			log.info("Indo-100- GenericServiceImpl.validateUser() e - " + ce);
			IndoUtil.populateErrorMap(data, "Indo-100", "Faile to login.", 0);
		}
		log.info("GenericServiceImpl.validateUser(-).............end. map :" + data);
		return data;
	}


@Override
	public Map<String, Object> registerUser(String uid, String pwd, String email, String name, String dob, String pob,String addr) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			int ct = dbUtil.saveData("Insert into im2_users(userid,user_password, email_id,firstname,dob,pob,area_id) values(?,'IM21234',?,?,to_date(?,'dd-mm-yyyy'))",
					  new Object[] { uid, email, name, dob, pob, addr });
			if (ct > 0) {
				data.put("Status", "SUCCESS");
			} else {
				IndoUtil.populateErrorMap(data, "Indo-101", "Unable to register now. Please try again later.", 0);
			}
		} catch (EmptyResultDataAccessException ce) {
			IndoUtil.populateErrorMap(data, "Indo-101", "Unable to register now. Please try again later.", 0);
		} catch (Exception ce) {
			log.info("Indo-100- GenericServiceImpl.registerUser() e - " + ce);
			IndoUtil.populateErrorMap(data, "Indo-100", ce.getClass().getSimpleName(), 0);
		}
		return data;
	}

	@Override
	public Map<String, Object> forgot(String userid, String emailID) {
		log.info("GenericServiceImpl.forgot(-)  start");
		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> vList = new ArrayList<Map<String, Object>>();
		try {
			vList = dbUtil.getData("select * from IM2_users where userId=? ",new Object[] {userid});
			if (vList != null && vList.size() > 0) {
				data.put("USER_PASSWORD",vList.get(0).get("USER_PASSWORD"));
				data.put("Status","SUCCESS");
			}else{
				data.put("Status","FAILTURE");
			}
						 
		} catch (EmptyResultDataAccessException ce) {
			IndoUtil.populateErrorMap(data, "Indo-101", "Unable to Validate User now. Please try again later.", 0);
		} catch (Exception ce) {
			log.info("Indo-100- GenericServiceImpl.forgot() e - " + ce);
			IndoUtil.populateErrorMap(data, "Indo-100", ce.getClass().getSimpleName(), 0);
		}
		return data;
	}

	@Override
	public Map<String, Object> changePassword(String newpassword,  String userId ) {
			Map<String, Object> data = new HashMap<String, Object>();
			log.info("GenericServiceImpl.changePassword(-) start ");
			int count = dbUtil.saveData("update IM2_USERS SET USER_PASSWORD=?  where  userId=?",new Object[] { newpassword, userId });
			log.info("GenericServiceImpl.changePassword(-)  n :"+count);
		if (count >= 0) {
			data.put("Status", "SUCCESS");
		} else {
			data.put("Status", "FAILTURE");
		}
			log.info("GenericServiceImpl.forget(-) end :");
		return data;
	}

	@Override
	public List<Map<String, Object>> getPackage(String pkg_code, String pkg_category, String pkg_group) {
		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			list = dbUtil.getData("select * from SATURN_PACKAGE_INFO where PACKAGE_CODE =? and PACKAGE_CATEGORY =? and PACKAGE_GROUP =? ",new Object[] { pkg_code, pkg_category, pkg_group });
			if (list != null) {
				data.put("Status", "SUCCESS");
				return list;
			}else
				IndoUtil.populateErrorMap(data, "Indo-101", "Unable to get Package. Please try again later.", 0);
			} catch (EmptyResultDataAccessException ce) {
				IndoUtil.populateErrorMap(data, "Indo-100", "Unable to get Package. Please try again later..", 0);
			} catch (Exception ce) {
				log.info("Indo-100- GenericServiceImpl.getPackage() e - " + ce);
				IndoUtil.populateErrorMap(data, "Indo-100", "Fail to get Package.", 0);
			}
		return list;
	}

	@Override
	public List<Map<String, Object>> getallPackage() {
		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			list = dbUtil.getData("select DISTINCT package_code , package_group, PACKAGE_CATEGORY from SATURN_PACKAGE_INFO",new Object[] {});
			if (list != null) {
				data.put("Status", "SUCCESS");
			} else
				IndoUtil.populateErrorMap(data, "Indo-101", "Unable to get Package. Please try again later.", 0);
		} catch (EmptyResultDataAccessException ce) {
			IndoUtil.populateErrorMap(data, "Indo-100", "Unable to get Package. Please try again later.", 0);
		} catch (Exception ce) {
			log.info("Indo-100- GenericServiceImpl.getallPackage() e - " + ce);
			IndoUtil.populateErrorMap(data, "Indo-100", "Faile to login.", 0);
		}
		return list;
	}

	@Override
	public Map<String, Object> updatePackag(String pkg_name_en, String tariff, String benefit_en, String benefit_id,String gift_flag, String Buy_flag, String buy_extra_flag, String param, String unre_param, String keyword,String unreg_keyword, String pkg_name_id) {
		Map<String, Object> data = new HashMap<String, Object>();
			try {
			int n = dbUtil.saveData("update SATURN_PACKAGE_INFO SET PACKAGE_NAME_EN =?,TARIFF =?,BENEFIT_EN =?,BENEFIT_ID =?,GIFT_FLAG =?,BUY_FLAG =?,BUY_EXTRA_FLAG =?,PARAM =?,KEYWORD =?,UNREG_KEYWORD =?,UNREG_PARAM =?,PACKAGE_NAME_ID =?",
									new Object[] { pkg_name_en, tariff, benefit_en, benefit_id, gift_flag, Buy_flag, buy_extra_flag,param, unre_param, unreg_keyword, pkg_name_id });
			if (n != 0) {
				data.put("Status", "SUCCESS");
			} else
				data.put("Status", "FAILURE");
		} catch (Exception ce) {
			log.info("Indo-100- GenericServiceImpl.updatePackage() e - " + ce);
		}
		return data;
	}

	@Override
	public Map<String, Object> newStore(String id, String name, String city, String address, String Longitude,String LattiTude, String StoreDescription) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			int n = dbUtil.saveData("Insert into SATURN_STORE_DATA (ID,NAME,CITY,ADDRESS,LONGITUDE,LATTITUDE,STORE_DESC) values (?,?,?,?,?,?,?)",
						new Object[] { id, name, city, address, Longitude, LattiTude, StoreDescription });
				if (n != 0)
					data.put("Status", "SUCCESS");
				else
					data.put("Status", "FAILURE");
		} catch (Exception ce) {
			log.info("Indo-100- GenericServiceImpl.newStore() e - " + ce);
		}
		return data;
	}
	
	@Override
	public Map<String, Object> newPackage2(String PACKAGE_NAME, String Packagegroup, String TARIFF, String QUOTA,String GIFT_FLAG, String BUY_FLAG, String BUY_EXTRA_FLAG, String PARAM, String COMMENTS, String PACKAGE_CATEGORY,String UNREG_KEYWORD, String UNREG_PARAM, String SERVICECLASS,String DESCRIPTION,String KEYWORD){
			Map<String, Object> data = new HashMap<String, Object>();
		try {
			int n = dbUtil.saveData("Insert into SATURN_PACKAGES_INFO (DESCRIPTION,KEYWORD,TARIFF,QUOTA,PACKAGE_GROUP,PACKAGE_NAME,GIFT_FLAG,BUY_FLAG,BUY_EXTRA_FLAG,PARAM,COMMENTS,PACKAGE_CATEGORY,UNREG_KEYWORD,UNREG_PARAM,SERVICECLASS) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
					new Object[] { DESCRIPTION,KEYWORD,TARIFF,QUOTA,Packagegroup,PACKAGE_NAME,GIFT_FLAG,BUY_FLAG,BUY_EXTRA_FLAG,PARAM,COMMENTS,PACKAGE_CATEGORY,UNREG_KEYWORD,UNREG_PARAM,SERVICECLASS});
			if (n!=0)
				data.put("Status", "SUCCESS");
			else
				data.put("Status", "FAILURE");	
		} catch (Exception ce) {
			log.info("Indo-100- GenericServiceImpl.newPackage2() e - " + ce);
		}
		return data;
	}

	@Override
	public Map<String, Object> EditStore(String id, String name, String city, String address, String Longitude,String LattiTude, String StoreDescription) {
			Map<String, Object> data = new HashMap<String, Object>();
			try {
				int n = dbUtil.saveData("update SATURN_STORE_DATA  SET city =?, address =?, LONGITUDE=?, LATTITUDE =?, STORE_DESC =? where id=? and name=?",
						new Object[] { city, address, Longitude, LattiTude, StoreDescription, id, name });
				if (n > 0)
					data.put("Status", "SUCCESS");
				else
					data.put("Status", "FAILURE");
			}
		 catch (Exception ce) {
			log.info("Indo-100- GenericServiceImpl.EditStore() e - " + ce);
		}
		return data;
	}
	
	@Override
	public Map<String, Object> NewSspPackage(String package_cod, String keyword, String shortCode) {
			log.info("GenericServiceImpl.UpdateSspPackage_code(-). start ");
			Map<String, Object> data = new HashMap<String, Object>();
				try {
					int n = dbUtil.saveData("Insert into SATURN_PACKAGE_ACT (Pack_code,keyword,short_code) values (?,?,?)",new Object[] { package_cod, keyword, shortCode });
					if (n != 0)
						data.put("Status", "SUCCESS");
					else
						data.put("Status", "FAILURE");
				}catch (Exception ce) {
					log.info("Indo-100- GenericServiceImpl.UpdateSspPackage_code() e - " + ce);
				}
					log.info("GenericServiceImpl.UpdateSspPackage_code(-). end ");
		return data;
	}

	@Override
public java.util.Map<String,Object> NewPackage1(String PackageType, String PackageCategory, String description, String packageCategoryID, String catSeq, byte[] imagebyte) {	
		log.info("GenericServiceImpl.NewPackage1(-). Start ");
		Map<String, Object> data = new HashMap<String, Object>();
		try { 
			if(catSeq.equals(""))
				                catSeq="0";
			String bannerImage=Base64Converter.encodeImage(imagebyte);
			int n = dbUtil.saveData("Insert into SATURN_PACKAGE_CATEGORY (PACKAGE_TYPE,PACKAGE_CATEGORY,DESCRIPTION,BANNER_IMAGE,PACKAGE_CATEGORY_ID,CAT_SEQ) values (?,?,?,?,?,?)",
					new Object[] {PackageType,PackageCategory,description, bannerImage,packageCategoryID,Integer.parseInt(catSeq)});
				if (n != 0)
					data.put("Status", "SUCCESS");
				else
					data.put("Status", "FAILURE");
	
			} catch (Exception ce) {
			log.info("Indo-100- GenericServiceImpl.NewPackage1() e - " + ce);
		}
		 log.info("GenericServiceImpl.NewPackage1(-). End ");
		return data;
	}

	/*updated code */
	  	@Override
	public java.util.Map<String,Object> EditPackage1(String edit_Package_Type, String edit_Package_Category, String edit_description, String edit_package_CategoryID, String edit_cat_Seq, byte[] Banner_image) {
			log.info("GenericServiceImpl.EditPackage1(-). Start ");
			Map<String, Object> data = new HashMap<String, Object>();
			try {
				if(edit_cat_Seq.equals(""))
					            edit_cat_Seq="0";
				String banner_image=Base64Converter.encodeImage(Banner_image);
				int n = dbUtil.saveData("update SATURN_PACKAGE_CATEGORY  SET DESCRIPTION =?, BANNER_IMAGE =? ,PACKAGE_CATEGORY_ID=? ,CAT_SEQ=? where PACKAGE_TYPE=? and PACKAGE_CATEGORY=?",
					new Object[] { edit_description, banner_image, edit_package_CategoryID,Integer.parseInt(edit_cat_Seq),edit_Package_Type,edit_Package_Category });
				if (n>=0)
					data.put("Status", "SUCCESS");
				else
					data.put("Status", "FAILURE");
			} catch (Exception ce) {
			log.info("Indo-100- GenericServiceImpl.EditPackage1() e - " + ce);
			}
			log.info("GenericServiceImpl.EditPackage1(-). End ");
		return data;
	}

	  
/*// Old code	
	@Override
	public java.util.Map<String,Object> EditPackage1(String edit_Package_Type, String edit_Package_Category, String edit_description, String edit_package_CategoryID, String edit_cat_Seq, String edit_Banner_Image) {
			log.info("GenericServiceImpl.EditPackage1(-). Start ");
			Map<String, Object> data = new HashMap<String, Object>();
			try {
				if(edit_cat_Seq.equals(""))
					            edit_cat_Seq="0";
				
		
				int n = dbUtil.saveData("update SATURN_PACKAGE_CATEGORY  SET DESCRIPTION =?, BANNER_IMAGE =? ,PACKAGE_CATEGORY_ID=? ,CAT_SEQ=? where PACKAGE_TYPE=? and PACKAGE_CATEGORY=?",
					new Object[] { edit_description, null, edit_package_CategoryID,Integer.parseInt(edit_cat_Seq),edit_Package_Type,edit_Package_Category });
					log.info("int n : "+n);
				if (n>=0)
					data.put("Status", "SUCCESS");
				else
					data.put("Status", "FAILURE");
			} catch (Exception ce) {
			log.info("Indo-100- GenericServiceImpl.EditPackage1() e - " + ce);
			}
			log.info("GenericServiceImpl.EditPackage1(-). End ");
		return data;
	}
*/
	  	
	@Override
	public Map<String, Object> EditOffer(String offerID, String package_Code, String edit_Tariff,String edit_OfferNameID, String edit_OfferNameEN, String edit_BenefitID, byte[] imageEn,String edit_BenefitEN, String edit_Keyword, String edit_Param, String edit_offerLink, String edit_OfferType,String edit_CustomerType, byte[] imageID) {
		log.info("GenericServiceImpl.EditOffer(-). Start");
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			/*
			String BannerEN = Base64Converter.encodeImage(imageEn);
			String BannerID = Base64Converter.encodeImage(imageID);
			*/
			int n = dbUtil.saveData("update SATURN_OFFERS  SET OFFER_TYPE=?, TARIFF =? , OFFER_NAME_ID=?, OFFER_NAME_EN =?  ,  BENEFIT_ID =? ,BENEFIT_EN=?, KEYWORD=? ,PARAM=? ,OFFER_LINK=?,  CUST_TYPE=? ,BANNER_IMAGE_ID=?, BANNER_IMAGE_EN=? where OFFER_ID=? and PACKAGE_CODE=?",
				new Object[] { edit_OfferType,edit_Tariff,edit_OfferNameID, edit_OfferNameEN, edit_BenefitID,edit_BenefitID, edit_Keyword,edit_Param,edit_offerLink,edit_CustomerType, Base64Converter.encodeImage(imageID),Base64Converter.encodeImage(imageEn),offerID,package_Code});
			log.info("int n :"+n);
			if (n > 0)
				data.put("Status", "SUCCESS");
			else
				data.put("Status", "FAILURE");
			} catch (Exception ce) {
			log.info("Indo-100- GenericServiceImpl.EditOffer() e - " + ce);
		}
		log.info("GenericServiceImpl.EditOffer(-). End");
	return data;

		
		
	}	
	
	
	@Override
	public Map<String, Object> EditOffer(String OfferID, String package_Code, String edit_Tariff, String edit_OfferNameID, String edit_OfferNameEN, String edit_BenefitID, String edit_BannerImageEN, String edit_BenefitEN, String edit_Keyword, String edit_Param, String edit_offerLink, String edit_OfferType, String edit_CustomerType, String edit_Banner_Image_ID) {
			log.info("GenericServiceImpl.EditOffer(-). Start");
			Map<String, Object> data = new HashMap<String, Object>();
			
			try {/*
				String BannerEN = Base64Converter.encodeImage(packageInfor.getBannerImageEN().getBytes());
				String BannerID = Base64Converter.encodeImage(packageInfor.getBannerImageID().getBytes());
				*/
				int n = dbUtil.saveData("update SATURN_OFFERS  SET OFFER_TYPE=?, TARIFF =? , OFFER_NAME_ID=?, OFFER_NAME_EN =?  ,  BENEFIT_ID =? ,BENEFIT_EN=?, KEYWORD=? ,PARAM=? ,OFFER_LINK=?,  CUST_TYPE=? ,BANNER_IMAGE_ID=?, BANNER_IMAGE_EN=? where OFFER_ID=? and PACKAGE_CODE=?",
					new Object[] { edit_OfferType,edit_Tariff,edit_OfferNameID, edit_OfferNameEN, edit_BenefitID,edit_BenefitID, edit_Keyword,edit_Param,edit_offerLink,edit_CustomerType, null, null,OfferID,package_Code});
				log.info("int n :"+n);
				if (n > 0)
					data.put("Status", "SUCCESS");
				else
					data.put("Status", "FAILURE");
				} catch (Exception ce) {
				log.info("Indo-100- GenericServiceImpl.EditOffer() e - " + ce);
			}
			log.info("GenericServiceImpl.EditOffer(-). End");
		return data;
	}


	@Override
	public Map<String, Object> EditSSPOffer(String pack_code, String keyword, String short_code) {
			log.info("GenericServiceImpl.EditSSPOffer(-). Start");
			Map<String, Object> data = new HashMap<String, Object>();
			try {
				int n = dbUtil.saveData("update SATURN_PACKAGE_ACT  SET  KEYWORD =? , SHORT_CODE=?  where PACK_CODE=? ",new Object[] { keyword,short_code,pack_code });
				if (n != 0)
				data.put("Status", "SUCCESS");
			else
				data.put("Status", "FAILURE");
		} catch (Exception ce) {
			log.info("Indo-100- GenericServiceImpl.EditSSPOffer() e - " + ce);
		}
			log.info("GenericServiceImpl.EditSSPOffer(-). End");
		return data;
	}
  
	@Override
	public Map<String, Object> NewOffer(String offer_id, String pack_code, String tariff, String offer_Name_ID,String offer_Name_EN, String banefit_ID, byte[] imageEn, String banefit_EN, String keyword, String param,String offer_Link, String offer_Type, String customer_Type, byte[] imageID) {

		Map<String, Object> data = new HashMap<String, Object>();
		try {
			String BannerEN = Base64Converter.encodeImage(imageEn);
			String BannerID = Base64Converter.encodeImage(imageID);
		
			int n = dbUtil.saveData("Insert into SATURN_OFFERS (OFFER_ID,PACKAGE_CODE,TARIFF,OFFER_NAME_ID,OFFER_NAME_EN,BENEFIT_ID,BENEFIT_EN,KEYWORD,PARAM,OFFER_LINK,OFFER_TYPE,CUST_TYPE,BANNER_IMAGE_ID,BANNER_IMAGE_EN) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
				new Object[] { offer_id,pack_code,tariff,offer_Name_ID,offer_Name_EN, banefit_ID,banefit_EN,keyword,param,offer_Link,offer_Type,customer_Type,BannerID,BannerEN });
			log.info("int n : " + n);
			if (n != 0)
				data.put("Status", "SUCCESS");
			else
				data.put("Status", "FAILURE");
		}catch(Exception ce) {
		log.info("Indo-100- GenericServiceImpl.NewOffer() e - " + ce);
		}
		log.info("GenericServiceImpl.NewOffer(-). End");	
	return data;
		
	}
	
	
	// old one 
	@Override
	public java.util.Map<String,Object> NewOffer(String offer_id, String pack_code, String tariff, String offer_Name_ID, String offer_Name_EN, String banefit_ID, String banner_Image_En, String banefit_EN, String keyword, String param, String offer_Link, String offer_Type, String customer_Type, String banner_Image_ID){
			
			Map<String, Object> data = new HashMap<String, Object>();
			try {
				/*String BannerEN = Base64Converter.encodeImage(packageInfor.getBannerImageEN().getBytes());
				String BannerID = Base64Converter.encodeImage(packageInfor.getBannerImageID().getBytes());
				*/
				int n = dbUtil.saveData("Insert into SATURN_OFFERS (OFFER_ID,PACKAGE_CODE,TARIFF,OFFER_NAME_ID,OFFER_NAME_EN,BENEFIT_ID,BENEFIT_EN,KEYWORD,PARAM,OFFER_LINK,OFFER_TYPE,CUST_TYPE,BANNER_IMAGE_ID,BANNER_IMAGE_EN) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
					new Object[] { offer_id,pack_code,tariff,offer_Name_ID,offer_Name_EN, banefit_ID,banefit_EN,keyword,param,offer_Link,offer_Type,customer_Type,null,null });
				log.info("int n : " + n);
				if (n != 0)
					data.put("Status", "SUCCESS");
				else
					data.put("Status", "FAILURE");
			}catch(Exception ce) {
			log.info("Indo-100- GenericServiceImpl.NewOffer() e - " + ce);
			}
			log.info("GenericServiceImpl.NewOffer(-). End");	
		return data;
	}

	@Override
	public List<Map<String, Object>> getAllOffer() {
				log.info("GenericServiceImpl.getAllOffer(-). Start");
				Map<String, Object> data = new HashMap<String, Object>();
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			try {
				list = dbUtil.getData("select DISTINCT Offer_ID from SATURN_OFFERS where Offer_ID IS NOT NULL", new Object[] {});
				log.info("offer List : " + list);
				if (list != null) {
					data.put("Status", "SUCCESS");
				} else
					IndoUtil.populateErrorMap(data, "Indo-101", "Unable to get Package. Please try again later.", 0);
			} catch (Exception ce) {
				IndoUtil.populateErrorMap(data, "Indo-100", "Unable to get Package. Please try again later.", 0);
			}
			log.info("GenericServiceImpl.getAllOffer(-). End");					
		return list;
	}

	@Override
	public List<Map<String, Object>> getAllStore() {
			log.info("GenericServiceImpl.getAllStore(-). Start");
			Map<String, Object> data = new HashMap<String, Object>();
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			list = dbUtil.getData("select  DISTINCT ID  from SATURN_STORE_DATA where ID IS NOT NULL", new Object[] {});
			log.info("offer List : " + list);
			if (list != null) {
				data.put("Status", "SUCCESS");
			} else
				IndoUtil.populateErrorMap(data, "Indo-101", "Unable to get Store. Please try again later.", 0);
		}catch (Exception ce) {
			log.info("Indo-100- GenericServiceImpl.getAllStore() e - " + ce);
			IndoUtil.populateErrorMap(data, "Indo-100", "Unable to get Store. Please try again later.", 0);
		}
		log.info("GenericServiceImpl.getAllStore(-). End");
		return list;
	}

	@Override
	public Map<String, Object> getAllOfferSSP() {
		log.info("GenericServiceImpl.getAllOfferSSP(-). Start");
		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			list = dbUtil.getData("select DISTINCT  PACK_CODE  from SATURN_PACKAGE_ACT where PACK_CODE IS NOT NULL ", new Object[] {});
			if (list.size()>0) {
				data.put("Status", "SUCCESS");
				data.put("list",list);
			} else{
				data.put("Status", "FAILTURE");
				IndoUtil.populateErrorMap(data, "Indo-101", "Record not found.  Please try again later.", 0);
			}
		 } catch (Exception ce) {
			 log.info("Indo-100- GenericServiceImpl.getAllOfferSSP() e - " + ce);
			 IndoUtil.populateErrorMap(data, "Indo-100", "Unable to GET SSP Offer.", 0);
		}
		log.info("GenericServiceImpl.getAllOfferSSP(-). End");	
		return data;
	}
	
	public List getPackage1(String PACKAGE_TYPE, String PACKAGE_CATEGORY) {
		log.info("GenericServiceImpl.getPackage1(-). Start");
		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			 list = dbUtil.getData("select * from SATURN_PACKAGE_CATEGORY where PACKAGE_TYPE =? and PACKAGE_CATEGORY =? ",new Object[] { PACKAGE_TYPE, PACKAGE_CATEGORY });
			 log.info("getPackage1-------" + list);
			if (list.size()>0) {
				data.put("Status", "SUCCESS");
			}else
				IndoUtil.populateErrorMap(data, "Indo-101", "Unable to get Package. Please try again later.", 0);
		} catch (Exception ce) {
			log.info("Indo-100- GenericServiceImpl.getOffer() e - " + ce);			
			IndoUtil.populateErrorMap(data, "Indo-100", "Unable to get Package. Please try again later.", 0);
		}
		log.info("GenericServiceImpl.getPackage1(-). End");
		return list;
	}
	
	@SuppressWarnings("unused")
	public List getOffer(String OfferID, String package_Code) {
			Map<String, Object> data = new HashMap<String, Object>();
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			try {
				list = dbUtil.getData("select * from SATURN_OFFERS where Offer_ID =? and PACKAGE_Code =? ",new Object[] { OfferID, package_Code });
			if (list != null) {
				data.put("Status", "SUCCESS");
			} else
				IndoUtil.populateErrorMap(data, "Indo-101", "Unable to get Package. Please try again later.", 0);
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(data, "Indo-100", "Unable to get Package. Please try again later.", 0);
			log.info("Indo-100- GenericServiceImpl.getOffer() e - " + ce);
		}
		log.info("GenericServiceImpl.getOffer(-). End");
		return list;
	}
	

	 
	public List getPackage2(String PackageName, String package_Group) {
			log.info("GenericServiceImpl.getPackage2(-). Start");
			Map<String, Object> data = new HashMap<String, Object>();
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			list = dbUtil.getData("select * from SATURN_PACKAGES_INFO where PACKAGE_NAME =? and PACKAGE_GROUP =? ",new Object[] { PackageName, package_Group });
			if (list != null) {
				data.put("Status", "SUCCESS");
			}else
				IndoUtil.populateErrorMap(data, "Indo-101", "Unable to get Package. Please try again later.", 0);
		} catch (Exception ce) {
			log.info("Indo-100- GenericServiceImpl.getPackage2() e - " + ce);
			IndoUtil.populateErrorMap(data, "Indo-100", "Subscriber number not found. Please register.", 0);
	}
		log.info("GenericServiceImpl.getPackage2(-). End");	
		return list;
 }


	
	public List getStore(String ID, String Name) {
			log.info("GenericServiceImpl.getStore(-). Start");
			Map<String, Object> data = new HashMap<String, Object>();
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			list = dbUtil.getData("select * from SATURN_STORE_DATA where ID =? and Name=? ",new Object[] {ID,Name});
			if (list != null) {
				data.put("Status", "SUCCESS");
			}else
				IndoUtil.populateErrorMap(data, "Indo-101", "Unable to get Package. Please try again later.", 0);
		} catch (Exception ce) {
			log.info("Indo-100- GenericServiceImpl.getStore() e - " + ce);
			IndoUtil.populateErrorMap(data, "Indo-100", "Unable to get Package. Please try again later.", 0);
		}
		log.info("GenericServiceImpl.getStore(-). End");	
		return list;

	}

	@Override
	public Map<String, Object> deleteOffer(String OfferID, String package_Code) {
			log.info("GenericServiceImpl.deleteOffer(-). Start");
			Map<String, Object> data = new HashMap<String, Object>();
		try{
			int n = dbUtil.saveData("delete from SATURN_OFFERS where Offer_ID =? and PACKAGE_Code =? ",new Object[] { OfferID, package_Code });
			if (n != 0)
				data.put("Status", "SUCCESS");
			else
				data.put("Status", "FAILURE");
		} catch (Exception ce) {
			log.info("Indo-100- GenericServiceImpl.NewOffer() e - " + ce);
		}
		log.info("GenericServiceImpl.deleteOffer(-). End");
		return data;
	}

	@Override
	public Map<String, Object> deletePackage1(String PACKAGE_TYPE, String PACKAGE_CATEGORY) {
				log.info("GenericServiceImpl.deletePackage1(-). Start");
				Map<String, Object> data = new HashMap<String, Object>();
			try {
				int n = dbUtil.saveData("delete from  SATURN_PACKAGE_CATEGORY where PACKAGE_TYPE =? and PACKAGE_CATEGORY =? ",new Object[] { PACKAGE_TYPE, PACKAGE_CATEGORY });
				log.info("int n : " + n);
				if (n != 0)
					data.put("Status", "SUCCESS");
				else
					data.put("Status", "FAILURE");
				}catch (Exception ce) {
					log.info("Indo-100- GenericServiceImpl.deletePackage1() e - " + ce);
			}
			log.info("GenericServiceImpl.deletePackage1(-). End");			
		return data;
	}

	@Override
	public Map<String, Object> deletePackage2(String PACKAGE_Name, String PACKAGE_group){
			log.info("GenericServiceImpl.deletePackage2(-). Start");
			Map<String, Object> data = new HashMap<String, Object>();
		try{
			int n = dbUtil.saveData("delete from SATURN_PACKAGES_INFO where PACKAGE_NAME =? and PACKAGE_GROUP=? ",new Object[] {PACKAGE_Name,PACKAGE_group});
			log.info("int n : " + n);
			if (n != 0)
				data.put("Status", "SUCCESS");
			else
				data.put("Status", "FAILURE");
			}catch (Exception ce) {
			log.info("Indo-100- GenericServiceImpl.NewOffer() e - " + ce);
		}
			log.info("GenericServiceImpl.deletePackage2(-). End");
		return data;

	}

	@Override
	public Map<String, Object> deleteSSPOffer(String Pack_code) {
			log.info("GenericServiceImpl.deleteSSPOffer(-). Start");
			Map<String, Object> data = new HashMap<String, Object>();
		try{
			int n = dbUtil.saveData("delete from SATURN_PACKAGE_ACT where PACK_Code =? ",new Object[] {Pack_code});
			if (n > 0)
				data.put("Status", "SUCCESS");
			else
				data.put("Status", "FAILURE");
		} catch (Exception ce) {
			log.info("Indo-100- GenericServiceImpl.deleteSSPOffer() e - " + ce);
		}
		log.info("GenericServiceImpl.deleteSSPOffer(-). End");
		return data;

	}
	
	@Override
	public Map<String, Object> getSSPOffer(String pkg_code) {
		log.info("-----------------START------------------------");
			log.info("GenericServiceImpl.getSSPOffer(-). Start");
			Map<String, Object> data = new HashMap<String, Object>();
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			list = dbUtil.getData("select * from SATURN_PACKAGE_ACT where PACK_CODE =?",new Object[] {pkg_code});
			if (list.size()>0) {
				data.put("list", list);
				data.put("Status", "SUCCESS");
			}else{
				data.put("Status", "FAILTURE");
				IndoUtil.populateErrorMap(data, "Indo-101", "Unable to get Package. Please try again later.", 0);
					
			}
			}catch (Exception ce) {
			log.info("Indo-100- GenericServiceImpl.getSSPOffer() e - " + ce);
			IndoUtil.populateErrorMap(data, "Indo-100","Unable to get Package. Please try again later.", 0);
		}
		log.info("GenericServiceImpl.getSSPOffer(-). End");
		return data;
	}

	@Override
	public Map<String, Object> deleteStore(String ID, String NAme) {
			log.info("GenericServiceImpl.deleteStore(-). Start");
			Map<String, Object> data = new HashMap<String, Object>();
		try {
			int n = dbUtil.saveData("delete from SATURN_STORE_DATA  where id=? and name=?",new Object[] {ID,NAme});
			log.info("int n : "+ n);
			if (n != 0)
				data.put("Status", "SUCCESS");
			else
				data.put("Status", "FAILURE");
		} catch (Exception ce) {
			log.info("Indo-100- GenericServiceImpl.deleteStore() e - " + ce);
		}
		log.info("GenericServiceImpl.deleteStore(-). End");
		return data;
	}

	@Override
	public List<Map<String, Object>> getAllPackage1() {
			log.info("GenericServiceImpl.getAllPackage1(-). Start");
			Map<String, Object> data = new HashMap<String, Object>();
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			list = dbUtil.getData("select DISTINCT PACKAGE_TYPE  from SATURN_PACKAGE_CATEGORY where PACKAGE_TYPE IS NOT NULL", new Object[] {});
			log.info("offer List : " + list);
			if (list != null) {
				data.put("Status", "SUCCESS");
			} else
				IndoUtil.populateErrorMap(data, "Indo-101", "Unable to get Package. Please try again later.", 0);
		} catch (Exception ce) {
			log.info("Indo-100- GenericServiceImpl.getAllPackage1() e - " + ce);
			IndoUtil.populateErrorMap(data, "Indo-100", "Unable to get Package. Please try again later.", 0);
		}
		log.info("GenericServiceImpl.getAllPackage1(-). End");
		return list;
	}
	
	@Override
	public List<Map<String, Object>> getAllPackage2() {
			log.info("GenericServiceImpl.getAllPackage2(-). Start");
			Map<String, Object> data = new HashMap<String, Object>();
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			list = dbUtil.getData("select DISTINCT PACKAGE_NAME from SATURN_PACKAGES_INFO where PACKAGE_NAME IS NOT NULL", new Object[] {});
			log.info("offer List : " + list);
			if (list != null) {
				data.put("Status", "SUCCESS");
			} else
				IndoUtil.populateErrorMap(data, "Indo-101", "Unable to get Package. Please try again later.", 0);

		} catch (Exception ce) {
			log.info("Indo-100- GenericServiceImpl.getAllPackage2() e - " + ce);
			IndoUtil.populateErrorMap(data, "Indo-100", "Unable to get Package. Please try again later.", 0);
		}
		log.info("GenericServiceImpl.getAllPackage2(-). End");
		return list;
	}
	
	
	public Map<String, Object> EditPackage2(String PACKAGE_NAME, String Packagegroup, String TARIFF, String QUOTA,String GIFT_FLAG, String BUY_FLAG, String BUY_EXTRA_FLAG, String PARAM, String COMMENTS, String PACKAGE_CATEGORY,String UNREG_KEYWORD, String UNREG_PARAM, String SERVICECLASS,String DESCRIPTION,String KEYWORD){
		log.info("GenericServiceImpl.EditPackage2(-). End");
			Map<String, Object> data = new HashMap<String, Object>();
		try{
			int n = dbUtil.saveData("update SATURN_PACKAGES_INFO  SET DESCRIPTION =?, KEYWORD =?, TARIFF=?, QUOTA =?, GIFT_FLAG=?,BUY_FLAG=?,BUY_EXTRA_FLAG=?, PARAM=?,COMMENTS=?,PACKAGE_CATEGORY=?,UNREG_KEYWORD=? ,UNREG_PARAM=?,SERVICECLASS=? where PACKAGE_NAME=? and PACKAGE_GROUP=?",
					new Object[] { DESCRIPTION, KEYWORD, TARIFF, QUOTA,GIFT_FLAG,BUY_FLAG,BUY_EXTRA_FLAG,PARAM,COMMENTS,PACKAGE_CATEGORY,UNREG_KEYWORD,UNREG_PARAM,SERVICECLASS, PACKAGE_NAME, Packagegroup});
			log.info("int n : "+n);
			if (n> 0)
				data.put("Status", "SUCCESS");
			else
				data.put("Status", "FAILURE");
		}catch(Exception ce){
			log.info("Indo-100- GenericServiceImpl.EditPackage2() e - " + ce);
			IndoUtil.populateErrorMap(data, "Indo-100", "Unable to get Package2. Please try again later.", 0);
		}
		log.info("GenericServiceImpl.EditPackage2(-). Start");
		return data;
		}
   
	@Override
	public List getOfferAjaxOfferID(String OfferID) {
		log.info("GenericServiceImpl.getOfferAjaxOfferID(-). Start");
		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	try{
		list = dbUtil.getData("select DISTINCT PACKAGE_CODE from SATURN_OFFERS where Offer_ID =? and PACKAGE_CODE IS NOT NULL  ",new Object[] { OfferID });
		log.info("offer List : " + list);
		if (list != null) {
			data.put("Status", "SUCCESS");
		} else
			IndoUtil.populateErrorMap(data, "Indo-101", "Unable to get Package. Please try again later.", 0);

	} catch (Exception ce) {
		log.info("Indo-100- GenericServiceImpl.getOfferAjaxOfferID() e - " + ce);
		IndoUtil.populateErrorMap(data, "Indo-100", "Unable to get Package. Please try again later.", 0);
	}
	log.info("GenericServiceImpl.getOfferAjaxOfferID(-). End");
	return list;

	}

	@Override
	public List getStoreAjaxID(String ID) {
		log.info("GenericServiceImpl.getStoreAjaxID(-). Start");
		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	try{
		list = dbUtil.getData("select DISTINCT Name from SATURN_STORE_DATA where ID =? and Name IS NOT NULL  ",new Object[] {ID});
		log.info("offer List : " + list);
		if (list != null) {
			data.put("Status", "SUCCESS");
		} else
			IndoUtil.populateErrorMap(data, "Indo-101", "Unable to get Package. Please try again later.", 0);

	} catch (Exception ce) {
		log.info("Indo-100- GenericServiceImpl.getStoreAjaxID() e - " + ce);
		IndoUtil.populateErrorMap(data, "Indo-100", "Unable to get Package. Please try again later.", 0);
	}
	log.info("GenericServiceImpl.getStoreAjaxID(-). End");
	return list;

	}

	
	
	@Override
	public List getPackageInformationAjax(String PackageName) {
		log.info("GenericServiceImpl.getPackage2AjaxPACKAGE_NAME(-). Start");
		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	try{
		list = dbUtil.getData("select DISTINCT PACKAGE_GROUP from SATURN_PACKAGES_INFO where package_Name =? and package_group IS NOT NULL  ",new Object[] {PackageName});
		log.info("offer List : " + list);
		if (list != null) {
			data.put("Status", "SUCCESS");
		} else
			IndoUtil.populateErrorMap(data, "Indo-101", "Unable to get Package. Please try again later.", 0);

	} catch (Exception ce) {
		log.info("Indo-100- GenericServiceImpl.getAllPackage2() e - " + ce);
		IndoUtil.populateErrorMap(data, "Indo-100", "Unable to get Package. Please try again later.", 0);
	}
	log.info("GenericServiceImpl.getAllPackage2(-). End");
	return list;

	}
	@Override
	public List getPackageCategoryAjax(String PACKAGE_TYPE) {
		log.info("GenericServiceImpl.getPackageCategoryAjax(-). Start");
		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	try{
		list = dbUtil.getData("select DISTINCT PACKAGE_CATEGORY from SATURN_PACKAGE_CATEGORY where PACKAGE_TYPE =? and PACKAGE_CATEGORY IS NOT NULL  ",new Object[] {PACKAGE_TYPE});
		log.info("offer List : " + list);
		if (list != null) {
			data.put("Status", "SUCCESS");
		} else
			IndoUtil.populateErrorMap(data, "Indo-101", "Unable to get Package. Please try again later.", 0);

	} catch (Exception ce) {
		log.info("Indo-100- GenericServiceImpl.getPackageCategoryAjax() e - " + ce);
		IndoUtil.populateErrorMap(data, "Indo-100", "Unable to get Package. Please try again later.", 0);
	}
	log.info("GenericServiceImpl.getPackageCategoryAjax(-). End");
	return list;

	}
	
	@Override
	public Map<String, Object> createOrder( String login_id, String ship_addr,String city,String state,String country,String postcode,String pkg_name,String amount) {
		int order = IndoUtil.randInt(111111, 999999);
		int track = IndoUtil.randInt(11111111, 99999999);
		String name="",email="", msisdn="";
		Map<String, Object> map=new HashMap<String, Object>();
		try{
			//Map<String, Object> status= etobeeServiceCreate(msisdn, login_id, ship_addr, city, state, country, postcode);
					List<Map<String, Object>> data = dbUtil.getData("select * from ijoin_user where userid=?", new Object[]{login_id});
					if(null!=data && data.size()>0){
						
						if(StringUtils.isEmpty(msisdn)){
							//msisdn=data.get(0).get("ALT_NUMBER").toString();
						}
						//name = data.get(0).get("NAME").toString();
						//email= data.get(0).get("EMAIL").toString();
					}
				
					int ct =dbUtil.saveData("insert into IJOIN_ORDER (order_id,ship_address,invoice,order_date,tracking_num,delivery_status,login_id,pl_name,amount) values(?,?,?,SYSDATE,?,?,?,?,?)", new Object[]{order,ship_addr,order,track,"Ready",login_id,pkg_name,amount});
					if(ct>0){
						map.put("OrderId", order);
						map.put("TrackingId", track);
						map.put("Status", "SUCCESS");
					}
					else{
						map.put("Status", "FAILURE");
					}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Saturn-1024", "",0);
			log.error("Saturn-2051- GenericServiceImpl.createOrder() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.createOrder - END");
		}
		return map;
	}
	
	@Override
	public java.util.Map<String,Object> etobeeServiceCreate(String msisdn, String email_id, String ship_addr, String city, String state, String country, String postcode, String order_id) {
	int track = IndoUtil.randInt(11111111, 99999999);
		HttpEntity entity = null;
		CloseableHttpClient  client = null;
		HttpPost request = null;
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			client = httpConn.getHttpClient();
			JsonArray jArray = new JsonArray();
			JsonObject jMain = new JsonObject();
			jMain.addProperty("select_driver", false);
			jMain.addProperty("web_order_id", order_id);
			
			JsonObject jSender = new JsonObject();
			jSender.addProperty("name", "Angela");
			jSender.addProperty("mobile", msisdn);
			jSender.addProperty("email", email_id);
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
			jrecipient.addProperty("name", "Sven");
			jrecipient.addProperty("mobile", "+62 12345678");
			jrecipient.addProperty("email", "angela@exampe.com");
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
			jpackage.addProperty("photo", "http://www.flickr.com/bird.jpg");
			jpackage.addProperty("size", "5*5");
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
			jMain.addProperty("pickup_time", 1475402400);
			jMain.addProperty("destination_comments", "Call 123 if nobody is in");
			
			/*StringEntity input = new StringEntity("\"select_driver\",\"web_order_id\",{\"sender\":[\"name\":\",\"mobile\":\",\"email\":\"],{\"origin\":[\"address\":\","
					+ "\"city\":\",\"state\":\",\"country\":\",\"postcode\":\"],\"origin_comments\":\",\"recipient\":"
					+ "{\"name\":\",\"mobile\":\",\"email\":\"},\"destination\":{\"address\":\",\"city\":\",\"state\":\",\"country\":\",\"postcode\":\"},"
					+ "\"package\":{\"quantity\":\",\"transaction_value\":\",\"insurance\":\",\"photo\":\",\"size\":\",\"weight\":\",\"volume\":\",\"note\":\",\"width\":\","
					+ "\"height\":\",\"length\":\",\"locker_dropoff\":\"},\"merchant_id\":\",\"paid_by_parent\":\",\"isCOD\":\",\"pickup_time\":\",\"destination_comments\":\"}}");
			*/

			String input = jMain.toString();
			log.info(input);
			StringEntity se = new StringEntity(input);
		    se.setContentType("application/json");
		    request = new HttpPost("http://api.staging.etobee.com/api/create_order");
		    request.setEntity(se);
		    String authString = "indosat@etobee.com" + ":" + "indosat123";
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
		     map.put("Status", "SUCCESS");
		     log.info("GenericServiceImpl.etobeeServiceCreate() success "+content);
		    }else{
		     entity = response.getEntity();
		     String content = EntityUtils.toString(entity);
		     log.info("GenericServiceImpl.etobeeServiceCreate() failed "+content);
		    }
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Saturn-1024", "",0);
			log.error("Saturn-2051- GenericServiceImpl.etobeeServiceCreate() ce "+IndoUtil.getFullLog(ce));
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
					log.info("Saturn-2051- Exception Occured "+e);
				}
	      } 
		return map;
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
			jMain.addProperty("order_number", "EDS12345678");
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
		     map.put("Status", "SUCCESS");
		     log.info("GenericServiceImpl.trackDeliveryStatus() success "+content);
		    }else{
		     entity = response.getEntity();
		     String content = EntityUtils.toString(entity);
		     log.info("GenericServiceImpl.trackDeliveryStatus() failed "+content);
		    }
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Saturn-1024", "",0);
			log.error("Saturn-2051- GenericServiceImpl.trackDeliveryStatus() ce "+IndoUtil.getFullLog(ce));
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
					log.info("Saturn-2051- Exception Occured "+e);
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
			jMain.addProperty("order_number", "EDS12345678");
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
		     map.put("Status", "SUCCESS");
		     log.info("GenericServiceImpl.updateDeliveryStatus() success "+content);
		    }else{
		     entity = response.getEntity();
		     String content = EntityUtils.toString(entity);
		     log.info("GenericServiceImpl.updateDeliveryStatus() failed "+content);
		    }
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Saturn-1024", "",0);
			log.error("Saturn-2051- GenericServiceImpl.updateDeliveryStatus() ce "+IndoUtil.getFullLog(ce));
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
					log.info("Saturn-2051- Exception Occured "+e);
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
					map.put("Status", "FAILURE");
				}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Saturn-1024", "",0);
			log.error("Saturn-2051- GenericServiceImpl.createOrder() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.createOrder - END");
		}
		return map;
	}
	
	@Override
	public Map<String, Object> getLocation(String province,String district) {
		Map<String, Object> map=new HashMap<String, Object>();
		Map<String, Object> data=new HashMap<String, Object>();
		int ct = 0;
		try{
			if(StringUtils.isEmpty(district) && StringUtils.isEmpty(province)){
				List<Map<String, Object>> list = dbUtil.getData("select DISTINCT province from ijoin_location",new Object[]{});
				for (Map<String, Object> data1 : list) {
					data = (Map<String, Object>) data1;
				}
				if(list!= null){
					map.put("Province", list);
					map.put("Status","SUCCESS");
				}
			} 
			if(StringUtils.isEmpty(district) && !StringUtils.isEmpty(province)){
				List<Map<String, Object>> list = dbUtil.getData("select districts from ijoin_location WHERE province=?",new Object[]{province});
				for (Map<String, Object> data1 : list) {
					data = (Map<String, Object>) data1;
				}
				if(list!= null){
					map.put("Districts", list);
					map.put("Status","SUCCESS");
				}
			}
			else if(StringUtils.isEmpty(province) && !StringUtils.isEmpty(district)){
				List<Map<String, Object>> list = dbUtil.getData("select city from ijoin_location where districts=?",new Object[]{district});
				for (Map<String, Object> data1 : list) {
					data = (Map<String, Object>) data1;
				}
				if(list!= null){
					map.put("Cities", list);
					map.put("Status","SUCCESS");
				}
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Saturn-1024", "",0);
			log.error("Saturn-2051- GenericServiceImpl.uploadImage() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.uploadImage - END");
		}
		return map;
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
					/*if (null == user_type || user_type.toString().equals("")) {
						Map<String, Object> ldap = ldapService.getUser(mob);
						if (IndoUtil.isSuccess(ldap) && null != ldap.get("user_type")) {
							user_type = ldap.get("user_type").toString();
							msisdns.put("user_type", ldap.get("user_type").toString());
						}
					} else {*/
						msisdns.put("user_type", user_type);
					//}
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
	public Map<String, Object> versionCheck(String currentVersion) {
		log.info("GenericServiceImpl.versionCheck() - START");
		if (null == currentVersion || currentVersion.equals("")) {
			currentVersion = "0.0";
		}
		Map<String, Object> data = new HashMap<String, Object>();
		String upgradeType = "NU";
		try {
			Map<String, Object> data1 = new HashMap<String, Object>();
			data1 = dbUtil.getRow("select * from IJOIN_APP_VERSION WHERE currentversioncode=?",new Object[] { currentVersion });
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
				IndoUtil.populateErrorMap(map, "Saturn-1024", "",0);
				log.error("Saturn-2051- GenericServiceImpl.getLocation() ce "+IndoUtil.getFullLog(ce));
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
				list = dbUtil.getData("select * from ijoin_user where userid=? and password=? ",new Object[]{userid,password});
				if(list.size()> 0 ){
					map.put("Status", "SUCCESS");
					map.put("UserData", list.get(0));
					map.remove("PASSWORD");
				}else{
					map.put("Status", "FAILURE");
					map.put("ErrorDescription", "Please check the userid and password.");
					return map;
				}
			}
			else if(!StringUtils.isEmpty(userid) && StringUtils.isEmpty(password) && !StringUtils.isEmpty(social_id)){
				list = dbUtil.getData("select * from ijoin_user where userid=? and social_id=? ",new Object[]{userid,social_id});
				if(list.size()> 0 ){
					map.put("Status", "SUCCESS");
					map.put("UserData", list.get(0));
					map.remove("PASSWORD");
				}else{
					map.put("Status", "FAILURE");
					map.put("ErrorDescription", "Please check the userid and password.");
				}
			}
			else{
				map.put("Status", "FAILURE");
				map.put("ErrorDescription", "UserId,Password or Social_id is missing");


			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Saturn-1024", "",0);
			log.error("Saturn-2051- GenericServiceImpl.loginUser() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("GenericServiceImpl.loginUser - END");
		}
		return map;
	}  
}