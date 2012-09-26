package de.prob.ui.operationview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.google.inject.Injector;

import de.prob.animator.domainobjects.OpInfo;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.History;
import de.prob.statespace.HistoryElement;
import de.prob.statespace.StateId;
import de.prob.webconsole.ServletContextListener;

public class ExecuteRandomHandler extends AbstractHandler implements IHandler {

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);
		int steps = -1;
		try {
			steps = Integer.parseInt(event
					.getParameter("de.prob.ui.randomoperation.steps"));
		} catch (NumberFormatException e) {
			// ignore, we will open Dialog
		}

		if (steps < 0) {
			try {
				steps = askForValue(shell);
			} catch (NumberFormatException e) {
				// something went terribly wrong, do nothing
				return null;
			}

		}

		animate(steps);

		return null;
	}

	public void animate(final int steps) {
		Injector injector = ServletContextListener.INJECTOR;
		AnimationSelector selector = injector.getInstance(AnimationSelector.class);
		
		History currentHistory = selector.getCurrentHistory();
		History randomHistory = currentHistory.randomAnimation(steps);
		randomHistory.notifyAnimationChange(currentHistory, randomHistory);
	}

	private int askForValue(final Shell shell) {
		InputDialog inputDialog = new InputDialog(shell, "Random Animation",
				"Number of steps:", "1", new IInputValidator() {
					String errormsg = "Number must be a non-negative Integer.";

					public String isValid(final String newText) {
						Integer num;
						try {
							num = Integer.parseInt(newText);
						} catch (NumberFormatException e) {
							return errormsg;
						}
						if (num < 0) {
							return errormsg;
						}
						return null;
					}
				});
		inputDialog.open();
		String answer = inputDialog.getValue();
		if (answer != null) {
			return Integer.parseInt(answer);
		} else
			throw new NumberFormatException();
	}
}
