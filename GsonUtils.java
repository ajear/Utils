package com.example.util;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @auther QIANG.CQ.ZHOU
 * @VERSING 2019�~9��26��U��4:02:51
 */
public class GsonUtils {

	private static final Gson gson = new Gson();
	
	private GsonUtils() {

	}

	public static <T> T getBeanFromJson(String json, Class<T> clas ) {
		return gson.fromJson(json, clas);

	}

	public static String getJsonStringFromObject(Object object) {
        return object == null ? null : gson.toJson(object);
    }
	
	public static <T> List<T> listFromJson(String json) {
		return gson.fromJson(json, new TypeToken<List<T>>(){}.getType());
	}

	public static <T> T getBeanFromJson(String json,Type t){
		return gson.fromJson(json, t);
	}
}
