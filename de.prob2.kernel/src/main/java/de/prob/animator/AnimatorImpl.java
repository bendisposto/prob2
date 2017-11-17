package de.prob.animator;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;

import com.google.inject.Inject;

import de.prob.animator.command.AbstractCommand;
import de.prob.animator.command.ComposedCommand;
import de.prob.animator.command.GetErrorsCommand;
import de.prob.animator.command.GetTotalNumberOfErrorsCommand;
import de.prob.cli.ProBInstance;
import de.prob.exception.CliError;
import de.prob.exception.ProBError;
import de.prob.statespace.AnimationSelector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
			logger.trace("Blocking animator");
			startTransaction();
		}
		logger.trace("Starting execution of {}", command);
		do {
			IPrologResult result = processor.sendCommand(command);
			List<String> errormessages = getErrors();

			if (result instanceof YesResult && errormessages.isEmpty()) {
				logger.trace("Execution successful, processing result");
				try {
					command.processResult(((YesResult) result).getBindings());
				} catch (RuntimeException e) {
					this.kill();
					throw new CliError("Exception while processing command result", e);
				}
			} else {
				logger.trace("Execution unsuccessful, processing error");
				command.processErrorResult(result, errormessages);
			}
			logger.trace("Executed {} (completed: {}, interrupted: {})", command, command.isCompleted(), command.isInterrupted());
			
			if (!command.isCompleted() && Thread.currentThread().isInterrupted()) {
				logger.info("Stopping execution of {} because this thread was interrupted", command);
				break;
			}
		} while (!command.isCompleted());
		logger.trace("Done executing {}", command);
		if (command.blockAnimator()) {
			endTransaction();
			logger.trace("Unblocked animator");
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

	@Override
	public long getTotalNumberOfErrors() {
		GetTotalNumberOfErrorsCommand command = new GetTotalNumberOfErrorsCommand();
		execute(command);
		return command.getTotalNumberOfErrors().longValue();
	}

}
