import java.nio.file.Paths

import de.prob.model.representation.AbstractModel
import de.prob.model.representation.DependencyGraph
import de.prob.model.representation.DependencyGraph.ERefType
import de.prob.model.representation.Variable
import de.prob.statespace.Trace

final s = api.b_load(Paths.get(dir, "machines", "includes", "M1.mch").toString())
def t = new Trace(s)
t = t.$initialise_machine()
t = t."MA.set"()
t = t."MB.set"()
assert t != null

final m = s.model
final graph = m.graph
assert graph.vertices == ["M1", "MB.M2", "MA.M2", "MA.MD.M3", "MA.MC.M3", "MB.MD.M3", "MB.MC.M3"].toSet()
["MA.MD.M3", "MA.MC.M3", "MB.MD.M3", "MB.MC.M3"].each {
	assert graph.getOutEdges(it).empty
}

def outEdges(DependencyGraph graph, String vertex) {
	graph.getOutEdges(vertex).collect { e ->
		[e.to.elementName, e.relationship]
	}.toSet()
}
assert outEdges(graph, "M1") == [["MA.M2", ERefType.INCLUDES], ["MB.M2", ERefType.INCLUDES]].toSet()
assert outEdges(graph, "MA.M2") == [["MA.MC.M3", ERefType.INCLUDES], ["MA.MD.M3", ERefType.INCLUDES]].toSet()
assert outEdges(graph, "MB.M2") == [["MB.MC.M3", ERefType.INCLUDES], ["MB.MD.M3", ERefType.INCLUDES]].toSet()

def variables(AbstractModel model, String machineName) {
	model.getComponent(machineName).variables.collect {it.expression.code}
}

assert variables(m, "M1") == ["v1"]
assert variables(m, "MB.M2") == ["MB.vv"]
assert variables(m, "MA.M2") == ["MA.vv"]
assert variables(m, "MA.MD.M3") == ["MA.MD.bb"]
assert variables(m, "MA.MC.M3") == ["MA.MC.bb"]
assert variables(m, "MB.MD.M3") == ["MB.MD.bb"]
assert variables(m, "MB.MC.M3") == ["MB.MC.bb"]

def invariants(AbstractModel model, String machineName) {
	model.getComponent(machineName).invariants.collect {it.predicate.code}
}

assert invariants(m, "M1") == ["v1:BOOL"]
assert invariants(m, "MB.M2") == ["MB.vv:BOOL"]
assert invariants(m, "MA.M2") == ["MA.vv:BOOL"]
assert invariants(m, "MA.MD.M3") == ["MA.MD.bb:BOOL"]
assert invariants(m, "MA.MC.M3") == ["MA.MC.bb:BOOL"]
assert invariants(m, "MB.MD.M3") == ["MB.MD.bb:BOOL"]
assert invariants(m, "MB.MC.M3") == ["MB.MC.bb:BOOL"]

def operations(AbstractModel model, String machineName) {
	model.getComponent(machineName).operations.collect {it.name}
}

assert operations(m, "M1") == ["set"]
assert operations(m, "MB.M2") == ["MB.set"]
assert operations(m, "MA.M2") == ["MA.set"]
assert operations(m, "MA.MD.M3") == ["MA.MD.set"]
assert operations(m, "MA.MC.M3") == ["MA.MC.set"]
assert operations(m, "MB.MD.M3") == ["MB.MD.set"]
assert operations(m, "MB.MC.M3") == ["MB.MC.set"]

Variable variable(AbstractModel model, String element, String var) {
	model.getComponent(element).variables.getElement(var)
}

variable(m, "M1", "v1").subscribe(s)
variable(m, "MB.M2", "MB.vv").subscribe(s)
variable(m, "MA.M2", "MA.vv").subscribe(s)
variable(m, "MA.MD.M3", "MA.MD.bb").subscribe(s)
variable(m, "MA.MC.M3", "MA.MC.bb").subscribe(s)
variable(m, "MB.MD.M3", "MB.MD.bb").subscribe(s)
variable(m, "MB.MC.M3", "MB.MC.bb").subscribe(s)

// checks that the subscription works correctly. This will be eliminated once the old subscription mechanism is removed.
assert variable(m, "M1", "v1").isSubscribed(s)
assert variable(m, "MB.M2", "MB.vv").isSubscribed(s)
assert variable(m, "MA.M2", "MA.vv").isSubscribed(s)
assert variable(m, "MA.MD.M3", "MA.MD.bb").isSubscribed(s)
assert variable(m, "MA.MC.M3", "MA.MC.bb").isSubscribed(s)
assert variable(m, "MB.MD.M3", "MB.MD.bb").isSubscribed(s)
assert variable(m, "MB.MC.M3", "MB.MC.bb").isSubscribed(s)

"included machines are prefixed and represented correctly"
