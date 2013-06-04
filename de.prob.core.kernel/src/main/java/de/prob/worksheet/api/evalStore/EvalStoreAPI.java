package de.prob.worksheet.api.evalStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.be4.classicalb.core.parser.node.Node;
import de.prob.animator.command.EvalstoreCreateByStateCommand;
import de.prob.animator.command.EvalstoreEvalCommand;
import de.prob.animator.command.EvalstoreEvalCommand.EvalstoreResult;
import de.prob.animator.command.GetStateValuesCommand;
import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.Trace;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpace;
import de.prob.worksheet.api.DefaultWorksheetAPI;
import de.prob.worksheet.api.IContext;
import de.prob.worksheet.block.impl.HTMLBlock;
import de.prob.worksheet.block.impl.StoreValuesBlock;
import de.prob.worksheet.evaluator.evalStore.IEvalStoreConstants;

public class EvalStoreAPI extends DefaultWorksheetAPI {

	private Long evalStoreId;
	private StateSpace animation;
	private AnimationSelector animations;

	public static Logger logger = LoggerFactory.getLogger(EvalStoreAPI.class);

	@Inject
	public EvalStoreAPI(AnimationSelector animations) {
		EvalStoreAPI.logger.trace("in: animations={}",
				animations.getTraces());
		this.animations = animations;

		EvalStoreAPI.logger.trace("return:");
	}

	public void getCurrentState() {
		EvalStoreAPI.logger.trace("in:");

		// initialize new API Context
		Trace currentHistory = animations.getCurrentTrace();
		String sId;
		animation = null;
		if (currentHistory == null) {
			notifyErrorListeners(
					IEvalStoreConstants.NO_ANIMATION,
					"No Animation is started. You have to start an ProB animation before using the worksheet",
					true);
			EvalStoreAPI.logger.trace("return: No Animation is started");
			return;
		} else {
			StateId stateId = currentHistory.getCurrentState();
			sId = stateId.getId();
			animation = currentHistory.getStateSpace();
		}

		// create a new EvalStore from State
		EvalstoreCreateByStateCommand cmd = new EvalstoreCreateByStateCommand(
				sId);
		animation.execute(cmd);
		evalStoreId = cmd.getEvalstoreId();
		notifyStoreChange(animation, evalStoreId);

		// get State Values
		GetStateValuesCommand valCmd = GetStateValuesCommand
				.getEvalstoreValuesCommand(evalStoreId);
		animation.execute(valCmd);

		// generate Output
		String output = "";
		HashMap<String, String> values = valCmd.getResult();
		Set<Entry<String, String>> entries = values.entrySet();
		for (Entry<String, String> value : entries) {
			output += value.getKey() + "=" + value.getValue() + "\n";
		}
		if (output.equals(""))
			output = "{}";
		notifyOutputListeners(IEvalStoreConstants.CMD_RESULT, output,
				"Get state from animation", null);

		EvalStoreAPI.logger.trace("return:");
	}

	public void evaluate(String expression) {
		EvalStoreAPI.logger.trace("{}", expression);

		// check preconditions
		if (evalStoreId == null || animation == null) {
			notifyErrorListeners(IEvalStoreConstants.NOT_INITIALIZED_EVAL,
					"No state is selected (call: Get state from animation)",
					true);
			logger.debug("animation={}, StoreId={}", animation, evalStoreId);
			return;
		}
		if (isAnimationStopped(animation)) {
			notifyErrorListeners(
					IEvalStoreConstants.ANIMATION_STOPPED,
					"The History has been removed (call Get state from animation)",
					true);
			return;
		}

		try {
			IEvalElement eval = new EventB(expression);
			EvalstoreEvalCommand cmd = new EvalstoreEvalCommand(evalStoreId,
					eval);
			animation.execute(cmd);
			EvalstoreResult storeResult = cmd.getResult();
			if (storeResult.isSuccess()) {
				evalStoreId = storeResult.getResultingStoreId();
				notifyStoreChange(animation, evalStoreId);

				notifyOutputListeners(IEvalStoreConstants.CMD_RESULT,
						storeResult.getResult().getValue(), "HTML", null);
				storeResult.getResult().getValue();

			} else {
				if (storeResult.hasInterruptedOccurred()) {
					notifyErrorListeners(IEvalStoreConstants.INTERRUPT,
							"No Success Interrupt", true);
					EvalStoreAPI.logger.error("{}", storeResult.getResult());
				}
				if (storeResult.hasTimeoutOccurred()) {
					notifyErrorListeners(IEvalStoreConstants.TIMEOUT,
							"No Success Timeout", true);
					EvalStoreAPI.logger.error("{}", storeResult.getResult());
				}
				if (storeResult.getResult().hasError()) {
					notifyErrorListeners(IEvalStoreConstants.CMD_ERROR,
							"No Success Result Error: "
									+ storeResult.getResult().getErrors(), true);
					EvalStoreAPI.logger.error("{}", storeResult.getResult());
				}
			}
		} catch (Exception e) {
			if (e.getMessage() != null) {
				notifyErrorListeners(IEvalStoreConstants.EXCEPTION,
						e.getMessage(), true);
				EvalStoreAPI.logger.error("{}", e.getMessage());
			} else {
				notifyErrorListeners(
						IEvalStoreConstants.EXCEPTION,
						"ProB has thrown an exception maybe your expression is not correctly spelled.",
						true);
				EvalStoreAPI.logger
						.error("ProB has thrown an exceptrion maybe your expression is not correctly spelled.",
								e.getMessage());

			}
		}

	}

	public void out(String out) {
		notifyOutputListeners(IEvalStoreConstants.CMD_OUT, out,
				HTMLBlock.PRINT_NAME, null);
	}

	public void getStoreValues() {
		// check preconditions
		if (evalStoreId == null || animation == null) {
			notifyErrorListeners(IEvalStoreConstants.NOT_INITIALIZED,
					"No state is selected (call: Get state from animation)",
					true);
			return;
		}
		if (isAnimationStopped(animation)) {
			notifyErrorListeners(
					IEvalStoreConstants.ANIMATION_STOPPED,
					"The History has been removed (call Get state from animation)",
					true);
			return;
		}
		try {
			GetStateValuesCommand cmd = GetStateValuesCommand
					.getEvalstoreValuesCommand(evalStoreId);
			animations.getCurrentTrace().getStateSpace().execute(cmd);

			// generate Output String
			String output = generateStoreValuesOutput(cmd);
			notifyOutputListeners(IEvalStoreConstants.CMD_RESULT, output,
					StoreValuesBlock.PRINT_NAME, null);
		} catch (Exception e) {
			notifyErrorListeners(IEvalStoreConstants.EXCEPTION, "Exception:\n"
					+ e.getLocalizedMessage(), true);

		}
	}

	public void analyzeAst(String expr) {
		try {
			// generate Node tree for Expression
			SubExpr test = new SubExpr();
			Node ast = new EventB(expr).getAst();
			ast.apply(test);
			JNode root = test.Nodes.getLast();

			analyzeEvaluate(expr);
			// evaluate Nodes of Node Tree
			for (int x = test.exps.size() - 1; x >= 0; x--) {
				String exp = test.exps.get(x);
				JNode node = root.find(exp);
				String result = analyzeEvaluate(exp);
				if (result != null && exp != null) {
					node.setName(exp + "<br style='margin:2px;margin-top:0;'/>"
							+ result);
					if (result.equals("true")) {
						node.setColor("#00ff00");
					} else if (result.equals("false")) {
						node.setColor("#ff0000");
					} else {
						node.setColor("#00ffff");
					}
				}
			}
			notifyOutputListeners(IEvalStoreConstants.CMD_TREE, "", "Tree",
					root);
		} catch (Exception e) {
			if (e.getMessage() != null) {
				notifyErrorListeners(IEvalStoreConstants.EXCEPTION,
						e.getMessage(), true);
				EvalStoreAPI.logger.error("{}", e.getMessage());
			} else {
				notifyErrorListeners(
						IEvalStoreConstants.EXCEPTION,
						"ProB has thrown an exception maybe your expression is not correctly spelled.",
						true);
				EvalStoreAPI.logger
						.error("ProB has thrown an exceptrion maybe your expression is not correctly spelled.",
								e.getMessage());
			}
		}
	}

	private String analyzeEvaluate(String expression) {
		EvalStoreAPI.logger.trace("{}", expression);
		EvalStoreAPI.logger.debug("evaluating: " + expression);
		// check preconditions
		if (evalStoreId == null || animation == null) {
			notifyErrorListeners(IEvalStoreConstants.NOT_INITIALIZED_EVAL,
					"No state is selected (call: Get state from animation)",
					true);
			return "Error";
		}
		if (isAnimationStopped(animation)) {
			notifyErrorListeners(
					IEvalStoreConstants.ANIMATION_STOPPED,
					"The History has been removed (call Get state from animation)",
					true);
			return "Error";
		}
		try {
			IEvalElement eval = new EventB(expression);
			EvalstoreEvalCommand cmd = new EvalstoreEvalCommand(evalStoreId,
					eval);
			animations.getCurrentTrace().getStateSpace().execute(cmd);
			EvalstoreResult storeResult = cmd.getResult();
			if (storeResult.isSuccess()) {
				evalStoreId = storeResult.getResultingStoreId();
				notifyStoreChange(animation, evalStoreId);

				return storeResult.getResult().getValue();

			} else {
				if (storeResult.hasInterruptedOccurred()) {
					EvalStoreAPI.logger.error("{}", storeResult.getResult());
					return "No Success Interrupt";
				}
				if (storeResult.hasTimeoutOccurred()) {
					EvalStoreAPI.logger.error("{}", storeResult.getResult());
					return "No Success Timeout";
				}
				if (storeResult.getResult().hasError()) {
					EvalStoreAPI.logger.error("{}", storeResult.getResult());
					return "No Success Result Error: "
							+ storeResult.getResult().getErrors();
				}
			}
		} catch (Exception e) {
			notifyErrorListeners(IEvalStoreConstants.EXCEPTION, e.getMessage(),
					true);
			return ("error");
		}
		return "";

	}

	@Override
	public void setContext(IContext context) {
		if (!(context instanceof EvalStoreContext)) {
			notifyErrorListeners(
					IEvalStoreConstants.CONTEXT_ERROR,
					"Exception: A wrong context has been set. Please store your data and close this editor",
					true);
		} else {
			evalStoreId = (Long) context.getBinding("EvalStoreId");
			animation = (StateSpace) context.getBinding("StateSpace");
		}
	}

	// Helper Functions
	private void notifyStoreChange(StateSpace animation, Long evalStoreId) {
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(evalStoreId);
		data.add(animation);
		notifyActionListeners(IEvalStoreConstants.STORE_CHANGE, "", data);
	}

	private boolean isAnimationStopped(StateSpace animation) {
		logger.debug("statespace count:{}", animations.getStatespaces().size());
		return !animations.getStatespaces().contains(animation);
	}

	private String generateStoreValuesOutput(GetStateValuesCommand cmd) {
		HashMap<String, String> values = cmd.getResult();
		EvalStoreAPI.logger.debug("Current Store Values: " + values);
		String output = "";
		Set<Entry<String, String>> entries = values.entrySet();
		for (Entry<String, String> value : entries) {
			output += value.getKey() + "=" + value.getValue() + "\n";
		}
		if (output.equals(""))
			output = "{}";
		return output;
	}

}
