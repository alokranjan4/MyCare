/**
 * 
 */
package com.ibm.ijoin.util;

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
	private static Logger log = Logger.getLogger("im2");
	IndoServiceProperties confProp=IndoServiceProperties.getInstance();
    Properties prop = confProp.getConfigSingletonObject();
	
	private static final char[]	CHARSET_AZ		= "abcdefghijklmnopqrstuvwxyz".toCharArray();
	private static final char[]	CHARSET_AZ_09	= "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
	public static final String[] MONTHS = {"January","February","March","April","May","June","July","August","September","October","November","December"};

	static String btn = "<button class='close' aria-label='Close' data-dismiss='alert' type='button'><span aria-hidden='true'>×</span></button>";
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
		catch (JsonGenerationException e) {System.out.println();
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
		System.out.println("IndoUtil.getMonths() - months " + months);
		System.out.println("IndoUtil.getMonths() - years " + years);
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
		if(!isEmpty(desc)){
			data.put("ErrorDescription", desc);
		}else{
			data.put("ErrorDescription", "System Error.");
		}
		return data;
	}
	public static Map<String,Object> populateErrorMap(Map<String,Object> data, String code, String desc, int extra){
		data.put("Status", "FAILURE");
		data.put("ErrorCode", code);
		data.remove("Success");
		if(!isEmpty(desc)){
			data.put("ErrorDescription", desc);
		}else{
			data.put("ErrorDescription", "System Error.");
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
		if(null!=map && !map.isEmpty() && null!=map.get("Status")){
			if(map.get("Status").toString().equalsIgnoreCase("SUCCESS")){
				return true;
			}
		}
		return false;
	}
	
	public static String sMsg(String msg){
		return "<div class='text-success'>"+msg+"</div>";
	}
	public static String eMsg(String msg){
		return "<p class='text-danger'>"+msg+"</p>";
	}
	public static String iMsg(String msg){
		return "class='text-warning'>"+msg+"</div>";
	}
	
	public Map<String,String> EPSOrderService(String xml,String type){
		System.out.println("Entering  EPSOrderService for customer Type "+ type );
		Map<String,String> data = new HashMap<String,String>();
		try {
			String  status="";
			String id="";
			String error="";
			String response="";
			String url= prop.getProperty("ICARE_TRANSACTION_URL");
			EPSOrderWebserviceMobaServiceLocator loc= new EPSOrderWebserviceMobaServiceLocator();
			EPSOrderWebserviceMoba moba = loc.getEPSOrderWebserviceMobaSoapPort(new URL(url));
			System.out.println("customer type is "+type);
			if(type!=null && type.equalsIgnoreCase("Postpaid")){
				System.out.println("Requesting to Postpaid Method");
				System.out.println("Requesting to Postpaid Method");
				response = moba.updateOrderPostpaid(xml);
				}else{
					System.out.println("Requesting to Prepaid Method");
					System.out.println("Requesting to Prepaid Method");
				response = moba.updateOrderPrepaid(xml);
			}
			
			System.out.println(response);
			System.out.println("Response from Service is "+response);
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
		if(null!=obj && obj instanceof String){
			return obj.toString();
		}
		return "";
	}
	public static void main(String args[]){
		System.out.println(parseDate("30012017", "ddMMyyyy"));
		
	}
	
	public static String prefix62(String msisdn){
		if(null!=msisdn && msisdn.startsWith("08")){
			msisdn = msisdn.replaceFirst("08", "628");
		}else if(null!=msisdn && msisdn.startsWith("8")){
			msisdn = msisdn.replaceFirst("8", "628");
		}
		return msisdn;
	}

}
