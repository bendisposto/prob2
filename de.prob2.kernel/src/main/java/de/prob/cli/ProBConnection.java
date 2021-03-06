package de.prob.cli;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;

import com.google.common.base.MoreObjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProBConnection {

	private static final int BUFFER_SIZE = 1024;

	private Socket socket;
	private BufferedInputStream inputStream;
	private PrintStream outputStream;
	private final Logger logger = LoggerFactory.getLogger(ProBConnection.class);
	private volatile boolean shutingDown;
	private volatile boolean busy;
	private final String key;
	private final int port;

	public ProBConnection(final String key, final int port) {
		this.key = key;
		this.port = port;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(ProBConnection.class).add("key", key)
				.add("port", port).toString();
	}

	public void connect() throws IOException {
		logger.debug("Connecting to port {} using key {}", port, key);
		socket = new Socket(InetAddress.getByName(null), port);
		inputStream = new BufferedInputStream(socket.getInputStream());
		OutputStream outstream = socket.getOutputStream();
		outputStream = new PrintStream(outstream, false, "utf8");
		logger.debug("Connected");
	}

	private static String shorten(final String s) {
		final String shortened = s.length() <= 200 ? s : (s.substring(0, 200) + "...");
		return shortened.endsWith("\n") ? shortened.substring(0, shortened.length()-1) : shortened;
	}

	public String send(final String term) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug(shorten(term));
		}
		if (shutingDown) {
			logger.error("Cannot send terms while probcli is shutting down: {}", term);
			throw new IOException("ProB has been shut down. It does not accept messages.");
		}
		if (isStreamReady()) {
			outputStream.println(term);
			outputStream.flush();
		}
		String answer = getAnswer();
		logger.trace(answer);
		return answer;
	}

	public boolean isBusy() {
		return busy;
	}

	private String getAnswer() throws IOException {
		String input;
		input = readAnswer();
		if (input == null) {
			throw new IOException(
					"ProB binary returned nothing - it might have crashed");
		}
		return input;
	}

	protected String readAnswer() throws IOException {
		final StringBuilder result = new StringBuilder();
		final byte[] buffer = new byte[BUFFER_SIZE];
		boolean done = false;

		while (!done) {
			/*
			 * It might be necessary to check for inputStream.available() > 0.
			 * Or add some kind of timer to prevent the thread blocks forever.
			 * See task#102
			 */
			busy = true;
			int count = inputStream.read(buffer);
			busy = false; // as soon as we read something, we know that the
							// Prolog has been processed and we do not want to
							// allow interruption
			if (count > 0) {
				final byte length = 1;

				// check for end of transmission (i.e. last byte is 1)
				if (buffer[count - length] == 1) {
					done = true;
					count--; // remove end of transmission marker
				}

				// trim white spaces and append
				// instead of removing the last byte trim is used, because on
				// windows prob uses \r\n as new line.
				String s = new String(buffer, 0, count, "utf8");
				result.append(s.replace("\r", "").replace("\n", ""));
			} else {
				done = true;
			}
		}

		return result.length() > 0 ? result.toString() : null;
	}

	private boolean isStreamReady() {
		if (inputStream == null || outputStream == null) {
			logger.warn("Stream to ProB server not ready");
			return false;
		}
		return true;
	}

	public void disconnect() {
		shutingDown = true;
	}

	public String getKey() {
		return key;
	}

}
