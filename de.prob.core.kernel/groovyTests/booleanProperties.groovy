import de.prob.animator.command.CheckInvariantStatusCommand;
import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
m = api.b_load(dir+"/machines/scheduler.mch")
s = m as StateSpace
t = new Trace(s)

cmd = new CheckInitialisationStatusCommand("root")
s.execute(cmd)
assert cmd.isInitialized() == false

cmd = new CheckInitialisationStatusCommand("0")
s.execute(cmd)
assert cmd.isInitialized() == true

s.eval(s[0],["1+1" as ClassicalB])[0].getValue() == "2" // If state 0 is initialized, we should be able to evaluate in that state

cmd = new CheckInvariantStatusCommand("0")
s.execute(cmd)
assert cmd.isInvariantViolated() == false

cmd = new CheckMaxOperationReachedStatusCommand("0")
s.execute(cmd)
assert cmd.maxOperationReached() == false

cmd = new CheckTimeoutStatusCommand("0")
s.execute(cmd)
assert cmd.isTimeout() == false

t = t.randomAnimation(10)
assert s[5] != null
cmd = new CheckInitialisationStatusCommand("5")
s.execute(cmd)
assert cmd.isInitialized() == true

cmd = new CheckInvariantStatusCommand("5")
s.execute(cmd)
assert cmd.isInvariantViolated() == false
s.animator.cli.shutdown();
"Able to test the boolean properties"