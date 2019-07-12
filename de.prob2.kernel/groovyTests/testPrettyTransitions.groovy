import java.nio.file.Paths

import de.prob.animator.domainobjects.FormulaExpand
import de.prob.statespace.Trace

final s = api.eventb_load(Paths.get(dir, "Lift", "lift0.bcm").toString())
def t = new Trace(s)
t = t.$setup_constants()
t = t.$initialise_machine()
t = t.up()

final list = t.getTransitionList(true, FormulaExpand.EXPAND)
assert list.collect {it.name} == ["\$setup_constants", "\$initialise_machine", "up"]
assert list.collect {it.rep} == ["\$setup_constants()", "\$initialise_machine()", "up()"]
assert list.collect {it.prettyRep} == ["SETUP_CONSTANTS()", "INITIALISATION()", "up()"]

"a Transition has a pretty representation for internal ProB transition names"
