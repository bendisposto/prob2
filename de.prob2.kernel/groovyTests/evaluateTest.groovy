import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.TranslatedEvalResult;
import de.prob.statespace.*
import de.prob.translator.types.Atom;

c = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
StateSpace s = c.getStateSpace()
h = new Trace(s)
h = h.add(0)
h = h.add(3)
assert "2" == h.getCurrentState().getId()
assert ['1']== s.eval(s[3],["2-1" as ClassicalB]).collect { it.toString() }
assert ['{}']== s.eval(s[0],["waiting" as ClassicalB]).collect { it.getValue().toString() }
assert ['{PID2}']== s.eval(s[2],["waiting" as ClassicalB]).collect { it.toString() }

formula = "x : waiting & x = PID2 & y : NAT & y = 1" as ClassicalB
EvalResult res = s.eval(s[2],[formula]).get(0)
assert res.value == "TRUE"
assert res.getSolutions().containsKey("x")
assert res.getSolutions().containsKey("y")
assert res.x == "PID2"
assert res.y == "1"

t = res.translate()
assert t != null && t instanceof TranslatedEvalResult
assert t.value == true
assert t.getSolutions().containsKey("x")
assert t.getSolutions().containsKey("y")
assert t.x == new Atom("PID2")
assert t.y == 1


s.animator.cli.shutdown();
"Evaluation of formulas works (scheduler.mch)"