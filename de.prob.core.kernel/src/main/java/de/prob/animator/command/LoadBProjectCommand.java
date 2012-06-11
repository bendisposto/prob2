package de.prob.animator.command;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.be4.classicalb.core.parser.analysis.prolog.NodeIdAssignment;
import de.be4.classicalb.core.parser.analysis.prolog.RecursiveMachineLoader;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Loads a Classical B machine that has already been parsed and put into a
 * Recursive Machine Loader
 * 
 * @author joy
 * 
 */
public class LoadBProjectCommand implements ICommand {
	Logger logger = LoggerFactory.getLogger(LoadBProjectCommand.class);
	private NodeIdAssignment nodeIdMapping;
	private final RecursiveMachineLoader rml;

	public LoadBProjectCommand(final RecursiveMachineLoader rml) {
		this.rml = rml;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("load_classical_b");
		pto.printTerm(getLoadTerm(rml));
		pto.printVariable("Errors");
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		ListPrologTerm e = (ListPrologTerm) bindings.get("Errors");
		if (!e.isEmpty()) {
			for (PrologTerm prologTerm : e) {
				logger.error("Error from Prolog: '{}'", prologTerm);
			}
			throw new ProBException();
		}
	}

	private PrologTerm getLoadTerm(final RecursiveMachineLoader rml) {
		StructuredPrologOutput parserOutput = new StructuredPrologOutput();
		rml.printAsProlog(parserOutput);
		nodeIdMapping = rml.getNodeIdMapping();
		return collectSentencesInList(parserOutput);
	}

	private PrologTerm collectSentencesInList(final StructuredPrologOutput po) {
		List<PrologTerm> parserOutput = po.getSentences();
		StructuredPrologOutput loadCommandTerm = new StructuredPrologOutput();
		loadCommandTerm.openList();
		// skip the first two sentences (parser version + filepath)
		for (int i = 2; i < parserOutput.size(); i++) {
			CompoundPrologTerm prologTerm = (CompoundPrologTerm) parserOutput
					.get(i);
			loadCommandTerm.printTerm(prologTerm.getArgument(1));
		}
		loadCommandTerm.closeList();
		loadCommandTerm.fullstop();

		PrologTerm result = loadCommandTerm.getSentences().get(0);
		return result;
	}

	public NodeIdAssignment getNodeIdMapping() {
		return nodeIdMapping;
	}
}