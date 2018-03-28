import de.prob.animator.command.ComputeCoverageCommand
import de.prob.statespace.Trace

final s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch") 
def h = new Trace(s)
h = h.add(0)
h = h.add(1)
h = h.add(2)
h.getCurrentState().explore()
final cmd = new ComputeCoverageCommand()
s.execute(cmd)
result = cmd.getResult()
assert result.getOps() == ["'INITIALISATION:1'","'nr_ready:2'","'new:5'","'del:1'","'ready:1'"]
assert result.getTotalNumberOfNodes() == 8
assert result.getTotalNumberOfTransitions() == 10
assert result.getNodes() == ["'deadlocked:0'","'invariant_violated:0'","'live:3'","'open:5'","'invariant_not_checked:5'","'total:8'"]
assert result.getUncovered() == ["swap"]

"ComputeCoverageCommand returns the expected result"
