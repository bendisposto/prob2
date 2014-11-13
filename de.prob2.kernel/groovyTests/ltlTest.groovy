import de.prob.animator.domainobjects.*
import de.prob.check.LTLCounterExample;
import de.prob.check.LTLError;
import de.prob.statespace.*


// You can change the model you are testing here.
m = api.b_load(dir+"/machines/scheduler.mch")
s = m as StateSpace

ltl_check = { formula ->
	checker = new ModelChecker(new LTLChecker(s, formula))
	checker.start()
	checker.getResult()
}

res = ltl_check(new LTL("G({ TRUE = TRUE })"))
assert res instanceof LTLOk
res = ltl_check(new LTL("G({ card(active) = 0 })"))
assert res instanceof LTLCounterExample
t = res.getTrace(s)
assert t.getTransitionList().size() == 8

res = ltl_check(new LTL("G({ active < 7 })"))
assert res instanceof LTLError

s.animator.cli.shutdown();
"ltl formulas can be checked"