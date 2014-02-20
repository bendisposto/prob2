package de.prob.animator.command;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.analysis.prolog.RecursiveMachineLoader;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

/**
 * Loads a Classical B Machine from a string
 * 
 * @author joy
 * 
 */
public class LoadBProjectFromStringCommand extends AbstractCommand {

	Logger logger = LoggerFactory
			.getLogger(LoadBProjectFromStringCommand.class);
	private LoadBProjectCommand loadBProjectCommand;

	public LoadBProjectFromStringCommand(final String s) throws BException {
		RecursiveMachineLoader rml = getLoader(s);
		loadBProjectCommand = new LoadBProjectCommand(rml, new File(
				"from_string"));
	}

	private RecursiveMachineLoader getLoader(final String model)
			throws BException {

		BParser bparser = new BParser();
		Start ast = parseString(model, bparser);
		final RecursiveMachineLoader rml = new RecursiveMachineLoader(".",
				bparser.getContentProvider());
		rml.loadAllMachines(new File(""), ast, null, bparser.getDefinitions(),
				bparser.getPragmas());
		return rml;

	}

	private Start parseString(final String model, final BParser bparser)
			throws BException {
		logger.trace("Parsing file from String", model);

		Start ast = null;
		ast = bparser.parse(model, false);
		return ast;
	}

	@Override
	public void writeCommand(IPrologTermOutput pto) {
		loadBProjectCommand.writeCommand(pto);
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		loadBProjectCommand.processResult(bindings);
	}

}
