import java.nio.file.Paths

import de.prob.statespace.Trace

final s = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString())
def t = new Trace(s)
t = t.$initialise_machine()

t = t.new("pp = PID1")
t = t.new("pp = PID2")
t = t.ready("rr = PID1")

assert t.currentState.stateRep == "( active={PID1} &\n       ready={} &\n       waiting={PID2} )"

"can get the state representation from prolog"
