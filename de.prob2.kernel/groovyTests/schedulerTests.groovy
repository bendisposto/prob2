import java.nio.file.Paths

import de.prob.animator.domainobjects.ClassicalB
import de.prob.model.classicalb.ClassicalBModel
import de.prob.statespace.Trace

final f = "1 + 2" as ClassicalB
final s = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString())
final m = s as ClassicalBModel
s.subscribe(m, f)
def t = new Trace(s)
t.currentState.explore()

def h = t.add 0

final idAt0 = h.currentState
h = h.add 3
assert h.currentState == s.getState("2")
h = h.add 8

final idAt8 = h.currentState
assert idAt0 == idAt8
def h2 = new Trace(s)
h2 = h2.add 0
h2 = h2.add 3
h2 = h2.add 9

s.subscribe(m, m.scheduler.variables.collect {it.formula})
final varsAt6 = s[6].explore().values
assert varsAt6[m.scheduler.variables.waiting.formula].value == "{}"
assert varsAt6[m.scheduler.variables.active.formula].value == "{PID2}"
assert varsAt6[m.scheduler.variables.ready.formula].value == "{}"
final f1 = "1+1=2" as ClassicalB
s.subscribe(m, f1)
assert s.valuesAt(h2.currentState).containsKey(f1)
assert s.valuesAt(h2.currentState)[f1].value == "TRUE"

final root = s.root
assert s.isValidOperation(s[0],"new", "pp = PID1")
assert !s.isValidOperation(s[0],"blah", "TRUE = TRUE")
assert s.isValidOperation(root,"\$initialise_machine", "TRUE = TRUE")
assert !s.isValidOperation(root,"\$setup_constants", "TRUE = TRUE")

t = s as Trace
assert t.canExecuteEvent("\$initialise_machine", [])
t = t.$initialise_machine("TRUE = TRUE")
assert t.canExecuteEvent("new", ["pp = PID1"])
t = t.new("pp = PID1")
assert !t.canExecuteEvent("blah", [])

"Some attributes of the scheduler model were tested"
