import de.prob.animator.domainobjects.*
import de.prob.exception.ProBError
import de.prob.model.eventb.ModelModifier
import de.prob.statespace.*


s = api.eventb_load(dir+File.separator+"Empty"+File.separator+"EmptyMachine.bcm")
assert s.getMainComponent() != null
m = s as EventBModel

mm = new ModelModifier(m).make {
	machine(name: "EmptyMachine") {
		var_block "x", "x : NAT", "x := 0"
		
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

m = mm.getModel()
s = m.load(m.EmptyMachine)
t = s as Trace
t = t.$initialise_machine()
assert !t.canExecuteEvent("event1", [])
assert !t.canExecuteEvent("event2", [])

mm = mm.make {
	machine(name: "EmptyMachine") {
		event(name: "event1") {
			removeGuard "g"
		}
		
		event(name: "event2") {
			removeGuard "g"
		}
	}
}

m = mm.getModel()
s = m.load(m.EmptyMachine)
t = s as Trace
t = t.$initialise_machine()
assert t.canExecuteEvent("event1", [])
assert t.canExecuteEvent("event2", [])
t = t.event1().event1().event2()
assert t.evalCurrent("x").value == "5"

"the model API works correctly"
