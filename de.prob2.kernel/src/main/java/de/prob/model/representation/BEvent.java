package de.prob.model.representation;

import com.github.krukow.clj_lang.PersistentHashMap;


public abstract class BEvent extends AbstractElement implements Named {

	protected final String name;

	public BEvent(final String name, PersistentHashMap<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> children) {
		super(children);
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
