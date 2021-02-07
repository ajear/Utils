package com.example.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletInputStream;

import org.springframework.core.io.support.PropertiesLoaderUtils;


/**
*@auther QIANG.CQ.ZHOU
*@VERSING 2020年8月13日上午8:24:27
*/
public class IOUtil {
	/**
	 * 保存文件流為本地文件
	 * @param path 保存地址
	 * @param file 
	 */
	public void saveFileToThis(String path,InputStream inputStream ) {
		FileOutputStream downloadFile;
		int index;
		byte[] bytes = new byte[1024];
		try {
			downloadFile = new FileOutputStream(path);
			while ((index = inputStream.read(bytes)) != -1) {
				downloadFile.write(bytes, 0, index);
				downloadFile.flush();
			}
			inputStream.close();
			downloadFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 讀取請求流（只有Content-Type=Application/json 才可以解析）
	 * @param in servlet請求流
	 * @param encodeing 格式（UTF-8）
	 * @return
	 * @throws IOException
	 */
	public static String readLineString(ServletInputStream in,String encodeing) throws IOException {
		  byte[] buf = new byte[8 * 1024];
		  StringBuffer sbuf = new StringBuffer();
		  int result;
		  do {
		   result = in.readLine(buf, 0, buf.length); // does +=
		   if (result != -1) {
		    sbuf.append(new String(buf, 0, result, encodeing));
		   }
		  } while (result !=-1); // loop only if the buffer was filled

		  if (sbuf.length() == 0) {
		   return null; // nothing read, must be at the end of stream
		  }
		  return sbuf.toString();
	}
	
	
	/**
     * 
     * @Title: printAllProperty   
     * @Description: 输出所有配置信息  
     * @param props
     * @return void  
     * @throws
     */
    private static void printAllProperty(Properties props)  
    {  
        @SuppressWarnings("rawtypes")  
        Enumeration en = props.propertyNames();  
        while (en.hasMoreElements())  
        {  
            String key = (String) en.nextElement();  
            String value = props.getProperty(key);  
            System.out.println(key + " : " + value);  
        }  
    }

	 /**
     * 根据key读取value
     * 
     * @Title: getProperties_1   
     * @Description: 第一种方式：根据文件名使用spring中的工具类进行解析  
     *                  filePath是相对路劲，文件需在classpath目录下
     *                   比如：config.properties在包com.test.config下， 
     *                路径就是com/test/config/config.properties    
     *          
     * @param filePath 
     * @param keyWord      
     * @return
     * @return String  
     * @throws
     */
    public static String getProperties_1(String filePath, String keyWord){
        Properties prop = null;
        String value = null;
        try {
            // 通过Spring中的PropertiesLoaderUtils工具类进行获取
            prop = PropertiesLoaderUtils.loadAllProperties(filePath);
            // 根据关键字查询相应的值
            value = prop.getProperty(keyWord);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }
    
    /**
     * 读取配置文件所有信息
     * 
     * @Title: getProperties_1   
     * @Description: 第一种方式：根据文件名使用Spring中的工具类进行解析  
     *                  filePath是相对路劲，文件需在classpath目录下
     *                   比如：config.properties在包com.test.config下， 
     *                路径就是com/test/config/config.properties
     *              
     * @param filePath 
     * @return void  
     * @throws
     */
    public static void getProperties_1(String filePath){
        Properties prop = null;
        try {
            // 通过Spring中的PropertiesLoaderUtils工具类进行获取
            prop = PropertiesLoaderUtils.loadAllProperties(filePath);
            printAllProperty(prop);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 根据key读取value
     * 
     * @Title: getProperties_2   
     * @Description: 第二种方式：使用缓冲输入流读取配置文件，然后将其加载，再按需操作
     *                    绝对路径或相对路径， 如果是相对路径，则从当前项目下的目录开始计算， 
     *                  如：当前项目路径/config/config.properties, 
     *                  相对路径就是config/config.properties   
     *           
     * @param filePath
     * @param keyWord
     * @return
     * @return String  
     * @throws
     */
    public static String getProperties_2(String filePath, String keyWord){
        Properties prop = new Properties();
        String value = null;
        try {
            // 通过输入缓冲流进行读取配置文件
            InputStream InputStream = new BufferedInputStream(new FileInputStream(new File(filePath)));
            // 加载输入流
            prop.load(InputStream);
            // 根据关键字获取value值
            value = prop.getProperty(keyWord);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
    
    /**
     * 读取配置文件所有信息
     * 
     * @Title: getProperties_2   
     * @Description: 第二种方式：使用缓冲输入流读取配置文件，然后将其加载，再按需操作
     *                    绝对路径或相对路径， 如果是相对路径，则从当前项目下的目录开始计算， 
     *                  如：当前项目路径/config/config.properties, 
     *                  相对路径就是config/config.properties   
     *           
     * @param filePath
     * @return void
     * @throws
     */
    public static void getProperties_2(String filePath){
        Properties prop = new Properties();
        try {
            // 通过输入缓冲流进行读取配置文件
            InputStream InputStream = new BufferedInputStream(new FileInputStream(new File(filePath)));
            // 加载输入流
            prop.load(InputStream);
            printAllProperty(prop);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 根据key读取value
     * 
     * @Title: getProperties_3   
     * @Description: 第三种方式：
     *                    相对路径， properties文件需在classpath目录下， 
     *                  比如：config.properties在包com.test.config下， 
     *                  路径就是/com/test/config/config.properties 
     * @param filePath
     * @param keyWord
     * @return
     * @return String  
     * @throws
     */
    public static String getProperties_3(String filePath, String keyWord){
        Properties prop = new Properties();
        String value = null;
        try {
            InputStream inputStream = IOUtil.class.getResourceAsStream(filePath);
            prop.load(inputStream);
            value = prop.getProperty(keyWord);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }
    
    /**
     * 读取配置文件所有信息
     * 
     * @Title: getProperties_3   
     * @Description: 第三种方式：
     *                    相对路径， properties文件需在classpath目录下， 
     *                  比如：config.properties在包com.test.config下， 
     *                  路径就是/com/test/config/config.properties 
     * @param filePath
     * @return
     * @throws
     */
    public static void getProperties_3(String filePath){
        Properties prop = new Properties();
        try {
            InputStream inputStream = IOUtil.class.getResourceAsStream(filePath);
            prop.load(inputStream);
            printAllProperty(prop);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
