package de.prob.worksheet.evaluator;

import de.prob.worksheet.api.ContextHistory;
import de.prob.worksheet.api.IContext;
import de.prob.worksheet.block.impl.DefaultBlock;

public interface IEvaluator {
	public abstract void evaluate(String code);

	public abstract void setInitialContext(IContext iContext);

	// TODO decouple the evaluator completely from worksheet e.g. not
	// outputBlocks just outputArray of any type
	public abstract DefaultBlock[] getOutputs();

	public abstract ContextHistory getContextHistory();
}
