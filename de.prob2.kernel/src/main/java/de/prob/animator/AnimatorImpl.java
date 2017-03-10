package de.prob.animator;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.inject.Inject;

import de.prob.animator.command.AbstractCommand;
import de.prob.animator.command.ComposedCommand;
import de.prob.animator.command.GetErrorsCommand;
import de.prob.cli.ProBInstance;
import de.prob.exception.CliError;
import de.prob.exception.ProBError;
import de.prob.statespace.AnimationSelector;

class AnimatorImpl implements IAnimator {

	private static int counter = 0;
	private final String id = "animator" + counter++;

	private final ProBInstance cli;
	private final Logger logger = LoggerFactory.getLogger(AnimatorImpl.class);
	private final CommandProcessor processor;
	private final GetErrorsCommand getErrors;
	public static final boolean DEBUG = false;
	private final AnimationSelector animations;
	private boolean busy = false;

	@Inject
	public AnimatorImpl(@Nullable final ProBInstance cli, final CommandProcessor processor,
			final GetErrorsCommand getErrors, final AnimationSelector animations) {
		this.cli = cli;
		this.processor = processor;
		this.getErrors = getErrors;
		this.animations = animations;
		processor.configure(cli);
	}

	@SuppressWarnings("unused")
	@Override
	public synchronized void execute(final AbstractCommand command) {
		if (cli == null) {
			logger.error("Probcli is missing. Try \"upgrade\".");
			throw new CliError("no cli found");
		}

		if (DEBUG && !command.getSubcommands().isEmpty()) {
			List<AbstractCommand> cmds = command.getSubcommands();
			for (AbstractCommand abstractCommand : cmds) {
				execute(abstractCommand);
			}
		}

		if (command.blockAnimator()) {
			startTransaction();
		}
		do {
			IPrologResult result = processor.sendCommand(command);
			List<String> errormessages = getErrors();

			if (result instanceof YesResult && errormessages.isEmpty()) {
				try {
					command.processResult(((YesResult) result).getBindings());
				} catch (Exception e) {
					String message = "Exception of type " + e.getClass() + " was thrown when executing "
							+ command.getClass().getSimpleName() + ". Message was: " + e.getMessage();
					logger.error(message, e);
					// FIXME kill all Clis?
					if (!System.getProperty("dontexit").equals("true")) {
						System.exit(-1);
					} else {
						throw new CliError("exception during result handling", e);
					}
				}
			} else {
				command.processErrorResult(result, errormessages);
			}
		} while (!command.isCompleted());
		if (command.blockAnimator()) {
			endTransaction();
		}
	}

	private synchronized List<String> getErrors() {
		IPrologResult errorresult = processor.sendCommand(getErrors);
		if (errorresult instanceof NoResult || errorresult instanceof InterruptedResult) {
			throw new ProBError("Get errors must be successful");
		} else if (errorresult instanceof YesResult) {
			getErrors.processResult(((YesResult) errorresult).getBindings());
			List<String> errors = getErrors.getErrors();
			if (errors.isEmpty())
				return Collections.emptyList();
			else {
				String msg = Joiner.on('\n').join(errors);
				logger.error("ProB raised exception(s):\n", msg);
				return errors;
			}
		}
		throw new ProBError("Unknown result type");
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(AnimatorImpl.class).addValue(cli).toString();
	}

	@Override
	public void execute(final AbstractCommand... commands) {
		execute(new ComposedCommand(commands));
	}

	@Override
	public void sendInterrupt() {
		logger.info("Sending an interrupt to the CLI");
		cli.sendInterrupt();
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

	@Override
	public void kill() {
		cli.shutdown();
	}

}
