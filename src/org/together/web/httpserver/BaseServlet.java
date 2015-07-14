package org.together.web.httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

public class BaseServlet extends HttpServlet {

	private static final long serialVersionUID = -2926234836973163829L;

	// 用来区分同一个url对应的不同请求
	protected final static String S_ACTION = "saction";

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void outputJSON(HttpServletResponse resp, Object obj)
			throws IOException {
		HashMap map = new HashMap<>();
		try {
			map.put("status", "OK");
			map.put("result", obj);
		} catch (Exception e) {
			map.put("result", "服务器出现异常！");
			e.printStackTrace();
		}
		resp.setContentType("text/html;charset=utf-8");
		resp.getOutputStream().write(JSON.toJSONBytes(map));
	}

	protected String readJSONString(HttpServletRequest req) {
		StringBuffer json = null;
		String line = null;
		try {
			json = new StringBuffer();
			BufferedReader reader = req.getReader();
			while ((line = reader.readLine()) != null) {
				json.append(line);
			}
			return json.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	
	public static void outputHtml(HttpServletResponse resp, String html)
			throws IOException {
		resp.setContentType("text/html;charset=utf-8");
		resp.getWriter().print(html);
	}
		
		
		

}
