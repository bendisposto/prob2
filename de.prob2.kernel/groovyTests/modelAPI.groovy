import de.prob.animator.domainobjects.*
import de.prob.exception.ProBError
import de.prob.model.eventb.ModelModifier
import de.prob.statespace.*

// You can change the model you are testing here.
m = api.eventb_load(dir+"/Empty/EmptyMachine.bcm")
assert m.getMainComponent() != null
s = m as StateSpace

modelModifier = new ModelModifier(m)
m.getStateSpace().animator.cli.shutdown()
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

"the model API works correctly"