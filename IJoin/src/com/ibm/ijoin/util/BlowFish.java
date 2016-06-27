package com.ibm.ijoin.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class BlowFish {

    public static void main(String[] args) throws Exception {
        System.out.println(encrypt("isat1234"));
        System.out.println(decrypt("c7AauLMZcEA="));
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
    	try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(hashString.getBytes("UTF-8"));
			byte[] digest = md.digest();
			String hashedValue= String.format("%064x", new java.math.BigInteger(1, digest));
			return hashedValue;
		} catch (UnsupportedEncodingException e) {
			//log.error("Exception occured while hasing the password "+e);
		} 
 catch (NoSuchAlgorithmException e) {
	// log.error("Exception occured while hasing the password "+e);
		}
    	return null;
    }
}