import java.nio.file.Paths

import de.prob.animator.command.CbcSolveCommand
import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.EvalResult

final s = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString())

def cbcSolve = {String formula ->
	final cmd = new CbcSolveCommand(formula as ClassicalB)
	s.execute(cmd)
	cmd
}

final cmd1 = cbcSolve("1 = 2")
final res1 = cmd1.value
assert res1 instanceof EvalResult
assert res1.value == "FALSE"
assert cmd1.freeVariables == []

// TODO: Get another example for timeout ?
// final res = cbcSolve("x : POW(NAT) & card(x) = 1000000000")
// assert res instanceof ComputationNotCompletedResult
// assert res.reason == "time out"

final cmd2 = cbcSolve("x : POW(NAT) & card(x) = 4 & y : x")
final res2 = cmd2.value
assert res2 instanceof EvalResult
assert res2.value == "TRUE"
assert "x" in cmd2.freeVariables
assert "y" in cmd2.freeVariables

// x is the set {0,1,2,3}. However, we can not rely on the order of elements
final setString = res2.x
assert setString.startsWith("{")
assert setString.endsWith("}")
final elements = setString[1..-2].split(",").toList().toSet()
assert elements == ["0", "1", "2", "3"].toSet()

assert res2.y in ["0", "1", "2", "3"]
// TODO
// Test after translation
// final set = new HashSet()
// (0..3).each { set.add(it) }
// assert res.x == set
// assert res.y == 0



/* It is also possible to create a fourth result type from CbcSolveCommand
 This is a ComputationNotCompletedResult with reason "no solution found (but there might be one)"
 However, it was not obvious which predicate could produce this result, which is why it is not tested here */ 

"CBC solving works correctly"
