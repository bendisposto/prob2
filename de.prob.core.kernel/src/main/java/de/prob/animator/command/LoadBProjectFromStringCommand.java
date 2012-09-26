package de.prob.animator.command;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.analysis.prolog.NodeIdAssignment;
import de.be4.classicalb.core.parser.analysis.prolog.RecursiveMachineLoader;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Loads a Classical B Machine from a string
 * 
 * @author joy
 * 
 */
public class LoadBProjectFromStringCommand implements ICommand {

	private final PrologTerm model;
	Logger logger = LoggerFactory
			.getLogger(LoadBProjectFromStringCommand.class);
	private NodeIdAssignment nodeIdMapping;

	public LoadBProjectFromStringCommand(final String s) throws BException {
		this.model = getLoadTerm(s);
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("load_classical_b");
		pto.printTerm(model);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
	}

	private PrologTerm getLoadTerm(final String model) throws BException {

		BParser bparser = new BParser();
		Start ast = parseString(model, bparser);
		final RecursiveMachineLoader rml = new RecursiveMachineLoader(".",
				bparser.getContentProvider());
		rml.loadAllMachines(new File(""), ast, null, bparser.getDefinitions(),
				bparser.getPragmas());

		StructuredPrologOutput parserOutput = new StructuredPrologOutput();
		logger.trace("Done with parsing '{}'", model);
		rml.printAsProlog(parserOutput);
		nodeIdMapping = rml.getNodeIdMapping();
		return collectSentencesInList(parserOutput);
	}

	private Start parseString(final String model, final BParser bparser)
			throws BException {
		logger.trace("Parsing file from String", model);

		Start ast = null;
		ast = bparser.parse(model, false);
		return ast;
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
