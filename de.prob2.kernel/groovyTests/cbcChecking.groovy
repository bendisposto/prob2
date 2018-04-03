import java.nio.file.Paths

import de.prob.animator.domainobjects.EventB
import de.prob.animator.domainobjects.FormulaExpand
import de.prob.check.CBCDeadlockChecker
import de.prob.check.CBCDeadlockFound
import de.prob.check.CBCInvariantChecker
import de.prob.check.CBCInvariantViolationFound
import de.prob.check.IModelCheckJob
import de.prob.check.ModelCheckOk
import de.prob.check.ModelChecker

final s1 = api.eventb_load(Paths.get(dir, "machines", "InvalidModel", "createErrors.bcm").toString())

final modelCheck = {IModelCheckJob job ->
	final checker = new ModelChecker(job)
	checker.start()
	checker.result
}

final res1 = modelCheck(new CBCDeadlockChecker(s1))
assert res1 instanceof CBCDeadlockFound
final tDeadlock = res1.getTrace(s1)
final ops1 = tDeadlock.getTransitionList(true, FormulaExpand.EXPAND)
assert ops1.size() == 1
assert ops1[0].name == "deadlock_check"

final res2 = modelCheck(new CBCDeadlockChecker(s1, "deadlocked = FALSE" as EventB))
assert res2 instanceof ModelCheckOk // whoops!!! =)
assert res2.message == "No deadlock was found"

final res3 = modelCheck(new CBCInvariantChecker(s1))
assert res3 instanceof CBCInvariantViolationFound
final tInvViolation1 = res3.getTrace(s1)
final ops3 = tInvViolation1.getTransitionList(true, FormulaExpand.EXPAND)
assert ops3.size() == 2
assert ops3[0].name == "invariant_check_violate_invariant"
assert ops3[1].name == "violate_invariant"

final res4 = modelCheck(new CBCInvariantChecker(s1, ["deadlock"]))
assert res4 instanceof ModelCheckOk
assert res4.message == "No Invariant violation was found"

final s2 = api.eventb_load(Paths.get(dir, "Time", "clock.bcm").toString())

final res5 = modelCheck(new CBCInvariantChecker(s2))
assert res5 instanceof CBCInvariantViolationFound
final tInvViolation2 = res5.getTrace(s2)
final ops5 = tInvViolation2.getTransitionList(true, FormulaExpand.EXPAND)
assert ops5.size() == 2
assert ops5[0].name == "invariant_check_tock"
assert ops5[1].name == "tock"
assert ops5[1].parameterValues[0].toInteger() >= 0

"constraint based deadlock and invariant checking works correctly"
