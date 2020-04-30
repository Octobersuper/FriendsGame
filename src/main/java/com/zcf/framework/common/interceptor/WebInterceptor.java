package com.zcf.framework.common.interceptor;

import com.zcf.framework.common.json.Body;
import com.zcf.framework.common.json.Meta;
import com.zcf.framework.common.utils.JsonUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * 请求拦截器
 *
 * @author
 * @date 2017/2/22
 */
public class WebInterceptor implements HandlerInterceptor {

	// token 参数名
	public static String TOKEN = "token";

	/**
	 * 拦截请求验证请求有效性
	 *
	 * @param request
	 * @param response
	 * @param handler
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 验证token参数
		String token = request.getParameter(TOKEN);
		if (StringUtils.isEmpty(token)) {
			writeJson(response, Meta.CODE_451);
			return false;
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}

	/**
	 * response返回 json
	 *
	 * @param response
	 * @param meta
	 * @throws Exception
	 */
	public static void writeJson(HttpServletResponse response, Meta meta) throws Exception {
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json");
		OutputStream out = response.getOutputStream();
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, "utf-8"));
		pw.println(JsonUtils.beanToJson(new Body(meta)));
		pw.flush();
		pw.close();
	}

}