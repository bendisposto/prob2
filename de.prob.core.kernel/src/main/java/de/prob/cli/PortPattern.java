/**
 * 
 */
package de.prob.cli;

import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This {@link AbstractCliPattern} looks for a network port number where the
 * executable listens for commands.
 * 
 * If no port number is found, {@link #notFound()} throws a {@link CliException}
 * 
 * @author plagge
 */
class PortPattern extends AbstractCliPattern<Integer> {
	int port;

	private final Logger logger = LoggerFactory.getLogger(PortPattern.class);

	public PortPattern() {
		super("Port: (\\d+)$");
	}

	@Override
	protected void setValue(Matcher matcher) throws IllegalArgumentException {
		port = Integer.parseInt(matcher.group(1));
		logger.info("Server has startet and listens on port " + port);
	}

	/**
	 * Returns the port number.
	 */
	@Override
	public Integer getValue() {
		return port;
	}

	@Override
	public void notifyNotFound() {
		logger.error("Could not determine port of ProB server");
	}

	@Override
	public boolean notFoundIsFatal() {
		return true;
	}

}
