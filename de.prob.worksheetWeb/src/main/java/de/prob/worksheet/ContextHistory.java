package de.prob.worksheet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ContextHistory stores a list of IContext objects in the order they are
 * added or merged
 * 
 * @author Rene
 */
public class ContextHistory implements Iterable<IContext> {
	Logger logger = LoggerFactory.getLogger(ContextHistory.class);
	private ArrayList<IContext> history;

	public ContextHistory(IContext initialContext) {
		logger.trace("{}", initialContext);
		if (initialContext == null)
			throw new IllegalArgumentException();
		history = new ArrayList<IContext>();
		history.add(initialContext);
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
		logger.trace("{}", id);
		logger.trace("{}", contextHistory);

		ListIterator<IContext> it = history.listIterator();
		int index = -1;
		int x = 0;
		IContext next;
		while (it.hasNext()) {
			next = it.next();
			if (next.getId().equals(id)) {
				next.destroy();
				it.remove();
			}
			if (index == -1) {
				if (next.equals(contextHistory.get(0)))
					index = x + 1;
				else
					x++;
			}
		}
		logger.debug("{}", history);

		if (index == -1)
			index = history.size();
		boolean first = true;
		for (IContext context : contextHistory) {
			if (first) {
				first = false;
				continue;
			}
			context.setId(id);
			this.add(index, context);
			index++;
		}
		logger.trace("{}", history);
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
	 * Appends a context to the end of this history
	 * 
	 * @param context
	 */
	public void add(IContext context) {
		logger.trace("{}", context);
		boolean equals = context.equals(last());
		logger.debug("{}", equals);
		if (equals) {
			return;
		}
		this.history.add(context);
		logger.debug("{}", history);
	}

	/**
	 * Inserts a context into this history at the specified index
	 * 
	 * @param index
	 *            to insert the context to
	 * @param context
	 *            to be inserted
	 */
	public void add(int index, IContext context) {
		logger.trace("{}", context);
		if (index < history.size()) {
			boolean equals = context.equalsBindings(history.get(index));
			logger.debug("{}", equals);
			if (equals) {
				history.get(index).setId(context.getId());
				return;
			}
		}
		this.history.add(index, context);

	}

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
	 * @param id for the first context to be removed
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
