package org.together.web.httpserver;

import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.google.inject.Provider;

public class JndiProvider<T> implements Provider<T> {

	@Inject
	InitialContext context;
	final String name;
	final Class<T> type;

	JndiProvider(Class<T> type, String name) {
		this.name = name;
		this.type = type;
	}

	public T get() {
		try {
			return type.cast(context.lookup(name));
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Creates a JNDI provider for the given type and name.
	 */
	static <T> Provider<T> fromJndi(Class<T> type, String name) {
		return new JndiProvider<T>(type, name);
	}
}