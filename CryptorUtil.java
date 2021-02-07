package com.example.util;

import static org.hamcrest.CoreMatchers.containsString;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.springframework.stereotype.Component;

import oracle.net.aso.k;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
*@auther QIANG.CQ.ZHOU
*@VERSING 2020年5月4日上午11:01:04
*/
@Component
public class CryptorUtil {
	
	/** 
     * 从指定字符串生成密钥，密钥所需的字节数组长度为16位 不足时后面补0，超出16位只取前16位 
     * 
     * @param arrBTmp 
     *            构成该字符串的字节数组 
     * @return 生成的密钥 
     * @throws java.lang.Exception 
     */     
    private static byte[] getKey(byte[] arrBTmp) {  
        byte[] arrB = new byte[16];// 创建一个空的16位字节数组（默认值为0）  
        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {// 将原始字节数组转换为16位  
            arrB[i] = arrBTmp[i];  
        }  
        return arrB;  
    }
    
	/**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
	public static String byteArray2ToHexStr(byte[] key) {
		int length=key.length;
		StringBuffer sb=new StringBuffer(length*2);
		for(int i=0;i<key.length;i++) {
			String hex=Integer.toHexString(key[i] & 0xFF);
			if (hex.length()==1) {
				hex="0"+hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}
	
	 /**
	  * 將16進制轉換為二進制數組
	  * @param strIn
	  * @return
	  */
    public static byte[] hexStr2ToByteArray(String strIn) {  
        byte[] arrB = strIn.getBytes();  
        int iLen = arrB.length;  
        byte[] arrOut = new byte[iLen / 2];// 两个字符表示一个字节，所以字节数组长度是字符串长度除以2  
        for (int i = 0; i < iLen; i = i + 2) {  
            String strTmp = new String(arrB, i, 2);  
            arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);  
        }  
        return arrOut;  
    }
	
	/**
     * 生成密钥
     * 自动生成base64 编码后的AES128位密钥
     */
	public static String getAESKey() throws Exception{
		KeyGenerator kg=KeyGenerator.getInstance("AES");
		kg.init(128);
		SecretKey sk=kg.generateKey();
		byte[] b=sk.getEncoded();
		//返回16進制key
		return byteArray2ToHexStr(b);
	}
	
	/**
	 * AES加密
	 * @param data 待加密
	 * @param key 密鑰
	 * @return
	 * @throws Exception
	 */
	public static String enAes(String data, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key,"AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return byteArray2ToHexStr(encryptedBytes);
    }

	/**
	 * AES解密
	 * @param data 待解密
	 * @param key 密鑰
	 * @return
	 * @throws Exception
	 */
    public static String deAes(String data, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decValue = cipher.doFinal(hexStr2ToByteArray(data));
        return new String(decValue);
    }

	/**
	 * 返回16進制加密字符串
	 * @param str 需要加密的字符串
	 * @param hashType 加密類型
	 * @return
	 */
	public String getHash2MD5(String str) {
		StringBuilder sb = new StringBuilder();
		MessageDigest messageDigest;
		try {
			messageDigest=MessageDigest.getInstance("MD5");
			messageDigest.update(str.getBytes("UTF-8"));
			for (byte b : messageDigest.digest()) {
				sb.append(String.format("%02X", b));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 校驗密碼與MD5是否一致
	 * @param pwd
	 * @param md5
	 * @return
	 */
	public boolean checkPassword(String pwd,String md5) {
		return getHash2MD5(pwd).equalsIgnoreCase(md5);
	}
	
}
