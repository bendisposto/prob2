package de.prob.animator.command;

import java.io.File;

import de.be4.classicalb.core.parser.rules.RulesProject;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.PrologTerm;

public class LoadRulesProjectCommand extends AbstractCommand {
	private static final String PROLOG_COMMAND_NAME = "load_classical_b_from_list_of_facts";

	private final RulesProject project;
	private final File mainFile;

	public LoadRulesProjectCommand(RulesProject project, File f) {
		this.project = project;
		this.mainFile = f;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		pto.printAtom(mainFile.getAbsolutePath());
		pto.openList();
		printLoadTerm(pto);
		pto.closeList();
		pto.closeTerm();
	}

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		// There are no output variables.
	}

	private void printLoadTerm(IPrologTermOutput pto) {
		StructuredPrologOutput parserOutput = new StructuredPrologOutput();
		this.project.printProjectAsPrologTerm(parserOutput);
		for (PrologTerm term : parserOutput.getSentences()) {
			pto.printTerm(term);
		}
	}
}
