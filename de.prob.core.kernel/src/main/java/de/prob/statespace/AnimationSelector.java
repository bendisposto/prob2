package de.prob.statespace;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Singleton;

@Singleton
public class AnimationSelector implements IAnimationListener {
	
	List<IHistoryChangeListener> listeners = new ArrayList<IHistoryChangeListener>();
	List<History> histories = new ArrayList<History>();
	History currentHistory = null;
	
	public void registerHistoryChangeListener(IHistoryChangeListener listener) {
		listeners.add(listener);
	}
	
	@Override
	public void currentStateChanged(History oldHistory, History newHistory) {
		if(oldHistory.equals(currentHistory)) {
			notifyHistoryChange(newHistory);
		}
		histories.set(histories.indexOf(oldHistory), newHistory);
		currentHistory = newHistory;
	}

	public void changeCurrentHistory(History history) {
		currentHistory = history;
		notifyHistoryChange(history);
	}
	
	public void addNewHistory(History history) {
		histories.add(history);
		history.registerAnimationListener(this);
		currentHistory = history;
		notifyHistoryChange(history);
	}
	
	public void notifyHistoryChange(History history) {
		for (IHistoryChangeListener listener : listeners) {
			listener.historyChange(history);
		}
	}
}