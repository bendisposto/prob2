import de.prob.statespace.Trace

// Test for b models
final s1 = api.b_load(dir+File.separator +"machines"+File.separator+"MultipleExample.mch")
def t = new Trace(s1)
t = t.anyEvent()

assert t.canExecuteEvent("Set",[])
t = t.Set()
final op1 = t.getCurrentTransition()
assert !op1.isEvaluated()
op1.evaluate()
assert op1.isEvaluated()
assert op1.params != null
assert op1.params.size() == 0
assert op1.returnValues != null
assert op1.returnValues.size() == 3
assert op1.returnValues == ["1", "2", "3"]
assert op1.getRep() == "1,2,3 <-- Set()"

assert t.canExecuteEvent("Crazy1",["p=10"])
t = t.Crazy1("p=10")
final op2 = t.getCurrentTransition()
assert !op2.isEvaluated()
op2.evaluate()
assert op2.isEvaluated()
assert op2.params != null
assert op2.params.size() == 1
assert op2.params == ["10"]
assert op2.returnValues != null
assert op2.returnValues.size() == 2
assert op2.returnValues == ["10","14"]
assert op2.getRep() == "10,14 <-- Crazy1(10)"

assert t.canExecuteEvent("Crazy2", ["p1=7", "p2={4,7,9}"])
t = t.Crazy2("p1=7", "p2={4,7,9}")
final op3 = t.getCurrentTransition()
assert !op3.isEvaluated()
op3.evaluate()
assert op3.isEvaluated()
assert op3.params != null
assert op3.params.size() == 2
assert op3.params == ["7", "{4,7,9}"]
assert op3.returnValues != null
assert op3.returnValues.size() == 3
assert op3.returnValues == ["3","{(7|->{4,7,9})}","8"]
assert op3.getRep() == "3,{(7|->{4,7,9})},8 <-- Crazy2(7,{4,7,9})" 

// For csp:
final s2 = api.csp_load(dir+"/machines/csp/Deterministic1.csp")

t = s2 as Trace
t = t.anyEvent("NonDeterm3")
final op4 = t.getCurrentTransition()
assert op4 != null
assert op4.getRep() == "NonDeterm3"
assert op4.getParams() == []
assert op4.getReturnValues() == []

t = t.addTransitionWith("a",["1"])
final op5 = t.getCurrentTransition()
assert op5 != null
assert op5.getRep() == "a.1"
assert op5.getParams() == ["1"]
assert op5.getReturnValues() == []

"the ops are expanded as expected"
