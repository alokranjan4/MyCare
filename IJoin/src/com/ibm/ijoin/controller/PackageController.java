package com.ibm.ijoin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
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
import com.ibm.ijoin.util.IndoServiceProperties;
import com.ibm.ijoin.util.IndoUtil;

@Controller
@EnableWebMvc	
public class PackageController {
	@Autowired
	GenericService genService;

	private static Logger log = Logger.getLogger("ijoinLogger");
	IndoServiceProperties confProp = IndoServiceProperties.getInstance();
	Properties prop = confProp.getConfigSingletonObject();

	@RequestMapping(value = "/home", produces = "application/json")
	public String home(HttpSession session) {
		log.info("Entering Home ");
		return "home";
	}
	


	@RequestMapping(value = "/register")
	public String register(HttpServletRequest req) {
		log.info("-----------------START------------------------");
		Map<String, Object> map = new HashMap<String, Object>();
		String loginPage = null;
		try {
			String userId = req.getParameter("userid");
			String password = req.getParameter("pwd");
			map = genService.validateUser(userId, password);
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

	@RequestMapping(value = "/registerUser")
	public String register(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
		log.info("-----------------START------------------------");
		try {
			Map<String, Object> map = genService.registerUser(params.get("userid"), params.get("pwd"),
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
	
	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	public String changePassword(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
		try{
			log.info("-----------------START------------------------");
		Map<String, Object> map = new HashMap<String, Object>();
		if(params.get("newPassword").toString().equalsIgnoreCase(params.get("ConfirmPassword").toString())){
			HttpSession session = req.getSession(false);
			map = genService.changePassword(params.get("newPassword"), session.getAttribute("LoginID").toString());
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
				map = genService.forgot(params.get("LoginID"), params.get("EmailID"));
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
				}
		}
		else{
			model.addAttribute("msg", IndoUtil.eMsg("User ID and Email ID can't be Empty."));
		}
		} catch (Exception ce) {
			model.addAttribute("msg", IndoUtil.eMsg("Failed to change password. Please try again."));
			log.error("PackageController.register() ce " + IndoUtil.getFullLog(ce));
		}
		return "login";
	}
	

	/*
	@RequestMapping(value = "/newOffer", method = RequestMethod.POST)
	public @ResponseBody Map<String,?>  newOffer(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
		Map<String,Object> map=new HashMap<String,Object>();
		try {
			log.info("params : : " + params);
			map = genService.NewOffer(params.get("offer_id"),params.get("pack_code"),params.get("tariff"),params.get("offer_Name_ID"),params.get("offer_Name_EN"),params.get("banefit_ID"),params.get("banner_Image_En"),params.get("banefit_EN"),params.get("keyword"),params.get("param"),params.get("offer_Link"),params.get("offer_Type"),params.get("customer_Type"),params.get("banner_Image_ID"));
			if (IndoUtil.isSuccess(map)) {
				map.put("Status","SUCCESS");
			}else{
				map.put("Status","FAILURE");	
			}
			return map;
		} catch (Exception ce) {
			model.addAttribute("msg", IndoUtil.eMsg("Failed to insert New Offer. Please try again."));
			log.error("PackageController.UploadOffer() ce " + IndoUtil.getFullLog(ce));
		}
		return null;
	}*/

	
	
	@RequestMapping(value = "/newOffer", method = RequestMethod.POST)
	public @ResponseBody Map<String,?> newOffer(@RequestPart("banner_Image_ID") MultipartFile file1,@RequestPart("banner_Image_En") MultipartFile file2,HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
		Map<String,Object> map=new HashMap<String,Object>();
		try {
			log.info("params : : " + params);
			log.info("file :"+file1);
			log.info("file :"+file2);
				byte[] imageID=null;
				byte[] imageEn=null;
				if(null!=file1||file2!=null){
					System.out.println(file1.getSize());
					imageID=file1.getBytes();
					imageEn=file2.getBytes();
				}
				map = genService.NewOffer(params.get("offer_id"),params.get("pack_code"),params.get("tariff"),params.get("offer_Name_ID"),params.get("offer_Name_EN"),params.get("banefit_ID"),imageEn,params.get("banefit_EN"),params.get("keyword"),params.get("param"),params.get("offer_Link"),params.get("offer_Type"),params.get("customer_Type"),imageID);
				if (IndoUtil.isSuccess(map)) {
					map.put("Status","SUCCESS");
				}else{
					map.put("Status","FAILURE");	
				}
				return map;
			
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(map, "Indo-218","Failed to insert New Offer.",0);
			model.addAttribute("msg", IndoUtil.eMsg("Failed to insert New Offer. Please try again."));
			log.error("PackageController.UploadOffer() ce " + IndoUtil.getFullLog(ce));
		}
		return null;
	}
	
	@RequestMapping(value = "/newPackage1", method = RequestMethod.POST)
	public @ResponseBody Map<String,?> newPackage1(@RequestPart("BannerImage") MultipartFile file,HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
		Map<String,Object> map=new HashMap<String,Object>();
		System.out.println(file);
		try {
		byte[] imagebyte=null;
		if(null!=file){
			System.out.println(file.getSize());
			imagebyte=file.getBytes();
		}
			log.info("newPackage1 : : " + params);
			Map<String, Object> data = genService.NewPackage1(params.get("PackageType"),params.get("PackageCategory"),params.get("description"),params.get("packageCategoryID"),params.get("catSeq"),imagebyte);
		
			if (IndoUtil.isSuccess(data)) {
				map.put("Status","SUCCESS");

			}else{
				map.put("Status","FAILTURE");
			}
			return map;
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(map, "Indo-218","package category insertion fail.",0);
			log.error("PackageController.newPackage1() ce " + IndoUtil.getFullLog(ce));
		}
		return null;
	}

	
	
	//updated one
		
	
	  @RequestMapping(value = "/EditPackage1", method = RequestMethod.POST)
     public @ResponseBody Map<String,?> EditPackage1(@RequestPart("edit_Banner_Image") MultipartFile file,HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
		Map<String,Object> map=new HashMap<String,Object>();
		System.out.println(file);
		
		try {
		byte[] imagebyte=null;
		if(null!=file){
			System.out.println(file.getSize());
			imagebyte=file.getBytes();
		}
			log.info("params: " + params);
			Map<String, Object> data = genService.EditPackage1(params.get("edit_Package_Type"),params.get("edit_Package_Category"),params.get("edit_description"),params.get("edit_package_CategoryID"),params.get("edit_cat_Seq"),imagebyte);
			if (IndoUtil.isSuccess(data)) {
				map.put("Status","SUCCESS");
			} else {
				map.put("Status","FAILTURE");
			}
			return map;
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(map, "Indo-218","Editp Package1 fail.",0);
			log.error("PackageController.EditPackage1() ce " + IndoUtil.getFullLog(ce));
		}
		return null;
	}

	/*
	@RequestMapping(value = "/EditPackage1", method = RequestMethod.POST)
	public @ResponseBody Map<String,?> EditPackage1(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
		Map<String,Object> map=new HashMap<String,Object>();
		try {
			log.info("params: " + params);
			Map<String, Object> data = genService.EditPackage1(params.get("edit_Package_Type"),params.get("edit_Package_Category"),params.get("edit_description"),params.get("edit_package_CategoryID"),params.get("edit_cat_Seq"),params.get("edit_Banner_Image"));
			if (IndoUtil.isSuccess(data)) {
				map.put("Status","SUCCESS");
			} else {
				map.put("Status","FAILTURE");
			}
			return map;
		} catch (Exception ce) {
			map.put("Status","FAILTURE");
			log.error("PackageController.EditPackage1() ce " + IndoUtil.getFullLog(ce));
		}
		return null;
	}
  */
	/* old one  
	@RequestMapping(value = "/EditOffer", method = RequestMethod.POST)
	public  @ResponseBody  Map<String,?> EditOffer(HttpServletRequest req,@RequestParam Map<String, String> params, Model model) {
		Map<String,Object> map=new HashMap<String,Object>();
	
		try {
			log.info("params : " + params);
			map=genService.EditOffer(params.get("edit_OfferID"),params.get("edit_PackageCode"),params.get("edit_Tariff"),params.get("edit_OfferNameID"),params.get("edit_OfferNameEN"),params.get("edit_BenefitID"),params.get("edit_BannerImageEN"),params.get("edit_BenefitEN"),params.get("edit_Keyword"),params.get("edit_Param"),params.get("edit_offerLink"),params.get("edit_OfferType"),params.get("edit_CustomerType"),params.get("edit_Banner_Image_ID"));
			
			if (IndoUtil.isSuccess(map)) {
			  map.put("Status", "SUCCESS");
			} else {
			  map.put("Status", "FAILTURE");
			}
			 return map;
		} catch (Exception ce) {
			map.put("Status", "FAILTURE");
			log.error("PackageController.EditOffer() ce " + IndoUtil.getFullLog(ce));
		}
		return null;
	}
  */
	  @RequestMapping(value = "/EditOffer", method = RequestMethod.POST)
	  public @ResponseBody Map<String,?> EditOffer(@RequestPart("edit_BannerImageID") MultipartFile file1,@RequestPart("edit_BannerImageEN") MultipartFile file2,HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
			Map<String,Object> map=new HashMap<String,Object>();
			try {
				log.info("params : : " + params);
				log.info("file :"+file1);
				log.info("file :"+file2);
					byte[] imageID=null;
					byte[] imageEn=null;
					if(null!=file1||file2!=null){
						System.out.println(file1.getSize());
						imageID=file1.getBytes();
						imageEn=file2.getBytes();
					}
					log.info("params : " + params);
					map=genService.EditOffer(params.get("edit_OfferID"),params.get("edit_PackageCode"),params.get("edit_Tariff"),params.get("edit_OfferNameID"),params.get("edit_OfferNameEN"),params.get("edit_BenefitID"),imageEn,params.get("edit_BenefitEN"),params.get("edit_Keyword"),params.get("edit_Param"),params.get("edit_offerLink"),params.get("edit_OfferType"),params.get("edit_CustomerType"),imageID);
					
					if (IndoUtil.isSuccess(map)) {
					  map.put("Status", "SUCCESS");
					} else {
					  map.put("Status", "FAILTURE");
					}
					 return map;
				} catch (Exception ce) {
					IndoUtil.populateErrorMap(map, "Indo-218","Edition offer fail.",0);
					log.error("PackageController.EditOffer() ce " + IndoUtil.getFullLog(ce));
				}
				return null;
			}
	  
	@RequestMapping(value = "/EditSSPOffer", method = RequestMethod.POST)
	public  @ResponseBody Map<String, ?> EditSSPOffer(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
		Map<String, Object> data=null; 
		log.info("-----------------START------------------------");
		try {
			log.info("PackageController.EditSSPOffer() params : " + params);
			 data = genService.EditSSPOffer(params.get("PACK_CODE"), params.get("Keyword"),params.get("ShortCode"));
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
/*
	@RequestMapping(value = "/newPackCat", method = RequestMethod.POST)
	public  @ResponseBody Map<String, ?> newPackCat(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
		Map<String, Object> data=new HashMap<String,Object>();
		try {
			log.info("newPackCat : : " + params);
			if (IndoUtil.isSuccess(data)) {
	         data.put("Staus","SUCCESS");
			}else {
			 data.put("Staus","FAILTURE");
			}
			return data;
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(data, "Indo-218","No Data Found.",0);
			model.addAttribute("msg", IndoUtil.eMsg("Failed to update SSP Offer. Please try again."));
			log.error("PackageController.EditSSPOffer() ce " + IndoUtil.getFullLog(ce));
		}
		return null;
	}
	*/
	
	@RequestMapping(value = "/editPackage2", method = RequestMethod.POST)
	public @ResponseBody Map<String,?> editPackage2(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
		Map<String,Object> map=new HashMap<String,Object>();
		try {
			log.info("editPackage2 : : " + params);
			map = genService.EditPackage2(params.get("PACK_NAME"), params.get("PACK_GROUP"),params.get("TARIFF2"), params.get("QUOTA2"), params.get("GIFT_FLAG2"), params.get("BUY_FLAG2"),params.get("BUY_EXTRA_FLAG2"), params.get("PARAM2"), params.get("COMMENTS2"),
					params.get("PACKAGE_CATEGORY2"), params.get("UNREG_KEYWORD2"), params.get("UNREG_PARAM2"),params.get("SERVICECLASS2"), params.get("DESCRIPTION2"), params.get("KEYWORD2"));
			if (IndoUtil.isSuccess(map)) {
				map.put("Status","SUCCESS");
			} else {
				map.put("Status","FAILTURE");
			}
			return map;
		} catch (Exception ce) {
			IndoUtil.populateErrorMap(map, "Indo-218","package information edition fail.",0);
			log.error("PackageController.editPackage2() ce " + IndoUtil.getFullLog(ce));
		}
		return null;
	}
	
	/*
	@RequestMapping(value = "/changeHotOffer", method = RequestMethod.GET)
	public String changeHotOffer(HttpServletRequest req) {
		log.info("PackageController.changeHotOffer(-) Get start.");
		return "changeHotOffer";
	}

	@RequestMapping(value = "/updatePackge", method = RequestMethod.GET)
	public String changePackge(HttpServletRequest req) {
		log.info("PackageController.changePackge(-) Get start.");
		return "updatePackge";
	}
*/
	@RequestMapping(value = "/showallOfferSSP", method = RequestMethod.GET)
	public ModelAndView showallOfferSSP(HttpServletRequest req) {
		
		log.info("-----------------START------------------------");
		Map<String,Object> map=new HashMap<String,Object>();
		List list=null;
		try{
		log.info("PackageController.showallOfferSSP(-) start.");
		map = genService.getAllOfferSSP();
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

	@RequestMapping(value = "/showallOffer", method = RequestMethod.GET)
	public ModelAndView showallOffer(HttpServletRequest req) {
		log.info("PackageController.showallOffer(-) start.");
		List list = genService.getAllOffer();
		log.info("PackageController.showallOffer(-) start. List : "+list);
		return new ModelAndView("showallOffer", "list", list);
	}

	@RequestMapping(value = "/showallStore", method = RequestMethod.GET)
	public ModelAndView showallStore(HttpServletRequest req) {
		log.info("PackageController.showallOffer(-) start.");
		List list = genService.getAllStore();
		return new ModelAndView("showallStore", "list", list);
	}

	@RequestMapping(value = "/showallPackage1", method = RequestMethod.GET)
	public ModelAndView showallPackage1(HttpServletRequest req) {
		log.info("PackageController.showallPackage1(-) start.");
		List list = genService.getAllPackage1();
		return new ModelAndView("showallPackage1", "list", list);
	}

	@RequestMapping(value = "/showallPackage2", method = RequestMethod.GET)
	public ModelAndView showallPackage2(HttpServletRequest req) {
		log.info("PackageController.showallPackage2(-) start.");
		List list = genService.getAllPackage2();
		return new ModelAndView("showallPackage2", "list", list);
	}

	@RequestMapping(value = "/getOffer" ,method=RequestMethod.POST)
	public  @ResponseBody  Map<String,?> getOffer(HttpServletRequest req,@RequestParam Map<String, String> params, Model model) {
		Map<String,Object> map=new HashMap<String,Object>();
		log.info("-----------------START------------------------");
		try{
		String action = params.get("action");
		log.info("getpackage action :======== " + action);
		System.out.println("PackageController.getpackage() params " + params);
		if (action.equalsIgnoreCase("edit")) {
			System.out.println("edit");
			List list = genService.getOffer(params.get("Offer_ID"), params.get("PACKAGE_Code"));
			if (list.size() != 0){
				map.put("Status","SUCCESS");
				map.put("list",list);
			}
			else {
				map.put("Status","FAILTURE");
				map.put("list",list);
			}
			return map;
		} else if (action.equalsIgnoreCase("show")) {
			List list = genService.getOffer(params.get("Offer_ID"), params.get("PACKAGE_Code"));
			if (list.size() != 0){
				map.put("Status","SUCCESS");
				map.put("list",list);
			}
			else {
				map.put("Status","FAILTURE");
				map.put("list",list);
			}
			return map;
		} else if (action.equalsIgnoreCase("delete")) {
			Map<String, Object> data = genService.deleteOffer(params.get("Offer_ID"), params.get("PACKAGE_Code"));
			
			if (IndoUtil.isSuccess(data)) {
				map.put("Status","SUCCESS");
			}else{
				map.put("Status","FAILTURE");
			}
		 
			return map;
		} else if (action.equalsIgnoreCase("New")) {
			map.put("Status","SUCCESS");
			return map;
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		log.info("-----------------END------------------------");
		return null;
	}

	@RequestMapping(value = "/getPackage2",method=RequestMethod.POST)
	public @ResponseBody Map<String,?>  getPackage2(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
		 Map<String,Object> map=new HashMap<String,Object>();
		 List list=null;
		 log.info("-----------------START------------------------");
		 try{
			 String action = params.get("action");
			 log.info("getPackage2 action :======== " + action);
			 System.out.println("IM2.getPackage2() params " + params);
			 	if (action.equalsIgnoreCase("edit")) {
			 		 list = genService.getPackage2(params.get("PACKAGE_NAME1"), params.get("PACKAGE_GROUP1"));
			 	if (list.size() > 0){
			 		map.put("Status","SUCCESS");
			 		map.put("list",list);
			 	}else {
			 		map.put("Status","FAILTURE");
			 		map.put("list",list);
			 	}
			 		return map;
			 }else if(action.equalsIgnoreCase("show")) {
			
				 list = genService.getPackage2(params.get("PACKAGE_NAME1"), params.get("PACKAGE_GROUP1"));
				 if (list.size() > 0){
					 	map.put("Status","SUCCESS");
					 	map.put("list",list);
				 }else{
					 	map.put("Status","FAILTURE");
					 	map.put("list",list);
			     }
			return map;
		}else if(action.equalsIgnoreCase("delete")) {
			Map<String, Object> data = genService.deletePackage2(params.get("PACKAGE_NAME1"),params.get("PACKAGE_GROUP1"));
			if (IndoUtil.isSuccess(data)) {
				map.put("Status","SUCCESS");
		   }else{
				map.put("Status","FAILTURE");
			}
			return map;
		}else if(action.equalsIgnoreCase("New")) {
			map.put("Status","SUCCESS");
			return map;
		}
		 }catch(Exception ce){
			 log.error("PackageController.newStoreInfo() ce " + IndoUtil.getFullLog(ce));
		 }
		log.info("-----------------END------------------------");
		return null;
	}

	@RequestMapping(value = "/getPackage1")
	public @ResponseBody Map<String,?> getPackage1(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
		String action = params.get("action");
		log.info("-----------------START------------------------");
		Map<String,Object> map=new HashMap<String,Object>();
		List list =null;
		try{
		System.out.println("PackageController.getPackage1() params " + params);
		if (action.equalsIgnoreCase("edit")) {
			System.out.println("edit option");
			 list = genService.getPackage1(params.get("PACKAGE_TYPE"), params.get("PACKAGE_CATEGORY"));
			if (list.size() > 0){
				map.put("Status","SUCCESS");
				map.put("list",list);
			}
			else {
				map.put("Status","FAILTURE");
				map.put("list",list);
			}
			return map;
		} else if (action.equalsIgnoreCase("show")) {
			log.info("Show list :");
			 list = genService.getPackage1(params.get("PACKAGE_TYPE"), params.get("PACKAGE_CATEGORY"));
			if (list.size() > 0){
				map.put("Status","SUCCESS");
				map.put("list",list);
			}
			else {
				map.put("Status","FAILTURE");
			}
			return map;
		} else if (action.equalsIgnoreCase("delete")) {
		   map= genService.deletePackage1(params.get("PACKAGE_TYPE"), params.get("PACKAGE_CATEGORY"));
			if (IndoUtil.isSuccess(map)) {
				map.put("Status","SUCCESS");
			}
			else{
				map.put("Status","FAILTURE");
			}
			return map;

		} else if (action.equalsIgnoreCase("New")) {
			map.put("Status","SUCCESS");
			return map;
		}
		}catch(Exception ce){
			log.error("PackageController.newStoreInfo() ce " + IndoUtil.getFullLog(ce));
		}
		log.info("-----------------END------------------------");
		return null;
	}

	@RequestMapping(value = "/getStore")
	public @ResponseBody Map<String,?> getStore(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
		String action = params.get("action");
		log.info("-----------------START------------------------");
		Map<String,Object> map=new HashMap<String,Object>();
		List list=null;
		try{
		log.info("getStore action :======== " + action);
		System.out.println("PackageController.getpackage() params " + params);
		if (action.equalsIgnoreCase("edit")) {
			System.out.println("edit");
			 list = genService.getStore(params.get("ID"), params.get("NAME"));
			 
			 log.info("list_Size :"+list.size());
			if(list.size() >0){
				map.put("Status","SUCCESS");
				map.put("list",list);
			}else {
				map.put("Status","FAILTURE");
				map.put("list",list);
			}
			log.info("Map: "+map);
			return map;
		} else if (action.equalsIgnoreCase("show")) {
			 list = genService.getStore(params.get("ID"), params.get("NAME"));
				if (list.size() != 0){
					map.put("Status","SUCCESS");
					map.put("list",list);
				}else {
					map.put("Status","FAILTURE");
					map.put("list",list);
				}
				return map;

		} else if (action.equalsIgnoreCase("delete")) {
			Map<String, Object> data = genService.deleteStore(params.get("ID"), params.get("NAME"));
			if (IndoUtil.isSuccess(data)) {
				map.put("Status","SUCCESS");
			} else {
				map.put("Status","FAILTURE");
			}
			return map;
		} else if (action.equalsIgnoreCase("New")) {
			map.put("Status","SUCCESS");
			return map;

		}
		}catch(Exception ce){
			log.error("PackageController.newStoreInfo() ce " + IndoUtil.getFullLog(ce));
		}
		log.info("-----------------EDN------------------------");
		return null;
	}

	/*
	 * @RequestMapping(value="/order",method=RequestMethod.GET) public String
	 * order(HttpServletRequest req){ log.info("PackageController.order(-) start.");
	 * return "order"; }
	 */
	@RequestMapping(value = "newStoreInfo", method = RequestMethod.POST)
	public  @ResponseBody Map<String, Object>  newStoreInfo(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
		Map<String, Object> data=null;
		try {
			log.info("Params : " + params);
			 data = genService.newStore(params.get("StoreID"), params.get("StoreName"), params.get("City"),params.get("Address"), params.get("Longitude"), params.get("LattiTude"),params.get("StoreDescription"));
			if (IndoUtil.isSuccess(data)) {
				data.put("Status","SUCCESS");
			} else {
				data.put("Status","FAILTURE");
			}
			return data;
		} catch (Exception ce) {
			data.put("Status","FAILTURE");
			log.error("PackageController.newStoreInfo() ce " + IndoUtil.getFullLog(ce));
		}
		return null;
	}

	@RequestMapping(value = "newPackage2", method = RequestMethod.POST)
	public @ResponseBody Map<String,?> newPackage2(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
		Map<String,Object> map=new HashMap<String,Object>();
		try {
			log.info("newPackage2 Params : " + params);
			 map = genService.newPackage2(params.get("PACKAGE_NAME"), params.get("PACKAGE_GROUP"),
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
			return map;
		} catch (Exception ce) {
			map.put("Status","FAILTURE");
			log.error("PackageController.newPackage2() ce " + IndoUtil.getFullLog(ce));
		}
		return null;
	}

	@RequestMapping(value = "EditStoreInfo", method = RequestMethod.POST)
	public  @ResponseBody Map<String,?> EditStoreInfo(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
		log.info("-----------------START------------------------");
			Map<String,Object> map=new HashMap<String,Object>();
		try {
			log.info("PackageController.EditStoreInfo() Params :" + params);
			 map = genService.EditStore(params.get("StoreID"), params.get("StoreName"),params.get("CITY"), params.get("Address"), params.get("Longitude"), params.get("LattiTude"), params.get("StoreDescription"));
			
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
			 data = genService.NewSspPackage(params.get("PackageCode"), params.get("Keyword"),
					params.get("ShortCode"));
			if (IndoUtil.isSuccess(data)) {
				data.put("Status", "SUCCESS");
			}else {
				data.put("Status", "FAILTURE");
			}
			log.info("-----------------END------------------------");
			return data;
			
		} catch (Exception ce) {
			data.put("Status", "FAILTURE");
			log.error("PackageController.NewSspPackage(-) ce " + IndoUtil.getFullLog(ce));
		}
		return null;
	}

	@RequestMapping("/updateOffer")
	public ModelAndView updateOffer(HttpServletRequest req, @RequestParam Map<String, String> params, Model model) {
		Map<String, Object> map = new HashMap<String, Object>();
		log.info("-----------------START------------------------");
		log.info("PackageController.getpackage() params " + params);
		map = genService.updatePackag(params.get("PACKAGE_NAME_EN"), params.get("TARIFF"), params.get("BENEFIT_EN"),params.get("BENEFIT_ID"), params.get("GIFT_FLAG"), params.get("BUY_FLAG"), params.get("BUY_EXTRA_FLAG"),params.get("PARAM"), params.get("KEYWORD"), params.get("UNREG_KEYWORD"), params.get("UNREG_PARAM"),params.get("PACKAGE_NAME_ID"));
		  List list = genService.getallPackage();
        log.info("-----------------END------------------------");
		return new ModelAndView("showallPackage", "list", list);
	}

	@RequestMapping(value = "/getSSPOffer",method=RequestMethod.POST)
	public @ResponseBody  Map<String,?> getSSPOffer(HttpServletRequest req, @RequestParam Map<String, String> params) {
		Map<String,Object> map= new HashMap<String,Object>();
		
		log.info("-----------------START------------------------");
		try{
			String action = params.get("action");
			log.info("PackageController.getSSPOffer() params " + params);
				if(action.equalsIgnoreCase("edit")) {
						map = genService.getSSPOffer(params.get("PACK_CODE"));
						if (IndoUtil.isSuccess(map)){
							map.put("Status", "SUCCESS");
						 }else{
							map.put("Status", "FAILURE");
				 }
			log.info("PackageController.getSSPOffer()  Status :"+map.get("Status"));
			return map; 
		}else if (action.equalsIgnoreCase("show")) {
			map = genService.getSSPOffer(params.get("PACK_CODE"));
			if (IndoUtil.isSuccess(map)){
				map.put("Status", "SUCCESS");
			}else{
				map.put("Status", "FAILURE");
			}
			log.info("PackageController.getSSPOffer()  Status :"+map.get("Status"));
			return map; 
		} else if (action.equalsIgnoreCase("delete")) {
			map = genService.deleteSSPOffer(params.get("PACK_CODE"));
		   if(IndoUtil.isSuccess(map)){
			   map.put("Status", "SUCCESS");
		   }
		   else{
			   map.put("Status", "FAILTURE");
		   }
		   log.info("PackageController.getSSPOffer()  Status :"+map.get("Status"));
		   return map;
		} else if (action.equalsIgnoreCase("New")) {
		 map.put("Status", "SUCCESS");
		 log.info("PackageController.getSSPOffer()  Status :"+map.get("Status"));
		 return map;
		}
		//list = genService.getAllOfferSSP();
		log.info("PackageController.getSSPOffer() end");
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Indo-218","No Data Found.",0);
			log.error("PackageController.getSSPOffer() ce " + IndoUtil.getFullLog(ce));
		}
		return null;
	}
	
	@RequestMapping(value = "/getOfferAjaxOfferID", method = RequestMethod.GET)
	public ModelAndView getOfferAjaxCode(HttpServletRequest req) {
		log.info("-----------------START------------------------");
		log.info("PackageController.getOfferAjaxCode(-) aciton: "+req.getParameter("action"));
		List list=genService.getOfferAjaxOfferID(req.getParameter("action"));
		log.info("PackageController.getOfferAjaxCode(-) end");
		return new ModelAndView("showallOffer","Ajexlist",list);
	}
	
	@RequestMapping(value = "/getStoreAjaxID", method = RequestMethod.GET)
	public ModelAndView getStoreAjaxID(HttpServletRequest req) {
		log.info("PackageController.getStoreAjaxID(-) start.");
		log.info("aciton: "+req.getParameter("action"));
		List list=genService.getStoreAjaxID(req.getParameter("action"));
		log.info("PackageController.getStoreAjaxID(-) end ");
		return new ModelAndView("showallStore","Ajexlist",list);
	}
	
	
	
	@RequestMapping(value = "/getPackageInformationAjax", method = RequestMethod.GET)
	public ModelAndView getPackageInformationAjax(HttpServletRequest req) {
		log.info("PackageController.getPackageInformationAjax(-) start.");
		log.info("aciton: "+req.getParameter("action"));
		List list=genService.getPackageInformationAjax(req.getParameter("action"));
		log.info("PackageController.getPackageInformationAjax(-) end");
		return new ModelAndView("showallPackage2","Ajexlist",list);
	}
	
	@RequestMapping(value = "/getPackageCategoryAjax", method = RequestMethod.GET)
	public ModelAndView getPackageCategoryAjax(HttpServletRequest req) {
		log.info("PackageController.getPackageCategoryAjax(-) start.");
		log.info("aciton: "+req.getParameter("action"));
		List list=genService.getPackageCategoryAjax(req.getParameter("action"));
		log.info("PackageController.getPackageCategoryAjax(-) end");
		return new ModelAndView("showallPackage1","Ajexlist",list);
	}
	
	
}
