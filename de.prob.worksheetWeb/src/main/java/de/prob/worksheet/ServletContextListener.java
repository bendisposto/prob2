package de.prob.worksheet;

import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

import com.google.inject.Injector;

/**
 * This ServletContextListener is responsible for Initialization and Destruction
 * of the Worksheet Web Application. It also serves as a container for a Guice
 * Injector which is a childInjector of de.prob.webconsole s injector.
 * 
 * @author Rene
 * 
 */
@WebListener
public class ServletContextListener implements
		javax.servlet.ServletContextListener {

	/**
	 * This is a Guice Injector which is the child of de.prob.webconsoles
	 * injector.
	 */
	public static final Injector INJECTOR = de.prob.webconsole.ServletContextListener.INJECTOR
			.createChildInjector(new WorksheetModule());

	/**
	 * Returns the static instance of the guice injector
	 * 
	 * @return guice injector
	 */
	protected Injector getInjector() {
		return ServletContextListener.INJECTOR;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.ServletContextListener#contextInitialized(javax.servlet
	 * .ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.
	 * ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub

	}

}