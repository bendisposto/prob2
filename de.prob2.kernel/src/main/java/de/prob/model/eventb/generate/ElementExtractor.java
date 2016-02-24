package de.prob.model.eventb.generate;

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import de.be4.eventbalg.core.parser.analysis.DepthFirstAdapter;
import de.be4.eventbalg.core.parser.node.Node;
import de.prob.model.eventb.FormulaParseException;
import de.prob.model.eventb.FormulaTypeException;
import de.prob.model.eventb.ModelGenerationException;

public abstract class ElementExtractor extends DepthFirstAdapter {
	protected Set<IFormulaExtension> typeEnv;

	public ElementExtractor(Set<IFormulaExtension> typeEnv) {
		this.typeEnv = typeEnv;
	}

	public Set<IFormulaExtension> getTypeEnvironment() {
		return typeEnv;
	}

	protected void handleException(ModelGenerationException e, Node node) {
		if (e instanceof FormulaParseException) {
			throw new FormulaParseError(node.getStartPos(), node.getEndPos(),
					((FormulaParseException) e).getFormula());
		}
		if (e instanceof FormulaTypeException) {
			throw new FormulaTypeError(node.getStartPos(), node.getEndPos(),
					((FormulaTypeException) e).getFormula(),
					((FormulaTypeException) e).getExpected());
		}
	}

}
