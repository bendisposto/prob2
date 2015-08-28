package de.prob.animator.command;

import de.prob.animator.domainobjects.IBEvalElement;

public abstract class UnsatCoreBaseCommand extends AbstractCommand {

	protected IBEvalElement core;

	public IBEvalElement getCore() {
		return core;
	}

}
