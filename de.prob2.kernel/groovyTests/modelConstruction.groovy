
import de.prob.animator.domainobjects.*
import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.translate.*
import de.prob.statespace.*

mm = new ModelModifier()
mm.make {
	context(name: "levels") {
		theorem always_true: "1 < 5"
		constants "TOP", "BOTTOM"
		axioms top_axm: "TOP = 5",
		       bottom_axm: "BOTTOM = 1"
	}
	
	machine(name: "lift0", sees: ["levels"]) {
		
		var_block name: "level", 
		          invariant: [inv_level: "level : BOTTOM..TOP"],
				  init: [act_level: "level := BOTTOM"]
				  
		var_block name: "door_open",
				  invariant: [inv_door: "door_open : BOOL"],
				  init: [act_door: "door_open := FALSE"]
				  
		invariant always_true: "1 > 0", true
		theorem   also_always_true: "5 < 6"
		invariant "inv2", "level > 0"
		
		event(name: "up") {
			guard level_grd: "level < TOP"
			guard door_grd: "door_open = FALSE"
			action move_up: "level := level + 1" 
		}
		
		event(name: "down") {
			theorem always_true: "9 : 1..10"
			guard level_grd: "level > BOTTOM"
			guard door_grd: "door_open = FALSE"
			action move_down: "level := level - 1"
		}
		
		event(name: "open_door") {
			guard door_grd: "door_open = FALSE"
			action open: "door_open := TRUE"
		}
		
		event(name: "close_door") {
			guard door_grd: "door_open = TRUE"
			action close: "door_open := FALSE"
		}
	}
	
	context(name: "IDoNothing") {
		axiom this_is_true: "1 = 1"
	}
	
	context(name: "door", extends: ["IDoNothing"]) {
		enumerated_set name: "door_state",
		               constants: ["open", "closed"]
	}
	
	machine(name: "lift1", refines: ["lift0"], sees: ["door","levels"]) {
		variables "door", "level"
		invariants door_inv: "door : door_state",
				   level_inv: "level : BOTTOM..TOP",
				   gluing: "door_open = TRUE <=> door = open"
				   
		initialisation {
			actions level: "level := BOTTOM",
			        door: "door := closed"
		}
		
		event(name: "up", refines: "up") {
			guards level_grd: "level < TOP",
				   door_grd: "door = closed"
		
			action move_up: "level := level + 1" 
		}
		
		event(name: "down", refines: "down") {
			guard level_grd: "level > BOTTOM"
			guard door_grd: "door = closed"
			action move_down: "level := level - 1"
		}
		
		event(name: "open_door", refines: "open_door") {
			guard door_grd: "door = closed"
			action open: "door := open"
		}
		
		event(name: "close_door", refines: "close_door") {
			guard door_grd: "door = open"
			action close: "door := closed"
		}
	}
}

assert mm.temp.levels.axioms.always_true.isTheorem()

lift0 = mm.temp.lift0
assert lift0 != null
assert lift0.variables.level != null
assert lift0.invariants.inv_level.getPredicate().getCode() == "level : BOTTOM..TOP"
assert lift0.invariants.inv2.getPredicate().getCode() == "level > 0"
assert lift0.invariants.always_true.isTheorem()
assert lift0.invariants.also_always_true.isTheorem()
init = lift0.events.INITIALISATION
init.actions.act_level.getCode().getCode() == "level := 1"
init.actions.act_door.getCode().getCode() == "door_open := FALSE"
assert lift0.events.down.guards.always_true.isTheorem()

assert mm.temp.door.Extends[0].getName() == "IDoNothing"

//File dir = mm.writeToRodin("MyLift", dir)
//dir.deleteDir()

m = mm.getModifiedModel("lift1")
s = m as StateSpace
t = m as Trace

t = t.$setup_constants()
t = t.$initialise_machine()
assert !t.canExecuteEvent("down",[])
assert t.canExecuteEvent("up", [])
t = t.up().up().up().up()
assert t.canExecuteEvent("down",[])
assert !t.canExecuteEvent("up",[])
t = t.down()
assert t.canExecuteEvent("down",[]) && t.canExecuteEvent("up",[])
t = t.open_door()
assert !t.canExecuteEvent("down",[])
assert !t.canExecuteEvent("up",[])
assert t.evalCurrent("door").value == "open"

s.animator.cli.shutdown();


"it is possible to construct a model"