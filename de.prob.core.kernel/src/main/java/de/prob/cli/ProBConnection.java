package de.prob.cli;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;

public class ProBConnection {

	private Socket socket;
	private BufferedInputStream inputStream;
	private PrintStream outputStream;
	private final Logger logger = LoggerFactory.getLogger(ProBConnection.class);
	private volatile boolean shutingDown;
	private final String key;
	private final int port;

	public ProBConnection(final String key, final int port) {
		this.key = key;
		this.port = port;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(ProBConnection.class).add("key", key)
				.add("port", port).toString();
	}

	public void connect() throws IOException {
		logger.debug("Connecting to port {} using key {}", port, key);
		// try {
		socket = new Socket(InetAddress.getByName(null), port);
		inputStream = new BufferedInputStream(socket.getInputStream());
		OutputStream outstream = socket.getOutputStream();
		outputStream = new PrintStream(outstream, false, Charset
				.defaultCharset().name());
		logger.debug("Connected");
		// } catch (final IOException e) {
		// if (socket != null) {
		// try {
		// socket.close();
		// } catch (final IOException e2) {
		// } finally {
		// socket = null;
		// inputStream = null;
		// outputStream = null;
		// }
		// }
		// logger.error("Opening connection to ProB server failed", e);
		// throw new ProBException();
		// }
	}

	public String send(final String term) throws IOException {
		logger.trace(term);
		if (shutingDown) {
			final String message = "probcli is currently shutting down";
			logger.error(message);
			throw new IllegalStateException(
					"ProB has been shut down. It does not accept messages. Received: "
							+ term);
		}
		if (isStreamReady()) {
			outputStream.println(term);
			outputStream.flush();
		}
		String answer = getAnswer();
		logger.trace(answer);
		return answer;
	}

	private String getAnswer() throws IOException {
		String input = null;
		input = readAnswer();
		if (input == null)
			throw new IOException(
					"ProB binary returned nothing - it might have crashed");
		return input;
	}

	protected String readAnswer() throws IOException {
		final StringBuilder result = new StringBuilder();
		final byte[] buffer = new byte[1024];
		boolean done = false;

		while (!done) {
			/*
			 * It might be necessary to check for inputStream.available() > 0.
			 * Or add some kind of timer to prevent the thread blocks forever.
			 * See task#102
			 */
			int count = inputStream.read(buffer);

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
				String s = new String(buffer, 0, count, Charset
						.defaultCharset().name());
				result.append(s.trim());
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
