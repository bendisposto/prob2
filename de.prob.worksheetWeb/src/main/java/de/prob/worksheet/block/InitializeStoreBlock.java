package de.prob.worksheet.block;

import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.inject.Inject;

import de.prob.worksheet.IWorksheetMenuNode;
import de.prob.worksheet.ServletContextListener;
import de.prob.worksheet.WorksheetMenuNode;
import de.prob.worksheet.WorksheetObjectMapper;
import de.prob.worksheet.editor.HTMLEditor;
import de.prob.worksheet.editor.IWorksheetEditor;

@XmlType(name = "Initialize")
public class InitializeStoreBlock extends DefaultBlock {
	@Inject
	public InitializeStoreBlock() {
		this.setEvaluatorType("state");
		this.setOutput(false);
		this.setEditor(new HTMLEditor());
		this.setImmediateEvaluation(true);
		this.setInputAndOutput(true);
		this.initBlockMenu("Initialize State", new String[] { "Standard" });
	}

	@Override
	@JsonIgnore
	public void setEditor(IWorksheetEditor editor) {
		// TODO Auto-generated method stub
		super.setEditor(editor);
	}

	private void initBlockMenu(String type, String[] excludes) {
		final ArrayList<IWorksheetMenuNode> menu = new ArrayList<IWorksheetMenuNode>();
		String[] blockTypes = this.getInputBlockTypes();
		final WorksheetMenuNode typeMenu = new WorksheetMenuNode(type, "", "");

		Arrays.sort(excludes);
		for (final String typeName : blockTypes) {
			if (Arrays.binarySearch(excludes, typeName) >= 0
					|| typeName.equals(type))
				continue;
			final WorksheetMenuNode node = new WorksheetMenuNode(typeName, "",
					"");
			node.setClick("function(){$(this).closest('.ui-block').block('switchBlock','"
					+ typeName + "');}");
			typeMenu.addChild(node);
		}
		if (typeMenu.getChildren().length != 0) {
			menu.add(typeMenu);
			this.setMenu(menu.toArray(new IWorksheetMenuNode[menu.size()]));
			this.setHasMenu(true);
		} else {
			this.setHasMenu(false);
		}
	}

	private String[] getInputBlockTypes() {
		WorksheetObjectMapper mapper = ServletContextListener.INJECTOR
				.getInstance(WorksheetObjectMapper.class);
		return mapper.getInputBlockNames();
	}

	@Override
	public boolean equals(final Object obj) {
		// TODO Do we need an overriden default equals method ????
		return super.equals(obj);
	}

	@Override
	public String getOverrideEditorContent() {
		return "getCurrentState";
	}
}
