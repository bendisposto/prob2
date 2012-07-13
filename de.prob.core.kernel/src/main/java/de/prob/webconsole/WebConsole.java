package de.prob.webconsole;

import java.awt.Desktop;
import java.net.URI;
import java.security.ProtectionDomain;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.webapp.WebAppContext;

public class WebConsole {

	public static void run() throws Exception {

		System.setProperty("org.eclipse.jetty.util.log.class", "");

		Server server = new Server(8080);

		ProtectionDomain protectionDomain = WebConsole.class
				.getProtectionDomain();
		String warFile = protectionDomain.getCodeSource().getLocation()
				.toExternalForm();

		WebAppContext context = new WebAppContext(warFile, "/");
		context.setServer(server);

		// Add the handlers
		HandlerList handlers = new HandlerList();
		handlers.addHandler(context);
		server.setHandler(handlers);

		server.start();
		Desktop.getDesktop().browse(new URI("http://localhost:8080"));
		server.join();

	}

}
