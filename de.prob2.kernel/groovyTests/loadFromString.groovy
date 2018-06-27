import de.prob.statespace.Trace

final modelString = """MACHINE blah
SETS
 MySet={a, b, c}
VARIABLES x
INVARIANT
 x : MySet
INITIALISATION x := a
OPERATIONS
  ChangeX(y) = PRE y : MySet THEN x := y END
END"""


final modelFactory = api.modelFactoryProvider.classicalBFactory
assert modelFactory != null 

final em = modelFactory.create("blah", modelString)
final m = em.model
final s = em.load()

// Test that animation works correctly
def t = new Trace(s)
t = t.anyEvent()

assert m.blah != null
assert m.blah.sets != null
assert m.blah.sets.collect { it.name } == ["MySet"]
assert m.blah.variables != null
assert m.blah.variables.collect { it.name } == ["x"]
assert m.blah.operations.collect { it.name } == ["ChangeX"]

"it is possible to load a b model from a string"
