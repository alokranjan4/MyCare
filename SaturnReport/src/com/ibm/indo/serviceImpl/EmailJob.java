package com.ibm.indo.serviceImpl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ibm.indo.util.IndoUtil;
public class EmailJob implements Job 
{
	private static Logger		log		= Logger.getLogger("saturnLogger");
	
	public void execute(JobExecutionContext context)	throws JobExecutionException {
		log.info("OrderSatusJob.execute() - start- ");
				try {
					
					//PostURL("https://mobileagent.indosatooredoo.com/IndoReports/service/mulOrderStatus");
					PostURL("http://10.128.168.2:8080/SaturnReport/service/sentEmail");
					log.info("Service get called === http://10.128.168.2:8080/SaturnReport/service/sentEmail");
				} catch (Exception e) {
					log.error(IndoUtil.getFullLog(e));
				}
				
		log.info("OrderSatusJob.execute() - end- ");		
	}
	
	public void PostURL(String url) {
		BufferedReader in = null;
		OutputStreamWriter w = null;
        try {
            String rawData = "RAW_DATA_HERE";
          //  String url = "URL_HERE";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("POST"); //e.g POST
            con.setRequestProperty("Accept", "text/html"); //e.g key = Accept, value = application/json

            con.setDoOutput(true);
            w = new OutputStreamWriter(con.getOutputStream(), "UTF-8");

            w.write(rawData);
            w.close();

            int responseCode = con.getResponseCode();

            in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();
            //Use Jsoup on response to parse it if it makes your work easier.
        } catch(Exception e) {
            e.printStackTrace();
        }finally{
        	try{
        		if(null!=in){in.close();}if(null!=w){w.close();}
        	}catch(Exception ce){}
        }
    }
}
