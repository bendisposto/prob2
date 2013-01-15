package de.prob.worksheet.evaluator.state;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import de.prob.worksheet.ServletContextListener;
import de.prob.worksheet.api.IWorksheetAPIListener;
import de.prob.worksheet.api.IWorksheetEvent;
import de.prob.worksheet.api.WorksheetErrorEvent;
import de.prob.worksheet.api.state.StateAPI;
import de.prob.worksheet.block.HTMLErrorBlock;
import de.prob.worksheet.block.IBlock;
import de.prob.worksheet.evaluator.IWorksheetEvaluator;
import de.prob.worksheet.parser.SimpleConsoleParser;
import de.prob.worksheet.parser.SimpleConsoleParser.evalObject;

public class StateEvaluator implements IWorksheetEvaluator, IWorksheetAPIListener {

	public StateAPI	api;

	public StateEvaluator() {
		this.api = ServletContextListener.INJECTOR.getInstance(StateAPI.class);
	}

	@Override
	public IBlock[] evaluate(final String code) {
		ArrayList<IBlock> outputBlocks=new ArrayList<IBlock>();
		final OutputListener outListener = new OutputListener(outputBlocks);
		final ErrorListener errorListener = new ErrorListener(outputBlocks);
		this.api.addErrorListener(errorListener);
		this.api.addOutputListener(outListener);

		final SimpleConsoleParser cbwParser = new SimpleConsoleParser();
		final evalObject[] evalObjects = cbwParser.parse(code);

		IBlock last=null;
		for (final evalObject evalObject : evalObjects) {
			if(outputBlocks.size()>0)
				last=outputBlocks.get(outputBlocks.size()-1);
			if(last!=null && last instanceof HTMLErrorBlock && ((HTMLErrorBlock)last).getHaltAll())
				break;
			String errorString="";
			if(evalObject.methodInstance==null){
				errorString="Method unknown: "+evalObject.method[0];
				if(evalObject.method.length>1)
					errorString+=" with args ["+((Method) evalObject.methodInstance).getParameterTypes()+"]";
				errorListener.notify(new WorksheetErrorEvent(3001,errorString, true));
			}
			if (evalObject.methodInstance instanceof Method) {
				if (((Method) evalObject.methodInstance).getDeclaringClass().isInstance(this.api)) {
					final Object[] args = Arrays.copyOfRange(evalObject.method, 1, evalObject.method.length);
					try {
						((Method) evalObject.methodInstance).invoke(this.api, args);
					} catch (final IllegalAccessException e) {
						errorListener.notify(new WorksheetErrorEvent(3001,"You don't have access to the method "+evalObject.method[0]+" with args ["+((Method) evalObject.methodInstance).getParameterTypes()+"]", true));
					} catch (final IllegalArgumentException e) {
						errorListener.notify(new WorksheetErrorEvent(3001,"Illegal arguments for method "+evalObject.method[0]+" with args ["+((Method) evalObject.methodInstance).getParameterTypes()+"]", true));
					} catch (final InvocationTargetException e) {
						errorListener.notify(new WorksheetErrorEvent(3001,"Invocation arguments for method "+evalObject.method[0]+" with args ["+((Method) evalObject.methodInstance).getParameterTypes()+"]", true));
						e.printStackTrace();
					}
				}
			}
		}
		return outputBlocks.toArray(new IBlock[outputBlocks.size()]);
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.api.IWorksheetAPIListener#notify(de.prob.worksheet.api.IWorksheetEvent)
	 */
	@Override
	public void notify(final IWorksheetEvent event) {
		
	}

}
