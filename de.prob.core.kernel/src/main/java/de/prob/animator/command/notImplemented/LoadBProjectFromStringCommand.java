package de.prob.animator.command.notImplemented;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.analysis.prolog.NodeIdAssignment;
import de.be4.classicalb.core.parser.analysis.prolog.RecursiveMachineLoader;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.ProBException;
import de.prob.animator.command.ICommand;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class LoadBProjectFromStringCommand implements ICommand {

	private final String model;
	Logger logger = LoggerFactory.getLogger(LoadBProjectFromStringCommand.class);
	
	public LoadBProjectFromStringCommand(String s) {
		this.model = s;
	}
	
	@Override
	public void writeCommand(IPrologTermOutput pto) throws ProBException {
		pto.openTerm("load_classical_b");
//		pto.printTerm(getLoadTerm(model));
		pto.printVariable("Errors");
		pto.closeTerm();
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings)
			throws ProBException {
		// TODO Auto-generated method stub

	}
	
//	private PrologTerm getLoadTerm(final String model) throws ProBException {
//		StructuredPrologOutput parserOutput = null;
//		Start ast = parse(model);
//]
////		try {
////			rml.loadAllMachines(model, ast, null, bparser.getDefinitions());
////		} catch (BException e) {
////			logger.error("Parser Error. {}.", e.getLocalizedMessage());
////			logger.debug("Details", e);
////			throw new ProBException();
////		}
//		parserOutput = new StructuredPrologOutput();
//		logger.trace("Done with parsing '{}'", model);
//		nodeIdMapping = rml.getNodeIdMapping();
//		return collectSentencesInList(parserOutput);
//	}
//
//	private Start parse(final String model)
//			throws ProBException {
//		logger.trace("Parsing file from String",model);
//
//		Start ast = null;
//		try {
//			ast = BParser.parse(model);
//		} catch (BException e) {
//			logger.error("Parser Error. {}.", e.getLocalizedMessage());
//			logger.debug("Details", e);
//			throw new ProBException();
//		}
//		return ast;
//	}
//
//	private PrologTerm collectSentencesInList(final StructuredPrologOutput po) {
//		List<PrologTerm> parserOutput = po.getSentences();
//		StructuredPrologOutput loadCommandTerm = new StructuredPrologOutput();
//		loadCommandTerm.openList();
//		// skip the first two sentences (parser version + filepath)
//		for (int i = 2; i < parserOutput.size(); i++) {
//			CompoundPrologTerm prologTerm = (CompoundPrologTerm) parserOutput
//					.get(i);
//			loadCommandTerm.printTerm(prologTerm.getArgument(1));
//		}
//		loadCommandTerm.closeList();
//		loadCommandTerm.fullstop();
//
//		PrologTerm result = loadCommandTerm.getSentences().get(0);
//		return result;
//	}
//
//	public NodeIdAssignment getNodeIdMapping() {
//		return nodeIdMapping;
//	}

}
