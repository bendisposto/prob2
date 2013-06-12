package de.prob.webconsole.shellcommands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jline.FileNameCompletor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import de.prob.webconsole.GroovyExecution;

public class RunScriptCommand extends AbstractShellCommand {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(RunScriptCommand.class);

	@Override
	public Object perform(final List<String> m, final GroovyExecution exec)
			throws IOException {

		if (m.size() != 2 && m.size() != 3) {
			String msg = "Run command takes one or two parameters. A filename and optionally a variablename where the result should be stored.";
			LOGGER.error(msg);
			return "error: " + msg;
		}

		String filename = m.get(1);

		if (filename.startsWith("~")) {
			filename = filename.replaceFirst("~",
					System.getProperty("user.home"));
		}

		int lastDot = filename.lastIndexOf('.');

		File f = new File(filename);
		if (!f.exists()) {
			String msg = "File '" + filename + "' does not exist.";
			LOGGER.error(msg);
			return "error: " + msg;
		}

		String extension = filename.substring(lastDot, filename.length());

		if (extension.equals(".groovy")) {
			StringBuffer fileData = new StringBuffer(1000);
			BufferedReader reader = new BufferedReader(new FileReader(f));
			char[] buf = new char[1024];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
				buf = new char[1024];
			}
			reader.close();

			String result;
			if (m.size() == 3) {
				result = exec.runScript(fileData.toString(), m.get(2));
			} else {
				result = exec.runScript(fileData.toString());
			}

			return result;

		}

		String msg = "Groovy script must have the extension .groovy, not "
				+ extension;
		LOGGER.error(msg);
		return "error: " + msg;
	}

	@Override
	public List<String> complete(final List<String> args, final int pos) {
		ArrayList<String> suggestions = new ArrayList<String>();
		ArrayList<String> s = new ArrayList<String>();
		FileNameCompletor completor = new FileNameCompletor();
		String input = Joiner.on(" ").join(args);
		completor.complete(input, pos, suggestions);
		if (suggestions.isEmpty()) {
			return suggestions;
		}
		int lastSlash = input.lastIndexOf(File.separator);
		if (lastSlash > -1) {
			String prefix = input.substring(0, lastSlash);
			String suffix = input.substring(lastSlash + 1);
			String commonPrefix = findLongestCommonPrefix(suggestions);
			if (commonPrefix.isEmpty() || commonPrefix.equals(suffix)) {
				for (String string : suggestions) {
					s.add(string);
				}
			} else {
				s.add(prefix + File.separator + commonPrefix);
			}
		}
		if (s.size() == 1) {
			return Collections.singletonList("run " + s.get(0));
		}

		return s;
	}
}
