/**
 * 
 */
package com.ibm.services.test;

import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Hex;

/**
 * @author Adeeb
 *
 */
public class AESToy3 {
	public static void main(String args[]){
		try {
		byte[] iv = Hex.decodeHex("b3f155789090bee8977596333a4666f5".toCharArray());
		byte[] salt = Hex.decodeHex("777d4630e264972b91a1f7165707ba5d03aaf343fef20656cde21d90a33d26e5".toCharArray());
		
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec("secret_key".toCharArray(), salt, 10);
			byte[] key = factory.generateSecret(spec).getEncoded();
			byte[] ciphertext = DatatypeConverter.parseBase64Binary("+lJhH/F+b6DyYS2zUU6+WnS1Vc/nqX6xt53iI6JKFtU=");
			
			ScriptEngineManager f = new ScriptEngineManager();
		    ScriptEngine engine = f.getEngineByName("<script src='http://crypto-js.googlecode.com/svn/tags/3.1.2/build/rollups/pbkdf2.js'></script>");
		    engine.eval("print('Hello, World')");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}