package de.prob.model.eventb.theory;

import java.util.List;

import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.ModelElementList;
import de.prob.model.representation.Variable;

public class ProofRulesBlock extends AbstractElement {

	private final String name;
	private final List<RewriteRule> rewriteRules = new ModelElementList<RewriteRule>();
	private final List<MetaVariable> metaVariables = new ModelElementList<MetaVariable>();
	private final List<InferenceRule> inferenceRules = new ModelElementList<InferenceRule>();

	public ProofRulesBlock(final String name) {
		this.name = name;
	}

	public void addRewriteRules(final List<RewriteRule> rewriteRules) {
		put(RewriteRule.class, rewriteRules);
		this.rewriteRules.addAll(rewriteRules);
	}

	public void addMetaVariables(final List<MetaVariable> metaVariables) {
		put(Variable.class, metaVariables);
		this.metaVariables.addAll(metaVariables);
	}

	public void addInferenceRules(final List<InferenceRule> inferenceRules) {
		put(InferenceRule.class, inferenceRules);
		this.inferenceRules.addAll(inferenceRules);
	}

	public List<RewriteRule> getRewriteRules() {
		return rewriteRules;
	}

	public List<InferenceRule> getInferenceRules() {
		return inferenceRules;
	}

	public List<MetaVariable> getMetaVariables() {
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
