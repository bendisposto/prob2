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
import de.prob.webconsole.shellcommands.RunScriptCommand;

@Singleton
public class ShellCommands {

	private Map<String, AbstractShellCommand> magic = new HashMap<String, AbstractShellCommand>();

	@Inject
	public ShellCommands(Downloader d) {
		magic.put("load", new LoadCommand());
		magic.put("upgrade", d);
		magic.put("import", new ImportCommand());
		magic.put("run", new RunScriptCommand());
	}

	public List<String> getMagic(String text) {
		List<String> list1 = Arrays.asList(text.split("\\s"));
		List<String> list = new ArrayList<String>();
		for (String string : list1) {
			String trim = string.trim();
			if (!trim.isEmpty())
				list.add(trim);
		}
		if (magic.containsKey(text.trim().split("\\s")[0]))
			return list;
		return Collections.emptyList();
	}

	public String perform(List<String> m, GroovyExecution exec)
			throws IOException {
		Object result = magic.get(m.get(0)).perform(m, exec);
		return result == null ? "null" : result.toString();
	}

	public List<String> complete(List<String> m, int pos) {
		List<String> args = m.subList(1, m.size());
		return magic.get(m.get(0)).complete(args, pos);
	}

	public Set<String> getSpecialCommands() {
		return magic.keySet();
	}

}
