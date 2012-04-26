

package de.prob.scripting

import java.util.List

import org.codehaus.groovy.tools.shell.CommandSupport;
import org.codehaus.groovy.tools.shell.PShell;
import org.codehaus.groovy.tools.shell.Shell


class InteractiveCommand extends CommandSupport {

	protected InteractiveCommand(final Shell shell) {
		super(shell, "interactiveMode","i-mode");
	}

	@Override
	public Object execute(List args) {
		// binds the method "interactive" in the InteractiveObj to the command "interactive"
		((PShell) shell).add("interactive = interactiveObj.&interactive");
		return null;
	}
}
