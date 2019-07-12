package de.prob.animator;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CommandProcessor {

	private ProBInstance cli;

	private final Logger logger = LoggerFactory.getLogger(CommandProcessor.class);

	private static String shorten(final String s) {
		final String shortened = s.length() <= 200 ? s : (s.substring(0, 200) + "...");
		return shortened.endsWith("\n") ? shortened.substring(0, shortened.length()-1) : shortened;
	}

	public IPrologResult sendCommand(final AbstractCommand command) {

		String query;
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
// comment out: 
// de.prob.cli.ProBConnection.send(ProBConnection.java:55) already logs
// 		if (logger.isDebugEnabled()) {
// 			logger.debug(shorten(query));
// 		}
		String result = cli.send(query);

		final Start ast = parseResult(result);
		IPrologResult extractResult = extractResult(ast);
		if (logger.isDebugEnabled()) {
			logger.debug(shorten(extractResult.toString()));
		}
		return extractResult;
	}

	private IPrologResult extractResult(final Start ast) {
		PResult topnode = ast.getPResult();
		if (topnode instanceof ANoResult) {
			return new NoResult();
		} else if (topnode instanceof AInterruptedResult) {
			return new InterruptedResult();
		} else if (topnode instanceof AYesResult) {
			Map<String, PrologTerm> binding = BindingGenerator.createBinding(ast);
			return new YesResult(new SimplifiedROMap<String, PrologTerm>(binding));
		} else if (topnode instanceof AExceptionResult) {
			AExceptionResult r = (AExceptionResult) topnode;
			String message = r.getString().getText();
			throw new ProBError(message);
		} else {
			throw new ProBError("unknown prolog result " + ast);
		}
	}

	private Start parseResult(final String input) {
		return ProBResultParser.parse(input);
	}

	public void configure(final ProBInstance cli) {
		this.cli = cli;
	}

}
