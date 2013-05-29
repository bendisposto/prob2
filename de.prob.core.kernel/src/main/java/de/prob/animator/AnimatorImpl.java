package de.prob.animator;

import java.util.List;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.inject.Inject;

import de.prob.animator.command.AbstractCommand;
import de.prob.animator.command.ComposedCommand;
import de.prob.animator.command.GetErrorsCommand;
import de.prob.cli.ProBInstance;
import de.prob.exception.CliError;
import de.prob.exception.ProBError;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.term.PrologTerm;

class AnimatorImpl implements IAnimator {

	private final ProBInstance cli;
	private final Logger logger = LoggerFactory.getLogger(AnimatorImpl.class);
	private final CommandProcessor processor;
	private final GetErrorsCommand getErrors;
	public static boolean DEBUG = false;

	@Inject
	public AnimatorImpl(@Nullable final ProBInstance cli,
			final CommandProcessor processor, final GetErrorsCommand getErrors) {
		this.cli = cli;
		this.processor = processor;
		this.getErrors = getErrors;
		processor.configure(cli);
	}

	@Override
	public synchronized void execute(final AbstractCommand command) {
		if (cli == null) {
			// System.out.println("Probcli is missing. Try \"upgrade\".");
			logger.error("Probcli is missing. Try \"upgrade\".");
			throw new CliError("no cli found");
		}
		ISimplifiedROMap<String, PrologTerm> bindings = null;
		try {
			if (DEBUG && !command.getSubcommands().isEmpty()) {
				List<AbstractCommand> cmds = command.getSubcommands();
				for (AbstractCommand abstractCommand : cmds) {
					execute(abstractCommand);
				}
			} else {
				bindings = processor.sendCommand(command);
				command.processResult(bindings);
			}
		} finally {
			getErrors();
		}
	}

	private synchronized void getErrors() {
		ISimplifiedROMap<String, PrologTerm> errorbindings;
		List<String> errors;
		errorbindings = processor.sendCommand(getErrors);
		getErrors.processResult(errorbindings);
		errors = getErrors.getErrors();
		if (errors != null && !errors.isEmpty()) {
			String msg = Joiner.on('\n').join(errors);
			logger.error("ProB raised exception(s):\n", msg);
			throw new ProBError(msg);
		}
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(AnimatorImpl.class).addValue(cli)
				.toString();
	}

	@Override
	public void execute(final AbstractCommand... commands) {
		execute(new ComposedCommand(commands));
	}

	@Override
	public void sendInterrupt() {
		cli.sendInterrupt();
	}

	public static void setDebug(final boolean debug) {
		DEBUG = debug;
	}

}
