package de.prob.animator.command;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
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
		long time = System.currentTimeMillis();
		ASTProlog astPrinter = new ASTProlog(pto, null);
		System.out
				.println("Create AST: " + (System.currentTimeMillis() - time));
		translator.printProlog(astPrinter, pto);
		System.out.println("Print Prolog: "
				+ (System.currentTimeMillis() - time));
		System.out.println();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
	}

}
