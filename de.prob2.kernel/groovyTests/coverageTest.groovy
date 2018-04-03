import java.nio.file.Paths

import de.prob.animator.command.ComputeCoverageCommand
import de.prob.statespace.Trace

final s = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString())
def h = new Trace(s)
h = h.add(0)
h = h.add(1)
h = h.add(2)
h.currentState.explore()
final cmd = new ComputeCoverageCommand()
s.execute(cmd)
result = cmd.result
assert result.ops == ["'INITIALISATION:1'", "'nr_ready:2'", "'new:5'", "'del:1'", "'ready:1'"]
assert result.totalNumberOfNodes == 8
assert result.totalNumberOfTransitions == 10
assert result.nodes == ["'deadlocked:0'", "'invariant_violated:0'", "'live:3'", "'open:5'", "'invariant_not_checked:5'", "'total:8'"]
assert result.uncovered == ["swap"]

"ComputeCoverageCommand returns the expected result"
