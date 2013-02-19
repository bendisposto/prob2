package de.prob.worksheet.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ContextHistory stores a list of IContext objects in the order they are
 * added or merged. It removes siblings which are equal.
 * 
 * TODO: Add handling for mixing Context Types (remember duplicate in
 * setContextsForId)
 * 
 * @author Rene
 */
public class ContextHistory implements Iterable<IContext> {
	/**
	 * The static instance of a slf4j Logger for this class
	 */
	private static Logger logger = LoggerFactory
			.getLogger(ContextHistory.class);
	/**
	 * An ArrayList for storing the Contexts of this ContextHistory
	 */
	private ArrayList<IContext> history;

	/**
	 * The constructor for the ContextHistory Object.
	 * 
	 * If this ContextHistory is not used as a SubHistory e.g. for a block it
	 * must be initialized with a context named root with null binding
	 * 
	 * @param initialContext
	 *            for this history
	 */
	public ContextHistory(IContext initialContext) {
		logger.trace("in: initialContext={}", initialContext);
		if (initialContext == null)
			throw new IllegalArgumentException();
		history = new ArrayList<IContext>();
		history.add(initialContext);
		logger.trace("return:");
	}

	/**
	 * Returns the IContext which is initial (that means the last one before it)
	 * for a given id
	 * 
	 * @param id
	 *            for which the initialContext should be retrieved
	 * @return the initial Context for the id
	 */
	public IContext getInitialContextForId(String id) {
		logger.trace("{}", id);
		logger.debug("{}", history);
		Iterator<IContext> it = history.iterator();
		IContext last = null;
		IContext next;
		while (it.hasNext()) {
			next = it.next();
			if (next.getId().equals(id)) {
				logger.trace("{}", last);
				return last;
			}
			last = next;
		}
		return last;
	}

	/**
	 * Inserts a ContextHistory into this History and sets all IContext ids of
	 * the inserted History, except the first one, to id.
	 * 
	 * The first IContext in the inserted History has to be inside this history.
	 * Its used like an anchor in order to know where to insert the other
	 * Contexts.
	 * 
	 * @param id
	 *            for the new Contexts
	 * @param contextHistory
	 *            the History to insert
	 */
	public void setContextsForId(String id, ContextHistory contextHistory) {
		logger.trace("in: id0={}, context={}", id, contextHistory);

		// TODO Feature: dispose IContext when his Binding is not present in
		// History after remove (when implemented in CLI)
		this.removeContextsWithId(id);

		int initialContextIndex = this.getIndexForLastContext(contextHistory
				.get(0));
		if (initialContextIndex == -1)
			initialContextIndex = history.size() - 1;

		int insertIndex = initialContextIndex + 1;
		if (contextHistory.size() == 1) {
			IContext newContext = contextHistory.get(0).getDuplicate();
			newContext.setId(id);
			this.add(insertIndex, newContext);
		} else {
			boolean first = true;
			for (IContext context : contextHistory) {
				if (first) {
					first = false;
					continue;
				}
				context.setId(id);
				this.add(insertIndex, context);
				insertIndex++;
			}
		}
		logger.debug("History={}", history);
		logger.trace("return:");
	}

	/**
	 * Returns the Index of the last occurrence of the IContexts Binding in this
	 * ContextHistory
	 * 
	 * @param context
	 *            containing the Binding to retrieve the index for
	 * @return the index of the IContext
	 */
	private int getIndexForLastContextBinding(IContext context) {
		logger.trace("in: context={}", context);
		int index = this.history.size();
		ListIterator<IContext> it = this.history.listIterator(index);
		IContext previous;
		while (it.hasPrevious()) {
			previous = it.previous();
			index--;
			if (previous.equalsBindings(context)) {
				logger.trace("return: index:{}", index);
				return index;
			}
		}
		logger.trace("return: index={}", -1);
		return -1;
	}

	/**
	 * Returns the Index of the last occurrence of this context
	 * 
	 * @param context
	 *            to retrieve the index for
	 * @return the index of the last occurrence of the IContext
	 */
	private int getIndexForLastContext(IContext context) {
		logger.trace("in: context={}", context);
		int index = this.history.size();
		ListIterator<IContext> it = this.history.listIterator(index);
		IContext previous;
		while (it.hasPrevious()) {
			previous = it.previous();
			index--;
			if (previous.equals(context)) {
				logger.trace("return: index:{}", index);
				return index;
			}
		}
		logger.trace("return: index={}", -1);
		return -1;
	}

	/**
	 * Removes all IContext Objects from this ContextHistory with the specified
	 * id
	 * 
	 * @param id
	 *            of the IContexts to be removed
	 * @return the list of contexts which are removed
	 */
	private List<IContext> removeContextsWithId(String id) {
		logger.trace("in: id={}", id);
		ArrayList<IContext> removed = new ArrayList<IContext>();
		ListIterator<IContext> it = history.listIterator();
		IContext next;
		while (it.hasNext()) {
			next = it.next();
			if (next.getId().equals(id)) {
				removed.add(next);
				it.remove();
			}
		}
		logger.trace("return: removedContexts={}", removed);
		return removed;
	}

	/**
	 * Returns the IContext for the given index
	 * 
	 * @param index
	 *            of the context
	 * @return the context at index
	 */
	public IContext get(int index) {
		return this.history.get(index);
	}

	/**
	 * Returns the number of IContext Objects in this history
	 * 
	 * @return the size of the history
	 */
	public int size() {
		logger.trace("{}", history.size());
		return history.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<IContext> iterator() {
		return this.history.iterator();
	}

	/**
	 * Returns the last Context in the History
	 * 
	 * @return the last Context
	 */
	public IContext last() {
		logger.trace("", history.get(history.size() - 1));
		logger.debug("{}", history);
		return history.get(history.size() - 1);
	}

	/**
	 * Appends a context to the end of this history If the context equals the
	 * last one (id and bindings) the context isn't inserted
	 * 
	 * @param context
	 */
	public void add(IContext context) {
		logger.trace("in: context={}", context);
		boolean equals = context.equals(last());
		if (equals) {
			return;
		}
		this.history.add(context);
		logger.debug("{}", history);
		logger.trace("return:");
	}

	/**
	 * Inserts a context into this history at the specified index if the
	 * inserted context equals(id and binding) the one at or previous the
	 * insertion point it isn't inserted
	 * 
	 * @param index
	 *            to insert the context to
	 * @param context
	 *            to be inserted
	 */
	public void add(int index, IContext context) {
		logger.trace("in: context={}", context);
		if (index >= history.size())
			this.add(context);

		boolean equalsAt = context.equals(history.get(index));
		boolean equalsPrevious = false;
		if (index > 0) {
			equalsPrevious = context.equals(history.get(index - 1));
		}
		if (!equalsAt && !equalsPrevious) {
			logger.debug("Context is inserted into History");
			history.add(index, context);
			return;
		} else {
			logger.debug("Context is not inserted becaus its already there");
		}

		logger.trace("return:");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return history.toString();
	}

	/**
	 * Returns an ordered ArrayList of IContext Objects containing all Contexts
	 * of this History
	 * 
	 * @return the Contexts of this history
	 */
	public ArrayList<IContext> getHistory() {
		return history;
	}

	/**
	 * Removes all IContext after and including the contexts with the given id
	 * 
	 * @param id
	 *            for the first context to be removed
	 */
	public void removeHistoryAfterInitial(String id) {
		logger.trace("{}", id);
		logger.debug("{}", history);
		Iterator<IContext> it = history.iterator();
		IContext next;
		boolean found = false;
		while (it.hasNext()) {
			next = it.next();
			if (next.getId().equals(id)) {
				found = true;
			}
			if (found) {
				next.destroy();
				it.remove();
			}
		}
		return;
	}

}
