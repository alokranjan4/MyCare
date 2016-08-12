/**
 * 
 */
package com.ibm.ijoin.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
	
	private static Logger log = Logger.getLogger("ijoinLogger");
	
	public static Map<String,String> getAttributes(List<String> attributes, String xml, String url){
		//log.debug("IndoXMLParseUtil.getAttributes() xml - "+xml);
		Map<String,String> data = new HashMap<String, String>();
		HttpEntity entity = null;
		HttpPost httppost = null;
		RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(15000) .setConnectTimeout(15000)
				.setConnectionRequestTimeout(15000).build();
		CloseableHttpClient  httpclient = null;
		try {
				httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
				httppost = new HttpPost(url);
		        StringEntity se = new StringEntity(xml, "UTF-8");
		        se.setContentType("text/xml");
		        httppost.setEntity(se);
		        HttpResponse httpresponse = httpclient.execute(httppost);
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
		            log.debug("IndoXMLParseUtil.getAttributes() errordata-"+content);
		        	data.put("Status", "FAILURE");
		        	data.put("Success", "false");
		        	data.put("StatusCode", String.valueOf(statusCode));
		        }
		} catch (MalformedURLException e) {
			log.error("IndoXMLParseUtil.getAttributes() e "+e);
			IndoUtil.populateErrorMap(data, "Saturn-400","No Data Found.");
		} catch (IOException e) {
			log.error("IndoXMLParseUtil.getAttributes() e1 "+e);
			IndoUtil.populateErrorMap(data, "Saturn-400","No Data Found.");
		}catch (Exception e) {
			log.error("IndoXMLParseUtil.getAttributes() e1 "+e);
			IndoUtil.populateErrorMap(data, "Saturn-400","No Data Found.");
		}finally{
			try {
				EntityUtils.consume(entity);
					if(null!=httppost){
					httppost.releaseConnection();
					}if(null!=httpclient){
					httpclient.close();
					}
				} catch (IOException e) {
				log.error("Saturn-2028- IndoXMLParseUtil.getAttributes() Closing connection "+e);
			}
			log.info("IndoXMLParseUtil.getAttributes() - END");
		}
		return data;
	}
	
	public static String generateXML(String xml, Map<String,String> keyVal){
		log.info("IndoXMLParseUtil.generateXML()");
		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><MobaMessage xmlns=\"com/icare/eai/schema/evMobaQRetailProfile\" "
				+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"com/icare/eai/schema/evMobaQRetailProfile "
				+ "evMobaQRetailProfile.xsd\"><MobaQRetailProfileReq><MSISDN></MSISDN></MobaQRetailProfileReq></MobaMessage>";
		Document xmlDoc = Jsoup.parse(xml, "", Parser.xmlParser());
		for (Entry<String, String> entry : keyVal.entrySet())
		{
			xmlDoc.select(entry.getKey()).first().text(entry.getValue());
		}
		//log.info("IndoXMLParseUtil.generateXML() xmlDoc "+xmlDoc.toString());
		return xmlDoc.toString();
	}
	
	public static List<Map<String,String>> getXMLParentChildData(String xml, String parent, Map<String,String> childs){
		//log.info("IndoXMLParseUtil.getXMLParentChildData() - START");
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		//xml="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Person><First-Name>1</First-Name><Last-Name>2</Last-Name></Person><Person><First-Name>3</First-Name><Last-Name>4</Last-Name></Person>";
		Document xmlDoc = Jsoup.parse(xml, "", Parser.xmlParser());
		Elements eles = xmlDoc.select(parent);
		for(Element ele: eles){
			Elements children = ele.children();
			Map<String,String> map = new HashMap<String,String>();
			for(Element child: children){
				if(childs.containsKey(child.tagName().trim())){
					map.put(childs.get(child.tagName()), child.text());
				}
			}
			list.add(map);
		}
		//log.info("IndoXMLParseUtil.getXMLParentChildData() - END");
	//	log.info("IndoXMLParseUtil.getXMLParentChildData()"+list);
		return list;
	}
	public static Map<String,String> getXMLData(String xml, Map<String,String> tags){
		//log.info("IndoXMLParseUtil.getXMLData() - START");
		//xml="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Person><First-Name>1</First-Name><Last-Name>2</Last-Name></Person><Person><First-Name>3</First-Name><Last-Name>4</Last-Name></Person>";
		Document xmlDoc = Jsoup.parse(xml, "", Parser.xmlParser());
		Map<String,String> map = new HashMap<String,String>();
		for (Entry<String, String> entry : tags.entrySet())
		{
			Element child = xmlDoc.select(entry.getKey()).first();
			map.put(entry.getValue(), child.text());
		}
		//log.info("IndoXMLParseUtil.getXMLData() - END");
	//	log.info("IndoXMLParseUtil.getXMLData()"+map);
		return map;
	}
	
	public static List<Map<String,String>> getXMLParentChildData(Document xmlDoc, String parent, Map<String,String> childs){
		//log.info("IndoXMLParseUtil.getXMLParentChildData() - START");
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Elements eles = xmlDoc.select(parent);
		for(Element ele: eles){
			Elements children = ele.children();
			Map<String,String> map = new HashMap<String,String>();
			for(Element child: children){
				if(childs.containsKey(child.tagName().trim())){
					map.put(childs.get(child.tagName()), child.text());
				}
			}
			list.add(map);
		}
		//log.info("IndoXMLParseUtil.getXMLParentChildData() - END");
	//	log.info("IndoXMLParseUtil.getXMLParentChildData()"+list);
		return list;
	}
	public static Map<String,String> getXMLData(Document xmlDoc, Map<String,String> tags){
		//log.info("IndoXMLParseUtil.getXMLData() - START");
		Map<String,String> map = new HashMap<String,String>();
		for (Entry<String, String> entry : tags.entrySet())
		{
			Element child = xmlDoc.select(entry.getKey()).first();
			map.put(entry.getValue(), child.text());
		}
		//log.info("IndoXMLParseUtil.getXMLData() - END");
	//	log.info("IndoXMLParseUtil.getXMLData()"+map);
		return map;
	}
	public static Map<String,Object> xmlToMapAll(String xml){
		//log.info("IndoXMLParseUtil.xmlToMapAll() - START");
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			Document xmlDoc = Jsoup.parse(xml, "", Parser.xmlParser());
			Elements eles =xmlDoc.getAllElements();
			for(Element ele: eles){
				Map<String,Object> mi = new HashMap<String,Object>();
				if(ele.children().size()>1){
					mi = getChilds(ele.children());
				}else{
					mi.put(ele.tagName(), ele.ownText());
				}
				list.add(mi);
				//map.putAll(mi);
			}
			
		    map.put("data", list);
		    map.put("Status", "SUCCESS");
		}catch(Exception ce){
			log.error("IndoXMLParseUtil.xmlToMapAll() ce "+IndoUtil.getFullLog(ce));
		}
		return map;
	}
	public static Map<String,Object> getChilds(Elements childs){
		//log.info("IndoXMLParseUtil.getChilds() - START");
		Map<String,Object> map = new HashMap<String,Object>();
		for(Element child: childs){
			if(child.children().size()>0){
				map = getChilds(child.children());
			}else{
				map.put(child.tagName(), child.ownText());
			}
		}
		//log.info("IndoXMLParseUtil.getChilds() - END");
		return map;
	}
	public static List<Map<String,String>> getParentChildXML(Elements eles){
		//log.info("IndoXMLParseUtil.getParentChildXML() - START");
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		try{
			for(Element ele : eles){
				Map<String,String> map = new HashMap<String,String>();
				for(Element el : ele.children()){
					map.put(el.tagName(), el.ownText());
				}
				list.add(map);
			}
		}catch(Exception ce){
			log.error("IndoXMLParseUtil.getParentChildXML() ce "+IndoUtil.getFullLog(ce));
		}
		return list;
	}
	public static Map<String,Object> getIdenticalChilds(Elements ele){
		//log.info("IndoXMLParseUtil.getIdenticalChilds() - START");
			Map<String,Object> map = new HashMap<String,Object>();
			List<String> li = new ArrayList<String>();
			if(ele.size()>1){
				for(Element el : ele){
					li.add(el.ownText());
				}
				map.put(ele.first().tagName(),li);
			}else if(ele.size()==1){
				map.put(ele.first().tagName(),ele.first().ownText());
			}
			//log.info("IndoXMLParseUtil.getIdenticalChilds() - END");
		return map;
	}
	public static Root testExtract(Elements eles){
		for(Element ele : eles){
			log.info(ele.tagName()+" size = "+ele.childNodes().size());
			
		}
		return null;
	}
	public static void main(String args[]){
		/*Map<String,String> map= new HashMap<String,String>();
		map.put("MSISDN", "1234567");
		generateXML("",map);
		*/
		/*Map<String,String> li = new HashMap<String,String>();
		li.put("first-name","FirstName");
		li.put("last-name","LastName");
		getXMLData("", li);*/
		String xml="<ExtMessage xmlns=\"com/icare/eai/schema/evExtQMainPkgQuotaResp\">   <ExtQMainPkgQuotaResp>     <ServiceNumber>6285770355730</ServiceNumber><Source><a>10</a><b>11</b><a>12</a></Source><Status>Success</Status><ErrorMessage/><InitialQuota>2621440</InitialQuota>     <UsedQuota>62859.49</UsedQuota>   </ExtQMainPkgQuotaResp> </ExtMessage> ";
		/*Map<String, Object>  ds =  xmlToMapAll(xml);
		log.info("IndoXMLParseUtil.main() "+ds);*/
		Document xmlDoc = Jsoup.parse(xml, "", Parser.xmlParser());
		Elements eles = xmlDoc.select("a");
		//log.info("************"+eles.toString());
		Map<String, Object> d  = getIdenticalChilds(eles);
		log.info(d);
	}
}
