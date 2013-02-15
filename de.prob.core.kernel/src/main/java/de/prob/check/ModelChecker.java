package de.prob.check;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import de.prob.animator.command.ConsistencyCheckingCommand;
import de.prob.animator.domainobjects.OpInfo;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpace;

public class ModelChecker {

	private final Watcher watcher;

	public ModelChecker(final StateSpace s) {
		Worker worker = new Worker(s);
		watcher = new Watcher(worker);
	}

	public ModelChecker(final StateSpace s, final List<String> options) {
		Worker worker = new Worker(s, options);
		watcher = new Watcher(worker);
	}

	public boolean cancel() {
		return watcher.cancel(true);
	}

	public ModelCheckingResult getResult() throws InterruptedException,
			ExecutionException {
		return watcher.get();
	}

	public void start() {
		watcher.run();
	}

	private class Worker implements Callable<ModelCheckingResult> {

		private final StateSpace s;
		private final List<String> options;
		private BigInteger last;

		public Worker(final StateSpace s) {
			this.s = s;
			options = new ArrayList<String>();
			options.add("find_deadlocks");
			options.add("find_invariant_violations");
			last = new BigInteger("-1");
		}

		public Worker(final StateSpace s, final List<String> options) {
			this.s = s;
			this.options = options;
			last = new BigInteger("-1");
		}

		@Override
		public ModelCheckingResult call() throws Exception {
			boolean abort = false;
			ModelCheckingResult res = null;
			while (!abort) {
				res = do_model_checking_step();
				abort = res.isAbort();
			}
			return res;
		}

		private ModelCheckingResult do_model_checking_step() {
			ConsistencyCheckingCommand cmd = new ConsistencyCheckingCommand(
					500, options, last);
			s.execute(cmd);
			ModelCheckingResult result = cmd.getResult();
			List<OpInfo> newOps = cmd.getNewOps();
			addCheckedStates(newOps);
			OpInfo lastOp = newOps.get(newOps.size() - 1);
			last = new BigInteger(lastOp.id);
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
					if (dest == null) {
						dest = new StateId(dK, s);
						s.addVertex(dest);
						states.put(dK, dest);
					}
					s.addEdge(src, dest, opInfo);
					ops.put(opInfo.id, opInfo);
				}
			}

		}
	}

	private class Watcher extends FutureTask<ModelCheckingResult> {

		public Watcher(final Callable<ModelCheckingResult> callable) {
			super(callable);
		}
	}

}
