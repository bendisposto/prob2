package de.prob.scripting;

import java.util.List;

import org.codehaus.groovy.tools.shell.CommandSupport;
import org.codehaus.groovy.tools.shell.PShell;
import org.codehaus.groovy.tools.shell.Shell;

public class UpgradeCommand extends CommandSupport {

	protected UpgradeCommand(final Shell shell) {
		super(shell, "upgrade", "\\u");
	}

	@Override
	public Object execute(@SuppressWarnings("rawtypes") final List args) {
		Api api = ((PShell) shell).getApi();
		System.out.println(api.upgrade());
		return null;
	}

}
