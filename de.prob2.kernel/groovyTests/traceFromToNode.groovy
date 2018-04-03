import java.nio.file.Paths

import de.prob.animator.command.FindTraceBetweenNodesCommand
import de.prob.animator.domainobjects.FormulaExpand
import de.prob.statespace.Trace

final s = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString())
final t = new Trace(s)
final t1 = t.$initialise_machine().new("pp = PID1")

final t2 = t1.new("pp = PID2").ready("rr = PID1")

final cmd1 = new FindTraceBetweenNodesCommand(s, t1.currentState.id, t2.currentState.id)
s.execute(cmd1)
final t3 = s.getTrace(cmd1)
assert t3 != null && !t3.getTransitionList(FormulaExpand.TRUNCATE).empty && t3.getTransitionList(FormulaExpand.TRUNCATE).size() == 2

final t4 = s.getTrace(t1.currentState.id, t2.currentState.id)
assert t4 != null && t3.getTransitionList(FormulaExpand.TRUNCATE).size() == t4.getTransitionList(FormulaExpand.TRUNCATE).size()

final t5 = t1
final cmd2 = new FindTraceBetweenNodesCommand(s, t1.currentState.id, t5.currentState.id)
s.execute(cmd2)
final t6 = s.getTrace(cmd2)
assert t6 != null && t6.getTransitionList(FormulaExpand.TRUNCATE).empty && t6.currentState == t1.currentState

"can create a trace between two given nodes"
