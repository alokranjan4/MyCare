package com.ibm.ijoin.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.ibm.ijoin.service.GenericService;
import com.ibm.ijoin.service.PackageService;
import com.ibm.ijoin.util.IndoServiceProperties;
import com.ibm.ijoin.util.IndoUtil;
import com.ibm.ijoin.util.SessionUtil;

@Controller
@EnableWebMvc
public class PackageController {

	@Autowired
	PackageService adminGenService;
	@Autowired
	GenericService genService;

	private static Logger log = Logger.getLogger("ijoinLogger");
	IndoServiceProperties confProp = IndoServiceProperties.getInstance();
	Properties prop = confProp.getConfigSingletonObject();

	@RequestMapping(value = "/forgot", method = RequestMethod.GET)
	public String forgotPage(HttpServletRequest req) {
		log.info("PackageController.forgotPage(-) start.");
		return "forgot";
	}

	@RequestMapping(value = "/changePassword", method = RequestMethod.GET)
	public String changePassword(HttpServletRequest req) {
		log.info("PackageController.changePassword(-) start.");
		return "changePassword";
	}
	
	
	@RequestMapping(value = "/showOffer", method = RequestMethod.GET)
	public ModelAndView showOffer(HttpServletRequest req) {
		log.info("-----------------START------------------------");
		Map<String,Object> map=new HashMap<String,Object>();
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		log.info("PackageController.showOffer(-) start.");
		try{
			map= adminGenService.showOffers();
			if(IndoUtil.isSuccess(map)){
				 list=(List<Map<String, Object>>) map.get("list");	
			 }
		}catch(Exception ce){
			log.info("Indo-218- PackageController.showOffer() ce "+IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Indo-100", "Failed to login.", 0);
		}
		log.info("-----------------END------------------------");
		return new ModelAndView("showallOffer", "list", list);
	}

	@RequestMapping(value = "/showStore", method = RequestMethod.GET)
	public ModelAndView showStore(HttpServletRequest req) {
		log.info("-----------------START------------------------");
		log.info("PackageController.showStore(-) start.");
		Map<String,Object> map=new HashMap<String,Object>();
		List list=null;
		try{
			list = adminGenService.showStores();
			if(IndoUtil.isSuccess(map)){
				 list=(List) map.get("list");
				}
			}catch(Exception ce){
				log.info("Indo-218- IJoinController.showStore() ce "+IndoUtil.getFullLog(ce));
				IndoUtil.populateErrorMap(map, "Indo-100", "Failed to login.", 0);
			}
		log.info("-----------------END------------------------");
		return new ModelAndView("showallStore", "list", list);
	}

	@RequestMapping(value = "/showPackCategory", method = RequestMethod.GET)
	public ModelAndView showPackCategory(HttpServletRequest req) {
		log.info("-----------------START------------------------");
		log.info("PackageController.showPackCategory(-) start.");
		Map<String,Object> map=new HashMap<String,Object>();
		List list=null;
		try{
		map =adminGenService.showPackCategory();
		if(IndoUtil.isSuccess(map)){
			 list=(List) map.get("list");
		}
		}catch(Exception ce){
			log.info("Indo-218- IJoinController.showAllPackageCategory() ce "+IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Indo-100", "Failed to login.", 0);
		}
		log.info("-----------------END------------------------");
		return new ModelAndView("showallPackage1", "list", list);
	}

	@RequestMapping(value = "/showPackInfo", method = RequestMethod.GET)
	public ModelAndView showPackInfo(HttpServletRequest req) {
		Map<String, Object> map = new HashMap<String,Object>();
		List<Map<String, Object>> list=null; 
		log.info("-----------------START------------------------");
			log.info("PackageController.showPackInfo(-) start.");
			try{
			map=adminGenService.getPacksInfor();
			if(IndoUtil.isSuccess(map)){
				map.put("Status", "SUCCESS");	
				list=(List<Map<String, Object>>) map.get("list");
			}else{
				map.put("Status", "SUCCESS");
			}
			}catch(Exception ce){
				log.info("Indo-218- PackageController.getPackCategory() ce "+IndoUtil.getFullLog(ce));
				IndoUtil.populateErrorMap(map, "Indo-100", "Failed to get package category.", 0);
			}
		log.info("-----------------END------------------------");
		return new ModelAndView("showallPackage2", "list", list);
	}

	@RequestMapping(value = "/home", produces = "application/json")
	public String home(HttpSession session) {
		log.info("Entering Home ");
		return "home";
	}

	@RequestMapping(value = "/registerUser")
	public String register(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
		log.info("-----------------START------------------------");
		try {
			Map<String, Object> map = adminGenService.registerUser(params.get("userid"), params.get("pwd"),
					params.get("email"), params.get("name"), params.get("dofbirth"), params.get("pofbirth"),
					params.get("addr"));
			if (IndoUtil.isSuccess(map)) {
				model.addAttribute("msg", IndoUtil.sMsg("Registered Succesfully. Please login."));
			} else {
				model.addAttribute("msg", IndoUtil.eMsg("Failed to register. Please try again."));
			}
		} catch (Exception ce) {
			model.addAttribute("msg", IndoUtil.eMsg("Failed to register. Please try again."));
			log.error("PackageController.register() ce " + IndoUtil.getFullLog(ce));
		}
		log.info("-----------------END------------------------");
		return "register";
	}


	
	@RequestMapping(value = "/register")
	public String register(HttpServletRequest req) {
		log.info("-----------------START------------------------");
		Map<String, Object> map = new HashMap<String, Object>();
		String loginPage = null;
		try {
			String userId = req.getParameter("userid");
			String password = req.getParameter("pwd");
			map = adminGenService.validateUser(userId, password);
			if (IndoUtil.isSuccess(map)) {
				loginPage = "home";
			} else {
				IndoUtil.populateErrorMap(map, "Indo-101", "Unable to register now. Please try again later.", 0);
			}
		} catch (Exception ce) {
			log.info("Indo-100- GenericServiceImpl.validateUser() e - " + ce);
			map.put("Status", "FAILURE");
			IndoUtil.populateErrorMap(map, "Indo-100", "Faile to login.", 0);
			log.info("-----------------END------------------------" + map.get("Status"));
		}
		log.info("-----------------END------------------------");
		return loginPage;
	}

	
	
	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	public String changePassword(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
		try{
			log.info("-----------------START------------------------");
		Map<String, Object> map = new HashMap<String, Object>();
		if(params.get("newPassword").toString().equalsIgnoreCase(params.get("ConfirmPassword").toString())){
			HttpSession session = req.getSession(false);
			map = adminGenService.changePassword(params.get("newPassword"), session.getAttribute("LoginID").toString());
			if (IndoUtil.isSuccess(map)) {
				model.addAttribute("msg", IndoUtil.sMsg("Password changed Successfully."));
				}
		}else{
			model.addAttribute("msg", IndoUtil.eMsg("Password not Match. Please try again."));
		}
		
		} catch (Exception ce) {
			model.addAttribute("msg", IndoUtil.eMsg("Failed to change password. Please try again."));
			log.error("PackageController.changePassword() ce " + IndoUtil.getFullLog(ce));
		}
		log.info("-----------------END------------------------");
		return "changePassword";
	}
	@RequestMapping(value = "/forgot", method = RequestMethod.POST)
	public String forgot(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
		log.info("-----------------START------------------------");
		Map<String, Object> map = new HashMap<String, Object>();
		try{
		String action = req.getParameter("action");
		log.info("Params  : " +params);
		String emailID=params.get("EmailID");
		if(emailID!=null&&params.get("LoginID").toString()!=null){
				map = adminGenService.forgot(params.get("LoginID"), params.get("EmailID"));
				if (IndoUtil.isSuccess(map)) {
					Properties props1 = new Properties();
					props1.put("mail.smtp.host", "smtpgw.indosatooredoo.com");
					props1.put("mail.smtp.port", "25");
				//	props1.put("mail.smtp.user", false);
				//	props1.put("mail.smtp.password", false);
					props1.put("mail.transport.protocol","smtp"); 
					Session session = Session.getDefaultInstance(props1);
					session.setDebug(true);
					MimeMessage msg = new MimeMessage(session);
					log.info("Msg is  || " + msg);
					InternetAddress addressFrom = new InternetAddress("noreply@indosatooredoo.com");
					log.debug("addressFrom is  || " + addressFrom);
					log.debug("Msg is *** || " + msg);
					addressFrom.setPersonal("INDOSAT");
			        msg.setFrom(addressFrom);
					msg.addRecipient(Message.RecipientType.TO, new InternetAddress(emailID));
					msg.setSubject("Your Password");
					msg.setText("Your password is : "+map.get("USER_PASSWORD"));
					Transport.send(msg);
					model.addAttribute("msg", IndoUtil.sMsg("Password send on your Email ID."));
				}else{
					model.addAttribute("msg", IndoUtil.eMsg("Login ID not match ."));
				}
		}
		else{
			model.addAttribute("msg", IndoUtil.eMsg("User ID and Email ID can't be Empty."));
		}
		} catch (Exception ce) {
			model.addAttribute("msg", IndoUtil.eMsg("Failed to retrive password. Please try again."));
			IndoUtil.populateErrorMap(map, "Indo-218","Failed to retrive password.",0);
			log.error("PackageController.forgot() ce " + IndoUtil.getFullLog(ce));
		}
		return "login";
	}
	
	
	@RequestMapping(value = "/newOffer", method = RequestMethod.POST)
	public @ResponseBody Map<String,?> newOffer(@RequestPart("banner_Image_ID") MultipartFile file1,@RequestPart("banner_Image_En") MultipartFile file2,HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
		Map<String,Object> map=new HashMap<String,Object>();
		log.info("-----------------START------------------------");
		log.info("PackageController.newOffer(-)  params : "+params);
		try {
				byte[] imageID=null;
				byte[] imageEn=null;
				if(null!=file1||file2!=null){
					imageID=file1.getBytes();
					imageEn=file2.getBytes();
				}
				map = adminGenService.newOffer(params.get("offer_id"),params.get("pack_code"),params.get("tariff"),params.get("offer_Name_ID"),params.get("offer_Name_EN"),params.get("banefit_ID"),imageEn,params.get("banefit_EN"),params.get("keyword"),params.get("param"),params.get("offer_Link"),params.get("offer_Type"),params.get("customer_Type"),imageID);
				if (IndoUtil.isSuccess(map)) {
					map.put("Status","SUCCESS");
				}else{
					map.put("Status","FAILURE");	
				}

				log.info("-----------------End------------------------");
				return map;
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(map, "Indo-218","Failed to insert New Offer.",0);
			log.error("PackageController.newOffer() ce " + IndoUtil.getFullLog(ce));
		}
		return null;
	}

	@RequestMapping(value="/newPackCategory", method = RequestMethod.POST)
	public @ResponseBody Map<String,?> newPackCategory(@RequestPart("BannerImage") MultipartFile file,HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
		log.info("-----------------START------------------------");
		log.info("PackageController.newPackageCategory(-)  params : "+params);
		Map<String,Object> map=new HashMap<String,Object>();
		try {
		byte[] imagebyte=null;
			if(null!=file){
				imagebyte=file.getBytes();
			}
			Map<String, Object> data = adminGenService.newPackCategory(params.get("PackageType"),params.get("PackageCategory"),params.get("description"),params.get("packageCategoryID"),params.get("catSeq"),imagebyte);
			if (IndoUtil.isSuccess(data)) {
				map.put("Status","SUCCESS");

			}else{
				map.put("Status","FAILTURE");
			}
			log.info("-----------------END------------------------");
			return map;
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(map, "Indo-218","package category insertion fail.",0);
			log.error("PackageController.newPackageCategory() ce " + IndoUtil.getFullLog(ce));
		}
		return null;
	}
	  @RequestMapping(value = "/EditPackCategory", method = RequestMethod.POST)
	     public @ResponseBody Map<String,?> EditPackCategory(@RequestPart("edit_Banner_Image") MultipartFile file,HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
			Map<String,Object> map=new HashMap<String,Object>();
			log.info("-----------------START------------------------");
			log.info("PackageController.EditPackCategory(-)  params : "+params);
			try {
			byte[] imagebyte=null;
			if(null!=file){
				imagebyte=file.getBytes();
			}
				Map<String, Object> data = adminGenService.EditPackCategory(params.get("edit_Package_Type"),params.get("edit_Package_Category"),params.get("edit_description"),params.get("edit_package_CategoryID"),params.get("edit_cat_Seq"),imagebyte);
				if (IndoUtil.isSuccess(data)) {
					map.put("Status","SUCCESS");
				} else {
					map.put("Status","FAILTURE");
				}
		log.info("-----------------END------------------------");
				return map;
			} catch (Exception ce) {
				IndoUtil.populateErrorMap(map, "Indo-218","Editp Package1 fail.",0);
				log.error("PackageController.EditPackCategory() ce " + IndoUtil.getFullLog(ce));
			}
			return null;
		}
	  
	  @RequestMapping(value = "/editOffer", method = RequestMethod.POST)
	  public @ResponseBody Map<String,?> editOffer(@RequestPart("edit_BannerImageID") MultipartFile file1,@RequestPart("edit_BannerImageEN") MultipartFile file2,HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
			Map<String,Object> map=new HashMap<String,Object>();
			log.info("-----------------START------------------------");
			log.info("PackageController.editOffer(-) start. params : "+params);
			try {
					byte[] imageID=null;
					byte[] imageEn=null;
					if(null!=file1||file2!=null){
						imageID=file1.getBytes();
						imageEn=file2.getBytes();
					}
					map=adminGenService.editOffer(params.get("edit_OfferID"),params.get("edit_PackageCode"),params.get("edit_Tariff"),params.get("edit_OfferNameID"),params.get("edit_OfferNameEN"),params.get("edit_BenefitID"),imageEn,params.get("edit_BenefitEN"),params.get("edit_Keyword"),params.get("edit_Param"),params.get("edit_offerLink"),params.get("edit_OfferType"),params.get("edit_CustomerType"),imageID);
					if (IndoUtil.isSuccess(map)) {
					  map.put("Status", "SUCCESS");
					} else {
					  map.put("Status", "FAILTURE");
					}
					log.info("-----------------END------------------------");
					return map;
				}catch(Exception ce) {
					IndoUtil.populateErrorMap(map, "Indo-218","Edition offer Fail.",0);
					log.error("PackageController.editOffer() ce " + IndoUtil.getFullLog(ce));
				}
				return null;
			}
	  
		@RequestMapping(value = "/EditSSPOffer", method = RequestMethod.POST)
		public  @ResponseBody Map<String, ?> EditSSPOffer(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
			Map<String, Object> data=null; 
			log.info("-----------------START------------------------");
			try {
				log.info("PackageController.EditSSPOffer() params : " + params);
				 data = adminGenService.EditSSPOffer(params.get("PACK_CODE"), params.get("Keyword"),params.get("ShortCode"));
				if (IndoUtil.isSuccess(data)) {
		         data.put("Staus","SUCCESS");
				}else {
				 data.put("Staus","FAILTURE");
				}
				return data;
			} catch (Exception ce) {
				IndoUtil.populateErrorMap(data, "Indo-218","edition ssp offer fail.",0);
				model.addAttribute("msg", IndoUtil.eMsg("Failed to update SSP Offer. Please try again."));
				log.error("PackageController.EditSSPOffer() ce " + IndoUtil.getFullLog(ce));
			}
			return null;
		}
		
		@RequestMapping(value = "/editPackInfor", method = RequestMethod.POST)
		public @ResponseBody Map<String,?> editPackInfor(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
			log.info("-----------------START------------------------");
			log.info("PackageController.editPackInfor(-)  params : "+params);
			Map<String,Object> map=new HashMap<String,Object>();
			try {
				map = adminGenService.editPackInfor(params.get("PACK_NAME"), params.get("PACK_GROUP"),params.get("TARIFF2"), params.get("QUOTA2"), params.get("GIFT_FLAG2"), params.get("BUY_FLAG2"),params.get("BUY_EXTRA_FLAG2"), params.get("PARAM2"), params.get("COMMENTS2"),
						params.get("PACKAGE_CATEGORY2"), params.get("UNREG_KEYWORD2"), params.get("UNREG_PARAM2"),params.get("SERVICECLASS2"), params.get("DESCRIPTION2"), params.get("KEYWORD2"));
				if (IndoUtil.isSuccess(map)) {
					map.put("Status","SUCCESS");
				} else {
					map.put("Status","FAILTURE");
				}
		log.info("-----------------END------------------------");
		return map;
			} catch (Exception ce) {
				IndoUtil.populateErrorMap(map, "Indo-218","package information edition fail.",0);
				log.error("PackageController.editPackInfor() ce " + IndoUtil.getFullLog(ce));
			}
		return null;
		}
		
		@RequestMapping(value = "/showSSPOffer", method = RequestMethod.GET)
		public ModelAndView showSSPOffer(HttpServletRequest req) {
			log.info("-----------------START------------------------");
			Map<String,Object> map=new HashMap<String,Object>();
			List list=null;
			try{
			log.info("PackageController.showallOfferSSP(-) start.");
			map = adminGenService.showSSPOffer();
			if(IndoUtil.isSuccess(map)){
				 list=(List) map.get("list");
			}
			}catch(Exception ce){
				IndoUtil.populateErrorMap(map, "Indo-218","Enable to load all ssp offer.",0);
				log.error("PackageController.showallOfferSSP() ce " + IndoUtil.getFullLog(ce));
			}
			log.info("-----------------END------------------------");
			return new ModelAndView("showallOfferSSP","list",list);
		}
		
		@RequestMapping(value = "/getSSPOffer",method=RequestMethod.POST)
		public @ResponseBody  Map<String,?> getSSPOffer(HttpServletRequest req, @RequestParam Map<String, String> params) {
			Map<String,Object> map= new HashMap<String,Object>();
			log.info("-----------------START------------------------");
			log.info("PackageController.getSSPOffer() params " + params);
			try{
				String action = params.get("action");
					if(action.equalsIgnoreCase("edit")) {
							map = adminGenService.getSSPOffer(params.get("PACK_CODE"));
							if (IndoUtil.isSuccess(map)){
								map.put("Status", "SUCCESS");
							 }else{
								map.put("Status", "FAILURE");
					 }
				log.info("-----------------END------------------------");
				return map; 
			}else if (action.equalsIgnoreCase("show")) {
				map = adminGenService.getSSPOffer(params.get("PACK_CODE"));
				if (IndoUtil.isSuccess(map)){
					map.put("Status", "SUCCESS");
				}else{
					map.put("Status", "FAILURE");
				}
				log.info("-----------------END------------------------");
			return map; 
			} else if (action.equalsIgnoreCase("delete")) {
				map = adminGenService.deleteSSPOffer(params.get("PACK_CODE"));
			   if(IndoUtil.isSuccess(map)){
				   map.put("Status", "SUCCESS");
			   }
			   else{
				   map.put("Status", "FAILTURE");
			   }
			   log.info("-----------------END------------------------");
			   return map;
			} 
			}catch(Exception ce){
				IndoUtil.populateErrorMap(map, "Indo-218","No Data Found.",0);
				log.error("PackageController.getSSPOffer() ce " + IndoUtil.getFullLog(ce));
			}
			return null;
		}
		

		@RequestMapping(value = "/getOffer" ,method=RequestMethod.POST)
		public  @ResponseBody  Map<String,?> getOffer(HttpServletRequest req,@RequestParam Map<String, String> params, Model model) {
			Map<String,Object> map=new HashMap<String,Object>();
			log.info("-----------------START------------------------");
			try{
			String action = params.get("action");
			System.out.println("PackageController.getpackage() params " + params);
			if (action.equalsIgnoreCase("edit")) {
				System.out.println("edit");
				 map= adminGenService.getOffer(params.get("Offer_ID"), params.get("PACKAGE_Code"));
				if(IndoUtil.isSuccess(map)){
					map.put("Status","SUCCESS");
					map.put("list",map.get("list"));
				}
				else {
					map.put("Status","FAILTURE");
					map.put("list",map.get("list"));
				}
				log.info("-----------------END------------------------");
				return map;
			}else if(action.equalsIgnoreCase("show")){
				map = adminGenService.getOffer(params.get("Offer_ID"), params.get("PACKAGE_Code"));
				if (IndoUtil.isSuccess(map)){
					map.put("Status","SUCCESS");
					map.put("list",map.get("list"));
				}
				else {
					map.put("Status","FAILTURE");
					map.put("list",map.get("list"));
				}
				log.info("-----------------END------------------------");
				return map;
			}else if(action.equalsIgnoreCase("delete")){
				map =adminGenService.deleteOffer(params.get("Offer_ID"), params.get("PACKAGE_Code"));
				if (IndoUtil.isSuccess(map)) {
					map.put("Status","SUCCESS");
				}else{
					map.put("Status","FAILTURE");
				}
				log.info("-----------------END------------------------");
				return map;
			}
			}catch(Exception ce){
				log.info("Indo-218- IJoinController.getOffer() ce "+IndoUtil.getFullLog(ce));
				IndoUtil.populateErrorMap(map, "Indo-100", "Failed to offer information.", 0);
			}
			log.info("-----------------END------------------------");
			return null;
		}
		
		@RequestMapping(value = "/getPackInfor",method=RequestMethod.POST)
		public @ResponseBody Map<String,?>  getPackInfor(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
			 Map<String,Object> map=new HashMap<String,Object>();
			 List list=null;
			 log.info("-----------------START------------------------");
				log.info("PackageController.getPackInfor(-)  params : "+params);
			 try{
				 String action = params.get("action");
				 	if (action.equalsIgnoreCase("edit")) {
				 		 map = adminGenService.getPackInfor(params.get("PACKAGE_NAME1"), params.get("PACKAGE_GROUP1"));
				 	if (IndoUtil.isSuccess(map)){
				 		map.put("Status","SUCCESS");
				 		map.put("list",map.get("list"));
				 	}else {
				 		map.put("Status","FAILTURE");
				 	}
				 	log.info("-----------------END------------------------");
				 	return map;
				 }else if(action.equalsIgnoreCase("show")) {
				
					 	map=adminGenService.getPackInfor(params.get("PACKAGE_NAME1"), params.get("PACKAGE_GROUP1"));
					 	if (IndoUtil.isSuccess(map)){
					 		map.put("Status","SUCCESS");
					 		map.put("list",map.get("list"));
					 	}else {
					 		map.put("Status","FAILTURE");
					 	}
					 	log.info("-----------------END------------------------");
					 	return map;
				 	}else if(action.equalsIgnoreCase("delete")) {
				 		map= adminGenService.deletePackInfor(params.get("PACKAGE_NAME1"),params.get("PACKAGE_GROUP1"));
				 		if (IndoUtil.isSuccess(map)) {
				 				map.put("Status","SUCCESS");
				 		}else{
				 				map.put("Status","FAILTURE");
				 		}
				log.info("-----------------END------------------------");
				return map;
			}
			 }catch(Exception ce){
					IndoUtil.populateErrorMap(map, "Indo-100", "Failed to get package infor.", 0);
				 log.error("PackageController.newStoreInfo() ce " + IndoUtil.getFullLog(ce));
			 }
			log.info("-----------------END------------------------");
			return null;
		}

		@RequestMapping(value = "/getPackCategory")
		public @ResponseBody Map<String,?> getPackCategory(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
			String action = params.get("action");
			log.info("-----------------START------------------------");
			Map<String,Object> map=new HashMap<String,Object>();
			List list =null;
			try{
			System.out.println("PackageController.getPackCategory() params " + params);
			if (action.equalsIgnoreCase("edit")) {
					map= adminGenService.getPackCategory(params.get("PACKAGE_TYPE"), params.get("PACKAGE_CATEGORY"));
				if (IndoUtil.isSuccess(map)){
					map.put("Status","SUCCESS");
					map.put("list",map.get("list"));
				}
				else {
					map.put("Status","FAILTURE");
					map.put("list",list);
				}
				log.info("-----------------END------------------------");
					
				return map;
			} else if (action.equalsIgnoreCase("show")) {
				 map = adminGenService.getPackCategory(params.get("PACKAGE_TYPE"), params.get("PACKAGE_CATEGORY"));
				if (IndoUtil.isSuccess(map)){
						map.put("Status","SUCCESS");
						map.put("list",map.get("list"));
				}
				else {
					map.put("Status","FAILTURE");
				}
				log.info("-----------------END------------------------");
				return map;
			} else if (action.equalsIgnoreCase("delete")) {
			   map= adminGenService.deletePackCategory(params.get("PACKAGE_TYPE"), params.get("PACKAGE_CATEGORY"));
				if (IndoUtil.isSuccess(map)) {
					map.put("Status","SUCCESS");
				}
				else{
					map.put("Status","FAILTURE");
				}
				log.info("-----------------END------------------------");
				return map;
			}
			}catch(Exception ce){
				log.info("Indo-218- PackageController.getPackCategory() ce "+IndoUtil.getFullLog(ce));
				IndoUtil.populateErrorMap(map, "Indo-100", "Failed to get package category.", 0);
			}
			return null;
		}
		@RequestMapping(value = "/getStore")
		public @ResponseBody Map<String,?> getStore(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
			String action = params.get("action");
			log.info("-----------------START------------------------");
			Map<String,Object> map=new HashMap<String,Object>();
			List list=null;
			try{
				log.info("PackageController.getStore() params " + params);
				if (action.equalsIgnoreCase("edit")) {
					list = adminGenService.getStore(params.get("ID"), params.get("NAME"));
					if(list.size() >0){
						map.put("Status","SUCCESS");
						map.put("list",list);
					}else {
						map.put("Status","FAILTURE");
					}
					log.info("-----------------END------------------------");
				return map;
			} else if (action.equalsIgnoreCase("show")) {
				 list = adminGenService.getStore(params.get("ID"), params.get("NAME"));
					if (list.size() != 0){
						map.put("Status","SUCCESS");
						map.put("list",list);
					}else {
						map.put("Status","FAILTURE");
					}
			log.info("-----------------END------------------------");
					return map;

			}else if(action.equalsIgnoreCase("delete")) {
				Map<String, Object> data = adminGenService.deleteStore(params.get("ID"), params.get("NAME"));
				if (IndoUtil.isSuccess(data)) {
					map.put("Status","SUCCESS");
				} else {
					map.put("Status","FAILTURE");
				}
			log.info("-----------------END------------------------");
				return map;
			}
			}catch(Exception ce){
				log.error("PackageController.newStoreInfo() ce " + IndoUtil.getFullLog(ce));
			}
			log.info("-----------------EDN------------------------");
			return null;
		}


		@RequestMapping(value = "newStore", method = RequestMethod.POST)
		public  @ResponseBody Map<String, Object>  newStoreInfo(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
			log.info("-----------------START------------------------");
			log.info("PackageController.newStore(-)  params : "+params);
			Map<String,Object> data=new HashMap<String,Object>();
			try {
				 data = adminGenService.newStore(params.get("StoreID"), params.get("StoreName"), params.get("City"),params.get("Address"), params.get("Longitude"), params.get("LattiTude"),params.get("StoreDescription"));
				if (IndoUtil.isSuccess(data)) {
					data.put("Status","SUCCESS");
				} else {
					data.put("Status","FAILTURE");
				}
				log.info("-----------------END------------------------");				
				return data;
			} catch (Exception ce) {
				IndoUtil.populateErrorMap(data, "Indo-100", "Failed to get package category.", 0);
				log.error("PackageController.newStore() ce " + IndoUtil.getFullLog(ce));
			}
			return null;
		}

		@RequestMapping(value = "newPackInfor", method = RequestMethod.POST)
		public @ResponseBody Map<String,?> newPackInfor(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
			log.info("-----------------START------------------------");
			log.info("PackageController.newPackInfor(-)  params : "+params);
			Map<String,Object> map=new HashMap<String,Object>();
			try {
				 map = adminGenService.newPackInfor(params.get("PACKAGE_NAME"), params.get("PACKAGE_GROUP"),
						params.get("TARIFF"), params.get("QUOTA"), params.get("GIFT_FLAG"), params.get("BUY_FLAG"),
						params.get("BUY_EXTRA_FLAG"), params.get("PARAM"), params.get("COMMENTS"),
						params.get("PACKAGE_CATEGORY"), params.get("UNREG_KEYWORD"), params.get("UNREG_PARAM"),
						params.get("SERVICECLASS"), params.get("DESCRIPTION"), params.get("KEYWORD"));
				if (IndoUtil.isSuccess(map)) {
					map.put("Status","SUCCESS");
				} 
				else {
					map.put("Status","FAILTURE");
				}
			log.info("-----------------END------------------------");
				return map;
			}catch(Exception ce) {
				IndoUtil.populateErrorMap(map, "Indo-100", "Failed to get package information.", 0);
				log.error("PackageController.newPackInfor() ce " + IndoUtil.getFullLog(ce));
			}
			return null;
		}
		
		
		
		

		@RequestMapping(value = "EditStoreInfo", method = RequestMethod.POST)
		public  @ResponseBody Map<String,?> EditStoreInfo(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
			log.info("-----------------START------------------------");
				Map<String,Object> map=new HashMap<String,Object>();
			try {
				log.info("PackageController.EditStoreInfo() Params :" + params);
				 map = adminGenService.EditStore(params.get("StoreID"), params.get("StoreName"),params.get("CITY"), params.get("Address"), params.get("Longitude"), params.get("LattiTude"), params.get("StoreDescription"));
				
				if (IndoUtil.isSuccess(map)) {
					map.put("Status", "SUCCESS");
				} else {
					map.put("Status", "FAILTURE");
				}
				log.info("-----------------END------------------------");
				return map;
			} catch (Exception ce) {
				map.put("Status", "FAILTURE");
				log.error("PackageController.EditStoreInfo() ce " + IndoUtil.getFullLog(ce));
			}
			return null;
		}

		@RequestMapping(value = "/NewSspPackage", method = RequestMethod.POST)
		public @ResponseBody  Map<String,?> NewSspPackage(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {

				log.info("-----------------START------------------------");	
			Map<String, Object> data=null;
			try {
				log.info("PackageController.NewSspPackage(-) Params : " + params);
				 data = adminGenService.NewSspPackage(params.get("PackageCode"), params.get("Keyword"),
						params.get("ShortCode"));
				if (IndoUtil.isSuccess(data)) {
					data.put("Status", "SUCCESS");
				}else {
					data.put("Status", "FAILTURE");
				}
				log.info("-----------------END------------------------");
				return data;
				
			} catch (Exception ce) {
				IndoUtil.populateErrorMap(data, "Indo-218","No Data Found.",0);
				log.error("PackageController.NewSspPackage(-) ce " + IndoUtil.getFullLog(ce));
			}
			return null;
		}

		@RequestMapping("/updateOffer")
		public ModelAndView updateOffer(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
			Map<String, Object> map = new HashMap<String, Object>();
			log.info("-----------------START------------------------");
			log.info("PackageController.getpackage() params " + params);
			map = adminGenService.updatePackag(params.get("PACKAGE_NAME_EN"), params.get("TARIFF"), params.get("BENEFIT_EN"),params.get("BENEFIT_ID"), params.get("GIFT_FLAG"), params.get("BUY_FLAG"), params.get("BUY_EXTRA_FLAG"),params.get("PARAM"), params.get("KEYWORD"), params.get("UNREG_KEYWORD"), params.get("UNREG_PARAM"),params.get("PACKAGE_NAME_ID"));
			  List list = adminGenService.getallPackage();
	        log.info("-----------------END------------------------");
			return new ModelAndView("showallPackage", "list", list);
		}

		
		
		@RequestMapping(value = "/createUser" ,method=RequestMethod.POST)
		public  @ResponseBody  Map<String,?> createUser(HttpServletRequest req,@RequestParam Map<String, String> params, Model model) {
			Map<String,Object> map=new HashMap<String,Object>();
			log.info("-----------------START------------------------");
			try{
					map=adminGenService.createUser(params.get("userId"),params.get("name"),params.get("msisdn"),params.get("email"),params.get("type"),params.get("password"),params.get("status"),params.get("gallery"));
						
					if(IndoUtil.isSuccess(map)){
						map.put("Status","SUCCESS");
					}
					else{
						map.put("Status","FAILTURE");
					}
					return map;
			    }catch(Exception ce){
					log.info("Indo-218- IJoinController.createAdminUser() ce "+IndoUtil.getFullLog(ce));
					IndoUtil.populateErrorMap(map, "Indo-100", "Failed to create admin user.", 0);
			}
			log.info("-----------------END------------------------");
			return null;
		}
		
		@RequestMapping(value = "/deleteUser" ,method=RequestMethod.POST)
		public  @ResponseBody  Map<String,?> deleteUser(HttpServletRequest req,@RequestParam Map<String, String> params, Model model) {
			Map<String,Object> map=new HashMap<String,Object>();
			log.info("-----------------START------------------------");
			try{
					map=adminGenService.deleteUser(params.get("userId"));
					if(IndoUtil.isSuccess(map)){
						map.put("Status","SUCCESS");
					}
					else{
						map.put("Status","FAILTURE");
					}
					return map;
			    }catch(Exception ce){
					log.info("Indo-218- IJoinController.deleteUser() ce "+IndoUtil.getFullLog(ce));
					IndoUtil.populateErrorMap(map, "Indo-100", "Failed to delete user.", 0);
			}
			log.info("-----------------END------------------------");
			return null;
		}

		@RequestMapping(value = "/getUser" ,method=RequestMethod.POST)
		public  @ResponseBody  Map<String,?> getUser(HttpServletRequest req,@RequestParam Map<String, String> params, Model model) {
			Map<String,Object> map=new HashMap<String,Object>();
			log.info("-----------------START------------------------");
			try{
					map=adminGenService.getUser(params.get("userId"));
					if(IndoUtil.isSuccess(map)){
						map.put("Status","SUCCESS");
					}
					else{
						map.put("Status","FAILTURE");
					}
					return map;
			    }catch(Exception ce){
					log.info("Indo-218- IJoinController.deleteUser() ce "+IndoUtil.getFullLog(ce));
					IndoUtil.populateErrorMap(map, "Indo-100", "Failed to delete user.", 0);
			}
			log.info("-----------------END------------------------");
			return null;
		}
		
		@RequestMapping(value = "/editUser" ,method=RequestMethod.POST)
		public  @ResponseBody  Map<String,?> editUser(HttpServletRequest req,@RequestParam Map<String, String> params, Model model) {
			Map<String,Object> map=new HashMap<String,Object>();
			log.info("-----------------START------------------------"+params);
			try{
				
					map=adminGenService.editUser(params.get("msisdn"),params.get("name"),params.get("userId"),params.get("type"),params.get("password"),params.get("status"),params.get("email"),params.get("gallery"));	
					if(IndoUtil.isSuccess(map)){
						map.put("Status","SUCCESS");
					}
					else{
						map.put("Status","FAILTURE");
					}
					return map;
			    }catch(Exception ce){
					log.info("Indo-218- IJoinController.deleteUser() ce "+IndoUtil.getFullLog(ce));
					IndoUtil.populateErrorMap(map, "Indo-100", "Failed to delete user.", 0);
			}
			log.info("-----------------END------------------------");
			return null;
		}
	
	
	@RequestMapping(value = "/getStoreAjaxID", method = RequestMethod.GET)
	public  @ResponseBody  Map<String,?>  getStoreAjaxID(HttpServletRequest req) {
		log.info("-----------------START------------------------");
		log.info("PackageController.getStoreAjaxID(-) start.");
		Map<String,Object> map=new HashMap<String,Object>();
		try{		
		map= adminGenService.getStoresInfo(req.getParameter("action"));
		if(IndoUtil.isSuccess(map)){
			map.put("Status","SUCCESS");
			map.put("list",map.get("list"));
		}
		else {
			map.put("Status","FAILTURE");
			map.put("list",map.get("list"));
		}

		log.info("-----------------END------------------------");
		return map;
		}catch(Exception ce){
			log.info("Indo-218- PackageController.getStoresInfo() ce "+IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Indo-100", "Failed to get Store infor.", 0);
		}
		log.info("-----------------END------------------------");
		return null;
	}

	@RequestMapping(value = "/createUsers", method = RequestMethod.GET)
	public ModelAndView createUsers(HttpServletRequest req) {
		log.info("PackageController.createUsers(-) start.");
		return new ModelAndView("createUsers");
	}

	@RequestMapping(value = "/getUsesDetails", method = RequestMethod.GET)
	public ModelAndView createUsersSearch(HttpServletRequest req) {
		log.info("PackageController.getUsesDetails(-) start.");
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			map = adminGenService.getUsesDetails();
			if (IndoUtil.isSuccess(map)) {
				return new ModelAndView("userDetails", "list", map.get("list"));
			}
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(map, "Saturn-1024", "", 0);
			log.error("Saturn-2051- GenericServiceImpl.getUsesDetails() ce " + IndoUtil.getFullLog(ce));
		}
		return new ModelAndView("userDetails");
	}

	
	@RequestMapping(value = "/userGallery", method = RequestMethod.GET)
	public ModelAndView userGallery(HttpServletRequest req) {
		log.info("-----------------START------------------------");
		Map<String,Object> map=new HashMap<String,Object>();
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		log.info("PackageController.userGallery(-) start.");
		try{
		String UserID=SessionUtil.getLoginVO(req).getUserid();
			map= adminGenService.userGallery(UserID); 
			if(IndoUtil.isSuccess(map)){
				 list=(List<Map<String, Object>>) map.get("list");	
			 }
		}catch(Exception ce){
			log.info("Indo-218- PackageController.userGallery() ce "+IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Indo-100", "Failed get Gallery.", 0);
		}
		log.info("-----------------END------------------------");
		return new ModelAndView("gallery", "list", list);
	}
	
	
	
	@RequestMapping(value = "/getPackaGroup", method = RequestMethod.GET)
	public  @ResponseBody  Map<String,?>  getPackaGroup(HttpServletRequest req) {
		log.info("-----------------START------------------------");
		log.info("PackageController.getPackaGroup(-) start.");
		Map<String,Object> map=new HashMap<String,Object>();
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		try{
		map = adminGenService.getPackaGroup(req.getParameter("action"));
		if(IndoUtil.isSuccess(map)){
			map.put("Status","SUCCESS");
			map.put("list",map.get("list"));
		}
		else {
			map.put("Status","FAILTURE");
			map.put("list",map.get("list"));
		}
		log.info("-----------------END------------------------");
		return map;
	
		}catch(Exception ce){
			log.info("Indo-218- PackageController.getPackaGroup() ce "+IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Indo-100", "Failed to login.", 0);
		}
		return null;
	}
	
	@RequestMapping(value = "/getPackCategory", method = RequestMethod.GET)
	public  @ResponseBody  Map<String,?>  getPackCategory(HttpServletRequest req) {
		log.info("-----------------START------------------------");
		log.info("PackageController.getPackCategory(-) start.");
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			map = adminGenService.getPackCategory(req.getParameter("action"));
		if(IndoUtil.isSuccess(map)){
			map.put("Status","SUCCESS");
			map.put("list",map.get("list"));
		}
		else {
			map.put("Status","FAILTURE");
			map.put("list",map.get("list"));
		}
		log.info("-----------------END------------------------");
		return map;
		}catch(Exception ce){
			log.info("Indo-218- PackageController.getPackCategory() ce " + IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Indo-100", "Failed to get package category.", 0);
		}
		return null;
	}

	
	@RequestMapping(value = "/getPackCode", method = RequestMethod.GET)
	public  @ResponseBody  Map<String,?>  getPackCode(HttpServletRequest req) {
		log.info("-----------------START------------------------");
		log.info("PackageController.getPackCode(-) aciton: " + req.getParameter("action"));
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			map = adminGenService.getPackCode(req.getParameter("action"));
		if(IndoUtil.isSuccess(map)){
			map.put("Status","SUCCESS");
			map.put("list",map.get("list"));
		}
		else {
			map.put("Status","FAILTURE");
			map.put("list",map.get("list"));
		}
		log.info("-----------------END------------------------");
		return map;
		}catch(Exception ce){
			log.info("Indo-218- PackageController.getPackCode() ce " + IndoUtil.getFullLog(ce));
			IndoUtil.populateErrorMap(map, "Indo-100", "Failed to get Package codes.", 0);
			}
		return null;
	}
	@RequestMapping(value = "/uploadNPDF", method = RequestMethod.POST)
	public @ResponseBody Map<String,?> uploadNPDF(@RequestPart("pdf_file") MultipartFile file1,HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
		Map<String,Object> map=new HashMap<String,Object>();
		log.info("-----------------START------------------------");
		log.info("PackageController.newOffer(-)  params : "+params);
		try {
				String fName = IndoUtil.getAlphaNumeric(16)+".pdf";
				File f = new File("/var/www/pdf/"+fName);
				if(!f.exists()){
					f.createNewFile();
				}
				file1.transferTo(f);
				log.info("PackageController.uploadPDF() pdf_file "+file1.getOriginalFilename());
				genService.updloadPDF(IndoUtil.prefix62(params.get("msisdn")), fName);
				log.info("-----------------End------------------------");
				return map;
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(map, "Indo-218","Failed to insert New Offer.",0);
			log.error("PackageController.newOffer() ce " + IndoUtil.getFullLog(ce));
		}
		return null;
	}
}
