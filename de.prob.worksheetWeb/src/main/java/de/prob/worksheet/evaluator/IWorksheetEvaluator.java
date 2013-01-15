package de.prob.worksheet.evaluator;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import de.prob.worksheet.block.IBlock;
import de.prob.worksheet.evaluator.classicalB.ClassicalBEvaluator;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "@type")
@JsonSubTypes({ @Type(value = ClassicalBEvaluator.class, name = "ClassicalB") })
public interface IWorksheetEvaluator {
	public IBlock[] evaluate(String code);
}
