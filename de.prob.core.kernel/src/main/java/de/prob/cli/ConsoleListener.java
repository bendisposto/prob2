package de.prob.cli;

import java.io.BufferedReader;
import java.io.IOException;

import org.slf4j.Logger;

final class ConsoleListener implements Runnable {
	private final ProBInstance cli;
	private final BufferedReader stream;
	private final Logger logger;

	ConsoleListener(ProBInstance cli, BufferedReader stream, Logger logger) {
		this.cli = cli;
		this.stream = stream;
		this.logger = logger;
	}

	public void run() {
		try {
			logLines();
		} catch (IOException e) {
			if (!"Stream closed".equals(e.getMessage())) {
				final String message = "OutputLogger died with error";
				logger.info(message, e);
			}
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	void logLines() throws IOException {
		String line = null;
		if (!cli.isShuttingDown()) {
			do {
				line = readAndLog();
			} while (line != null && !cli.isShuttingDown());
		}
	}

	String readAndLog() throws IOException {
		String line;
		line = stream.readLine();
		if (line != null) {
			logger.debug(line);
		}
		return line;
	}

}