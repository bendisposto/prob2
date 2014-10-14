package de.prob.model.representation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class is the subclass of all model elements (Models, Machines, Contexts,
 * Variables, etc.)
 * 
 * @author joy
 * 
 */
public abstract class AbstractElement {

	boolean frozen = false;
	/**
	 * Maps from a subclass of {@link AbstractElement} to a set containing all
	 * elements for that subclass
	 */
	protected Map<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> children = new HashMap<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>>();

	/**
	 * Each {@link AbstractElement} can have children of a subclass that extends
	 * {@link AbstractElement}. These are specified by the class of the child.
	 * To get a Set of all of the children of a particular class, use this
	 * method.
	 * 
	 * @param c
	 *            {@link Class} T of the desired type of children
	 * @return {@link Set} containing all the children of type T
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractElement> ModelElementList<T> getChildrenOfType(
			final Class<T> c) {
		ModelElementList<? extends AbstractElement> list = children.get(c);
		if (list == null) {
			return new ModelElementList<T>();
		}
		return (ModelElementList<T>) list;
	}

	/**
	 * Maps a {@link Collection} of elements to the specified {@link Class}
	 * 
	 * @param c
	 *            {@link Class} to specify children elements
	 * @param elements
	 *            {@link Collection} of elements with which c will be mapped
	 */
	public <T extends AbstractElement> void put(final Class<T> c,
			final ModelElementList<? extends T> elements) {
		if (frozen) {
			throw new IllegalModificationException();
		}
		children.put(c, elements);
	}

	/**
	 * @return the {@link Map} of {@link Class} to {@link Set} of children
	 */
	public Map<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> getChildren() {
		return children;
	}

	/**
	 * Once an {@link AbstractElement} is frozen, it can no longer be modified.
	 * In essence, this changes a mutable type into an immutable one. It also
	 * recursively freezes all children {@link ModelElementList}s.
	 */
	public void freeze() {
		frozen = true;
		for (ModelElementList<? extends AbstractElement> list : children
				.values()) {
			list.freeze();
		}
	}
}
