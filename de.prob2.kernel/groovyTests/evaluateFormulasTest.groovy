import de.prob.animator.domainobjects.ClassicalB
import de.prob.statespace.State
import de.prob.statespace.Trace

final s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")

final formula = "waiting \\/ ready" as ClassicalB
assert !s.formulaRegistry.containsKey(formula)
s.subscribe(s, formula)

def h = new Trace(s)
h = h.add(0)
h = h.add(4)
h = h.add(6)
final a = h.getCurrentState()
assert a == s[4]
assert a.getClass() == State

final values1 = s.valuesAt(a)
assert values1.containsKey(formula)
assert values1[formula].getValue() == "{PID1,PID3}"
h = h.back()
h = h.back()
final b = h.getCurrentState()
assert b == s[0]
final values2 = s.valuesAt(b)
assert values2.containsKey(formula)
assert values2[formula].getValue() == "{}"

final f2 = "card(waiting)" as ClassicalB
final before = b.getValues()
assert !before.containsKey(f2)
s.subscribe(s, f2)
final after = b.getValues()
assert after.containsKey(f2)
assert after.get(f2).getValue() == "0"

"A registered formula is automatically evaluated in every state and can be found in the cache later"
