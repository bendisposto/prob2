package de.prob.worksheet.evaluator.classicalB;


public class ClassicalBEvaluator{/* implements IWorksheetEvaluator {

	public WorksheetAPI	api;

	public ClassicalBEvaluator() {
		this.api = ServletContextListener.INJECTOR.getInstance(WorksheetAPI.class);
	}

	@Override
	public IBlock[] evaluate(final String code) {
		final ArrayList<IBlock> retVal = new ArrayList<IBlock>();
		this.api.addApiListener(new OutputWorksheetApiListener(retVal));

		final SimpleConsoleParser cbwParser = new SimpleConsoleParser();
		final EvalObject[] evalObjects = cbwParser.parse(code);

		for (final EvalObject evalObject : evalObjects) {
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
*/
}
