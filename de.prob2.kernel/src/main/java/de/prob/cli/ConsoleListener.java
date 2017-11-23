package de.prob.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.ref.WeakReference;

import org.slf4j.Logger;

final class ConsoleListener implements Runnable {
	private final WeakReference<ProBInstance> cli;
	private final BufferedReader stream;
	private final Logger logger;

	ConsoleListener(ProBInstance cli, BufferedReader stream, Logger logger) {
		this.cli = new WeakReference<>(cli);
		this.stream = stream;
		this.logger = logger;
	}

	public void run() {
		try (final BufferedReader ignored = this.stream) {
			logLines();
		} catch (IOException e) {
			String message = e.getMessage();
			if (!"Stream closed".equals(message)) {
				message = "OutputLogger died with error";
			}
			logger.info(message, e);
		}
	}

	void logLines() throws IOException {
		String line;
		ProBInstance instance;
		do {
			instance = cli.get();
			if (instance == null || instance.isShuttingDown()) {
				return;
			}
			line = readAndLog();
		} while (line != null);
	}

	String readAndLog() throws IOException {
		String line;
		line = stream.readLine();
		if (line != null) {
			logger.info(line + "\u001b[0m");
		}
		return line;
	}

}
