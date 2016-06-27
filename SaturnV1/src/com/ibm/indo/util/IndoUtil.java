/**
 * 
 */
package com.ibm.indo.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.xml.rpc.ServiceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.icare.eps.EPSOrderWebserviceMoba;
import com.icare.eps.EPSOrderWebserviceMobaServiceLocator;

/**
 * @author Aadam
 * 
 */
public class IndoUtil implements IndoConstants {
	private static Logger log = Logger.getLogger("saturnLoggerV1");
	IndoServiceProperties confProp=IndoServiceProperties.getInstance();
    Properties prop = confProp.getConfigSingletonObject();
	
    public static Properties langProp = LangProperties.getInstance().getConfigSingletonObject();
    
	private static final char[]	CHARSET_AZ		= "abcdefghijklmnopqrstuvwxyz".toCharArray();
	private static final char[]	CHARSET_AZ_09	= "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
	public static final String[] MONTHS = {"January","February","March","April","May","June","July","August","September","October","November","December"};


	public static String getRandAlphabets(int pLength) {
		Random random = new SecureRandom();
		char[] result = new char[pLength];
		for (int i = 0; i < result.length; i++) {
			// picks a random index out of character set > random character 
			int randomCharIndex = random.nextInt(CHARSET_AZ.length);
			result[i] = CHARSET_AZ[randomCharIndex];
		}
		return new String(result);
	}

	public static String getAlphaNumeric(int pLength) {
		Random random = new SecureRandom();
		char[] result = new char[pLength];
		for (int i = 0; i < result.length; i++) {
			// picks a random index out of character set > random character
			int randomCharIndex = random.nextInt(CHARSET_AZ_09.length);
			result[i] = CHARSET_AZ_09[randomCharIndex];
		}
		return new String(result);
	}
	public static String prefix62(String msisdn){
		if(null!=msisdn && msisdn.startsWith("08")){
			msisdn = msisdn.replaceFirst("08", "628");
		}else if(null!=msisdn && msisdn.startsWith("8")){
			msisdn = msisdn.replaceFirst("8", "628");
		}
		return msisdn;
	}
	public static int randInt(int min, int max) {

		// Usually this can be a field rather than a method variable
		Random rand = new Random();
		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}
	public static String getNewCommID() {
		return "CommID-"+randInt(111111, 999999);
	}
	public static JsonArray toJsonArray(List<String> list) {

		Gson gson = new Gson();
		JsonElement element = gson.toJsonTree(list, new TypeToken<List<String>>() {
		}.getType());
		if (!element.isJsonArray()) {
			// fail appropriately
		}
		JsonArray jsonArray = element.getAsJsonArray();
		return jsonArray;
	}

	/*
	 * public static java.sql.Date getSQLDate(String date){ SimpleDateFormat
	 * formatter = new SimpleDateFormat("dd-MM-yyyy"); // your template here try
	 * { java.util.Date dateStr=formatter.parse(date); java.sql.Date dateDB =
	 * new java.sql.Date(dateStr.getTime()); return dateDB; } catch
	 * (ParseException ex) {
	 * Logger.getLogger(IndoUtil.class.getName()).log(Level.SEVERE, null, ex); }
	 * return null; }
	 * 
	 * public static java.sql.Date getSQLDateMMDDYYYY(String date, String
	 * format){ SimpleDateFormat formatter = new SimpleDateFormat(format); //
	 * your template here"MM/dd/yyyy" try { java.util.Date
	 * dateStr=formatter.parse(date); java.sql.Date dateDB = new
	 * java.sql.Date(dateStr.getTime()); return dateDB; } catch (ParseException
	 * ex) { Logger.getLogger(IndoUtil.class.getName()).log(Level.SEVERE, null,
	 * ex); } return null; }
	 */
	public static String formatDate(String format) {
		Date dat = new Date();
		String dateFormat = new SimpleDateFormat(format).format(dat);
		return dateFormat;
	}

	public static String parseDate(String date, String pattern, String newPattern) {
		String formatDate = null;
		try {
			if(StringUtils.isEmpty(date)){
				return "";
			}
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			Date dt = sdf.parse(date);
			sdf = new SimpleDateFormat(newPattern);
			formatDate = sdf.format(dt);
		}
		catch (Exception e) {
			log.error("Indo-3000- IndoUtil.parseDate() - e " + getFullLog(e));
		}
		return formatDate;
	}
	public static Date parseDate(String date, String pattern) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			sdf.setLenient(false);
			Date dt = sdf.parse(date);
			return dt;
		}
		catch (Exception e) {
			log.info("Indo-3001- Exception " + e.toString());
		}
		return null;
	}
	public static String parseDate(Date date, String pattern) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			String dt = sdf.format(date);
			return dt;
		}
		catch (Exception e) {
			log.info("Indo-3002- Exception " + e.toString());
		}
		return null;
	}

	public static JsonArray toJsonArray(ArrayList<Integer> intlist) {
		Gson gson = new Gson();
		JsonElement element = gson.toJsonTree(intlist, new TypeToken<List<String>>() {
		}.getType());
		if (!element.isJsonArray()) {
			// fail appropriately
		}
		JsonArray jsonArray = element.getAsJsonArray();
		return jsonArray;
	}


	public static void setRequestMessage(HttpServletRequest pRequest, String pMessage) {
		pRequest.setAttribute(PARAM_REQ_MESSAGE, pMessage);
	}

	public static String getRequestMessage(HttpServletRequest pRequest) {
		return pRequest.getAttribute(PARAM_REQ_MESSAGE).toString();
	}

	public static void setSessionMessage(HttpServletRequest pRequest, String pMessage) {
		pRequest.getSession().setAttribute(PARAM_SESSION_MESSAGE, pMessage);
	}

	public static String getSessionMessage(HttpServletRequest pRequest) {
		return pRequest.getSession().getAttribute(PARAM_SESSION_MESSAGE).toString();
	}

	public static String getFullLog(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

	public static String convertToJSON(Object obj) {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		try {
			String json = ow.writeValueAsString(obj);
			return json;
		}
		catch (JsonGenerationException e) {
			log.error("Indo-3003- IndoUtil.convertToJSON() e "+IndoUtil.getFullLog(e));
		}
		catch (JsonMappingException e) {
			log.error("Indo-3004- IndoUtil.convertToJSON() e1 "+IndoUtil.getFullLog(e));
		}
		catch (IOException e) {
			log.error("Indo-3005- IndoUtil.convertToJSON() e2 "+IndoUtil.getFullLog(e));
		}
		return null;
	}

	public static List<String> getMonths(Date date, int prev) {

		List<String> months = new ArrayList<String>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		String month = "";
		month = new SimpleDateFormat("MMMM").format(cal.getTime()) + "-" + new SimpleDateFormat("yy").format(cal.getTime());
		months.add(month);
		for (int i = 1; i <= prev; i++) {
			cal.add(Calendar.MONTH, -1);
			month = new SimpleDateFormat("MMMM").format(cal.getTime()) + "-" + new SimpleDateFormat("yy").format(cal.getTime());
			months.add(month);
		}

		return months;
	}

	public static String getPrevDate(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, -days);
		String dat = new SimpleDateFormat("yyyy").format(cal.getTime()) + "-" +new SimpleDateFormat("MM").format(cal.getTime()) + "-" + new SimpleDateFormat("dd").format(cal.getTime());
		return dat;
	}
	
	public static List<String> getMonthsPost(Date date, int next) {

		List<String> months = new ArrayList<String>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		String month = "";
		month = new SimpleDateFormat("MMMM").format(cal.getTime()) + "-" + new SimpleDateFormat("yy").format(cal.getTime());
		months.add(month);
		for (int i = 1; i <= next; i++) {
			cal.add(Calendar.MONTH, 1);
			month = new SimpleDateFormat("MMMM").format(cal.getTime()) + "-" + new SimpleDateFormat("yy").format(cal.getTime());
			months.add(month);
		}

		return months;
	}

	public static Object[] getYearMonths(Date date, int prev) {

		Object[] obj = new Object[2];
		List<String> months = new ArrayList<String>();
		List<String> years = new ArrayList<String>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		String month = "";
		String year = "";
		month = new SimpleDateFormat("MMMM").format(cal.getTime());
		year = new SimpleDateFormat("yy").format(cal.getTime());
		months.add(month);
		years.add(year);
		for (int i = 1; i <= prev; i++) {
			cal.add(Calendar.MONTH, -1);
			month = new SimpleDateFormat("MMMM").format(cal.getTime());
			year = new SimpleDateFormat("yy").format(cal.getTime());
			months.add(month);
			years.add(year);
		}
		log.info("IndoUtil.getMonths() - months " + months);
		log.info("IndoUtil.getMonths() - years " + years);
		obj[0] = months;
		return obj;
	}
	
	public static int getMonthNumber(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH);
		return month+1;
	}
	
	public static String getMonthName(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH);
		return MONTHS[month];
	}
	
	public static boolean isToday(String date, String format){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int day = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTime(parseDate(date, format));
		int day1 = cal.get(Calendar.DAY_OF_MONTH);
		if(day==day1){
			return true;
		}
		return false;
	}
	
	public static boolean isEmpty(String pStr){
		if(null==pStr || pStr.equals("")){
			return true;
		}
		return false;
	}
	public static boolean isBlank(String str) {
		if (null == str || str.trim().length() == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public static String addBlankSpace() {
		return " ";
	}

	/**
	 * Check for the java.lang.Integer value.
	 * 
	 * @param str
	 * @return boolean 'true' or 'false'
	 */
	public static boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Check whether given input is bad or not.
	 * 
	 * @param strings
	 * @return boolean
	 */
	public static boolean isBadGivenInput(String[] strings) {
		for (short iCount = 0; iCount < strings.length; iCount++) {
			if (isBlank(strings[iCount])) {
				return true;
			}
		}
		return false;
	}
	
	public static String calcDiff(Object object1, Object object2) {
		Double c = new Double(0);
		try {
			c = (Double.parseDouble(object1.toString()) - Double.parseDouble(object2.toString())) ;
		}
		catch (Exception e) {
			log.error("Indo-3006- PaymentCalc.calcDiff - Exception " + IndoUtil.getFullLog(e));
		}
		return String.valueOf(c);
	}
		
	public static String getPrevMonthYear(String date ) {
		String result="";
		try {
		Date d1 = IndoUtil.parseDate(date, "MM/dd/yyyy");
		Calendar cal =Calendar.getInstance();
		cal.setTime(d1);
		cal.add(Calendar.MONTH, -1);
		String month = new SimpleDateFormat("MMMM").format(cal.getTime());
		String year = new SimpleDateFormat("yyyy").format(cal.getTime());
		result = month + " " + year;
		}
		catch (Exception e) {
			log.error("Indo-3007- IndoUtil.getPrevMonthYear Exception "+ IndoUtil.getFullLog(e));
		}
		return result;
	}
	public static Map<String,String> populateErrorMap(Map<String,String> data, String code, String desc){
		data.put("Status", "FAILURE");
		data.put("ErrorCode", code);
		data.remove("Success");
		if(!isEmpty(desc) && !isEmpty(langProp.getProperty(desc+"_EN"))){
			data.put("ErrorMessage_en", langProp.getProperty(desc+"_Head_EN"));
			data.put("ErrorMessage_id", langProp.getProperty(desc+"_Head_ID"));
			data.put("ErrorDescription_en", langProp.getProperty(desc+"_EN"));
			data.put("ErrorDescription_id", langProp.getProperty(desc+"_ID"));
		}else if(!isEmpty(desc)){
			data.put("ErrorMessage_en", desc);
			data.put("ErrorMessage_id", desc);
			data.put("ErrorDescription_en", desc);
			data.put("ErrorDescription_id", desc);
		}else{
			data.put("ErrorMessage_en", "Sorry, system/network is error");
			data.put("ErrorMessage_id", "Maaf, sistem/jaringan sedang error!");
			data.put("ErrorDescription_en", "Please check its availability later.");
			data.put("ErrorDescription_id", "Mohon periksa kembali");
		}
		return data;
	}
	public static Map<String,Object> populateErrorMap(Map<String,Object> data, String code, String desc, int extra){
		data.put("Status", "FAILURE");
		data.put("ErrorCode", code);
		data.remove("Success");
	//	log.info("IndoUtil.populateErrorMap() -- "+langProp.getProperty(desc+"_EN"));
		if(!isEmpty(desc) && !isEmpty(langProp.getProperty(desc+"_EN"))){
			data.put("ErrorMessage_en", langProp.getProperty(desc+"_Head_EN"));
			data.put("ErrorMessage_id", langProp.getProperty(desc+"_Head_ID"));
			data.put("ErrorDescription_en", langProp.getProperty(desc+"_EN"));
			data.put("ErrorDescription_id", langProp.getProperty(desc+"_ID"));
		}else if(!isEmpty(desc)){
			data.put("ErrorMessage_en", desc);
			data.put("ErrorMessage_id", desc);
			data.put("ErrorDescription_en", desc);
			data.put("ErrorDescription_id", desc);
		}else{
			data.put("ErrorMessage_en", "Sorry, system/network is error");
			data.put("ErrorMessage_id", "Maaf, sistem/jaringan sedang error!");
			data.put("ErrorDescription_en", "Please check its availability later.");
			data.put("ErrorDescription_id", "Mohon periksa kembali");
		}
		return data;
	}
	public static String validateNumber(String Msisdn){
		if(Msisdn.startsWith("08")){
			return Msisdn.replaceFirst("08", "628");
		}else if(Msisdn.startsWith("8")){
			return Msisdn.replaceFirst("8", "628");
		}else{
			return Msisdn;
		}
	}
	public static boolean isSuccess(Map<String,?> map){
		if(null!=map.get("Status")){
			if(map.get("Status").toString().equals("SUCCESS")){
				return true;
			}
		}
		return false;
	}
	public static boolean isMigrated(Map<String,?> map){
		if(null!=map.get("Mode")){
			if(map.get("Mode").toString().equals("Migrated")){
				return true;
			}
		}
		return false;
	}
	public Map<String,String> EPSOrderService(String xml,String type){
		log.info("Entering  EPSOrderService for customer Type "+ type );
		Map<String,String> data = new HashMap<String,String>();
		try {
			String  status="";
			String id="";
			String error="";
			String response="";
			String url= prop.getProperty("ICARE_TRANSACTION_URL");
			EPSOrderWebserviceMobaServiceLocator loc= new EPSOrderWebserviceMobaServiceLocator();
			EPSOrderWebserviceMoba moba = loc.getEPSOrderWebserviceMobaSoapPort(new URL(url));
			log.info("customer type is "+type);
			if(type!=null && type.equalsIgnoreCase("Postpaid")){
				log.info("Requesting to Postpaid Method");
				log.info("Requesting to Postpaid Method");
				response = moba.updateOrderPostpaid(xml);
				}else{
					log.info("Requesting to Prepaid Method");
					log.info("Requesting to Prepaid Method");
				response = moba.updateOrderPrepaid(xml);
			}
			
			log.info(response);
			log.info("Response from Service is "+response);
			Document xmlDoc = Jsoup.parse(response, "", Parser.xmlParser());
			if(null!=xmlDoc && null!=xmlDoc.select("Success")){
				status = xmlDoc.select("Success").first().ownText().trim();
			}
			if(status!=null && status.equalsIgnoreCase("true")){
				if(null!=xmlDoc && null!=xmlDoc.select("EPSOrderID") && null!=xmlDoc.select("EPSOrderID").first()){
					id = xmlDoc.select("EPSOrderID").first().ownText().trim();
				}
			}else{
				if(null!=xmlDoc && null!=xmlDoc.select("ErrorMessage")){
					error = xmlDoc.select("ErrorMessage").first().ownText().trim();
				}
			}
		if(null!=status && status.equalsIgnoreCase("true")){
				data.put("CommunicationId",id);
				data.put("Status", "SUCCESS");
			}else{
				data.put("Status","FAILURE");
				IndoUtil.populateErrorMap(data, "Indo-800",error);
			}
			log.info("Indo-30- IndoUtil.EPSOrderService() response "+response);
			return  data;
		} catch (MalformedURLException e) {
			IndoUtil.populateErrorMap(data, "Indo-800", e.getClass().getSimpleName());
			log.info("Indo-3008- IndoUtil.EPSOrderService() ce"+e);
		} catch (ServiceException e) {
			IndoUtil.populateErrorMap(data, "Indo-800", e.getClass().getSimpleName());
			log.info("Indo-3009- IndoUtil.EPSOrderService() ce"+e);
		} catch (RemoteException e) {
			IndoUtil.populateErrorMap(data, "Indo-800", e.getClass().getSimpleName());
			log.info("Indo-3010- IndoUtil.EPSOrderService() ce"+e);
			
		}
		return null;
	}
	
	public static String  getMD5(String input) {
        try {
        	MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
 
	public static HttpURLConnection httpCon(String httpURL, String query){
		log.info("IndoUtil.httpCon()-Trying to  Connect");
		try{
		URL myurl = new URL(httpURL);
	      HttpURLConnection con = (HttpURLConnection)myurl.openConnection();
	      con.setRequestMethod("POST");
	      con.setRequestProperty("Content-length", String.valueOf(query.length())); 
	      con.setRequestProperty("Content-Type","application/x-www-form-urlencoded"); 
	      con.setDoOutput(true); 
	      con.setDoInput(true); 
	      log.info("IndoUtil.httpCon()- Connection Established");
	      return con;
		}catch(Exception ce){
			log.info("Indo-3012- IndoUtil.httpCon()"+ce);
		}
		return null;
	}
	public static HttpsURLConnection httpsCon(String httpsURL, String query){
		log.info("IndoUtil.httpCons()-Trying to  Connect");
		try{
		TrustManager[] trustAllCerts = new TrustManager[]{
		          new X509TrustManager() {
		              public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		                  return null;
		              }
		              public void checkClientTrusted(
		                  java.security.cert.X509Certificate[] certs, String authType) {
		              }
		              public void checkServerTrusted(
		                  java.security.cert.X509Certificate[] certs, String authType) {
		              }
		          }
		      };
		      // Install the all-trusting trust manager   
		      SSLContext sc = SSLContext.getInstance("SSL");   
		      sc.init(null, trustAllCerts, new java.security.SecureRandom());   
		      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());   
		      // Create all-trusting host name verifier   
		      HostnameVerifier allHostsValid = new HostnameVerifier() {   
		          public boolean verify(String hostname, SSLSession session) {   
		              return true;   
		          }   
		      }; 
		      // Install the all-trusting host verifier   
		      HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid); 
		      URL myurl = new URL(httpsURL);
		      HttpsURLConnection con = (HttpsURLConnection)myurl.openConnection();
		      con.setConnectTimeout(3000);
		      con.setReadTimeout(3000);
		      con.setRequestMethod("POST");
		      con.setRequestProperty("Content-length", String.valueOf(query.length())); 
		      con.setRequestProperty("Content-Type","application/x-www-form-urlencoded"); 
		      con.setDoOutput(true); 
		      con.setDoInput(true); 
		      log.info("IndoUtil.httpCon()- Connection Established");
		      return con;
		}catch(IOException e){
			log.info("Indo-3013- IndoUtil.httpscon()-IOException "+IndoUtil.getFullLog(e));
		}catch(Exception ce){
			log.info("Indo-3014- IndoUtil.httpscon()"+IndoUtil.getFullLog(ce));
		}finally{
			//if(null!=con){con.disconnect();}
		}
			return null;
	}
	public static String getString(Object obj){
		if(null!=obj){
			return obj.toString();
		}
		return "";
	}
	public static String getAsString(JsonElement obj){
		if(null!=obj){
			return obj.getAsString();
		}
		return null;
	}
	public static Map<String,String> saveOrUpdateQuery(Map<String,String> insertMap,Map<String,String> updateMap, String idCol,String idVal, String table){
		Map<String,String> queries = new HashMap<String,String>();
		try{
			String whereClause = "";
			List<String> ignore = new ArrayList<String>();
			ignore.add("SYSDATE");
			StringBuffer qrySelect= new StringBuffer("Select count(*) as count from ").append(table);
			if(insertMap.containsKey("where")){
				whereClause=insertMap.get("where");
				insertMap.remove("where");
				qrySelect.append(" where ").append(whereClause);
			}else if(ignore.contains(idVal.toUpperCase())){
				qrySelect.append(" where ").append(idCol).append("=").append(idVal);
			}else{
				qrySelect.append(" where ").append(idCol).append("='").append(idVal).append("'");
			}
			StringBuffer qryUpdate= new StringBuffer("Update ").append(table).append(" set ");
			StringBuffer qryInsert= new StringBuffer("Insert into ").append(table).append("(");
			if(null==updateMap || updateMap.isEmpty()){updateMap=insertMap;}
			for (Map.Entry<String, String> entry : updateMap.entrySet()){
				qryUpdate.append(entry.getKey()).append("=");
				if(ignore.contains(entry.getValue().toUpperCase())){
					qryUpdate.append(entry.getValue()).append(",");
				}else{
					qryUpdate.append("'").append(entry.getValue()).append("',");
				}
			}
			qryUpdate.delete(qryUpdate.length()-1,qryUpdate.length());
			if(!whereClause.isEmpty()){
				qryUpdate.append(" where ").append(whereClause);
			}else{
				qryUpdate.append(" where ").append(idCol).append("='").append(idVal).append("'");
			}
			StringBuffer values = new StringBuffer().append("values(");
			for (Map.Entry<String, String> entry : insertMap.entrySet()){
				qryInsert.append(entry.getKey()).append(",");
				if(ignore.contains(entry.getValue().toUpperCase())){
					values.append(entry.getValue()).append(",");
				}else{
					values.append("'").append(entry.getValue()).append("',");
				}
			}
			qryInsert.delete(qryInsert.length()-1,qryInsert.length());
			qryInsert.append(") ");
			values.delete(values.length()-1,values.length());
			values.append(")");
			qryInsert.append(values);
			
			queries.put("select",qrySelect.toString());
			queries.put("update",qryUpdate.toString());
			queries.put("insert",qryInsert.toString());
			//queries.put("Status", "SUCCESS");
			log.info("IndoUtil.saveOrUpdateQuery() queries  "+queries);
		}catch(Exception ce){
			log.error("IndoUtil.saveOrUpdateQuery() e "+getFullLog(ce));
			populateErrorMap(queries, "Util-700", " to generate sql.");
		}
		return queries;
	}
	public static Map<String,Object> saveOrUpdateSpring(Map<String,String> insertMap,Map<String,String> updateMap, String idCol,String idVal, String table){
		Map<String,Object> queries = new HashMap<String,Object>();
		try{
			String whereClause = "";
			List<Object> insertObj = new ArrayList<Object>();
			List<Object> updObj = new ArrayList<Object>();
			
			List<String> ignore = new ArrayList<String>();
			ignore.add("SYSDATE");
			StringBuffer qrySelect= new StringBuffer("Select count(*) as count from ").append(table);
			if(insertMap.containsKey("where")){
				whereClause=insertMap.get("where");
				insertMap.remove("where");
				qrySelect.append(" where ").append(whereClause);
			}else if(ignore.contains(idVal.toUpperCase())){
				qrySelect.append(" where ").append(idCol).append("=").append(idVal);
			}else{
				qrySelect.append(" where ").append(idCol).append("='").append(idVal).append("'");
			}
			StringBuffer qryUpdate= new StringBuffer("Update ").append(table).append(" set ");
			StringBuffer qryInsert= new StringBuffer("Insert into ").append(table).append("(");
			if(null==updateMap || updateMap.isEmpty()){updateMap=insertMap;}
			for (Map.Entry<String, String> entry : updateMap.entrySet()){
				if(ignore.contains(entry.getValue())){
					qryUpdate.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
				}else{
					qryUpdate.append(entry.getKey()).append("=?,");
					updObj.add(entry.getValue());
				}
			}
			qryUpdate.delete(qryUpdate.length()-1,qryUpdate.length());
			if(!whereClause.isEmpty()){
				qryUpdate.append(" where ").append(whereClause);
			}else{
				qryUpdate.append(" where ").append(idCol).append("=?");
				updObj.add(idVal);
			}
			StringBuffer values = new StringBuffer().append(" values(");
			for (Map.Entry<String, String> entry : insertMap.entrySet()){
				if(ignore.contains(entry.getValue())){
					qryInsert.append(entry.getKey()).append(",");
					values.append(entry.getValue()).append(",");
				}else{
					qryInsert.append(entry.getKey()).append(",");
					values.append("?,");
					insertObj.add(entry.getValue());
				}
			}
			qryInsert.delete(qryInsert.length()-1,qryInsert.length());
			qryInsert.append(") ");
			values.delete(values.length()-1,values.length());
			values.append(")");
			qryInsert.append(values);
			//log.info("IndoUtil.saveOrUpdateSpring() insertObj "+insertObj);
			queries.put("select",qrySelect.toString());
			queries.put("update",qryUpdate.toString());
			queries.put("insert",qryInsert.toString());
			queries.put("updateObj",updObj.toArray());
			queries.put("insertObj",insertObj.toArray());
			//queries.put("Status", "SUCCESS");
			log.info("IndoUtil.saveOrUpdateQuery() select  "+qrySelect.toString());
			log.info("IndoUtil.saveOrUpdateQuery() update  "+qryUpdate.toString());
			log.info("IndoUtil.saveOrUpdateQuery() insert  "+qryInsert.toString());
		}catch(Exception ce){
			log.error("IndoUtil.saveOrUpdateQuery() e "+getFullLog(ce));
			//populateErrorMap(queries, "Util-701", "failed to generate sql.",0);
		}
		return queries;
	}
	public static Object[] msisdnArray(String msisdn){
		Object[] obj = new Object[5];
		String msisdn62 = IndoUtil.prefix62(msisdn);
		String msisdn08 = "";
		if(msisdn.startsWith("62")){
			msisdn08=StringUtils.removeStart(msisdn, "62");
		}
		String msisdn0 = "";
		if(msisdn.startsWith("62")){
			msisdn0=StringUtils.removeStart(msisdn, "62");
			msisdn0="0"+msisdn0;
		}
		if(msisdn.startsWith("8")){
			msisdn0="0"+msisdn0;
		}
		String msisdnN = "";
		if(msisdn.startsWith("62")){
			msisdnN=StringUtils.removeStart(msisdn, "62");
		}
		if(msisdn.startsWith("08")){
			msisdnN=StringUtils.removeStart(msisdn, "0");
		}
		obj[0] = msisdn;
		obj[1] = msisdn62;
		obj[2] = msisdn08;
		obj[3] = msisdn0;
		obj[4] = msisdnN;
		return obj;
	}
	public static Object[] appendValue(Object[] obj, Object newObj) {
		ArrayList<Object> temp = new ArrayList<Object>(Arrays.asList(obj));
		temp.add(newObj);
		return temp.toArray();
	  }
	public static void main(String args[]){
		Map<String,String> map = new HashMap<String,String>();
		map.put("user", "test");
		map.put("pwd", "1234");map.put("pwd1", "SYSDATE");map.put("pwd2", "1234");map.put("pwd3", "1234");map.put("pwd4", "1234");
		Map<String,String> mapU = new HashMap<String,String>();
		mapU.put("user", "test");
		mapU.put("pwd", "SYSDATE");
		Map<String, Object> obj = saveOrUpdateSpring(map,mapU,"id","1201","users");
		Object[] upO = (Object[]) obj.get("updateObj");
		Object[] inO = (Object[]) obj.get("insertObj");
		log.info(obj.get("update"));
		for(Object o : upO){
			log.info(o);
		}
		log.info(obj.get("insert"));
		for(Object o : inO){
			log.info(o);
		}
	}
}
