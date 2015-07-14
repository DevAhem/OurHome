package org.together.web.httpserver;

import javax.sql.DataSource;

import com.google.inject.AbstractModule;

public class BaseModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(DataSource.class).toProvider(
				JndiProvider.fromJndi(DataSource.class,
						"java:comp/env/jdbc/prdmpool")).asEagerSingleton();
		;

	}

}
