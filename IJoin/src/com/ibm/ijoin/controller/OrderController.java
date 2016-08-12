package com.ibm.ijoin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ibm.ijoin.service.GenericService;
import com.ibm.ijoin.util.IndoUtil;
import com.ibm.ijoin.util.SessionUtil;
import com.ibm.services.vo.LoginVO;
/*
 * 
 * Author Alok Ranjan
 * 
 */
@Controller
public class OrderController {
	@Autowired
	GenericService genService;

	private static Logger log = Logger.getLogger("ijoinLogger");
	
	@RequestMapping(value = "/order", method = RequestMethod.GET)
	public String order(HttpServletRequest req) {
		log.info("OrderController.order(-) start.");

		return "order";
	}

	@RequestMapping(value = "/orderdetails", produces = "application/json")
	public @ResponseBody Map<String, Object> orderdetails(HttpServletRequest req,
			@RequestParam Map<String, String> params) {
		log.info("-----------------START------------------------" + params);
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			int start = Integer.parseInt(params.get("start"));
			int len = Integer.parseInt(params.get("length"));
			String SearchKey=params.get("search[value]");
			log.info("OrderController.orderdetails() SearchKey - "+SearchKey);
			if(!StringUtils.isEmpty(SearchKey)){
				data = genService.orderSearchDetails(Integer.toString(start), Integer.toString(start + len),SearchKey);
			}else{
				data = genService.orderdetails(Integer.toString(start), Integer.toString(start + len),SearchKey);
			}
			return data;
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(data, "Order-218", "No Data Found.", 0);
			log.info("Indo-218- OrderController.orderdetails() ce " + ce);
		}
		log.info("-----------------END------------------------" + data.get("Status"));
		return data;
	}

	@RequestMapping(value = "/getOrderDetails")
	public String getOrderDetails(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
		log.info("-----------------START------------------------" + params);
		List<Map<String, Object>> list = null;
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = genService.getOrderDetails(params.get("id"));
			if (IndoUtil.isSuccess(map)) {
				list = (List<Map<String, Object>>) map.get("Details");
			}
			model.addAttribute("list", list);
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(map, "Order-524","No Data Found.",0);
			log.info("Indo-218- OrderController.getOrderDetails() ce " + ce);
		}
		return "showDetails";
	}

	@RequestMapping(value = "/updateOrder", method = RequestMethod.POST)
	public  @ResponseBody Map<String, Object> updatepackage(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
		Map<String, Object> map = new HashMap<String, Object>();
		LoginVO vo = SessionUtil.getLoginVO(req);
		log.info("OrderController.updatepackage() vo "+vo);
		String LoginID = "";
		if(null!=vo){
			LoginID = vo.getUserid();
		}
		try {
			log.info("OrderController.updateOrder() params " + params+","+ LoginID);
			map = genService.updateOrder(params.get("order_id"), params.get("msisdn"), params.get("act_status"),params.get("iccid"),params.get("order_status"),LoginID);
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(map, "Order-524","Unable to Update",0);
			log.info("Indo-218- OrderController.updateOrder() ce " + ce);
		}
		return map;
	}
	
	@RequestMapping(value = "/autoLogin/{uid}")
	public @ResponseBody Map<String, Object> autoLogin(HttpServletRequest req,HttpServletResponse res, @PathVariable("uid") String user) {
		log.info("-----------------START------------------------");
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			log.info("OrderController.autoLogin() uid - "+user);
			data = genService.autoLogin(user);
			if(IndoUtil.isSuccess(data)){
				data.put("regFlag", "Y");
			}else{
				data.put("regFlag", "N");
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Order-524","AutoLogin Failed",0);
			log.info("Saturn-524- OrderController.autoLogin() ce "+IndoUtil.getFullLog(ce));
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	} 
	@RequestMapping(value = "/orderStatus/{oid}")
	public @ResponseBody Map<String, Object> orderStatus(HttpServletRequest req,HttpServletResponse res, @PathVariable("oid") String oid) {
		log.info("-----------------START------------------------");
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			log.info("OrderController.orderStatus() oid - "+oid);
			data = genService.autoLogin(oid);
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Order-524","No Data Found.",0);
			log.info("Saturn-524- OrderController.orderStatus() ce "+IndoUtil.getFullLog(ce));
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return data;
	}
	@RequestMapping(value = "/uploadPDFTest")
	public String uploadPDFTest(HttpServletRequest req,HttpServletResponse res) {
		log.info("-----------------START------------------------");
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			return "uploadPDF";
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Order-524","No Data Found.",0);
			log.info("Saturn-524- OrderController.uploadPDFTest() ce "+IndoUtil.getFullLog(ce));
		}
		log.info("-----------------END------------------------"+data.get("Status"));
		return null;
	}
}
