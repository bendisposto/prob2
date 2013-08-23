package de.prob.model.representation;

import de.prob.animator.domainobjects.IEvalElement

public class ModelElementList<E> implements List<E> {

	def List<E> list
	def Map<String,E> keys

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

	def calcAndAdd(E e) {
		if(e.metaClass.respondsTo(e, "getName")) {
			keys[e.getName()]=e
		}
	}

	@Override
	public boolean add(E e) {
		calcAndAdd(e)
		return list.add(e)
	}

	@Override
	public void add(int index, E element) {
		calcAndAdd(e)
		list.add(index,element)
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		c.each { calcAndAdd(it) }
		return list.addAll(c)
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
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
		keys.remove(o)
		return list.remove(o)
	}

	@Override
	public E remove(int index) {
		keys.remove(list.get(index))
		return list.remove(index)
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		c.each { keys.remove(it) }
		return list.removeAll(c)
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		def toRemove = []
		keys.each {
			if(!c.contains(it)) {
				toRemove << it
			}
		}
		toRemove.each { keys.remove(it) }
		return list.retainAll(c)
	}

	@Override
	public E set(int index, E element) {
		keys.remove(list.get(index))
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
}
