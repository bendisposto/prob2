import java.nio.file.Paths

import de.prob.animator.domainobjects.ComputationNotCompletedResult
import de.prob.model.eventb.EventBModel
import de.prob.model.eventb.ModelModifier
import de.prob.statespace.Trace

final s1 = api.eventb_load(Paths.get(dir, "Empty", "EmptyMachine.bcm").toString())
assert s1.mainComponent != null
final m = s1 as EventBModel

final mm2 = new ModelModifier(m).make {
	context(name: "EmptyContext") {
		enumerated_set(name: "mySet", constants: ["x", "y", "z"])
	}
}

final m2 = mm2.model
final s2 = m2.load(m2.EmptyMachine)
def t = s2 as Trace
t = t.$initialise_machine()
final x2 = t.evalCurrent("mySet")
assert x2.getValue() == "{x,y,z}"

final axmName = m2.EmptyContext.axioms[0].getName()
final mm3 = mm2.make {
	context(name: "EmptyContext") {
		removeSet "mySet"
		removeConstant "x"
		removeConstant "y"
		removeConstant "z"
		removeAxiom axmName
	}
}

final m3 = mm3.model
final s3 = m3.load(m3.EmptyMachine)

t = s3 as Trace
t = t.$initialise_machine()
final x3 = t.evalCurrent("mySet")
assert x3 instanceof ComputationNotCompletedResult

final mm4 = mm3.make {
	context(name: "EmptyContext") {
		constant "one"
		set "set"
		axiom ax1: "set = {one}"
	}
}

final m4 = mm4.model
final s4 = m4.load(m4.EmptyMachine)
t = s4 as Trace
t = t.$initialise_machine()
final x4 = t.evalCurrent("set")
assert x4.value == "{one}"

final mm5 = mm4.make { 
	context(name: "EmptyContext") {
		removeConstant "one"
		removeAxiom "ax1"
		removeSet "set"
	}
}

final m5 = mm5.model
final s5 = m5.load(m5.EmptyMachine)
t = s5 as Trace
t = t.$initialise_machine()
final x5 = t.evalCurrent("set")
assert x5 instanceof ComputationNotCompletedResult

final mm6 = mm5.make {
	machine(name: "EmptyMachine") {
		variable "x"
		invariant i1: "x : NAT"
		invariant i2: "x < 10"
		
		initialisation {
			action a1: "x := 0"
		}
		
		event(name: "inc") {
			parameter "y"
			where g1: "x + y < 10"
			then ac1: "x := x + y"
		}
	}
}

final m6 = mm6.getModel()
final s6 = m6.load(m6.EmptyMachine)
t = s6 as Trace
t = t.$initialise_machine()
t = t.inc("y = 4")
t = t.inc("y = 2")
final x61 = t.evalCurrent("x")
assert x61.value == "6"
t = t.inc("y = 3")
final x62 = t.evalCurrent("x")
assert x62.value == "9"
assert !t.canExecuteEvent("inc",["y = 1"])

final mm7 = mm6.make {
	machine(name: "EmptyMachine") {
		event(name: "inc") {
			removeParameter "y"
			removeGuard "g1"
			removeAction "ac1"
			
			guard "x < 4"
			action "x := x + 1"
		}
	}
}

final m7 = mm7.model
final s7 = m7.load(m7.EmptyMachine)
t = s7 as Trace
t = t.$initialise_machine().inc().inc().inc().inc()
final x7 = t.evalCurrent("x")
assert x7.value == "4"
assert !t.canExecuteEvent("inc",[])

final mm8 = mm7.make {
	machine(name: "EmptyMachine") {
		removeEvent "inc"
	}
}

final m8 = mm8.model
final s8 = m8.load(m8.EmptyMachine)
t = s8 as Trace
t = t.$initialise_machine()
assert !t.canExecuteEvent("inc", [])

final mm9 = mm8.make {
	machine(name: "EmptyMachine") {
		removeVariable "x"
		removeInvariant "i1"
		removeInvariant "i2"
		initialisation {
			removeAction "a1"
		}
	}
}
final m9 = mm9.model
final s9 = m9.load(m9.EmptyMachine)
t = s9 as Trace
t = t.$initialise_machine()
final x9 = t.evalCurrent("x")
assert x9 instanceof ComputationNotCompletedResult

final mm10 = mm9.make {
	machine(name: "EmptyMachine") {
		variable "x"
		invariant "x : NAT"
		initialisation {
			action a: "x := 1"
		}
		
		duplicateEvent("INITIALISATION", "hehe")
		
		event(name: "hehe") {
			removeAction "a"
			action a2: "x := x + 2"
		}
	}
}

final m10 = mm10.model
final s10 = m10.load(m10.EmptyMachine)
t = s10 as Trace
t = t.$initialise_machine()
t = t.hehe().hehe()
final x10 = t.evalCurrent("x")
assert x10.value == "5"

"the model API works correctly"
