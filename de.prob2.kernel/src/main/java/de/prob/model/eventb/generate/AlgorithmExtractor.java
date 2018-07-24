package de.prob.model.eventb.generate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.be4.eventbalg.core.parser.node.AAlgorithm;
import de.be4.eventbalg.core.parser.node.AAssertStmt;
import de.be4.eventbalg.core.parser.node.AAssignStmt;
import de.be4.eventbalg.core.parser.node.ACallStmt;
import de.be4.eventbalg.core.parser.node.AId;
import de.be4.eventbalg.core.parser.node.AIfStmt;
import de.be4.eventbalg.core.parser.node.ALoopInvariant;
import de.be4.eventbalg.core.parser.node.ALoopVariant;
import de.be4.eventbalg.core.parser.node.AReturnStmt;
import de.be4.eventbalg.core.parser.node.ASimpleAssignStmt;
import de.be4.eventbalg.core.parser.node.AWhileStmt;
import de.be4.eventbalg.core.parser.node.PId;
import de.be4.eventbalg.core.parser.node.PLoopInvariant;
import de.be4.eventbalg.core.parser.node.PLoopVariant;
import de.be4.eventbalg.core.parser.node.PStmt;

import de.prob.model.eventb.ModelGenerationException;
import de.prob.model.eventb.algorithm.ast.Block;

import org.eventb.core.ast.extension.IFormulaExtension;

public class AlgorithmExtractor extends ElementExtractor {

	public AlgorithmExtractor(final Set<IFormulaExtension> typeEnv) {
		super(typeEnv);
	}

	public Block extract(final AAlgorithm node) {
		LinkedList<PStmt> block = node.getBlock();
		return extractStmts(block);
	}

	private Block extractStmts(final LinkedList<PStmt> block) {
		Block b = new Block(new ArrayList<>(), typeEnv);
		for (PStmt pStmt : block) {
			try {
				b = extractStmt(b, pStmt);
			} catch (ModelGenerationException e) {
				handleException(e, pStmt);
			}
		}
		return b;
	}

	private Block extractStmt(final Block b, final PStmt pStmt)
			throws ModelGenerationException {
		if (pStmt instanceof AWhileStmt) {
			AWhileStmt whileStmt = (AWhileStmt) pStmt;
			return b.While(whileStmt.getCondition().getText(),
					extractStmts(whileStmt.getStatements()),
					extractInvariant(b, whileStmt.getInvariant()),
					extractVariant(b, whileStmt.getVariant()));
		}
		if (pStmt instanceof AIfStmt) {
			AIfStmt ifStmt = (AIfStmt) pStmt;
			Block thenBlock = extractStmts(ifStmt.getThen());
			Block elseBlock = extractStmts(ifStmt.getElse());
			return b.If(ifStmt.getCondition().getText(), thenBlock, elseBlock);
		}
		if (pStmt instanceof AAssertStmt) {
			return b.Assert(((AAssertStmt) pStmt).getPredicate().getText());
		}
		if (pStmt instanceof AAssignStmt) {
			return b.Assign(((AAssignStmt) pStmt).getAction().getText());
		}
		if (pStmt instanceof ASimpleAssignStmt) {
			return b.Assign(((ASimpleAssignStmt) pStmt).getAction().getText());
		}
		if (pStmt instanceof AReturnStmt) {
			return b.Return(getIdList(((AReturnStmt) pStmt).getIdentifiers()));
		}
		if (pStmt instanceof ACallStmt) {
			return b.Call(((ACallStmt) pStmt).getName().getText(),
					getIdList(((ACallStmt) pStmt).getArguments()),
					getIdList(((ACallStmt) pStmt).getResults()));
		}
		throw new IllegalArgumentException("Unsupported statement type: "
				+ pStmt.getClass() + " " + pStmt.getStartPos());
	}

	private List<String> getIdList(LinkedList<PId> idL) {
		List<String> idList = new ArrayList<>();
		for (PId pId : idL) {
			if (pId instanceof AId) {
				idList.add(((AId) pId).getName().getText());
			}
		}
		return idList;
	}

	private String extractVariant(final Block b, final PLoopVariant variant) {
		if (variant instanceof ALoopVariant) {
			try {
				String text = ((ALoopVariant) variant).getExpression()
						.getText();
				b.parseExpression(text);
				return text;
			} catch (ModelGenerationException e) {
				handleException(e, variant);
			}
		}
		return null;
	}

	private String extractInvariant(final Block b, final PLoopInvariant variant) {
		if (variant instanceof ALoopInvariant) {
			try {
				String text = ((ALoopInvariant) variant).getPredicate()
						.getText();
				b.parsePredicate(text);
				return text;
			} catch (ModelGenerationException e) {
				handleException(e, variant);
			}
		}
		return null;
	}
}
