package oa.seal;

import org.together.home.money.person.PersonService;

import com.google.inject.Guice;
import com.google.inject.Injector;

import pub.TestModule;

public class TestService {

	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new TestModule());
		PersonService service = injector.getInstance(PersonService.class);
		service.test();
	}
}
