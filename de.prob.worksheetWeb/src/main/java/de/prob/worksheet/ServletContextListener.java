package de.prob.worksheet;

import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	public static final Logger logger = LoggerFactory
			.getLogger(ServletContextListener.class);
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
		ServletContextListener.logger.trace("in:");
		ServletContextListener.logger.trace("return: injector={}",
				ServletContextListener.INJECTOR);
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
		ServletContextListener.logger.trace("in: event={}", sce);
		ServletContextListener.logger.trace("return:");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.
	 * ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContextListener.logger.trace("in: event={}", sce);
		ServletContextListener.logger.trace("return:");
	}

}