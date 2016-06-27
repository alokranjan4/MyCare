/**
 * 
 */
package com.ibm.indo.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.indo.service.WSSDBService;
import com.ibm.indo.util.IndoServiceProperties;
import com.ibm.indo.util.IndoUtil;
import com.ibm.indo.util.WSSDBUtil;

/**
 * @author Adeeb
 *
 */
@Service
public class WSSDBServiceImpl implements WSSDBService {
	@Autowired
	private  WSSDBUtil  geneva;
	private static Logger log = Logger.getLogger("saturnLoggerV1");
	IndoServiceProperties confProp=IndoServiceProperties.getInstance();
    Properties prop = confProp.getConfigSingletonObject();
    
	@Override
	public Map<String, Object> getUserProfile(String userName) {
		log.info("WSSDBServiceImpl.getUserProfile() - START");
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			String qry="";
			List<String> obj = new ArrayList<String>();
			if(StringUtil.isNumeric(userName)){
				qry = "SELECT * FROM WSS_USER WHERE USERNAME=? or ID=?";
				obj.add(userName);obj.add(userName);
			}else{
				qry = "SELECT * FROM WSS_USER WHERE USERNAME=?";
				obj.add(userName);
			}
			List<Map<String, Object>> data = geneva.getData(qry, obj.toArray());
			if(null!=data && data.size()>0){
				map=data.get(0);
				Map<String, Object> msisdn = getMsisdn(map.get("ID").toString());
				if(IndoUtil.isSuccess(msisdn)){
					map.put("msisdns",msisdn.get("msisdns"));
					map.put("Status", "SUCCESS");
				}else{
					map.clear();
					log.info("WSSDBServiceImpl.getUserProfile() missing msisdn for "+userName);
					IndoUtil.populateErrorMap(map, "WSS-000", "missing msisdn",0);
				}
			}else{
				map.clear();
				IndoUtil.populateErrorMap(map, "WSS-000", "No data found.",0);
			}
		}catch(Exception ce){
			map.clear();
			IndoUtil.populateErrorMap(map, "WSS-001", "No data found.",0);
			log.error("WSSDBServiceImpl.getUserProfile() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("WSSDBServiceImpl.getUserProfile() - END");
		}
		return map;
	}
	
	@Override
	public Map<String, Object> getMsisdn(String id) {
		log.info("WSSDBServiceImpl.getMsisdn() - START");
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			List<Map<String, Object>> data = geneva.getData("select * from WSS_MSISDN where USER_ID=? order by id asc", new Object[]{id});
			if(null!=data && data.size()>0){
				map.put("msisdns", data);
				map.put("Status", "SUCCESS");
				return map;
			}
		}catch(Exception ce){
			log.error("WSSDBServiceImpl.getUserProfile() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("WSSDBServiceImpl.getMsisdn() - END");
		}
		return map;
	}
	@Override
	public Map<String, Object> getMsisdnDetails(String msisdn) {
		log.info("WSSDBServiceImpl.getMsisdnDetails() - START");
		Map<String,Object> map = new HashMap<String,Object>();
		String msisdn62 = msisdn;
		String msisdn0	= msisdn;
		String msisdnNo62 = msisdn;
		String msisdnNo620 = msisdn;
		if(!msisdn62.startsWith("62")){
			msisdn62= IndoUtil.prefix62(msisdn);
		}
		if(!msisdn0.startsWith("0") && !msisdn62.startsWith("62")){
			msisdn0="0"+msisdn;
		}
		if(msisdn62.startsWith("62")){
			msisdnNo62=StringUtils.removeStart(msisdn, "62");
			//msisdnNo62=msisdn62;
		}
		if(msisdn62.startsWith("62")){
			msisdnNo620=StringUtils.removeStart(msisdn, "62");
			msisdnNo620="0"+msisdnNo620;
		}
		log.info("WSSDBServiceImpl.getMsisdnDetails() msisdn62 "+msisdn62);
		log.info("WSSDBServiceImpl.getMsisdnDetails() msisdn0 "+msisdn0);
		log.info("WSSDBServiceImpl.getMsisdnDetails() msisdnNo62 "+msisdnNo62);
		try{
			List<Map<String, Object>> data = geneva.getData("select * from WSS_MSISDN where MSISDN=? or MSISDN=? or MSISDN=? or MSISDN=? or MSISDN=?", new Object[]{msisdn, msisdn0, msisdn62, msisdnNo62,msisdnNo620});
			if(null!=data && data.size()>0){
				map.put("msisdn", data.get(0));
				map.put("Status", "SUCCESS");
			}else{
				IndoUtil.populateErrorMap(map, "WSS-003", "Not registered.",0);
			}
		}catch(Exception ce){
			log.error("WSSDBServiceImpl.getMsisdnByNum() ce "+IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map,"WSS-004","Internal server error. Unable to get details",0);
		}finally{
			log.info("WSSDBServiceImpl.getMsisdnDetails() - END");
		}
		return map;
	}
	@Override
	public Map<String, Object> getUserProfileByMsisdn(String msisdn) {
		log.info("WSSDBServiceImpl.getUserProfileByMsisdn() - START");
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			List<Map<String, Object>> data = geneva.getData("select * from WSS_MSISDN where MSISDN=?", new Object[]{msisdn});
			if(null!=data && data.size()>0){
				map.put("msisdn", data);
				map.put("Status", "SUCCESS");
			}else if(null!=data && data.size()==0){
				msisdn = IndoUtil.prefix62(msisdn);
				data = geneva.getData("select * from WSS_MSISDN where MSISDN=?", new Object[]{msisdn});
				if(null!=data && data.size()>0){
					map.put("msisdn", data);
					map.put("Status", "SUCCESS");
				}else{
					IndoUtil.populateErrorMap(map, "WSS-003", "Not registered.",0);
					return map;
				}
			}
			if(IndoUtil.isSuccess(map)){
				return  getUserProfile(map.get("USER_ID").toString());
			}
		}catch(Exception ce){
			log.error("WSSDBServiceImpl.getMsisdnByNum() ce "+IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map,"WSS-004","Internal server error.",0);
		}finally{
			log.info("WSSDBServiceImpl.getUserProfileByMsisdn() - END");
		}
		return map;
	}
	
	@Override
	public Map<String, Object> getData(String table) {
		log.info("WSSDBServiceImpl.getData() - START");
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			List<Map<String, Object>> data = geneva.getData(table, new Object[]{});
			map.put("data", data);
		}catch(Exception ce){
			log.error("WSSDBServiceImpl.getData() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("WSSDBServiceImpl.getData() - END");
		}
		return map;
	}
	@Override
	public Map<String, Object> changepassword(String uid, String newPassword) {
		log.info("WSSDBServiceImpl.changepassword() - START");
		Map<String,Object> map = new HashMap<String,Object>();
		int ct = geneva.saveData("UPDATE wss_user set PASSWORDKU=? WHERE USERNAME=?", new Object[]{newPassword,uid});
		if(ct>0){
			map.put("Status", "SUCCESS");
		}
		log.info("WSSDBServiceImpl.changepassword() - END");
		return map;
	}
	@Override
	public Map<String, Object> regUser(Map<String,String> user) {
		log.info("WSSDBServiceImpl.regUser() - START");
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			Map<String, Object> row = geneva.getRow("SELECT * FROM wss_msisdn WHERE id = ( SELECT MAX(id) FROM wss_msisdn)", new Object[]{});
			int mid = Integer.parseInt(row.get("ID").toString());
			row = geneva.getRow("SELECT * FROM wss_user WHERE id = ( SELECT MAX(id) FROM wss_user)", new Object[]{});
			int uid = Integer.parseInt(row.get("ID").toString());
			int ct = geneva.saveData("INSERT into wss_user(id,PASSWORDKU,PASSWORD_EXPIRED,USERNAME,NAME,VERSION,ACCOUNT_EXPIRED,ACCOUNT_LOCKED,ENABLED) values(?,?,?,?,?,?,?,?,?)", 
					new Object[]{uid+1,user.get("pwd"),"0",user.get("user_id"),user.get("user_name"),"1","0","0","1"});
			if(ct>0){
				ct = geneva.saveData("INSERT into wss_msisdn(id,MSISDN,REDEMPTION_STATUS,USER_ID,VERSION) values(?,?,?,?,?)", 
						new Object[]{mid+1,user.get("msisdn"),"active",uid+1,"0"});
				if(ct>0){
					map.put("Status", "SUCCESS");
				}
			}
		}catch(Exception ce){
			log.error("WSSDBServiceImpl.regUser() ce "+IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map,"WSS-005","Saturn-101",0);
		}finally{
			log.info("WSSDBServiceImpl.regUser() - END");
		}
		return map;
	}
	@Override
	public Map<String, Object> addMsisdn(String msisdn,String id) {
		log.info("WSSDBServiceImpl.addMsisdn() - START");
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			List<Map<String, Object>> data = geneva.getData("SELECT * FROM wss_user WHERE USERNAME=?", new Object[]{id});
			if(null!=data && data.size()>0){
				Map<String, Object> row = geneva.getRow("SELECT * FROM wss_msisdn WHERE id = ( SELECT MAX(id) FROM wss_msisdn)", new Object[]{});
				int mid = Integer.parseInt(row.get("ID").toString());
				int ct = geneva.saveData("INSERT into wss_msisdn(id,MSISDN,REDEMPTION_STATUS,USER_ID,VERSION) values(?,?,?,?,?)", 
						new Object[]{mid+1,IndoUtil.prefix62(msisdn),"active",data.get(0).get("ID"),"0"});
				if(ct>0){
					map.put("Status", "SUCCESS");
				}
			}else{
				IndoUtil.populateErrorMap(map,"WSS-009","User ID not found",0);
			}
		}catch(Exception ce){
			log.error("WSSDBServiceImpl.regUser() ce "+IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map,"WSS-005","Saturn-101",0);
		}finally{
			log.info("WSSDBServiceImpl.addMsisdn() - END");
		}
		return map;
	}
	@Override
	public Map<String, Object> removeMsisdn(String msisdn) {
		log.info("WSSDBServiceImpl.removeMsisdn() - START");
		Map<String,Object> map = new HashMap<String,Object>();
		String msisdn62 = msisdn;
		String msisdn0	= msisdn;
		String msisdnNo62 = msisdn;
		String msisdnNo620 = msisdn;
		if(!msisdn62.startsWith("62")){
			IndoUtil.prefix62(msisdn);
		}
		if(!msisdn0.startsWith("0") && !msisdn62.startsWith("62")){
			msisdn0="0"+msisdn;
		}
		if(msisdn62.startsWith("62")){
			msisdnNo62=StringUtils.removeStart(msisdn, "62");
		}
		if(msisdn62.startsWith("62")){
			msisdnNo620=StringUtils.removeStart(msisdn, "62");
			msisdnNo620="0"+msisdnNo620;
		}
		try{
			int ct = geneva.saveData("delete from wss_msisdn WHERE MSISDN=? or MSISDN=? or MSISDN=? or MSISDN=? or MSISDN=?", new Object[]{msisdn, msisdn0, msisdn62, msisdnNo62,msisdnNo620});
			if(ct>0){
					map.put("Status", "SUCCESS");
				}
		}catch(Exception ce){
			log.error("WSSDBServiceImpl.regUser() ce "+IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map,"WSS-005","Saturn-101",0);
		}finally{
			log.info("WSSDBServiceImpl.removeMsisdn() - END");
		}
		return map;
	}
}
