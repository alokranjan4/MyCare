/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ibm.indo.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Aadam
 */
@Repository
public class WSSDBUtil implements Serializable{
	
	private static final long serialVersionUID = -6203751104015962909L;
	@Autowired
	JdbcTemplate wssConn;
	
	private static Logger log = Logger.getLogger("saturnLoggerV1");
	
	@Transactional(readOnly=true)
	public List<Map<String, Object>> getData(String qry, Object[] pAttr){
		wssConn.setQueryTimeout(10);
		log.info("WSSDBUtil.getData() qry "+qry);
		List<Map<String, Object>> vList =  new ArrayList<Map<String,Object>>(); 
		vList = wssConn.queryForList(qry, pAttr);
		if(vList!=null){
			log.info("List size is "+vList.size());
		}
		return vList;
	}
	
	@Transactional(readOnly=true)
	public List<Map<String, Object>> getListData(String qry){
		wssConn.setQueryTimeout(10);
		log.info("WSSDBUtil.getData() qry "+qry);
		List<Map<String, Object>> vList =  new ArrayList<Map<String,Object>>(); 
		vList = wssConn.queryForList(qry);
		if(vList!=null){
			log.info("List size is "+vList.size());
		}
		return vList;
	}
	@Transactional(readOnly=true)
	public Map<String, Object> getRow(String qry, Object[] pAttr){
		wssConn.setQueryTimeout(10);
		log.info("WSSDBUtil.getRow() qry "+qry);
		Map<String, Object> map = new HashMap<String, Object>();
		map = wssConn.queryForMap(qry, pAttr);
		if(map!=null){
			log.info("map size is "+map.size());
		}
		return map;
	}
    @Transactional(readOnly=true)
	public List<String> getSingleCol(String qry){
    	wssConn.setQueryTimeout(10);
    	log.info(" WSSDBUtil.getSingleCol() - qry" + qry );
    	List<String> vList = new ArrayList<String>();
    	vList = wssConn.queryForList(qry, String.class);
    	if(vList!=null){
			log.info("List size is "+vList.size());
		}
    	return vList;
	}
    
    @Transactional(readOnly=true)
  	public String getSingleValue(String qry){
      	log.info(" WSSDBUtil.getSingleCol() - qry" + qry );
      	wssConn.setQueryTimeout(10);
      	String strResult = wssConn.queryForObject(qry, String.class);
      	return strResult;
  	}
    //
    
    @Transactional
    public int[] insertBatch(String sql,List<Object[]> pAttr){
    	wssConn.setQueryTimeout(10);
    	log.info("WSSDBUtil.insertBatch() qry "+sql);
        int[] res = wssConn.batchUpdate(sql, pAttr);
        return res;
    }

    @Transactional
	public int saveData(String pQuery, Object[] pAttr){
    	wssConn.setQueryTimeout(10);
    	log.info("WSSDBUtil.insertBatch() qry "+pQuery);
    	int vCount = 0 ;
    	vCount = wssConn.update(pQuery, pAttr);
    	return vCount;
	}
    
    public int saveDataNoTrans(String pQuery, Object[] pAttr){
    	wssConn.setQueryTimeout(10);
    	int vCount = 0 ;
    	vCount = wssConn.update(pQuery, pAttr);
    	return vCount;
	}
}