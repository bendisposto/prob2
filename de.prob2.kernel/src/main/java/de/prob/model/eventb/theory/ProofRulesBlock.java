package de.prob.model.eventb.theory;

import com.github.krukow.clj_lang.PersistentHashMap;

import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.ModelElementList;

public class ProofRulesBlock extends AbstractElement {

	private final String name;

	public ProofRulesBlock(final String name) {
		this.name = name;
	}

	public ProofRulesBlock(final String name, PersistentHashMap<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> children) {
		super(children);
		this.name = name;
	}

	public ProofRulesBlock set(Class<? extends AbstractElement> clazz, ModelElementList<? extends AbstractElement> elements) {
		return new ProofRulesBlock(name, assoc(clazz, elements));
	}

	public ModelElementList<RewriteRule> getRewriteRules() {
		return getChildrenOfType(RewriteRule.class);
	}

	public ModelElementList<InferenceRule> getInferenceRules() {
		return getChildrenOfType(InferenceRule.class);
	}

	public ModelElementList<MetaVariable> getMetaVariables() {
		return getChildrenOfType(MetaVariable.class);
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
