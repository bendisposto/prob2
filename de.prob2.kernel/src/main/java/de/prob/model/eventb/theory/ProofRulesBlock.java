package de.prob.model.eventb.theory;

import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.ModelElementList;
import de.prob.model.representation.Variable;

public class ProofRulesBlock extends AbstractElement {

	private final String name;
	private ModelElementList<RewriteRule> rewriteRules = new ModelElementList<RewriteRule>();
	private ModelElementList<MetaVariable> metaVariables = new ModelElementList<MetaVariable>();
	private ModelElementList<InferenceRule> inferenceRules = new ModelElementList<InferenceRule>();

	public ProofRulesBlock(final String name) {
		this.name = name;
	}

	public void addRewriteRules(final ModelElementList<RewriteRule> rewriteRules) {
		put(RewriteRule.class, rewriteRules);
		this.rewriteRules = rewriteRules;
	}

	public void addMetaVariables(
			final ModelElementList<MetaVariable> metaVariables) {
		put(Variable.class, metaVariables);
		this.metaVariables = metaVariables;
	}

	public void addInferenceRules(
			final ModelElementList<InferenceRule> inferenceRules) {
		put(InferenceRule.class, inferenceRules);
		this.inferenceRules = inferenceRules;
	}

	public ModelElementList<RewriteRule> getRewriteRules() {
		return rewriteRules;
	}

	public ModelElementList<InferenceRule> getInferenceRules() {
		return inferenceRules;
	}

	public ModelElementList<MetaVariable> getMetaVariables() {
		return metaVariables;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof ProofRulesBlock) {
			return name.equals(((ProofRulesBlock) obj).getName());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
