package de.prob.webconsole;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Singleton;

import de.prob.webconsole.shellcommands.AbstractShellCommand;
import de.prob.webconsole.shellcommands.LoadCommand;

@Singleton
public class ShellCommands {

	private Map<String, AbstractShellCommand> magic = new HashMap<String, AbstractShellCommand>();

	public ShellCommands() {
		magic.put("load", new LoadCommand());
	}

	public List<String> getMagic(String text) {
		List<String> list = Arrays.asList(text.trim().split("\\s"));
		if (magic.containsKey(text.trim().split("\\s")[0]))
			return list;
		return Collections.emptyList();
	}

	public String perform(List<String> m) {
		return magic.get(m.get(0)).perform(m);
	}

	public List<String> complete(List<String> m, int pos) {
		List<String> args = m.subList(1, m.size());
		return magic.get(m.get(0)).complete(args, pos);
	}

}
