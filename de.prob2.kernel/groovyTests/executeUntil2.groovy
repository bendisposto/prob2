import de.prob.animator.command.ExecuteUntilCommand
import de.prob.animator.domainobjects.LTL
import de.prob.statespace.Trace

final s1 = api.b_load(dir+File.separator+"machines"+File.separator+"Simple.mch")
def t = s1 as Trace
t = t.$initialise_machine()
t = t.read("xx = 1")
final cond1 = new LTL("F Y [end]")
final cmd1 = new ExecuteUntilCommand(s1, t.getCurrentState(), cond1)
s1.execute(cmd1)
assert cmd1.isSuccess()
t = t.addTransitions(cmd1.getNewTransitions())
assert t.getTransitionList(true).collect { it.getRep() } == ["\$initialise_machine()", "1 <-- read(1)", "nothing()", "end()"]

/*
TODO: The command is too slow to do this.
cond = new LTL("{loop = FALSE & loop2 = TRUE}")
cmd = new ExecuteUntilCommand(s1, t.getCurrentState(), cond)
s1.execute(cmd)
assert !cmd.isSuccess()
assert cmd.conditionNotReached()
 */

final cond2 = new LTL("{loop = 2}")
final cmd2 = new ExecuteUntilCommand(s1, t.getCurrentState(), cond2)
s1.execute(cmd2)
assert !cmd2.isSuccess()
assert cmd2.hasTypeError()
s1.animator.cli.shutdown()

final s2 = api.b_load(dir+File.separator+"machines"+File.separator+"SimpleDeadlock.mch")
final cond3 = new LTL("{TRUE = FALSE}")
final cmd3 = new ExecuteUntilCommand(s2, s2.getRoot(), cond3)
s2.execute(cmd3)
assert !cmd3.isSuccess()
assert cmd3.isDeadlocked()

"for simple scheduler, it is possible to randomly animate until a condition is met. The result trace is as expected."
