
import java.util.logging.FileHandler.InitializationErrorManager;

import de.prob.animator.domainobjects.*
import de.prob.statespace.*
import de.prob.model.eventb.translate.*

mm = new ModelModifier("MyLift",dir)
mm.make {
	context(name: "door") {
		enumerated_set(name: "door_state", constants: ["open", "closed"])
	}
	
	machine(name: "lift0", sees: ["door"], mainComponent: true) {
		variable "level"
		variable "door_open"
		invariant "inv_level": "level : 1..5"
		invariant "inv_door":  "door_open : door_state"
		invariant "always_true": "1 > 0", true
		invariant "inv2", "level > 0" 
		initialisation {
			action "level": "level := 1"
			action "door":  "door_open := closed"
		}
		
		event(name: "up") {
			guard "level_grd": "level < 5"
			guard "door_grd": "door_open = closed"
			action "move_up": "level := level + 1" 
		}
		
		event(name: "down") {
			guard "level_grd": "level > 1"
			guard "door_grd": "door_open = closed"
			action "move_down": "level := level - 1"
		}
	}
}

lift0 = mm.temp.lift0
assert lift0 != null
assert lift0.variables.level != null
assert lift0.invariants.inv_level.getPredicate().getCode() == "level : 1..5"
assert lift0.invariants.inv2.getPredicate().getCode() == "level > 0"
assert lift0.invariants.always_true.isTheorem()
init = lift0.events.INITIALISATION
init.actions.level.getCode().getCode() == "level := 1"
init.actions.door.getCode().getCode() == "door := closed"

m = mm.getModifiedModel()
s = m as StateSpace
t = m as Trace

t = t.$initialise_machine()
assert !t.canExecuteEvent("down",[])
assert t.canExecuteEvent("up", [])
t = t.up().up().up().up()
assert t.canExecuteEvent("down",[])
assert !t.canExecuteEvent("up",[])

s.animator.cli.shutdown();

"it is possible to construct a model"