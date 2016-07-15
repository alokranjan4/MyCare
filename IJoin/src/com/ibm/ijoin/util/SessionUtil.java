package com.ibm.ijoin.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.ibm.services.vo.LoginVO;

/**
*
* @author Aadam
*/
public class SessionUtil {

	static IndoServiceProperties confProp=IndoServiceProperties.getInstance();
	public static Properties prop = confProp.getConfigSingletonObject();
	private static Logger log = Logger.getLogger("saturnLoggerV1");
	public static boolean userStatus(HttpServletRequest req){
		
		LoginVO vo = getLoginVO(req);
		if(null!=vo && null!=vo.getAuthenticationFlag() && vo.getAuthenticationFlag().equalsIgnoreCase("Y")){
			return true;
		}
		return false;
	}
	public static void clearUser(HttpServletRequest req){
		req.getSession().removeAttribute("userMsisdns");
		req.getSession().removeAttribute("listMsisdns");
		req.getSession().removeAttribute("loginVO");
		req.getSession().removeAttribute("token");
	}
	public static LoginVO getLoginVO(HttpServletRequest req){
		return (LoginVO) req.getSession().getAttribute("loginVO");
	}
	public static void setMsisdns(HttpServletRequest req, List<Map<String, Object>> msisdns){
		req.getSession().setAttribute("userMsisdns", msisdns);
	}
	public static List<Map<String, Object>> getMsisdns(HttpServletRequest req){
		return (List<Map<String, Object>>) req.getSession().getAttribute("userMsisdns");
	}
	public static List<String> getListMsisdns(HttpServletRequest req){
		List<String> msisdnList = (List<String>) req.getSession().getAttribute("listMsisdns");
		if(null==msisdnList){
			List<Map<String, Object>> msisdns =  getMsisdns(req);
			if(null!=msisdns && msisdns.size()!=0){
				msisdnList = new ArrayList<String>();
				for(Map<String, Object> map : msisdns){
					if(null!=map.get("msisdn")){
						msisdnList.add(map.get("msisdn").toString());
					}
				}
			}
			req.getSession().setAttribute("listMsisdns", msisdnList);
		}
		log.info("SessionUtil.getListMsisdns() msisdnList "+msisdnList);
		return msisdnList;
	}
	public static boolean isAuthorised(HttpServletRequest req, String msisdn){
		msisdn = IndoUtil.prefix62(msisdn);
		log.info("SessionUtil.isAuthorised() msisdn "+msisdn);
		List<String> list = getListMsisdns(req);
		log.info("SessionUtil.isAuthorised() list "+list);
		if(null!=list && list.contains(msisdn)){
			return true;
		}
		/*List<Map<String, Object>> msisdns =  getMsisdns(req);
		if(null!=msisdns){
			for(Map<String, Object> map : msisdns){
				if(null!=map.get("msisdn")){
					if(map.get("msisdn").toString().equals(msisdn)){
						return true;
					}
				}
			}
		}*/
		return false;
	}
	public static void setToken(HttpServletRequest req, String token){
		req.getSession().setAttribute("token", token);
	}
	public static boolean isValidToken(HttpServletRequest req){
		String tok = req.getHeader("authorization");
		String token = (String) req.getSession().getAttribute("token");
		if(null!=tok && tok.trim().equals(prop.get("token"))){
			return true;
		}
		if(null!=token && null!=tok && tok.trim().equals(token)){
			return true;
		}
		return false;
	}
	public static boolean isValidLoginToken(HttpServletRequest req){
		String tok = req.getHeader("authorization").trim();
		String token = (String) req.getSession().getAttribute("token");
		log.info("SessionUtil.isValidLoginToken() tok "+tok);
		if(null!=token && null!=tok && tok.equals(token)){
			return true;
		}
		return false;
	}
	
	public static boolean setLoginVO(HttpServletRequest req, LoginVO vo){
		req.getSession().setAttribute("loginVO", vo);
		return true;
	}
}
