import de.prob.animator.domainobjects.*
import de.prob.statespace.*

m = api.b_load(dir+"/machines/scheduler.mch")
s = m as StateSpace
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


varsAt6 = s.getValues()[s[6]]
assert varsAt6[m.scheduler.variables.waiting.getFormula()].value == "{}"
assert varsAt6[m.scheduler.variables.active.getFormula()].value == "{PID2}"
assert varsAt6[m.scheduler.variables.ready.getFormula()].value == "{}"
f1 = "1+1=2" as ClassicalB
s.subscribe(m, f1)
assert s.valuesAt(h2.getCurrentState()).containsKey(f1)
assert s.valuesAt(h2.getCurrentState())[f1].getValue() == "TRUE"


s.animator.cli.shutdown();
"Some attributes of the scheduler model were tested"
