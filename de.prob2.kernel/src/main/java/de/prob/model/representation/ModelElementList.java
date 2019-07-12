package de.prob.model.representation;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.github.krukow.clj_lang.IPersistentMap;
import com.github.krukow.clj_lang.PersistentHashMap;
import com.github.krukow.clj_lang.PersistentVector;

import groovy.lang.GroovyObjectSupport;

public class ModelElementList<E> extends GroovyObjectSupport implements List<E> {
	private final PersistentVector<E> list;
	private final PersistentHashMap<String, E> keys;

	public ModelElementList() {
		this(PersistentVector.emptyVector(), PersistentHashMap.emptyMap());
	}

	public ModelElementList(final List<E> elements) {
		PersistentVector<E> list = PersistentVector.emptyVector();
		PersistentHashMap<String, E> keys = PersistentHashMap.emptyMap();
		for (final E e : elements) {
			keys = addMapping(keys, e);
			list = list.assocN(list.size(), e);
		}
		this.list = list;
		this.keys = keys;
	}

	private ModelElementList(final PersistentVector<E> list, final PersistentHashMap<String, E> keys) {
		this.list = list;
		this.keys = keys;
	}

	@Override
	public E getProperty(final String prop) {
		return keys.get(prop);
	}

	// Note: this intentionally hides the standard Groovy hasProperty method. Some of our code relies on this (e. g. ModelModifier).
	public boolean hasProperty(final String prop) {
		return keys.containsKey(prop);
	}

	public ModelElementList<E> addElement(final E e) {
		final PersistentHashMap<String, E> newkeys = addMapping(keys, e);
		final PersistentVector<E> newlist = list.assocN(list.size(), e);
		return new ModelElementList<>(newlist, newkeys);
	}

	public ModelElementList<E> addMultiple(final Collection<? extends E> elements) {
		PersistentVector<E> list = this.list;
		PersistentHashMap<String, E> keys = this.keys;
		for (final E e : elements) {
			keys = addMapping(keys, e);
			list = list.assocN(list.size(), e);
		}
		return new ModelElementList<>(list, keys);
	}

	public ModelElementList<E> removeElement(final E e) {
		final PersistentHashMap<String, E> newkeys = removeMapping(keys, e);
		final PersistentVector<E> newlist = removeE(list, e);
		return new ModelElementList<>(newlist, newkeys);
	}

	public ModelElementList<E> replaceElement(final E oldE, final E newE) {
		if (list.contains(oldE)) {
			PersistentHashMap<String, E> newkeys = removeMapping(keys, oldE);
			newkeys = addMapping(newkeys, newE);
			final PersistentVector<E> newlist = list.assocN(list.indexOf(oldE), newE);
			return new ModelElementList<>(newlist, newkeys);
		}
		return this;
	}

	private PersistentVector<E> removeE(final PersistentVector<E> list, final E e) {
		PersistentVector<E> newlist = PersistentVector.emptyVector();
		for (final E it : list) {
			if (!it.equals(e)) {
				newlist = newlist.assocN(newlist.size(), it);
			}
		}
		return newlist;
	}

	private PersistentHashMap<String, E> addMapping(final PersistentHashMap<String, E> keys, final E e) {
		IPersistentMap<String, E> newkeys = keys;
		if (e instanceof Named) {
			newkeys = newkeys.assoc(((Named)e).getName(), e);
		}
		if (e instanceof AbstractFormulaElement) {
			newkeys = newkeys.assoc('_' + ((AbstractFormulaElement)e).getFormula().getFormulaId().getUUID(), e);
		}
		return (PersistentHashMap<String, E>)newkeys;
	}

	private PersistentHashMap<String, E> removeMapping(final PersistentHashMap<String, E> keys, final E e) {
		IPersistentMap<String, E> newkeys = keys;
		if (e instanceof Named) {
			newkeys = newkeys.without(((Named)e).getName());
		}
		if (e instanceof AbstractFormulaElement) {
			newkeys = newkeys.without('_' + ((AbstractFormulaElement)e).getFormula().getFormulaId().getUUID());
		}
		return (PersistentHashMap<String, E>)newkeys;
	}

	// FIXME Does the same as get(String) and should probably be deprecated at some point
	/**
	 * @param name of the element to be retrieved
	 * @return the specified element, or null if no element with that name exists.
	 */
	public E getElement(final String name) {
		return keys.get(name);
	}

	@Override
	@Deprecated
	public boolean add(final E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void add(final int index, final E element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean addAll(final Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean addAll(final int index, final Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(final Object o) {
		return list.contains(o);
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		return list.containsAll(c);
	}

	@Override
	public E get(final int index) {
		return list.get(index);
	}

	/**
	 * @return the element associated with the specified name if a mapping for the name exists in the {@link ModelElementList}
	 */
	public E get(final String name) {
		return keys.get(name);
	}

	@Override
	public int indexOf(final Object o) {
		return list.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return list.iterator();
	}

	@Override
	public int lastIndexOf(final Object o) {
		return list.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return list.listIterator();
	}

	@Override
	@Deprecated
	public boolean remove(final Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public E remove(final int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean removeAll(final Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean retainAll(final Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public E set(final int index, final E element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public List<E> subList(final int fromIndex, final int toIndex) {
		return new ModelElementList<>(list.subList(fromIndex, toIndex));
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public final <T> T[] toArray(final T[] a) {
		@SuppressWarnings("unchecked")
		final T[] arr = (T[])list.toArray(a);
		return arr;
	}

	@Override
	public ListIterator<E> listIterator(final int index) {
		return list.listIterator(index);
	}

	@Override
	public String toString() {
		return list.toString();
	}

	@SuppressWarnings("unused") // Groovy operator overload for this[index]
	public E getAt(final int index) {
		return get(index);
	}

	@SuppressWarnings("unused") // Groovy operator overload for this[property]
	public E getAt(final String property) {
		return keys.get(property);
	}
}
