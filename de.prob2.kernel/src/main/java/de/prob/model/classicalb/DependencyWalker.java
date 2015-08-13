package de.prob.model.classicalb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Joiner;

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
	private final String prefix;
	private final String name;
	private final Map<String, Start> map;
	private ModelElementList<ClassicalBMachine> machines;
	private Set<LinkedList<TIdentifierLiteral>> machineIds;

	public DependencyWalker(final LinkedList<TIdentifierLiteral> machine,
			final ModelElementList<ClassicalBMachine> machines,
			final DependencyGraph graph, final Map<String, Start> map) {
		this.machineIds = new HashSet<LinkedList<TIdentifierLiteral>>();
		this.name = extractMachineName(machine);
		this.prefix = extractMachinePrefix(machine);
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
			addMachine(dest, prefix, ERefType.IMPORTS);
		}
	}

	@Override
	public void caseAMachineReference(final AMachineReference node) {
		final String dest = extractMachineName(node.getMachineName());
		final String prefix = extractMachinePrefix(node.getMachineName());
		addMachine(dest, concat(this.prefix, prefix), ERefType.INCLUDES);
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
		addMachine(dest, prefix, ERefType.REFINES);
	}

	private void registerMachineNames(final List<PExpression> machineNames,
			final ERefType depType) {
		for (final PExpression machineName : machineNames) {
			if (machineName instanceof AIdentifierExpression) {
				final AIdentifierExpression identifier = (AIdentifierExpression) machineName;
				final String dest = extractMachineName(identifier
						.getIdentifier());
				addMachine(dest, depType == ERefType.USES ? dest : prefix, depType); // TODO test this
			}
		}
	}

	private String extractMachineName(final LinkedList<TIdentifierLiteral> list) {
		machineIds.add(list);
		return list.getLast().getText();
	}

	private String extractMachinePrefix(
			LinkedList<TIdentifierLiteral> list) {
		if (list.size() > 1) {
			List<TIdentifierLiteral> subList = list.subList(0, list.size() - 1);
			List<String> names = new ArrayList<String>();
			for (TIdentifierLiteral tIdentifierLiteral : subList) {
				names.add(tIdentifierLiteral.getText());
			}
			return Joiner.on(".").join(names);
		}
		return null;
	}

	private ClassicalBMachine makeMachine(final String dest, final String prefix) {
		final DomBuilder builder = new DomBuilder(prefix);
		final Start start = map.get(dest);
		start.apply(builder);
		return builder.getMachine();
	}

	// Takes the name of the destination machine, makes it, and puts it in the
	// graph
	private void addMachine(final String dest, String prefix, final ERefType refType) {
		final ClassicalBMachine newMachine = makeMachine(dest, prefix);
		final String name = newMachine.getName();
		machines = machines.addElement(newMachine);
		graph = graph.addEdge(concat(this.prefix, this.name), name, refType);
	}

	public String concat(String prefix, String name) {
		if (prefix == null) {
			return name;
		}
		return prefix + "." + name;
	}

	public ModelElementList<ClassicalBMachine> getMachines() {
		return machines;
	}

	public DependencyGraph getGraph() {
		return graph;
	}

	public Set<LinkedList<TIdentifierLiteral>> getMachineIds() {
		return machineIds;
	}

}
