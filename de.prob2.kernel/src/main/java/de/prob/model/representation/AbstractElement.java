package de.prob.model.representation;

import java.util.Map;
import java.util.Set;

import com.github.krukow.clj_lang.PersistentHashMap;

/**
 * This class is the subclass of all model elements (Models, Machines, Contexts,
 * Variables, etc.)
 *
 * @author joy
 *
 */
public abstract class AbstractElement {

	/**
	 * Maps from a subclass of {@link AbstractElement} to a set containing all
	 * elements for that subclass
	 */
	protected final PersistentHashMap<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> children;

	public AbstractElement() {
		this(PersistentHashMap.<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>>emptyMap());
	}

	public AbstractElement(
			PersistentHashMap<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> children) {
		this.children = children;
	}

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

	@SuppressWarnings("unchecked")
	protected <T extends AbstractElement, S extends T> ModelElementList<S> getChildrenAndCast(Class<T> key, Class<S> realType) {
		ModelElementList<? extends AbstractElement> list = children.get(key);
		if (list == null) {
			return new ModelElementList<S>();
		}
		return (ModelElementList<S>) list;
	}

	public PersistentHashMap<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>>assoc(
			Class<? extends AbstractElement> key, ModelElementList<? extends AbstractElement> val) {
		return (PersistentHashMap<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>>) children.assoc(key, val);
	}

	/**
	 * @return the {@link Map} of {@link Class} to {@link Set} of children
	 */
	public Map<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> getChildren() {
		return children;
	}
}
