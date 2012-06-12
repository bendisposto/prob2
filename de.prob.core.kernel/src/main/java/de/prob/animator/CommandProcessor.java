package de.prob.animator;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;

import de.prob.ProBException;
import de.prob.animator.command.ICommand;
import de.prob.cli.ProBInstance;
import de.prob.core.sablecc.node.Start;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ProBResultParser;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.PrologTermStringOutput;
import de.prob.prolog.term.PrologTerm;

class CommandProcessor {

	private ProBInstance cli;
	private Logger logger;

	public ISimplifiedROMap<String, PrologTerm> sendCommand(
			final ICommand command) {

		PrologTermStringOutput pto = new PrologTermStringOutput();
		command.writeCommand(pto);
		pto.printAtom("true");
		final String query = pto.fullstop().toString();

		String result = cli.send(query);

		Map<String, PrologTerm> bindings = Collections.emptyMap();
		try {
			final Start ast = parseResult(result);
			bindings = BindingGenerator.createBindingMustNotFail(query, ast);
		} catch (ResultParserException e) {
			logger.error("Non well-formed answer '{}'", result);
			throw new ProBException(e);
		}
		return new SimplifiedROMap<String, PrologTerm>(bindings);
	}

	private Start parseResult(final String input) {
		if (input == null)
			return null;
		else
			return ProBResultParser.parse(input);
	}

	public void configure(final ProBInstance cli, final Logger logger) {
		this.cli = cli;
		this.logger = logger;
	}

}
