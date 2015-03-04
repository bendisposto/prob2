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

	private static final Logger LOGGER = LoggerFactory
			.getLogger(WebConsole.class);

	private static int PORT = 17080;

	public static void run(final String local, final String iface,
			final int port) throws Exception {

		if (port > 0) {
			PORT = port;
		}

		WebConsole.run(iface, new Runnable() {

			@Override
			public void run() {
				if (!local.isEmpty()) {
					try {
						Desktop.getDesktop().browse(
								new URI("http://localhost:" + WebConsole.PORT
										+ "/" + local));
					} catch (IOException e) {
						e.printStackTrace();
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	public static void run(final String iface, final Runnable openBrowser)
			throws Exception {

		System.setProperty("org.eclipse.jetty.util.log.class", "");

		Server server = new Server();

		ProtectionDomain protectionDomain = WebConsole.class
				.getProtectionDomain();
		WebConsole.LOGGER.debug("Protection Domain: "
				+ protectionDomain.toString());
		String warFile = protectionDomain.getCodeSource().getLocation()
				.toExternalForm();
		WebConsole.LOGGER.debug("External Form: " + warFile);

		if (!warFile.endsWith(".jar") && !warFile.endsWith("bin/")
				&& (!warFile.endsWith("main/"))) {
			warFile += "bin/";
		}

		WebAppContext context = new WebAppContext(warFile, "/");
		context.setServer(server);

		server.setStopAtShutdown(true);

		// Add the handlers
		HandlerList handlers = new HandlerList();
		handlers.addHandler(context);
		server.setHandler(handlers);

		int port = WebConsole.PORT;
		boolean found = false;
		do {
			try {
				Connector connector = new SelectChannelConnector();
				connector.setStatsOn(true);
				connector.setServer(server);
				String hostname = System.getProperty("prob.host", iface);
				connector.setHost(hostname);
				server.setConnectors(new Connector[] { connector });
				connector.setPort(port);
				server.start();
				found = true;
			} catch (BindException ex) {
				port++;
			}
		} while (!found && port < 17180);

		if (!found) {
			throw new BindException(
					"No free port found between 17080 and 17179");
		}

		WebConsole.PORT = port;

		openBrowser.run();
		server.join();
	}

	public static int getPort() {
		return WebConsole.PORT;
	}

}
