import java.nio.file.Paths

import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.EnumerationWarning
import de.prob.statespace.Trace

final s = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString())
def t = new Trace(s)
t = t.$initialise_machine()

final x = "{w | w : NATURAL & w mod 2 = 0} /\\ {v | v : NATURAL & v mod 2 = 1} = {}" as ClassicalB
final res = t.evalCurrent(x)
assert res instanceof EnumerationWarning

"enumeration warnings are handled"
