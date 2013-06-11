package de.prob.ui.visualization;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;

import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EvalElementFactory;
import de.prob.animator.domainobjects.EvaluationException;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.exception.ProBLoggerFactory;
import de.prob.webconsole.ServletContextListener;

public class OpenPredicateVizHandler extends AbstractHandler implements
		IHandler {

	Logger logger = ProBLoggerFactory.getLogger(OpenPredicateVizHandler.class);

	private final EvalElementFactory evalFactory;

	public OpenPredicateVizHandler() {

		evalFactory = ServletContextListener.INJECTOR
				.getInstance(EvalElementFactory.class);
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);
		String encodedFormula = event.getParameter("de.prob.ui.viz.eval");

		if (encodedFormula.equals("enter")) {
			encodedFormula = askForValue(shell);
		}

		IEvalElement e = evalFactory.deserialize(encodedFormula);
		OpenFormula open = new OpenFormula();
		open.run(e);

		return null;
	}

	private String askForValue(final Shell shell) {
		InputDialog inputDialog = new InputDialog(shell, "Formula Input",
				"Enter a formula for visualization:", null,
				new IInputValidator() {
					String errormsg = "Input must be a valid Classical B formula";

					@Override
					public String isValid(final String newText) {
						try {
							ClassicalB formula = new ClassicalB(newText);
						} catch (Exception e) {
							return errormsg;
						}
						return null;
					}
				});
		inputDialog.open();
		String answer = inputDialog.getValue();
		if (answer != null) {
			return new ClassicalB(answer).serialized();
		} else {
			throw new EvaluationException(
					"String not valid Classical B formula");
		}
	}

}
