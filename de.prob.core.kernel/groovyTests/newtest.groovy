import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
m = api.b_load(dir+"/machines/MultipleExample.mch")
s = m as StateSpace
t = new Trace(s)
t = t.anyEvent()

assert t.canExecuteEvent("Set",[])
t = t.Set()
op = t.current.edge
assert !op.isEvaluated()
op.ensureEvaluated(s)
assert op.isEvaluated()
assert op.params != null
assert op.params.size() == 0
assert op.returnValues != null
assert op.returnValues.size() == 3
assert op.returnValues == ["1", "2", "3"]







s.animator.cli.shutdown();
"the ops are expanded as expected"