/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ibm.ijoin.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Aadam
 */
@Repository
public class DBUtil implements Serializable{
	
	private static final long serialVersionUID = -6203751104015962909L;
	@Autowired
	JdbcTemplate jdbcTemp;
	
	private static Logger log = Logger.getLogger("ijoinLogger");
	
	@Transactional(readOnly=true)
	public List<Map<String, Object>> getData(String qry, Object[] pAttr){
		jdbcTemp.setQueryTimeout(10);
		log.info("DBUtil.getData() qry "+qry);
		List<Map<String, Object>> vList =  new ArrayList<Map<String,Object>>(); 
		vList = jdbcTemp.queryForList(qry, pAttr);
		if(vList!=null){
			log.info("Query list size is "+vList.size());
		}
		return vList;
	}
	
	@Transactional(readOnly=true)
	public List<Map<String, Object>> getListData(String qry){
		log.info("DBUtil.getData() qry "+qry);
		List<Map<String, Object>> vList =  new ArrayList<Map<String,Object>>(); 
		vList = jdbcTemp.queryForList(qry);
		if(vList!=null){
			log.info("Query list size is "+vList.size());
		}
		return vList;
	}
	@Transactional(readOnly=true)
	public Map<String, Object> getRow(String qry, Object[] pAttr){
		log.info("DBUtil.getRow() qry "+qry);
		Map<String, Object> map = new HashMap<String, Object>();
		map = jdbcTemp.queryForMap(qry, pAttr);
		if(map!=null){
			log.info("Query list size is "+map.size());
		}
		return map;
	}
    @Transactional(readOnly=true)
	public List<String> getSingleCol(String qry){
    	log.info(" DBUtil.getSingleCol() - qry" + qry );
    	List<String> vList = new ArrayList<String>();
    	vList = jdbcTemp.queryForList(qry, String.class);
    	if(vList!=null){
			log.info("Query list size is "+vList.size());
		}
    	return vList;
	}
    @Transactional(readOnly=true)
   	public List<String> getSingleCol(String qry, Object[] pAttr){
       	log.info(" DBUtil.getSingleCol() - qry" + qry );
       	List<String> vList = new ArrayList<String>();
       	vList = jdbcTemp.queryForList(qry,pAttr,String.class);
       	if(vList!=null){
   			log.info("Query list size is "+vList.size());
   		}
       	return vList;
   	}
    
    @Transactional(readOnly=true)
  	public String getSingleValue(String qry){
      	log.info(" DBUtil.getSingleCol() - qry" + qry );
      	
      	String strResult = jdbcTemp.queryForObject(qry, String.class);
      	log.info("Query value is "+strResult);
      	return strResult;
  	}
    //
    
    @Transactional
    public int[] insertBatch(String sql,List<Object[]> pAttr){
    	log.info("DBUtil.insertBatch() qry "+sql);
        int[] res = jdbcTemp.batchUpdate(sql, pAttr);
        return res;
    }

    @Transactional
	public int saveData(String pQuery, Object[] pAttr){
    	log.info("DBUtil.insertBatch() qry "+pQuery);
    	int vCount = 0 ;
    	vCount = jdbcTemp.update(pQuery, pAttr);
    	log.info("Query value is "+vCount);
    	return vCount;
	}
    
    public int saveDataNoTrans(String pQuery, Object[] pAttr){
    	int vCount = 0 ;
    	vCount = jdbcTemp.update(pQuery, pAttr);
    	log.info("Query value is "+vCount);
    	return vCount;
	}
    @Transactional
   	public int saveORUpdate(Map<String,?> queries, boolean update){
     //  	log.info("DBUtil.saveORUpdate() insert qry "+queries.get("insert"));log.info("DBUtil.saveORUpdate() update qry "+queries.get("update"));
       	int vCount = 0 ;
       	try{
       		Map<String, Object> map = getRow(queries.get("select").toString(), new Object[]{});
       		int ct = Integer.parseInt(map.get("COUNT").toString());
       		if(ct==0){
       			log.info("DBUtil.saveORUpdate()-- Inserting......");
       			if(queries.containsKey("insertObj")){
       				Object[] ins = (Object[]) queries.get("insertObj");
       				vCount = saveData(queries.get("insert").toString(), ins);
       			}else{
       				vCount = saveData(queries.get("insert").toString(), new Object[]{});
       			}
           		log.info("DBUtil.saveORUpdate()-- Inserted......"+vCount);
           		return vCount;
       		}else if(update){
       			if(queries.containsKey("updateObj")){
       				Object[] upd = (Object[]) queries.get("updateObj");
       				vCount = saveData(queries.get("update").toString(), upd);
       			}else{
       				vCount = saveData(queries.get("update").toString(), new Object[]{});
       			}
           		log.info("DBUtil.saveORUpdate()---- Updating...."+ vCount);
       		}else if(!update){
       			return -5;
       		}
       	}catch(EmptyResultDataAccessException e){
       		log.info("DBUtil.saveORUpdate()-- Inserting......");
       		vCount = saveData(queries.get("insert").toString(), new Object[]{});
       		log.info("DBUtil.saveORUpdate()-- Inserted......"+vCount);
       	}catch(Exception e){
       		log.error("DBUtil.saveORUpdata() e "+IndoUtil.getFullLog(e));
       	}
       	return vCount;
   	}
}