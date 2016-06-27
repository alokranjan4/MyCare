package com.ibm.indo.service;

import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.ibm.services.vo.InvoiceListVO;

public interface SprintTwoService {
//	InvoiceListVO retrieveInvoice(String baNumber);
	InvoiceListVO getCorpInvoices(String msisdn);
	Map<String, Object> contactUsMenu(JsonObject jObj);
	Map<String, Object> addFavourite(String msisdn, String Transaction_type, String tx1, String txt2, String txt3,String txt4, String txt5, String display, String CustType);
	Map<String, Object> retriveFavourite(String msisdn);
	Map<String, Object> arrangeFavourite(List<Object[]> listObj);
	Map<String, Object> deleteFavourite(List<Object[]> listObj);
	Map<String,Object> retrieveStore();
	Map<String, Object> logPayment(String msisdn, String transactionType, String amount, String transactionData1,
			String transactionData2, String transactionData3, String transactionData4, String transactionData5,
			String custType);
	
}
