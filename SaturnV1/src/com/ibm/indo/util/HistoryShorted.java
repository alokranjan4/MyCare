package com.ibm.indo.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class HistoryShorted {

	private static Logger log = Logger.getLogger("saturnLoggerV1");
    List<Map<String,String>> resListMap=new ArrayList<Map<String,String>>();

	public List<Map<String, String>> sortHistoryMap(List<Map<String, String>> list2) {
		
		System.out.println("HistoryShorted.shortHistoryMap(-).......start");
		//System.out.println("Map without sort: " + list2);
		
		
		

		Map<String, String> mapList = new HashMap<String, String>();
		List<String> list=new ArrayList<String>();
		for (Map<String, String> map : list2) {
			String[] newStr = (map.get("AnswerTime")).toString().split(" ");
			//String usageType = map.get("UsageType");
			//System.out.println("usageType: " + usageType);
			mapList.put(newStr[0], map.get("UsageType"));
			//mapList.put((map.get(newStr[0])?null) , map.get("UsageType"));
			list.add(newStr[0]+" "+map.get("UsageType"));
		}// for

		System.out.println("CPS1: \n" + mapList + "\n" + list + "\n\n");
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (String temp : list) {
			Integer count = map.get(temp);
			map.put(temp, (count == null) ? 1 : count + 1);
		}// for

		//System.out.println("CPS2: \n" + map + "\n\n");
		
		Map<String, String> cpMap = new HashMap<String, String>();
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			List<String> cpList=new ArrayList<String>();
            int values = entry.getValue();
            String key = entry.getKey().toString().split(" ")[0];
            String usageType = entry.getKey().toString().split(" ")[1];
            int count = entry.getValue();
            
            try {
            	if(cpMap.get(key) != null) {
            		
            		cpMap.put(key, cpMap.get(key) + "," + usageType + "=" + values);
                } else {
                	cpMap.put(key, usageType + "=" + values);
                }
            } catch(NullPointerException e) {
            	cpMap.put(key, usageType + "=" + values);
            }
		}
		
		//System.out.println("CPS3: \n" + cpMap + "\n\n");
		
		for (Map.Entry<String, String> entry : cpMap.entrySet()) {
			String[] str1 = entry.getValue().split(",");

			Map<String, String> map1 = new HashMap<String, String>();
			map1.put("Usage_Date", entry.getKey());
			for(int i=0; i<str1.length; i++) {
				String[] str2 = str1[i].split("=");
				map1.put(str2[0], str2[1]);
			}
			
			resListMap.add(map1);
		}
		System.out.println("shortedMap: " + resListMap);
		System.out.println("HistoryShorted.shortHistoryMap(-).......start");

		return resListMap;
	}
	
   


}