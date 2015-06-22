import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
m0 = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
s0 = m0 as StateSpace
t0 = new Trace(s0)
t0 = t0.$initialise_machine()

m1 = api.eventb_load(dir+File.separator+"Lift"+File.separator+"lift0.bcm")
s1 = m1 as StateSpace
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

tt1 = tt.execute(0, "new", [])
assert tt1.traces[0].getCurrentTransition().getName() == "new"
assert tt1.traces[1].getCurrentTransition().getName() == "up"
tt2 = tt1.execute(1, "down", [])
assert tt2.traces[0].getCurrentTransition().getName() == "del"
assert tt2.traces[1].getCurrentTransition().getName() == "down"


s0.animator.cli.shutdown();
s1.animator.cli.shutdown();
"it is possible to sync traces with event/parameter combinations"