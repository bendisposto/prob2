package de.prob.model.representation.newdom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractElement {
	protected Map<Class<? extends AbstractElement>, List<? extends AbstractElement>> children = new HashMap<Class<? extends AbstractElement>, List<? extends AbstractElement>>();

	public List<? extends AbstractElement> getChildrenOfType(
			final Class<? extends AbstractElement> c) {
		return children.get(c);
	}

	public <T extends AbstractElement> void put(final Class<T> c,
			final List<? extends T> elements) {
		children.put(c, elements);
	}

}
