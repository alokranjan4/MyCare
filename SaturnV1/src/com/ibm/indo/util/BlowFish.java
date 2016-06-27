package com.ibm.indo.util;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class BlowFish {

    public static void main(String[] args) throws Exception {
        System.out.println(encrypt("isat1234"));
        System.out.println(decrypt("eu/VV5T30Nc="));
        System.out.println(BlowFish.hashed(BlowFish.decrypt("mth/iUgbr+6tRUsNDXUH/A==")));
    }

    public static String encrypt(String username) throws Exception {
        byte[] keyData = ("p4ssw0rdw55").getBytes();
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyData, "Blowfish");
        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] hasil = cipher.doFinal(username.getBytes());
        return new BASE64Encoder().encode(hasil);
    }
    
    public static String decrypt(String string) throws Exception {
        byte[] keyData = ("p4ssw0rdw55").getBytes();
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyData, "Blowfish");
        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] hasil = cipher.doFinal(new BASE64Decoder().decodeBuffer(string));
        return new String(hasil);
    }
    
    public static String hashed(String hashString) throws Exception{
    	try{
	        MessageDigest digest = MessageDigest.getInstance("SHA-512");
	        byte[] hash = digest.digest(hashString.getBytes("UTF-8"));
	        StringBuffer hexString = new StringBuffer();

	        for (int i = 0; i < hash.length; i++) {
	            String hex = Integer.toHexString(0xff & hash[i]);
	            if(hex.length() == 1) hexString.append('0');
	            hexString.append(hex);
	        }
	        return hexString.toString();
	    } catch(Exception ex){
	       throw new RuntimeException(ex);
	    }  
    }
}