import de.prob.statespace.*
m = api.b_load("/home/joy/code/prob2/de.prob.core.kernel/groovyTests/machines/scheduler.mch")
s = m.getStatespace()
h = new History(s)
h1 = h.anyOperation(".*i.*")
assert h1 != h
h2 = h1.anyOperation(".*z.*")
assert h2 == h1
h = h2
h = h.anyOperation("new")
assert h.current.edge.name == "new"
h = h.anyOperation(["new","del"])
assert h.current.edge.name == "new" || h.current.edge.name == "del"
h = h.anyEvent("nr_ready")
assert h.current.edge.name == "nr_ready"

