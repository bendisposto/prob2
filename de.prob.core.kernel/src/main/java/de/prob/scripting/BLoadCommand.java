package de.prob.scripting;

import java.util.Arrays;
import java.util.List;

import jline.Completor;

import org.codehaus.groovy.tools.shell.CommandSupport;
import org.codehaus.groovy.tools.shell.PShell;
import org.codehaus.groovy.tools.shell.Shell;

public class BLoadCommand extends CommandSupport {

	protected BLoadCommand(final Shell shell) {
		super(shell, "b_load", "\\b");
	}

	@Override
	protected List<Completor> createCompletors() {
		return Arrays.asList((Completor) new FileCompletor());
	}

	@Override
	public Object execute(@SuppressWarnings("rawtypes") final List args) {
		Api api = ((PShell) shell).getApi();
		return api.b_load((String) args.get(0));
	}

}
