package de.prob.worksheet.evaluator;

import de.prob.worksheet.api.ContextHistory;
import de.prob.worksheet.api.IContext;
import de.prob.worksheet.api.evalStore.EvalStoreAPI;
import de.prob.worksheet.block.IBlock;
import de.prob.worksheet.parser.SimpleConsoleParser.EvalObject;

public interface IWorksheetEvaluator {
	public abstract void evaluate(String code);

	public abstract void setImport(EvalStoreAPI api);

	public abstract void setInitialContext(IContext iContext);

	public abstract void evaluateObject(EvalObject evalObject);

	public abstract void evaluateObjects(EvalObject[] evalObjects);

	public abstract void evaluateScript(String script);

	public abstract EvalObject[] parseScript(String script);

	// TODO decouple the evaluator completely from worksheet e.g. not
	// outputBlocks just outputArray of any type
	public abstract IBlock[] getOutputs();

	public abstract ContextHistory getContextHistory();
}
