/**
 * 
 */
package com.ibm.indo.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.security.KeyManagementException;
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

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.xml.rpc.ServiceException;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
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
	private static Logger log = Logger.getLogger("saturnLogger");
	IndoServiceProperties confProp=IndoServiceProperties.getInstance();
    Properties prop = confProp.getConfigSingletonObject();
	
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
			log.error("IndoUtil.parseDate() - e " + getFullLog(e));
		}
		return formatDate;
	}
	public static Date parseDate(String date, String pattern) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			Date dt = sdf.parse(date);
			return dt;
		}
		catch (Exception e) {
			log.info("Exception " + e.toString());
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
			log.info("Exception " + e.toString());
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
			log.error("IndoUtil.convertToJSON() e "+IndoUtil.getFullLog(e));
		}
		catch (JsonMappingException e) {
			log.error("IndoUtil.convertToJSON() e1 "+IndoUtil.getFullLog(e));
		}
		catch (IOException e) {
			log.error("IndoUtil.convertToJSON() e2 "+IndoUtil.getFullLog(e));
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
	public static String getPrevMins(Date date, int mins) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, -mins);
		return parseDate(cal.getTime(), "yyyy-MM-dd_hh-mm");
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
			log.error("PaymentCalc.calcDiff - Exception " + IndoUtil.getFullLog(e));
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
			log.error("IndoUtil.getPrevMonthYear Exception "+ IndoUtil.getFullLog(e));
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
		if(null!=map.get("Status")){
			if(map.get("Status").toString().equals("SUCCESS")){
				return true;
			}
		}
		return false;
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
			System.out.println("IndoUtil.EPSOrderService() response "+response);
			return  data;
		} catch (MalformedURLException e) {
			IndoUtil.populateErrorMap(data, "Indo-800", e.getClass().getSimpleName());
			System.out.println("IndoUtil.EPSOrderService() ce"+e);
		} catch (ServiceException e) {
			IndoUtil.populateErrorMap(data, "Indo-800", e.getClass().getSimpleName());
			System.out.println("IndoUtil.EPSOrderService() ce"+e);
		} catch (RemoteException e) {
			IndoUtil.populateErrorMap(data, "Indo-800", e.getClass().getSimpleName());
			System.out.println("IndoUtil.EPSOrderService() ce"+e);
			
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
	public static CloseableHttpClient httpsCon(){
		System.out.println("IndoUtil.httpsCon()");
		try{
			SSLContext context = null;
	    	TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}
	
				public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
				}
	
				public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
				}
			} };
	
			try {
				context = SSLContext.getInstance("SSL");
			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			}
			try {
				context.init(null, trustAllCerts, new java.security.SecureRandom());
			} catch (KeyManagementException e1) {
				e1.printStackTrace();
			}
			
			SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(context, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
					  .register("https", sslConnectionFactory)
					  .register("http", new PlainConnectionSocketFactory()).build();
			
		    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			 // Increase max total connection to 200
			 cm.setMaxTotal(100);
			 // Increase default max connection per route to 20
			 cm.setDefaultMaxPerRoute(20);
			 RequestConfig defaultRequestConfig = RequestConfig.custom()
					    .setSocketTimeout(5000)
					    .setConnectTimeout(5000)
					    .setConnectionRequestTimeout(5000)
					    .build();
			 CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).setConnectionManagerShared(true)
					 .setDefaultRequestConfig(defaultRequestConfig).build();
		    return httpClient;
		}catch(Exception ce){
			System.out.println("IndoUtil.getNewHttpsClient()");
		}
			return null;
	}
	public static void main(String args[]){
		System.out.println(IndoUtil.parseDate(new Date(), "yyyyMMddHHmmssSSS"));
	}
}
