package de.prob.worksheet.evaluator;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import de.prob.worksheet.ContextHistory;
import de.prob.worksheet.IContext;
import de.prob.worksheet.api.evalStore.EvalStoreAPI;
import de.prob.worksheet.block.IBlock;
import de.prob.worksheet.evaluator.classicalB.ClassicalBEvaluator;
import de.prob.worksheet.parser.SimpleConsoleParser.EvalObject;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "@type")
@JsonSubTypes({ @Type(value = ClassicalBEvaluator.class, name = "ClassicalB") })
public interface IWorksheetEvaluator {
	public abstract void evaluate(String code);

	public abstract void setImport(EvalStoreAPI api);

	public abstract void setInitialContext(IContext iContext);

	public abstract void evaluateObject(EvalObject evalObject);

	public abstract void evaluateObjects(EvalObject[] evalObjects);

	public abstract void evaluateScript(String script);

	public abstract EvalObject[] parseScript(String script);

	public abstract IBlock[] getOutputs();

	public abstract ContextHistory getContextHistory();
}
