import sun.font.Type1Font.T1DisposerRecord;
import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
t = new Trace(s)
t1 = t.$initialise_machine().new("pp = PID1")

t2 = t1.new("pp = PID2").ready("rr = PID1")

cmd = new FindTraceBetweenNodesCommand(s, t1.getCurrentState().getId(), t2.getCurrentState().getId())
s.execute(cmd)
t3 = s.getTrace(cmd)
assert t3 != null && !t3.getTransitionList().isEmpty() && t3.getTransitionList().size() == 2

t4 = s.getTrace(t1.getCurrentState().getId(), t2.getCurrentState().getId())
assert t4 != null && t3.getTransitionList().size() == t4.getTransitionList().size()

t5 = t1
cmd = new FindTraceBetweenNodesCommand(s, t1.getCurrentState().getId(), t5.getCurrentState().getId())
s.execute(cmd)
t6 = s.getTrace(cmd)
assert t6 != null && t6.getTransitionList().isEmpty() && t6.getCurrentState() == t1.getCurrentState()

"can create a trace between two given nodes"