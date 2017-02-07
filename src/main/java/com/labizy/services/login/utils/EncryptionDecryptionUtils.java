package com.labizy.services.login.utils;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

//import org.springframework.security.crypto.codec.*;

public class EncryptionDecryptionUtils {
	//Purposefully, I didn't add any logger in any method of this ultra-sensitive class.. :-)
	
	public String encodeToBase64String(byte[] str){
		byte[] encodedBytes = Base64.encodeBase64(str);
		String encodedString = StringUtils.newStringUtf8(encodedBytes);
		
		return encodedString;
	}
	
	public String encodeToBase64String(String str){
		byte[] encodedBytes = Base64.encodeBase64(str.getBytes());
		String encodedString = StringUtils.newStringUtf8(encodedBytes);
		
		return encodedString;
	} 

	public String decodeToBase64String(String str){
		byte[] decodedBytes = Base64.decodeBase64(str.getBytes());
		String decodedString = StringUtils.newStringUtf8(decodedBytes);
		
		return decodedString;
	} 

	public String hashToBase64String(String plainString){
		String result = null;
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(plainString.getBytes(StandardCharsets.UTF_8));
			
			result = encodeToBase64String(hash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	public boolean matchWithHashedBase64String(String plainString, String hashedBase64String){
		boolean result = false;
		
		String hashedToBase64String = hashToBase64String(plainString);
		
		return (hashedToBase64String.equals(hashedBase64String));
	}
	
	public static void main(String[] args){
		String password = "Hell0L@bizy";
		EncryptionDecryptionUtils encryptionDecryptionUtils = new EncryptionDecryptionUtils();
		
		String encoded = encryptionDecryptionUtils.encodeToBase64String(password);
		System.out.println("Encoded (This is what you get from the UI) : " + encoded);
		
		String decoded = encryptionDecryptionUtils.decodeToBase64String(encoded);
		System.out.println("Decoded : " + decoded);
		
		if(password.equals(decoded)){
			System.out.println("Success..!");
		}else{
			System.out.println("Failure..!!!!");
		}
		
		///////////////////////////////////////////////////////////////////////////////
		
		String hashedKey = encryptionDecryptionUtils.hashToBase64String(encryptionDecryptionUtils.decodeToBase64String(encoded));
		System.out.println("Hashed Key : " + hashedKey); 
		
		boolean passwordsMatch = encryptionDecryptionUtils.matchWithHashedBase64String(encryptionDecryptionUtils.decodeToBase64String(encoded), hashedKey);

		if(passwordsMatch){
			System.out.println("Passwords Match..!");
		}else{
			System.out.println("Passwords Don't Match..!!!!");
		}
	}
}
