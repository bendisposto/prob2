package de.prob.model.representation;

import de.prob.animator.domainobjects.IEvalElement



public class ModelElementList<E> implements List<E> {

	def List<E> list
	def Map<String,E> keys
	def frozen = false

	def ModelElementList() {
		list = new ArrayList<E>()
		keys = new HashMap<String,E>()
	}

	def ModelElementList(elements) {
		this()
		addAll(elements)
	}

	def getProperty(String prop) {
		return keys[prop];
	}

	def hasProperty(String prop) {
		return keys.containsKey(prop)
	}

	private calcAndAdd(E e) {
		if (frozen) {
			throw new IllegalModificationException()
		}

		if(e.metaClass.respondsTo(e, "getName")) {
			keys[e.getName()]=e
		}
		if(e.metaClass.respondsTo(e, "getFormula")) {
			keys["_" + e.getFormula().getFormulaId().uuid]=e
		}
	}

	private removeElement(E e) {
		if (frozen) {
			throw new IllegalModificationException()
		}

		if(e.metaClass.respondsTo(e, "getName")) {
			keys.remove(e.getName())
		}
		if(e.metaClass.respondsTo(e, "getFormula")) {
			def key = "_" + e.getFormula().getFormulaId().uuid
			keys.remove(key)
		}
	}

	@Override
	public boolean add(E e) {
		if (frozen) {
			throw new IllegalModificationException()
		}

		calcAndAdd(e)
		return list.add(e)
	}

	@Override
	public void add(int index, E element) {
		if (frozen) {
			throw new IllegalModificationException()
		}

		calcAndAdd(e)
		list.add(index,element)
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		if (frozen) {
			throw new IllegalModificationException()
		}

		c.each { calcAndAdd(it) }
		return list.addAll(c)
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		if (frozen) {
			throw new IllegalModificationException()
		}

		c.each { calcAndAdd(it) }
		return c.addAll(index,c)
	}

	@Override
	public void clear() {
		keys.clear()
		list.clear()
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
	public boolean remove(Object o) {
		if (frozen) {
			throw new IllegalModificationException()
		}

		removeElement(o)
		return list.remove(o)
	}

	@Override
	public E remove(int index) {
		if (frozen) {
			throw new IllegalModificationException()
		}

		removeElement(list.get(index))
		return list.remove(index)
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		if (frozen) {
			throw new IllegalModificationException()
		}

		c.each { removeElement(it) }
		return list.removeAll(c)
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		if (frozen) {
			throw new IllegalModificationException()
		}

		def toRemove = new HashSet<String>()
		keys.each { k,v ->
			if(!c.contains(v)) {
				toRemove << k
			}
		}
		toRemove.each { keys.remove(it) }
		return list.retainAll(c)
	}

	@Override
	public E set(int index, E element) {
		if (frozen) {
			throw new IllegalModificationException()
		}

		removeElement(list.get(index))
		calcAndAdd(element)
		return list.set(index,element)
	}

	@Override
	public int size() {
		return list.size()
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		ModelElementList<E> newList = new ModelElementList<E>()
		List<E> sub = list.subList(fromIndex, toIndex)
		newList.addAll(sub)
		return newList
	}

	@Override
	public Object[] toArray() {
		return list.toArray()
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return list.toArray(a)
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return list.listIterator(index)
	}

	public List<IEvalElement> evalElements() {
		List<IEvalElement> eval = new ArrayList<IEvalElement>()
		list.each {
			if(it.metaClass.respondsTo(it, "getEvaluate")) {
				eval << it.getEvaluate()
			}
		}
		return eval
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

	/**
	 *  Freezes all subelements of the list (if they are of type {@link AbstractElement}) and
	 *  disallows further modification of the list. This essentially transforms a mutable list into
	 *  an immutable instance of a list. Accessing elements of the list is allowed, but no further
	 *  modification of the list is allowed.
	 *
	 */
	def void freeze() {
		frozen = true
		list.each {
			if(it instanceof AbstractElement) {
				it.freeze()
			}
		}
	}
}
