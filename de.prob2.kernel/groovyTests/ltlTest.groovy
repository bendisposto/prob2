import de.prob.animator.domainobjects.LTL
import de.prob.check.LTLChecker
import de.prob.check.LTLCounterExample
import de.prob.check.LTLError
import de.prob.check.LTLOk
import de.prob.check.ModelChecker

// You can change the model you are testing here.
final s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")

final ltl_check = { formula ->
	final checker = new ModelChecker(new LTLChecker(s, formula))
	checker.start()
	checker.getResult()
}

final res1 = ltl_check(new LTL("G({ TRUE = TRUE })"))
assert res1 instanceof LTLOk
final res2 = ltl_check(new LTL("G({ card(active) = 0 })"))
assert res2 instanceof LTLCounterExample
def t = res2.getTrace(s)
assert t.getTransitionList().size() == 8

final res3 = ltl_check(new LTL("G({ active < 7 })"))
assert res3 instanceof LTLError

"ltl formulas can be checked"
