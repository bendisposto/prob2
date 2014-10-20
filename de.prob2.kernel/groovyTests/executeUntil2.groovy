import de.prob.animator.domainobjects.*
import de.prob.exception.ProBError;
import de.prob.statespace.*

m = api.b_load(dir+"/machines/Simple.mch")
s = m as StateSpace
t = m as Trace
t = t.$initialise_machine()
t = t.read("xx = 1")
cond = new LTL("F Y [end]")
cmd = new ExecuteUntilCommand(s, t.getCurrentState(), cond)
s.execute(cmd)
assert cmd.isSuccess()
t = t.addOps(cmd.getNewTransitions())
assert t.head.getOpList().collect { it.getRep() } == ["\$initialise_machine(FALSE,FALSE)", "1 <-- read(1)", "nothing()", "end()"]

cond = new LTL("{loop = FALSE & loop2 = TRUE}")
cmd = new ExecuteUntilCommand(s, t.getCurrentState(), cond)
s.execute(cmd)
assert !cmd.isSuccess()
assert cmd.conditionNotReached()


cond = new LTL("{loop = 2}")
cmd = new ExecuteUntilCommand(s, t.getCurrentState(), cond)
s.execute(cmd)
assert !cmd.isSuccess()
assert cmd.hasTypeError()
s.animator.cli.shutdown()

m = api.b_load(dir + "/machines/SimpleDeadlock.mch")
s = m as StateSpace
cond = new LTL("{TRUE = FALSE}")
cmd = new ExecuteUntilCommand(s, s.getRoot(), cond)
s.execute(cmd)
assert !cmd.isSuccess()
assert cmd.isDeadlocked()

s.animator.cli.shutdown()
"for simple scheduler, it is possible to randomly animate until a condition is met. The result trace is as expected."