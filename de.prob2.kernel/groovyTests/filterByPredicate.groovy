import de.prob.animator.domainobjects.ClassicalB
import de.prob.statespace.Trace

// You can change the model you are testing here.
final s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
def t = new Trace(s)
t = t.anyEvent()

t = t.new()
t = t.ready()
t.getCurrentState().getOutTransitions().each { it.getDestination().explore() }
final allStates = s.getStatesFromPredicate("TRUE = TRUE" as ClassicalB)

def validateResults = { stateL, formula ->
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
