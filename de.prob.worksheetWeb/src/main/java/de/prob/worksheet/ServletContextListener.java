package de.prob.worksheet;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class ServletContextListener extends GuiceServletContextListener {

	public static final Injector	INJECTOR	= Guice.createInjector(new WorksheetModule());

	@Override
	protected Injector getInjector() {
		return ServletContextListener.INJECTOR;
	}

}