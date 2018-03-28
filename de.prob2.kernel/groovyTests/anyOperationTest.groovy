import de.prob.statespace.Trace

final s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
def h = new Trace(s)
def h1 = h.anyOperation(".*i.*")
assert h1 != h
def h2 = h1.anyOperation(".*z.*")
assert h2 == h1
h = h2
h = h.anyOperation("new")
assert h.current.getTransition().getName() == "new"
h = h.anyOperation(["new","del"])
assert h.current.getTransition().getName() == "new" || h.current.getTransition().getName() == "del"
h = h.anyEvent("nr_ready")
assert h.current.getTransition().getName() == "nr_ready"

"anyOperation and anyEvent on Trace work correctly"
