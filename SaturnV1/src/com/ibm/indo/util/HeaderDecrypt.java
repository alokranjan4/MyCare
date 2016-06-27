/**
 * 
 */
package com.ibm.indo.util;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.axis.encoding.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

/**
 * @author Adeeb
 *
 */
public class HeaderDecrypt {
	private static Logger log = Logger.getLogger("saturnLoggerV1");
	public static String decryptMsisdnHeader(String msisdnRaw){
	 	log.info("HeaderDecrypt.decryptMsisdnHeader() START ");
	 	log.info("HeaderDecrypt.decryptMsisdnHeader() ");
        int[] c = new int[256];
        for(int i=0;i<256;i++){
            c[i]=i;
        }
        byte[] a = DigestUtils.md5("indosat");
        int d=0;
        for(int i=0;i<256;i++){
            d=(d + c[i] + (int) (a[i%16] & 0xFF) ) % 256;
            c[i]=c[i] ^ c[d];
            c[d]=c[i] ^ c[d];
            c[i]=c[i] ^ c[d];
        }
        byte[] b = Base64.decode(msisdnRaw);
        // need to make sure that we're processing byte (8bit) instead of char (16bit),
        // we switch mechanism to new String(byte, encoding)
        byte[] r = new byte[b.length];
        int i;
        i = 0;
        d = 0;
        for(int y=0;y<b.length;y++){
            i=(i+1) % 256;
            d=(d+c[i]) % 256;

            c[i]=c[i] ^ c[d];
            c[d]=c[i] ^ c[d];
            c[i]=c[i] ^ c[d];
            r[y] =(byte) ((byte) c[(c[i]+c[d]) % 256] ^ b[y]);
        }
		try {
			String msisdn = new String(r, "US-ASCII");
			if (isNumeric(msisdn)) {
	            return msisdn;
	        }
		} catch (UnsupportedEncodingException e) {
			log.error("HeaderDecrypt.decryptMsisdnHeader() e "+IndoUtil.getFullLog(e));
		}
        return decryptMsisdnHeaderByWap(msisdnRaw);
    }

    // FIXME: change to method that supports timeout
    public static String decryptMsisdnHeaderByWap(String msisdnRaw) {
    	try {
        String url = "https://m.mycare.indosatooredoo.com/rc4.php";
        InputStream result;
        url += "?m=" + URLEncoder.encode(msisdnRaw, "UTF-8");
        String msisdn = "";
        
            URL myUrl = new URL(url.toString());
            msisdn = myUrl.toString();
            log.info("HeaderDecrypt.decryptMsisdnHeaderByWap() msisdn "+msisdn);
            return msisdn;
        }catch (Exception e) {
           log.error("HeaderDecrypt.decryptMsisdnHeaderByWap() e "+IndoUtil.getFullLog(e));
            return "";
        }
    }
    public static boolean isNumeric(String str)
    {
        return str.matches("[+-]?\\d*(\\.\\d+)?");
    }
}
