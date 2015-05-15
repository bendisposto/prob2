
import de.prob.animator.domainobjects.*
import de.prob.statespace.*
import de.prob.model.eventb.Event;
import de.prob.model.eventb.translate.*

mm = new ModelModifier("MyLift",dir)
mm.make {
	context(name: "levels") {
		constant "TOP"
		axiom "top_axm": "TOP = 5"
		constant "BOTTOM"
		axiom "bottom_axm": "BOTTOM = 1"
	}
	
	machine(name: "lift0", sees: ["levels"]) {
		variable "level"
		variable "door_open"
		invariant "inv_level": "level : BOTTOM..TOP"
		invariant "inv_door":  "door_open : BOOL"
		invariant "always_true": "1 > 0", true
		invariant "inv2", "level > 0" 
		initialisation {
			action "level": "level := BOTTOM"
			action "door":  "door_open := FALSE"
		}
		
		event(name: "up") {
			guard "level_grd": "level < TOP"
			guard "door_grd": "door_open = FALSE"
			action "move_up": "level := level + 1" 
		}
		
		event(name: "down") {
			guard "level_grd": "level > BOTTOM"
			guard "door_grd": "door_open = FALSE"
			action "move_down": "level := level - 1"
		}
		
		event(name: "open_door") {
			guard "door_grd": "door_open = FALSE"
			action "open": "door_open := TRUE"
		}
		
		event(name: "close_door") {
			guard "door_grd": "door_open = TRUE"
			action "close": "door_open := FALSE"
		}
	}
	
	context(name: "door") {
		constant "open"
		constant "closed"
		set "door_state"
		axiom "partition": "partition(door_state,{open},{closed})"
	}
	
	machine(name: "lift1", refines: ["lift0"], sees: ["door","levels"], mainComponent: true) {
		variable "door"
		variable "level"
		invariant "door_inv": "door : door_state"
		invariant "level_inv": "level : BOTTOM..TOP"
		invariant "gluing": "door_open = TRUE <=> door = open"
		initialisation {
			action "level": "level := BOTTOM"
			action "door": "door := closed"
		}
		event(name: "up", refines: "up") {
			guard "level_grd": "level < TOP"
			guard "door_grd": "door = closed"
			action "move_up": "level := level + 1" 
		}
		
		event(name: "down", refines: "down") {
			guard "level_grd": "level > BOTTOM"
			guard "door_grd": "door = closed"
			action "move_down": "level := level - 1"
		}
		
		event(name: "open_door", refines: "open_door") {
			guard "door_grd": "door = closed"
			action "open": "door := open"
		}
		
		event(name: "close_door", refines: "close_door") {
			guard "door_grd": "door = open"
			action "close": "door := closed"
		}
	}
}

lift0 = mm.temp.lift0
assert lift0 != null
assert lift0.variables.level != null
assert lift0.invariants.inv_level.getPredicate().getCode() == "level : BOTTOM..TOP"
assert lift0.invariants.inv2.getPredicate().getCode() == "level > 0"
assert lift0.invariants.always_true.isTheorem()
init = lift0.events.INITIALISATION
init.actions.level.getCode().getCode() == "level := 1"
init.actions.door.getCode().getCode() == "door_open := FALSE"

mm.writeToRodin()

m = mm.getModifiedModel()
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