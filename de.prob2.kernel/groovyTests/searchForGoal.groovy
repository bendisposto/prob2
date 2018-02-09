import de.prob.animator.command.SetBGoalCommand
import de.prob.animator.domainobjects.*
import de.prob.check.ModelCheckErrorUncovered;
import de.prob.exception.ProBError
import de.prob.statespace.*

// You can change the model you are testing here.
s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")

model_check = { job ->
	def checker = new ModelChecker(job)
	checker.start()
	checker.getResult()
}

cmd = new SetBGoalCommand("1=1" as ClassicalB)
s.execute(cmd)

thrown = false
try {
	cmd = new SetBGoalCommand("1" as ClassicalB)
	s.execute(cmd)
} catch(ProBError e) {
	assert e.getMessage().contains("typeerror")
	thrown = true
}
assert thrown

res = model_check(new ConsistencyChecker(s, new ModelCheckingOptions().checkGoal(true), "card(waiting) = 2" as ClassicalB))
assert res instanceof ModelCheckErrorUncovered
t = res.getTrace(s)
assert t != null
assert t.evalCurrent("card(waiting) = 2" as ClassicalB).value == "TRUE"

res = model_check(new ConsistencyChecker(s, new ModelCheckingOptions().checkGoal(true), "1" as ClassicalB))
assert res instanceof CheckError

"checking for goal works"
