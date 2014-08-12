import de.prob.animator.domainobjects.*
import de.prob.statespace.*


m = api.b_load(dir+"/machines/scheduler.mch")
s = m as StateSpace

cmd = new SimpleTextCommand("compute_operations_for_state(root,Transitions)","Transitions")
s.execute(cmd)
assert cmd.getResults() == ["op"]


s.animator.cli.shutdown();
"possible to create a simple text command"