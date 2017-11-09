package de.prob.model.eventb.algorithm.graph;

import de.prob.model.eventb.algorithm.ast.IAssignment;
import de.prob.model.eventb.algorithm.ast.Statement;
import groovy.lang.Closure;
import groovy.lang.Reference;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.util.HashSet;
import java.util.Set;

public class MergeAssignment implements IGraphTransformer {

	private Set<Statement> visited = new HashSet<Statement>();

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
		if (node instanceof IAssignment) {
			Set<Edge> inEdges = g.get().inEdges(node);
			final Reference<Boolean> toMerge = new Reference<Boolean>(!inEdges.isEmpty());
			DefaultGroovyMethods.each(inEdges, new Closure<Boolean>(this, this) {
				public Boolean doCall(Edge e) {
					return setGroovyRef(toMerge, toMerge.get() && (!e.getConditions().isEmpty() && e.getAssignment() == null));
				}

			});
			if (toMerge.get()) {
				Set<Edge> outE = g.get().outEdges(node);
				assert outE.size() == 1;
				final Edge oE = DefaultGroovyMethods.first(outE);
				g.set(g.get().removeNode(node));
				DefaultGroovyMethods.each(inEdges, new Closure<ControlFlowGraph>(this, this) {
					public ControlFlowGraph doCall(Edge e) {
						return setGroovyRef(g, g.get().addEdge(e.mergeAssignment(oE)));
					}

				});
				return merge(g.get(), oE.getTo());
			}

		}


		DefaultGroovyMethods.each(g.get().outEdges(node), new Closure<ControlFlowGraph>(this, this) {
			public ControlFlowGraph doCall(Edge e) {
				return setGroovyRef(g, merge(g.get(), e.getTo()));
			}

		});
		return g.get();
	}

	public HashSet<Statement> getVisited() {
		return (HashSet) visited;
	}

	public void setVisited(Set<Statement> visited) {
		this.visited = visited;
	}

	private static <T> T setGroovyRef(Reference<T> ref, T newValue) {
		ref.set(newValue);
		return newValue;
	}
}
