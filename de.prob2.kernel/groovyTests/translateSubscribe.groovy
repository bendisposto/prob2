import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
api.loadVariablesByDefault = false
m = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
s = m as StateSpace
t = new Trace(s)
t = t.anyEvent()
current = t.getCurrentState()

f = "1 + 3" as ClassicalB
s.subscribe(m, f)
current.explore()

assert current.values[f] instanceof EvalResult
assert current.values[f].value == "4"

s.animator.cli.shutdown();
"a translate formula object can be subscribed"