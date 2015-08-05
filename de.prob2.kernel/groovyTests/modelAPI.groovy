import de.prob.animator.domainobjects.*
import de.prob.exception.ProBError
import de.prob.model.eventb.ModelModifier
import de.prob.statespace.*

/*
s = api.eventb_load(dir+File.separator+"Empty"+File.separator+"EmptyMachine.bcm")
assert s.getMainComponent() != null
m = s as EventBModel
s.animator.cli.shutdown()

mm = new ModelModifier(m).make {
	context(name: "EmptyContext") {
		enumerated_set(name: "mySet", constants: ["x", "y", "z"])
	}
}

m2 = mm.getModel()
println m2.EmptyContext.children
s = m2.load(m2.EmptyMachine)
t = s as Trace
t = t.$setup_constants()
t = t.$initialise_machine()
x = t.evalCurrent("mySet")
assert x.getValue() == "{x,y,z}"
s.animator.cli.shutdown()


mm = mm.make {
	context(name: "EmptyContext") {
		removeSet(m2.EmptyContext.sets.mySet)
		m2.EmptyContext.constants.each {
			remove(it)
		}
	}
}

m3 = mm.getModel()
s = m3.load(m3.EmptyMachine)
t = s as Trace
t = t.$initialise_machine()
x = t.evalCurrent("mySet")
assert x instanceof ComputationNotCompletedResult
s.animator.cli.shutdown()
/*
modelModifier = new ModelModifier(m3)

contextModifier = modelModifier.getContext("EmptyContext")

constant = contextModifier.addConstant("one")
set = contextModifier.addSet("set")
axiom = contextModifier.addAxiom("set = {one}")
m4 = modelModifier.getModifiedModel()
s = m4.load(m4.EmptyMachine)
t = s as Trace
t = t.$initialise_machine()
x = t.evalCurrent("set")
assert x.value == "{one}"

modelModifier = new ModelModifier(m4)
s.animator.cli.shutdown()
contextModifier = modelModifier.getContext("EmptyContext")

assert contextModifier.removeConstant(constant)
assert contextModifier.removeAxiom(axiom)
assert contextModifier.removeSet(set)
m5 = modelModifier.getModifiedModel()
s = m5.load(m5.EmptyMachine)
t = s as Trace
t = t.$initialise_machine()
x = t.evalCurrent("set")
assert x instanceof ComputationNotCompletedResult

modelModifier = new ModelModifier(m5)
s.animator.cli.shutdown()
machineModifier = modelModifier.getMachine("EmptyMachine")

varBlock = machineModifier.addVariable("x", "x : NAT", "x := 0")
invariant = machineModifier.addInvariant("x < 10")
eventModifier = machineModifier.addEvent("inc")

paramBlock = eventModifier.addParameter("y", "y : NAT")
guard = eventModifier.addGuard("x + y < 10")
action = eventModifier.addAction("x := x + y")
m6 = modelModifier.getModifiedModel()
s = m6.load(m6.EmptyMachine)
t = s as Trace
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
s.animator.cli.shutdown()
machineModifier = modelModifier.getMachine("EmptyMachine")

eventModifier = machineModifier.getEvent("inc")
assert eventModifier.removeParameter(paramBlock)
assert eventModifier.removeGuard(guard)
assert eventModifier.removeAction(action)

guard = eventModifier.addGuard("x < 4")
action = eventModifier.addAction("x := x + 1")
m7 = modelModifier.getModifiedModel()
s = m7.load(m7.EmptyMachine)
t = s as Trace
t = t.$initialise_machine().inc().inc().inc().inc()
x = t.evalCurrent("x")
assert x.value == "4"
assert !t.canExecuteEvent("inc",[])

modelModifier = new ModelModifier(m7)
s.animator.cli.shutdown()
machineModifier = modelModifier.getMachine("EmptyMachine")

assert machineModifier.removeEvent(eventModifier.getEvent())
m8 = modelModifier.getModifiedModel()
s = m8.load(m8.EmptyMachine)
t = s as Trace
t = t.$initialise_machine()
assert !t.canExecuteEvent("inc", [])

modelModifier = new ModelModifier(m8)
s.animator.cli.shutdown()
machineModifier = modelModifier.getMachine("EmptyMachine")

assert machineModifier.removeVariableBlock(varBlock)
assert machineModifier.removeInvariant(invariant)
m9 = modelModifier.getModifiedModel()
s = m9.load(m9.EmptyMachine)
t = s as Trace
t = t.$initialise_machine()
x = t.evalCurrent("x")
assert x instanceof ComputationNotCompletedResult

modelModifier = new ModelModifier(m9)
s.animator.cli.shutdown()
machineModifier = modelModifier.getMachine("EmptyMachine")

xBlock = machineModifier.addVariable("x", "x : NAT", "x := 1")
init = machineModifier.getMachine().events.INITIALISATION
clonedInit = machineModifier.duplicateEvent(init, "hehe")
act = xBlock.initialisationAction
assert clonedInit.removeAction(act)
clonedInit.addAction("x := x + 2")
m10 = modelModifier.getModifiedModel()
s = m10.load(m10.EmptyMachine)
t = s as Trace
t = t.$initialise_machine()
t = t.hehe().hehe()
x = t.evalCurrent("x")
assert x.value == "5"

s.animator.cli.shutdown()*/
"the model API works correctly"