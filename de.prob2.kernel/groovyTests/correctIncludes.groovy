import de.prob.animator.domainobjects.*
import de.prob.model.representation.DependencyGraph.ERefType;
import de.prob.model.representation.DependencyGraph.Edge;
import de.prob.statespace.*

// You can change the model you are testing here.
s = api.b_load(dir+File.separator+"machines"+File.separator+"includes"+File.separator+"M1.mch")
t = new Trace(s)
t = t.$initialise_machine()
t = t."MA.set"()
t = t."MB.set"()
assert t != null

m = s.getModel()
graph = m.getGraph()
assert graph.getVertices() == ["M1","MB.M2","MA.M2","MA.MD.M3","MA.MC.M3","MB.MD.M3","MB.MC.M3"] as HashSet
["MA.MD.M3","MA.MC.M3","MB.MD.M3","MB.MC.M3"].each {
	assert graph.getOutEdges(it).isEmpty()
}

def outEdges(graph, vertex) {
	graph.getOutEdges(vertex).collect { Edge e ->
		[e.getTo().getElementName(), e.getRelationship()]
	} as HashSet
}
assert outEdges(graph, "M1") == [["MA.M2",ERefType.INCLUDES],["MB.M2",ERefType.INCLUDES]] as HashSet
assert outEdges(graph, "MA.M2") == [["MA.MC.M3",ERefType.INCLUDES],["MA.MD.M3",ERefType.INCLUDES]] as HashSet
assert outEdges(graph, "MB.M2") == [["MB.MC.M3",ERefType.INCLUDES],["MB.MD.M3",ERefType.INCLUDES]] as HashSet

def variables(model, machineName) {
	model.getComponent(machineName).variables.collect { it.getExpression().getCode() }
}

assert variables(m, "M1") == ["v1"]
assert variables(m, "MB.M2") == ["MB.vv"]
assert variables(m, "MA.M2") == ["MA.vv"]
assert variables(m, "MA.MD.M3") == ["MA.MD.bb"]
assert variables(m, "MA.MC.M3") == ["MA.MC.bb"]
assert variables(m, "MB.MD.M3") == ["MB.MD.bb"]
assert variables(m, "MB.MC.M3") == ["MB.MC.bb"]

def invariants(model, machineName) {
	model.getComponent(machineName).invariants.collect { it.getPredicate().getCode() }
}

assert invariants(m, "M1") == ["v1:BOOL"]
assert invariants(m, "MB.M2") == ["MB.vv:BOOL"]
assert invariants(m, "MA.M2") == ["MA.vv:BOOL"]
assert invariants(m, "MA.MD.M3") == ["MA.MD.bb:BOOL"]
assert invariants(m, "MA.MC.M3") == ["MA.MC.bb:BOOL"]
assert invariants(m, "MB.MD.M3") == ["MB.MD.bb:BOOL"]
assert invariants(m, "MB.MC.M3") == ["MB.MC.bb:BOOL"]

def operations(model, machineName) {
	model.getComponent(machineName).operations.collect { it.getName() }
}

assert operations(m, "M1") == ["set"]
assert operations(m, "MB.M2") == ["MB.set"]
assert operations(m, "MA.M2") == ["MA.set"]
assert operations(m, "MA.MD.M3") == ["MA.MD.set"]
assert operations(m, "MA.MC.M3") == ["MA.MC.set"]
assert operations(m, "MB.MD.M3") == ["MB.MD.set"]
assert operations(m, "MB.MC.M3") == ["MB.MC.set"]

def variable(model, element, var) {
	model.getComponent(element).variables.getElement(var)
}

// checks that the subscription works correctly. This will be eliminated once the old subscription mechanism is removed.
assert variable(m, "M1", "v1").isSubscribed(s)
assert variable(m, "MB.M2", "MB.vv").isSubscribed(s)
assert variable(m, "MA.M2", "MA.vv").isSubscribed(s)
assert variable(m, "MA.MD.M3", "MA.MD.bb").isSubscribed(s)
assert variable(m, "MA.MC.M3", "MA.MC.bb").isSubscribed(s)
assert variable(m, "MB.MD.M3", "MB.MD.bb").isSubscribed(s)
assert variable(m, "MB.MC.M3", "MB.MC.bb").isSubscribed(s)

s.animator.cli.shutdown();
"included machines are prefixed and represented correctly"