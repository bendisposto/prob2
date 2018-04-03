import java.nio.file.Paths

import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.EvalResult
import de.prob.animator.domainobjects.TranslatedEvalResult
import de.prob.statespace.Trace
import de.prob.translator.types.Atom

final s = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString())
def h = new Trace(s)
h = h.add(0)
h = h.add(3)
assert h.currentState.id == "2"
assert s.eval(s[3], ["2-1" as ClassicalB]).collect {it.toString()} == ['1']
assert s.eval(s[0], ["waiting" as ClassicalB]).collect {it.value.toString()} == ['{}']
assert s.eval(s[2], ["waiting" as ClassicalB]).collect {it.toString()} == ['{PID2}']

final formula = "x : waiting & x = PID2 & y : NAT & y = 1" as ClassicalB
final res = s.eval(s[2], [formula])[0]
assert res instanceof EvalResult
assert res.value == "TRUE"
assert res.solutions.containsKey("x")
assert res.solutions.containsKey("y")
assert res.x == "PID2"
assert res.y == "1"

final t = res.translate()
assert t != null && t instanceof TranslatedEvalResult
assert t.value == true
assert t.solutions.containsKey("x")
assert t.solutions.containsKey("y")
assert t.x == new Atom("PID2")
assert t.y == 1

"Evaluation of formulas works (scheduler.mch)"
