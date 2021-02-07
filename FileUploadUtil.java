package com.example.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.constant.ErrorConstant;
import com.example.service.FileUploadService;

/**
*@auther QIANG.CQ.ZHOU
*@VERSING 2020年8月13日上午9:23:20
*/
public class FileUploadUtil {
	
	private final static Logger logger = LoggerFactory.getLogger(FileUploadService.class);
	private final static String UPLOAD_PATH_FILE = "/uploadFile/";
	private final static String UPLOAD_PATH_IMAGE = "/image/";
	private final static String DiskFilePath = "e:\\uploadTemp";
	private static String UPLOAD_PATH_PREFIX = null;
	
	public APIResponse upload(HttpServletRequest request, HttpServletResponse response, MultipartFile file) {

		/**
		 * 使用commons.fileupload
		 */
		DiskFileItemFactory factory=new DiskFileItemFactory();
        ServletFileUpload upload=new ServletFileUpload(factory);
 
//        request.setCharacterEncoding("utf-8");
        //文件名中文乱码处理也可以如此写
        upload.setHeaderEncoding("utf-8");
 
        //设置缓冲区大小与临时文件目录
        factory.setSizeThreshold(1024*1024*10);
        File uploadTemp=new File(DiskFilePath);
        uploadTemp.mkdirs();
        factory.setRepository(uploadTemp);
 
        //设置单个文件大小限制
        upload.setFileSizeMax(1024*1024*10);
        //设置所有文件总和大小限制
        upload.setSizeMax(1024*1024*30);
 
        try {
            List<FileItem> list=upload.parseRequest(request);
            System.out.println(list);
            for (FileItem fileItem:list){
                if (!fileItem.isFormField()&&fileItem.getName()!=null&&!"".equals(fileItem.getName())){
                    String filName=fileItem.getName();
                    //利用UUID生成伪随机字符串，作为文件名避免重复
                    String uuid= UUID.randomUUID().toString();
                    //获取文件后缀名
                    String suffix=filName.substring(filName.lastIndexOf("."));
 
                    //获取文件上传目录路径，在项目部署路径下的upload目录里。若想让浏览器不能直接访问到图片，可以放在WEB-INF下
                    String uploadPath=request.getSession().getServletContext().getRealPath("/upload");
 
                    File file1=new File(uploadPath);
                    file1.mkdirs();
                    //写入文件到磁盘，该行执行完毕后，若有该临时文件，将会自动删除
                    fileItem.write(new File(uploadPath,uuid+suffix));
                    
                }
            }
        }  catch (Exception e) {
            e.printStackTrace();
        }
		
		if (file.isEmpty()) {
			return APIResponse.fail(ErrorConstant.Att.ADD_NEW_ATT_FILE_NULL);
		}
		return APIResponse.success(ErrorConstant.Att.ADD_NEW_ATT_FILE_SUCCESS);
	}
	
	/**
	 * 使用spring接受參數時使用
	 * @param file（MultipartFile）
	 * @return
	 */
	public APIResponse upload(MultipartFile file) {
		// 獲取文件大小
		long size = file.getSize();
		if (size > 1 * 1024 * 1024) {
			return APIResponse.fail(ErrorConstant.Att.UPLOAD_FILE_SIZE_OUT);
		}
		String fileName = file.getOriginalFilename();
		String ext = fileName.substring(fileName.lastIndexOf("."));
		String suffix = fileName.split("\\.")[1];
		String fileType = file.getContentType();

		// 添加類型限制，也可以配置文件獲取
		List<String> imgTypes = new ArrayList<String>();
		List<String> fileTypes = new ArrayList<String>();
		imgTypes.add("png");
		imgTypes.add("gif");
		imgTypes.add("jpeg");
		fileTypes.add("txt");
		fileTypes.add("xlxs");
		if (!imgTypes.contains(suffix) || !fileType.contains(suffix)) {
			return APIResponse.fail(ErrorConstant.Att.ADD_NEW_ATT_TYPR_FAIL);
		}

		// 固定位置
		// String path="E:/image/";

		// 獲取項目絕對路徑
		// String
		// path=FileUploadController.class.getResource("/").getPath().replace("classes",
		// "upload");

		if (imgTypes.contains(suffix)) {
			UPLOAD_PATH_PREFIX = UPLOAD_PATH_IMAGE;
		}
		if (fileType.contains(suffix)) {
			UPLOAD_PATH_PREFIX = UPLOAD_PATH_FILE;
		}
		// 上傳項目相對路徑
		String realPath = new String("src/main/resources/static" + UPLOAD_PATH_PREFIX);
		logger.info("-----------上传文件保存的路径【" + realPath + "】-----------");

		// 創建存放上传文件的文件夹
		File parent = new File(realPath);
		if (!parent.exists()) {
			// 递归生成文件夹
			parent.mkdirs();
		}
		// String filename = UUID.randomUUID().toString();
		// File newfile =new File(parent, filename+ext);
		File newfile = new File(parent.getAbsolutePath() + File.separator + fileName);
		try {
			file.transferTo(newfile);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return APIResponse.success(UPLOAD_PATH_PREFIX + fileName);
	}
}
