import de.prob.statespace.*
import de.prob.animator.command.*
import de.prob.animator.domainobjects.EvaluationErrorResult;

m = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
s = m as StateSpace
t = s as Trace
t = t.$initialise_machine()
t = t.new("pp=PID1")
f = "1+1" as ClassicalB
f2 = "active" as ClassicalB
cmd = new RegisterFormulaCommand(f)
s.execute(cmd)
cmd = new RegisterFormulaCommand(f2)
s.execute(cmd)
cmd = new EvaluateRegisteredFormulasCommand("3",[f,f2])
s.execute(cmd)
assert cmd.getResults().get(f).getValue() == "2"
assert cmd.getResults().get(f2).getValue() == "{}"
cmd = new EvaluateRegisteredFormulasCommand("root",[f,f2])
s.execute(cmd)
assert cmd.getResults().get(f).getValue() == "2"
assert cmd.getResults().get(f2) instanceof EvaluationErrorResult

s.animator.cli.shutdown();
"It is possible to register formulas and asynchronously evaluate them later"