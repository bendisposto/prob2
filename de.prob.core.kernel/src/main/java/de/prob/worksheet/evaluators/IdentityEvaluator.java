package de.prob.worksheet.evaluators;

import de.prob.worksheet.IEvaluator;
import de.prob.worksheet.WorkSheet;

public class IdentityEvaluator implements IEvaluator {

	@Override
	public Object evaluate(WorkSheet context, String content) {
		return content;
	}

}
