package com.ibm.indo.serviceImpl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ibm.indo.util.IndoUtil;
public class OrderSatusJob implements Job 
{
	private static Logger		log		= Logger.getLogger("indoLogger");
	public void execute(JobExecutionContext context)	throws JobExecutionException {
		log.info("OrderSatusJob.execute() - start- ");
				try {
					/*Jsoup.connect("http://localhost:8080/IndoSelfServices/service/mulOrderStatus")
					.header("Content-Type", "text/html")
				    .timeout(5000)
				    .userAgent("Mozilla")
				    .post();*/
					PostURL("https://mobileagent.indosatooredoo.com/IndoSelfServices/service/mulOrderStatus");
					//Jsoup.connect("http://localhost:8080/IndoSelfServices/service/mulCataOrderStatus").header("Content-Type", "application/x-www-form-urlencoded").header("Accept", "application/json").ignoreContentType(true).timeout(10000).post();
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
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

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
