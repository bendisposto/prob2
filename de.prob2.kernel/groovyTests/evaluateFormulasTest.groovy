import de.prob.statespace.*

s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")

formula = "waiting \\/ ready" as ClassicalB
assert !s.formulaRegistry.containsKey(formula)
s.subscribe(s, formula)

h = new Trace(s)
h = h.add(0)
h = h.add(4)
h = h.add(6)
a = h.getCurrentState()
assert a == s[4]
assert a.getClass() == State

values = s.valuesAt(a)
assert values.containsKey(formula)
assert values[formula].getValue() == "{PID1,PID3}"
h = h.back()
h = h.back()
b = h.getCurrentState()
assert b == s[0]
values = s.valuesAt(b)
assert values.containsKey(formula)
assert values[formula].getValue() == "{}"

f2 = "card(waiting)" as ClassicalB
before = b.getValues()
assert !before.containsKey(f2)
s.subscribe(s, f2)
after = b.getValues()
assert after.containsKey(f2)
assert after.get(f2).getValue() == "0"


s.animator.cli.shutdown();
"A registered formula is automatically evaluated in every state and can be found in the cache later"