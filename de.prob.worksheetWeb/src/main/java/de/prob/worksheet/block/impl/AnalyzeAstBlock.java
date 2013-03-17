package de.prob.worksheet.block.impl;

import javax.xml.bind.annotation.XmlType;

import de.prob.worksheet.editor.impl.CodeMirrorTextEditor;

@XmlType(name = "analyzeExpression")
public class AnalyzeAstBlock extends DefaultBlock {
	public AnalyzeAstBlock() {
		setEditor(new CodeMirrorTextEditor());
		setEvaluatorType("state");
		setImmediateEvaluation(true);
		setOutput(false);
		setToUnicode(true);
		initBlockMenu("Analyze Expression", new String[] {});
		setHasMenu(true);

	}

	@Override
	public String getOverrideEditorContent() {
		return "analyzeAst(\"" + getEditor().getEditorContent() + "\")";
	}

}
