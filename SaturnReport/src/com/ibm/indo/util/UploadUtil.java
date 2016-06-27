package com.ibm.indo.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class UploadUtil {
	private static Logger log = Logger.getLogger("saturnLogger");
	
	
	public static XSSFWorkbook downloadExcelFile(List<Map<String, Object>> data) {
		log.info("UploadUtil.downloadExcelFile() - START");
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("SCM Data");
		int rownum = 0;
		Row r = sheet.createRow(rownum++);
		int cellnum = 0;
		Set<String> keySet = null;
		for (Map<String, Object> map : data) {
			keySet = map.keySet();
		}
		for (String key : keySet) {
			Cell cell = r.createCell(cellnum++);
			cell.setCellValue(key);
		}
		for (Map<String, Object> map : data) {
			keySet = map.keySet();
			Row row = sheet.createRow(rownum++);
			cellnum = 0;
			for (String key : keySet) {
				Cell cell = row.createCell(cellnum++);
				if (map.get(key) != null && !map.get(key).toString().isEmpty()) {
					cell.setCellValue(map.get(key).toString());
				} else {
					cell.setCellValue("null");
				}
			}

		}
		log.info("UploadUtil.downloadExcelFile() - END");
		return workbook;
	}

	
	public static File downloadCSVFile(String[] dbCols, String[] xlCols,List<Map<String, Object>> data) throws IOException {
		log.info("UploadUtil.downloadCSVFile() - START");
		FileWriter writer = null;
		String dbValue="";
		File file=new File("FileName");
		writer=new FileWriter(file);
		for(String col : xlCols){
			writer.append(col);
			writer.append(",");
		}
		writer.append("\n");
		for(Map<?, ?> m: data){
        	for(String st: dbCols){
        		if (m.get(st) != null && !m.get(st).toString().isEmpty()) {
        			dbValue=m.get(st).toString().replaceAll(",", " ");
	        		writer.append(dbValue);
	        		writer.append(",");
        		}else{
        			writer.append("");
        			writer.append(",");
        		}	
        	}
        	writer.append("\n");
        }
		writer.flush();
		writer.close();
		log.info("UploadUtil.downloadCSVFile() - END");
		return file;
	}

	public static XSSFSheet downloadMultipleSheetExcel(XSSFSheet sheet,String[] dbCols, String[] xlCols,List<Map<String, Object>> data) {
		log.info("UploadUtil.downloadExcelFile() - START");
		//XSSFWorkbook workbook = new XSSFWorkbook();
		//XSSFSheet sheet = workbook.createSheet("SCM Data");
		int rownum = 0;
		Row r = sheet.createRow(rownum++);
		int cellnum = 0;
		for(String col : xlCols){
			Cell cell = r.createCell(cellnum++);
			cell.setCellValue(col);
		}		
		for(Map<?, ?> m: data){
			if(m.containsKey("Action")){
				log.info("UploadUtil.downloadMultipleSheetExcel()*******************"+m);
			}
				Row row = sheet.createRow(rownum++);
				cellnum = 0;
	        	for(String st: dbCols){
	        		Cell cell = row.createCell(cellnum++);
	        		if (m.get(st) != null && !m.get(st).toString().isEmpty()) {	        			
	        			cell.setCellValue(m.get(st).toString());
	        		}else{
	        			cell.setCellValue("");
	        		}	
	        	}
	   
	    }
		log.info("UploadUtil.downloadExcelFile() - END");
		return sheet;
	}
	
	public static XSSFWorkbook downloadExcelFileColName(String[] dbCols, String[] xlCols,List<Map<String, Object>> data) {
		log.info("UploadUtil.downloadExcelFile() - START");
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("SCM Data");
		int rownum = 0;
		Row r = sheet.createRow(rownum++);
		int cellnum = 0;
		for(String col : xlCols){
			Cell cell = r.createCell(cellnum++);
			cell.setCellValue(col);
		}		
		for(Map<?, ?> m: data){
				Row row = sheet.createRow(rownum++);
				cellnum = 0;
	        	for(String st: dbCols){
	        		Cell cell = row.createCell(cellnum++);
	        		if (m.get(st) != null && !m.get(st).toString().isEmpty()) {	        			
	        			cell.setCellValue(m.get(st).toString());
	        		}else{
	        			cell.setCellValue("");
	        		}	
	        	}
	   
	    }
		return workbook;
	}
}
