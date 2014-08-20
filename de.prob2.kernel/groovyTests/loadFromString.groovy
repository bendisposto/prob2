import de.be4.classicalb.core.parser.BParser;
import de.prob.animator.domainobjects.*
import de.prob.statespace.*


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


modelProvider = api.modelFactoryProvider.classical_b_factory.modelCreator
assert modelProvider != null 

m = modelProvider.get()
s = m as StateSpace

cmd = new LoadBProjectFromStringCommand(modelString)
s.execute(cmd)
ast = cmd.parseString(modelString, new BParser())
rml = cmd.getLoader(modelString)

m.initialize(ast, rml, new File(""))

s.execute(new StartAnimationCommand())

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