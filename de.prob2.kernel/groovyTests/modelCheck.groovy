import java.nio.file.Paths

import de.prob.check.ConsistencyChecker
import de.prob.check.ModelCheckErrorUncovered
import de.prob.check.ModelCheckOk
import de.prob.check.ModelChecker
import de.prob.check.ModelCheckingOptions

final s1 = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString())

final modelCheck = { job ->
	final checker = new ModelChecker(job)
	checker.start()
	checker.result
}

final checker = new ModelChecker(new ConsistencyChecker(s1))
checker.start()
final res1 = checker.result
assert res1 instanceof ModelCheckOk
final coverage = checker.coverage
assert coverage != null

final s2 = api.eventb_load(Paths.get(dir, "machines", "InvalidModel", "createErrors.bcm").toString())

final res2 = modelCheck(new ConsistencyChecker(s2, new ModelCheckingOptions().checkInvariantViolations(true)))
assert res2 instanceof ModelCheckErrorUncovered
assert res2.getMessage() == "Invariant violation found."
assert res2.getTrace(s2) != null

final res3 = modelCheck(new ConsistencyChecker(s2, new ModelCheckingOptions().checkDeadlocks(true)))
assert res3 instanceof ModelCheckErrorUncovered
assert res3.getMessage() == "Deadlock found."
assert res3.getTrace(s2) != null

"model checking works correctly"
