import de.prob.animator.domainobjects.*
import de.prob.statespace.*

m = api.b_load(dir+"/machines/scheduler.mch")
s = m as StateSpace
t = new Trace(s)

boolean thrown = false
try {
  cmd = new GetShortestTraceCommand(s, "7")
  s.execute(cmd)
  assert cmd.getOperationIds().isEmpty()
} catch(RuntimeException e) {
	thrown = true
}
assert thrown == true

t = t.randomAnimation(10)
assert s[4] != null
cmd = new GetShortestTraceCommand(s, "4")
s.execute(cmd)
ops = cmd.getNewTransitions()

assert ops != null
assert !ops.isEmpty()
t = s.getTrace("4")
opList = t.getTransitionList()
assert !opList.isEmpty()
assert ops.size() == opList.size()
len = ops.size()
(0..(len-1)).each {
	assert opList[it] == ops[it]
}

cmd = new FindValidStateCommand(s, "card(waiting) = 2" as ClassicalB)
s.execute(cmd)
t = cmd.getTrace(s)
opList = t.getTransitionList(true)
assert opList.size() == 1
assert opList[0].getName() == "find_valid_state"

t = s.getTraceToState("pp : waiting" as ClassicalB)
assert t != null
ops = t.getTransitionList(true)
assert opList.size() == 1
assert opList[0].getName() == "find_valid_state"

s.animator.cli.shutdown();
"Finding trace through current state space works"