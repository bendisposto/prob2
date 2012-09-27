package de.prob.statespace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Singleton;

import de.prob.model.representation.AbstractModel;

@Singleton
public class AnimationSelector implements IAnimationListener {
	
	List<IHistoryChangeListener> listeners = new ArrayList<IHistoryChangeListener>();
	List<History> histories = new ArrayList<History>();
	History currentHistory = null;
	Map<History, AbstractModel> models = new HashMap<History, AbstractModel>();
	
	public void registerHistoryChangeListener(IHistoryChangeListener listener) {
		listeners.add(listener);
		if( currentHistory != null ) {
			notifyHistoryChange(currentHistory,models.get(currentHistory));
		}
	}
	
	@Override
	public void currentStateChanged(History oldHistory, History newHistory) {
		if(oldHistory.equals(currentHistory)) {
			notifyHistoryChange(newHistory,models.get(oldHistory));
		}
		histories.set(histories.indexOf(oldHistory), newHistory);
		models.put(newHistory, models.get(oldHistory));
		models.remove(oldHistory);
		currentHistory = newHistory;
	}

	public void changeCurrentHistory(History history) {
		currentHistory = history;
		notifyHistoryChange(history, models.get(history));
	}
	
	public void addNewHistory(History history, AbstractModel model) {
		histories.add(history);
		models.put(history, model);
		history.registerAnimationListener(this);
		currentHistory = history;
		notifyHistoryChange(history, model);
	}
	
	public void notifyHistoryChange(History history, AbstractModel model) {
		for (IHistoryChangeListener listener : listeners) {
			listener.historyChange(history,model);
		}
	}
	
	public History getCurrentHistory() {
		return currentHistory;
	}
	
	public List<History> getHistories() {
		return histories;
	}
	
	public AbstractModel getModel(History history) {
		return models.get(history);
	}
}