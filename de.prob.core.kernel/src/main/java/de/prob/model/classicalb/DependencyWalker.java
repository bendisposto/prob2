package de.prob.model.classicalb;

import java.util.LinkedList;
import java.util.List;

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
import de.be4.classicalb.core.parser.node.TIdentifierLiteral;
import de.prob.model.classicalb.RefType.ERefType;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class DependencyWalker extends DepthFirstAdapter {

	private final DirectedSparseMultigraph<String, RefType> graph;
	private final String src;

	public DependencyWalker(String name,
			DirectedSparseMultigraph<String, RefType> graph) {
		src = name;
		this.graph = graph;
	}

	@Override
	public void caseASeesMachineClause(ASeesMachineClause node) {
		registerMachineNames(node.getMachineNames(), new RefType(ERefType.SEES));
	}

	@Override
	public void caseAUsesMachineClause(AUsesMachineClause node) {
		registerMachineNames(node.getMachineNames(), new RefType(ERefType.USES));
	}

	@Override
	public void caseAImportsMachineClause(AImportsMachineClause node) {
		LinkedList<PMachineReference> machineReferences = node
				.getMachineReferences();
		for (PMachineReference r : machineReferences) {
			String dest = extractMachineName(((AMachineReference) r)
					.getMachineName());
			graph.addEdge(new RefType(ERefType.IMPORTS), src, dest);
		}
	}

	@Override
	public void caseAMachineReference(AMachineReference node) {
		String dest = extractMachineName(node.getMachineName());
		graph.addEdge(new RefType(ERefType.INCLUDES), src, dest);
	}

	@Override
	public void outARefinementMachineParseUnit(ARefinementMachineParseUnit node) {
		registerRefinementMachine(node.getRefMachine());
	}

	@Override
	public void outAImplementationMachineParseUnit(
			AImplementationMachineParseUnit node) {
		registerRefinementMachine(node.getRefMachine());
	}

	private void registerRefinementMachine(TIdentifierLiteral refMachine) {
		String dest = refMachine.getText();
		graph.addEdge(new RefType(ERefType.REFINES), src, dest);
	}

	private void registerMachineNames(List<PExpression> machineNames,
			RefType depType) {
		for (PExpression machineName : machineNames) {
			if (machineName instanceof AIdentifierExpression) {
				AIdentifierExpression identifier = (AIdentifierExpression) machineName;
				String dest = extractMachineName(identifier.getIdentifier());
				graph.addEdge(depType, src, dest);
			}
		}
	}

	private String extractMachineName(LinkedList<TIdentifierLiteral> list) {
		return list.getLast().getText();
	}

}
