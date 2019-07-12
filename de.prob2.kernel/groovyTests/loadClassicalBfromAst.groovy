import de.be4.classicalb.core.parser.BParser

import de.prob.statespace.Trace

final f = BParser.parse("MACHINE ss END")
final s = api.b_load(f)
def t = new Trace(s)
t = t.$initialise_machine()
assert t.currentState.invariantOk

"load constructed B machine test"
