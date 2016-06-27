/**
 * 
 */
package com.ibm.indo.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.indo.service.HttpConnService;
import com.ibm.indo.service.XMLService;
import com.ibm.indo.util.IndoUtil;

/**
 * @author Adeeb
 *
 */
@Service
public class XMLServiceImpl implements XMLService {
	@Autowired
	private HttpConnService httpConn;
	private static Logger log = Logger.getLogger("saturnLoggerV1");
	
	@Override
	public Map<String,String> getAttributes(List<String> attributes, String xml, String url) {
		HttpEntity entity = null;
		CloseableHttpClient  client = null;
		HttpPost request = null;
		Map<String,String> data = new HashMap<String, String>();
	      try{
	    	  client = httpConn.getHttpClient();
		      request = new HttpPost(url);
		      StringEntity se = new StringEntity(xml, "UTF-8");
		        se.setContentType("text/xml");
		        request.setEntity(se);
		        HttpResponse httpresponse = client.execute(request);
		        int statusCode = httpresponse.getStatusLine().getStatusCode();
		        if (statusCode == 200 ){
		            entity = httpresponse.getEntity();
		            String content = EntityUtils.toString(entity);
		           //log.info("IndoXMLParseUtil.getAttributes() content - "+content);
		            Document xmlDoc = Jsoup.parse(content, "", Parser.xmlParser());
		            for(String att : attributes){
		            	 Elements links = xmlDoc.select(att);
		            	 if(null!=links && !links.isEmpty() && null!=links.get(0)){
		            		 data.put(att, links.get(0).text());
		            	 }
		            }
		            return data;
		        }else{
		        	entity = httpresponse.getEntity();
		            String content = EntityUtils.toString(entity);
		            log.debug("XMLServiceImpl.getAttributes() errordata-"+content);
		        	data.put("Status", "FAILURE");
		        	data.put("Success", "false");
		        	data.put("StatusCode", String.valueOf(statusCode));
		        }
	      }catch(IOException e){
	    	  IndoUtil.populateErrorMap(data, "Indo-1023","No Data Found.");
	    	  log.error("Indo-2061- XMLServiceImpl.getAttributes() e- "+IndoUtil.getFullLog(e));
	      }catch(Exception ce){
	    	  IndoUtil.populateErrorMap(data, "Indo-1023","No Data Found.");
				log.error("Indo-2062- XMLServiceImpl.getAttributes()  ce- "+IndoUtil.getFullLog(ce));
	      }finally{
	    	   try {
	    		  	EntityUtils.consumeQuietly(entity);
		    		  	if(null!=request){
		    		  		request.releaseConnection();
		    		  	}if(null!=client){
		    		  		client.close();
		    		  	}
					} catch (IOException e) {
					log.info("Indo-2062- XMLServiceImpl.getAttributes() closing streams "+e);
				}
	      }
		return data;
	}

	@Override
	public Map<String, Object> getAttributes(List<String> attributes, String xml, String url, List<String> attr) {
		HttpEntity entity = null;
		CloseableHttpClient  client = null;
		HttpPost request = null;
		Map<String, Object> data = new HashMap<String, Object>();
	      try{
	    	  	client = httpConn.getHttpClient();
	    	  	request = new HttpPost(url);
		        StringEntity se = new StringEntity(xml, "UTF-8");
		        se.setContentType("text/xml");
		        request.setEntity(se);
		        HttpResponse httpresponse = client.execute(request);
		        int statusCode = httpresponse.getStatusLine().getStatusCode();
		        if (statusCode == 200 ){
		            entity = httpresponse.getEntity();
		            String content = EntityUtils.toString(entity);
		         //   log.debug("IndoXMLParseUtil.getAttributes() content - "+content);
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
		        	entity = httpresponse.getEntity();
		            String content = EntityUtils.toString(entity);
		            log.debug("IndoXMLParseUtil.getAttributes() errordata-"+content);
		        	data.put("Status", "FAILURE");
		        	data.put("StatusCode", String.valueOf(statusCode));
		        }
	      }catch(IOException e){
	    	  IndoUtil.populateErrorMap(data, "Indo-2064","No Data Found.",0);
	    	  log.info("Indo-2064- XMLServiceImpl.getAttributes()- "+IndoUtil.getFullLog(e));
	      }catch(Exception ce){
	    	  IndoUtil.populateErrorMap(data, "Indo-2065","No Data Found.",0);
				log.info("Indo-2065- XMLServiceImpl.getAttributes()  ce- "+IndoUtil.getFullLog(ce));
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
					log.info("Indo-2066- XMLServiceImpl.getAttributes() closing streams "+e);
				}
	      }
		return data;
	}

	@Override
	public Map<String, Object> getRawXML(String xml, String url) {
		HttpEntity entity = null;
		CloseableHttpClient  client = null;
		HttpPost request = null;
		Map<String, Object> data = new HashMap<String, Object>();
	      try{
	    	  	client = httpConn.getHttpClient();
	    	  	request = new HttpPost(url);
		        StringEntity se = new StringEntity(xml, "UTF-8");
		        se.setContentType("text/xml");
		        request.setEntity(se);
		       
		        HttpResponse httpresponse = client.execute(request);
		        int statusCode = httpresponse.getStatusLine().getStatusCode();
		     //   log.info("IndoXMLParseUtil.getRawXML() statusCode-"+statusCode);
		        if (statusCode == 200 ){
		            entity = httpresponse.getEntity();
		            log.info("URL icare "+ url);
		            log.info("Request submitted to Icare.");
		            log.info("Entity from icare "+ entity);
		            String content = EntityUtils.toString(entity);
		            log.info("xml content from icare "+ content);
		            data.put("Status", "SUCCESS");
		            data.put("xml", content);
		            return data;
		        }else{
		        	entity = httpresponse.getEntity();
		            String content = EntityUtils.toString(entity);
		            log.debug("IndoXMLParseUtil.getAttributes() errordata-"+content);
		            data.put("xml", content);
		        	data.put("Status", "FAILURE");
		        	data.put("Success", "false");
		        	data.put("StatusCode", String.valueOf(statusCode));
		        }
	      }catch(IOException e){
	    	  IndoUtil.populateErrorMap(data, "Indo-2064","No Data Found.",0);
	    	  log.info("Indo-2064- XMLServiceImpl.getRawXML()- "+IndoUtil.getFullLog(e));
	      }catch(Exception ce){
	    	  IndoUtil.populateErrorMap(data, "Indo-2065","No Data Found.",0);
				log.info("Indo-2065- XMLServiceImpl.getRawXML()  ce- "+IndoUtil.getFullLog(ce));
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
					log.info("Indo-2066- XMLServiceImpl.getRawXML() closing streams "+e);
				}
	      }
		return data;
	}
}
