import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
s0 = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
t0 = new Trace(s0)
t0 = t0.$initialise_machine()

s1 = api.eventb_load(dir+File.separator+"Lift"+File.separator+"lift0.bcm")
t1 = new Trace(s1)
t1 = t1.$setup_constants().$initialise_machine()

tt = new SyncedTraces([t0,t1], [new SyncedEvent("sync1").sync(t0,"new",[]).sync(t1,"up",[]), 
	                            new SyncedEvent("sync2").sync(t0,"del",[]).sync(t1,"down",[])])
tt1 = tt.sync1() 
assert tt1.traces[0].getCurrentTransition().getName() == "new"
assert tt1.traces[1].getCurrentTransition().getName() == "up"
tt2 = tt1.sync2()
assert tt2.traces[0].getCurrentTransition().getName() == "del"
assert tt2.traces[1].getCurrentTransition().getName() == "down"

thrown = false
try {
	tt.sync2()
} catch(IllegalArgumentException e) {
    thrown = true
}
assert thrown

thrown = false
try {
	tt.iDontExist()
} catch(IllegalArgumentException e) {
	thrown = true
}
assert thrown

tt1 = tt.execute(0, "new", [])
assert tt1.traces[0].getCurrentTransition().getName() == "new"
assert tt1.traces[1].getCurrentTransition().getName() == "up"
tt2 = tt1.execute(1, "down", [])
assert tt2.traces[0].getCurrentTransition().getName() == "del"
assert tt2.traces[1].getCurrentTransition().getName() == "down"

tt3 = tt.execute(0, "nr_ready", [])
assert tt3.traces[0].getCurrentTransition().getName() == "nr_ready"
assert tt3.traces[1] == t1

assert tt.toString() instanceof String

"it is possible to sync traces with event/parameter combinations"