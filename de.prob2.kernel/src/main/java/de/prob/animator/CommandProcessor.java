package de.prob.animator;

import java.util.Collections;
import java.util.Map;

import de.prob.animator.command.AbstractCommand;
import de.prob.animator.command.IRawCommand;
import de.prob.cli.ProBInstance;
import de.prob.core.sablecc.node.AExceptionResult;
import de.prob.core.sablecc.node.AInterruptedResult;
import de.prob.core.sablecc.node.ANoResult;
import de.prob.core.sablecc.node.AYesResult;
import de.prob.core.sablecc.node.PResult;
import de.prob.core.sablecc.node.Start;
import de.prob.exception.ProBError;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ProBResultParser;
import de.prob.prolog.output.PrologTermStringOutput;
import de.prob.prolog.term.PrologTerm;

class CommandProcessor {

	private ProBInstance cli;

	public IPrologResult sendCommand(final AbstractCommand command) {

		String query = "";
		if (command instanceof IRawCommand) {
			query = ((IRawCommand) command).getCommand();
			if (!query.endsWith(".")) {
				query += ".";
			}
		} else {
			PrologTermStringOutput pto = new PrologTermStringOutput();
			command.writeCommand(pto);
			pto.printAtom("true");
			query = pto.fullstop().toString();
		}

		String result = cli.send(query);

		Map<String, PrologTerm> bindings = Collections.emptyMap();
		final Start ast = parseResult(result);

		return extractResult(ast);
	}

	private IPrologResult extractResult(final Start ast) {
		PResult topnode = ast.getPResult();
		if (topnode instanceof ANoResult) {
			return new NoResult();
		} else if (topnode instanceof AInterruptedResult) {
			return new InterruptedResult();
		} else if (topnode instanceof AYesResult) {
			Map<String, PrologTerm> binding = BindingGenerator
					.createBinding(ast);
			return new YesResult(new SimplifiedROMap<String, PrologTerm>(
					binding));
		} else if (topnode instanceof AExceptionResult) {
			AExceptionResult r = (AExceptionResult) topnode;
			String message = r.getString().getText();
			throw new ProBError("Error while executing prolog. Message was: "
					+ message);
		}
		return null;
	}

	private Start parseResult(final String input) {
		if (input == null) {
			return null;
		} else {
			return ProBResultParser.parse(input);
		}
	}

	public void configure(final ProBInstance cli) {
		this.cli = cli;
	}

}
