package de.prob.worksheet.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.statespace.StateSpace;
import de.prob.worksheet.api.evalStore.EvalStoreContext;

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
	private ArrayList<Integer> originalIndexes;

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
	public void setContextsForId(int index, String id,
			ContextHistory contextHistory) {
		logger.trace("in: id={}, context={}", id, contextHistory);

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<IContext> iterator() {
		logger.trace("in:");
		logger.trace("return: iterator={}", this.history.iterator());
		return this.history.iterator();
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
			logger.trace("return:");
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
				it.remove();
			}
		}
		logger.trace("return:");
		return;
	}

	// Refactoring
	public void reset() {
		logger.trace("in");
		IContext initial = this.history.get(0);
		this.history.clear();
		this.history.add(initial);
	}

	public void addEmptyContext(String id) {
		logger.trace("in");
		// TODO change from EvalStoreContext to a special empty Context
		EvalStoreContext context = new EvalStoreContext(id, null, null);
		history.add(context);
	}

	public void insertEmptyContext(String previousId, String newId) {
		logger.trace("in: previous={},new={}", previousId, newId);
		ListIterator<IContext> it = history.listIterator();
		boolean reached = false;
		IContext next;
		while (it.hasNext()) {
			next = it.next();
			if (!reached && next.getId().equals(previousId)) {
				reached = true;
			}
			if (reached && !next.getId().equals(previousId)) {
				// insert now
				if (it.hasPrevious())
					next = it.previous();
				it.add(new EvalStoreContext(newId, null, null));
				break;
			}
			if (!it.hasNext()) {
				it.add(new EvalStoreContext(newId, null, null));
			}
		}
		logger.debug("history={}", history);
	}

	public void remove(String id) {
		logger.trace("in");
		ListIterator<IContext> it = history.listIterator();
		IContext next;
		while (it.hasNext()) {
			next = it.next();
			if (next.getId().equals(id)) {
				it.remove();
			}
		}
	}

	public void setContexts(String id, ContextHistory blockHistory) {
		logger.trace("in: id={}, blockHository={}", id, blockHistory);
		/*
		 * Cases: 1. Keine neue History für die id 2.
		 */

		int insertIndex = firstIndexForId(id);
		remove(id);
		if (blockHistory.size() == 1) {
			history.add(insertIndex, new EvalStoreContext(id,
					(Long) blockHistory.get(0).getBinding("EvalStoreId"),
					(StateSpace) blockHistory.get(0).getBinding("StateSpace")));
		} else {
			blockHistory.history.remove(0);
			for (IContext context : blockHistory.history)
				context.setId(id);
			history.addAll(insertIndex, blockHistory.history);
		}
		logger.debug("History={}", history);
	}

	private int firstIndexForId(String id) {
		logger.trace("in");
		ListIterator<IContext> it = history.listIterator();
		IContext next;
		while (it.hasNext()) {
			int nextIndex = it.nextIndex();
			next = it.next();
			if (next.getId().equals(id))
				return nextIndex;
		}
		return -1;
	}

	private int lastIndexForId(String id) {
		logger.trace("in");
		ListIterator<IContext> it = history.listIterator();
		boolean reached = false;
		IContext next;
		while (it.hasNext()) {
			int nextIndex = it.nextIndex();
			next = it.next();
			if (!reached && next.getId().equals(id)) {
				reached = true;
			}
			if (reached && !next.getId().equals(id)) {
				// insert now
				if (it.hasPrevious())
					nextIndex--;
				return nextIndex;
			}
		}
		return -1;
	}

	public void reset(String id) {
		logger.trace("in");
		ListIterator<IContext> it = history.listIterator();
		boolean reachedFirst = false;
		IContext next;
		String lastId = null;
		while (it.hasNext()) {
			int nextIndex = it.nextIndex();
			next = it.next();

			if (!reachedFirst && next.getId().equals(id)) {
				reachedFirst = true;
			}
			if (reachedFirst) {
				next.resetBindings();
			}
			if (reachedFirst && lastId.equals(next.getId())) {
				it.remove();
			}
			lastId = next.getId();
		}
	}

	// refactoring copied from previous
	/**
	 * Returns the IContext which is initial (that means the last one before it)
	 * for a given id. If the id doesn't have a context the last context of the
	 * History is returned
	 * 
	 * @param id
	 *            for which the initialContext should be retrieved
	 * @return the initial Context for the id
	 */
	public IContext getInitialContextForId(String id) {
		logger.trace("in: id={}", id);
		logger.debug("History={}", history);
		Iterator<IContext> it = history.iterator();
		IContext last = null;
		IContext next;
		while (it.hasNext()) {
			next = it.next();
			if (next.getId().equals(id)) {
				logger.trace("return: context={}", last);
				return last;
			}
			last = next;
		}
		logger.trace("return: context={}", last);
		return last;
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
		logger.trace("in:");
		logger.trace("return: History={}", history);
		return history;
	}

	/**
	 * Returns the last Context in the History
	 * 
	 * @return the last Context
	 */
	public IContext last() {
		logger.trace("in:");
		logger.debug("History={}", history);
		logger.trace("return: context={}", history.get(history.size() - 1));
		return history.get(history.size() - 1);
	}

	/**
	 * Returns the IContext for the given index
	 * 
	 * @param index
	 *            of the context
	 * @return the context at index
	 */
	public IContext get(int index) {
		logger.trace("in: index={}", index);
		logger.trace("return: context={}", this.history.get(index));
		return this.history.get(index);
	}

	/**
	 * Returns the number of IContext Objects in this history
	 * 
	 * @return the size of the history
	 */
	public int size() {
		logger.trace("in:");
		logger.trace("return: size={}", history.size());
		return history.size();
	}
}
