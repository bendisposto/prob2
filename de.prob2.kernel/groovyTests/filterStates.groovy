import de.prob.animator.command.FilterStatesForPredicateCommand
import de.prob.animator.domainobjects.ClassicalB
import de.prob.statespace.Trace

// You can change the model you are testing here.
final s = api.b_load(dir+""+File.separator+"machines"+File.separator+"scheduler.mch")
def t = new Trace(s)

t = t.$initialise_machine()
t = t.new("pp = PID1")
t = t.new("pp = PID2")
t = t.new("pp = PID3")
t = t.del("pp = PID3")
t = t.ready("rr = PID2")

final sIds = t.getTransitionList().collect { it.getDestination().explore() }
final formula = "card(waiting) = 1" as ClassicalB
final cmd = new FilterStatesForPredicateCommand(formula, sIds )
s.execute(cmd)
assert cmd.getFiltered() != null

filtered = cmd.getFiltered()
sIds.each {
	if (filtered.contains(it.getId())) {
		assert !s.canBeEvaluated(it) || s.eval(it,[formula])[0].value == "TRUE"
	} else {
		final x = it.eval("waiting")
		assert it.eval(formula).value == "FALSE"
	}
}

"states can be filtered by predicate"
