import de.prob.animator.command.EvalstoreCreateByStateCommand
import de.prob.animator.command.EvalstoreEvalCommand
import de.prob.animator.command.EvalstoreEvalCommand.EvalstoreResult
import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.EvalResult

// You can change the model you are testing here.
final s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")

final cmd1 = new EvalstoreCreateByStateCommand("root");
s.execute(cmd1);
final store = cmd1.getEvalstoreId();
assert store == 1

final cmd2 = new EvalstoreEvalCommand(store, "1+1" as ClassicalB);
s.execute(cmd2);
final res2 = cmd2.getResult()
assert res2 instanceof EvalstoreResult
assert res2.result instanceof EvalResult
assert res2.result.value == "2"
assert res2.hasTimeoutOccurred() == false
assert res2.hasInterruptedOccurred() == false
assert res2.getResultingStoreId() == 1
assert res2.getNewIdentifiers() == []

final cmd3 = new EvalstoreEvalCommand(store, "x : NATURAL & x > 4" as ClassicalB);
s.execute(cmd3);
final res3 = cmd3.getResult()
assert res3 instanceof EvalstoreResult
assert res3.result instanceof EvalResult
assert res3.result.getValue() == "true"
assert res3.hasTimeoutOccurred() == false
assert res3.hasInterruptedOccurred() == false
assert res3.getResultingStoreId() == 2
assert res3.getNewIdentifiers() == ["x"]

"Eval store may be working correctly."
