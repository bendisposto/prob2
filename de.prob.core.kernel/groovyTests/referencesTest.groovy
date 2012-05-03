import de.be4.classicalb.core.parser.BParser
import de.prob.model.classicalb.*
import de.prob.model.classicalb.RefType.ERefType;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph
c = new ClassicalBModel(null)
bparser = new BParser();
f = new File(dir+"/machines/references.mch")
ast = bparser.parseFile(f,false)
graph = new DirectedSparseMultigraph<String, RefType>();
dw = new DependencyWalker("Foo", null, graph)
ast.apply(dw)

assert graph.findEdge("Foo", "Bar").relationship == ERefType.REFINES
assert graph.findEdge("Foo", "A").relationship == ERefType.SEES
assert graph.findEdge("Foo", "B").relationship == ERefType.USES
assert graph.findEdge("Foo", "C").relationship == ERefType.INCLUDES
assert graph.findEdge("Foo", "D").relationship == ERefType.INCLUDES
assert graph.findEdge("Foo", "E").relationship == ERefType.IMPORTS