import de.prob.animator.command.CbcSolveCommand
import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.EvalResult

// You can change the model you are testing here.
final s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")

def cbc_solve = { String str ->
	final cmd = new CbcSolveCommand(str as ClassicalB)
	s.execute(cmd)
	cmd
}

final cmd1 = cbc_solve("1 = 2")
final res1 = cmd1.getValue()
assert res1 instanceof EvalResult
assert res1.getValue() == "FALSE"
assert cmd1.getFreeVariables() == []

//TODO: Get another example for timeout ?
//res = cbc_solve("x : POW(NAT) & card(x) = 1000000000")
//assert res instanceof ComputationNotCompletedResult
//assert res.getReason() == "time out"

final cmd2 = cbc_solve("x : POW(NAT) & card(x) = 4 & y : x")
final res2 = cmd2.getValue()
assert res2 instanceof EvalResult
assert res2.getValue() == "TRUE"
assert cmd2.getFreeVariables().contains("x")
assert cmd2.getFreeVariables().contains("y")

// x is the set {0,1,2,3}. However, we can not rely on the order of elements
def set = res2.x.replaceAll("\\{","[")
set = set.replaceAll("\\}","]")
set = Eval.me(set)

assert set.size() == 4
assert set.containsAll([0,1,2,3])

assert ["0","1","2","3"].contains(res2.y)
// TODO
// Test after translation
// set = new HashSet()
// (0..3).each { set.add(it) }
// assert res.x == set
// assert res.y == 0



/* It is also possible to create a fourth result type from CbcSolveCommand
 This is a ComputationNotCompletedResult with reason "no solution found (but there might be one)"
 However, it was not obvious which predicate could produce this result, which is why it is not tested here */ 

"CBC solving works correctly"
