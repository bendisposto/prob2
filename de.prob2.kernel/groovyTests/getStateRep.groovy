import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
t = new Trace(s)
t = t.$initialise_machine()

t = t.new("pp = PID1")
t = t.new("pp = PID2")
t = t.ready("rr = PID1")

assert t.getCurrentState().getStateRep() == "( active={PID1} &\n       ready={} &\n       waiting={PID2} )"



s.animator.cli.shutdown();
"can get the state representation from prolog"