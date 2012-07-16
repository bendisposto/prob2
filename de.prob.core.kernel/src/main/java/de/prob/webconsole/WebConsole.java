package de.prob.webconsole;

import java.awt.Desktop;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.URI;
import java.security.ProtectionDomain;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * @author bendisposto
 * 
 */
public class WebConsole {

	/**
	 * Taken from Apache MINA
	 * 
	 * @param port
	 * @return next available port
	 */
	public static boolean available(final int port) {
		if (port < 8080 || port > 8180)
			throw new IllegalArgumentException("Invalid start port: " + port);

		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException e) {
		} finally {
			if (ds != null) {
				ds.close();
			}

			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					/* should not be thrown */
				}
			}
		}

		return false;
	}

	public static void run() throws Exception {

		System.setProperty("org.eclipse.jetty.util.log.class", "");

		int port = 8080;

		while (!available(port)) {
			port++;
		}
		Server server = new Server();

		Connector connector = new SelectChannelConnector();
		connector.setPort(port);
		connector.setServer(server);
		String hostname = System.getProperty("prob.host", "127.0.0.1");
		connector.setHost(hostname);

		server.setConnectors(new Connector[] { connector });

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
		Desktop.getDesktop().browse(new URI("http://localhost:" + port));
		server.join();

	}
}
