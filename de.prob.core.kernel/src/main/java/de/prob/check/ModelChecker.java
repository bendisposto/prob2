package de.prob.check;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.prob.animator.command.ConsistencyCheckingCommand;
import de.prob.animator.domainobjects.OpInfo;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpace;

/**
 * The ModelChecker is a thread safe encapsulation of the model checking
 * process. It uses the {@link Callable} and {@link Future} objects to make a
 * thread that can be started, executed, and cancelled.
 * 
 * @author joy
 * 
 */
public class ModelChecker {

	private final Worker worker;
	private final ExecutorService executor;
	private Future<ModelCheckingResult> f;

	public ModelChecker(final StateSpace s) {
		this(s, ConsistencyCheckingSearchOption.getDefaultOptions());
	}

	public ModelChecker(final StateSpace s, final List<String> options) {
		worker = new Worker(s, options);
		executor = Executors.newSingleThreadExecutor();
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
	public ModelCheckingResult getResult() {
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
	 * {@link ModelCheckingResult} by submitting the {@link Worker} to a single
	 * threaded {@link ExecutorService}
	 */
	public void start() {
		f = executor.submit(worker);
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

	public BigInteger getLastTransition() {
		return worker.getLast();
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

	private class Worker implements Callable<ModelCheckingResult> {

		private final StateSpace s;
		private final List<String> options;
		private BigInteger last;
		private ModelCheckingResult res;

		/**
		 * implements {@link Callable}. When called, the Worker performs model
		 * checking until an result is found or until the user cancels the
		 * operation.
		 * 
		 * @param s
		 *            {@link StateSpace} object in which to perform the model
		 *            checking
		 * @param options
		 */
		public Worker(final StateSpace s, final List<String> options) {
			this.s = s;
			this.options = options;
			last = s.getLastCalculatedStateId();
		}

		@Override
		public ModelCheckingResult call() throws Exception {
			boolean abort = false;
			while (!abort) {
				if (Thread.interrupted()) {
					Thread.currentThread().interrupt();
					break;
				}
				res = do_model_checking_step();
				options.remove(ConsistencyCheckingSearchOption.inspect_existing_nodes
						.name());
				abort = res.isAbort();
			}
			s.notifyStateSpaceChange();
			return res;
		}

		public ModelCheckingResult getResult() {
			return res;
		}

		private ModelCheckingResult do_model_checking_step() {
			ConsistencyCheckingCommand cmd = new ConsistencyCheckingCommand(
					500, options, last);

			s.execute(cmd);
			ModelCheckingResult result = cmd.getResult();
			List<OpInfo> newOps = cmd.getNewOps();
			addCheckedStates(newOps);
			return result;
		}

		private void addCheckedStates(final List<OpInfo> newOps) {
			HashMap<String, StateId> states = s.getStates();
			HashMap<String, OpInfo> ops = s.getOps();

			BigInteger i = s.getLastCalculatedStateId();

			for (OpInfo opInfo : newOps) {
				if (!ops.containsKey(opInfo.id)) {
					String sK = opInfo.src;
					if (!sK.equals("root")) {
						int value = Integer.parseInt(sK);
						if (value > i.intValue()) {
							i = new BigInteger(sK);
						}
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
			s.setLastCalculatedStateId(i);
			last = i;

			s.notifyStateSpaceChange();
		}

		public BigInteger getLast() {
			return last;
		}
	}

}
