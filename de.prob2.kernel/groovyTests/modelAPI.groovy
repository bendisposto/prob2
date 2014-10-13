import de.prob.animator.domainobjects.*
import de.prob.exception.ProBError
import de.prob.model.eventb.ModelModifier
import de.prob.statespace.*


m = api.eventb_load(dir+"/Empty/EmptyMachine.bcm")
assert m.getMainComponent() != null
s = m as StateSpace

modelModifier = new ModelModifier(m)
m.getStateSpace().animator.cli.shutdown()

// Currently do not support adding refinements to machines
assert modelModifier.getContext("I-DONT-EXIST") == null
assert modelModifier.getMachine("I-DONT-EXIST") == null

contextModifier = modelModifier.getContext("EmptyContext")

block = contextModifier.addEnumeratedSet("mySet","x","y","z")
m2 = modelModifier.getModifiedModel()
t = m2 as Trace
t = t.$initialise_machine()
x = t.evalCurrent("mySet")
assert x.getValue() == "{x,y,z}"


modelModifier = new ModelModifier(m2)
m2.getStateSpace().animator.cli.shutdown()
contextModifier = modelModifier.getContext("EmptyContext")

assert contextModifier.removeEnumeratedSet(block)
m3 = modelModifier.getModifiedModel()
t = m3 as Trace
t = t.$initialise_machine()
x = t.evalCurrent("mySet")
assert x instanceof ComputationNotCompletedResult

modelModifier = new ModelModifier(m3)
m3.getStateSpace().animator.cli.shutdown()
contextModifier = modelModifier.getContext("EmptyContext")

constant = contextModifier.addConstant("one")
set = contextModifier.addSet("set")
axiom = contextModifier.addAxiom("set = {one}")
m4 = modelModifier.getModifiedModel()
t = m4 as Trace
t = t.$initialise_machine()
x = t.evalCurrent("set")
assert x.value == "{one}"

modelModifier = new ModelModifier(m4)
m4.getStateSpace().animator.cli.shutdown()
contextModifier = modelModifier.getContext("EmptyContext")

assert contextModifier.removeConstant(constant)
assert contextModifier.removeAxiom(axiom)
assert contextModifier.removeSet(set)
m5 = modelModifier.getModifiedModel()
t = m5 as Trace
t = t.$initialise_machine()
x = t.evalCurrent("set")
assert x instanceof ComputationNotCompletedResult

modelModifier = new ModelModifier(m5)
m5.getStateSpace().animator.cli.shutdown()
machineModifier = modelModifier.getMachine("EmptyMachine")

varBlock = machineModifier.addVariable("x", "x : NAT", "x := 0")
invariant = machineModifier.addInvariant("x < 10")
eventModifier = machineModifier.addEvent("inc")

paramBlock = eventModifier.addParameter("y", "y : NAT")
guard = eventModifier.addGuard("x + y < 10")
action = eventModifier.addAction("x := x + y")
m6 = modelModifier.getModifiedModel()
t = m6 as Trace
t = t.$initialise_machine()
t = t.inc("y = 4")
t = t.inc("y = 2")
x = t.evalCurrent("x")
assert x.value == "6"
t = t.inc("y = 3")
x = t.evalCurrent("x")
assert x.value == "9"
assert !t.canExecuteEvent("inc",["y = 1"])

modelModifier = new ModelModifier(m6)
m6.getStateSpace().animator.cli.shutdown()
machineModifier = modelModifier.getMachine("EmptyMachine")

eventModifier = machineModifier.getEvent("inc")
assert eventModifier.removeParameter(paramBlock)
assert eventModifier.removeGuard(guard)
assert eventModifier.removeAction(action)

guard = eventModifier.addGuard("x < 4")
action = eventModifier.addAction("x := x + 1")
m7 = modelModifier.getModifiedModel()
t = m7 as Trace
t = t.$initialise_machine().inc().inc().inc().inc()
x = t.evalCurrent("x")
assert x.value == "4"
assert !t.canExecuteEvent("inc",[])

modelModifier = new ModelModifier(m7)
m7.getStateSpace().animator.cli.shutdown()
machineModifier = modelModifier.getMachine("EmptyMachine")

assert machineModifier.removeEvent(eventModifier.getEvent())
m8 = modelModifier.getModifiedModel()
t = m8 as Trace
t = t.$initialise_machine()
assert !t.canExecuteEvent("inc", [])

modelModifier = new ModelModifier(m8)
m8.getStateSpace().animator.cli.shutdown()
machineModifier = modelModifier.getMachine("EmptyMachine")

assert machineModifier.removeVariableBlock(varBlock)
assert machineModifier.removeInvariant(invariant)
m9 = modelModifier.getModifiedModel()
t = m9 as Trace
t = t.$initialise_machine()
x = t.evalCurrent("x")
assert x instanceof ComputationNotCompletedResult

m9.getStateSpace().animator.cli.shutdown()
"the model API works correctly"