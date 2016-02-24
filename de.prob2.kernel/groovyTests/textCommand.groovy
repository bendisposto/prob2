import de.prob.animator.domainobjects.*
import de.prob.statespace.*


s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")

cmd = new SimpleTextCommand("compute_operations_for_state(root,Transitions)","Transitions")
s.execute(cmd)
assert cmd.getResults() == ["op"]

"possible to create a simple text command"