import de.prob.animator.domainobjects.*
import de.prob.exception.ProBError
import de.prob.model.eventb.ModelModifier
import de.prob.statespace.*


/*s = api.eventb_load(dir+File.separator+"Empty"+File.separator+"EmptyMachine.bcm")
assert s.getMainComponent() != null
m = s as EventBModel

modelModifier = new ModelModifier(m)
s.animator.cli.shutdown()
machineModifier = modelModifier.getMachine("EmptyMachine")

machineModifier.addVariable("x", "x : NAT", "x := 0")
eventM = machineModifier.addEvent("event1")
guard = eventM.addGuard("x > 0")
act = eventM.addAction("x := x + 2")
eventM2 = machineModifier.addEvent("event2")
guard2 = eventM2.addGuard("x > 0")
assert guard == guard2
act2 = eventM2.addAction("x := x + 1")
m = modelModifier.getModifiedModel()
s = m.load(m.EmptyMachine)
t = s as Trace
t = t.$initialise_machine()
assert !t.canExecuteEvent("event1", [])
assert !t.canExecuteEvent("event2", [])

modelModifier = new ModelModifier(m)
s.animator.cli.shutdown()
machineModifier = modelModifier.getMachine("EmptyMachine")

events = machineModifier.getMachine().events.findAll { it.getName() != "INITIALISATION" }
assert events.size() == 2
events.each { assert machineModifier.getEvent(it.getName()).removeGuard(guard) }
m = modelModifier.getModifiedModel()
s = m.load(m.EmptyMachine)
t = s as Trace
t = t.$initialise_machine()
assert t.canExecuteEvent("event1", [])
assert t.canExecuteEvent("event2", [])
t = t.event1().event1().event2()
assert t.evalCurrent("x").value == "5"

s.animator.cli.shutdown()*/
"the model API works correctly"
