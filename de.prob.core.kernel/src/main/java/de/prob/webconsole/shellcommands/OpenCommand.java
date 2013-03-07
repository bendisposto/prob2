package de.prob.webconsole.shellcommands;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.webconsole.GroovyExecution;

public class OpenCommand extends AbstractShellCommand {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(OpenCommand.class);

	@Override
	public Object perform(final List<String> m, final GroovyExecution exec)
			throws IOException {
		if (m.size() != 2) {
			String msg = "Load command takes exactly one parameter, a filename.";
			LOGGER.error(msg);
			return "error: " + msg;
		}

		String cmd = m.get(1);
		if (cmd.equals("javadoc")) {
			try {
				java.awt.Desktop
						.getDesktop()
						.browse(new URL(
								"http://nightly.cobra.cs.uni-duesseldorf.de/prob2/javadoc/de.prob.core.kernel/build/docs/javadoc/")
								.toURI());
				return "Javadoc opened";
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
}
