package de.prob.cli;

import java.io.BufferedReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.inject.Inject;

import de.prob.ProBException;
import de.prob.annotations.Home;

class ProBInstanceImpl implements ProBInstance {

	private final Thread thread;

	private volatile boolean shutingDown = false;

	final Logger logger = LoggerFactory.getLogger(ProBInstanceImpl.class);
	private final Process process;

	private final long userInterruptReference;

	private final ProBConnection connection;

	public ProBInstanceImpl(final Process process, final BufferedReader stream,
			final Long userInterruptReference, final ProBConnection connection)
			throws ProBException {
		this.process = process;
		this.connection = connection;
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
	public void sendUserInterruptReference(@Home final String home,
			final OsSpecificInfo osInfo) {
		try {
			final String command = home + osInfo.userInterruptCmd;
			String[] cmd = new String[] { command,
					Long.toString(userInterruptReference) };
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			logger.warn("calling the send_user_interrupt command failed", e);
		}
	}

	public String send(final String term) throws ProBException {
		return connection.send(term);
	}

	public boolean isShuttingDown() {
		return shutingDown;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(ProBInstanceImpl.class)
				.addValue(connection).toString();
	}

}