package org.together.home.money.person;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.together.web.httpserver.BaseServlet;

@Singleton
@WebServlet(urlPatterns = "/money/person")
public class PersonServlet extends BaseServlet {

	private static final long serialVersionUID = -3650166165809925090L;
	
	@Inject
	PersonService service;

	enum Action {
		IMCOME_ADD,

		IMCOME_MODIFY,

		IMCOME_REMOVE,

		IMCOME_QUERY,

		EXPEND_ADD,

		EXPEND_MODIFY,

		EXPEND_REMOVE,

		EXPEND_QUERY
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			Action action = Action.valueOf(req.getParameter(ACTION));
			switch (action) {
			case IMCOME_ADD:
				outputJSON(resp, service.addIncome(req));
				break;
			case IMCOME_MODIFY:
				outputJSON(resp, service.modifyIncome(req));
				break;
			case IMCOME_REMOVE:
				outputJSON(resp, service.removeIncome(req));
				break;
			case IMCOME_QUERY:
				outputJSON(resp, service.queryIncome(req));
				break;
			case EXPEND_ADD:
				outputJSON(resp, service.addIncome(req));
				break;
			case EXPEND_MODIFY:
				outputJSON(resp, service.modifyIncome(req));
				break;
			case EXPEND_REMOVE:
				outputJSON(resp, service.removeIncome(req));
				break;
			case EXPEND_QUERY:
				outputJSON(resp, service.queryIncome(req));
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
