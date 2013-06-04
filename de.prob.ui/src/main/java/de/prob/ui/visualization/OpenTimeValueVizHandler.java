package de.prob.ui.visualization;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.window.Window;
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
		String timeExpression = "";

		if (encodedFormula.equals("enter")
				&& animations.getCurrentTrace() != null) {
			Answer answer = askForValue(shell, animations.getCurrentTrace()
					.getModel());
			encodedFormula = answer.formula;
			timeExpression = answer.timeExpression;
		}

		try {
			IEvalElement formula = evalFactory.deserialize(encodedFormula);
			IEvalElement time = timeExpression.equals("") ? null : evalFactory
					.deserialize(timeExpression);
			String sessionId = VisualizationUtil.createSessionId();
			servlet.openSession(sessionId, formula, time);
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

	private Answer askForValue(final Shell shell, final AbstractModel model) {
		SpecifyFormulaDialog dialog = new SpecifyFormulaDialog(shell, model);
		dialog.create();
		if (dialog.open() == Window.OK) {

			String expr = dialog.getExpression();
			String time = dialog.getTimeExpression();
			if (model instanceof ClassicalBModel) {
				return new Answer(new ClassicalB(expr).serialized(),
						time.equals("") ? ""
								: new ClassicalB(time).serialized());
			} else if (model instanceof EventBModel) {
				return new Answer(new EventB(expr).serialized(),
						time.equals("") ? "" : new EventB(time).serialized());
			} else if (model instanceof CSPModel) {
				return new Answer(new CSP(expr, (CSPModel) model).serialized(),
						time.equals("") ? ""
								: new CSP(time, (CSPModel) model).serialized());
			}
		}
		throw new EvaluationException("String not a valid formula");
	}

	class Answer {
		public String formula;
		public String timeExpression;

		public Answer(final String formula, final String timeExpression) {
			this.formula = formula;
			this.timeExpression = timeExpression;
		}

	}

}
