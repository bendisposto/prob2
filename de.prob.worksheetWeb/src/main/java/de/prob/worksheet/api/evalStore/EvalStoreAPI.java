package de.prob.worksheet.api.evalStore;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.be4.classicalb.core.parser.node.Node;
import de.prob.animator.IAnimator;
import de.prob.animator.command.EvalstoreCreateByStateCommand;
import de.prob.animator.command.EvalstoreEvalCommand;
import de.prob.animator.command.EvalstoreEvalCommand.EvalstoreResult;
import de.prob.animator.command.GetStateValuesCommand;
import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.History;
import de.prob.statespace.StateId;
import de.prob.webconsole.ServletContextListener;
import de.prob.worksheet.api.DefaultWorksheetAPI;
import de.prob.worksheet.api.IContext;
import de.prob.worksheet.block.impl.HTMLBlock;
import de.prob.worksheet.block.impl.StoreValuesBlock;
import de.prob.worksheet.evaluator.evalStore.IEvalStoreConstants;

public class EvalStoreAPI extends DefaultWorksheetAPI {

	private Long evalStoreId;
	private AnimationSelector animations;

	public static Logger logger = LoggerFactory.getLogger(EvalStoreAPI.class);

	@Inject
	public EvalStoreAPI(AnimationSelector animations) {
		EvalStoreAPI.logger.trace("in: animations={}",
				animations.getHistories());
		this.animations = animations;

		EvalStoreAPI.logger.trace("return:");
	}

	public void getCurrentState() {
		EvalStoreAPI.logger.trace("in:");
		Long before = evalStoreId;
		animations = ServletContextListener.INJECTOR
				.getInstance(AnimationSelector.class);
		EvalStoreAPI.logger.debug("Animations: " + animations.getHistories());
		History currentHistory = animations.getCurrentHistory();
		EvalStoreAPI.logger.debug("CurrentHistory: " + currentHistory);
		String sId;
		IAnimator animator = null;
		if (currentHistory == null) {
			// logger.debug("No History present! Injecting new IAnimator");
			animator = ServletContextListener.INJECTOR
					.getInstance(IAnimator.class);
			sId = "root";
			notifyErrorListeners(
					IEvalStoreConstants.NO_ANIMATION,
					"No Animation is started. You have to start an ProB animation before using the worksheet",
					true);
			EvalStoreAPI.logger.trace("return: No Animation is started");
			return;
		} else {
			StateId stateId = currentHistory.getCurrentState();
			sId = stateId.getId();
			animator = currentHistory.getStatespace();
		}
		EvalStoreAPI.logger.debug("Current StateId" + sId);

		EvalstoreCreateByStateCommand cmd = new EvalstoreCreateByStateCommand(
				sId);
		animator.execute(cmd);
		evalStoreId = cmd.getEvalstoreId();
		EvalStoreAPI.logger.debug("EvalstoreId: " + evalStoreId);
		GetStateValuesCommand valCmd = GetStateValuesCommand
				.getEvalstoreValuesCommand(evalStoreId);
		animator.execute(valCmd);

		String output = "";
		HashMap<String, String> values = valCmd.getResult();
		EvalStoreAPI.logger.debug("Current Store Values: " + values);

		Set<Entry<String, String>> entries = values.entrySet();
		for (Entry<String, String> value : entries) {
			output += value.getKey() + "=" + value.getValue() + "\n";
		}
		notifyActionListeners(IEvalStoreConstants.STORE_CHANGE, "", before,
				evalStoreId);
		if (output.equals(""))
			output = "{}";
		notifyOutputListeners(IEvalStoreConstants.CMD_RESULT, output,
				"Get state from animation", null);
		EvalStoreAPI.logger.trace("return:");
	}

	public void evaluate(String expression) {
		EvalStoreAPI.logger.trace("{}", expression);
		Long before = evalStoreId;
		if (evalStoreId == null) {
			notifyErrorListeners(IEvalStoreConstants.NOT_INITIALIZED,
					"No state is selected (call: Get state from animation)",
					true);
			return;
		}
		IEvalElement eval = new EventB(expression);
		EvalstoreEvalCommand cmd = new EvalstoreEvalCommand(evalStoreId, eval);
		try {
			animations.getCurrentHistory().getStatespace().execute(cmd);
			EvalstoreResult storeResult = cmd.getResult();
			if (storeResult.isSuccess()) {
				evalStoreId = storeResult.getResultingStoreId();
				notifyActionListeners(IEvalStoreConstants.STORE_CHANGE, "",
						before, evalStoreId);

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
		if (evalStoreId == null) {
			notifyErrorListeners(IEvalStoreConstants.NOT_INITIALIZED,
					"No State is selected! (call: Get state from animation)",
					true);
			return;
		}
		GetStateValuesCommand cmd = GetStateValuesCommand
				.getEvalstoreValuesCommand(evalStoreId);
		animations.getCurrentHistory().getStatespace().execute(cmd);

		HashMap<String, String> values = cmd.getResult();
		EvalStoreAPI.logger.debug("Current Store Values: " + values);
		String output = "";
		Set<Entry<String, String>> entries = values.entrySet();
		for (Entry<String, String> value : entries) {
			output += value.getKey() + "=" + value.getValue() + "\n";
		}
		if (output.equals(""))
			output = "{}";
		notifyOutputListeners(IEvalStoreConstants.CMD_RESULT, output,
				StoreValuesBlock.PRINT_NAME, null);
	}

	public void analyzeAst(String expr) {

		try {
			System.out.println(expr);
			Long before = evalStoreId;
			/*
			 * if (evalStoreId == null) {
			 * notifyOutputListeners(IEvalStoreConstants.CMD_RESULT,
			 * "No State is selected! (call: Get state from animation)",
			 * StoreValuesBlock.PRINT_NAME, null); return; }
			 */
			SubExpr test = new SubExpr();
			Node ast = new EventB(expr).getAst();

			ast.apply(test);
			EvalStoreAPI.logger.debug("{}", test.exps);
			EvalStoreAPI.logger.debug("{}", test.Nodes);
			JNode root = test.Nodes.getLast();
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
		Long before = evalStoreId;
		if (evalStoreId == null) {
			notifyErrorListeners(IEvalStoreConstants.NOT_INITIALIZED_EVAL,
					"No state is selected (call: Get state from animation)",
					true);
			return "Error";
		}
		IEvalElement eval = new EventB(expression);
		EvalstoreEvalCommand cmd = new EvalstoreEvalCommand(evalStoreId, eval);
		try {
			animations.getCurrentHistory().getStatespace().execute(cmd);
			EvalstoreResult storeResult = cmd.getResult();
			if (storeResult.isSuccess()) {
				evalStoreId = storeResult.getResultingStoreId();
				notifyActionListeners(IEvalStoreConstants.STORE_CHANGE, "",
						before, evalStoreId);

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
			Long id = (Long) context.getBinding("EvalStoreId");
			EvalStoreAPI.logger.trace("{}", id);
			evalStoreId = id;
		}
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
