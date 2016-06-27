/**
 * 
 */
package com.ibm.indo.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

/**
 * @author Adam
 *
 */
public class IndoXMLParseUtil {
	
	private static Logger log = Logger.getLogger("saturnLogger");
	
	
	public static Map<String,String> getAttributes(List<String> attributes, String xml, String url){
		//log.debug("IndoXMLParseUtil.getAttributes() xml - "+xml);
		Map<String,String> data = new HashMap<String, String>();
		try {
			HttpParams params = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params, 5000);
			HttpConnectionParams.setSoTimeout(params, 15000);
			HttpClient httpclient = new DefaultHttpClient(params);
		    HttpPost httppost = new HttpPost(url);
		        StringEntity se = new StringEntity(xml, "UTF-8");
		        se.setContentType("text/xml");
		        httppost.setEntity(se);
		        HttpResponse httpresponse = httpclient.execute(httppost);
		        int statusCode = httpresponse.getStatusLine().getStatusCode();
		        if (statusCode == 200 ){
		            HttpEntity entity = httpresponse.getEntity();
		            String content = EntityUtils.toString(entity);
		           // log.info("IndoXMLParseUtil.getAttributes() content - "+content);
		            Document xmlDoc = Jsoup.parse(content, "", Parser.xmlParser());
		            for(String att : attributes){
		            	 Elements links = xmlDoc.select(att);
		            	 if(null!=links && !links.isEmpty() && null!=links.get(0)){
		            		 data.put(att, links.get(0).text());
		            	 }
		            }
		            return data;
		        }else{
		        	HttpEntity entity = httpresponse.getEntity();
		            String content = EntityUtils.toString(entity);
		            log.debug("IndoXMLParseUtil.getAttributes() errordata-"+content);
		        	data.put("Status", "FAILURE");
		        	data.put("Success", "false");
		        	data.put("StatusCode", String.valueOf(statusCode));
		        }
		} catch (MalformedURLException e) {
			log.debug("IndoXMLParseUtil.getAttributes() e "+e);
			IndoUtil.populateErrorMap(data, "Indo-400",e.getClass().getSimpleName());
		} catch (IOException e) {
			log.debug("IndoXMLParseUtil.getAttributes() e1 "+e);
			IndoUtil.populateErrorMap(data, "Indo-400",e.getClass().getSimpleName());
		}catch (Exception e) {
			log.debug("IndoXMLParseUtil.getAttributes() e1 "+e);
			IndoUtil.populateErrorMap(data, "Indo-400",e.getClass().getSimpleName());
		}finally{}
		return data;
	}
	public static Map<String,Object> getAttributes(List<String> attributes, String xml, String url,List<String> attr){
		log.debug("IndoXMLParseUtil.getAttributes() xml - "+xml);
		Map<String,Object> data = new HashMap<String, Object>();
		try {
			HttpParams params = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params, 5000);
			HttpConnectionParams.setSoTimeout(params, 15000);
			HttpClient httpclient = new DefaultHttpClient(params);
		    HttpPost httppost = new HttpPost(url);
		        StringEntity se = new StringEntity(xml, "UTF-8");
		        se.setContentType("text/xml");
		        httppost.setEntity(se);
		        HttpResponse httpresponse = httpclient.execute(httppost);
		        int statusCode = httpresponse.getStatusLine().getStatusCode();
		        if (statusCode == 200 ){
		            HttpEntity entity = httpresponse.getEntity();
		            String content = EntityUtils.toString(entity);
		            log.debug("IndoXMLParseUtil.getAttributes() content - "+content);
		            Document xmlDoc = Jsoup.parse(content, "", Parser.xmlParser());
					for(String att : attributes){
						Elements links = xmlDoc.select(att);
						if(null!=links && !links.isEmpty() && null!=links.get(0)){
							data.put(att, links.get(0).text());
						}
					}
		   		 if(null!= attr && attr.size()>0){
		   			List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		   			 for(Element ele : xmlDoc.select(attr.get(1))){
		   				 Map<String,String> map = new HashMap<String,String>();
		   					for(int i=2;i<attr.size();i++){
		   						map.put(attr.get(i),ele.select(attr.get(i)).html());
		   					}
		   					list.add(map);
		   				}
		   			 data.put(attr.get(0), list);
		   		 }
		            return data;
		        }else{
		        	HttpEntity entity = httpresponse.getEntity();
		            String content = EntityUtils.toString(entity);
		            log.debug("IndoXMLParseUtil.getAttributes() errordata-"+content);
		        	data.put("Status", "FAILURE");
		        	data.put("StatusCode", String.valueOf(statusCode));
		        }
		} catch (MalformedURLException e) {
			log.debug("IndoXMLParseUtil.getAttributes() e "+e);
			IndoUtil.populateErrorMap(data, "Indo-401",e.getClass().getSimpleName(),0);
		} catch (IOException e) {
			log.debug("IndoXMLParseUtil.getAttributes() e1 "+e);
			IndoUtil.populateErrorMap(data, "Indo-401",e.getClass().getSimpleName(),0);
		}catch (Exception e) {
			log.debug("IndoXMLParseUtil.getAttributes() e1 "+e);
			IndoUtil.populateErrorMap(data, "Indo-401",e.getClass().getSimpleName(),0);
		}finally{}
		return data;
	}
}
