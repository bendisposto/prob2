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
import de.prob.worksheet.block.IBlock;
import de.prob.worksheet.evaluator.IWorksheetEvaluator;
import de.prob.worksheet.parser.SimpleConsoleParser;
import de.prob.worksheet.parser.SimpleConsoleParser.EvalObject;

public class StateEvaluator implements IWorksheetEvaluator {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	public EvalStoreAPI api = null;
	OutputListener outListener;
	ErrorListener errorListener;
	HistoryListener actionListener;

	ArrayList<IBlock> outputBlocks = new ArrayList<IBlock>();
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
	 * de.prob.worksheet.evaluator.IWorksheetEvaluator#setImport(de.prob.worksheet
	 * .api.state.StateAPI)
	 */
	@Override
	public void setImport(EvalStoreAPI api) {
		logger.trace(api.toString());
		this.api = api;
		this.api.addErrorListener(errorListener);
		this.api.addOutputListener(outListener);
		this.api.addActionListener(actionListener);
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
		logger.trace("{}", context);
		this.contextHistory = new ContextHistory(context);
		actionListener = new HistoryListener(contextHistory);
		this.api.addActionListener(actionListener);

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
		logger.trace(script);
		if (api == null) {
			logger.error("Evaluator isn't correctly initialized. Api is missing.");
			return;
		}
		if (contextHistory.size() == 0) {
			logger.error("Evaluator isn't correctly initialized. initialContext is missing.");
			return;
		}
		this.evaluateScript(script);
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.evaluator.IWorksheetEvaluator#evaluateObject(de.prob
	 * .worksheet.parser.SimpleConsoleParser.EvalObject)
	 */
	@Override
	public void evaluateObject(EvalObject evalObject) {
		logger.trace(evalObject.toString());
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

		logger.debug("{}", this.contextHistory.last());
		this.api.setEvalStoreId((Long) this.contextHistory.last().getBinding(
				"EvalStoreId"));

		if (evalObject.methodInstance instanceof Method) {
			if (((Method) evalObject.methodInstance).getDeclaringClass()
					.isInstance(this.api)) {
				final Object[] args = Arrays.copyOfRange(evalObject.method, 1,
						evalObject.method.length);
				try {
					((Method) evalObject.methodInstance).invoke(this.api, args);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.evaluator.IWorksheetEvaluator#evaluateObjects(de.prob
	 * .worksheet.parser.SimpleConsoleParser.EvalObject[])
	 */
	@Override
	public void evaluateObjects(EvalObject[] evalObjects) {
		logger.trace(Arrays.toString(evalObjects));
		for (EvalObject object : evalObjects) {
			if (errorListener.isHaltAll())
				break;
			evaluateObject(object);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.evaluator.IWorksheetEvaluator#evaluateScript(java.lang
	 * .String)
	 */
	@Override
	public void evaluateScript(String script) {
		logger.trace(script);
		EvalObject[] evalObjects = this.parseScript(script);
		this.evaluateObjects(evalObjects);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.evaluator.IWorksheetEvaluator#parseScript(java.lang
	 * .String)
	 */
	@Override
	public EvalObject[] parseScript(String script) {
		logger.trace(script);
		final SimpleConsoleParser cbwParser = new SimpleConsoleParser();
		final EvalObject[] evalObjects = cbwParser.parse(script);
		logger.trace(Arrays.toString(evalObjects));
		return evalObjects;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.evaluator.IWorksheetEvaluator#getOutputs()
	 */
	@Override
	public IBlock[] getOutputs() {
		this.outputBlocks = this.outListener.outputBlocks;
		IBlock[] ret = this.outputBlocks.toArray(new IBlock[this.outputBlocks
				.size()]);
		logger.trace(Arrays.toString(ret));
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.evaluator.IWorksheetEvaluator#getContextHistory()
	 */
	@Override
	public ContextHistory getContextHistory() {
		logger.trace("{}", this.contextHistory);
		return this.contextHistory;
	}

}
