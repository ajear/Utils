//package com.example.config;
//
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//
//import javax.servlet.Servlet;
//import javax.servlet.ServletConfig;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import javax.swing.RepaintManager;
//
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.ModelAndView;
//
//import com.example.util.StringUtil;
//import com.example.util.VerifyUtil;
//
///**
// * @auther QIANG.CQ.ZHOU
// * @VERSING 2020年4月17日下午5:27:12
// */
//@Component
//public class LoginInterceptor implements HandlerInterceptor {
//	/**
//	 * 在请求处理之前进行调用（Controller方法调用之前）
//	 *
//	 * @return 返回true才会继续向下执行，返回false取消当前请求
//	 */
//	@Override
//	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//			throws Exception {
//		StringBuffer url=request.getRequestURL();
//		HttpSession session = request.getSession(true);
//		if (session.getAttribute("User")!=null) {
//			if (StringUtil.isNotEmpty(session.getAttribute("code").toString()) && StringUtil.isNotEmpty(session.getAttribute("captchaCode").toString())) {
//				if ( session.getAttribute("captchaCode").toString().toUpperCase().equals(session.getAttribute("code").toString().toUpperCase())) {
//					webContentNoCache(request,response);
//					return true;
//				} else {
//					response.sendRedirect(request.getContextPath() + "/login");// 該路徑值得是@RequestMapping綁定路徑
//					return false;
//				}
//			} else {
//				response.sendRedirect(request.getContextPath() + "/login");// 該路徑值得是@RequestMapping綁定路徑
//				return false;
//			}
//
//		} else {
//			response.sendRedirect(request.getContextPath() + "/login");// 該路徑值得是@RequestMapping綁定路徑
//			return false;
//		}
//
//	}
//
//	/**
//	 * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
//	 */
//	@Override
//	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
//			ModelAndView modelAndView) throws Exception {
//		// TODO Auto-generated method stub
//		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
//	}
//
//	/**
//	 * 在整个请求结束之后被调用，DispatcherServlet 渲染视图之后执行（主要进行资源清理工作）
//	 */
//	@Override
//	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
//			throws Exception {
//		// TODO Auto-generated method stub
//		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
//	}
//
//	public void webContentNoCache(ServletRequest servletRequest,
//			ServletResponse servletResponse)
//			throws UnsupportedEncodingException {
//		((HttpServletResponse) servletResponse).setHeader("Cache-Control",
//				"no-cache");
//		((HttpServletResponse) servletResponse).setHeader("Pragma", "no-cache");
//		((HttpServletResponse) servletResponse).setDateHeader("Expires", -1);
//		servletRequest.setCharacterEncoding("UTF-8");
//		servletResponse.setCharacterEncoding("UTF-8");
//		servletResponse.setContentType("text/html;charset=UTF-8");
//	}
//}
