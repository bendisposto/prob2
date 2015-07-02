import java.awt.image.RescaleOp;

import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")

model_check = { job ->
	checker = new ModelChecker(job)
	checker.start()
	checker.getResult()
}

res = model_check(new ConsistencyChecker(s))
assert res instanceof ModelCheckOk

s.animator.cli.shutdown();

s = api.eventb_load(dir+File.separator+"machines"+File.separator+"InvalidModel"+File.separator+"createErrors.bcm")

res = model_check(new ConsistencyChecker(s, new ModelCheckingOptions().checkInvariantViolations(true)))
assert res instanceof ModelCheckErrorUncovered
assert res.getMessage() == "Invariant violation found."
assert res.getTrace(s) != null

res = model_check(new ConsistencyChecker(s, new ModelCheckingOptions().checkDeadlocks(true)))
assert res instanceof ModelCheckErrorUncovered
assert res.getMessage() == "Deadlock found."
assert res.getTrace(s) != null

s.animator.cli.shutdown();
"model checking works correctly"