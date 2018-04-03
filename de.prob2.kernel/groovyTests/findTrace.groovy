import java.nio.file.Paths

import de.prob.animator.command.FindStateCommand
import de.prob.animator.command.GetShortestTraceCommand
import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.FormulaExpand
import de.prob.statespace.Trace

final s = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString())
def t = new Trace(s)

final cmd1 = new GetShortestTraceCommand(s, "7")
s.execute(cmd1)
assert !cmd1.traceFound()

t = t.randomAnimation(10)
assert s[4] != null
final cmd2 = new GetShortestTraceCommand(s, "4")
s.execute(cmd2)
final ops2 = cmd2.newTransitions

assert ops2 != null
assert !ops2.empty
t = s.getTrace("4")
final opList2 = t.getTransitionList(FormulaExpand.EXPAND)
assert !opList2.empty
assert ops2.size() == opList2.size()
final len = ops2.size()
(0..(len-1)).each {
	assert opList2[it] == ops2[it]
}

final cmd3 = new FindStateCommand(s, "card(waiting) = 2" as ClassicalB, true)
s.execute(cmd3)
t = cmd3.getTrace(s)
final opList3 = t.getTransitionList(true, FormulaExpand.EXPAND)
assert opList3.size() == 1
assert opList3[0].name == "find_valid_state"

t = s.getTraceToState("pp : waiting" as ClassicalB)
assert t != null
final ops3 = t.getTransitionList(true, FormulaExpand.EXPAND)
assert opList3.size() == 1
assert opList3[0].name == "find_valid_state"

"Finding trace through current state space works"
