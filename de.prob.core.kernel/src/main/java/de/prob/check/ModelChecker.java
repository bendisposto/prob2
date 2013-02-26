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

	public boolean cancel() {
		if (f != null) {
			return f.cancel(true);
		}
		return false;
	}

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

	public void start() {
		f = executor.submit(worker);
	}

	public boolean isDone() {
		return f == null ? null : f.isDone();
	}

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

		public Worker(final StateSpace s, final List<String> options) {
			this.s = s;
			this.options = options;
			this.last = s.getLastCalculatedTransitionId();
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
			if (!newOps.isEmpty()) {
				OpInfo lastOp = newOps.get(newOps.size() - 1);
				s.setLastCalculatedTransitionId(new BigInteger(lastOp.id));
				last = s.getLastCalculatedTransitionId();
			}
			return result;
		}

		private void addCheckedStates(final List<OpInfo> newOps) {
			HashMap<String, StateId> states = s.getStates();
			HashMap<String, OpInfo> ops = s.getOps();

			for (OpInfo opInfo : newOps) {
				if (!ops.containsKey(opInfo.id)) {
					String sK = opInfo.src;
					String dK = opInfo.dest;
					StateId src = states.get(sK);
					if (src == null) {
						src = new StateId(sK, s);
						s.addVertex(src);
						states.put(sK, src);
					}
					StateId dest = states.get(dK);
					boolean destNew = false;
					if (dest == null) {
						destNew = true;
						dest = new StateId(dK, s);
						s.addVertex(dest);
						states.put(dK, dest);
					}
					s.addEdge(src, dest, opInfo);
					ops.put(opInfo.id, opInfo);
				}
			}
			s.notifyStateSpaceChange();
		}

		public BigInteger getLast() {
			return last;
		}
	}

}
