import java.nio.file.Paths

import de.prob.animator.command.CheckInitialisationStatusCommand
import de.prob.animator.command.CheckInvariantStatusCommand
import de.prob.animator.command.CheckMaxOperationReachedStatusCommand
import de.prob.animator.command.CheckTimeoutStatusCommand
import de.prob.animator.command.GetEnabledOperationsCommand
import de.prob.animator.command.GetOperationByPredicateCommand
import de.prob.animator.command.GetStateBasedErrorsCommand
import de.prob.animator.command.IStateSpaceModifier
import de.prob.animator.domainobjects.ClassicalB
import de.prob.statespace.Trace

final s = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString())

final cmd1 = new CheckInitialisationStatusCommand("root")
s.execute(cmd1)
assert !cmd1.initialized

def thrown1 = false
try { 
	s[0]
} catch (IllegalArgumentException e) {
	thrown1 = true
}
assert thrown1
final cmd2 = new GetEnabledOperationsCommand(s, "root")
s.execute(cmd2)
assert cmd2 instanceof IStateSpaceModifier
assert cmd2.enabledOperations.size() == 1
ops = cmd2.enabledOperations
assert ops[0].getId() == "0"
assert s[0] != null // Tests if after executing this command, the new state "0" has been added to the state space

final cmd3 = new CheckInitialisationStatusCommand("0")
s.execute(cmd3)
assert cmd3.initialized

assert s.eval(s[0], ["1+1" as ClassicalB])[0].value == "2" // If state 0 is initialized, we should be able to evaluate in that state

final cmd4 = new CheckInvariantStatusCommand("0")
s.execute(cmd4)
assert !cmd4.invariantViolated

final cmd5 = new CheckMaxOperationReachedStatusCommand("0")
s.execute(cmd5)
assert !cmd5.maxOperationReached()

final cmd6 = new CheckTimeoutStatusCommand("0")
s.execute(cmd6)
assert !cmd6.timeout

final cmd7 = new GetOperationByPredicateCommand(s, "0", "new", "pp = PID1" as ClassicalB, 1)
s.execute(cmd7)
transitions = cmd7.newTransitions
assert transitions.size() == 1
assert transitions[0].getId() == "1"
op = transitions[0]
assert op.getName() == "new"
assert op.getParameterValues() == ["PID1"]

// GetOperationByPredicateCommand must be called with a predicate
def thrown2 = false
try {
	final cmd8 = new GetOperationByPredicateCommand(s, "0", "new", "PID1" as ClassicalB, 1)
	s.execute(cmd8)
} catch (IllegalArgumentException e) {
	thrown2 = true
}
assert thrown2

final cmd9 = new GetOperationByPredicateCommand(s, "0", "blah", "TRUE = TRUE" as ClassicalB, 1)
s.execute(cmd9)
assert cmd9.errors == ["Unknown Operation blah"]

final cmd10 = new GetOperationByPredicateCommand(s, "0", "blah", "TRUE = TRUE" as ClassicalB, 0)
s.execute(cmd10)
assert cmd10.errors == ["max nr of solutions too small"]

final cmd11 = new GetOperationByPredicateCommand(s, "0", "blah", "TRUE = FALSE" as ClassicalB, 1)
s.execute(cmd11)
assert cmd11.newTransitions.empty

def t = s as Trace
t = t.randomAnimation(10)
assert s[5] != null
final cmd12 = new CheckInitialisationStatusCommand("5")
s.execute(cmd12)
assert cmd12.initialized

final cmd13 = new CheckInvariantStatusCommand("5")
s.execute(cmd13)
assert !cmd13.invariantViolated

final cmd14 = new GetStateBasedErrorsCommand("5")
s.execute(cmd14)
assert cmd14.result.empty

"Animation commands work correctly"
