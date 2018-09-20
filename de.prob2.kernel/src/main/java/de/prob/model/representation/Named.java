package de.prob.model.representation;

/**
 * An object with a name. If such an object is inserted into a {@link ModelElementList}, it can be looked up by name in the list.
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface Named {
	public abstract String getName();
}
