package de.prob.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;
import com.google.inject.Inject;

import de.prob.exception.CliError;

public class ProBInstance {

	private final Thread thread;

	private volatile boolean shutingDown = false;

	final Logger logger = LoggerFactory.getLogger(ProBInstance.class);
	private final Process process;

	private final ProBConnection connection;

	private String[] interruptCommand;

	private AtomicInteger processCounter;

	@Inject
	public ProBInstance(final Process process, final BufferedReader stream,
			final Long userInterruptReference, final ProBConnection connection,
			final String home, final OsSpecificInfo osInfo,
			AtomicInteger processCounter) {
		this.process = process;
		this.connection = connection;
		this.processCounter = processCounter;
		final String command = home + osInfo.getUserInterruptCmd();
		interruptCommand = new String[] { command,
				Long.toString(userInterruptReference.longValue()) };
		thread = makeOutputPublisher(stream);
		thread.start();
	}

	private Thread makeOutputPublisher(final BufferedReader stream) {
		return new Thread(new ConsoleListener(this, stream, logger));
	}

	public void shutdown() {
		if (shutingDown == false) {
			processCounter.decrementAndGet();
		}
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

	public void sendInterrupt() {
		try {
			if (connection.isBusy()) {
				logger.info("sending interrupt signal");
				Runtime.getRuntime().exec(interruptCommand);
			} else {
				logger.info("ignoring interrupt signal because the connection is not busy");
			}
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
		return MoreObjects.toStringHelper(ProBInstance.class)
				.addValue(connection).toString();
	}

}