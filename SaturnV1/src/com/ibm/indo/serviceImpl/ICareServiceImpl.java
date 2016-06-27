/**
 * 
 */
package com.ibm.indo.serviceImpl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.xml.rpc.ServiceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.infinispan.Cache;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.indo.service.ICareService;
import com.ibm.indo.service.XMLService;
import com.ibm.indo.util.CacheUtil;
import com.ibm.indo.util.IndoServiceProperties;
import com.ibm.indo.util.IndoUtil;
import com.ibm.indo.util.IndoXMLParseUtil;
import com.indosat.eai.catalist.mobileagent.CorporateProfileInputType;
import com.indosat.eai.catalist.mobileagent.CorporateProfileOutputType;
import com.isat.catalist.eai.mobileagent.ws.MobileAgentService;
import com.isat.catalist.eai.mobileagent.ws.MobileAgentServiceServiceLocator;
import com.isat.catalist.eai.mobileagent.ws.MobileAgentServiceServiceSoapBindingStub;

/**
 * @author Adeeb
 *
 */
@Service
public class ICareServiceImpl implements ICareService {
	private static Logger log = Logger.getLogger("saturnLoggerV1");
	IndoServiceProperties confProp=IndoServiceProperties.getInstance();
    Properties prop = confProp.getConfigSingletonObject();
	@Autowired
	private XMLService xmlService;
	/* (non-Javadoc)
	 * @see com.ibm.indo.service.ICareService#customerProfile(java.lang.String)
	 */
	@Override
	public Map<String, Object> customerProfile(String msisdn) {
		msisdn= IndoUtil.prefix62(msisdn);
		log.info("ICareServiceImpl.customerProfile() - START");
		Map<String,Object> map = new HashMap<String,Object>();
		String url= "http://10.128.81.78:7800/WLI_EAI_PROCESS/ServletEAISyncExt";
		try{
			Cache<String, Object>  cache = CacheUtil.getInstance().getEntityCache();
			Map<String,Object> cacheMap = (Map<String, Object>) cache.get(msisdn+"_customerProfile");
		//	log.info("ICareServiceImpl.customerProfile() cacheMap "+cacheMap);
			if(null!=cacheMap){
				return cacheMap;
			}else{
				String xml="<?xml version=\"1.0\"?><evw:WSSMessage xmlns:evw=\"com/icare/eai/schema/evWSSGetUserProfile\"><evw:WSSGetUserProfileReq><evw:ServiceNumber>"+msisdn+"</evw:ServiceNumber></evw:WSSGetUserProfileReq></evw:WSSMessage>";
				//log.info("ICareServiceImpl.customerProfile() input xml "+xml);
				Map<String, Object>  data = xmlService.getRawXML(xml, url);
			//	log.info("ICareServiceImpl.customerProfile() evWSSGetUserProfile "+data);
				if(IndoUtil.isSuccess(data)){
					String response = (String) data.get("xml");
					response = response.replaceAll("evw:", "");
					Document xmlDoc = Jsoup.parse(response, "", Parser.xmlParser());
					Elements eles = xmlDoc.select("UserProfile");
					List<Map<String, String>> d = IndoXMLParseUtil.getParentChildXML(eles);
					if(null!= d && d.size()>0){
						if(null!=d.get(0).get("CustBirthDate") && !StringUtils.isEmpty(d.get(0).get("CustBirthDate").toString())){
							d.get(0).put("CustBirthDate", IndoUtil.parseDate(d.get(0).get("CustBirthDate").toString().split("\\s+")[0], "yyyy-MM-dd", "dd MMM yyyy"));
						}
						if(null!=d.get(0).get("BillBirthDate") && !StringUtils.isEmpty(d.get(0).get("BillBirthDate").toString())){
							d.get(0).put("BillBirthDate", IndoUtil.parseDate(d.get(0).get("BillBirthDate").toString().split("\\s+")[0], "yyyy-MM-dd", "dd MMM yyyy"));
						}
						map.put("UserProfile", d.get(0));
						map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("ServiceNumber")));
						map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("ErrorMessage")));
						map.put("Status", "SUCCESS");
						cache.put(msisdn+"_customerProfile",map, 180, TimeUnit.SECONDS);
					}else{
					//	IndoUtil.populateErrorMap(map, "Saturn-700", "Saturn-109",0);
						map.putAll(customerProfileCorporate(msisdn));
					}
					return map;
				}
			}
		}catch(Exception ce){
			log.error("ICareServiceImpl.customerProfile() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("ICareServiceImpl.customerProfile() - END");
		}
		return map;
	}

	/* (non-Javadoc)
	 * @see com.ibm.indo.service.ICareService#getPUK(java.lang.String)
	 */
	@Override
	public Map<String, Object> getPUK(String msisdn) {
		msisdn = IndoUtil.prefix62(msisdn);
		log.info("ICareServiceImpl.getPUK() -- START");
		Map<String,Object> map = new HashMap<String,Object>();
		String url= "http://10.128.81.78:7800/WLI_EAI_PROCESS/ServletEAISyncExt";
		try{
			String xml="<?xml version=\"1.0\"?><evw:WSSMessage xmlns:evw=\"com/icare/eai/schema/evWSSGetPUK\"><evw:WSSGetPUKReq><evw:ServiceNumber>"+msisdn+"</evw:ServiceNumber></evw:WSSGetPUKReq></evw:WSSMessage>";
			Map<String, Object> data = xmlService.getRawXML(xml, url);
			log.info("ICareServiceImpl.getSSPDetails() evWSSGetPUK "+data);
			if(IndoUtil.isSuccess(data)){
				String response = (String) data.get("xml");
				Document xmlDoc = Jsoup.parse(response, "", Parser.xmlParser());
				Elements eles = xmlDoc.select("PUK");
				List<Map<String, String>> d = IndoXMLParseUtil.getParentChildXML(eles);
				if(null!= d && d.size()>0){
					map.put("PUK", d.get(0));
					map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("ServiceNumber")));
					map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("ErrorMessage")));
					map.put("Status", "SUCCESS");
				}else{
					IndoUtil.populateErrorMap(map, "IC-701", "Saturn-109",0);
				}
				return map;
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Saturn-9001", "Saturn-109", 0);
			log.error("ICareServiceImpl.getPUK() - ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("ICareServiceImpl.getPUK() -- END");
		}
		return map;
	}

	/* (non-Javadoc)
	 * @see com.ibm.indo.service.ICareService#topUpHistory(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Map<String, Object> topUpHistory(String msisdn, String startDate, String endDate, String type) {
		msisdn= IndoUtil.prefix62(msisdn);
		log.info("ICareServiceImpl.topUpHistory() -- START");
		Map<String,Object> map = new HashMap<String,Object>();
		String url= "http://10.128.81.78:7800/WLI_EAI_PROCESS/ServletEAISyncExt";
		try{
			String xml="<?xml version=\"1.0\"?><evex:ExtMessage xmlns:evex=\"com/icare/eai/schema/evExtQTopupHistory\"><evex:ExtQTopupHistoryReq><evex:ServiceNumber>"+msisdn+"</evex:ServiceNumber><evex:ServiceType>"+type+"</evex:ServiceType><evex:Source>Portal</evex:Source><evex:StartDate>"+startDate+"</evex:StartDate><evex:EndDate>"+endDate+"</evex:EndDate></evex:ExtQTopupHistoryReq></evex:ExtMessage>";
			Map<String, Object> data = xmlService.getRawXML(xml, url);
		//	log.info("ICareServiceImpl.topUpHistory() evExtQTopupHistory "+data);
			if(IndoUtil.isSuccess(data)){
				String response = (String) data.get("xml");
				Document xmlDoc = Jsoup.parse(response, "", Parser.xmlParser());
				Elements eles = xmlDoc.select("TopupHistory");
				List<Map<String, String>> d = IndoXMLParseUtil.getParentChildXML(eles);
				map.put("TopupHistory", d);
				map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("ServiceNumber")));
				map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("ErrorMessage")));
				map.put("Status", "SUCCESS");
				return map;
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Saturn-9002", "Saturn-109", 0);
			log.error("ICareServiceImpl.topUpHistory() - ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("ICareServiceImpl.topUpHistory() -- END");
		}
		return map;
	}

	/* (non-Javadoc)
	 * @see com.ibm.indo.service.ICareService#usageHistory(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Map<String, Object> usageHistory(String msisdn, String startDate, String endDate, String type) {
		msisdn= IndoUtil.prefix62(msisdn);
		log.info("ICareServiceImpl.usageHistory() -- START");
		Map<String,Object> map = new HashMap<String,Object>();
	//	String url= "http://10.128.81.78:7800/WLI_EAI_PROCESS/ServletEAISyncExt";
		String url = "http://10.128.164.54/hdpapi/shrma?msisdn="+msisdn+"&startDate="+startDate+"&endDate="+endDate;
		try{
		//	String xml="<?xml version=\"1.0\"?><evex:ExtMessage xmlns:evex=\"com/icare/eai/schema/evExtQUsageHistory\"><evex:ExtQUsageHistoryReq><evex:ServiceNumber>"+msisdn+"</evex:ServiceNumber><evex:ServiceType>"+type+"</evex:ServiceType><evex:Source>Portal</evex:Source><evex:StartDate>"+startDate+"</evex:StartDate><evex:EndDate>"+endDate+"</evex:EndDate></evex:ExtQUsageHistoryReq></evex:ExtMessage>";
		//	Map<String, Object> data = xmlService.getRawXML(xml, url);
		//	log.info("ICareServiceImpl.customerProfile() evExtQUsageHistory "+data);
		//	if(IndoUtil.isSuccess(data)){
		//		String response = (String) data.get("xml");
			Document xmlDoc = Jsoup.connect(url).header("Authorization", "Basic cj12JTY2XyhLXT0/RCxLRjo=").header("User-Agent", "MINSAT/3.5/9.0").timeout(60000).get();
		//	log.info("GenericServiceImpl.usageHistory() - response "+xmlDoc.toString());
				Elements eles = xmlDoc.select("Charging");
				List<Map<String, String>> d = IndoXMLParseUtil.getParentChildXML(eles);
				Map<String,Object> dataMap = new LinkedHashMap<>();
				for(Map<String, String> m : d){
					List<Map<String,Object>> li = null;
					String cDate=IndoUtil.parseDate(m.get("TRANSACTIONSTARTTIME"), "yyyyMMddhhmmss","yyyy-MM-dd");
					log.info("ICareServiceImpl.usageHistory() cDate "+cDate);
					li=null==dataMap.get(cDate)?new ArrayList<Map<String,Object>>():(List<Map<String, Object>>) dataMap.get(cDate);
					if(li.size()==0){
						li.add(new LinkedHashMap<String,Object>());li.add(new LinkedHashMap<String,Object>());li.add(new LinkedHashMap<String,Object>());
						/*li.get(0).put("Usage_Date", cDate);*/li.get(0).put("UsageType", "Content");li.get(0).put("Total", 0);
						/*li.get(1).put("Usage_Date", cDate);*/li.get(1).put("UsageType", "VOICE");li.get(1).put("Total", 0);
						/*li.get(2).put("Usage_Date", cDate);*/li.get(2).put("UsageType", "SMS");li.get(2).put("Total", 0);
					}
					if(null!=m.get("TRAFFICTYPES") && m.get("TRAFFICTYPES").equalsIgnoreCase("DATA")){
						int vol = Integer.parseInt(m.get("VOLUME"));
						li.get(0).put("Total", null==li.get(0).get("Total")?vol:vol+Integer.parseInt(li.get(0).get("Total").toString()));
					}else if(null!=m.get("TRAFFICTYPES") && m.get("TRAFFICTYPES").equalsIgnoreCase("VOICE")){
						int vol = Integer.parseInt(m.get("DURATION"));
						li.get(1).put("Total", null==li.get(1).get("Total")?vol:vol+Integer.parseInt(li.get(1).get("Total").toString()));
					}else if(null!=m.get("TRAFFICTYPES") && m.get("TRAFFICTYPES").equalsIgnoreCase("SMS")){
						int vol = Integer.parseInt(m.get("VOLUME"));
						li.get(2).put("Total", null==li.get(2).get("Total")?vol:vol+Integer.parseInt(li.get(2).get("Total").toString()));
					}
					dataMap.put(cDate, li);
				}
				
		//		HistoryShorted hs=new HistoryShorted();
			//	List<Map<String, String>> d1=hs.sortHistoryMap((ArrayList<Map<String, String>>) d);
				map.put("UsageList", dataMap);
				map.put("Msisdn", msisdn);
				//map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("ServiceNumber")));
				map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("ErrorMessage")));
				map.put("Status", "SUCCESS");
				return map;
		//	}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Saturn-9003", "Saturn-109", 0);
			log.error("ICareServiceImpl.usageHistory() - ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("ICareServiceImpl.usageHistory() -- END");
		}
		return map;
	}

	/* (non-Javadoc)
	 * @see com.ibm.indo.service.ICareService#getPromo(java.lang.String)
	 */
	@Override
	public Map<String, Object> getPromo(String msisdn) {
		msisdn= IndoUtil.prefix62(msisdn);
		log.info("ICareServiceImpl.getPromo() -- START");
		Map<String,Object> map = new HashMap<String,Object>();
		String url= "http://10.128.81.78:7800/WLI_EAI_PROCESS/ServletEAISyncExt";
		try{
			String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?> <WSSMessage xmlns=\"com/icare/eai/schema/evWSSGetServiceClassAIR\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"com/icare/eai/schema/evWSSGetServiceClassAIR evWSSGetServiceClassAIR.xsd\"> <WSSGetServiceClassAIR> <ServiceNumber>"+msisdn+"</ServiceNumber> </WSSGetServiceClassAIR> </WSSMessage>";
			Map<String, Object> data = xmlService.getRawXML(xml, url);
		//	log.info("ICareServiceImpl.getPromo() evWSSGetServiceClassAIR "+data);
			if(IndoUtil.isSuccess(data)){
				String response = (String) data.get("xml");
				Document xmlDoc = Jsoup.parse(response, "", Parser.xmlParser());
				Elements eles = xmlDoc.select("ServiceClass");
				List<Map<String, String>> d = IndoXMLParseUtil.getParentChildXML(eles);
				map.put("ServiceClass", d);
				map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("ServiceNumber")));
				map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("ErrorMessage")));
				map.put("Status", "SUCCESS");
				return map;
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Saturn-9004", "Saturn-109", 0);
			log.error("ICareServiceImpl.getPromo() - ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("ICareServiceImpl.getPromo() -- END");
		}
		return map;
	}

	/* (non-Javadoc)
	 * @see com.ibm.indo.service.ICareService#getActivePackage(java.lang.String)
	 */
	@Override
	public Map<String, Object> getActivePackage(String msisdn) {
		msisdn= IndoUtil.prefix62(msisdn);
		log.info("ICareServiceImpl.getActivePackage() -- START");
		Map<String,Object> map = new HashMap<String,Object>();
		String url= "http://10.128.81.78:7800/WLI_EAI_PROCESS/ServletEAISyncExt";
		try{
			String xml="<?xml version=\"1.0\"?><evw:WSSMessage xmlns:evw=\"com/icare/eai/schema/evWSSGetActMainPkg\"><evw:WSSGetActPkgReq><evw:ServiceNumber>"+msisdn+"</evw:ServiceNumber></evw:WSSGetActPkgReq></evw:WSSMessage>";
			Map<String, Object> data = xmlService.getRawXML(xml, url);
		//	log.info("ICareServiceImpl.getActivePackage() evWSSGetActMainPkg "+data);
			if(IndoUtil.isSuccess(data)){
				String response = (String) data.get("xml");
				Document xmlDoc = Jsoup.parse(response, "", Parser.xmlParser());
				Elements eles = xmlDoc.select("Package");
				List<Map<String, String>> d = IndoXMLParseUtil.getParentChildXML(eles);
				map.put("ListOfPackage", d);
				map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("ServiceNumber")));
				map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("ErrorMessage")));
				map.put("Status", "SUCCESS");
				return map;
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Saturn-9005", "Saturn-109", 0);
			log.error("ICareServiceImpl.getActivePackage() - ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("ICareServiceImpl.getActivePackage() -- END");
		}
		return map;
	}

	/* (non-Javadoc)
	 * @see com.ibm.indo.service.ICareService#getActiveProducts(java.lang.String)
	 */
	@Override
	public Map<String, Object> getActiveProducts(String msisdn) {
		msisdn= IndoUtil.prefix62(msisdn);
		log.info("ICareServiceImpl.getActiveProducts() -- START");
		Map<String,Object> map = new HashMap<String,Object>();
		String url= "http://10.128.81.78:7800/WLI_EAI_PROCESS/ServletEAISyncExt";
		try{
			String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?> <WSSMessage xmlns=\"com/icare/eai/schema/evWSSGetActPrd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"com/icare/eai/schema/evWSSGetActPrd evWSSGetActPrd.xsd\"> <WSSGetActPrdReq> <ServiceNumber>"+msisdn+"</ServiceNumber> </WSSGetActPrdReq> </WSSMessage>";
			Map<String, Object> data = xmlService.getRawXML(xml, url);
	//		log.info("ICareServiceImpl.getActiveProducts() evWSSGetActPrd "+data);
			if(IndoUtil.isSuccess(data)){
				String response = (String) data.get("xml");
				Document xmlDoc = Jsoup.parse(response, "", Parser.xmlParser());
				Elements eles = xmlDoc.select("Product");
				List<Map<String, String>> d = IndoXMLParseUtil.getParentChildXML(eles);
				map.put("ListOfProduct", d);
				map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("ServiceNumber")));
				map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("ErrorMessage")));
				map.put("Status", "SUCCESS");
				return map;
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Saturn-9006", "Saturn-109", 0);
			log.error("ICareServiceImpl.getActiveProducts() - ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("ICareServiceImpl.getActiveProducts() -- END");
		}
		return map;
	}

	/* (non-Javadoc)
	 * @see com.ibm.indo.service.ICareService#getPackageQuota(java.lang.String)
	 */
	@Override
	public Map<String, Object> getPackageQuota(String msisdn) {
		msisdn= IndoUtil.prefix62(msisdn);
		log.info("ICareServiceImpl.getPackageQuota() -- START");
		Map<String,Object> map = new HashMap<String,Object>();
		String url= "http://10.128.81.78:7800/WLI_EAI_PROCESS/ServletEAISyncExt";
		try{
			String xml="<?xml version=\"1.0\"?><evex:ExtMessage xmlns:evex=\"com/icare/eai/schema/evExtQMainPkgQuota\"><evex:ExtQMainPkgQuotaReq><evex:ServiceNumber>"+msisdn+"</evex:ServiceNumber><evex:Source>string</evex:Source></evex:ExtQMainPkgQuotaReq></evex:ExtMessage>";
			Map<String, Object> data = xmlService.getRawXML(xml, url);
		//	log.info("ICareServiceImpl.getPackageQuota() evExtQMainPkgQuota "+data);
			if(IndoUtil.isSuccess(data)){
				String response = (String) data.get("xml");
				Document xmlDoc = Jsoup.parse(response, "", Parser.xmlParser());
				map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("InitialQuota")));
				map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("UsedQuota")));
				map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("ServiceNumber")));
				map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("ErrorMessage")));
				map.put("Status", "SUCCESS");
				return map;
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Saturn-9007", "Saturn-109", 0);
			log.error("ICareServiceImpl.getPackageQuota() - ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("ICareServiceImpl.getPackageQuota() -- END");
		}
		return map;
	}

	/* (non-Javadoc)
	 * @see com.ibm.indo.service.ICareService#inquiryPrepaid(java.lang.String)
	 */
	@Override
	public Map<String, Object> inquiryPrepaid(String msisdn) {
		msisdn=IndoUtil.prefix62(msisdn);
		log.info("ICareServiceImpl.inquiryPrepaid() -- START");
		Map<String,Object> map = new HashMap<String,Object>();
		String url= "http://10.128.81.78:7800/WLI_EAI_PROCESS/ServletEAISyncExt";
		try{
			String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?> <ExtMessage xmlns=\"com/icare/eai/schema/evExtQPrepaidInfoAIR\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"com/icare/eai/schema/evExtQPrepaidInfoAIR evExtQPrepaidInfoAIR.xsd \"> <ExtQPrepaidInfoAIRReq> <ServiceNumber>"+msisdn+"</ServiceNumber><Source>Portal</Source></ExtQPrepaidInfoAIRReq></ExtMessage>";
			Cache<String, Object>  cache = CacheUtil.getInstance().getEntityCache();
			Map<String,Object> cacheMap = (Map<String, Object>) cache.get(msisdn+"_inquiryPrepaid");
		//	log.info("ICareServiceImpl.inquiryPrepaid() cacheMap "+cacheMap);
			if(null!=cacheMap){
				return cacheMap;
			}else{
				Map<String, Object> data = xmlService.getRawXML(xml, url);
		//		log.info("ICareServiceImpl.inquiryPrepaid() evExtQPrepaidInfoAIR "+data);
				if(IndoUtil.isSuccess(data)){
					String response = (String) data.get("xml");
					Document xmlDoc = Jsoup.parse(response, "", Parser.xmlParser());
					Elements eles = xmlDoc.select("PrepaidInfo");
					List<Map<String, String>> d = IndoXMLParseUtil.getParentChildXML(eles);
					if(null!= d && d.size()>0){
						if(null!=d.get(0).get("CardActiveUntil") && !StringUtils.isEmpty(d.get(0).get("CardActiveUntil").toString())){
							d.get(0).put("CardActiveUntil", IndoUtil.parseDate(d.get(0).get("CardActiveUntil").toString(), "dd/MM/yyyy", "dd MMM yyyy"));
						}
						if(null!=d.get(0).get("GracePeriodUntil") && !StringUtils.isEmpty(d.get(0).get("GracePeriodUntil").toString())){
							d.get(0).put("GracePeriodUntil", IndoUtil.parseDate(d.get(0).get("GracePeriodUntil").toString(), "dd/MM/yyyy", "dd MMM yyyy"));
						}
						map.put("PrepaidInfo", d.get(0));
						map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("ServiceNumber")));
						map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("ErrorMessage")));
						map.put("Status", "SUCCESS");
						cache.put(msisdn+"_inquiryPrepaid",map, 30, TimeUnit.SECONDS);
					}else{
						IndoUtil.populateErrorMap(map, "IC-702", "Saturn-109",0);
					}
					return map;
				}
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Saturn-9008", "Saturn-109", 0);
			log.error("ICareServiceImpl.inquiryPrepaid() - ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("ICareServiceImpl.inquiryPrepaid() -- END");
		}
		return map;
	}

	/* (non-Javadoc)
	 * @see com.ibm.indo.service.ICareService#billingInfo(java.lang.String)
	 */
	@Override
	public Map<String, Object> billingInfo(String accountNo) {
		log.info("ICareServiceImpl.billingInfo() -- START");
		Map<String,Object> map = new HashMap<String,Object>();
		String url= "http://10.128.81.78:7800/WLI_EAI_PROCESS/ServletEAISyncExt";
		try{
			Cache<String, Object>  cache = CacheUtil.getInstance().getEntityCache();
			Map<String,Object> cacheMap = (Map<String, Object>) cache.get(accountNo+"_billingInfo");
			//log.info("ICareServiceImpl.billingInfo() cacheMap "+cacheMap);
			if(null!=cacheMap){
				return cacheMap;
			}else{
				String xml="<?xml version=\"1.0\"?><evex:ExtMessage xmlns:evex=\"com/icare/eai/schema/evExtQBillingInfo\"><evex:ExtQBillingInfoReq><evex:BillingAccountNo>"+accountNo+"</evex:BillingAccountNo><evex:Source>Portal</evex:Source><evex:InvoiceQty>5</evex:InvoiceQty></evex:ExtQBillingInfoReq></evex:ExtMessage>";
				Map<String, Object> data = xmlService.getRawXML(xml, url);
				if(IndoUtil.isSuccess(data)){
					String response = (String) data.get("xml");
					Document xmlDoc = Jsoup.parse(response, "", Parser.xmlParser());
					Elements eles = xmlDoc.select("BillingInfo");
					List<Map<String, String>> d = IndoXMLParseUtil.getParentChildXML(eles);
					map.put("ListOfBillingInfo", d);
					map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("ServiceNumber")));
					map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("ErrorMessage")));
					map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("UnbilledUsage")));
					map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("LastRatedEventDtm")));
					map.put("Status", "SUCCESS");
					cache.put(accountNo+"_billingInfo",map, 30, TimeUnit.SECONDS);
					return map;
				}
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Saturn-9009", "Saturn-109", 0);
			log.error("ICareServiceImpl.billingInfo() - ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("ICareServiceImpl.billingInfo() -- END");
		}
		return map;
	}

	/* (non-Javadoc)
	 * @see com.ibm.indo.service.ICareService#getSuplementaryPackage(java.lang.String)
	 */
	@Override
	public Map<String, Object> getSuplementaryPackage(String msisdn) {
		msisdn= IndoUtil.prefix62(msisdn);
		log.info("ICareServiceImpl.getSuplementaryPackage() -- START");
		Map<String,Object> map = new HashMap<String,Object>();
		String url= "http://10.128.81.78:7800/WLI_EAI_PROCESS/ServletEAISyncExt";
		try{
			String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?><WSSMessage xmlns=\"com/icare/eai/schema/evWSSGetActSuppPkg\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"com/icare/eai/schema/evWSSGetActSuppPkg evWSSGetActSuppPkg.xsd\"><WSSGetActPkgReq><ServiceNumber>"+msisdn+"</ServiceNumber></WSSGetActPkgReq></WSSMessage>";
			Map<String, Object> data = xmlService.getRawXML(xml, url);
		//	log.info("ICareServiceImpl.getSuplementaryPackage() evWSSGetActSuppPkg "+data);
			if(IndoUtil.isSuccess(data)){
				String response = (String) data.get("xml");
				Document xmlDoc = Jsoup.parse(response, "", Parser.xmlParser());
				Elements eles = xmlDoc.select("Package");
				List<Map<String, String>> d = IndoXMLParseUtil.getParentChildXML(eles);
				map.put("ListOfPackage", d);
				map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("ServiceNumber")));
				map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("ErrorMessage")));
				map.put("Status", "SUCCESS");
				return map;
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Saturn-9010", "Saturn-109", 0);
			log.error("ICareServiceImpl.getSuplementaryPackage() - ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("ICareServiceImpl.getSuplementaryPackage() -- END");
		}
		return map;
	}
	@Override
	public Map<String, Object> dedicatedAccountCs3(String msisdn) {
		msisdn= IndoUtil.prefix62(msisdn);
		log.info("ICareServiceImpl.dedicatedAccountCs3() -- START");
		Map<String,Object> map = new HashMap<String,Object>();
		String url= "http://10.128.81.78:7800/WLI_EAI_PROCESS/ServletEAISyncExt";
		try{
			String xml="<?xml version=\"1.0\"?> <evex:ExtMessage xmlns:evex=\"com/icare/eai/schema/evExtQDAAIRCS3\">   <evex:ExtQDAAIRCS3Req>     <evex:ServiceNumber>"+msisdn+"</evex:ServiceNumber>     <evex:Source>Portal</evex:Source>   </evex:ExtQDAAIRCS3Req> </evex:ExtMessage>";
			Map<String, Object> data = xmlService.getRawXML(xml, url);
			//log.info("ICareServiceImpl.dedicatedAccountCs3() evExtQDAAIRCS3 "+data);
			if(IndoUtil.isSuccess(data)){
				String response = (String) data.get("xml");
				Document xmlDoc = Jsoup.parse(response, "", Parser.xmlParser());
				Elements eles = xmlDoc.select("DedicatedAccount");
				List<Map<String, String>> d = IndoXMLParseUtil.getParentChildXML(eles);
				map.put("ListOfDedicatedAccount", d);
				map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("ServiceNumber")));
				map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("ErrorMessage")));
				map.put("Status", "SUCCESS");
				return map;
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Saturn-9013", "Saturn-109", 0);
			log.error("ICareServiceImpl.dedicatedAccountCs3() - ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("ICareServiceImpl.dedicatedAccountCs3() -- END");
		}
		return map;
	}
	@Override
	public Map<String, Object> dedicatedAccountCs5(String msisdn) {
		msisdn= IndoUtil.prefix62(msisdn);
		log.info("ICareServiceImpl.dedicatedAccountCs5() -- START");
		Map<String,Object> map = new HashMap<String,Object>();
		String url= "http://10.128.81.78:7800/WLI_EAI_PROCESS/ServletEAISyncExt";
		try{
			String xml="<?xml version=\"1.0\"?> <evex:ExtMessage xmlns:evex=\"com/icare/eai/schema/evExtQDAAIRCS5\">   <evex:ExtQDAAIRCS5Req>     <evex:ServiceNumber>"+msisdn+"</evex:ServiceNumber>     <evex:Source>Portal</evex:Source>   </evex:ExtQDAAIRCS5Req> </evex:ExtMessage>";
			Map<String, Object> data = xmlService.getRawXML(xml, url);
		//	log.info("ICareServiceImpl.dedicatedAccountCs5() evExtQDAAIRCS5 "+data);
			if(IndoUtil.isSuccess(data)){
				String response = (String) data.get("xml");
				Document xmlDoc = Jsoup.parse(response, "", Parser.xmlParser());
				Elements eles = xmlDoc.select("DedicatedAccount");
				List<Map<String, String>> d = IndoXMLParseUtil.getParentChildXML(eles);
				map.put("ListOfDedicatedAccount", d);
				map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("ServiceNumber")));
				map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("ErrorMessage")));
				map.put("Status", "SUCCESS");
				return map;
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Saturn-9014", "Saturn-109", 0);
			log.error("ICareServiceImpl.dedicatedAccountCs5() - ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("ICareServiceImpl.dedicatedAccountCs5() -- END");
		}
		return map;
	}
	@Override
	public Map<String, Object> topUp(String msisdn, String code) {
		msisdn= IndoUtil.prefix62(msisdn);
		log.info("ICareServiceImpl.topUp() -- START");
		Map<String,Object> map = new HashMap<String,Object>();
		String url= "http://10.128.81.78:7800/WLI_EAI_PROCESS/ServletEAISyncExt";
		try{
			String xml="<?xml version=\"1.0\"?> <evex:ExtMessage xmlns:evex=\"com/icare/eai/schema/evExtTopupAIRCS5\">   <evex:ExtTopupAIRCS5Req>     <evex:ServiceNumber>"+msisdn+"</evex:ServiceNumber>     <evex:Source>Portal</evex:Source>     <evex:VoucherHRN>"+code+"</evex:VoucherHRN>   </evex:ExtTopupAIRCS5Req> </evex:ExtMessage>";
			Map<String, Object> data = xmlService.getRawXML(xml, url);
		//	log.info("ICareServiceImpl.topUp() evExtTopupAIRCS5 "+data);
			if(IndoUtil.isSuccess(data)){
				String response = (String) data.get("xml");
				Document xmlDoc = Jsoup.parse(response, "", Parser.xmlParser());
				map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("Status")));
				map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("ErrorMessage")));
				if(null!= map.get("ErrorMessage")){
					String e = map.get("ErrorMessage").toString();
					if(e.equals("1") || e.equals("0") || e.equals("2")){
						map.put("Status", "SUCCESS");
						Cache<String, Object>  cache = CacheUtil.getInstance().getEntityCache();
						cache.remove(msisdn+"_inquiryPrepaid");
						Map<String, Object> balMap = inquiryPrepaid(msisdn);
						if(IndoUtil.isSuccess(balMap)){
							Map<String, Object> validMap = (Map<String, Object>) balMap.get("PrepaidInfo");
							map.put("validity", validMap.get("CardActiveUntil"));
							map.put("balance", validMap.get("Balance"));
						}else{
							map.put("validity", "");
							map.put("balance","");
						}
					}else if(Integer.parseInt(e)>=120){
						IndoUtil.populateErrorMap(map, "Saturn-9015", "Saturn-1028-120",0);
					}else{
						IndoUtil.populateErrorMap(map, "Saturn-9015", "Saturn-1028-"+e,0);
					}
				}
				return map;
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Saturn-9015", "Saturn-109", 0);
			log.error("ICareServiceImpl.topUp() - ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("ICareServiceImpl.topUp() -- END");
		}
		return map;
	}
	/* (non-Javadoc)
	 * @see com.ibm.indo.service.ICareService#getSSPDetails(java.lang.String)
	 */
	@Override
	public Map<String, Object> getSSPDetails(String msisdn) {
		msisdn= IndoUtil.prefix62(msisdn);
		log.info("ICareServiceImpl.getSSPDetails() -- START");
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			String url = "http://10.147.246.140:8002/PULLHandler/GetSubsInfo?eid=WSS&info=DATA&key="+msisdn;
			Document xmlDoc = Jsoup.connect(url).timeout(10000).get();
			Elements eles = xmlDoc.select("Service");
			List<Map<String, String>> d = IndoXMLParseUtil.getParentChildXML(eles);
			map.put("Services", d);
			map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("ServiceNumber")));
			map.putAll(IndoXMLParseUtil.getIdenticalChilds(xmlDoc.select("ErrorMessage")));
			map.put("Status", "SUCCESS");
		//	log.info("ICareServiceImpl.getSSPDetails() Document "+xmlDoc.toString());
			return map;
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Saturn-9011", "Saturn-109", 0);
			log.error("ICareServiceImpl.getSSPDetails() - ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("ICareServiceImpl.getSSPDetails() -- END");
		}
		return map;
	}
	
	@Override
	public Map<String, Object> customerProfileCorporate(String Msisdn) {
		Msisdn=IndoUtil.prefix62(Msisdn);
		log.info("ICareServiceImpl.customerProfileCorporate() START fetching profile from catalist for "+Msisdn);
		Map<String,Object> map = new HashMap<String,Object>();
		MobileAgentServiceServiceLocator locator= new MobileAgentServiceServiceLocator();
			try {
				log.debug("URL is "+ prop.getProperty("CATALIST_URL"));
				MobileAgentService service = locator.getMobileAgentServiceSoapPort(new URL(prop.getProperty("CATALIST_URL")));
				MobileAgentServiceServiceSoapBindingStub stub = (MobileAgentServiceServiceSoapBindingStub) service;
				log.info("ICareServiceImpl.customerProfileCorporate() getTimeout "+stub.getTimeout());
				stub.setTimeout(5000);
				CorporateProfileInputType inputType= new CorporateProfileInputType();
				inputType.setMsisdn(Msisdn);
				CorporateProfileOutputType corpProfile= stub.getCorporateProfile(inputType);
				if(null!=corpProfile && null!=corpProfile.getCustomerType()){
					Map<String,Object> vProf = new HashMap<String,Object>();
					if(null!=corpProfile.getActiveDate()){
						vProf.put("ActiveDate", IndoUtil.parseDate(corpProfile.getActiveDate().getTime(), "dd.MM.yyyy"));
					}
					vProf.put("AssetStatus", corpProfile.getAssetStatus());
					vProf.put("CustStatus", corpProfile.getAssetStatus());
					vProf.put("BillingName", corpProfile.getCustomerName());
					vProf.put("CustName", corpProfile.getCustomerName());
					vProf.put("CustType", "Corporate");
					vProf.put("CustSegment", "Corporate");
					vProf.put("Iccid", corpProfile.getIccid());
					vProf.put("CustIDType", corpProfile.getIdType());
					vProf.put("Msisdn", corpProfile.getMsisdn());
				//	vProf.put("OfferName", corpProfile.getOfferName());
					vProf.put("CustIDNumber", corpProfile.getPicId());
					vProf.put("PicName", corpProfile.getPicName());
					vProf.put("ServiceType", corpProfile.getServiceType());
					vProf.put("Status", "SUCCESS");
					map.put("UserProfile", vProf);
				}else{
					map.put("UserProfile", null);
				}
				log.info("ICareServiceImpl.customerProfileCorporate() - corpProfile "+corpProfile);
				return map;
			} catch (MalformedURLException e) {
				IndoUtil.populateErrorMap(map, "Saturn-9012","Saturn-109",0);
				log.error("Saturn-9012- ICareServiceImpl.customerProfileCorporate() e2"+e);
			} catch (ServiceException e) {
				IndoUtil.populateErrorMap(map, "Saturn-9012","Saturn-109",0);
				log.error("Saturn-9012- ICareServiceImpl.customerProfileCorporate() e3"+e);
			} catch (RemoteException e) {
				IndoUtil.populateErrorMap(map, "Saturn-9012","Saturn-109",0);
				log.error("Saturn-9012- ICareServiceImpl.customerProfileCorporate() e4"+e);
			}catch (Exception e) {
				IndoUtil.populateErrorMap(map, "Saturn-9012","Saturn-109",0);
				log.error("Saturn-9012- ICareServiceImpl.customerProfileCorporate() e4"+e);
			}finally{
				log.info("ICareServiceImpl.customerProfileCorporate() - END");
			}
		return map;
	}
	@Override
	public Map<String, Object> corporateMyCareProfile(String Msisdn) {
		log.info("ICareServiceImpl.corporateMyCareProfile() - START");
		try{
			Map<String,Object> myCareMap = new HashMap<String,Object>();
			String jsonInput = myCareService(Msisdn,"","","get_user_information_by_msisdn");
			JsonObject jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			String status = jObj.get("status").getAsString();
			String userid ="";
			String username ="";
			if(status.equals("1")){
				if(jObj.has("data")){
					JsonObject data = jObj.get("data").getAsJsonObject();
					userid = data.get("user").getAsString();
					username = data.get("username").getAsString();
				}else{
					log.info("ICareServiceImpl.corporateMyCareProfile() data not found");
				}
			}else{
				log.info("ICareServiceImpl.corporateMyCareProfile() status "+status);
			}
			jsonInput = myCareService(Msisdn,userid,username,"getDashboardInfoPageSSP");
			jObj = (new JsonParser()).parse(jsonInput).getAsJsonObject();
			status = jObj.get("status").getAsString();
			if(status.equals("1")){
				if(jObj.has("data")){
					JsonObject data = jObj.get("data").getAsJsonObject();
					if(data.has("profileDashboard")){
						String val = data.get("profileDashboard").getAsJsonObject().get("Pemakaiansdtanggal").getAsString();
						myCareMap.put("UnbilledUsage", val);
						myCareMap.put("Status", "SUCCESS");
						log.info("ICareServiceImpl.corporateMyCareProfile() val "+val);
					}
				}else{
					log.info("ICareServiceImpl.corporateMyCareProfile() data not found");
				}
			}else{
				log.info("ICareServiceImpl.corporateMyCareProfile() status "+status);
			}
			return myCareMap;
		}catch(Exception ce){
			log.error("ICareServiceImpl.corporateMyCareProfile() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("ICareServiceImpl.corporateMyCareProfile() - END");
		}
		return null;
	}
	
	public static String myCareService(String msisdn,String user_id,String user_name, String url){
		log.info("ICareServiceImpl.myCareService() - START");
	  	HttpEntity entity = null;
		CloseableHttpClient  client = null;
		HttpPost request = null;
	      try{
	    	  String time= System.currentTimeMillis() + "";
	  		  String ds = IndoUtil.parseDate(new Date(), "YYYYmmddHHmmss");
	    	  String urlParameters = "";
	    	  	urlParameters += "msisdn=" + URLEncoder.encode(msisdn, "UTF-8");
				urlParameters += "&username=" + URLEncoder.encode(user_name, "UTF-8");
				urlParameters += "&user_id=" + URLEncoder.encode(user_id, "UTF-8");
		//		urlParameters += "&password=" + URLEncoder.encode("multijav", "UTF-8");
				urlParameters += "&token=" + URLEncoder.encode("461fd77b-1f04-4cf9-a045-49fb07435913\'", "UTF-8");
				urlParameters += "&bhs=" + URLEncoder.encode("en", "UTF-8");
			    urlParameters += "&time=" + URLEncoder.encode(ds+ time.substring(time.length()-3), "UTF-8");
	    	 log.info("LauncherController.test() urlParameters "+urlParameters);
	    	  
	    	  client = new HttpConnServiceImpl().getHttpClient();
		      request = new HttpPost("https://mycare.indosatooredoo.com/api/v4/"+url);
		      request.setHeader("Content-Type", "application/x-www-form-urlencoded");
		      //request.setHeader("Content-Length",""+Integer.toString(urlParameters.getBytes().length));
		      request.setHeader("Content-Language","en-US");
		      request.setHeader("X-MEN",md5("8471"+urlParameters));//8471, 5718, 31427741
		      request.setHeader("Authorization","Basic YW5kcm86cGVwMl1mb3J0aWZ5");
		      
		      StringEntity se = new StringEntity(urlParameters);
		      se.setContentType("application/x-www-form-urlencoded");
		      request.setEntity(se);
		      HttpResponse response = client.execute(request);
		      int statusCode = response.getStatusLine().getStatusCode();
		      log.info("ReportsController.test() statusCode "+statusCode);
	          entity = response.getEntity();
	          String content = EntityUtils.toString(entity);
	          log.info("ReportsController.test() content "+content);
			log.info("-----------------END------------------------");
			 return content;
		}catch(Exception ce){
			log.info("SaturnController.test() ce "+IndoUtil.getFullLog(ce));
		}finally{
			 try {
					EntityUtils.consume(entity);
					if(null!=request){
					request.releaseConnection();}if(null!=client){
					client.close();}
					} catch (IOException e) {
					e.printStackTrace();
				}
		}
		log.info("-----------------END------------------------");
		return url;
	}
	public final static String md5(String s) {
	    final String MD5 = "MD5";
	    try {
	        // Create MD5 Hash
	        MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();

	        // Create Hex String
	        StringBuilder hexString = new StringBuilder();
	        for (byte aMessageDigest : messageDigest) {
	            String h = Integer.toHexString(0xFF & aMessageDigest);
	            while (h.length() < 2)
	                h = "0" + h;
	            hexString.append(h);
	        }
	        return hexString.toString();

	    } catch (NoSuchAlgorithmException e) {
	        log.error("SaturnController e"+IndoUtil.getFullLog(e));
	    }
	    return "";
	}

	@Override
	public Map<String, Object> serviceRequest(String xml, String type) {
		log.info("ICareServiceImpl.serviceRequest() - START input XML "+xml);
		Map<String,Object> map = new HashMap<String,Object>();
		String url= "http://10.128.81.78:7800/WLI_EAI_PROCESS/ServletEAISyncWSS";
		try{
			Map<String, Object> data = xmlService.getRawXML(xml, url);
			log.info("ICareServiceImpl.serviceRequest() "+data);
			if(IndoUtil.isSuccess(data)){
				String response = (String) data.get("xml");
				Document xmlDoc = Jsoup.parse(response, "", Parser.xmlParser());
				log.info("ICareServiceImpl.serviceRequest() xmlDoc"+xmlDoc);
				map.put("Status", "SUCCESS");
				return map;
			}
		}catch(Exception ce){
			IndoUtil.populateErrorMap(map, "Saturn-9010", "Saturn-109", 0);
			log.error("ICareServiceImpl.serviceRequest() - ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("ICareServiceImpl.serviceRequest() -- END");
		}
		return map;
	}
}
