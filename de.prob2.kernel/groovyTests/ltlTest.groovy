import java.nio.file.Paths

import de.prob.animator.domainobjects.FormulaExpand
import de.prob.animator.domainobjects.LTL
import de.prob.check.LTLChecker
import de.prob.check.LTLCounterExample
import de.prob.check.LTLError
import de.prob.check.LTLOk
import de.prob.check.ModelChecker

final s = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString())

final ltlCheck = {LTL formula ->
	final checker = new ModelChecker(new LTLChecker(s, formula))
	checker.start()
	checker.result
}

final res1 = ltlCheck(new LTL("G({ TRUE = TRUE })"))
assert res1 instanceof LTLOk
final res2 = ltlCheck(new LTL("G({ card(active) = 0 })"))
assert res2 instanceof LTLCounterExample
def t = res2.getTrace(s)
assert t.getTransitionList(FormulaExpand.TRUNCATE).size() == 8

final res3 = ltlCheck(new LTL("G({ active < 7 })"))
assert res3 instanceof LTLError

"ltl formulas can be checked"
