import de.prob.statespace.*
import de.prob.animator.domainobjects.*;
c = api.b_load(dir+"/machines/scheduler.mch")
s = c.statespace
h = new Trace(s)
h = h.add 0
idAt0 = h.current.getCurrentState()
h = h.add 3
assert h.current.getCurrentState() == s.states.get("2")
assert s.isExplored(s.states.get("2"))
assert !s.isExplored(s.states.get("5"))
assert s.getOps().containsKey("1")
assert !s.isOutEdge(h.current.getCurrentState(),s.getOps().get("1"))
h = h.add 8
idAt8 = h.current.getCurrentState()
assert idAt0 == idAt8
h2 = new Trace(s)
h2 = h2.add 0
h2 = h2.add 3
h2 = h2.add 9
assert s.isExplored(s.states.get("0"))
assert s.isExplored(s.states.get("6"))
assert s.isExplored(s.states.get("root"))
assert !s.isExplored(s.states.get("5"))
assert s.states.get("5") != null
varsAt6 = s.info.getState(s.states.get("6"))
assert varsAt6.get("waiting") == "{}"
assert varsAt6.get("active") == "{PID2}"
assert varsAt6.get("ready") == "{}"
s.addUserFormula("1+1=2" as ClassicalB)
h.current.getCurrentState().f1
assert s.info.getVariable(h.current.getCurrentState(),"f1") == "TRUE"
