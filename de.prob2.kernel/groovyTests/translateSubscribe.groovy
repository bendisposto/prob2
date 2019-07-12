import java.nio.file.Paths

import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.EvalResult
import de.prob.model.classicalb.ClassicalBModel
import de.prob.statespace.Trace

final s = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString())
final m = s as ClassicalBModel
def t = new Trace(s)
t = t.anyEvent()
final current = t.currentState

final f = "1 + 3" as ClassicalB
s.subscribe(m, f)
current.explore()

assert current.values[f] instanceof EvalResult
assert current.values[f].value == "4"

"a translate formula object can be subscribed"
