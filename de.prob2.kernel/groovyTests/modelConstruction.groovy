import de.prob.model.eventb.ModelModifier
import de.prob.statespace.Trace

final mm = new ModelModifier().make {
	context(name: "levels") {
		theorem always_true: "1 < 5"
		constants "TOP", "BOTTOM"
		axioms "TOP = 5",
			"BOTTOM = 1"
	}
	
	machine(name: "lift0", sees: ["levels"]) {
		var name: "level", 
			invariant: [inv_level: "level : BOTTOM..TOP"],
			init: [act_level: "level := BOTTOM"]
		
		var name: "door_open",
			invariant: "door_open : BOOL",
			init: "door_open := FALSE"
		
		invariant always_true: "1 > 0", true
		theorem also_always_true: "5 < 6"
		invariant "level > 0"
		
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
	
	context(name: "door", extends: "IDoNothing") {
		enumerated_set name: "door_state",
			constants: ["open", "closed"]
	}
	
	machine(name: "lift1", refines: "lift0", sees: ["door","levels"]) {
		variables "door", "level"
		invariants "door : door_state",
			"level : BOTTOM..TOP" 
		invariant gluing: "door_open = TRUE <=> door = open"
		
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

final model = mm.model
assert model.levels.axioms.always_true.isTheorem()

final lift0 = model.lift0
assert lift0 != null
assert lift0.variables.level != null
assert lift0.invariants.collect { it.getName() } == ["inv_level", "typing_door_open", "always_true", "also_always_true", "inv0"]
assert lift0.invariants.inv_level.getPredicate().getCode() == "level : BOTTOM..TOP"
assert lift0.invariants.inv0.getPredicate().getCode() == "level > 0"
assert lift0.invariants.typing_door_open.getPredicate().getCode() == "door_open : BOOL"
assert lift0.invariants.always_true.isTheorem()
assert lift0.invariants.also_always_true.isTheorem()
final init = lift0.events.INITIALISATION
assert init.actions.collect { it.getName() } == ["act_level", "init_door_open"]
init.actions.act_level.getCode().getCode() == "level := 1"
init.actions.init_door_open.getCode().getCode() == "door_open := FALSE"
assert lift0.events.down.guards.always_true.isTheorem()

assert model.door.getExtends()[0].getName() == "IDoNothing"

//mtx = new ModelToXML()
//d = mtx.writeToRodin(m, "MyLift", dir)
//d.deleteDir()

final s = model.load(model.lift1)
def t = s as Trace

t = t.$setup_constants()
t = t.$initialise_machine()
assert !t.canExecuteEvent("down", [])
assert t.canExecuteEvent("up", [])
t = t.up().up().up().up()
assert t.canExecuteEvent("down", [])
assert !t.canExecuteEvent("up", [])
t = t.down()
assert t.canExecuteEvent("down", []) && t.canExecuteEvent("up", [])
t = t.open_door()
assert !t.canExecuteEvent("down", [])
assert !t.canExecuteEvent("up", [])
assert t.evalCurrent("door").value == "open"

"it is possible to construct a model"
