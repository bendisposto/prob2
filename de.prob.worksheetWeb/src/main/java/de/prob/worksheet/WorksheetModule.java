package de.prob.worksheet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import de.prob.worksheet.block.HTMLBlock;
import de.prob.worksheet.block.HTMLErrorBlock;
import de.prob.worksheet.block.IBlock;
import de.prob.worksheet.block.InitializeStoreBlock;
import de.prob.worksheet.block.JavascriptBlock;
import de.prob.worksheet.block.StoreValuesBlock;
import de.prob.worksheet.evaluator.IWorksheetEvaluator;
import de.prob.worksheet.evaluator.evalStore.StateEvaluator;

/**
 * This module configures the bindings for the Block and Evaluator Classes
 * 
 * @author Rene
 * 
 */
public class WorksheetModule extends AbstractModule {
	Logger logger = LoggerFactory.getLogger(WorksheetModule.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		logger.trace("");
		this.bind(IWorksheetEvaluator.class)
				.annotatedWith(Names.named("state")).to(StateEvaluator.class);

		this.bind(IBlock.class).annotatedWith(Names.named("Javascript"))
				.to(JavascriptBlock.class);
		this.bind(IBlock.class).annotatedWith(Names.named("HTML"))
				.to(HTMLBlock.class);
		this.bind(IBlock.class).annotatedWith(Names.named("Fehler"))
				.to(HTMLErrorBlock.class);
		this.bind(IBlock.class).annotatedWith(Names.named("Initialize State"))
				.to(InitializeStoreBlock.class);
		this.bind(IBlock.class).annotatedWith(Names.named("State Values"))
				.to(StoreValuesBlock.class);
	}
}
