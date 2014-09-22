import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
m = api.b_load(dir+"/machines/scheduler.mch")
s = m as StateSpace
t = new Trace(s)
t = t.anyEvent()


s.animator.cli.shutdown();
"add a description of the test here"