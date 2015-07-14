package org.together.web.httpserver;

import javax.servlet.annotation.WebListener;

import org.together.home.Packages;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

@WebListener
public class BaseServletConfigListener extends GuiceServletContextListener {
	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new BaseModule(), new Servlet3Module() {
			@Override
			protected void configureServlets() {
				configureWebServlets(Packages.class.getPackage());
			}
		});
	}
}