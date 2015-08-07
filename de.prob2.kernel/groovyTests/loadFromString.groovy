import de.be4.classicalb.core.parser.BParser;
import de.prob.animator.domainobjects.*
import de.prob.statespace.*
import de.prob.Main

modelString = """MACHINE blah
SETS
 MySet={a, b, c}
VARIABLES x
INVARIANT
 x : MySet
INITIALISATION x := a
OPERATIONS
  ChangeX(y) = PRE y : MySet THEN x := y END
END"""


modelFactory = api.modelFactoryProvider.classical_b_factory
assert modelFactory != null 

em = modelFactory.create(modelString)
m = em.model
s = em.load()

// Test that animation works correctly
t = new Trace(s)
t = t.anyEvent()

assert m.blah != null
assert m.blah.sets != null
assert m.blah.sets.collect { it.name } == ["MySet"]
assert m.blah.variables != null
assert m.blah.variables.collect { it.name } == ["x"]
assert m.blah.operations.collect { it.name } == ["ChangeX"]

s.animator.cli.shutdown();
"it is possible to load a b model from a string"