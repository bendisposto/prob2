package de.prob.worksheet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import de.prob.worksheet.block.impl.AnalyzeAstBlock;
import de.prob.worksheet.block.impl.DefaultBlock;
import de.prob.worksheet.block.impl.DocumentationBlock;
import de.prob.worksheet.block.impl.EventBBlock;
import de.prob.worksheet.block.impl.HTMLBlock;
import de.prob.worksheet.block.impl.HTMLErrorBlock;
import de.prob.worksheet.block.impl.InitializeStoreBlock;
import de.prob.worksheet.block.impl.StoreValuesBlock;
import de.prob.worksheet.block.impl.TreeBlock;
import de.prob.worksheet.evaluator.IEvaluator;
import de.prob.worksheet.evaluator.evalStore.StateEvaluator;

/**
 * This module configures the bindings for the Block and Evaluator Classes
 * 
 * @author Rene
 * 
 */
public class WorksheetModule extends AbstractModule {
	public static final Logger logger = LoggerFactory
			.getLogger(WorksheetModule.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		WorksheetModule.logger.trace("in: ");
		this.bind(IEvaluator.class).annotatedWith(Names.named("state"))
				.to(StateEvaluator.class);

		// this.bind(DefaultBlock.class).annotatedWith(Names.named("Javascript"))
		// .to(JavascriptBlock.class);

		this.bind(DefaultBlock.class).annotatedWith(Names.named("Event-B"))
				.to(EventBBlock.class);
		this.bind(DefaultBlock.class).annotatedWith(Names.named("HTML"))
				.to(HTMLBlock.class);
		this.bind(DefaultBlock.class).annotatedWith(Names.named("Fehler"))
				.to(HTMLErrorBlock.class);
		this.bind(DefaultBlock.class).annotatedWith(Names.named("Tree"))
				.to(TreeBlock.class);
		this.bind(DefaultBlock.class)
				.annotatedWith(Names.named("Analyze Expression"))
				.to(AnalyzeAstBlock.class);

		this.bind(DefaultBlock.class)
				.annotatedWith(Names.named("Get state from animation"))
				.to(InitializeStoreBlock.class);
		this.bind(DefaultBlock.class)
				.annotatedWith(Names.named("Show state properties"))
				.to(StoreValuesBlock.class);
		this.bind(DefaultBlock.class)
				.annotatedWith(Names.named("Documentation"))
				.to(DocumentationBlock.class);
		WorksheetModule.logger.trace("return:");
	}
}
