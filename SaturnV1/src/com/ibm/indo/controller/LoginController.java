/**
 * 
 */
package com.ibm.indo.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.indo.service.GenericService;
import com.ibm.indo.util.IndoUtil;

/**
 * @author Aadam
 *
 */
@RestController
@RequestMapping("/service")
@Consumes("application/json")
public class LoginController {
	@Autowired
	GenericService genService;
	private static Logger log = Logger.getLogger("saturnLoggerV1");
	
	/**
	 * This service is used to authenticate the agent.
	 * based on userid and password, the response 
	 * consists of either success result or error message.
	 * 
	 * @param jsonInput
	 * @return loginVO
	 */
	@RequestMapping(value = "/logout",produces="application/json")
	public Map<String, String> logoutService(HttpSession session) {
		log.info("Entering logoutService ");
		Map<String, String> data = new HashMap<String,String>();
		try{
			session.invalidate();
			data.put("Status","SUCCESS");
			data.put("Msg", "Logged out.");
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Saturn-301","Saturn-109");
			log.error("LoginController.loginService() ce "+ce);
		}
		log.info("Exiting logoutService "+data);
		return data;
	}
	

	

}
