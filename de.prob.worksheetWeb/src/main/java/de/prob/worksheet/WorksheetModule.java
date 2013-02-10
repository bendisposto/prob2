package de.prob.worksheet;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import de.prob.worksheet.block.HTMLBlock;
import de.prob.worksheet.block.HTMLErrorBlock;
import de.prob.worksheet.block.IBlock;
import de.prob.worksheet.block.JavascriptBlock;
import de.prob.worksheet.evaluator.IWorksheetEvaluator;
import de.prob.worksheet.evaluator.evalStore.StateEvaluator;

/**
 * This module configures the bindings for the Block and Evaluator Classes
 * @author Rene
 *
 */
public class WorksheetModule extends AbstractModule {

	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
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
