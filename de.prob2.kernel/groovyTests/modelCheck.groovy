import de.prob.check.ConsistencyChecker
import de.prob.check.ModelCheckErrorUncovered
import de.prob.check.ModelCheckOk
import de.prob.check.ModelChecker
import de.prob.check.ModelCheckingOptions

// You can change the model you are testing here.
final s1 = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")

final model_check = { job ->
	final checker = new ModelChecker(job)
	checker.start()
	checker.getResult()
}

final checker = new ModelChecker(new ConsistencyChecker(s1))
checker.start()
final res1 = checker.getResult()
assert res1 instanceof ModelCheckOk
final coverage = checker.getCoverage()
assert coverage != null

final s2 = api.eventb_load(dir+File.separator+"machines"+File.separator+"InvalidModel"+File.separator+"createErrors.bcm")

final res2 = model_check(new ConsistencyChecker(s2, new ModelCheckingOptions().checkInvariantViolations(true)))
assert res2 instanceof ModelCheckErrorUncovered
assert res2.getMessage() == "Invariant violation found."
assert res2.getTrace(s2) != null

final res3 = model_check(new ConsistencyChecker(s2, new ModelCheckingOptions().checkDeadlocks(true)))
assert res3 instanceof ModelCheckErrorUncovered
assert res3.getMessage() == "Deadlock found."
assert res3.getTrace(s2) != null

"model checking works correctly"
