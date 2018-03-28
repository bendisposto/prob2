import de.prob.animator.command.SimpleTextCommand

final s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")

final cmd = new SimpleTextCommand("compute_operations_for_state(root,Transitions)","Transitions")
s.execute(cmd)
assert cmd.getResults() == ["op"]

"possible to create a simple text command"
