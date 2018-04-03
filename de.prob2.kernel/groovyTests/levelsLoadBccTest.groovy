import java.nio.file.Paths

import de.prob.statespace.Trace

final s = api.eventb_load(Paths.get(dir, "Lift", "levels.bcc").toString())
def c = s as Trace
assert c.currentState == s.root
assert c.currentState.toString() == "root"
c = c.anyEvent()
final st = c.currentState
assert st != s.root
c = c.anyEvent()
assert c.currentState != st

"A .bcc file can be loaded and does not result in an empty machine"
