import java.nio.file.Paths

import de.prob.animator.command.ExecuteUntilCommand
import de.prob.animator.domainobjects.FormulaExpand
import de.prob.animator.domainobjects.LTL
import de.prob.statespace.Trace

final s1 = api.b_load(Paths.get(dir, "machines", "Simple.mch").toString())
def t = s1 as Trace
t = t.$initialise_machine()
t = t.read("xx = 1")
final cond1 = new LTL("F Y [end]")
final cmd1 = new ExecuteUntilCommand(s1, t.currentState, cond1)
s1.execute(cmd1)
assert cmd1.success
t = t.addTransitions(cmd1.newTransitions)
assert t.getTransitionList(true, FormulaExpand.EXPAND).collect {it.rep} == ["\$initialise_machine()", "1 <-- read(1)", "nothing()", "end()"]

/*
TODO: The command is too slow to do this.
cond = new LTL("{loop = FALSE & loop2 = TRUE}")
cmd = new ExecuteUntilCommand(s1, t.getCurrentState(), cond)
s1.execute(cmd)
assert !cmd.isSuccess()
assert cmd.conditionNotReached()
 */

final cond2 = new LTL("{loop = 2}")
final cmd2 = new ExecuteUntilCommand(s1, t.currentState, cond2)
s1.execute(cmd2)
assert !cmd2.success
assert cmd2.hasTypeError()
s1.kill()

final s2 = api.b_load(Paths.get(dir, "machines", "SimpleDeadlock.mch").toString())
final cond3 = new LTL("{TRUE = FALSE}")
final cmd3 = new ExecuteUntilCommand(s2, s2.root, cond3)
s2.execute(cmd3)
assert !cmd3.success
assert cmd3.deadlocked

"for simple scheduler, it is possible to randomly animate until a condition is met. The result trace is as expected."
