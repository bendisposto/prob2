import de.prob.statespace.*

c = api.b_load(dir+"/machines/scheduler.mch")
StateSpace s = c.getStatespace()
h = new Trace(s)
h = h.add(0)
h = h.add(3)
assert "2" == h.getCurrentState().getId()
assert ['1']== s.eval(s[3],["2-1" as ClassicalB]).collect { it.toString() }
assert ['{}']== s.eval(s[0],["waiting" as ClassicalB]).collect { it.toString() }
assert ['{PID2}']== s.eval(s[2],["waiting" as ClassicalB]).collect { it.toString() }
res = s.eval(s[2],["x : waiting & x = PID2" as ClassicalB]).get(0)
assert res.value == "TRUE"
assert res.getSolutions().containsKey("x")
assert res.x == "PID2"

s.animator.cli.shutdown();
"Evaluation of formulas works (scheduler.mch)"