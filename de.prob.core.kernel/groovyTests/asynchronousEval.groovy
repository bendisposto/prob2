import de.prob.statespace.*
import de.prob.animator.command.*

m = api.b_load(dir+"/machines/scheduler.mch")
s = m as StateSpace
t = s as Trace
t = t.add(0)
t = t.anyEvent()
f = "1+1" as ClassicalB
cmd = new RegisterFormulaCommand(f)
s.execute(cmd)
cmd = new EvaluateRegisteredFormulasCommand("3",[f])
s.execute(cmd)
assert cmd.getResults().get(f).getValue() == "2"
cmd = new EvaluateRegisteredFormulasCommand("root",[f])
s.execute(cmd)
assert cmd.getResults().isEmpty()

s.animator.cli.shutdown();
