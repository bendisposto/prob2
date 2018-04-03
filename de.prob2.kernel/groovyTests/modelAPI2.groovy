import java.nio.file.Paths

import de.prob.model.eventb.EventBModel
import de.prob.model.eventb.ModelModifier
import de.prob.statespace.Trace

final s1 = api.eventb_load(Paths.get(dir, "Empty", "EmptyMachine.bcm").toString())
assert s1.mainComponent != null
final m1 = s1 as EventBModel

final mm2 = new ModelModifier(m1).make {
	machine(name: "EmptyMachine") {
		var "x", "x : NAT", "x := 0"
		
		event(name: "event1") {
			when g: "x > 0"
			then "x := x + 2"
		}
		
		event(name: "event2") {
			when g: "x > 0"
			then "x := x + 1"
		}
	}
}

final m2 = mm2.model
final s2 = m2.load(m2.EmptyMachine)
t = s2 as Trace
t = t.$initialise_machine()
assert !t.canExecuteEvent("event1", [])
assert !t.canExecuteEvent("event2", [])

final mm3 = mm2.make {
	machine(name: "EmptyMachine") {
		event(name: "event1") {
			removeGuard "g"
		}
		
		event(name: "event2") {
			removeGuard "g"
		}
	}
}

final m3 = mm3.model
final s3 = m3.load(m3.EmptyMachine)
t = s3 as Trace
t = t.$initialise_machine()
assert t.canExecuteEvent("event1", [])
assert t.canExecuteEvent("event2", [])
t = t.event1().event1().event2()
assert t.evalCurrent("x").value == "5"

"the model API works correctly"
