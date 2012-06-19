package de.prob.cli;

import java.io.BufferedReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.inject.Inject;

import de.prob.exception.CliError;

public class ProBInstance {

	private final Thread thread;

	private volatile boolean shutingDown = false;

	final Logger logger = LoggerFactory.getLogger(ProBInstance.class);
	private final Process process;

	private final long userInterruptReference;

	private final ProBConnection connection;

	private final String home;

	private final OsSpecificInfo osInfo;

	public ProBInstance(final Process process, final BufferedReader stream,
			final Long userInterruptReference, final ProBConnection connection,
			final String home, final OsSpecificInfo osInfo) {
		this.process = process;
		this.connection = connection;
		this.home = home;
		this.osInfo = osInfo;
		this.userInterruptReference = userInterruptReference.longValue();
		thread = makeOutputPublisher(stream);
		thread.start();
	}

	private Thread makeOutputPublisher(final BufferedReader stream) {
		return new Thread(new ConsoleListener(this, stream, logger));
	}

	public void shutdown() {
		shutingDown = true;
		try {
			if (thread.isAlive()) {
				thread.interrupt();
			}
			connection.disconnect();
		} finally {
			process.destroy();
		}
	}

	@Inject
	public void sendInterrupt() {
		try {
			final String command = home + osInfo.userInterruptCmd;
			String[] cmd = new String[] { command,
					Long.toString(userInterruptReference) };
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			logger.warn("calling the send_user_interrupt command failed", e);
		}
	}

	public String send(final String term) {
		try {
			return connection.send(term);
		} catch (IOException e) {
			throw new CliError("Error during communicating with Prolog core.",
					e);
		}
	}

	public boolean isShuttingDown() {
		return shutingDown;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(ProBInstance.class).addValue(connection)
				.toString();
	}

}