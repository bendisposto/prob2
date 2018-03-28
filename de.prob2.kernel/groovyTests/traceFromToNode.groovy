import de.prob.animator.command.FindTraceBetweenNodesCommand
import de.prob.statespace.Trace

// You can change the model you are testing here.
final s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
final t = new Trace(s)
final t1 = t.$initialise_machine().new("pp = PID1")

final t2 = t1.new("pp = PID2").ready("rr = PID1")

final cmd1 = new FindTraceBetweenNodesCommand(s, t1.getCurrentState().getId(), t2.getCurrentState().getId())
s.execute(cmd1)
final t3 = s.getTrace(cmd1)
assert t3 != null && !t3.getTransitionList().isEmpty() && t3.getTransitionList().size() == 2

final t4 = s.getTrace(t1.getCurrentState().getId(), t2.getCurrentState().getId())
assert t4 != null && t3.getTransitionList().size() == t4.getTransitionList().size()

final t5 = t1
final cmd2 = new FindTraceBetweenNodesCommand(s, t1.getCurrentState().getId(), t5.getCurrentState().getId())
s.execute(cmd2)
final t6 = s.getTrace(cmd2)
assert t6 != null && t6.getTransitionList().isEmpty() && t6.getCurrentState() == t1.getCurrentState()

"can create a trace between two given nodes"
