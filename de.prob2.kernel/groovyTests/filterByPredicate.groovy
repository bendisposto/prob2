import de.prob.animator.domainobjects.*
import de.prob.statespace.*


// You can change the model you are testing here.
s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
t = new Trace(s)
t = t.anyEvent()

t = t.new()
t = t.ready()
t.getCurrentState().getOutTransitions().each { it.getDestination().explore() }
allStates = s.getStatesFromPredicate("TRUE = TRUE" as ClassicalB)

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

f = "card(waiting) = 1" as ClassicalB
states = s.getStatesFromPredicate(f)
validateResults(states, f)

f = "card(active) = 2" as ClassicalB
states = s.getStatesFromPredicate(f)
validateResults(states, f)

f = "ready = {}" as ClassicalB
states = s.getStatesFromPredicate(f)
validateResults(states, f)

"filtering by predicate works successfully"