package de.prob.webconsole;

import java.util.Set;

import javax.servlet.ServletContextEvent;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

import de.prob.Main;
import de.prob.MainModule;

public class ServletContextListener extends GuiceServletContextListener {

	public static final Injector INJECTOR = Guice
			.createInjector(new MainModule());

	@Override
	protected Injector getInjector() {
		return INJECTOR;
	}

	public void contextDestroyed(ServletContextEvent event) {
		Set<Process> keySet = Main.processes.keySet();
		for (Process process : keySet) {
			process.destroy();
		}
	}

}