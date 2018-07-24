import java.nio.file.Paths

import de.prob.animator.domainobjects.ClassicalB
import de.prob.statespace.State
import de.prob.statespace.Trace

final s = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString())

final formula = "waiting \\/ ready" as ClassicalB
assert !s.formulaRegistry.containsKey(formula)
s.subscribe(s, formula)

def h = new Trace(s)
h = h.add(0)
h = h.add(4)
h = h.add(6)
final a = h.currentState
assert a == s[4]
assert a.class == State

final values1 = s.valuesAt(a)
assert values1.containsKey(formula)
assert values1[formula].value == "{PID1,PID3}"
h = h.back()
h = h.back()
final b = h.currentState
assert b == s[0]
final values2 = s.valuesAt(b)
assert values2.containsKey(formula)
assert values2[formula].value == "{}"

final f2 = "card(waiting)" as ClassicalB
final before = b.values
assert !before.containsKey(f2)
s.subscribe(s, f2)
final after = b.values
assert after.containsKey(f2)
assert after[f2].value == "0"

"A registered formula is automatically evaluated in every state and can be found in the cache later"
