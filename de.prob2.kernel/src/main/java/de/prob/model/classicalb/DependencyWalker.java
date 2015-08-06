package de.prob.model.classicalb;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.be4.classicalb.core.parser.analysis.DepthFirstAdapter;
import de.be4.classicalb.core.parser.node.AIdentifierExpression;
import de.be4.classicalb.core.parser.node.AImplementationMachineParseUnit;
import de.be4.classicalb.core.parser.node.AImportsMachineClause;
import de.be4.classicalb.core.parser.node.AMachineReference;
import de.be4.classicalb.core.parser.node.ARefinementMachineParseUnit;
import de.be4.classicalb.core.parser.node.ASeesMachineClause;
import de.be4.classicalb.core.parser.node.AUsesMachineClause;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PMachineReference;
import de.be4.classicalb.core.parser.node.Start;
import de.be4.classicalb.core.parser.node.TIdentifierLiteral;
import de.prob.model.representation.DependencyGraph;
import de.prob.model.representation.DependencyGraph.ERefType;
import de.prob.model.representation.ModelElementList;

public class DependencyWalker extends DepthFirstAdapter {

	private DependencyGraph graph;
	private final String src;
	private final Map<String, Start> map;
	private ModelElementList<ClassicalBMachine> machines;

	public DependencyWalker(final String machine,
			final ModelElementList<ClassicalBMachine> machines,
			final DependencyGraph graph, final Map<String, Start> map) {
		src = machine;
		this.machines = machines;
		this.graph = graph;
		this.map = map;
	}

	@Override
	public void caseASeesMachineClause(final ASeesMachineClause node) {
		registerMachineNames(node.getMachineNames(), ERefType.SEES);
	}

	@Override
	public void caseAUsesMachineClause(final AUsesMachineClause node) {
		registerMachineNames(node.getMachineNames(), ERefType.USES);
	}

	@Override
	public void caseAImportsMachineClause(final AImportsMachineClause node) {
		final LinkedList<PMachineReference> machineReferences = node
				.getMachineReferences();
		for (final PMachineReference r : machineReferences) {
			final String dest = extractMachineName(((AMachineReference) r)
					.getMachineName());
			addMachine(dest, ERefType.IMPORTS);
		}
	}

	@Override
	public void caseAMachineReference(final AMachineReference node) {
		final String dest = extractMachineName(node.getMachineName());
		addMachine(dest, ERefType.INCLUDES);
	}

	@Override
	public void outARefinementMachineParseUnit(
			final ARefinementMachineParseUnit node) {
		registerRefinementMachine(node.getRefMachine());
	}

	@Override
	public void outAImplementationMachineParseUnit(
			final AImplementationMachineParseUnit node) {
		registerRefinementMachine(node.getRefMachine());
	}

	private void registerRefinementMachine(final TIdentifierLiteral refMachine) {
		final String dest = refMachine.getText();
		addMachine(dest, ERefType.REFINES);
	}

	private void registerMachineNames(final List<PExpression> machineNames,
			final ERefType depType) {
		for (final PExpression machineName : machineNames) {
			if (machineName instanceof AIdentifierExpression) {
				final AIdentifierExpression identifier = (AIdentifierExpression) machineName;
				final String dest = extractMachineName(identifier
						.getIdentifier());
				addMachine(dest, depType);
			}
		}
	}

	private String extractMachineName(final LinkedList<TIdentifierLiteral> list) {
		return list.getLast().getText();
	}

	private ClassicalBMachine makeMachine(final String dest, final boolean used) {
		final DomBuilder builder = new DomBuilder(used);
		final Start start = map.get(dest);
		start.apply(builder);
		return builder.getMachine();
	}

	// Takes the name of the destination machine, makes it, and puts it in the
	// graph
	private void addMachine(final String dest, final ERefType refType) {
		final ClassicalBMachine newMachine = makeMachine(dest,
				refType.equals(ERefType.USES));
		final String name = newMachine.getName();
		machines = machines.addElement(newMachine);
		graph = graph.addEdge(src, name, refType);
	}

	public ModelElementList<ClassicalBMachine> getMachines() {
		return machines;
	}

	public DependencyGraph getGraph() {
		return graph;
	}

}
