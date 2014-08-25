import java.awt.image.RescaleOp;

import de.prob.statespace.*
import de.prob.animator.command.*
import de.prob.check.*

m = api.b_load(dir+"/machines/scheduler.mch")
s = m as StateSpace
checker = new ModelChecker(new ConsistencyChecker(s))
checker.start();
res = checker.getResult()
assert res instanceof ModelCheckOk

cmd = new ApplySignatureMergeCommand([])
s.execute(cmd)
assert cmd.getStates().size() == 8
labels = cmd.getStates().collect { it.labels }
assert labels.size() == 8
assert labels.getClass() == ArrayList
assert labels.contains(["INITIALISATION"])
assert labels.contains(["new", "nr_ready"])
assert labels.contains(["del", "new", "nr_ready", "ready"])
assert labels.contains(["del", "nr_ready", "ready"])
assert labels.contains(["del", "new", "nr_ready", "ready", "swap"])
assert labels.contains(["del", "nr_ready", "ready", "swap"])
assert labels.contains(["nr_ready", "swap"])
assert labels.contains(["new", "nr_ready", "swap"])
assert cmd.getOps().size() == 32

cmd = new CalculateTransitionDiagramCommand("card(waiting)" as ClassicalB)
s.execute(cmd)
assert cmd.getStates().size() == 5
labels = cmd.getStates().collect { it.labels[0] }
assert labels.contains("card(waiting)")
assert labels.contains("3")
assert labels.contains("2")
assert labels.contains("1")
assert labels.contains("0")
assert cmd.getOps().size() == 13

s.animator.cli.shutdown();
"Reduction algorithms (signature merge and transition diagram) successfully applied to state space"