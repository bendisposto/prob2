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
import de.prob.statespace.AnimationSelector;

class AnimatorImpl implements IAnimator {

	private static int counter = 0;
	private final String id = "animator" + counter++;

	private final ProBInstance cli;
	private final Logger logger = LoggerFactory.getLogger(AnimatorImpl.class);
	private final CommandProcessor processor;
	private final GetErrorsCommand getErrors;
	public static boolean DEBUG = false;
	private final AnimationSelector animations;
	private boolean busy = false;

	@Inject
	public AnimatorImpl(@Nullable final ProBInstance cli,
			final CommandProcessor processor, final GetErrorsCommand getErrors,
			final AnimationSelector animations) {
		this.cli = cli;
		this.processor = processor;
		this.getErrors = getErrors;
		this.animations = animations;
		processor.configure(cli);
	}

	@Override
	public synchronized void execute(final AbstractCommand command) {

		do {
			if (cli == null) {
				logger.error("Probcli is missing. Try \"upgrade\".");
				throw new CliError("no cli found");
			}
			ISimplifiedROMap<String, PrologTerm> bindings = null;
			String errormessages = null;
			try {
				if (DEBUG && !command.getSubcommands().isEmpty()) {
					List<AbstractCommand> cmds = command.getSubcommands();
					for (AbstractCommand abstractCommand : cmds) {
						execute(abstractCommand);
					}
				} else {
					bindings = processor.sendCommand(command);
				}
			} finally {
				errormessages = getErrors();
			}
			if (errormessages == null && bindings != null)
				command.processResult(bindings);
			else
				command.processErrorResult(bindings, errormessages);
			
		} while (!command.isCompleted());
	}

	private synchronized String getErrors() {
		ISimplifiedROMap<String, PrologTerm> errorbindings;
		List<String> errors;
		errorbindings = processor.sendCommand(getErrors);
		getErrors.processResult(errorbindings);
		errors = getErrors.getErrors();
		if (errors != null && !errors.isEmpty()) {
			String msg = Joiner.on('\n').join(errors);
			logger.error("ProB raised exception(s):\n", msg);
			return msg;
		}
		return null;
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

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void startTransaction() {
		busy = true;
		animations.notifyAnimatorStatus(id, busy);
	}

	@Override
	public void endTransaction() {
		busy = false;
		animations.notifyAnimatorStatus(id, busy);
	}

	@Override
	public boolean isBusy() {
		return busy;
	}

}
