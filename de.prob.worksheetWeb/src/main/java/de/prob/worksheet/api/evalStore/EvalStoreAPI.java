package de.prob.worksheet.api.evalStore;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

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
			notifyOutputListeners(IEvalStoreConstants.NO_ANIMATION,
					"No Animation is started", "Initialize State", null);
			EvalStoreAPI.logger.trace("return: No Animation is started");
			return;
			// TODO try to find a way to start an animation if no is present
			/*
			 * logger.debug("No History present! Injecting new IAnimator");
			 * animator = ServletContextListener.INJECTOR
			 * .getInstance(IAnimator.class); sId = "root"; animator.execute(new
			 * StartAnimationCommand());
			 * this.notifyOutputListeners(EvalStoreAPI.OUTPUT_STATE_ID,
			 * EvalStoreAPI.OUTPUT_STATE_NAME, EvalStoreAPI.OUTPUT_STATE_DESC,
			 * "New Animation started","HTML","");
			 */
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
				"Initialize State", null);
		EvalStoreAPI.logger.trace("return:");
	}

	public void evaluate(String expression) {
		EvalStoreAPI.logger.trace("{}", expression);
		Long before = evalStoreId;
		if (evalStoreId == null) {
			notifyErrorListeners(IEvalStoreConstants.NOT_INITIALIZED,
					"no State is initialized (call getCurrentState)", true);
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
				EvalStoreAPI.logger.debug("{}", storeResult.getResult());
				notifyOutputListeners(IEvalStoreConstants.CMD_RESULT,
						storeResult.getResult().getValue(), "HTML", null);
				storeResult.getResult().getValue();
				EvalStoreAPI.logger.debug("Result.value = {}",
						storeResult.getResult().value);
				EvalStoreAPI.logger.debug("Result.explanation = {}",
						storeResult.getResult().explanation);
				EvalStoreAPI.logger.debug("Result.solution = {}",
						storeResult.getResult().solution);
				EvalStoreAPI.logger.debug("Result.getErrors = {}", storeResult
						.getResult().getErrors());
				EvalStoreAPI.logger.debug("Result.getQuanitfied = {}",
						storeResult.getResult().getQuantifiedVars());
				EvalStoreAPI.logger.debug("Result.getResultType = {}",
						storeResult.getResult().getResultType());

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
			notifyErrorListeners(IEvalStoreConstants.EXCEPTION, e.getMessage(),
					true);

		}

	}

	public void getStoreValues() {
		if (evalStoreId == null) {
			notifyOutputListeners(
					IEvalStoreConstants.CMD_RESULT,
					"No State is selected! Open Initialize State before getting his values",
					"State Values", null);
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
				"State Values", null);
	}

	public void setEvalStoreId(Long id) {
		EvalStoreAPI.logger.trace("{}", id);
		evalStoreId = id;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
