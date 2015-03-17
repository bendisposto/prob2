import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// Test for b models
m = api.b_load(dir+File.separator +"machines"+File.separator+"MultipleExample.mch")
s = m as StateSpace
t = new Trace(s)
t = t.anyEvent()

assert t.canExecuteEvent("Set",[])
t = t.Set()
op = t.getCurrentTransition()
assert !op.isEvaluated()
op.evaluate()
assert op.isEvaluated()
assert op.params != null
assert op.params.size() == 0
assert op.returnValues != null
assert op.returnValues.size() == 3
assert op.returnValues == ["1", "2", "3"]
assert op.getRep() == "1,2,3 <-- Set()"

assert t.canExecuteEvent("Crazy1",["p=10"])
t = t.Crazy1("p=10")
op = t.getCurrentTransition()
assert !op.isEvaluated()
op.evaluate()
assert op.isEvaluated()
assert op.params != null
assert op.params.size() == 1
assert op.params == ["10"]
assert op.returnValues != null
assert op.returnValues.size() == 2
assert op.returnValues == ["10","14"]
assert op.getRep() == "10,14 <-- Crazy1(10)"

assert t.canExecuteEvent("Crazy2", ["p1=7", "p2={4,7,9}"])
t = t.Crazy2("p1=7", "p2={4,7,9}")
op = t.getCurrentTransition()
assert !op.isEvaluated()
op.evaluate()
assert op.isEvaluated()
assert op.params != null
assert op.params.size() == 2
assert op.params == ["7", "{4,7,9}"]
assert op.returnValues != null
assert op.returnValues.size() == 3
assert op.returnValues == ["3","{(7|->{4,7,9})}","8"]
assert op.getRep() == "3,{(7|->{4,7,9})},8 <-- Crazy2(7,{4,7,9})" 

s.animator.cli.shutdown();

// For csp:
m = api.csp_load(dir+"/machines/csp/Deterministic1.csp")
t = m as Trace
s = t as StateSpace

t = t.anyEvent("NonDeterm3")
op = t.getCurrentTransition()
assert op != null
assert op.getRep() == "NonDeterm3"
assert op.getParams() == []
assert op.getReturnValues() == []

t = t.addTransitionWith("a",["1"])
op = t.getCurrentTransition()
assert op != null
assert op.getRep() == "a.1"
assert op.getParams() == ["1"]
assert op.getReturnValues() == []

s.animator.cli.shutdown();
"the ops are expanded as expected"