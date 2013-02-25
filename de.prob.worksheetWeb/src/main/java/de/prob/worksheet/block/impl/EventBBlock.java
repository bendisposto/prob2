package de.prob.worksheet.block.impl;

import javax.xml.bind.annotation.XmlType;

import com.google.inject.Inject;

import de.prob.worksheet.editor.impl.CodeMirrorTextEditor;

@XmlType(name = "EventBBlock")
public class EventBBlock extends DefaultBlock {

	@Inject
	public EventBBlock() {
		setEvaluatorType("state");
		initBlockMenu("Event-B", new String[] { "Standard" });
		setToUnicode(true);
		setEditor(new CodeMirrorTextEditor());
	}

	@Override
	public boolean equals(final Object obj) {
		// TODO Do we need an overriden default equals method ????
		return super.equals(obj);
	}

}
