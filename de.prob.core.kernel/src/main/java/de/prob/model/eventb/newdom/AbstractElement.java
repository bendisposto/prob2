package de.prob.model.eventb.newdom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractElement {
	protected Map<Class<? extends AbstractElement>, List<AbstractElement>> children = new HashMap<Class<? extends AbstractElement>, List<AbstractElement>>();

	public List<AbstractElement> getChildrenOfType(
			Class<? extends AbstractElement> c) {
		return children.get(c);
	}

	public <T extends AbstractElement> void put(Class<T> c, T element) {
		if (children.get(c) == null)
			children.put(c, new ArrayList<AbstractElement>());
		children.get(c).add(element);
	}

}
