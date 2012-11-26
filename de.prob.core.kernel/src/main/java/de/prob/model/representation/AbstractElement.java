package de.prob.model.representation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractElement {
	protected Map<Class<? extends AbstractElement>, java.util.Set<? extends AbstractElement>> children = new HashMap<Class<? extends AbstractElement>, Set<? extends AbstractElement>>();

	@SuppressWarnings("unchecked")
	public <T extends AbstractElement> Set<T> getChildrenOfType(final Class<T> c) {
		Set<? extends AbstractElement> set = children.get(c);
		if (set == null)
			return Collections.emptySet();
		return (Set<T>) set;
	}

	public <T extends AbstractElement> void put(final Class<T> c,
			final Collection<? extends T> elements) {
		children.put(c, new LinkedHashSet<T>(elements));
	}

	public Map<Class<? extends AbstractElement>, java.util.Set<? extends AbstractElement>> getChildren() {
		return children;
	}
}
