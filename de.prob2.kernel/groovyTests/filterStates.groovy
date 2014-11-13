import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
m = api.b_load(dir+"/machines/scheduler.mch")
s = m as StateSpace
t = new Trace(s)

t = t.$initialise_machine()
t = t.new("pp = PID1")
t = t.new("pp = PID2")
t = t.new("pp = PID3")
t = t.del("pp = PID3")
t = t.ready("rr = PID2")

sIds = t.getTransitionList().collect { it.getDestination().explore() }
formula = "card(waiting) = 1" as ClassicalB
cmd = new FilterStatesForPredicateCommand(formula, sIds )
s.execute(cmd)
assert cmd.getFiltered() != null

filtered = cmd.getFiltered()
sIds.each {
	if (filtered.contains(it.getId())) {
		assert !s.canBeEvaluated(it) || s.eval(it,[formula])[0].value == "TRUE"
	} else {
		x = it.eval("waiting")
		assert it.eval(formula).value == "FALSE"
	}
}



s.animator.cli.shutdown();
"states can be filtered by predicate"