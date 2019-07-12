import java.nio.file.Paths

import de.prob.animator.domainobjects.FormulaExpand
import de.prob.statespace.Trace

// Test for b models
final s1 = api.b_load(Paths.get(dir, "machines", "MultipleExample.mch").toString())
def t = new Trace(s1)
t = t.anyEvent()

assert t.canExecuteEvent("Set", [])
t = t.Set()
final op1 = t.currentTransition
assert !op1.evaluated
op1.evaluate(FormulaExpand.EXPAND)
assert op1.evaluated
assert op1.parameterValues != null
assert op1.parameterValues.size() == 0
assert op1.returnValues != null
assert op1.returnValues.size() == 3
assert op1.returnValues == ["1", "2", "3"]
assert op1.rep == "1,2,3 <-- Set()"

assert t.canExecuteEvent("Crazy1", ["p=10"])
t = t.Crazy1("p=10")
final op2 = t.currentTransition
assert !op2.evaluated
op2.evaluate(FormulaExpand.EXPAND)
assert op2.evaluated
assert op2.parameterValues != null
assert op2.parameterValues.size() == 1
assert op2.parameterValues == ["10"]
assert op2.returnValues != null
assert op2.returnValues.size() == 2
assert op2.returnValues == ["10", "14"]
assert op2.rep == "10,14 <-- Crazy1(10)"

assert t.canExecuteEvent("Crazy2", ["p1=7", "p2={4,7,9}"])
t = t.Crazy2("p1=7", "p2={4,7,9}")
final op3 = t.currentTransition
assert !op3.evaluated
op3.evaluate(FormulaExpand.EXPAND)
assert op3.evaluated
assert op3.parameterValues != null
assert op3.parameterValues.size() == 2
assert op3.parameterValues == ["7", "{4,7,9}"]
assert op3.returnValues != null
assert op3.returnValues.size() == 3
assert op3.returnValues == ["3", "{(7|->{4,7,9})}", "8"]
assert op3.rep == "3,{(7|->{4,7,9})},8 <-- Crazy2(7,{4,7,9})"

// For csp:
final s2 = api.csp_load(Paths.get(dir, "machines", "csp", "Deterministic1.csp").toString())

t = s2 as Trace
t = t.anyEvent("NonDeterm3")
final op4 = t.currentTransition
assert op4 != null
assert op4.rep == "NonDeterm3"
assert op4.parameterValues == []
assert op4.returnValues == []

t = t.addTransitionWith("a", ["1"])
final op5 = t.currentTransition
assert op5 != null
assert op5.rep == "a.1"
assert op5.parameterValues == ["1"]
assert op5.returnValues == []

"the ops are expanded as expected"
