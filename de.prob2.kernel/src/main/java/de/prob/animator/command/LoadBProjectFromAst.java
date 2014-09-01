package de.prob.animator.command;

import java.io.File;
import java.util.ArrayList;

import de.be4.classicalb.core.parser.CachingDefinitionFileProvider;
import de.be4.classicalb.core.parser.Definitions;
import de.be4.classicalb.core.parser.analysis.pragma.Pragma;
import de.be4.classicalb.core.parser.analysis.prolog.RecursiveMachineLoader;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class LoadBProjectFromAst extends AbstractCommand {

	private final LoadBProjectCommand loadCommand;

	public LoadBProjectFromAst(final Start ast) throws BException {
		RecursiveMachineLoader rml = getLoader(ast);
		loadCommand = new LoadBProjectCommand(rml, new File("from_string"));
	}

	private RecursiveMachineLoader getLoader(final Start ast) throws BException {
		final RecursiveMachineLoader rml = new RecursiveMachineLoader(".",
				new CachingDefinitionFileProvider());
		rml.loadAllMachines(new File(""), ast, null, new Definitions(),
				new ArrayList<Pragma>());
		return rml;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		loadCommand.writeCommand(pto);
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		loadCommand.processResult(bindings);
	}

}
