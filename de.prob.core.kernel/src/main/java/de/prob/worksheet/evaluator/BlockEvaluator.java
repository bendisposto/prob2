package de.prob.worksheet.evaluator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Key;
import com.google.inject.name.Names;

import de.prob.webconsole.ServletContextListener;
import de.prob.worksheet.api.ContextHistory;
import de.prob.worksheet.api.IContext;
import de.prob.worksheet.block.impl.DefaultBlock;
import de.prob.worksheet.document.impl.WorksheetDocument;

public class BlockEvaluator {
	Logger logger = LoggerFactory.getLogger(BlockEvaluator.class);

	public void evaluate(WorksheetDocument doc, final DefaultBlock block) {
		logger.trace("{}", doc);
		logger.trace("{}", block);

		if (block.isOutput())
			return;

		IEvaluator evaluator = initializeEvaluator(block.getEvaluatorType(),
				doc.history.getInitialContextForId(block.getId()));

		String script = block.getOverrideEditorContent();
		if (script == null) {
			script = block.getEditor().getEditorContent();
		}

		evaluateScript(evaluator, script);

		doc.removeOutputBlocks(block);
		DefaultBlock[] outputs = evaluator.getOutputs();

		if (block.isInputAndOutput()) {
			block.setEditor(outputs[0].getEditor());
			// block.getEditor().setEditorContent(
			// outputs[0].getEditor().getEditorContent());
			block.setToUnicode(outputs[0].isToUnicode());
			outputs[0] = block;
		}
		block.setMark(false);
		doc.insertOutputBlocks(block, outputs);
		// TODO Refactor in order to get Blocks at the correct position
		ContextHistory blockHistory = evaluator.getContextHistory();
		logger.debug("id={}, history={}", block.getId(), blockHistory);
		doc.setContexts(block.getId(), blockHistory);
		logger.debug("Worksheet History= {}", doc.history);
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
