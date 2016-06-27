/**
 * 
 */
package com.ibm.indo.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ibm.indo.util.DBUtil;
import com.ibm.indo.util.IndoUtil;
import com.ibm.indo.util.UploadUtil;

/**
 * @author Aadam
 *
 */
@Controller
@RequestMapping("/service")
public class ReportsController {
		@Autowired
		private DBUtil dbUtil;
 		private static Logger log = Logger.getLogger("saturnLogger");
        		
		@RequestMapping(value = "/dailyReport")
		public @ResponseBody void dailyReport(@RequestParam Map<String, String> params, HttpServletResponse response, HttpServletRequest req) {
			log.info("-----------------START Daily Reports------------------------");
			
	    	XSSFWorkbook workbook = new XSSFWorkbook();
	    	FileOutputStream stream  = null;
	    	FileWriter fileWritter = null;
	    	BufferedWriter bufferWritter = null;
	    	try{
	    	List<Map<String, Object>> qry = dbUtil.getData("Select qry,sheet from saturn_report order by SEQ_REPORT", new Object[]{});
	    	List<Map<String, Object>> summary = new ArrayList<Map<String, Object>>();
	    	Map<String,Object> stats = new HashMap<String,Object>();
			if(qry.size()>0){
				XSSFSheet sheet = null;
				for(int i=0;i<qry.size();i++){
					String q = qry.get(i).get("QRY").toString();
					String sheetName = qry.get(i).get("SHEET").toString();
					//log.info("ReportsController.dailyReport() query = "+q);
					log.info("Controller.dailyReport() - "+qry.get(i).get("SHEET"));
					List<Map<String, Object>> data = dbUtil.getData(q, new Object[]{});
					int sheetSize = data.size();
					Map<String,Object> counts = new HashMap<String,Object>();
					counts.put("Action",sheetName);counts.put("Total", sheetSize);
					stats.put(sheetName, sheetSize);
					summary.add(counts);
					log.info("============= Counts ====================="+summary);					 
					sheet = workbook.createSheet(sheetName);
					
					if(data.isEmpty() || data.get(0).isEmpty()){
						continue;
					}
					
					String[] xlCols = new String[data.get(0).keySet().size()];
					int j=0;
					for ( String key : data.get(0).keySet() ) {
						xlCols[j]=key;
						j++;
					}
										
					//String[] dbCols = new String[] { "ID", "NAME", "PWD"};
					sheet = UploadUtil.downloadMultipleSheetExcel(sheet, xlCols, xlCols, data);
					//log.info("Query - "+q);
				}
				sheet = workbook.createSheet("Summary");
				String[] xlCols = {"Action","Total"};
				sheet = UploadUtil.downloadMultipleSheetExcel(sheet, xlCols, xlCols, summary);
				workbook.setSheetOrder("Summary", 0);
				}
				log.info("Report Stored --------------- @ /app/saturnReports/");
				stream = new FileOutputStream("/app/saturnReports/New MyCare Transaction Data_"+IndoUtil.getPrevDate(new Date(), 1)+".xls");
				workbook.write(stream);
				stream.close();
				stats.put("date", IndoUtil.parseDate(new Date(), "dd-MM-yyyy"));
				String json = IndoUtil.convertToJSON(stats)+",\n";
				File file =new File("/app/saturnReports/stats.json");
				log.info("ReportsController.dailyReport() - JSON " +json);
	    		//if file doesnt exists, then create it
	    		if(!file.exists()){
	    			file.createNewFile();
	    		}
	    		log.info("ReportsController.dailyReport()- 0 - "+file.canRead());
	    		log.info("ReportsController.dailyReport()- 1 - "+file.canWrite());
	    		
	    		fileWritter = new FileWriter("/app/saturnReports/stats.json",true);
	    	        bufferWritter = new BufferedWriter(fileWritter);
	    	        bufferWritter.write(json);
	    	        bufferWritter.flush();
	    	        fileWritter.flush();
	    	        
	    	        log.info("ReportsController.dailyReport()- 2 - ");
	    	        
	    	        try{
	    				FileSystemResource resource = new FileSystemResource("/app/saturnReports/New MyCare Transaction Data_"+IndoUtil.getPrevDate(new Date(), 1)+".xls");
	    				byte[] pdf = IOUtils.toByteArray(resource.getInputStream());
	    				
	    				//String to = "alok.ranjan@in.ibm.com";
	    				String to="rudy.dalimunthe@indosatooredoo.com,alex.ginting@indosatooredoo.com,hence.steve@indosatooredoo.com,budi.irawan@indosatooredoo.com,dhoya.sugarda@indosatooredoo.com,dwiprabowo@indosatooredoo.com,d.firmansyah@indosatooredoo.com,himawan.adi@indosatooredoo.com,joko.sriyono@indosatooredoo.com,meigi.sigit@indosatooredoo.com,nusantara.widyandaru@indosatooredoo.com,tony.ariyanto@indosatooredoo.com,ranthony@id.ibm.com,reno.akbar@indosatooredoo.com,adityo.dwiarto@id.ibm.com,ravkonda@in.ibm.com,agustinus.widodo@indosatooredoo.com,dadang.fitriyana@indosatooredoo.com,ashfak@sg.ibm.com,srohit@id.ibm.com,cgdeshpa@in.ibm.com,isthambi@in.ibm.com,rkgoel@in.ibm.com,nur.rahmah@indosatooredoo.com,hardi.tanuwijaya@indosatooredoo.com,anto.hermawan@indosatooredoo.com,ranit.srivastava@in.ibm.com,alok.ranjan@in.ibm.com,moahmedn@in.ibm.com,ppathak5@in.ibm.com,sanborse@in.ibm.com";
	    				try {
	    					Properties props1 = new Properties();
	    					props1.put("mail.smtp.host", "smtpgw.indosatooredoo.com");
	    					props1.put("mail.smtp.port", "25");
	    				//	props1.put("mail.smtp.user", false);
	    				//	props1.put("mail.smtp.password", false);
	    					props1.put("mail.transport.protocol","smtp"); 
	    					Session session = Session.getDefaultInstance(props1);
	    					session.setDebug(true);
	    					MimeMessage msg = new MimeMessage(session);
	    					log.info("Msg is  || " + msg);
	    					InternetAddress addressFrom = new InternetAddress("noreply@indosatooredoo.com");
	    					log.debug("addressFrom is  || " + addressFrom);
	    					log.debug("Msg is *** || " + msg);
	    					addressFrom.setPersonal("INDOSAT");
	    					DataSource dataSource = new ByteArrayDataSource(pdf, "application/xls");
	    			        MimeBodyPart pdfBodyPart = new MimeBodyPart();
	    			        pdfBodyPart.setDataHandler(new DataHandler(dataSource));
	    			        pdfBodyPart.setFileName("New MyCare Transaction Data_"+IndoUtil.getPrevDate(new Date(), 1)+".xls");
	    			        MimeMultipart mimeMultipart = new MimeMultipart();
	    			        mimeMultipart.addBodyPart(pdfBodyPart);
	    					msg.setFrom(addressFrom);
	    					msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
	    					msg.setSubject("New MyCare Transaction Data for " +IndoUtil.getPrevDate(new Date(), 1));
	    					msg.setContent(mimeMultipart);
	    					Transport.send(msg);
	    				} catch (Exception e) {
	    					log.error("Exception Occured  In EmailManager ||" +IndoUtil.getFullLog(e));
	    				}
	    			}catch(Exception ce){
	    				log.info("Indo-255- IndoServiceController.getBalance() ce "+IndoUtil.getFullLog(ce));
	    			}
	    	}catch(Exception ce){
	    		log.debug("GenericServiceImpl.start() -Exception "+IndoUtil.getFullLog(ce));
	    	}finally{
	    		if(null!=stream){try {stream.close();}catch (IOException e) {}}
	    		if(null!=bufferWritter){try {bufferWritter.close();}catch (IOException e) {}}
	    		if(null!=fileWritter){try {fileWritter.close();}catch (IOException e) {}}
	    	}
			log.info("-----------------END Daily Reports------------------------");
		}
	}