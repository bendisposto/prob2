package de.prob.check;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.prob.animator.command.ModelCheckingJob;
import de.prob.statespace.StateSpace;
import de.prob.web.views.ModelCheckingUI;

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

	private final Worker worker;
	private final ExecutorService executor;
	private Future<IModelCheckingResult> f;
	private final String jobId;
	private final StateSpace stateSpace;

	public ModelChecker(final StateSpace s) {
		this(s, ModelCheckingOptions.DEFAULT);
	}

	public ModelChecker(final StateSpace s, final ModelCheckingOptions options) {
		this(s, options, null);
	}

	public ModelChecker(final StateSpace s, final ModelCheckingOptions options,
			final ModelCheckingUI ui) {
		stateSpace = s;
		jobId = "mc" + counter++;
		worker = new Worker(s, options, ui, jobId);
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
		if (f != null) {
			return f.cancel(true);
		}
		return false;
	}

	/**
	 * @return the {@link ModelCheckingResult} of the model checking process if
	 *         the model checking has been started.
	 */
	public IModelCheckingResult getResult() {
		try {
			if (f != null) {
				return f.get();
			}
		} catch (InterruptedException e) {
			f.cancel(true);
		} catch (ExecutionException e) {
			launderThrowable(e.getCause());
		} catch (CancellationException e) {
			return worker.getResult();
		}
		return null;
	}

	/**
	 * Starts the model checking process. Creates a {@link Future} of type
	 * {@link ModelCheckingResult} by submitting the {@link ModelChecker#worker}
	 * to a single threaded {@link ExecutorService}
	 */
	public void start() {
		f = executor.submit(worker);
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
		return f.isCancelled();
	}

	/**
	 * @return the state space object that is bound to this {@link ModelChecker}
	 *         instance
	 */
	public StateSpace getStateSpace() {
		return stateSpace;
	}

	public static RuntimeException launderThrowable(final Throwable t) {
		if (t instanceof RuntimeException) {
			return (RuntimeException) t;
		} else if (t instanceof Error) {
			throw (Error) t;
		} else {
			throw new IllegalStateException("Not unchecked", t);
		}
	}

	private class Worker implements Callable<IModelCheckingResult> {

		private final StateSpace s;
		private final ModelCheckingJob job;
		private final ModelCheckingUI ui;

		/**
		 * implements {@link Callable}. When called, the Worker performs model
		 * checking until an result is found or until the user cancels the
		 * operation.
		 * 
		 * @param s
		 *            {@link StateSpace} object in which to perform the model
		 *            checking
		 * @param options
		 *            {@link ModelCheckingOptions} specified by user
		 * @param ui
		 *            {@link ModelCheckingUI} if the UI should be informed of
		 *            updates. Otherwise, null.
		 * @param jobId
		 */
		public Worker(final StateSpace s, final ModelCheckingOptions options,
				final ModelCheckingUI ui, final String jobId) {
			this.s = s;
			this.ui = ui;
			job = new ModelCheckingJob(s, options, jobId, ui);
		}

		@Override
		public IModelCheckingResult call() throws Exception {
			s.execute(job);
			IModelCheckingResult result = job.getResult();
			if (ui != null) {
				ui.isFinished(jobId, result);
			}
			return result;
		}

		public IModelCheckingResult getResult() {
			return job.getResult();
		}

	}

}
