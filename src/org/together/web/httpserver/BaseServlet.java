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
	protected final static String ACTION = "action";

	public static void outputJSON(HttpServletResponse resp, byte[] res) throws IOException {
		resp.setContentType("text/html;charset=utf-8");
		resp.getOutputStream().write(res);
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

	public static void outputHtml(HttpServletResponse resp, String html) throws IOException {
		resp.setContentType("text/html;charset=utf-8");
		resp.getWriter().print(html);
	}

}
