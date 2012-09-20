package de.prob.statespace;

import de.prob.model.representation.AbstractModel;

public interface IHistoryChangeListener {
	public void historyChange(History history, AbstractModel model);
}
