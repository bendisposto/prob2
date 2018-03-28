import de.prob.animator.domainobjects.ClassicalB
import de.prob.model.classicalb.ClassicalBModel
import de.prob.statespace.Trace

final x = 1
final f = "1 + 2" as ClassicalB
final s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
final m = s as ClassicalBModel
s.subscribe(m, f)
def t = new Trace(s)
t.getCurrentState().explore()


def h = t.add 0


final idAt0 = h.getCurrentState()
h = h.add 3
assert h.getCurrentState() == s.getState("2")
h = h.add 8

final idAt8 = h.getCurrentState()
assert idAt0 == idAt8
def h2 = new Trace(s)
h2 = h2.add 0
h2 = h2.add 3
h2 = h2.add 9

s.subscribe(m, m.scheduler.variables.collect{it.getFormula()})
final varsAt6 = s[6].explore().getValues()
assert varsAt6[m.scheduler.variables.waiting.getFormula()].value == "{}"
assert varsAt6[m.scheduler.variables.active.getFormula()].value == "{PID2}"
assert varsAt6[m.scheduler.variables.ready.getFormula()].value == "{}"
final f1 = "1+1=2" as ClassicalB
s.subscribe(m, f1)
assert s.valuesAt(h2.getCurrentState()).containsKey(f1)
assert s.valuesAt(h2.getCurrentState())[f1].getValue() == "TRUE"

final root = s.getRoot()
assert s.isValidOperation(s[0],"new", "pp = PID1")
assert !s.isValidOperation(s[0],"blah", "TRUE = TRUE")
assert s.isValidOperation(root,"\$initialise_machine", "TRUE = TRUE")
assert !s.isValidOperation(root,"\$setup_constants", "TRUE = TRUE")

t = s as Trace
assert t.canExecuteEvent("\$initialise_machine", [])
t = t.$initialise_machine("TRUE = TRUE")
assert t.canExecuteEvent("new",["pp = PID1"])
t = t.new("pp = PID1")
assert !t.canExecuteEvent("blah",[])

"Some attributes of the scheduler model were tested"
