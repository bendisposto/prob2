package de.prob.animator.command;

import de.prob.model.eventb.translate.EventBToPrologTranslator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class LoadEventBProjectCommand extends AbstractCommand {

	private final EventBToPrologTranslator translator;

	public LoadEventBProjectCommand(final EventBToPrologTranslator translator) {
		this.translator = translator;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		translator.printProlog(pto);
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
	}

}
