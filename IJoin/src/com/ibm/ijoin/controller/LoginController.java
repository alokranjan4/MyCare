/**
 * 
 */
package com.ibm.ijoin.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.ibm.ijoin.service.GenericService;
import com.ibm.ijoin.util.IndoUtil;
import com.ibm.ijoin.util.SessionUtil;
import com.ibm.services.vo.LoginVO;

/**
 * @author Alok Ranjan
 *
 */
@Controller
public class LoginController {
	@Autowired
	GenericService genService;
	private static Logger log = Logger.getLogger("im2");
	
	/**
	 * This service is used to authenticate the agent.
	 * based on userid and password, the response 
	 * consists of either success result or error message.
	 * 
	 * @param jsonInput
	 * @return loginVO
	 */
	
	
	
	@RequestMapping(value = "/login",method=RequestMethod.GET)
	public String loginPage(HttpServletRequest req,@RequestParam Map<String,String> params, Model model) {
	  log.info("LoginController.loginPage(-) login page display ");
		 return "login";
		
	}
	
	@RequestMapping(value ="/login", method=RequestMethod.POST)
	public String loginService(HttpServletRequest req,@RequestParam Map<String,String> params, Model model) {
		log.info("LoginController.loginService(-).............start.");
		Map<String, Object> data = new HashMap<String,Object>();
		if(null==params.get("LoginID") || StringUtils.isEmpty(params.get("LoginID"))){
			log.info("LoginController.loginService(-).............user id :."+params.get("LoginID")+" "+params.get("Password"));
			
			return "login";
		}
		
		data =  genService.validateUser(params.get("LoginID"), params.get("Password"));
		log.info("LoginController.loginService(-).............start."+data);
		if(data.containsKey("LoginID")){
		String LoginID=data.get("LoginID").toString();
		HttpSession session = req.getSession();
	    session.setAttribute("LoginID", LoginID);
		}
		if(IndoUtil.isSuccess(data)){
			LoginVO vo = new LoginVO();
			vo.setAuthenticationFlag("Y");
			vo.setUserid(params.get("userid"));
			SessionUtil.setLoginVO(req, vo);
			return "home";
		}else{
			model.addAttribute("msg",IndoUtil.eMsg("Invalid userid/password."));
			return "login";
		}
		
	}
	@RequestMapping(value = "/logout")
	public String logoutService(HttpSession session) {
		log.info("-----------------START Logout------------------------");
		try{
			//model.addAttribute("msg",IndoUtil.eMsg("Su. Please login."));
			session.invalidate();
			return "login";
		}catch(Exception ce){
			
		}
		log.info("-----------------END------------------------");
		return null;
	}
	
	@RequestMapping(value = "/sessionInvalid")
	public String sessionInvalid(HttpServletRequest req, Model model) {
		log.info("-----------------START------------------------");
		try{
			model.addAttribute("msg",IndoUtil.eMsg("Session Expired. Please login."));
			return "login";
		}catch(Exception ce){
			
		}
		log.info("-----------------END------------------------");
		return null;
	}
	

}
