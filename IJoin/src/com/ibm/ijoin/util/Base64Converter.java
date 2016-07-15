package com.ibm.ijoin.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;

public class Base64Converter {
	
	private static Logger log = Logger.getLogger("im2");

	public  static String  encodeImage(byte[] imageByteArray) {
		 log.info("BannerEn  : 2");
		byte[] valueEncoded= Base64.encodeBase64(imageByteArray );
		log.info("BannerEn : "+valueEncoded);
		
		    return new String(valueEncoded);
        
    }
 
    public static  byte[] decodeImage(String imageDataString) {

        BufferedImage bufImg = null;   	
    	BASE64Decoder decoder = new BASE64Decoder();
        byte[] imgBytes = null;
		try {
			imgBytes = decoder.decodeBuffer(imageDataString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
 
	try {
		bufImg = ImageIO.read(new ByteArrayInputStream(imgBytes));
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
       File imgOutFile = new File("newLabel.png");
    try {
		ImageIO.write(bufImg, "png", imgOutFile);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    	
    	
    	byte[]   bytesDecoded = Base64.decodeBase64(imageDataString .getBytes());
        
    	return bytesDecoded;
    }

    
    
    
    
}
