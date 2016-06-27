/**
 * 
 */
package com.ibm.ijoin.util;

/**
 * @author Adeeb
 *
 */
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
public class HeaderCoder{
    public HeaderCoder(){}
    public static String encodeBase64(String str){
        String res = null;
        BASE64Encoder enc = new BASE64Encoder();
        res = enc.encode(str.getBytes());
        enc = null;
        return res;
    }
    public static String decodeBase64(String str) throws Exception{
        String res = null;
        String strBuf = URLDecoder.decode(str.trim(), "UTF-8");
        BASE64Decoder dec = new BASE64Decoder();
        res = new String(dec.decodeBuffer(strBuf));
        dec = null;
        return res;
    }
    public static String decodeBase64(String str, String charset) throws Exception{
        String res = null;
        String strBuf = str;
        BASE64Decoder dec = new BASE64Decoder();
        res = new String(dec.decodeBuffer(strBuf), charset);
        dec = null;
        return res;
    }
    public static String encryptSasnHttpHeader(String key, String data) throws GeneralSecurityException, UnsupportedEncodingException{
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte hash[] = digest.digest(key.getBytes());
        Cipher rc4 = Cipher.getInstance("RC4");
        rc4.init(1, new SecretKeySpec(hash, "RC4"));
        byte rc4Out[] = rc4.doFinal(data.getBytes());
        String encodedData = encodeBase64(new String(rc4Out));
        return encodedData;
    }
    public static String decryptSasnHttpHeader(String key, String encodedData) throws GeneralSecurityException, Exception{
        Charset charset = Charset.forName("ISO-8859-1");
        byte encryptedData[] = decodeBase64(encodedData, "ISO-8859-1").getBytes(charset);
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte hash[] = digest.digest(key.getBytes(charset));
        Cipher rc4 = Cipher.getInstance("RC4");
        rc4.init(2, new SecretKeySpec(hash, "RC4"));
        byte output[] = rc4.doFinal(encryptedData);
        return new String(output, "ISO-8859-1");
    }
    public static void main(String args[]){
        try{
            System.out.println((new OutputStreamWriter(new ByteArrayOutputStream())).getEncoding());
            System.out.println((new StringBuilder("Charset = ")).append(Charset.defaultCharset().toString()).toString());
            System.out.println((new StringBuilder("gBUGFKdjW5qg7Q4= : ")).append(decryptSasnHttpHeader("coba", "gBUGFKdjW5qg7Q4=")).toString());
            System.out.println((new StringBuilder("62816101005 : ")).append(encryptSasnHttpHeader("coba", "62816101005")).toString());
            System.out.println((new StringBuilder("gBUGFKdjW5qg7Q4= : ")).append(decryptSasnHttpHeader("coba", "gBUGFKdjW5qg7Q4=")).toString());
            System.out.println((new StringBuilder("6285770355730 : ")).append(encryptSasnHttpHeader("indosat", "6285770355730")).toString());
        }catch(Exception exception) { }
    }
}

