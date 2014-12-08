import de.prob.animator.command.EvalstoreCreateByStateCommand;
import de.prob.animator.command.EvalstoreEvalCommand;
import de.prob.animator.command.EvalstoreEvalCommand.EvalstoreResult;
import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
m = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
s = m as StateSpace

EvalstoreCreateByStateCommand c = new EvalstoreCreateByStateCommand(
	"root");
s.execute(c);
long store = c.getEvalstoreId();
assert store == 1

EvalstoreEvalCommand cmd = new EvalstoreEvalCommand(store, "1+1" as ClassicalB);
s.execute(cmd);
res = cmd.getResult()
assert res instanceof EvalstoreResult
assert res.result instanceof EvalResult
assert res.result.value == "2"
assert res.hasTimeoutOccurred() == false
assert res.hasInterruptedOccurred() == false
assert res.getResultingStoreId() == 1
assert res.getNewIdentifiers() == []

cmd = new EvalstoreEvalCommand(store, "x : NATURAL & x > 4" as ClassicalB);
s.execute(cmd);
res = cmd.getResult()
assert res instanceof EvalstoreResult
assert res.result instanceof EvalResult
assert res.result.getValue() == "true"
assert res.hasTimeoutOccurred() == false
assert res.hasInterruptedOccurred() == false
assert res.getResultingStoreId() == 2
assert res.getNewIdentifiers() == ["x"]


s.animator.cli.shutdown();
"Eval store MAY be working correctly, but NOBODY knows because who knows how the eval store works actually???"