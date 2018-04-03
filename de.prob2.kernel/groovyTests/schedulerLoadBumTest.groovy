import java.nio.file.Paths

import de.prob.statespace.Trace

final s = api.eventb_load(Paths.get(dir, "Scheduler", "Scheduler0.bum").toString()) 
def c = s as Trace
assert c.currentState == s.root
assert c.currentState.toString() == "root"
c = c.anyEvent()
st = c.currentState
assert st != s.root
c = c.anyEvent()
assert c.currentState != st

"A .bum file (Scheduler0.bum) was loaded and some steps were made"
