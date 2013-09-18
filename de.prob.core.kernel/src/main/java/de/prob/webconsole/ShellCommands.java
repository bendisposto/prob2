package de.prob.webconsole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.scripting.Downloader;
import de.prob.webconsole.shellcommands.AbstractShellCommand;
import de.prob.webconsole.shellcommands.ImportCommand;
import de.prob.webconsole.shellcommands.LoadCommand;
import de.prob.webconsole.shellcommands.OpenCommand;
import de.prob.webconsole.shellcommands.RunScriptCommand;

@Singleton
public class ShellCommands {

	private final Map<String, AbstractShellCommand> magic = new HashMap<String, AbstractShellCommand>();

	@Inject
	public ShellCommands(final Downloader d) {
		magic.put("load", new LoadCommand());
		magic.put("upgrade", d);
		magic.put("import", new ImportCommand());
		magic.put("run", new RunScriptCommand());
		magic.put("open", new OpenCommand());
	}

	public List<String> getMagic(final String text) {
		List<String> list1 = Arrays.asList(text.split("\\s"));
		List<String> list = new ArrayList<String>();
		for (String string : list1) {
			String trim = string.trim();
			if (!trim.isEmpty()) {
				list.add(trim);
			}
		}
		if (magic.containsKey(text.trim().split("\\s")[0])) {
			return list;
		}
		return Collections.emptyList();
	}

	public String perform(final List<String> m, final GroovyExecution exec)
			throws IOException {
		Object result = magic.get(m.get(0)).perform(m, exec);
		return result == null ? "null" : result.toString();
	}

	/**
	 * Performs completion for an {@link AbstractShellCommand}
	 * 
	 * @param m
	 *            list containing command and parameters
	 * @param pos
	 *            position of the cursor
	 * @return {@link List} of possible completions for given command
	 */
	public List<String> complete(final List<String> m, final int pos) {
		List<String> args = m.subList(1, m.size());
		return magic.get(m.get(0)).complete(args, pos);
	}

	public Set<String> getSpecialCommands() {
		return magic.keySet();
	}

}
