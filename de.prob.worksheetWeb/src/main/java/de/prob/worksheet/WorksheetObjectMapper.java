package de.prob.worksheet;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.worksheet.block.DefaultBlock;
import de.prob.worksheet.block.HTMLBlock;
import de.prob.worksheet.block.HTMLErrorBlock;
import de.prob.worksheet.block.IBlock;
import de.prob.worksheet.block.InitializeStoreBlock;
import de.prob.worksheet.block.JavascriptBlock;
import de.prob.worksheet.block.StoreValuesBlock;
import de.prob.worksheet.editor.HTMLEditor;
import de.prob.worksheet.editor.HTMLErrorEditor;
import de.prob.worksheet.editor.JavascriptEditor;

@Singleton
public class WorksheetObjectMapper extends ObjectMapper {
	static Logger logger = LoggerFactory.getLogger(WorksheetObjectMapper.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 6357362407554569616L;
	ArrayList<NamedType> editors = new ArrayList<NamedType>();
	ArrayList<NamedType> inputBlocks = new ArrayList<NamedType>();
	ArrayList<NamedType> outputBlocks = new ArrayList<NamedType>();

	@Inject
	public WorksheetObjectMapper() {
		super();
		this.initEditorResolvers();
		this.initBlockResolvers();
	}

	private void initEditorResolvers() {

		this.addEditorResolver(new NamedType(JavascriptEditor.class,
				"javascript"));
		this.addEditorResolver(new NamedType(HTMLEditor.class, "HTMLEditor"));
		this.addEditorResolver(new NamedType(HTMLErrorEditor.class, "errorHtml"));
	}

	private void initBlockResolvers() {
		this.addInputBlockResolver(new NamedType(JavascriptBlock.class,
				"Javascript"));
		this.addOutputBlockResolver(new NamedType(HTMLBlock.class, "HTML"));
		this.addOutputBlockResolver(new NamedType(HTMLErrorBlock.class,
				"Fehler"));
		this.addInputBlockResolver(new NamedType(DefaultBlock.class, "Standard"));
		this.addInputBlockResolver(new NamedType(InitializeStoreBlock.class,
				"Initialize State"));
		this.addInputBlockResolver(new NamedType(StoreValuesBlock.class,
				"State Values"));
	}

	private void addEditorResolver(final NamedType type) {
		this.editors.add(type);
		this.registerSubtypes(type);
	}

	private void addInputBlockResolver(final NamedType type) {
		this.inputBlocks.add(type);
		this.registerSubtypes(type);
	}

	private void addOutputBlockResolver(final NamedType type) {
		this.outputBlocks.add(type);
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

	public String[] getInputBlockNames() {
		final String[] returnValue = new String[this.inputBlocks.size()];
		int x = 0;
		for (final NamedType block : this.inputBlocks) {
			returnValue[x] = block.getName();
			x++;
		}
		return returnValue;
	}

	public IBlock getInputBlockTypeInstance(String name) {
		int x = 0;
		for (final NamedType block : this.inputBlocks) {
			if (block.getName().equals(name))
				try {
					return (IBlock) block.getType().newInstance();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return null;
	}

	public String[] getOutputBlockNames() {
		final String[] returnValue = new String[this.outputBlocks.size()];
		int x = 0;
		for (final NamedType block : this.outputBlocks) {
			returnValue[x] = block.getName();
			x++;
		}
		return returnValue;
	}

	public IBlock getOutputBlockTypeInstance(String name) {
		logger.trace("{}", name);
		for (final NamedType block : this.outputBlocks) {

			logger.debug("{}", block.getName());
			try {
				if (block.getName().equals(name))
					return (IBlock) block.getType().newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
}
