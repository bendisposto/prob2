import de.prob.animator.command.GetTransitionDiagramCommand
import de.prob.animator.domainobjects.ClassicalB
import de.prob.statespace.Trace

final s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
def t = new Trace(s)
t.getCurrentState().explore()

final cmd = new GetTransitionDiagramCommand(new ClassicalB("waiting"))
s.execute(cmd)

assert !cmd.getNodes().isEmpty()
assert !cmd.getEdges().isEmpty()

"All tests passed!"
