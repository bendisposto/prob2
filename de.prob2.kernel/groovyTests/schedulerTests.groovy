import de.prob.animator.domainobjects.*
import de.prob.statespace.*

x = 1
f = "1 + 2" as ClassicalB
s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
m = s as ClassicalBModel
s.subscribe(m, f)
t = new Trace(s)
t.getCurrentState().explore()


h = t.add 0


idAt0 = h.getCurrentState()
h = h.add 3
assert h.getCurrentState() == s.getState("2")
h = h.add 8

idAt8 = h.getCurrentState()
assert idAt0 == idAt8
h2 = new Trace(s)
h2 = h2.add 0
h2 = h2.add 3
h2 = h2.add 9

varsAt6 = s[6].explore().getValues()
assert varsAt6[m.scheduler.variables.waiting.getFormula()].value == "{}"
assert varsAt6[m.scheduler.variables.active.getFormula()].value == "{PID2}"
assert varsAt6[m.scheduler.variables.ready.getFormula()].value == "{}"
f1 = "1+1=2" as ClassicalB
s.subscribe(m, f1)
assert s.valuesAt(h2.getCurrentState()).containsKey(f1)
assert s.valuesAt(h2.getCurrentState())[f1].getValue() == "TRUE"

root = s.getRoot()
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

s.animator.cli.shutdown();
"Some attributes of the scheduler model were tested"
