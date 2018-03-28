import de.prob.animator.command.EvaluateRegisteredFormulasCommand
import de.prob.animator.command.RegisterFormulaCommand
import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.EvaluationErrorResult
import de.prob.statespace.Trace;

final s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")

def t = s as Trace
t = t.$initialise_machine()
t = t.new("pp=PID1")
final f = "1+1" as ClassicalB
final f2 = "active" as ClassicalB
final cmd1 = new RegisterFormulaCommand(f)
s.execute(cmd1)
final cmd2 = new RegisterFormulaCommand(f2)
s.execute(cmd2)
final cmd3 = new EvaluateRegisteredFormulasCommand("3",[f,f2])
s.execute(cmd3)
assert cmd3.getResults().get(f).getValue() == "2"
assert cmd3.getResults().get(f2).getValue() == "{}"
final cmd4 = new EvaluateRegisteredFormulasCommand("root",[f,f2])
s.execute(cmd4)
assert cmd4.getResults().get(f).getValue() == "2"
assert cmd4.getResults().get(f2) instanceof EvaluationErrorResult

"It is possible to register formulas and asynchronously evaluate them later"
