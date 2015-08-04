package org.together.home.login;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.together.pub.db.DBbean;
import org.together.web.httpserver.BaseServlet;

@Singleton
@WebServlet(urlPatterns = "/login")
public class LoginServlet extends BaseServlet {

	private static final long serialVersionUID = -3650166165809925090L;

	@Inject
	LoginService service;

	@Inject
	DBbean db;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		outputJSON(resp, service.login(req, resp));
	}

}
