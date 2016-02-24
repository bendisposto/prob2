import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
f = de.be4.classicalb.core.parser.BParser.parse("MACHINE ss END")
s = api.b_load(f)
t = new Trace(s)
t = t.$initialise_machine()
assert t.getCurrentState().isInvariantOk()

"load constructed B machine test"