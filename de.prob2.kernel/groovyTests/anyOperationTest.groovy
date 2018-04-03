import java.nio.file.Paths

import de.prob.statespace.Trace

final s = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString())
def h = new Trace(s)
final h1 = h.anyOperation(".*i.*")
assert h1 != h
final h2 = h1.anyOperation(".*z.*")
assert h2 == h1
h = h2
h = h.anyOperation("new")
assert h.current.transition.name == "new"
h = h.anyOperation(["new", "del"])
assert h.current.transition.name == "new" || h.current.transition.name == "del"
h = h.anyEvent("nr_ready")
assert h.current.transition.name == "nr_ready"

"anyOperation and anyEvent on Trace work correctly"
