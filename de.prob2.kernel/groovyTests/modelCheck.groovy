import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")

model_check = { job ->
	def checker = new ModelChecker(job)
	checker.start()
	checker.getResult()
}

checker = new ModelChecker(new ConsistencyChecker(s))
checker.start()
res = checker.getResult()
assert res instanceof ModelCheckOk
coverage = checker.getCoverage()
assert coverage != null
checker = null

s = api.eventb_load(dir+File.separator+"machines"+File.separator+"InvalidModel"+File.separator+"createErrors.bcm")

res = model_check(new ConsistencyChecker(s, new ModelCheckingOptions().checkInvariantViolations(true)))
assert res instanceof ModelCheckErrorUncovered
assert res.getMessage() == "Invariant violation found."
assert res.getTrace(s) != null

res = model_check(new ConsistencyChecker(s, new ModelCheckingOptions().checkDeadlocks(true)))
assert res instanceof ModelCheckErrorUncovered
assert res.getMessage() == "Deadlock found."
assert res.getTrace(s) != null

"model checking works correctly"
