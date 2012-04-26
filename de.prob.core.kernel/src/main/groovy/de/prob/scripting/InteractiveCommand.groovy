

package de.prob.scripting

import de.prob.statespace.StateSpace
import java.util.List

import org.codehaus.groovy.tools.shell.CommandSupport;
import org.codehaus.groovy.tools.shell.PShell;
import org.codehaus.groovy.tools.shell.Shell


class InteractiveCommand extends CommandSupport {

	protected InteractiveCommand(final Shell shell) {
		super(shell, "interactive","i-mode");
	}

	@Override
	public Object execute(List args) {
		PShell newShell = (PShell) shell;
		Api api = newShell.getApi();
		StateSpace s = api.getStatespace();
		newShell.addVariable("statespace", s);
		def opNames = [
			"nr_ready",
			"del",
			"ready",
			"swap"
		];

		opNames.each {
			newShell.add("${it}={ String pred -> statespace.stepWithOp('${it}',pred)}")
		}
		return null;
	}
}
