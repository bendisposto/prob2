package de.prob.worksheet.evaluator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Key;
import com.google.inject.name.Names;

import de.prob.worksheet.ContextHistory;
import de.prob.worksheet.IContext;
import de.prob.worksheet.ServletContextListener;
import de.prob.worksheet.WorksheetDocument;
import de.prob.worksheet.block.IBlock;

public class BlockEvaluator {
	Logger logger = LoggerFactory.getLogger(BlockEvaluator.class);

	public void evaluate(WorksheetDocument doc, final IBlock block,
			ContextHistory contextHistory) {
		logger.trace("{}", doc);
		logger.trace("{}", block);
		logger.trace("{}", contextHistory);

		if (block.getOutput())
			return;

		IWorksheetEvaluator evaluator = this.initializeEvaluator(
				block.getEvaluatorType(),
				contextHistory.getInitialContextForId(block.getId()));

		String script = block.getOverrideEditorContent();
		if (script == null) {
			script = block.getEditor().getEditorContent();
		}

		evaluateScript(evaluator, script);

		doc.removeOutputBlocks(block);
		IBlock[] outputs = evaluator.getOutputs();
		if (block.isInputAndOutput()) {
			block.getEditor().setEditorContent(
					outputs[0].getEditor().getEditorContent());
			outputs[0] = block;
		}
		doc.insertOutputBlocks(block, outputs);

		// TODO history isn't stored here
		ContextHistory blockHistory = evaluator.getContextHistory();
		logger.debug("id={}, history={}", block.getId(), blockHistory);
		contextHistory.setContextsForId(block.getId(), blockHistory);
		logger.debug("Worksheet History= {}", contextHistory);
		return;
	}

	private IWorksheetEvaluator initializeEvaluator(String evaluatorType,
			IContext initialContext) {
		logger.trace("evaluatorType={}, initialContext={}", evaluatorType,
				initialContext);
		IWorksheetEvaluator evaluator = ServletContextListener.INJECTOR
				.getInstance(Key.get(IWorksheetEvaluator.class,
						Names.named(evaluatorType)));
		evaluator.setInitialContext(initialContext);
		logger.trace("{}", evaluator);
		return evaluator;
	}

	private void evaluateScript(IWorksheetEvaluator evaluator, String script) {
		evaluator.evaluate(script);
	}

}
