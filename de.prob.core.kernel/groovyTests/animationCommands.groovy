import de.prob.animator.command.CheckInitialisationStatusCommand
import de.prob.animator.command.CheckInvariantStatusCommand
import de.prob.animator.command.CheckMaxOperationReachedStatusCommand
import de.prob.animator.command.CheckTimeoutStatusCommand
import de.prob.animator.command.GetEnabledOperationsCommand
import de.prob.animator.command.IStateSpaceModifier
import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
m = api.b_load(dir+"/machines/scheduler.mch")
s = m as StateSpace

cmd = new CheckInitialisationStatusCommand("root")
s.execute(cmd)
assert cmd.isInitialized() == false

assert s.getVertex("0") == null 
cmd = new GetEnabledOperationsCommand("root")
s.execute(cmd)
assert cmd instanceof IStateSpaceModifier
assert cmd.getEnabledOperations().size() == 1
ops = cmd.getEnabledOperations()
assert ops[0].getId() == "0"
assert s[0] != null // Tests if after executing this command, the new state "0" has been added to the state space

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

t = s as Trace
t = t.randomAnimation(10)
assert s[5] != null
cmd = new CheckInitialisationStatusCommand("5")
s.execute(cmd)
assert cmd.isInitialized() == true

cmd = new CheckInvariantStatusCommand("5")
s.execute(cmd)
assert cmd.isInvariantViolated() == false
s.animator.cli.shutdown();
"Animation commands work correctly"