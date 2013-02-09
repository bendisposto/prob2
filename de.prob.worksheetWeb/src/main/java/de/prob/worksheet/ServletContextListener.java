package de.prob.worksheet;

import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

import com.google.inject.Injector;

@WebListener
public class ServletContextListener implements
		javax.servlet.ServletContextListener {

	public static final Injector INJECTOR = de.prob.webconsole.ServletContextListener.INJECTOR
			.createChildInjector(new WorksheetModule());

	protected Injector getInjector() {
		return ServletContextListener.INJECTOR;
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub

	}

}