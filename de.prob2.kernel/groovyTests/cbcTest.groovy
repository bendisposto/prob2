import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
m = api.b_load(dir+"/machines/scheduler.mch")
s = m as StateSpace

cbc_solve = { String str ->
	cmd = new CbcSolveCommand(str as ClassicalB)
	s.execute(cmd)
	cmd.getValue()
}

res = cbc_solve("1 = 2")
assert res instanceof ComputationNotCompletedResult
assert res.getReason() == "contradiction found"

//TODO: Get another example for timeout ?
//res = cbc_solve("x : POW(NAT) & card(x) = 1000000000")
//assert res instanceof ComputationNotCompletedResult
//assert res.getReason() == "time out"

res = cbc_solve("x : POW(NAT) & card(x) = 4 & y : x")
assert res instanceof EvalResult
assert res.getValue() == "TRUE"

assert res.x == "{0,2,1,3}"
assert res.y == "0"
// TODO
// Test after translation
// set = new HashSet()
// (0..3).each { set.add(it) }
// assert res.x == set
// assert res.y == 0



/* It is also possible to create a fourth result type from CbcSolveCommand
 This is a ComputationNotCompletedResult with reason "no solution found (but there might be one)"
 However, it was not obvious which predicate could produce this result, which is why it is not tested here */ 

s.animator.cli.shutdown();
"CBC solving works correctly"