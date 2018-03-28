import de.prob.animator.domainobjects.EventB
import de.prob.check.CBCDeadlockChecker
import de.prob.check.CBCDeadlockFound
import de.prob.check.CBCInvariantChecker
import de.prob.check.CBCInvariantViolationFound
import de.prob.check.ModelCheckOk
import de.prob.check.ModelChecker

final s1 = api.eventb_load(dir + File.separator + "machines" + File.separator + "InvalidModel" + File.separator +"createErrors.bcm")

def model_check = { job ->
	final checker = new ModelChecker(job)
	checker.start()
	checker.getResult()
}

final res1 = model_check(new CBCDeadlockChecker(s1))
assert res1 instanceof CBCDeadlockFound
final t_deadlock = res1.getTrace(s1)
final ops1 = t_deadlock.getTransitionList(true)
assert ops1.size() == 1
assert ops1[0].getName() == "deadlock_check"

final res2 = model_check(new CBCDeadlockChecker(s1, "deadlocked = FALSE" as EventB))
assert res2 instanceof ModelCheckOk // whoops!!! =)
assert res2.message == "No deadlock was found"

final res3 = model_check(new CBCInvariantChecker(s1))
assert res3 instanceof CBCInvariantViolationFound
final t_invV = res3.getTrace(s1)
final ops3 = t_invV.getTransitionList(true)
assert ops3.size() == 2
assert ops3[0].getName() == "invariant_check_violate_invariant"
assert ops3[1].getName() == "violate_invariant"

final res4 = model_check(new CBCInvariantChecker(s1,["deadlock"]))
assert res4 instanceof ModelCheckOk
assert res4.message == "No Invariant violation was found"

final s2 = api.eventb_load(dir + File.separator + "Time" + File.separator +"clock.bcm")

final res5 = model_check(new CBCInvariantChecker(s2))
assert res5 instanceof CBCInvariantViolationFound
final t_inv = res5.getTrace(s2)
final ops5 = t_inv.getTransitionList(true)
assert ops5.size() == 2
assert ops5[0].getName() == "invariant_check_tock"
assert ops5[1].getName() == "tock"
assert ops5[1].getParams()[0].toInteger() >= 0

"constraint based deadlock and invariant checking works correctly"
