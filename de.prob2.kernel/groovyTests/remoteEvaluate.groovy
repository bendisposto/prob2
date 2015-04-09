import de.prob.animator.command.RemoteEvaluateCommand
import de.prob.animator.command.RemoteEvaluateCommand.EEvaluationStrategy
import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
m = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
s = m as StateSpace
/*
cmd = new RemoteEvaluateCommand("a = 4 & m = 2 + 4", EEvaluationStrategy.EXISTENTIAL)
s.execute(cmd)
assert cmd.getResult() instanceof EvalResult
assert cmd.getResult().value == "TRUE"
assert cmd.getResult().a == "4"
assert cmd.getResult().m == "6"
assert cmd.getAtomicStrings() == ["a", "m"]
assert cmd.hasEnumerationWarnings() == false
assert cmd.getResultType() == "exists"

cmd = new RemoteEvaluateCommand("a : 1..3 & a < 4", EEvaluationStrategy.UNIVERSAL)
s.execute(cmd)
assert cmd.getResult() instanceof EvalResult
assert cmd.getResult().value == "FALSE"
assert cmd.getResult().a == "0"
assert cmd.hasEnumerationWarnings() == false
assert cmd.getResultType() == "forall"

cmd = new RemoteEvaluateCommand("a : 1..3 => a > 0 & a < 4", EEvaluationStrategy.UNIVERSAL)
s.execute(cmd)
assert cmd.getResult() instanceof EvalResult
assert cmd.getResult().value == "TRUE"
assert cmd.getResult().getSolutions().isEmpty()
assert cmd.hasEnumerationWarnings() == false
assert cmd.getResultType() == "forall"
*/
s.animator.cli.shutdown();
"add a description of the test here"