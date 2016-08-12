package com.ibm.ijoin.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.ibm.services.vo.LoginVO;

public class SessionFilter implements Filter {
	
	private static Logger log = Logger.getLogger("ijoinLogger");
	
    public SessionFilter() {
    	super();
    }
    
    public void init(FilterConfig config) throws ServletException {
    	
    }
    
	public void destroy() {
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		log.info("request id "+ request.getSession().getId());
		LoginVO vo = (LoginVO)request.getSession().getAttribute("loginVO");
		//System.out.println("SessionFilter.doFilter() - vo "+vo);
		String path = request.getRequestURI();
		//System.out.println("SessionFilter.doFilter() - path - "+request.getContextPath());
		List<String> ignore = new ArrayList<String>();
		ignore.add(request.getContextPath()+"/service/login");
		ignore.add(request.getContextPath()+"/service/forgotPassword");
		ignore.add(request.getContextPath()+"/service/changePassword");
		ignore.add(request.getContextPath()+"/service/retrieveStaticData");
		ignore.add(request.getContextPath()+"/service/versionCheck");
		ignore.add(request.getContextPath()+"/service/registerUser");
		ignore.add(request.getContextPath()+"/service/tempService");
		ignore.add(request.getContextPath()+"/service/updatePackageStatus");
	ignore.add(request.getContextPath()+"/service/dashboard");
	ignore.add(request.getContextPath()+"/service/serviceRequest");
	ignore.add(request.getContextPath()+"/service/retrieveOffers");
	ignore.add(request.getContextPath()+"/service/retrieveUpgradablePackages");	
		List<String> ignoreToken = new ArrayList<String>();
		ignoreToken.add(request.getContextPath()+"/service/updatePackageStatus");
	ignoreToken.add(request.getContextPath()+"/service/dashboard");
	ignoreToken.add(request.getContextPath()+"/service/serviceRequest");
	ignoreToken.add(request.getContextPath()+"/service/retrieveOffers");
	ignoreToken.add(request.getContextPath()+"/service/retrieveUpgradablePackages");
		/////mulCataOrderStatus
		/*log.info("SessionFilter.authorization "+request.getHeader("authorization"));
		Enumeration<String> en = request.getHeaderNames();
		while(en.hasMoreElements()){
			log.info("SessionFilter.SessionFilter() "+en.nextElement());
		}*/
		if (ignore.contains(path)) {
			if(!SessionUtil.isValidToken(request) && !ignoreToken.contains(path)){
				response.setContentType("application/json");
		        response.setCharacterEncoding("UTF-8");
		        Map<String, Object> modelMap = new HashMap<String, Object>();
		        IndoUtil.populateErrorMap(modelMap, "Saturn-000", "Unauthorised User",0);
				modelMap.put("user", "Unauthorised");
		        response.getWriter().write(new Gson().toJson(modelMap));
			}else {
				//System.out.println("SessionFilter.doFilter()- Ignore URL..."+path);
				chain.doFilter(request, response);
			}
		}else{
			 if(null!=vo && null!=vo.getAuthenticationFlag() && vo.getAuthenticationFlag().equalsIgnoreCase("Y")){
				 if(!SessionUtil.isValidLoginToken(request)){
						response.setContentType("application/json");
				        response.setCharacterEncoding("UTF-8");
				        Map<String, Object> modelMap = new HashMap<String, Object>();
				        IndoUtil.populateErrorMap(modelMap, "Saturn-000", "Unauthorised User",0);
						modelMap.put("user", "Unauthorised");
				        response.getWriter().write(new Gson().toJson(modelMap));
					}else{
						chain.doFilter(request, response);
					}
			}else{
//				String servletPath = request.getServletPath();
//				String queryString = request.getQueryString();
//				String fullServletPath = request.getRequestURI();
				//System.out.println(" Filter || Session Filter  ||  doFilter || req " + request.getSession().getId());
				response.setContentType("application/json");
		        response.setCharacterEncoding("UTF-8");
		        Map<String, Object> modelMap = new HashMap<String, Object>();
		        IndoUtil.populateErrorMap(modelMap, "Saturn-000", "Saturn-000",0);
				modelMap.put("user", "invalid");
		        response.getWriter().write(new Gson().toJson(modelMap));
			}
		}
	}
}
