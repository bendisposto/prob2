package de.prob.check;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.prob.animator.command.ComputeCoverageCommand;
import de.prob.animator.command.ComputeCoverageCommand.ComputeCoverageResult;
import de.prob.statespace.StateSpace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ModelChecker is a thread safe encapsulation of the model checking
 * process. It uses the {@link Callable} and {@link Future} objects to make a
 * thread that can be started, executed, and cancelled.
 *
 * @author joy
 *
 */
public class ModelChecker {

	private static int counter = 0;
	private static final String JOBPREFIX = "mc";

	public static String generateJobId() {
		return JOBPREFIX + counter++;
	}
	private Logger logger = LoggerFactory.getLogger(ModelChecker.class);

	private final ExecutorService executor;
	private Future<IModelCheckingResult> f;
	private final String jobId;
	private final StateSpace stateSpace;
	private final IModelCheckJob job;

	public ModelChecker(final IModelCheckJob job) {
		this.job = job;
		jobId = job.getJobId();
		stateSpace = job.getStateSpace();
		executor = Executors.newSingleThreadExecutor();
	}

	public String getJobId() {
		return jobId;
	}

	/**
	 * @return result of the {@link Future#cancel(boolean)} with value true if
	 *         the future has been created. Otherwise false.
	 */
	public boolean cancel() {
		if (!isDone()) {
			stateSpace.sendInterrupt();
			return f.cancel(true);
		}
		return false;
	}

	/**
	 * @return the {@link IModelCheckingResult} of the model checking process if
	 *         the model checking has been started.
	 */
	public IModelCheckingResult getResult() {
		try {
			if (f != null) {
				return f.get();
			}
		} catch (InterruptedException e) {
			logger.debug("Interrupted",e);
			f.cancel(true);
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			logger.debug("Execution failed",e);
			launderThrowable(e.getCause());
		} catch (CancellationException e) {
			logger.debug("Cancellation",e);
			return job.getResult();
		}
		return null;
	}

	/**
	 * Starts the model checking process. Creates a {@link Future} of type
	 * {@link IModelCheckingResult} by submitting the {@link ModelChecker#job}
	 * to a single threaded {@link ExecutorService}
	 */
	public void start() {
		f = executor.submit(job);
	}

	/**
	 * @return true, if the job has been started. Otherwise, false.
	 */
	public boolean isStarted() {
		return f != null;
	}

	/**
	 * @return true, if the calculation is finished. Otherwise, false.
	 */
	public boolean isDone() {
		return f != null && f.isDone();
	}

	/**
	 * @return {@link Future#isCancelled()}
	 */
	public boolean isCancelled() {
		if (f == null) {
			return false;
		}
		return f.isCancelled();
	}

	/**
	 * @return the state space object that is bound to this {@link ModelChecker}
	 *         instance
	 */
	public StateSpace getStateSpace() {
		return stateSpace;
	}

	public ComputeCoverageResult getCoverage() {
		ComputeCoverageCommand cmd = new ComputeCoverageCommand();
		stateSpace.execute(cmd);
		return cmd.getResult();
	}

	public static RuntimeException launderThrowable(final Throwable t) {
		if (t instanceof RuntimeException) {
			throw (RuntimeException) t;
		} else if (t instanceof Error) {
			throw (Error) t;
		} else {
			throw new IllegalStateException("Not unchecked", t);
		}
	}

}
