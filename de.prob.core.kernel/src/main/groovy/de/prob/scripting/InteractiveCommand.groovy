

package de.prob.scripting


import org.codehaus.groovy.tools.shell.CommandSupport;
import org.codehaus.groovy.tools.shell.PShell
import org.codehaus.groovy.tools.shell.Shell


class InteractiveCommand extends CommandSupport {

	protected InteractiveCommand(final Shell shell) {
		super(shell, "interactive","i-mode");
	}

	@Override
	public Object execute(List args) {
		if(args.isEmpty()) {
			return "Command Usage: interactive <StateSpace>\nPlease include a reference to a StateSpace object as an argument"
		}
		PShell newShell = (PShell) shell
		String stateSpace = args.get(0);
		def opNames = [
			"nr_ready",
			"del",
			"ready",
			"swap"
		];

		opNames.each {
			newShell.add("${it}={ String pred -> ${stateSpace}.stepWithOp('${it}',pred)}")
		}
		return "-+-commands successfully added-+-";
	}
}
