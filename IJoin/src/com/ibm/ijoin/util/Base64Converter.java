package com.ibm.ijoin.util;

import org.apache.commons.codec.binary.Base64;

public class Base64Converter {
	public  String encodeImage(byte[] imageByteArray) {
		byte[] valueDecoded= Base64.encodeBase64(imageByteArray );
		return new String(valueDecoded);
        
    }
 
    public  byte[] decodeImage(String imageDataString) {
    	byte[]   bytesEncoded = Base64.decodeBase64(imageDataString .getBytes());
        return bytesEncoded;
    }

}
