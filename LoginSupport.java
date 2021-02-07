//package com.example.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.SpringBootConfiguration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
//
///**
// * @auther QIANG.CQ.ZHOU
// * @VERSING 2020年4月17日下午7:11:20
// */
//@SpringBootConfiguration
//public class LoginSupport extends WebMvcConfigurationSupport {
//
//	@Autowired
//	private LoginInterceptor loginInterceptor;
//
//	@Override
//	protected void addInterceptors(InterceptorRegistry registry) {
//		registry.addInterceptor(loginInterceptor).addPathPatterns("/**").excludePathPatterns("/login","/verifyCode","/doLogin","/welcome");//該路徑值得是@RequestMapping綁定路徑
//	}
//
//	/**
//     * 解决resources下面静态资源无法访问
//     * @param registry
//     */
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/**").addResourceLocations("/favicon.ico")
//                .addResourceLocations("classpath:/static/");
//        super.addResourceHandlers(registry);
//    }
//
//}
