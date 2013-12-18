package de.prob.check;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.prob.animator.command.ModelCheckingCommand;
import de.prob.statespace.OpInfo;
import de.prob.statespace.StateId;
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

		private static final int TIME = 500;
		private final StateSpace s;
		private ModelCheckingOptions options;
		private long last;
		private IModelCheckingResult res;
		private final ModelCheckingUI ui;
		private final String id;

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
			this.options = options;
			this.ui = ui;
			id = jobId;
			last = s.getLastCalculatedStateId();
		}

		@Override
		public IModelCheckingResult call() throws Exception {
			boolean notFinished = true;
			while (notFinished) {
				if (Thread.interrupted()) {
					Thread.currentThread().interrupt();
					break;
				}
				res = do_model_checking_step();
				options = options.recheckExisting(false);
				notFinished = res instanceof NotYetFinished;
			}
			if (ui != null) {
				ui.isFinished(id, res);
			}
			return res;
		}

		public IModelCheckingResult getResult() {
			return res;
		}

		private IModelCheckingResult do_model_checking_step() {
			ModelCheckingCommand cmd = new ModelCheckingCommand(TIME, options,
					last);

			s.execute(cmd);
			IModelCheckingResult result = cmd.getResult();
			if (ui != null) {
				ui.updateStats(id, result);
			}

			List<OpInfo> newOps = cmd.getNewOps();
			addCheckedStates(newOps);
			return result;
		}

		private void addCheckedStates(final List<OpInfo> newOps) {
			HashMap<String, StateId> states = s.getStates();
			HashMap<String, OpInfo> ops = s.getOps();

			long i = s.getLastCalculatedStateId();

			List<OpInfo> toNotify = new ArrayList<OpInfo>();
			for (OpInfo opInfo : newOps) {
				if (!ops.containsKey(opInfo.id)) {
					toNotify.add(opInfo);
					String sK = opInfo.src;
					if (!sK.equals("root")) {
						int value = Integer.parseInt(sK);
						i = Math.max(value, i);
					}

					String dK = opInfo.dest;
					StateId src = states.get(sK);
					if (src == null) {
						src = new StateId(sK, s);
						// s.addVertex(src);
						states.put(sK, src);
					}
					StateId dest = states.get(dK);
					if (dest == null) {
						dest = new StateId(dK, s);
						// s.addVertex(dest);
						states.put(dK, dest);
					}
					s.addEdge(opInfo, src, dest);
					ops.put(opInfo.id, opInfo);
				}
			}
			s.updateLastCalculatedStateId(i);
			last = i;

			s.notifyStateSpaceChange(toNotify);
		}

	}

}
