/*
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 */

package de.prob.animator.command;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public final class ComputeCoverageCommand extends AbstractCommand {
	private static final String PROLOG_COMMAND_NAME = "compute_coverage";
	private ComputeCoverageResult coverageResult;

	public ComputeCoverageResult getResult() {
		return coverageResult;
	}

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		IntegerPrologTerm totalNodeNr = (IntegerPrologTerm) bindings.get("TotalNodeNr");
		IntegerPrologTerm totalTransNr = (IntegerPrologTerm) bindings.get("TotalTransSum");

		ListPrologTerm ops = BindingGenerator.getList(bindings.get("OpStat"));
		ListPrologTerm nodes = BindingGenerator.getList(bindings.get("NodeStat"));
		ListPrologTerm uncovered = BindingGenerator.getList(bindings.get("Uncovered"));
		coverageResult = new ComputeCoverageResult(totalNodeNr, totalTransNr, ops, nodes, uncovered);
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME)
			.printVariable("TotalNodeNr")
			.printVariable("TotalTransSum")
			.printVariable("NodeStat")
			.printVariable("OpStat")
			.printVariable("Uncovered")
			.closeTerm();
	}

	public class ComputeCoverageResult {
		private final BigInteger totalNumberOfNodes;
		private final BigInteger totalNumberOfTransitions;
		private final List<String> ops;
		private final List<String> nodes;
		private final List<String> uncovered;

		public ComputeCoverageResult(
				final IntegerPrologTerm totalNumberOfNodes,
				final IntegerPrologTerm totalNumberOfTransitions,
				final ListPrologTerm ops, final ListPrologTerm nodes,
				final ListPrologTerm uncovered) {
			this.totalNumberOfNodes = totalNumberOfNodes.getValue();
			this.totalNumberOfTransitions = totalNumberOfTransitions.getValue();
			this.ops = ops.stream().map(Object::toString).collect(Collectors.toList());
			this.nodes = nodes.stream().map(Object::toString).collect(Collectors.toList());
			this.uncovered = uncovered.stream().map(Object::toString).collect(Collectors.toList());
		}

		public BigInteger getTotalNumberOfNodes() {
			return totalNumberOfNodes;
		}

		public BigInteger getTotalNumberOfTransitions() {
			return totalNumberOfTransitions;
		}

		public List<String> getOps() {
			return ops;
		}

		public List<String> getNodes() {
			return nodes;
		}

		public List<String> getUncovered() {
			return uncovered;
		}
	}
}
