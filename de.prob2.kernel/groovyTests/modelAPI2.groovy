import de.prob.animator.domainobjects.*
import de.prob.exception.ProBError
import de.prob.model.eventb.ModelModifier
import de.prob.statespace.*


m = api.eventb_load(dir+"/Empty/EmptyMachine.bcm")
assert m.getMainComponent() != null
s = m as StateSpace

modelModifier = new ModelModifier(m)
m.getStateSpace().animator.cli.shutdown()
machineModifier = modelModifier.getMachine("EmptyMachine")

machineModifier.addVariable("x", "x : NAT", "x := 0")
eventM = machineModifier.addEvent("event1")
guard = eventM.addGuard("x > 0")
act = eventM.addAction("x := x + 2")
eventM2 = machineModifier.addEvent("event2")
guard2 = eventM2.addGuard("x > 0")
assert guard == guard2
act2 = eventM.addAction("x := x + 1")
m = modelModifier.getModifiedModel()
t = m as Trace
t = t.$initialise_machine()
assert !t.canExecuteEvent("event1", [])
assert !t.canExecuteEvent("event2", [])

modelModifier = new ModelModifier(m)
m.getStateSpace().animator.cli.shutdown()
machineModifier = modelModifier.getMachine("EmptyMachine")

events = machineModifier.getMachine().events.findAll { it.getName() != "INITIALISATION" }
events.each { assert machineModifier.getEvent(it.name).removeGuard(guard) }
m = modelModifier.getModifiedModel()
t = m as Trace
t = t.$initialise_machine()
assert t.canExecuteEvent("event1", [])
assert t.canExecuteEvent("event2", [])
t = t.event1().event1().event2()
assert t.evalCurrent("x").value == "5"

m.getStateSpace().animator.cli.shutdown()
"the model API works correctly"
