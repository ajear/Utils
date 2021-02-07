package com.example.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @auther QIANG.CQ.ZHOU
 * @VERSING 2020年6月10日下午2:00:22
 */
@Component
public class FTPUtil {
	 interface Common {
	        static final String ENCODE = "UTF-8";
	        static final int OUTTIME = 30000;
	    }
private static FTPClient ftpClient = null;

/**
	 * Description: 向FTP服务器上传文件
	 * 
	 * @param host
	 *            FTP服务器hostname
	 * @param port
	 *            FTP服务器端口
	 * @param username
	 *            FTP登录账号
	 * @param password
	 *            FTP登录密码
	 * @param basePath
	 *            FTP服务器基础目录
	 * @param filePath
	 *            FTP服务器文件存放路径。例如分日期存放：/2020/01/01。文件的路径为basePath+filePath
	 * @param input
	 *            本地要上传的文件的 输入流
	 * @return 成功返回true，否则返回false
	 */
	public boolean uploadFile(String ftpPath,String host, int port, String username, String password, MultipartFile file,
			String basePath, String newFilePath, InputStream input) {
		boolean result = false;
		String filename = file.getOriginalFilename();
		String ext = filename.substring(filename.lastIndexOf("."));
		try {
			int reply;
			ftpClient = new FTPClient();
			ftpClient.setConnectTimeout(Common.OUTTIME);// 設置連接超時時長
			ftpClient.setControlEncoding(Common.ENCODE);// 設置格式
			ftpClient.connect(host, port);// 連接FTP服務器
			ftpClient.login(username, password);// 登錄FTP
			reply = ftpClient.getReplyCode();// 連接成功會得到一個狀態碼
			System.out.println("連接狀態----" + reply);
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftpClient.disconnect();
				System.out.println("连接失败");
				return result;
			}
			System.out.println("连接成功");
			ftpClient.changeWorkingDirectory(basePath);
			// 切换到上传目录
			if (!"".equals(newFilePath)) {
				if (!ftpClient.changeWorkingDirectory(basePath + newFilePath)) {
					// 如果目录不存在创建目录
					String[] dirs = newFilePath.split("/");
					String tempPath = basePath;
					for (String dir : dirs) {
						if (null == dir || "".equals(dir))
							continue;
						tempPath += "/" + dir;
						if (!ftpClient.changeWorkingDirectory(tempPath)) {
							if (!ftpClient.makeDirectory(tempPath)) {
								return result;
							} else {
								ftpClient.changeWorkingDirectory(tempPath);
							}
						}
					}
				}
			}

			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			// 上傳文件
			filename = UUID.randomUUID().toString()+ext;
			if (!ftpClient.storeFile(filename, input)) {
				return result;
			}
			input.close();
			ftpClient.logout();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ftpClient.isConnected()) {
				try {
					ftpClient.disconnect();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
		return result;
	}

/**
 * 上传文件
 *
 * @param pathname       ftp服务保存地址
 * @param fileName       上传到ftp的文件名
 * @param originfilename 待上传文件的名称（绝对地址） 
 * @return
 */
public static String uploadFile(String hostname,int port,String username,String password,String pathname, String fileName, String originfilename) {
    
	   String res = "OK";
	   boolean flag = true;
    InputStream inputStream = null;
    try {
        inputStream = new FileInputStream(new File(originfilename));
        ftpClient = new FTPClient();
        ftpClient.setConnectTimeout(Common.OUTTIME);// 設置連接超時時長
        ftpClient.setControlEncoding(Common.ENCODE);// 設置格式
	       ftpClient.enterLocalPassiveMode();
	       Long time1= null,time2 = null,time3= null,time4= null,connectTime= null,loginTime= null,writeTime= null;
	       time1 = System.currentTimeMillis();
	       try {
	           ftpClient.connect(hostname, port); //连接ftp服务器
	           time2 = System.currentTimeMillis();
	           ftpClient.login(username, password);//登录ftp服务器
	           time3 = System.currentTimeMillis();
	           ftpClient.getReplyCode(); //是否成功登录服务器
	           System.out.println("連接狀態----" + ftpClient.getReplyCode());
	       } catch (Exception e) {
	    	   e.printStackTrace();
	    	   return "{\"result\":\"FAIL\",\"result_msg\":\""+"FTP Server error!"+"\"}";
	       }
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        
        //不符合"/"已经不能进入到pathname目录
        if (!pathname.equalsIgnoreCase("/") && !changeWorkingDirectory(pathname)){
     	   return "{\"result\":\"FAIL\",\"result_msg\":\""+"Folder has not created!"+"\"}";
        }
        
        ftpClient.changeWorkingDirectory(pathname);
        flag = ftpClient.storeFile(fileName, inputStream);
        time4 = System.currentTimeMillis();
        if(flag != true){
     	   return res = "{\"result\":\"FAIL\",\"result_msg\":\""+"UploadFile Error!"+"\"}";
        }
        String url="ftp://"+hostname+":"+port+pathname+"/"+fileName;
        res = "{\"result\":\"SUCCESS\",\"result_msg\":\""+"Upload File Success!"+"\",\"url\":\""+url+"\"}";
    } catch (Exception e) {
 	   e.printStackTrace();
 	   return "{\"result\":\"FAIL\",\"result_msg\":\""+"Upload File Fail!"+"\"}";
    } finally {
        try {
            ftpClient.logout();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (ftpClient.isConnected()) {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (null != inputStream) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    return res;
}

/**
 * 下载文件 *
 *
 * @param pathname  FTP服务器文件目录 *
 * @param filename  文件名称 *
 * @param localpath 下载后的文件路径 *
 * @return
 */
public static boolean downloadFile(String hostname,int port,String username,String password,String pathname, String filename, String localpath) {
    boolean flag = false;
    OutputStream os = null;
    try {
 	   ftpClient = new FTPClient();
	       ftpClient.setControlEncoding(Common.ENCODE);
	       try {
	           ftpClient.connect(hostname, port); //连接ftp服务器
	           ftpClient.login(username, password);//登录ftp服务器
	           ftpClient.getReplyCode(); //是否成功登录服务器
	       } catch (MalformedURLException e) {
	           e.printStackTrace();
	       } catch (IOException e) {
	           e.printStackTrace();
	       }finally {
	    	   Boolean res = ftpClient.login(username, password);//登录ftp服务器
	           System.out.println("res = "+ res);
	           ftpClient.getReplyCode();
	       }
        //切换FTP目录
        ftpClient.changeWorkingDirectory(pathname);
        FTPFile[] ftpFiles = ftpClient.listFiles();
        for (FTPFile file : ftpFiles) {
            if (filename.equalsIgnoreCase(file.getName())) {
                File localFile = new File(localpath + "/" + file.getName());
                os = new FileOutputStream(localFile);
                ftpClient.retrieveFile(file.getName(), os);
                os.close();
            }
        }
        ftpClient.logout();
        flag = true;
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        if (ftpClient.isConnected()) {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (null != os) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    return flag;
}

//改变目录路径
public static boolean changeWorkingDirectory(String directory) {
    boolean flag = true;
    try {
        flag = ftpClient.changeWorkingDirectory(directory);
    } catch (IOException ioe) {
        ioe.printStackTrace();
    }
    return flag;
}

//创建多层目录文件，如果有ftp服务器已存在该文件，则不创建，如果无，则创建
public static boolean createDirecroty(String remote) throws IOException {
    String directory = remote + "/";
    // 如果远程目录不存在，则递归创建远程服务器目录
    if (!directory.equalsIgnoreCase("/") && !changeWorkingDirectory(directory)) {
        int start = 0;
        int end = 0;
        if (directory.startsWith("/")) {
            start = 1;
        }
        end = directory.indexOf("/", start);
        String path = "";
        StringBuilder paths = new StringBuilder();
        while (true) {
            String subDirectory = new String(remote.substring(start, end).getBytes("GBK"), "iso-8859-1");
            path = path + "/" + subDirectory;
            if (!existFile(path)) {
                if (makeDirectory(subDirectory)) {
                    changeWorkingDirectory(subDirectory);
                } else {
                    System.out.println("创建目录[" + subDirectory + "]失败");
                    changeWorkingDirectory(subDirectory);
                }
            } else {
                changeWorkingDirectory(subDirectory);
            }
            paths.append("/").append(subDirectory);
            start = end + 1;
            end = directory.indexOf("/", start);
            // 检查所有目录是否创建完毕
            if (end <= start) {
                break;
            }
        }
    }
    return true;
}

//判断ftp服务器文件是否存在
public static boolean existFile(String path) throws IOException {
    boolean flag = false;
    FTPFile[] ftpFileArr = ftpClient.listFiles(path);
    if (ftpFileArr.length > 0) {
        flag = true;
    }
    return flag;
}

//创建目录
public static boolean makeDirectory(String dir) {
    boolean flag = true;
    try {
        flag = ftpClient.makeDirectory(dir);
    } catch (Exception e) {
        e.printStackTrace();
    }
    return flag;
}

/** * 删除文件 * 
 * @param pathname FTP服务器保存目录 * 
 * @param filename 要删除的文件名称 * 
 * @return */ 
 public static String deleteFile(String hostname,Integer port,String username,String password,String pathname, String filename){ 
    String res = "OK"; 
    try { 
         ftpClient = new FTPClient();
	        ftpClient.setControlEncoding(Common.ENCODE);
	        try {
	           ftpClient.connect(hostname, port); //连接ftp服务器
	           ftpClient.login(username, password);//登录ftp服务器
	           ftpClient.getReplyCode(); //是否成功登录服务器
	        } catch (MalformedURLException e) {
	           e.printStackTrace();
	        } catch (IOException e) {
	           e.printStackTrace();
	        }
         //切换FTP目录 
         ftpClient.changeWorkingDirectory(pathname); 
         ftpClient.dele(filename); 
         ftpClient.logout();
         res = "{\"result\":\"SECCESS\"}";
     } catch (Exception e) { 
         e.printStackTrace(); 
     } finally {
         if(ftpClient.isConnected()){ 
             try{
                 ftpClient.disconnect();
             }catch(IOException e){
                 e.printStackTrace();
             }
         } 
     }
     return res; 
 }


public static void main(String[] args) {
	   FTPUtil.uploadFile("192.168.56.1", 9001, "QIANG.CQ.ZHOU", "ZCQ.123.", "/upload", "132.png","D:/FTP/5ef9d6cf-a762-4f52-8476-3c2d1e0a7ac3.png" );
//	   FtpUtil.uploadFile("10.149.76.59",21,"test","test","/", "", "F:/TangLi/test.xml");
	   //FtpUtil.downloadFile("10.136.134.94", 21, "SRV-SMTFTPDB", "Deltasmtftp123", "/", "test.xlsx","F://sambaTest");
	   //FtpUtil.
}
}
