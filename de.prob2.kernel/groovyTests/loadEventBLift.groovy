import de.prob.model.eventb.EventBModel
import de.prob.model.representation.DependencyGraph.ERefType
/*
 * Tests loading of EventB to make sure that all components are there. 
 */

final s = api.eventb_load(dir+File.separator+"Lift"+File.separator+"lift0.bcm")
assert s != null

final l = s.totalNumberOfErrors

final m = s as EventBModel

assert ERefType.SEES == m.getRelationship("lift0", "levels")

final levels = m.levels
assert levels != null
final constants = levels.constants

assert constants != null
assert constants.size() == 6
assert constants.collect { it.name } == [
	"L0",
	"L1",
	"L2",
	"L3",
	"down",
	"up"
]
assert !constants.inject(false) { acc, val -> acc || val.isAbstract}

final axioms = levels.axioms
assert axioms != null
assert axioms.size() == 3

final sets = levels.sets
assert sets != null
assert sets.size() == 1
assert sets[0].name == "levels"

final lift0 = m.lift0
assert lift0 != null

final variables = lift0.variables
assert variables != null
assert variables.size() == 1
assert variables[0].name == "level"

final invariants = lift0.invariants
assert invariants != null
assert invariants.size() == 1

final events = lift0.events
assert events != null
assert events.size() == 4
assert events.collect { it.name } == [
	"INITIALISATION",
	"up",
	"down",
	"randomCrazyJump"
]

final up = events.up
assert up != null
assert up.guards.size() == 1
assert up.actions.size() == 1

final randomCrazyJump = events.randomCrazyJump
assert randomCrazyJump != null
assert randomCrazyJump.parameters.size() == 1
assert randomCrazyJump.parameters[0].name == "prm1"
assert randomCrazyJump.guards.size() == 1
assert randomCrazyJump.actions.size() == 1

final variant = lift0.variant
assert variant == null

assert levels in lift0.sees

assert l == s.totalNumberOfErrors

"When an EventB file is loaded (Lift example), the model elements are accessible."
