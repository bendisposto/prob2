package de.prob.webconsole;

import java.util.Set;

import javax.servlet.ServletContextEvent;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.servlet.GuiceServletContextListener;

import de.prob.Main;
import de.prob.MainModule;

/**
 * Developers can access instantiated classes from an active instance of ProB
 * 2.0 by accessing the {@link Injector} from this class (using the
 * {@link ServletContextListener#getInjector()} method).
 * 
 * @author joy
 * 
 */
public class ServletContextListener extends GuiceServletContextListener {

	public static final Injector INJECTOR = Guice.createInjector(
			Stage.DEVELOPMENT, new MainModule());

	@Override
	protected Injector getInjector() {
		return INJECTOR;
	}

	@Override
	public void contextDestroyed(final ServletContextEvent event) {
		Set<Process> keySet = Main.processes.keySet();
		for (Process process : keySet) {
			process.destroy();
		}
	}

}