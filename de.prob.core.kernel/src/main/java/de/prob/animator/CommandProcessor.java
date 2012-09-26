package de.prob.animator;

import java.util.Collections;
import java.util.Map;

import de.prob.animator.command.ICommand;
import de.prob.animator.command.IRawCommand;
import de.prob.cli.ProBInstance;
import de.prob.core.sablecc.node.Start;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ProBResultParser;
import de.prob.prolog.output.PrologTermStringOutput;
import de.prob.prolog.term.PrologTerm;

class CommandProcessor {

	private ProBInstance cli;

	public ISimplifiedROMap<String, PrologTerm> sendCommand(
			final ICommand command) {

		String query = "";
		if (command instanceof IRawCommand) {
			query = ((IRawCommand) command).getCommand();
			if (!query.endsWith("."))
				query += ".";
		} else {
			PrologTermStringOutput pto = new PrologTermStringOutput();
			command.writeCommand(pto);
			pto.printAtom("true");
			query = pto.fullstop().toString();
		}

		String result = cli.send(query);

		Map<String, PrologTerm> bindings = Collections.emptyMap();
		final Start ast = parseResult(result);
		bindings = BindingGenerator.createBindingMustNotFail(query, ast);
		return new SimplifiedROMap<String, PrologTerm>(bindings);
	}

	private Start parseResult(final String input) {
		if (input == null)
			return null;
		else
			return ProBResultParser.parse(input);
	}

	public void configure(final ProBInstance cli) {
		this.cli = cli;
	}

}
