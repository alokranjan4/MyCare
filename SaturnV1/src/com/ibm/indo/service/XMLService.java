/**
 * 
 */
package com.ibm.indo.service;

import java.util.List;
import java.util.Map;

/**
 * @author Adeeb
 *
 */
public interface XMLService {
	public Map<String,String> getAttributes(List<String> attributes, String xml, String url);
	 Map<String,Object> getAttributes(List<String> attributes, String xml, String url,List<String> attr);
	 Map<String,Object> getRawXML(String xml, String url);
}
