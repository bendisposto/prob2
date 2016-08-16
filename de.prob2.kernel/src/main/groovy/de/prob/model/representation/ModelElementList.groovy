package de.prob.model.representation;

import com.github.krukow.clj_lang.PersistentHashMap
import com.github.krukow.clj_lang.PersistentVector



/**
 * @author joy
 *
 * @param <E>
 */
/**
 * @author joy
 *
 * @param <E>
 */
public class ModelElementList<E> implements List<E> {

	//FIXME Maybe it is better to inherit from AbstractList and only override what we actually need

	def final PersistentVector<E> list
	def final PersistentHashMap<String,E> keys

	def ModelElementList() {
		list = PersistentVector.emptyVector()
		keys = PersistentHashMap.emptyMap()
	}

	def ModelElementList(List<E> elements) {
		def list = PersistentVector.emptyVector()
		def keys = PersistentHashMap.emptyMap()
		elements.each { e ->
			keys = addMapping(keys, e)
			list = list.assocN(list.size(), e)
		}
		this.list = list
		this.keys = keys
	}

	private ModelElementList(PersistentVector list, PersistentHashMap keys) {
		this.list = list
		this.keys = keys
	}


	def getProperty(String prop) {
		return keys[prop];
	}

	def hasProperty(String prop) {
		return keys.containsKey(prop)
	}

	public ModelElementList<E> addElement(E e) {
		def newkeys = addMapping(keys, e)
		def newlist = list.assocN(list.size(), e)
		return new ModelElementList<E>(newlist, newkeys)
	}

	public ModelElementList<E> addMultiple(Collection<? extends E> elements) {
		def list = list
		def keys = keys
		elements.each {e ->
			keys = addMapping(keys, e)
			list = list.assocN(list.size(), e)
		}
		return new ModelElementList<E>(list, keys)
	}

	public ModelElementList<E> removeElement(E e) {
		def newkeys = removeMapping(keys, e)
		def newlist = removeE(list, e)
		return new ModelElementList<E>(newlist,newkeys)
	}

	public ModelElementList<E> replaceElement(E oldE, E newE) {
		if (list.contains(oldE)) {
			def newkeys = removeMapping(keys, oldE)
			newkeys = addMapping(newkeys, newE)
			def newlist = list.assocN(list.indexOf(oldE),newE)
			return new ModelElementList<E>(newlist,newkeys)
		}
		this
	}

	private PersistentVector<E> removeE(PersistentVector<E> list, E e) {
		def newlist = PersistentVector.emptyVector()
		list.each {
			if (it != e) {
				newlist = newlist.assocN(newlist.size(), it)
			}
		}
		newlist
	}

	private PersistentHashMap<String,E> addMapping(PersistentHashMap<String,E> keys, E e) {
		def newkeys = keys
		if(e.metaClass.respondsTo(e, "getName")) {
			newkeys = newkeys.assoc(e.getName(),e)
		}
		if(e.metaClass.respondsTo(e, "getFormula") && e.getFormula()) {
			newkeys = newkeys.assoc("_" + e.getFormula().getFormulaId().uuid,e)
		}
		newkeys
	}

	private PersistentHashMap<String,E> removeMapping(PersistentHashMap<String,E> keys, E e) {
		def newkeys = keys
		if(e.metaClass.respondsTo(e, "getName")) {
			newkeys = newkeys.without(e.getName())
		}
		if(e.metaClass.respondsTo(e, "getFormula")) {
			def key = "_" + e.getFormula().getFormulaId().uuid
			newkeys = newkeys.without(key)
		}
		newkeys
	}

	/**
	 * @param name of the element to be retrieved
	 * @return the specified element, or null if no element with that name exists.
	 */
	def E getElement(String name) {
		return keys[name]
	}

	@Override
	@Deprecated
	public boolean add(E e) {
		throw new UnsupportedOperationException()
	}

	@Override
	@Deprecated
	public void add(int index, E element) {
		throw new UnsupportedOperationException()
	}

	@Override
	@Deprecated
	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException()
	}

	@Override
	@Deprecated
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException()
	}

	@Override
	@Deprecated
	public void clear() {
		throw new UnsupportedOperationException()
	}

	@Override
	public boolean contains(Object o) {
		return list.contains(o)
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c)
	}

	@Override
	public E get(int index) {
		return list.get(index)
	}

	/**
	 * @param name
	 * @return the element associated with the specified name if a mapping for the name exists in the {@link ModelElementList}
	 */
	public E get(String name) {
		return keys[name]
	}

	@Override
	public int indexOf(Object o) {
		return list.indexOf(o)
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty()
	}

	@Override
	public Iterator<E> iterator() {
		return list.iterator()
	}

	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o)
	}

	@Override
	public ListIterator<E> listIterator() {
		return list.listIterator()
	}

	@Override
	@Deprecated
	public boolean remove(Object o) {
		throw new UnsupportedOperationException()
	}

	@Override
	@Deprecated
	public E remove(int index) {
		throw new UnsupportedOperationException()
	}

	@Override
	@Deprecated
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException()
	}

	@Override
	@Deprecated
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException()
	}

	@Override
	@Deprecated
	public E set(int index, E element) {
		throw new UnsupportedOperationException()
	}

	@Override
	public int size() {
		return list.size()
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return new ModelElementList<E>(list.subList(fromIndex,toIndex))
	}

	@Override
	public Object[] toArray() {
		return list.toArray()
	}

	@SafeVarargs
	@Override
	public final <T> T[] toArray(T[] a) {
		return list.toArray(a)
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return list.listIterator(index)
	}

	@Override
	public String toString() {
		return list.toString();
	}

	def getAt(int index) {
		return get(index)
	}

	def getAt(String property) {
		return keys[property]
	}
}
