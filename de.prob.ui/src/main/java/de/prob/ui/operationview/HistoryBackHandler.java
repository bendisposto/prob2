package de.prob.ui.operationview;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import com.google.inject.Injector;

import de.prob.statespace.AnimationSelector;
import de.prob.statespace.History;
import de.prob.webconsole.ServletContextListener;

public class HistoryBackHandler extends AbstractHandler implements IHandler {

	public Object execute(final ExecutionEvent event) throws ExecutionException {

		Injector injector = ServletContextListener.INJECTOR;
		AnimationSelector selector = injector.getInstance(AnimationSelector.class);
		
		History currentHistory = selector.getCurrentHistory();
		History backHistory = currentHistory.back();
		backHistory.notifyAnimationChange(currentHistory, backHistory);
		return null;
	}
}
