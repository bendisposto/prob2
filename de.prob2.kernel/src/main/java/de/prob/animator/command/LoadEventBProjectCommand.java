package de.prob.animator.command;

import de.prob.model.eventb.translate.EventBModelTranslator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class LoadEventBProjectCommand extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "load_event_b_project";
	private final EventBModelTranslator translator;

	public LoadEventBProjectCommand(final EventBModelTranslator translator) {
		this.translator = translator;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		translator.printProlog(pto);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
	}

}
