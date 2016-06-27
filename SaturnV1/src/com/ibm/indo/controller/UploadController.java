/**
 * 
 */
package com.ibm.indo.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.indo.util.DBUtil;
import com.ibm.indo.util.IndoServiceProperties;
import com.ibm.indo.util.IndoUtil;

/**
 * @author Aadam
 *
 */
@RestController
@RequestMapping("/service")
@Consumes("application/json")
public class UploadController {
	@Autowired
	DBUtil dbUtil;
	
	private static Logger log = Logger.getLogger("saturnLoggerV1");
	IndoServiceProperties confProp=IndoServiceProperties.getInstance();
    Properties prop = confProp.getConfigSingletonObject();
    
    @RequestMapping(value = "/uploadImageOPS",produces="application/json",consumes="application/json")
	public Map<String, ?> uploadImage(HttpServletRequest req,@RequestBody String jsonInput) {
    	Map<String,Object> map = new HashMap<String,Object>();
    	StringBuffer qry = new StringBuffer();
    	try{
    	JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
    	String type= jObj.get("type").getAsString();
    	String table = jObj.get("table").getAsString();
    	JsonArray columns = jObj.get("columns").getAsJsonArray();
    	JsonArray values = jObj.get("values").getAsJsonArray();
    	if(type.equalsIgnoreCase("insert")){
    		qry.append("Insert into ").append(table).append("(");
    		for(int i=0;i<columns.size();i++){
    			qry.append(columns.get(i).getAsString()).append(",");
    		}
    		qry.delete(qry.length()-1,qry.length());
    		qry.append(") values(");
    		Object[] obj = new Object[values.size()];
    		for(int i=0;i<values.size();i++){
    			obj[i] = values.get(i).getAsString();
    			qry.append("?,");
    		}
    		qry.delete(qry.length()-1,qry.length());
    		qry.append(")");
    		
    		int ct = dbUtil.saveData(qry.toString(), obj);
    		if(ct>0){
    			map.put("Status", "SUCCESS");
    		}
    	}if(type.equalsIgnoreCase("update")){
    		qry.append("Update ").append(table).append(" set ");
    		List<String> whereCols = new ArrayList<String>();
    		List<String> whereVals = new ArrayList<String>();
    		JsonArray whereColumns = jObj.get("where").getAsJsonArray();
    		for(int i=0;i<whereColumns.size();i++){
    			whereCols.add(whereColumns.get(i).getAsString());
    		}
    		List<String> obj = new ArrayList<String>();
    		for(int i=0;i<columns.size();i++){
    			String col = columns.get(i).getAsString();
    			if(whereCols.contains(col)){
    				whereVals.add(values.get(i).getAsString());
    			}
    			qry.append(col).append("=?,");
    			obj.add(values.get(i).getAsString());
    		}
    		qry.delete(qry.length()-1,qry.length());
    		qry.append(" where ");
    		log.info("UploadController.uploadImage() whereVals "+whereVals);
    		for(int i=0;i<whereCols.size();i++){
    			qry.append(whereCols.get(i)).append("=? and ");
    			obj.add(whereVals.get(i));
    		}
    		qry.delete(qry.length()-4,qry.length());
    		log.info("UploadController.uploadImage() list "+Arrays.asList(obj));
    		int ct = dbUtil.saveData(qry.toString(), obj.toArray());
    		if(ct>0){
    			map.put("Status", "SUCCESS");
    		}
    	}
    	}catch(Exception ce){
    		log.error("UploadController.uploadImage() ce "+IndoUtil.getFullLog(ce));
    		map.put("Cause", ce.getLocalizedMessage());
    	}finally{
    		map.put("qry", qry.toString());
    	}
    	return map;
    }
}