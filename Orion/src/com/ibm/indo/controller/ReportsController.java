/**
 * 
 */
package com.ibm.indo.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ibm.indo.util.DBUtil;
import com.ibm.indo.util.IndoUtil;

/**
 * @author Aadam
 *
 */
@Controller
@RequestMapping("/service")
public class ReportsController {
	@Autowired
	private DBUtil dbUtil;
	// private static Logger log = Logger.getLogger("dbScheduler");

	@RequestMapping(value = "/dbReport")
	public @ResponseBody void dailyReport(@RequestParam Map<String, String> params, HttpServletResponse response,
			HttpServletRequest req) {
		System.out.println("-----------------Start Daily Reports------------------------");
		FileOutputStream stream = null;
		FileWriter fileWritter = null;
		BufferedWriter bufferWritter = null;
		String fName =  IndoUtil.getPrevMins(new Date(),2);
		try{
			List<Map<String, Object>> msisdnlist = dbUtil.getData("select msisdn from SATURN_ACTIVITY where created_on >= sysdate - (02/1440)", new Object[] {});
			System.out.println(msisdnlist);
		//	if (null != msisdnlist && msisdnlist.size() > 0) {
				StringBuilder builder = new StringBuilder();
				for (Map<String, Object> m : msisdnlist) {
					if (null != m.get("MSISDN")) {
						builder.append(m.get("MSISDN").toString()).append("\n");
					}
				}
				File f = new File("/tmp/mycare_"+fName);
			//	System.out.println("ReportsController.dailyReport() f "+f);
				if(!f.exists()){
					f.createNewFile();
				}
				fileWritter = new FileWriter(f, true);
				bufferWritter = new BufferedWriter(fileWritter);
				bufferWritter.write(builder.toString());
				bufferWritter.flush();
				fileWritter.flush();
		//	}
		}catch(IOException|NullPointerException ioe){
			System.out.println("ReportsController.dailyReport() e "+ioe);
		}finally{
    		if(null!=stream){try {stream.close();}catch (IOException e) {}}
    		if(null!=bufferWritter){try {bufferWritter.close();}catch (IOException e) {}}
    		if(null!=fileWritter){try {fileWritter.close();}catch (IOException e) {}}
    	}
		System.out.println("-----------------END Daily Reports------------------------");
	}
}