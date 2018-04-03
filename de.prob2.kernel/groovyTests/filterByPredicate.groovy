import java.nio.file.Paths

import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.FormulaExpand
import de.prob.animator.domainobjects.IEvalElement
import de.prob.statespace.State
import de.prob.statespace.Trace

final s = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString())
def t = new Trace(s)
t = t.anyEvent()

t = t.new()
t = t.ready()
t.currentState.getOutTransitions(FormulaExpand.EXPAND).each { it.destination.explore() }
final allStates = s.getStatesFromPredicate("TRUE = TRUE" as ClassicalB)

final validateResults = {List<State> stateL, IEvalElement formula ->
	allStates.each {
		if (stateL.contains(it)) {
			if (s.canBeEvaluated(it)) {
				assert s.eval(it, [formula])[0].value == "TRUE"
			}
		} else {
			assert s.canBeEvaluated(it) && s.eval(it, [formula])[0].value != "TRUE"
		}
	}
}

final f1 = "card(waiting) = 1" as ClassicalB
final states1 = s.getStatesFromPredicate(f1)
validateResults(states1, f1)

final f2 = "card(active) = 2" as ClassicalB
final states2 = s.getStatesFromPredicate(f2)
validateResults(states2, f2)

final f3 = "ready = {}" as ClassicalB
final states3 = s.getStatesFromPredicate(f3)
validateResults(states3, f3)

"filtering by predicate works successfully"
