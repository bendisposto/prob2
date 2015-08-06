package de.prob.model.eventb.theory;

import com.github.krukow.clj_lang.PersistentHashMap;

import de.prob.model.eventb.EventBAxiom;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.ModelElementList;

public class AxiomaticDefinitionBlock extends AbstractElement {

	private final String name;

	public AxiomaticDefinitionBlock(final String name) {
		this.name = name;
	}

	private AxiomaticDefinitionBlock(final String name, PersistentHashMap<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> children) {
		super(children);
		this.name = name;
	}

	public AxiomaticDefinitionBlock set(Class<? extends AbstractElement> clazz, ModelElementList<? extends AbstractElement> elements) {
		return new AxiomaticDefinitionBlock(name, assoc(clazz, elements));
	}

	public String getName() {
		return name;
	}

	public ModelElementList<Type> getTypeParameters() {
		return getChildrenOfType(Type.class);
	}

	public ModelElementList<Operator> getOperators() {
		return getChildrenOfType(Operator.class);
	}

	public ModelElementList<EventBAxiom> getAxioms() {
		return getChildrenOfType(EventBAxiom.class);
	}

}
