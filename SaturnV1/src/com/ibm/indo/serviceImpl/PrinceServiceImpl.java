/**
 * 
 */
package com.ibm.indo.serviceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.indo.service.PrinceService;
import com.ibm.indo.util.IndoServiceProperties;
import com.ibm.indo.util.IndoUtil;
import com.ibm.indo.util.PrinceUtil;

/**
 * @author Adeeb
 *
 */
@Service
public class PrinceServiceImpl implements PrinceService {
	@Autowired
	private PrinceUtil prince;
	private static Logger log = Logger.getLogger("saturnLoggerV1");
	IndoServiceProperties confProp=IndoServiceProperties.getInstance();
    Properties prop = confProp.getConfigSingletonObject();
	
	/* (non-Javadoc)
	 * @see com.ibm.indo.service.PrinceService#getUserProfile(java.lang.String)
	 */
	@Override
	public Map<String, Object> getUserProfile(String msisdn) {
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			List<Map<String, Object>> data = prince.getData(msisdn, new Object[]{});
			map.put("data", data);
		}catch(Exception ce){
			log.error("PrinceServiceImpl.getUserProfile() ce "+IndoUtil.getFullLog(ce));
		}
		return map;
	}

}
