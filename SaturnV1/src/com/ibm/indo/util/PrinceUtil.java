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
public class PrinceUtil implements Serializable{
	
	private static final long serialVersionUID = -6203751104015962909L;
	@Autowired
	JdbcTemplate littlePrince;
	
	private static Logger log = Logger.getLogger("saturnLoggerV1");
	
	@Transactional(readOnly=true)
	public List<Map<String, Object>> getData(String qry, Object[] pAttr){
		log.info("PrinceUtil.getData() qry "+qry);
		List<Map<String, Object>> vList =  new ArrayList<Map<String,Object>>(); 
		vList = littlePrince.queryForList(qry, pAttr);
		return vList;
	}
	
	@Transactional(readOnly=true)
	public List<Map<String, Object>> getListData(String qry){
		log.info("PrinceUtil.getData() qry "+qry);
		List<Map<String, Object>> vList =  new ArrayList<Map<String,Object>>(); 
		vList = littlePrince.queryForList(qry);
		return vList;
	}
	@Transactional(readOnly=true)
	public Map<String, Object> getRow(String qry, Object[] pAttr){
		log.info("PrinceUtil.getRow() qry "+qry);
		Map<String, Object> map = new HashMap<String, Object>();
		map = littlePrince.queryForMap(qry, pAttr);
		return map;
	}
    @Transactional(readOnly=true)
	public List<String> getSingleCol(String qry){
    	log.info(" PrinceUtil.getSingleCol() - qry" + qry );
    	List<String> vList = new ArrayList<String>();
    	vList = littlePrince.queryForList(qry, String.class);
    	return vList;
	}
    
    @Transactional(readOnly=true)
  	public String getSingleValue(String qry){
      	log.info(" PrinceUtil.getSingleCol() - qry" + qry );
      	
      	String strResult = littlePrince.queryForObject(qry, String.class);
      	return strResult;
  	}
    //
    
    @Transactional
    public int[] insertBatch(String sql,List<Object[]> pAttr){
    	log.info("PrinceUtil.insertBatch() qry "+sql);
        int[] res = littlePrince.batchUpdate(sql, pAttr);
        return res;
    }

    @Transactional
	public int saveData(String pQuery, Object[] pAttr){
    	log.info("PrinceUtil.insertBatch() qry "+pQuery);
    	int vCount = 0 ;
    	vCount = littlePrince.update(pQuery, pAttr);
    	return vCount;
	}
    
    public int saveDataNoTrans(String pQuery, Object[] pAttr){
    	int vCount = 0 ;
    	vCount = littlePrince.update(pQuery, pAttr);
    	return vCount;
	}
}