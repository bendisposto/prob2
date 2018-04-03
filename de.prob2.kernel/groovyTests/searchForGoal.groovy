import java.nio.file.Paths

import de.prob.animator.command.SetBGoalCommand
import de.prob.animator.domainobjects.ClassicalB
import de.prob.check.CheckError
import de.prob.check.ConsistencyChecker
import de.prob.check.ModelCheckErrorUncovered
import de.prob.check.ModelChecker
import de.prob.check.ModelCheckingOptions
import de.prob.exception.ProBError

final s = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString())

final modelCheck = { job ->
	final checker = new ModelChecker(job)
	checker.start()
	checker.result
}

final cmd1 = new SetBGoalCommand("1=1" as ClassicalB)
s.execute(cmd1)

def thrown = false
try {
	final cmd2 = new SetBGoalCommand("1" as ClassicalB)
	s.execute(cmd2)
} catch (ProBError e) {
	assert e.message.contains("typeerror")
	thrown = true
}
assert thrown

final res1 = modelCheck(new ConsistencyChecker(s, new ModelCheckingOptions().checkGoal(true), "card(waiting) = 2" as ClassicalB))
assert res1 instanceof ModelCheckErrorUncovered
final t = res1.getTrace(s)
assert t != null
assert t.evalCurrent("card(waiting) = 2" as ClassicalB).value == "TRUE"

final res2 = modelCheck(new ConsistencyChecker(s, new ModelCheckingOptions().checkGoal(true), "1" as ClassicalB))
assert res2 instanceof CheckError

"checking for goal works"
