package de.prob.model.eventb.theory;

import java.util.List;

import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.ModelElementList;
import de.prob.model.representation.Variable;

public class ProofRulesBlock extends AbstractElement {

	private final String name;

	public ProofRulesBlock(final String name) {
		this.name = name;
	}

	public void addRewriteRules(final List<RewriteRule> rewriteRules) {
		put(RewriteRule.class, rewriteRules);
	}

	public void addMetaVariables(final List<MetaVariable> metaVariables) {
		put(Variable.class, metaVariables);
	}

	public void addInferenceRules(final List<InferenceRule> inferenceRules) {
		put(InferenceRule.class, inferenceRules);
	}

	public List<RewriteRule> getRewriteRules() {
		return new ModelElementList<RewriteRule>(
				getChildrenOfType(RewriteRule.class));
	}

	public List<MetaVariable> getMetaVariables() {
		return new ModelElementList<MetaVariable>(
				getChildrenOfType(Variable.class));
	}

	public List<InferenceRule> getInferenceRules() {
		return new ModelElementList<InferenceRule>(
				getChildrenOfType(InferenceRule.class));
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
