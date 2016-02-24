import de.prob.animator.domainobjects.*
import de.prob.exception.ProBError
import de.prob.model.eventb.ModelModifier
import de.prob.statespace.*


s = api.eventb_load(dir+File.separator+"Empty"+File.separator+"EmptyMachine.bcm")
assert s.getMainComponent() != null
m = s as EventBModel

mm = new ModelModifier(m).make {
	context(name: "EmptyContext") {
		enumerated_set(name: "mySet", constants: ["x", "y", "z"])
	}
}

final m2 = mm.getModel()
s = m2.load(m2.EmptyMachine)
t = s as Trace
t = t.$initialise_machine()
x = t.evalCurrent("mySet")
assert x.getValue() == "{x,y,z}"

final axmName = m2.EmptyContext.axioms[0].getName()
mm = mm.make {
	context(name: "EmptyContext") {
		removeSet "mySet"
		removeConstant "x"
		removeConstant "y"
		removeConstant "z"
		removeAxiom axmName
	}
}

m3 = mm.getModel()
s = m3.load(m3.EmptyMachine)

t = s as Trace
t = t.$initialise_machine()
x = t.evalCurrent("mySet")
assert x instanceof ComputationNotCompletedResult

mm = mm.make {
	context(name: "EmptyContext") {
		constant "one"
		set "set"
		axiom ax1: "set = {one}"
	}
}

m4 = mm.getModel()
s = m4.load(m4.EmptyMachine)
t = s as Trace
t = t.$initialise_machine()
x = t.evalCurrent("set")
assert x.value == "{one}"

mm = mm.make { 
	context(name: "EmptyContext") {
		removeConstant "one"
		removeAxiom "ax1"
		removeSet "set"
	}
}

m5 = mm.getModel()
s = m5.load(m5.EmptyMachine)
t = s as Trace
t = t.$initialise_machine()
x = t.evalCurrent("set")
assert x instanceof ComputationNotCompletedResult

mm = mm.make {
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

final m6 = mm.getModel()
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

mm = mm.make {
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

final m7 = mm.getModel()
s = m7.load(m7.EmptyMachine)
t = s as Trace
t = t.$initialise_machine().inc().inc().inc().inc()
x = t.evalCurrent("x")
assert x.value == "4"
assert !t.canExecuteEvent("inc",[])

mm = mm.make {
	machine(name: "EmptyMachine") {
		removeEvent "inc"
	}
}

final m8 = mm.getModel()
s = m8.load(m8.EmptyMachine)
t = s as Trace
t = t.$initialise_machine()
assert !t.canExecuteEvent("inc", [])

mm = mm.make {
	machine(name: "EmptyMachine") {
		removeVariable "x"
		removeInvariant "i1"
		removeInvariant "i2"
		initialisation {
			removeAction "a1"
		}
	}
}
m9 = mm.getModel()
s = m9.load(m9.EmptyMachine)
t = s as Trace
t = t.$initialise_machine()
x = t.evalCurrent("x")
assert x instanceof ComputationNotCompletedResult

mm = mm.make {
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

m10 = mm.getModel()
s = m10.load(m10.EmptyMachine)
t = s as Trace
t = t.$initialise_machine()
t = t.hehe().hehe()
x = t.evalCurrent("x")
assert x.value == "5"

"the model API works correctly"