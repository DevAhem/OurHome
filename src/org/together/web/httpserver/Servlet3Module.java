package org.together.web.httpserver;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.together.pub.util.Classes;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.matcher.Matchers;
import com.google.inject.servlet.ServletModule;

public abstract class Servlet3Module extends ServletModule {

	private final List<Package> packages = Lists.newArrayList();

	@Override
	protected abstract void configureServlets();

	@SuppressWarnings("unchecked")
	protected void configureWebServlets(Package... packs) {
		for (Package pack:packs) {
			Preconditions.checkArgument(null != pack,
					"Package parameter to scan() cannot be null");
			packages.add(pack);
		}
		
		for (Package p : packages) {
			for (Class<?> servletClass : Classes.matching(
					Matchers.annotatedWith(WebServlet.class)).in(p)) {
				if (HttpServlet.class.isAssignableFrom(servletClass)) {
					Class<HttpServlet> servletClazz = (Class<HttpServlet>) servletClass;
					WebServlet webServlet = servletClass
							.getAnnotation(WebServlet.class);
					String[] urlPatterns = webServlet.urlPatterns();
					if (urlPatterns.length > 1) {
						serve(
								urlPatterns[0],
								Arrays.copyOfRange(urlPatterns, 1,
										urlPatterns.length)).with(servletClazz,
								getInitParams(webServlet));
					} else {
						if (urlPatterns.length > 0) {
							serve(urlPatterns[0]).with(servletClazz,
									getInitParams(webServlet));
						} else {
							addError(
									"Guice found a WebServlet %s with no urlPatterns defined.",
									webServlet.getClass().getCanonicalName());
						}
					}
				}
			}
		}
	}

	private Map<String, String> getInitParams(WebServlet webServlet) {
		checkNotNull(webServlet);
		checkNotNull(webServlet.initParams());
		final WebInitParam[] params = webServlet.initParams();
		final Map<String, String> initParams = Maps
				.newHashMapWithExpectedSize(params.length);
		for (int i = 0; i < params.length; i++) {
			WebInitParam w = params[i];
			initParams.put(w.name(), w.value());
		}
		return initParams;
	}

	protected void scanServlets(Package pack) {
		Preconditions.checkArgument(null != pack,
				"Package parameter to scan() cannot be null");
		packages.add(pack);
	}

}
