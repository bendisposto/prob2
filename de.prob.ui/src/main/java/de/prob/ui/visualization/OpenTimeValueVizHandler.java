package de.prob.ui.visualization;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

import de.prob.animator.domainobjects.CSP;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EvalElementFactory;
import de.prob.animator.domainobjects.EvaluationException;
import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.eventb.EventBModel;
import de.prob.model.representation.AbstractModel;
import de.prob.scripting.CSPModel;
import de.prob.statespace.AnimationSelector;
import de.prob.visualization.AnimationNotLoadedException;
import de.prob.webconsole.ServletContextListener;
import de.prob.webconsole.servlets.visualizations.ValueOverTimeServlet;

public class OpenTimeValueVizHandler extends AbstractHandler implements
		IHandler {

	Logger logger = LoggerFactory.getLogger(OpenTimeValueVizHandler.class);

	private final ValueOverTimeServlet servlet;
	private final EvalElementFactory evalFactory;
	private final AnimationSelector animations;

	public OpenTimeValueVizHandler() {
		Injector injector = ServletContextListener.INJECTOR;
		servlet = injector.getInstance(ValueOverTimeServlet.class);
		evalFactory = injector.getInstance(EvalElementFactory.class);
		animations = injector.getInstance(AnimationSelector.class);
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);
		String encodedFormula = event.getParameter("de.prob.ui.viz.eval");

		if (encodedFormula.equals("enter")
				&& animations.getCurrentHistory() != null) {
			encodedFormula = askForValue(shell, animations.getCurrentHistory()
					.getModel());
		}

		try {
			IEvalElement formula = evalFactory.deserialize(encodedFormula);
			String sessionId = VisualizationUtil.createSessionId();
			servlet.openSession(sessionId, formula);
			VisualizationUtil.createVisualizationViewPart("formula/?init="
					+ sessionId);
		} catch (PartInitException e) {
			logger.error("Could not create predicate visualization view: "
					+ e.getMessage());
		} catch (AnimationNotLoadedException e) {
			logger.error("Could not create predicate visualization because an animation is not loaded: "
					+ e.getMessage());
		}
		return null;
	}

	private String askForValue(final Shell shell, final AbstractModel model) {
		InputDialog inputDialog = new InputDialog(shell, "Formula Input",
				"Enter a formula for visualization:", null,
				new IInputValidator() {
					String errormsg = "Input must be a valid formula";

					@Override
					public String isValid(final String newText) {
						try {
							if (model instanceof ClassicalBModel) {
								new ClassicalB(newText);
							} else if (model instanceof EventBModel) {
								new EventB(newText);
							} else if (model instanceof CSPModel) {
								new CSP(newText, (CSPModel) model);
							}
						} catch (Exception e) {
							return errormsg;
						}
						return null;
					}
				});
		inputDialog.open();
		String answer = inputDialog.getValue();
		if (answer != null) {
			if (model instanceof ClassicalBModel) {
				return new ClassicalB(answer).serialized();
			} else if (model instanceof EventBModel) {
				new EventB(answer).serialized();
			} else if (model instanceof CSPModel) {
				new CSP(answer, (CSPModel) model).serialized();
			}

			return new ClassicalB(answer).serialized();
		} else {
			throw new EvaluationException("String not a valid formula");
		}
	}

}
