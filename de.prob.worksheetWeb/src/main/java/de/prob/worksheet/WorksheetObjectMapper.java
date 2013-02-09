package de.prob.worksheet;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;

import de.prob.worksheet.block.JavascriptBlock;
import de.prob.worksheet.editor.HTMLEditor;
import de.prob.worksheet.editor.JavascriptEditor;
import de.prob.worksheet.evaluator.classicalB.ClassicalBEvaluator;

public class WorksheetObjectMapper extends ObjectMapper {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6357362407554569616L;
	ArrayList<NamedType> editors = new ArrayList<NamedType>();
	ArrayList<NamedType> evaluaters = new ArrayList<NamedType>();
	ArrayList<NamedType> blocks = new ArrayList<NamedType>();

	public WorksheetObjectMapper() {
		super();
		this.addEditorResolvers();
		this.addEvaluaterResolvers();
		this.addBlockResolvers();
	}

	private void addEditorResolvers() {
		this.addEditorResolver(new NamedType(JavascriptEditor.class,
				"Javascript"));
		this.addEditorResolver(new NamedType(HTMLEditor.class, "HTMLOutput"));
	}

	private void addEvaluaterResolvers() {
		this.addEvaluaterResolver(new NamedType(ClassicalBEvaluator.class,
				"ClassicalB"));
	}

	private void addBlockResolvers() {
		this.addBlockResolver(new NamedType(JavascriptBlock.class,
				"JavascriptBlock"));
	}

	private void addEditorResolver(final NamedType type) {
		this.editors.add(type);
		this.registerSubtypes(type);
	}

	private void addEvaluaterResolver(final NamedType type) {
		this.evaluaters.add(type);
		this.registerSubtypes(type);
	}

	private void addBlockResolver(final NamedType type) {
		this.blocks.add(type);
		this.registerSubtypes(type);
	}

	public String[] getEditorNames() {
		final String[] returnValue = new String[this.editors.size()];
		int x = 0;
		for (final NamedType editor : this.editors) {
			returnValue[x] = editor.getName();
			x++;
		}
		return returnValue;
	}

	public String[] getEvaluatersNames() {
		final String[] returnValue = new String[this.evaluaters.size()];
		int x = 0;
		for (final NamedType evaluater : this.evaluaters) {
			returnValue[x] = evaluater.getName();
			x++;
		}
		return returnValue;
	}

	public String[] getBlockNames() {
		final String[] returnValue = new String[this.blocks.size()];
		int x = 0;
		for (final NamedType block : this.blocks) {
			returnValue[x] = block.getName();
			x++;
		}
		return returnValue;
	}
}
