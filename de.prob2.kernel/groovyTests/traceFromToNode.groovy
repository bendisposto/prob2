import sun.font.Type1Font.T1DisposerRecord;
import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
m = api.b_load(dir+"/machines/scheduler.mch")
s = m as StateSpace
t = new Trace(s)
t1 = t.anyEvent().anyEvent()

t2 = t1.randomAnimation(10)

cmd = new FindTraceBetweenNodesCommand(s, t1.getCurrentState().getId(), t2.getCurrentState().getId())
s.execute(cmd)
t3 = s.getTrace(cmd)
assert t3 != null && !t3.getOpList().isEmpty()

t4 = s.getTrace(t1.getCurrentState().getId(), t2.getCurrentState().getId())
assert t4 != null && t3.getOpList().size() == t4.getOpList().size()


s.animator.cli.shutdown();
"can create a trace between two given nodes"