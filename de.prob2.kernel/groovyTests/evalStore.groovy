import java.nio.file.Paths

import de.prob.animator.command.EvalstoreCreateByStateCommand
import de.prob.animator.command.EvalstoreEvalCommand
import de.prob.animator.command.EvalstoreEvalCommand.EvalstoreResult
import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.EvalResult

final s = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString())

final cmd1 = new EvalstoreCreateByStateCommand("root")
s.execute(cmd1)
final store = cmd1.evalstoreId
assert store == 1

final cmd2 = new EvalstoreEvalCommand(store, "1+1" as ClassicalB)
s.execute(cmd2)
final res2 = cmd2.result
assert res2 instanceof EvalstoreResult
assert res2.result instanceof EvalResult
assert res2.result.value == "2"
assert !res2.hasTimeoutOccurred()
assert !res2.hasInterruptedOccurred()
assert res2.resultingStoreId == 1
assert res2.newIdentifiers == []

final cmd3 = new EvalstoreEvalCommand(store, "x : NATURAL & x > 4" as ClassicalB)
s.execute(cmd3)
final res3 = cmd3.result
assert res3 instanceof EvalstoreResult
assert res3.result instanceof EvalResult
assert res3.result.value == "true"
assert !res3.hasTimeoutOccurred()
assert !res3.hasInterruptedOccurred()
assert res3.resultingStoreId == 2
assert res3.newIdentifiers == ["x"]

"Eval store may be working correctly."
