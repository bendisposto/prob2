import de.prob.animator.domainobjects.*
import de.prob.statespace.*

m = api.b_load(dir+"/machines/Simple.mch")
s = m as StateSpace
t = m as Trace
t = t.$initialise_machine()
t = t.read("xx = 1")
cond = new LTL("F Y [end]")
cmd = new ExecuteUntilCommand(s, t.getCurrentState(), cond)
s.execute(cmd)
t = t.addOps(cmd.getNewTransitions())
assert t.head.getOpList().collect { it.getRep() } == ["\$initialise_machine(FALSE,FALSE)", "1 <-- read(1)", "nothing()", "end()"]

s.animator.cli.shutdown();
"for simple scheduler, it is possible to randomly animate until a condition is met. The result trace is as expected."