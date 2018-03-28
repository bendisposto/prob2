import de.prob.animator.command.FindValidStateCommand
import de.prob.animator.command.GetShortestTraceCommand
import de.prob.animator.domainobjects.ClassicalB
import de.prob.statespace.Trace

final s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
def t = new Trace(s)

final cmd1 = new GetShortestTraceCommand(s, "7")
s.execute(cmd1)
assert !cmd1.traceFound()

t = t.randomAnimation(10)
assert s[4] != null
final cmd2 = new GetShortestTraceCommand(s, "4")
s.execute(cmd2)
final ops2 = cmd2.getNewTransitions()

assert ops2 != null
assert !ops2.isEmpty()
t = s.getTrace("4")
final opList2 = t.getTransitionList()
assert !opList2.isEmpty()
assert ops2.size() == opList2.size()
final len = ops2.size()
(0..(len-1)).each {
	assert opList2[it] == ops2[it]
}

final cmd3 = new FindValidStateCommand(s, "card(waiting) = 2" as ClassicalB)
s.execute(cmd3)
t = cmd3.getTrace(s)
final opList3 = t.getTransitionList(true)
assert opList3.size() == 1
assert opList3[0].getName() == "find_valid_state"

t = s.getTraceToState("pp : waiting" as ClassicalB)
assert t != null
final ops3 = t.getTransitionList(true)
assert opList3.size() == 1
assert opList3[0].getName() == "find_valid_state"

"Finding trace through current state space works"
