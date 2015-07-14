package org.together.home.sample;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.together.web.httpserver.BaseServlet;

/**
 * @author XiaXing 示例Servlet类，负责分发请求，转换request参数到简单对象中，异步任务处理等
 *
 */
@Singleton
@WebServlet(asyncSupported = true, urlPatterns = { "/Sample" })
public class SampleServlet extends BaseServlet {

	private static final long serialVersionUID = 2458855625573925714L;

	enum Action {
		SCAN,

		QUERY_BY_CODE
	}

	@Inject
	SampleService service;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Action action = Action.valueOf(req.getParameter(S_ACTION));
		switch (action) {
		case SCAN:
			// service的方法中接受的参数，可以直接是request，
			// 也可以是转换后的pojo，没有强制要求
			outputJSON(resp, service.scan(req));
			break;
		case QUERY_BY_CODE:
			outputJSON(resp, service.queryByCode(req));
			break;

		default:
			break;
		}

	}
}
