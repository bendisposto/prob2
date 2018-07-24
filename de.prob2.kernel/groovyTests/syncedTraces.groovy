import java.nio.file.Paths

import de.prob.statespace.SyncedEvent
import de.prob.statespace.SyncedTraces
import de.prob.statespace.Trace

final s0 = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString())
def t0 = new Trace(s0)
t0 = t0.$initialise_machine()

final s1 = api.eventb_load(Paths.get(dir, "Lift", "lift0.bcm").toString())
def t1 = new Trace(s1)
t1 = t1.$setup_constants().$initialise_machine()

final tt = new SyncedTraces([t0, t1], [
	new SyncedEvent("sync1").sync(t0, "new", []).sync(t1, "up", []),
	new SyncedEvent("sync2").sync(t0, "del", []).sync(t1, "down", []),
])
final tt1 = tt.sync1() 
assert tt1.traces[0].currentTransition.name == "new"
assert tt1.traces[1].currentTransition.name == "up"
final tt2 = tt1.sync2()
assert tt2.traces[0].currentTransition.name == "del"
assert tt2.traces[1].currentTransition.name == "down"

def thrown1 = false
try {
	tt.sync2()
} catch (IllegalArgumentException e) {
	thrown1 = true
}
assert thrown1

def thrown2 = false
try {
	tt.iDontExist()
} catch (IllegalArgumentException e) {
	thrown2 = true
}
assert thrown2

final tt3 = tt.execute(0, "new", [])
assert tt3.traces[0].currentTransition.name == "new"
assert tt3.traces[1].currentTransition.name == "up"
final tt4 = tt3.execute(1, "down", [])
assert tt4.traces[0].currentTransition.name == "del"
assert tt4.traces[1].currentTransition.name == "down"

final tt5 = tt.execute(0, "nr_ready", [])
assert tt5.traces[0].currentTransition.name == "nr_ready"
assert tt5.traces[1] == t1

assert tt.toString() instanceof String

"it is possible to sync traces with event/parameter combinations"
