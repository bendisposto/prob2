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
		logger.trace("in: animations={}", animations.getHistories());
		this.animations = animations;

		logger.trace("return:");
	}

	public void getCurrentState() {
		logger.trace("in:");
		Long before = this.evalStoreId;
		this.animations = ServletContextListener.INJECTOR
				.getInstance(AnimationSelector.class);
		logger.debug("Animations: " + animations.getHistories());
		History currentHistory = animations.getCurrentHistory();
		logger.debug("CurrentHistory: " + currentHistory);
		String sId;
		IAnimator animator = null;
		if (currentHistory == null) {
			// logger.debug("No History present! Injecting new IAnimator");
			animator = ServletContextListener.INJECTOR
					.getInstance(IAnimator.class);
			sId = "root";
			this.notifyOutputListeners(IEvalStoreConstants.NO_ANIMATION,
					"No Animation is started", "Initialize State", null);
			logger.trace("return: No Animation is started");
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
		logger.debug("Current StateId" + sId);

		EvalstoreCreateByStateCommand cmd = new EvalstoreCreateByStateCommand(
				sId);
		animator.execute(cmd);
		this.evalStoreId = cmd.getEvalstoreId();
		logger.debug("EvalstoreId: " + this.evalStoreId);
		GetStateValuesCommand valCmd = GetStateValuesCommand
				.getEvalstoreValuesCommand(this.evalStoreId);
		animator.execute(valCmd);

		String output = "";
		HashMap<String, String> values = valCmd.getResult();
		logger.debug("Current Store Values: " + values);

		Set<Entry<String, String>> entries = values.entrySet();
		for (Entry<String, String> value : entries) {
			output += value.getKey() + ":=" + value.getValue() + "\n";
		}
		notifyActionListeners(IEvalStoreConstants.STORE_CHANGE, "", before,
				this.evalStoreId);
		if (output.equals(""))
			output = "{}";
		notifyOutputListeners(IEvalStoreConstants.CMD_RESULT, output,
				"Initialize State", null);
		logger.trace("return:");
	}

	public void evaluate(String expression) {
		logger.trace("{}", expression);
		Long before = this.evalStoreId;
		if (this.evalStoreId == null) {
			this.notifyErrorListeners(IEvalStoreConstants.NOT_INITIALIZED,
					"no State is initialized (call getCurrentState)", true);
			return;
		}
		IEvalElement eval = new EventB(expression);
		EvalstoreEvalCommand cmd = new EvalstoreEvalCommand(this.evalStoreId,
				eval);
		try {
			this.animations.getCurrentHistory().getStatespace().execute(cmd);
			EvalstoreResult storeResult = cmd.getResult();
			if (storeResult.isSuccess()) {
				this.evalStoreId = storeResult.getResultingStoreId();
				this.notifyActionListeners(IEvalStoreConstants.STORE_CHANGE,
						"", before, this.evalStoreId);
				logger.debug("{}", storeResult.getResult());
				this.notifyOutputListeners(IEvalStoreConstants.CMD_RESULT,
						storeResult.getResult().getValue(), "HTML", null);
				storeResult.getResult().getValue();
				logger.debug("Result.value = {}", storeResult.getResult().value);
				logger.debug("Result.explanation = {}",
						storeResult.getResult().explanation);
				logger.debug("Result.solution = {}",
						storeResult.getResult().solution);
				logger.debug("Result.getErrors = {}", storeResult.getResult()
						.getErrors());
				logger.debug("Result.getQuanitfied = {}", storeResult
						.getResult().getQuantifiedVars());
				logger.debug("Result.getResultType = {}", storeResult
						.getResult().getResultType());

			} else {
				if (storeResult.hasInterruptedOccurred()) {
					this.notifyErrorListeners(IEvalStoreConstants.INTERRUPT,
							"No Success Interrupt", true);
					logger.error("{}", storeResult.getResult());
				}
				if (storeResult.hasTimeoutOccurred()) {
					this.notifyErrorListeners(IEvalStoreConstants.TIMEOUT,
							"No Success Timeout", true);
					logger.error("{}", storeResult.getResult());
				}
				if (storeResult.getResult().hasError()) {
					this.notifyErrorListeners(IEvalStoreConstants.CMD_ERROR,
							"No Success Result Error: "
									+ storeResult.getResult().getErrors(), true);
					logger.error("{}", storeResult.getResult());
				}
			}
		} catch (Exception e) {
			this.notifyErrorListeners(IEvalStoreConstants.EXCEPTION,
					e.getMessage(), true);

		}

	}

	public void getStoreValues() {
		if (this.evalStoreId == null) {
			this.notifyOutputListeners(
					IEvalStoreConstants.CMD_RESULT,
					"No State is selected! Open Initialize State before getting his values",
					"State Values", null);
			return;
		}
		GetStateValuesCommand cmd = GetStateValuesCommand
				.getEvalstoreValuesCommand(this.evalStoreId);
		animations.getCurrentHistory().getStatespace().execute(cmd);

		HashMap<String, String> values = cmd.getResult();
		logger.debug("Current Store Values: " + values);
		String output = "";
		Set<Entry<String, String>> entries = values.entrySet();
		for (Entry<String, String> value : entries) {
			output += value.getKey() + ":=" + value.getValue() + "\n";
		}

		notifyOutputListeners(IEvalStoreConstants.CMD_RESULT, output,
				"State Values", null);
	}

	public void setEvalStoreId(Long id) {
		logger.trace("{}", id);
		this.evalStoreId = id;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
