package com.ibm.indo.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ibm.indo.service.GenericService;
import com.ibm.indo.util.IndoUtil;
@RequestMapping("/service")
@Controller
public class KMLController {
	@Autowired
	GenericService genService;
	private static Logger log = Logger.getLogger("saturnLogger");

	@RequestMapping(value = "/getKml", method = RequestMethod.GET)
	public @ResponseBody FileSystemResource getKml(@RequestParam Map<String, String> params, HttpServletResponse response, HttpServletRequest req) {
		log.info("-----------------START KMLLOcation Reports------------------------");
		String fileName=params.get("fileName");
		try {
			return new FileSystemResource("/app/locations/"+fileName); 
		}catch(Exception ce){
			IndoUtil.populateErrorMap(null, "Saturn-301","Saturn-109");
			log.error("LoginController.loginService() ce "+ce);
		}
		log.info("Exiting logoutService ");
		return null;
	  }
}
