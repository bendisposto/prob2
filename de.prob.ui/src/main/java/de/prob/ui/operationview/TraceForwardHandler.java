package de.prob.ui.operationview;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import com.google.inject.Injector;

import de.prob.statespace.AnimationSelector;
import de.prob.statespace.Trace;
import de.prob.webconsole.ServletContextListener;

public class TraceForwardHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {

		Injector injector = ServletContextListener.INJECTOR;
		AnimationSelector selector = injector
				.getInstance(AnimationSelector.class);

		Trace currentTrace = selector.getCurrentTrace();
		Trace forwardTrace = currentTrace.forward();
		selector.replaceTrace(currentTrace, forwardTrace);
		return null;
	}
}
