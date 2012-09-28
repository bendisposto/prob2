package de.prob.webconsole.shellcommands;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Joiner;

import de.prob.webconsole.GroovyExecution;

public abstract class AbstractShellCommand {
	public abstract Object perform(List<String> m, GroovyExecution exec) throws IOException;

	public List<String> complete(List<String> m, int pos) {
		String join = Joiner.on(" ").join(m);
		return Collections.singletonList(join);
	}
}
