package de.prob.model.eventb.algorithm.graph;

import de.prob.model.eventb.algorithm.ast.If;
import de.prob.model.eventb.algorithm.ast.Statement;
import de.prob.model.eventb.algorithm.ast.While;
import groovy.lang.Closure;
import groovy.lang.Reference;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.util.HashSet;
import java.util.Set;

public class MergeConditionals implements IGraphTransformer {
	@Override
	public ControlFlowGraph transform(ControlFlowGraph graph) {
		if (graph.getEntryNode().equals(ControlFlowGraph.FILLER)) {
			return graph;
		}

		return merge(graph, graph.getEntryNode());
	}

	public ControlFlowGraph merge(ControlFlowGraph graph, Statement node) {
		if (visited.contains(node)) {
			return graph;
		}

		DefaultGroovyMethods.leftShift(visited, node);
		final Reference<ControlFlowGraph> g = new Reference<ControlFlowGraph>(graph);
		if (node instanceof While || node instanceof If) {
			g.set(mergeBranches(graph, node));
		}

		DefaultGroovyMethods.each(g.get().outEdges(node), new Closure<ControlFlowGraph>(this, this) {
			public ControlFlowGraph doCall(Edge e) {
				return setGroovyRef(g, merge(g.get(), e.getTo()));
			}

		});
		return g.get();
	}

	public ControlFlowGraph mergeBranches(final ControlFlowGraph graph, Statement s) {
		final Reference<ControlFlowGraph> g = new Reference<ControlFlowGraph>(graph);
		final Reference<Boolean> done = new Reference<Boolean>(false);
		while (!done.get()) {
			done.set(true);
			Set<Edge> outE = g.get().outEdges(s);
			DefaultGroovyMethods.each(outE, new Closure<Boolean>(this, this) {
				public Boolean doCall(final Edge e) {
					if (e.getTo() instanceof If && e.getAssignment() == null) {
						Set<Edge> ifEdges = graph.outEdges(e.getTo());
						g.set(g.get().removeNode(e.getTo()));
						DefaultGroovyMethods.each(ifEdges, new Closure<ControlFlowGraph>(MergeConditionals.this, MergeConditionals.this) {
							public ControlFlowGraph doCall(Edge e2) {
								return setGroovyRef(g, g.get().addEdge(e.mergeConditions(e2)));
							}

						});
						return setGroovyRef(done, false);
					}
					return false;
				}

			});
		}

		return g.get();
	}

	public HashSet<Statement> getVisited() {
		return (HashSet)visited;
	}

	public void setVisited(Set<Statement> visited) {
		this.visited = visited;
	}

	private Set<Statement> visited = new HashSet<Statement>();

	private static <T> T setGroovyRef(Reference<T> ref, T newValue) {
		ref.set(newValue);
		return newValue;
	}
}
