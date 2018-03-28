import de.prob.animator.command.SetBGoalCommand
import de.prob.animator.domainobjects.ClassicalB
import de.prob.check.CheckError
import de.prob.check.ConsistencyChecker
import de.prob.check.ModelCheckErrorUncovered
import de.prob.check.ModelChecker
import de.prob.check.ModelCheckingOptions
import de.prob.exception.ProBError

// You can change the model you are testing here.
final s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")

final model_check = { job ->
	final checker = new ModelChecker(job)
	checker.start()
	checker.getResult()
}

final cmd1 = new SetBGoalCommand("1=1" as ClassicalB)
s.execute(cmd1)

def thrown = false
try {
	final cmd2 = new SetBGoalCommand("1" as ClassicalB)
	s.execute(cmd2)
} catch (ProBError e) {
	assert e.getMessage().contains("typeerror")
	thrown = true
}
assert thrown

final res1 = model_check(new ConsistencyChecker(s, new ModelCheckingOptions().checkGoal(true), "card(waiting) = 2" as ClassicalB))
assert res1 instanceof ModelCheckErrorUncovered
final t = res1.getTrace(s)
assert t != null
assert t.evalCurrent("card(waiting) = 2" as ClassicalB).value == "TRUE"

final res2 = model_check(new ConsistencyChecker(s, new ModelCheckingOptions().checkGoal(true), "1" as ClassicalB))
assert res2 instanceof CheckError

"checking for goal works"
