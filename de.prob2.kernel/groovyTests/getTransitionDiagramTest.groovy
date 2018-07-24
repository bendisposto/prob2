import java.nio.file.Paths

import de.prob.animator.command.GetTransitionDiagramCommand
import de.prob.animator.domainobjects.ClassicalB
import de.prob.statespace.Trace

final s = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString())
def t = new Trace(s)
t.currentState.explore()

final cmd = new GetTransitionDiagramCommand(new ClassicalB("waiting"))
s.execute(cmd)

assert !cmd.nodes.isEmpty()
assert !cmd.edges.isEmpty()

"All tests passed!"
