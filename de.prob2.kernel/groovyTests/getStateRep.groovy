import de.prob.statespace.Trace

// You can change the model you are testing here.
final s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
def t = new Trace(s)
t = t.$initialise_machine()

t = t.new("pp = PID1")
t = t.new("pp = PID2")
t = t.ready("rr = PID1")

assert t.getCurrentState().getStateRep() == "( active={PID1} &\n       ready={} &\n       waiting={PID2} )"

"can get the state representation from prolog"
