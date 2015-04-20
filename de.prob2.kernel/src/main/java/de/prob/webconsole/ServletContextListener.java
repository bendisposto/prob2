package de.prob.webconsole;

import javax.servlet.ServletContextEvent;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

import de.prob.Main;

/**
 * Developers can access instantiated classes from an active instance of ProB
 * 2.0 by accessing the {@link Injector} from this class (using the
 * {@link ServletContextListener#getInjector()} method).
 * 
 * @author joy
 * 
 */
public class ServletContextListener extends GuiceServletContextListener {

	/**
	 * * @deprecated Use {@link Main.getInjector()} instead.
	 */
	@Deprecated
	public static final Injector INJECTOR = Main.getInjector();

	/**
	 * * @deprecated Use {@link Main.getInjector()} instead.
	 */
	@Deprecated
	@Override
	protected Injector getInjector() {
		return Main.getInjector();
	}

	@Override
	public void contextDestroyed(final ServletContextEvent event) {
	}

}