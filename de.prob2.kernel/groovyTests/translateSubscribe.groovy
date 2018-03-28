import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.EvalResult
import de.prob.model.classicalb.ClassicalBModel
import de.prob.statespace.Trace

// You can change the model you are testing here.
final s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
final m = s as ClassicalBModel
def t = new Trace(s)
t = t.anyEvent()
final current = t.getCurrentState()

final f = "1 + 3" as ClassicalB
s.subscribe(m, f)
current.explore()

assert current.values[f] instanceof EvalResult
assert current.values[f].value == "4"

"a translate formula object can be subscribed"
