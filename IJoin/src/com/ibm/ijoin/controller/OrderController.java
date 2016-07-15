package com.ibm.ijoin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ibm.ijoin.service.GenericService;
import com.ibm.ijoin.util.IndoUtil;

@Controller
public class OrderController {
	@Autowired
	GenericService genService;

	@RequestMapping(value = "/order", method = RequestMethod.GET)
	public String order(HttpServletRequest req) {
		System.out.println("OrderController.order(-) start.");

		return "order";
	}

	@RequestMapping(value = "/orderdetails", produces = "application/json")
	public @ResponseBody Map<String, Object> orderdetails(HttpServletRequest req,
			@RequestParam Map<String, String> params) {
		System.out.println("-----------------START------------------------" + params);
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			int start = Integer.parseInt(params.get("start"));
			int len = Integer.parseInt(params.get("length"));
			data = genService.orderdetails(Integer.toString(start), Integer.toString(start + len));
			System.out.println("-----------------END------------------------" + data.get("Status"));
			return data;
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(data, "Indo-218", "No Data Found.", 0);
			System.out.println("Indo-218- OrderController.orderdetails() ce " + ce);
		}
		System.out.println("-----------------END------------------------" + data.get("Status"));
		return data;
	}

	@RequestMapping(value = "/getOrderDetails")
	public String getOrderDetails(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
		System.out.println("-----------------START------------------------" + params);
		List<Map<String, Object>> list = null;
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = genService.getOrderDetails(params.get("id"));
			if (IndoUtil.isSuccess(map)) {
				list = (List<Map<String, Object>>) map.get("Details");
			}
			System.out.println("-----------------END------------------------" + list);
			model.addAttribute("list", list);
		} catch (Exception ce) {
			System.out.println("Indo-218- OrderController.getOrderDetails() ce " + ce);
		}
		return "showDetails";
	}

	@RequestMapping(value = "/updateOrder", method = RequestMethod.POST)
	public  @ResponseBody Map<String, Object> updatepackage(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
		Map<String, Object> map = new HashMap<String, Object>();
		System.out.println("OrderController.updateOrder() params " + params);
		try {
			map = genService.updateOrder(params.get("order_id"), params.get("msisdn"), params.get("act_status"),params.get("iccid"));
		} catch (Exception ce) {
			System.out.println("Indo-218- OrderController.updateOrder() ce " + ce);
		}
		return map;
	}
	

}