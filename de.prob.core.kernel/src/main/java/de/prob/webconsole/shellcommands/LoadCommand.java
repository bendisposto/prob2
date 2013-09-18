package de.prob.webconsole.shellcommands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jline.FileNameCompletor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import de.prob.model.representation.AbstractModel;
import de.prob.scripting.Api;
import de.prob.webconsole.GroovyExecution;

/**
 * @author joy
 * 
 *         In the groovy console, provides the user with the command "load".
 *         This takes one parameter, which is the path to a valid B, Event-B, or
 *         CSP specification. This loads the specification using the
 *         {@link Api#b_load(String)}, {@link Api#csp_load(String)} or
 *         {@link Api#eb_load(String)} methods, and saves the resulting
 *         {@link AbstractModel} in a variable model_N, where N is a variable
 *         number generated from the {@link GroovyExecution} engine.
 * 
 */
public class LoadCommand extends AbstractShellCommand {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(LoadCommand.class);

	@Override
	public Object perform(final List<String> m, final GroovyExecution exec)
			throws IOException {

		if (m.size() != 2) {
			String msg = "Load command takes exactly one parameter, a filename.";
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

		if (lastDot < 0) {
			String msg = "Unknown filetype";
			LOGGER.error(msg);
			return "error: " + msg;
		}

		String extension = filename.substring(lastDot, filename.length());

		String name = exec.freshVar("model_");
		String command = "";
		if (extension.equals(".mch") || extension.equals(".ref")
				|| extension.equals(".imp")) {
			command = name + " = api.b_load('" + filename + "')";
		}
		if (extension.equals(".eventb")) {
			command = name + " = api.eb_load('" + filename + "')";
		}
		if (extension.equals(".csp")) {
			command = name + " = api.csp_load('" + filename + "')";
		}
		if (command != "") {
			exec.evaluate(command);
			Object model = exec.getBindings().getVariable(name);
			if (model != null) {
				return name + " = " + model;
			}
			return model;
		}
		String msg = "Unknown filetype: " + extension;
		LOGGER.error(msg);
		return "error: " + msg;
	}

	@Override
	public List<String> complete(final List<String> args, final int pos) {
		if (pos < 0) {
			return Collections.emptyList();
		}
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
			return Collections.singletonList("load " + s.get(0));
		}

		return s;
	}
}
