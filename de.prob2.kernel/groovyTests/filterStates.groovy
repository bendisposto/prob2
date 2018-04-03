import java.nio.file.Paths

import de.prob.animator.command.FilterStatesForPredicateCommand
import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.FormulaExpand
import de.prob.statespace.Trace

// You can change the model you are testing here.
final s = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString())
def t = new Trace(s)

t = t.$initialise_machine()
t = t.new("pp = PID1")
t = t.new("pp = PID2")
t = t.new("pp = PID3")
t = t.del("pp = PID3")
t = t.ready("rr = PID2")

final sIds = t.getTransitionList(FormulaExpand.EXPAND).collect {it.destination.explore()}
final formula = "card(waiting) = 1" as ClassicalB
final cmd = new FilterStatesForPredicateCommand(formula, sIds)
s.execute(cmd)

final filtered = cmd.filtered
assert filtered != null
sIds.each {
	if (filtered.contains(it.id)) {
		assert !s.canBeEvaluated(it) || s.eval(it, [formula])[0].value == "TRUE"
	} else {
		final x = it.eval("waiting")
		assert it.eval(formula).value == "FALSE"
	}
}

"states can be filtered by predicate"
