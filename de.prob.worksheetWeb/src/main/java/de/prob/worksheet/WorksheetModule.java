package de.prob.worksheet;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import de.prob.worksheet.block.HTMLBlock;
import de.prob.worksheet.block.HTMLErrorBlock;
import de.prob.worksheet.block.IBlock;
import de.prob.worksheet.block.JavascriptBlock;
import de.prob.worksheet.evaluator.IWorksheetEvaluator;
import de.prob.worksheet.evaluator.evalStore.StateEvaluator;

public class WorksheetModule extends AbstractModule {

	@Override
	protected void configure() {
		// TODO Auto-generated method stub
		// this.bind(IWorksheetEvaluator.class).annotatedWith(Names.named("classicalB")).to(ClassicalBEvaluator.class);
		this.bind(IWorksheetEvaluator.class)
				.annotatedWith(Names.named("state")).to(StateEvaluator.class);

		this.bind(IBlock.class).annotatedWith(Names.named("javascript"))
				.to(JavascriptBlock.class);
		this.bind(IBlock.class).annotatedWith(Names.named("html"))
				.to(HTMLBlock.class);
		this.bind(IBlock.class).annotatedWith(Names.named("errorHtml"))
				.to(HTMLErrorBlock.class);

	}
}
