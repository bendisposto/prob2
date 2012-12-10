package de.prob.webconsole;

import java.awt.Desktop;
import java.io.IOException;
import java.net.BindException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.ProtectionDomain;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bendisposto
 * 
 */
public class WebConsole {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebConsole.class);
	private static int PORT;

	public static void run() throws Exception {
		run(new Runnable() {

			@Override
			public void run() {
				try {
					Desktop.getDesktop().browse(
							new URI("http://localhost:" + PORT));
				} catch (IOException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void run(final Runnable openBrowser) throws Exception {

		System.setProperty("org.eclipse.jetty.util.log.class", "");

		
		Server server = new Server();

		ProtectionDomain protectionDomain = WebConsole.class
				.getProtectionDomain();
		LOGGER.debug("Protection Domain: "+ protectionDomain.toString());
		String warFile = protectionDomain.getCodeSource().getLocation()
				.toExternalForm();
		LOGGER.debug("External Form: "+ warFile);

		if (!warFile.endsWith("bin/"))
			warFile += "bin/";

		WebAppContext context = new WebAppContext(warFile, "/");
		context.setServer(server);

		// Add the handlers
		HandlerList handlers = new HandlerList();
		handlers.addHandler(context);
		server.setHandler(handlers);

		int port = 8080;
		boolean found = false;
		do {
			try {
				Connector connector = new SelectChannelConnector();
				connector.setServer(server);
				String hostname = System.getProperty("prob.host", "127.0.0.1");
				connector.setHost(hostname);
				server.setConnectors(new Connector[] { connector });
				connector.setPort(port);
				server.start();
				found = true;
			} catch (BindException ex) {
				port++;
			}
		} while (!found && port < 8180);

		if (!found) {
			throw new BindException("No free port found between 8080 and 8179");
		}

		PORT = port;

		openBrowser.run();
		server.join();
	}

	public static int getPort() {
		return PORT;
	}

}
