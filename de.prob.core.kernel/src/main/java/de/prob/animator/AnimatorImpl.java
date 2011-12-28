package de.prob.animator;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.inject.Inject;

import de.prob.ProBException;
import de.prob.animator.command.ComposedCommand;
import de.prob.animator.command.GetErrorsCommand;
import de.prob.animator.command.ICommand;
import de.prob.cli.ProBInstance;
import de.prob.core.sablecc.node.Start;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ProBResultParser;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.PrologTermStringOutput;
import de.prob.prolog.term.PrologTerm;

class AnimatorImpl implements IAnimator {

	private final ProBInstance cli;
	private final Logger logger = LoggerFactory.getLogger(AnimatorImpl.class);

	@Inject
	public AnimatorImpl(final ProBInstance cli) {
		this.cli = cli;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.animator.IAnimator#execute(de.prob.animator.ICommand)
	 */
	@Override
	public void execute(final ICommand command) throws ProBException {
		GetErrorsCommand getErrors = new GetErrorsCommand();
		ComposedCommand cmds = new ComposedCommand(command, getErrors);
		ISimplifiedROMap<String, PrologTerm> bindings = null;

		List<String> errors = null;
		try {
			bindings = sendCommand(cmds);
			cmds.processResult(bindings);
			errors = getErrors.getErrors();
		} catch (RuntimeException e) {
			logger.error("Runtime error while executing query.", e);
			throw new ProBException();
		} finally {
			if (errors == null) {
				if (bindings == null) {
					// the exception occurred while sending the commands
					// launch another query to get errors
					bindings = sendCommand(getErrors);
					getErrors.processResult(bindings);
				} else {
					// we cannot call getErrors.processResult directly because
					// the wrapping ComposedCommand may have mapped the bindings
					cmds.reprocessResult(getErrors, bindings);
				}
				errors = getErrors.getErrors();
			}
			if (errors != null && !errors.isEmpty()) {
				logger.error("ProB raised exception(s):\n", Joiner.on('\n')
						.join(errors));
				throw new ProBException();
			}
		}
	}

	private ISimplifiedROMap<String, PrologTerm> sendCommand(
			final ICommand command) throws ProBException {

		PrologTermStringOutput pto = new PrologTermStringOutput();
		command.writeCommand(pto);
		final String query = pto.fullstop().toString();

		String result = cli.send(query);

		Map<String, PrologTerm> bindings = Collections.emptyMap();
		try {
			final Start ast = parseResult(result);
			bindings = BindingGenerator.createBindingMustNotFail(query, ast);
		} catch (ResultParserException e) {
			logger.error("Non well-formed answer '{}'", result);
		}
		return new SimplifiedROMap<String, PrologTerm>(bindings);
	}

	private Start parseResult(final String input) throws ProBException,
			ResultParserException {
		if (input == null)
			return null;
		else
			return ProBResultParser.parse(input);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(AnimatorImpl.class).addValue(cli)
				.toString();
	}

}
