import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
api.loadVariablesByDefault = false
s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
m = s as ClassicalBModel
t = new Trace(s)
t = t.anyEvent()
current = t.getCurrentState()

f = "1 + 3" as ClassicalB
s.subscribe(m, f)
current.explore()

assert current.values[f] instanceof EvalResult
assert current.values[f].value == "4"

"a translate formula object can be subscribed"