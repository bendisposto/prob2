package de.prob.worksheet.evaluator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Key;
import com.google.inject.name.Names;

import de.prob.worksheet.ServletContextListener;
import de.prob.worksheet.api.ContextHistory;
import de.prob.worksheet.api.IContext;
import de.prob.worksheet.block.impl.DefaultBlock;
import de.prob.worksheet.document.IWorksheetEvaluate;

public class BlockEvaluator {
	Logger logger = LoggerFactory.getLogger(BlockEvaluator.class);

	public void evaluate(IWorksheetEvaluate doc, final DefaultBlock block,
			ContextHistory contextHistory) {
		logger.trace("{}", doc);
		logger.trace("{}", block);
		logger.trace("{}", contextHistory);

		if (block.isOutput())
			return;

		IEvaluator evaluator = initializeEvaluator(block.getEvaluatorType(),
				contextHistory.getInitialContextForId(block.getId()));

		String script = block.getOverrideEditorContent();
		if (script == null) {
			script = block.getEditor().getEditorContent();
		}

		evaluateScript(evaluator, script);

		doc.removeOutputBlocks(block);
		DefaultBlock[] outputs = evaluator.getOutputs();

		if (block.isInputAndOutput()) {
			block.getEditor().setEditorContent(
					outputs[0].getEditor().getEditorContent());
			block.setToUnicode(outputs[0].isToUnicode());
			outputs[0] = block;
		}
		block.setMark(false);
		doc.insertOutputBlocks(block, outputs);

		// TODO history isn't stored here
		ContextHistory blockHistory = evaluator.getContextHistory();
		logger.debug("id={}, history={}", block.getId(), blockHistory);
		contextHistory.setContextsForId(block.getId(), blockHistory);
		logger.debug("Worksheet History= {}", contextHistory);
		return;
	}

	private IEvaluator initializeEvaluator(String evaluatorType,
			IContext initialContext) {
		logger.trace("evaluatorType={}, initialContext={}", evaluatorType,
				initialContext);
		IEvaluator evaluator = ServletContextListener.INJECTOR.getInstance(Key
				.get(IEvaluator.class, Names.named(evaluatorType)));
		evaluator.setInitialContext(initialContext);
		logger.trace("{}", evaluator);
		return evaluator;
	}

	private void evaluateScript(IEvaluator evaluator, String script) {
		evaluator.evaluate(script);
	}

}
