package de.prob.animator.command;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.be4.classicalb.core.parser.analysis.prolog.NodeIdAssignment;
import de.be4.classicalb.core.parser.analysis.prolog.RecursiveMachineLoader;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.PrologTerm;

/**
 * Loads a Classical B machine that has already been parsed and put into a
 * Recursive Machine Loader
 * 
 * @author joy
 * 
 */
public class LoadBProjectCommand extends AbstractCommand {
	private static final String PROLOG_COMMAND_NAME = "load_classical_b_from_list_of_facts";
	Logger logger = LoggerFactory.getLogger(LoadBProjectCommand.class);
	private NodeIdAssignment nodeIdMapping;
	private final RecursiveMachineLoader rml;
	private File mainMachine;

	public LoadBProjectCommand(final RecursiveMachineLoader rml, File f) {
		this.rml = rml;
		this.mainMachine = f;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		pto.printAtom(mainMachine.getAbsolutePath());
		pto.openList();
		printLoadTerm(rml, pto);
		pto.closeList();
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
	}

	private void printLoadTerm(final RecursiveMachineLoader rml,
			IPrologTermOutput pto) {
		StructuredPrologOutput parserOutput = new StructuredPrologOutput();
		rml.printAsProlog(parserOutput);
		nodeIdMapping = rml.getNodeIdMapping();
		for (PrologTerm term : parserOutput.getSentences()) {
			pto.printTerm(term);
		}
	}

	public NodeIdAssignment getNodeIdMapping() {
		return nodeIdMapping;
	}
}