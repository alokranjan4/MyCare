package com.ibm.ijoin.util;

import java.security.spec.KeySpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.net.ssl.*;

public class TripleDES {

    private static final String UNICODE_FORMAT = "UTF8";
    public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    private KeySpec ks;
    private SecretKeyFactory skf;
    private Cipher cipher;
    byte[] arrayBytes;
    private String myEncryptionKey;
    private String myEncryptionScheme;

    public TripleDES() throws Exception {
      myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;
      cipher = Cipher.getInstance(myEncryptionScheme);
    }

    private SecretKey initKey(String myEncryptionKey) throws Exception {
      arrayBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
      ks = new DESedeKeySpec(arrayBytes);
      skf = SecretKeyFactory.getInstance(myEncryptionScheme);
      SecretKey key = skf.generateSecret(ks);
      return key;
    }


    public String encrypt(String unencryptedString, String myEncryptionKey) {
      String encryptedString = null;
      try {
        SecretKey key = initKey(myEncryptionKey); 
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
        byte[] encryptedText = cipher.doFinal(plainText);
        encryptedString = new String(Base64.encodeBase64(encryptedText));
      } catch (Exception e) {
        e.printStackTrace();
      }
      return encryptedString;
    }


    public String decrypt(String encryptedString, String myEncryptionKey) {
      String decryptedText=null;
      try {
        SecretKey key = initKey(myEncryptionKey); 
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] encryptedText = Base64.decodeBase64(encryptedString.getBytes());
        byte[] plainText = cipher.doFinal(encryptedText);
        decryptedText= new String(plainText);
      } catch (Exception e) {
          e.printStackTrace();
      }
      return decryptedText;
    }


    public static void main(String args []) throws Exception
    {
      TripleDES td= new TripleDES();
      
      DateFormat timeFormat = new SimpleDateFormat("HHmmss");
      
      String dte=timeFormat.format(new Date());
      StringBuilder domPin=new StringBuilder("1nd054t2ois");
      String initiator="4gmobileagent";
      
      String sigA=dte+domPin;
      System.out.println("SigA "+ sigA);
      String sigB=domPin.reverse()+"|"+initiator;
      System.out.println("SigB "+ sigB);
      String sigC=sigA+"|"+sigB;
      System.out.println("SigC "+ sigC);
     

      String myEncryptionKey = "4yL8GJqTH5EiX0PPC0eT1lRZ";
      String encrypted=td.encrypt(sigC, myEncryptionKey);
      String decrypted=td.decrypt(encrypted, myEncryptionKey);

      System.out.println("String To Encrypt: "+ sigC);
      System.out.println("Encrypted String:" + encrypted);
      System.out.println("Decrypted String:" + decrypted);

      String httpsURL = "https://mapi.dompetku.com/webapi/user_inquiry";
      String query = "userid=4gmobileagent";
      query += "&signature=" + encrypted ;
      query += "&agentid=ibmtest@indosat.com" ;
      query += "&to=4gmobileagent&locationid=0";

      TrustManager[] trustAllCerts = new TrustManager[]{
          new X509TrustManager() {
              public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                  return null;
              }
              public void checkClientTrusted(
                  java.security.cert.X509Certificate[] certs, String authType) {
              }
              public void checkServerTrusted(
                  java.security.cert.X509Certificate[] certs, String authType) {
              }
          }
      };

      // Install the all-trusting trust manager   

      SSLContext sc = SSLContext.getInstance("SSL");   

      sc.init(null, trustAllCerts, new java.security.SecureRandom());   

      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());   

         

      // Create all-trusting host name verifier   

      HostnameVerifier allHostsValid = new HostnameVerifier() {   

          public boolean verify(String hostname, SSLSession session) {   

              return true;   

          }   

      }; 
      
      // Install the all-trusting host verifier   

      HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid); 
      

      URL myurl = new URL(httpsURL);
      HttpsURLConnection con = (HttpsURLConnection)myurl.openConnection();
      con.setRequestMethod("POST");
      con.setRequestProperty("Content-length", String.valueOf(query.length())); 
      con.setRequestProperty("Content-Type","application/x-www-form-urlencoded"); 
      con.setDoOutput(true); 
      con.setDoInput(true); 

      DataOutputStream output = new DataOutputStream(con.getOutputStream());  

      output.writeBytes(query);
      output.close();
      StringBuffer inputLine = new StringBuffer();
      DataInputStream input = new DataInputStream( con.getInputStream() ); 

      for( int c = input.read(); c != -1; c = input.read() ){
    	  System.out.print( (char)c ); 
    	  inputLine.append((char)c);
      }
     
      input.close(); 
      
      System.out.println("");
      System.out.println("Result is "+inputLine);
      System.out.println("Resp Code:"+con .getResponseCode()); 
      System.out.println("Resp Message:"+ con .getResponseMessage()); 
      
      JSONObject jsonObj = new JSONObject(inputLine.toString());
      System.out.println(jsonObj.get("trxid"));
    }

}

