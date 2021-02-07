package com.example.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @auther QIANG.CQ.ZHOU
 * @VERSING 2020年4月20日下午1:52:46
 */
public class StringUtil {

	public static boolean isEmpty(String value) {
		return value == null || value.length() == 0;
	}
	public static boolean isNotEmpty(String str){
		return !isEmpty(str);
	}
	
	public static String StringFilter(String str){
		String regEx="[`~!@#$%^&*()+=|{':;',\\[\\].<>/?~！@#￥%……&*（）——+|{【】‘；：”“’。，、？[\u4e00-\u9fa5]]"; 
		Pattern p = Pattern.compile(regEx); 
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
}
