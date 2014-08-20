import de.prob.statespace.*

s = api.b_load(dir+"/machines/scheduler.mch") as StateSpace

formula = "waiting \\/ ready" as ClassicalB
assert !s.formulaRegistry.containsKey(formula)
s.subscribe(s, formula)

h = new Trace(s)
h = h.add(0)
h = h.add(4)
h = h.add(6)
a = h.getCurrentState()
assert a == s[4]
assert a.getClass() == de.prob.statespace.StateId

values = s.getValues()[a]
assert values.containsKey(formula)
assert values[formula].getValue() == "{PID1,PID3}"
h = h.back()
h = h.back()
b = h.getCurrentState()
assert b == s[0]
values = s.getValues()[b]
assert values.containsKey(formula)
assert values[formula].getValue() == "{}"

s.animator.cli.shutdown();
"A registered formula is automatically evaluated in every state and can be found in the cache later"