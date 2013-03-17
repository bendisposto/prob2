package de.prob.worksheet.evaluator.evalStore;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.prob.worksheet.api.ContextHistory;
import de.prob.worksheet.api.IContext;
import de.prob.worksheet.api.WorksheetErrorEvent;
import de.prob.worksheet.api.evalStore.EvalStoreAPI;
import de.prob.worksheet.block.impl.DefaultBlock;
import de.prob.worksheet.evaluator.IEvaluator;
import de.prob.worksheet.parser.SimpleConsoleParser;
import de.prob.worksheet.parser.SimpleConsoleParser.EvalObject;

public class StateEvaluator implements IEvaluator {

	private static Logger logger = LoggerFactory
			.getLogger(StateEvaluator.class);

	public EvalStoreAPI api = null;
	OutputListener outListener;
	ErrorListener errorListener;
	HistoryListener actionListener;

	ArrayList<DefaultBlock> outputBlocks = new ArrayList<DefaultBlock>();
	ContextHistory contextHistory;

	@Inject
	public StateEvaluator(EvalStoreAPI api) {
		outListener = new OutputListener(outputBlocks);
		errorListener = new ErrorListener(outputBlocks);

		this.api = api;
		this.api.addErrorListener(errorListener);
		this.api.addOutputListener(outListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.evaluator.IWorksheetEvaluator#setInitialContext(java
	 * .lang.String[])
	 */
	@Override
	public void setInitialContext(IContext context) {
		StateEvaluator.logger.trace("{}", context);
		contextHistory = new ContextHistory(context);
		actionListener = new HistoryListener(contextHistory);
		api.addActionListener(actionListener);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.evaluator.IWorksheetEvaluator#evaluate(java.lang.String
	 * )
	 */
	@Override
	public void evaluate(final String script) {
		StateEvaluator.logger.trace(script);
		if (api == null) {
			StateEvaluator.logger
					.error("Evaluator isn't correctly initialized. Api is missing.");
			return;
		}
		if (contextHistory.size() == 0) {
			StateEvaluator.logger
					.error("Evaluator isn't correctly initialized. initialContext is missing.");
			return;
		}
		evaluateScript(script);
		return;
	}

	private void evaluateObject(EvalObject evalObject) {
		StateEvaluator.logger.trace(evalObject.toString());
		// TODO Change to parserError thrown by Parser

		if (evalObject.methodInstance == null) {
			String errorString = "Method unknown: " + evalObject.method[0];
			// FIXME error arguments can produce an exception
			// if(evalObject.method.length>1)
			// errorString+=" with args ["+((Method)
			// evalObject.methodInstance).getParameterTypes()+"]";
			errorListener.notify(new WorksheetErrorEvent(3001, errorString,
					true));
		}

		StateEvaluator.logger.debug("{}", contextHistory.last());
		api.setContext(contextHistory.last());

		if (evalObject.methodInstance instanceof Method) {
			if (((Method) evalObject.methodInstance).getDeclaringClass()
					.isInstance(api)) {
				final Object[] args = Arrays.copyOfRange(evalObject.method, 1,
						evalObject.method.length);
				try {
					StateEvaluator.logger.debug("evaluator invokes +"
							+ evalObject.methodInstance + " with args: "
							+ Arrays.toString(args));
					((Method) evalObject.methodInstance).invoke(api, args);
				} catch (final IllegalAccessException e) {
					errorListener.notify(new WorksheetErrorEvent(3001,
							"You don't have access to the method "
									+ evalObject.method[0]
									+ " with args ["
									+ ((Method) evalObject.methodInstance)
											.getParameterTypes() + "]", true));
				} catch (final IllegalArgumentException e) {
					errorListener.notify(new WorksheetErrorEvent(3001,
							"Illegal arguments for method "
									+ evalObject.method[0]
									+ " with args ["
									+ ((Method) evalObject.methodInstance)
											.getParameterTypes() + "]", true));
				} catch (final InvocationTargetException e) {
					errorListener.notify(new WorksheetErrorEvent(3001,
							"Invocation arguments for method "
									+ evalObject.method[0]
									+ " with args ["
									+ ((Method) evalObject.methodInstance)
											.getParameterTypes() + "]", true));
					e.printStackTrace();
				}
			}
		}

	}

	private void evaluateObjects(EvalObject[] evalObjects) {
		StateEvaluator.logger.trace(Arrays.toString(evalObjects));
		for (EvalObject object : evalObjects) {
			if (errorListener.isHaltAll())
				break;
			evaluateObject(object);
		}
	}

	private void evaluateScript(String script) {
		StateEvaluator.logger.trace(script);
		StateEvaluator.logger.debug("StateEvaluator starts evaluation: "
				+ script);
		EvalObject[] evalObjects = parseScript(script);
		evaluateObjects(evalObjects);
	}

	private EvalObject[] parseScript(String script) {
		StateEvaluator.logger.trace(script);
		StateEvaluator.logger.debug("StateEvaluator parses: " + script);
		final SimpleConsoleParser cbwParser = new SimpleConsoleParser();
		final EvalObject[] evalObjects = cbwParser.parse(script);
		StateEvaluator.logger.trace(Arrays.toString(evalObjects));
		return evalObjects;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.evaluator.IWorksheetEvaluator#getOutputs()
	 */
	@Override
	public DefaultBlock[] getOutputs() {
		outputBlocks = outListener.outputBlocks;
		DefaultBlock[] ret = outputBlocks.toArray(new DefaultBlock[outputBlocks
				.size()]);
		StateEvaluator.logger.trace(Arrays.toString(ret));
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.evaluator.IWorksheetEvaluator#getContextHistory()
	 */
	@Override
	public ContextHistory getContextHistory() {
		StateEvaluator.logger.trace("{}", contextHistory);
		return contextHistory;
	}

}
