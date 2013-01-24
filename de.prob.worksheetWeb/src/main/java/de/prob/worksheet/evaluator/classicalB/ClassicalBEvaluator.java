package de.prob.worksheet.evaluator.classicalB;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import de.prob.worksheet.ServletContextListener;
import de.prob.worksheet.api.classicalB.OutputWorksheetApiListener;
import de.prob.worksheet.api.classicalB.WorksheetAPI;
import de.prob.worksheet.block.IBlock;
import de.prob.worksheet.evaluator.IWorksheetEvaluator;
import de.prob.worksheet.parser.SimpleConsoleParser;
import de.prob.worksheet.parser.SimpleConsoleParser.evalObject;

public class ClassicalBEvaluator implements IWorksheetEvaluator {

	public WorksheetAPI	api;

	public ClassicalBEvaluator() {
		this.api = ServletContextListener.INJECTOR.getInstance(WorksheetAPI.class);
	}

	@Override
	public IBlock[] evaluate(final String code) {
		final ArrayList<IBlock> retVal = new ArrayList<IBlock>();
		this.api.addApiListener(new OutputWorksheetApiListener(retVal));

		final SimpleConsoleParser cbwParser = new SimpleConsoleParser();
		final evalObject[] evalObjects = cbwParser.parse(code);

		for (final evalObject evalObject : evalObjects) {
			if (evalObject.methodInstance instanceof Method) {
				if (((Method) evalObject.methodInstance).getDeclaringClass().isInstance(this.api)) {
					final Object[] args = Arrays.copyOfRange(evalObject.method, 1, evalObject.method.length);
					try {
						((Method) evalObject.methodInstance).invoke(this.api, args);
					} catch (final IllegalAccessException e) {
						// TODO catch block (add Error Output for worksheet)
						e.printStackTrace();
					} catch (final IllegalArgumentException e) {
						// TODO catch block (add Error Output for worksheet)
						e.printStackTrace();
					} catch (final InvocationTargetException e) {
						// TODO catch block (add Error Output for worksheet)
						e.printStackTrace();
					}
				}
			}
		}

		return retVal.toArray(new IBlock[retVal.size()]);
	}

}
