package org.together.web.httpserver;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.Filter;
import javax.servlet.annotation.WebFilter;
import javax.sql.DataSource;

import com.google.inject.servlet.GuiceFilter;

@WebFilter(urlPatterns={"/*"}, asyncSupported = true)
public class BaseFilter extends GuiceFilter implements Filter {
	@SuppressWarnings("unused")
	public BaseFilter() {
		try {
			Context c = new InitialContext();
			DataSource ds = (DataSource) c
					.lookup("java:comp/env/jdbc/prdmpool");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
