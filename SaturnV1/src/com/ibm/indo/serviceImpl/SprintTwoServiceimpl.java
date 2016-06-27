package com.ibm.indo.serviceImpl;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.ibm.indo.service.GenericService;
import com.ibm.indo.service.HttpConnService;
import com.ibm.indo.service.LDAPService;
import com.ibm.indo.service.SprintTwoService;
import com.ibm.indo.service.WSSDBService;
import com.ibm.indo.service.XMLService;
import com.ibm.indo.util.CacheUtil;
import com.ibm.indo.util.DBUtil;
import com.ibm.indo.util.EmailService;
import com.ibm.indo.util.IndoServiceProperties;
import com.ibm.indo.util.IndoUtil;
import com.ibm.indo.util.TripleDES;
import com.ibm.services.vo.InvoiceListVO;
import com.ibm.services.vo.InvoiceVO;
import com.indosat.eai.catalist.mobileagent.Invoice;
import com.indosat.eai.catalist.mobileagent.InvoiceInputType;
import com.indosat.eai.catalist.mobileagent.InvoiceOutputType;
import com.isat.catalist.eai.mobileagent.ws.MobileAgentService;
import com.isat.catalist.eai.mobileagent.ws.MobileAgentServiceServiceLocator;
import org.infinispan.Cache;

@Service
public class SprintTwoServiceimpl implements SprintTwoService{
	private static Logger log = Logger.getLogger("saturnLoggerV1");
	IndoServiceProperties confProp=IndoServiceProperties.getInstance();
    Properties prop = confProp.getConfigSingletonObject();
    @Autowired
	private DBUtil dbUtil;
	@Autowired
	WSSDBService wssDBService;
	@Autowired
	LDAPService ldapService;
	@Autowired
	HttpConnService httpConn;
	@Autowired
	private XMLService xmlService;
	@Autowired
	private GenericService genService;
	/*@Override
	public InvoiceListVO retrieveInvoice(String baNumber) {
		InvoiceListVO invoiceListVO = new InvoiceListVO();
		String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?> <MobaMessage xmlns=\"com/icare/eai/schema/evMobaQRetailInvoice\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"com/icare/eai/schema/evMobaQRetailInvoice evMobaQRetailInvoice.xsd\"> <MobaQRetailInvoiceReq> <BillingAccountNo>"+baNumber +"</BillingAccountNo> </MobaQRetailInvoiceReq> </MobaMessage>";
		//String url= "http://10.6.3.28:15001/WLI_EAI_PROCESS/ServletEAISyncExt";
		String url=prop.getProperty("ICARE_PROFILE_URL");
		log.info("URL is "+url);
		HttpEntity entity = null;
		CloseableHttpClient httpClient = null;
		 HttpPost httppost = null;
		try {
			RequestConfig defaultRequestConfig = RequestConfig.custom()
				    .setSocketTimeout(5000)
				    .setConnectTimeout(5000)
				    .setConnectionRequestTimeout(5000)
				    .build();
			httpClient = HttpClients.custom()
				 .setDefaultRequestConfig(defaultRequestConfig).build();
		    httppost = new HttpPost(url);
		        StringEntity se = new StringEntity(xml, "UTF-8");
		        se.setContentType("text/xml");
		        httppost.setEntity(se);
		        HttpResponse httpresponse = httpClient.execute(httppost);
		        int statusCode = httpresponse.getStatusLine().getStatusCode();
		        if (statusCode == 200 ){
		            entity = httpresponse.getEntity();
		            String content = EntityUtils.toString(entity);
		         //   log.info("GenericServiceImpl.getAttributes() retrieveLastReloads content - "+content);
		            Document xmlDoc = Jsoup.parse(content, "", Parser.xmlParser());
		            List<InvoiceVO> invoiceVOList = new ArrayList<InvoiceVO>();
		            if(xmlDoc.select("BillingAccountNo").first().text()!=null){
		            	invoiceListVO.setBillingAccountNumber(xmlDoc.select("BillingAccountNo").first().text());
		            	 log.info(xmlDoc.select("BillingAccountNo").first().text());
		            }
		            Elements es =  xmlDoc.select("BillingInvoice");
		            int maxLoop=0;
		            if(es!=null && es.size()>=3){
		            	maxLoop=3;
		            }else{
		            	maxLoop=es.size();
		            }
		            for(int i=0;i<=maxLoop-1;i++){
		            	InvoiceVO vo = new InvoiceVO();
						Elements dat = es.get(i).select("Date");
						Elements value = es.get(i).select("Value");
						if(null!=value && !value.isEmpty()){
							vo.setAmount(es.get(i).select("Value").first().text());
							if(null!=dat && es.get(i).select("Date").first().text()!=null){
								try{	
									DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
							        Date dte = inputFormat.parse(es.get(i).select("Date").first().text());
							        DateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");
							        vo.setInvoiceDate(outputFormat.format(dte));
								}catch(Exception e){
									log.info("Indo-2025- Exception Occured"+e);			
								}
							}
						}
						invoiceVOList.add(vo);
					}
		           // if(invoiceVOList.size()>0){
		            	log.info("Invoice List Found. Size is "+ invoiceVOList.size());
		            	invoiceListVO.setInvoicesList(invoiceVOList);
		            	invoiceListVO.setBillingAccountNumber(baNumber);
			            invoiceListVO.setStatus("SUCCESS");
		            }else{
		            	log.info("Invoice List is empty.");
		            	invoiceListVO.setBillingAccountNumber(baNumber);
		            	invoiceListVO.setErrorCode("Indo-1014");
			            invoiceListVO.setErrorDescription("No Data Found.");
			            invoiceListVO.setStatus("FAILURE");
		            }
		            return invoiceListVO;
		        }else{
		        	entity = httpresponse.getEntity();
		            String content = EntityUtils.toString(entity);
		            log.info("GenericServiceImpl.getAttributes() errordata-"+content);
		            invoiceListVO.setErrorCode("Indo-1014");
		            invoiceListVO.setErrorDescription(content);
		            invoiceListVO.setStatus("FAILURE");
		        }
		}catch (MalformedURLException e) {
			log.info("Indo-2026- GenericServiceImpl.getAttributes() e "+e);
			invoiceListVO.setErrorCode("Indo-1014");
			invoiceListVO.setErrorDescription(e.getClass().getSimpleName());
			invoiceListVO.setStatus("FAILURE");
		}catch (IOException e) {
			log.info("Indo-2027- GenericServiceImpl.getAttributes() e1 "+e);
			invoiceListVO.setErrorCode("Indo-1014");
			invoiceListVO.setErrorDescription(e.getClass().getSimpleName());
			invoiceListVO.setStatus("FAILURE");
		}catch (Exception e) {
			log.info("Indo-2028- GenericServiceImpl.getAttributes() e1 "+e);
			invoiceListVO.setErrorCode("Indo-1014");
			invoiceListVO.setErrorDescription(e.getClass().getSimpleName());
			invoiceListVO.setStatus("FAILURE");
		}finally{
			try {
				EntityUtils.consume(entity);
				httppost.releaseConnection();
				httpClient.close();
				} catch (IOException e) {
				log.info("Indo-2028- GenericServiceImpl.getAttributes() Closing connection "+e);
			}
		}
	
		return invoiceListVO;
	}*/
	
	@Override
	public InvoiceListVO getCorpInvoices(String msisdn) {	
		msisdn = IndoUtil.prefix62(msisdn);
	InvoiceListVO invoiceListVO = new InvoiceListVO();
	MobileAgentServiceServiceLocator locator= new MobileAgentServiceServiceLocator();
	try {
		MobileAgentService service = locator.getMobileAgentServiceSoapPort(new URL(prop.getProperty("CATALIST_URL")));
		InvoiceInputType input = new InvoiceInputType();
		input.setMsisdn(msisdn);
		InvoiceOutputType resp = service.getInvoices(input);
		if(null!=resp){
			Invoice[] invoices = resp.getInvoices();
			List<InvoiceVO> list = new ArrayList<InvoiceVO>();
			if(invoices!=null){
				//for(Invoice in : invoices){
				for(int i=0,j=0;i<invoices.length-1 && j<=2;i++,j++){
					InvoiceVO invoice = new InvoiceVO();
					//1990-03-28 00:00:00.0 to dd.MM.yyyy 2015-11-01 04:18:09.0
					if(invoices[i].getInvoiceDate()!=null){
						Date dte=IndoUtil.parseDate(invoices[i].getInvoiceDate(), "yyyy-MM-dd");
						log.info("Invoice Date date "+dte);
						log.info("set  invoice date is "+IndoUtil.parseDate(dte, "dd.MM.yyyy"));
						invoice.setInvoiceDate(IndoUtil.parseDate(dte, "dd.MM.yyyy"));
					}
					invoice.setAmount(String.valueOf(invoices[i].getInvoiceValue()));
					invoice.setDebtAge(invoices[i].getDebtAge());
					invoice.setDueDate(invoices[i].getDueDate());
					invoice.setInvoiceNetValue(invoices[i].getInvoiceNetValue());
					invoice.setInvoiceNumber(invoices[i].getInvoiceNumber());
					invoice.setInvoiceTaxValue(invoices[i].getInvoiceTaxValue());
					invoice.setBillDate(IndoUtil.parseDate(invoices[i].getBillDate(),"yyyy-MM-dd","dd"));
					list.add(invoice);
				}
				invoiceListVO.setInvoicesList(list);
				invoiceListVO.setStatus("SUCCESS");
				invoiceListVO.setMsisdn(msisdn);
			}else{
				invoiceListVO.setInvoicesList(list);
				invoiceListVO.setStatus("SUCCESS");
				invoiceListVO.setMsisdn(msisdn);
			}
		}else{
			invoiceListVO.setStatus("FAILURE");
			invoiceListVO.setErrorCode("Indo-1020");
			invoiceListVO.setErrorDescription("No Data Found.");
		}
	}catch(Exception ce){
		invoiceListVO.setStatus("FAILURE");
		invoiceListVO.setErrorCode("Indo-1019");
		invoiceListVO.setErrorDescription("No Data Found.");
		log.info("Indo-2044- Exception Occured "+ce.getLocalizedMessage());
	}
	return invoiceListVO;
	}
	
	@Override
	public Map<String,Object> retrieveStore(){
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			List<Map<String, Object>> menu = dbUtil.getData("select * from SATURN_CALL_CENTER order by seq asc", new Object[]{});
			map.put("Status", "SUCCESS");
			map.put("Menu", menu);
		}catch(Exception e){
			
		}
		return map;
	}
	
	@Override
	public Map<String,Object> contactUsMenu(JsonObject jObj){
		Map<String,Object> map = new HashMap<String,Object>();
		Cache<String, Object>  cache = CacheUtil.getInstance().getEntityCache();
		try{
			String action = jObj.get("action").getAsString();
			String lang="";
			if(null!=jObj.get("lang")){
				lang=jObj.get("lang").getAsString();
			}
			if(null==action || StringUtils.isEmpty(action)){
				Map<String,Object> cacheMap = (Map<String, Object>) cache.get("CallCenter_"+lang);
				if(null!=cacheMap){
					log.info("GenericServiceImpl.CallCenter() - returned from cache");
					return cacheMap;
				}else{
					String sql = "";
					if(lang.equalsIgnoreCase("EN")){
						sql="select * from SATURN_CALL_CENTER where LANG ='EN' order by seq asc";
					}else{
						sql="select * from SATURN_CALL_CENTER where LANG ='ID' order by seq asc";
					}
					List<Map<String, Object>> menu = dbUtil.getData(sql, new Object[]{});
					map.put("Status", "SUCCESS");
					map.put("Menu", menu);
					if(null!=menu && menu.size()!=0){
						cache.put("CallCenter_"+lang,map, 2, TimeUnit.HOURS);
					}
				}
			}else if(null!=action && action.equals("ContactUsStore")){
				Map<String,Object> cacheMap = (Map<String, Object>) cache.get("Store");
				if(null!=cacheMap){
					log.info("GenericServiceImpl.Store() - returned from cache");
					return cacheMap;
				}else{
					String sql = "";
					sql="select * from SATURN_STORE_DATA";
					List<Map<String, Object>> menu = dbUtil.getData(sql, new Object[]{});
					map.put("Status", "SUCCESS");
					map.put("StoreList", menu);
					if(null!=menu && menu.size()!=0){
						cache.put("Store"+lang,map, 2, TimeUnit.HOURS);
					}
				}
				
			}else if(null!=action && action.equals("Contactus")){
				String msisdn="";
				String otherMsisdn="";
				String about = "";
				String title="";
				String message="";
				if(null!=jObj.get("msisdn")){
					msisdn = jObj.get("msisdn").getAsString();
				}if(null!=jObj.get("otherMsisdn")){
					otherMsisdn = jObj.get("otherMsisdn").getAsString();
				}if(null!=jObj.get("title")){
					title = jObj.get("title").getAsString();
				}if(null!=jObj.get("message")){
					message = jObj.get("message").getAsString();
				}if(null!=jObj.get("about")){
					about = jObj.get("about").getAsString();
				}
				String  msg = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>";
				        msg += "<html xmlns='http://www.w3.org/1999/xhtml'>";
				        msg += "<head>";
				        msg += "<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />";
				        msg += "<title>MyCare feedback</title>";
				        msg += "</head>";
				        msg += "<body>";
				        msg += "<table>";
				        msg += "<tr><td>msisdn</td><td>:</td><td>"+msisdn+"</td></tr>";
				        msg += "<tr><td>otherMsisdn</td><td>:</td><td>"+otherMsisdn+"</td></tr>";
				        msg += "<tr><td>about</td><td>:</td><td>"+about+"</td></tr>";
				        msg += "<tr><td>title</td><td>:</td><td>"+title+"</td></tr>";
				        msg += "<tr><td>message</td><td>:</td><td>"+message+"</td></tr>";
				        msg += "</table>";
				        msg += "</body>";
				        msg += "</html>";
				        EmailService.sendEmailWithHtmlAttachment("cs@indosatooredoo.com", "Mycare Feedback", msg, "noreply@indosatooredoo.com", "", "");
				int ct = dbUtil.saveData("insert into saturn_feedback(msisdn,other_msisdn, about, title, message) values(?,?,?,?,?)", new Object[]{msisdn,otherMsisdn,about,title,message});
				if(ct>0){
					map.put("Status", "SUCCESS");
				}else{
					IndoUtil.populateErrorMap(map, "Saturn-900", "Saturn-101",0);
				}
			}else{
				List<Map<String, Object>> data = dbUtil.getData("select * from saturn_contactus where contact = ?", new Object[]{action});
				map.put("Status", "SUCCESS");
				map.put("Menu", data);
			}
		}catch(Exception ce){
			log.error("SprintTwoServiceimpl.contactUsMenu() "+IndoUtil.getFullLog(ce));
		}
		return map;
	}
	
	@Override
	public Map<String, Object> addFavourite(String msisdn,
			String Transaction_type, String tx1, String txt2, String txt3,
			String txt4, String txt5, String display, String CustType) {
		Map<String, Object> map= new HashMap<String, Object>();
		try{
			List<Map<String, Object>> list = dbUtil.getData("SELECT * from SATURN_FAVORITE_DATA where msisdn=? and Transaction_type=?", new Object[]{msisdn,Transaction_type});
			if(null!=list && list.size()>0){
				map.put("Status","SUCCESS");
				return map;
			}
			int ct = dbUtil.saveData("INSERT INTO SATURN_FAVORITE_DATA(MSISDN,Transaction_type,TEXT1,TEXT2,TEXT3,TEXT4,TEXT5,insert_Date,display_name,CustTYpe) values(?,?,?,?,?,?,?,sysdate,?,?)",
					 new Object[]{msisdn,Transaction_type,tx1,txt2,txt3,txt4,txt5,display,CustType});
			if(ct>0){
					map.put("Status","SUCCESS");
				} 
			}catch(Exception ce){
				IndoUtil.populateErrorMap(map, "Migrate-001",  "Failed to add favourite.",0);
				log.error("SprintTwoServiceImpl.addFavourite(-,-,...) ce "+IndoUtil.getFullLog(ce));
			}finally{
				log.info("SprintTwoServiceImpl.addFavourite(-,-,...) -T END");
			}
		return map;
	}
	
	@Override
	public Map<String, Object> retriveFavourite(String msisdn) {
		 Map<String,Object> map=new HashMap<String,Object>();
			try{
				List<Map<String, Object>> data = dbUtil.getData("select * from SATURN_FAVORITE_DATA where msisdn=?  order by fav_seq asc,insert_date desc",new Object[]{msisdn});
				List<Map<String, Object>> data1 = new ArrayList<Map<String, Object>>();
				List<Map<String, Object>> data2 = new ArrayList<Map<String, Object>>();
				if(null!=data && data.size()>0){
						map.put("Status","SUCCESS");
						for(Map<String, Object> m: data){
							if(null==m.get("FAV_SEQ") || StringUtils.isEmpty(m.get("FAV_SEQ").toString()) || m.get("FAV_SEQ").toString().equals("0")){
								data1.add(m);
							}else{
								data2.add(m);
							}
						}
						data1.addAll(data2);
						map.put("FavoriteList", data1);
					}
				}catch(Exception ce){
					IndoUtil.populateErrorMap(map, "Saturn-019",  "Failed to retrive favourite.",0);
					log.error("SprintTwoServiceImpl.retriveFavourite(-,-,...) ce "+IndoUtil.getFullLog(ce));
				}finally{
					log.info("SprintTwoServiceImpl.retriveFavourite(-,-,...) -T END");
				}
			return map;
	}
	@Override
	public Map<String, Object> arrangeFavourite(List<Object[]> listObj) {
		for(Object[] obj : listObj){
			for(int i=0;i<obj.length;i++){
				log.info("SprintTwoServiceimpl.arrangeFavourite() "+obj[i]);
			}
			log.info("#####################################");
		}
	 Map<String,Object> map=new HashMap<String,Object>();
		try{
				int[] ct = dbUtil.insertBatch("update SATURN_FAVORITE_DATA set fav_seq = ? where TRANSACTION_TYPE=? and MSISDN=?", listObj);
				map.put("Status", "SUCCESS");
			}catch(Exception ce){
				IndoUtil.populateErrorMap(map, "Saturn-019",  "Failed to arrangeFavourite .",0);
				log.error("SprintTwoServiceImpl.arrangeFavourite(-,-,...) ce "+IndoUtil.getFullLog(ce));
			}finally{
				log.info("SprintTwoServiceImpl.arrangeFavourite(-,-,...) -T END");
			}
		return map;
	}
	
	@Override
	public Map<String, Object> deleteFavourite(List<Object[]> listObj) {
	
	 Map<String,Object> map=new HashMap<String,Object>();
		try{
				int[] ct = dbUtil.insertBatch("delete from  SATURN_FAVORITE_DATA  where TRANSACTION_TYPE=? and MSISDN=?", listObj);
				map.put("Status", "SUCCESS");
			}catch(Exception ce){
				IndoUtil.populateErrorMap(map, "Saturn-019",  "Failed to Delete Favorite.",0);
				log.error("SprintTwoServiceImpl.arrangeFavourite(-,-,...) ce "+IndoUtil.getFullLog(ce));
			}finally{
				log.info("SprintTwoServiceImpl.arrangeFavourite(-,-,...) -T END");
			}
		return map;
	}
	
	public Map<String, String> dompetkuPayment(String msisdn,String amount) {
		if(msisdn.startsWith("628")){
			msisdn = msisdn.replaceFirst("628", "08");
		}
		Map<String, String> data = new HashMap<String, String>();
		try{
			  TripleDES td= new TripleDES();
		      DateFormat timeFormat = new SimpleDateFormat("HHmmss");
		      String dte=timeFormat.format(new Date());
		    //StringBuilder domPin=new StringBuilder("1nd054t2ois");
		    //String initiator="4gmobileagent";
		   // String myEncryptionKey = "4yL8GJqTH5EiX0PPC0eT1lRZ";
		    //String httpsURL = "https://mapi.dompetku.com/webapi/user_inquiry";//prod url
		    //String query = "userid=4gmobileagent";
		      StringBuilder domPin=new StringBuilder(prop.getProperty("REG_DOMPETKU_PIN"));
		      String initiator=prop.getProperty("REG_DOMPETKU_INITIATOR");
		      String myEncryptionKey = prop.getProperty("REG_DOMPETKU_KEY");
		      String httpsURL = "https://mapi.dompetku.com/webapi/do_payment";
		      String sigA=dte+domPin;
		      String sigB=domPin.reverse()+"|"+initiator;
		      String sigC=sigA+"|"+sigB;
		      String encrypted=td.encrypt(sigC, myEncryptionKey);
		     /* String query = "userid="+prop.getProperty("REG_DOMPETKU_USERID");
		      query += "&signature=" + encrypted ;
		      query += "&agentid="+agentId ;
		      query += "&to="+msisdn+"&locationid=0";*/
		      List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			  	urlParameters.add(new BasicNameValuePair("userid", prop.getProperty("REG_DOMPETKU_USERID")));
			  	urlParameters.add(new BasicNameValuePair("signature", encrypted));
			  	urlParameters.add(new BasicNameValuePair("extRef", ""));
			  	urlParameters.add(new BasicNameValuePair("to", msisdn));
			  	urlParameters.add(new BasicNameValuePair("amount", amount));
		  	HttpEntity entity = null;
			CloseableHttpClient  client = null;
			HttpPost request = null;
		      try{
		    	  client = httpConn.getHttpClient();
			      request = new HttpPost(httpsURL);
			      request.setEntity(new UrlEncodedFormEntity(urlParameters));
			      HttpResponse response = client.execute(request);
			      int statusCode = response.getStatusLine().getStatusCode();
			        if (statusCode == 200 ){
			        	entity = response.getEntity();
			            String content = EntityUtils.toString(entity);
			            log.info("");
					    log.info("Result is "+content);
					    log.info("Resp Code:"+statusCode); 
				      
			            return data;
			        }else{
			        	data.put("Status", "Failure");
			        	data.put("ErrorCode", "Indo-1016");
			        	data.put("ErrorDescription", "No Data Found.");
			        	entity = response.getEntity();
			        	String content = EntityUtils.toString(entity);
			            log.info("error dompetku check- "+content);
			        }
		      }catch(IOException e){
		    	  IndoUtil.populateErrorMap(data, "Indo-1023","No Data Found.");
		    	  log.info("Indo-2061- Exception IOException- "+IndoUtil.getFullLog(e));
		      }catch(Exception ce){
		    	  IndoUtil.populateErrorMap(data, "Indo-1023","No Data Found.");
					log.info("Indo-2062- Exception Occured while checking dompetku registration- "+IndoUtil.getFullLog(ce));
		      }finally{
		    	  log.info("***********Closing Streams********");
		    	  try {
		    		  	EntityUtils.consumeQuietly(entity);
			    		  	if(null!=request){
			    		  		request.releaseConnection();
			    		  	}if(null!=client){
			    		  		client.close();
			    		  	}
						} catch (IOException e) {
						log.info("Indo-2062- Exception Occured "+e);
					}
		      }
		}catch(Exception ce){
			IndoUtil.populateErrorMap(data, "Indo-1023",ce.getClass().getSimpleName());
			log.info("Indo-2063- Exception Occured while checking dompetku registration "+IndoUtil.getFullLog(ce));
		}
		return data;
	}
	




@Override
	public Map<String, Object> logPayment(String msisdn,String transactionType,String amount,
			String transactionData1, String transactionData2, String transactionData3, String transactionData4, String transactionData5,String custType) {
		Map<String, Object> map= new HashMap<String, Object>();
		try{
			int ct = dbUtil.saveData("INSERT INTO SATURN_PAYMENT_LOG(MSISDN,AMOUNT,Transaction_type,insert_Date,TEXT1,TEXT2,TEXT3,TEXT4,TEXT5,CUSTTYPE) values(?,?,?,sysdate,?,?,?,?,?,?)",
					 new Object[]{msisdn,amount,transactionType,transactionData1,transactionData2,transactionData3,transactionData4,transactionData5,custType});
			if(ct>0){
					map.put("Status","SUCCESS");
				} 
			}catch(Exception ce){
				IndoUtil.populateErrorMap(map, "Migrate-001",  "Failed to add logPayment.",0);
				log.error("SprintTwoServiceImpl.logPayment(-,-,...) ce "+IndoUtil.getFullLog(ce));
			}finally{
				log.info("SprintTwoServiceImpl.logPayment(-,-,...) -T END");
			}
		return map;
	}
}
