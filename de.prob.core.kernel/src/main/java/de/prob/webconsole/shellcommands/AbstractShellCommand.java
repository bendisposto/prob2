package de.prob.webconsole.shellcommands;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Joiner;

public abstract class AbstractShellCommand {
	public abstract String perform(List<String> m);

	public List<String> complete(List<String> m, int pos) {
		String join = Joiner.on(" ").join(m);
		return Collections.singletonList(join);
	}
}
