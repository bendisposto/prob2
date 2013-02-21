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
import de.prob.worksheet.block.InitializeStoreBlock;
import de.prob.worksheet.block.JavascriptBlock;
import de.prob.worksheet.block.StoreValuesBlock;
import de.prob.worksheet.editor.HTMLEditor;
import de.prob.worksheet.editor.HTMLErrorEditor;
import de.prob.worksheet.editor.JavascriptEditor;

/**
 * The WorksheetObjectMapper extends the Jackson Object Mapper. It binds the
 * Editors and Blocks for the Worksheet to Jackson and holds lists with them for
 * later retrieval of the name Classes
 * 
 * @author Rene
 * 
 */
@Singleton
public class WorksheetObjectMapper extends ObjectMapper {
	public static final Logger logger = LoggerFactory
			.getLogger(WorksheetObjectMapper.class);
	/**
	 * The serialVersionUID of this class;
	 */
	private static final long serialVersionUID = 6357362407554569616L;
	/**
	 * A list containing the named IEditor s bound to Jackson
	 */
	ArrayList<NamedType> editors = new ArrayList<NamedType>();
	/**
	 * A list containing the named Input IBlock s bound to Jackson
	 */
	ArrayList<NamedType> inputBlocks = new ArrayList<NamedType>();
	/**
	 * A list containing the named Output IBlock s bound to Jackson
	 */
	ArrayList<NamedType> outputBlocks = new ArrayList<NamedType>();

	/**
	 * The constructor of the WorksheetObjectMapper
	 */
	@Inject
	public WorksheetObjectMapper() {
		super();
		logger.trace("in:");
		this.initEditorResolvers();
		this.initBlockResolvers();
		logger.trace("return:");
	}

	/**
	 * Adds named Editors to this WorksheetObjectMapper. If you add new Editors
	 * to the Worksheet Project they must be configured here
	 */
	private void initEditorResolvers() {
		logger.trace("in:");
		this.addEditorResolver(new NamedType(JavascriptEditor.class,
				"javascript"));
		this.addEditorResolver(new NamedType(HTMLEditor.class, "HTMLEditor"));
		this.addEditorResolver(new NamedType(HTMLErrorEditor.class, "errorHtml"));
		logger.trace("return:");
	}

	/**
	 * Adds named Blocks to this WorksheetObjectMapper. If you add new Blocks to
	 * the Worksheet Project they must be configured here
	 */
	private void initBlockResolvers() {
		logger.trace("in:");
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
		logger.trace("return:");
	}

	/**
	 * Adds an Named Editor to the editor list and registers it as a subtype
	 * 
	 * @param type
	 *            to add
	 */
	private void addEditorResolver(final NamedType type) {
		logger.trace("in: type={}", type);
		this.editors.add(type);
		this.registerSubtypes(type);
		logger.trace("return:");
	}

	/**
	 * Adds an Named Input Block to the input block list and registers it as a
	 * subtype
	 * 
	 * @param type
	 *            to add
	 */
	private void addInputBlockResolver(final NamedType type) {
		logger.trace("in: type={}", type);
		this.inputBlocks.add(type);
		this.registerSubtypes(type);
		logger.trace("return:");
	}

	/**
	 * Adds an Named Output Block to the Output block list and registers it as a
	 * subtype
	 * 
	 * @param type
	 *            to add
	 */
	private void addOutputBlockResolver(final NamedType type) {
		logger.trace("in: type={}", type);
		this.outputBlocks.add(type);
		this.registerSubtypes(type);
		logger.trace("return:");
	}

	/**
	 * Returns an array containing the names of all registered editors
	 * 
	 * @return an array containing editor names
	 */
	public String[] getEditorNames() {
		logger.trace("in:");
		final String[] returnValue = new String[this.editors.size()];
		int x = 0;
		for (final NamedType editor : this.editors) {
			returnValue[x] = editor.getName();
			x++;
		}
		logger.trace("return: names={}", returnValue);
		return returnValue;
	}

	/**
	 * Returns an array containing the names of all registered Input Blocks
	 * 
	 * @return an array containing block names
	 */
	public String[] getInputBlockNames() {
		logger.trace("in:");
		final String[] returnValue = new String[this.inputBlocks.size()];
		int x = 0;
		for (final NamedType block : this.inputBlocks) {
			returnValue[x] = block.getName();
			x++;
		}
		logger.trace("return: names={}", returnValue);
		return returnValue;
	}

	/**
	 * Returns an array containing the names of all registered output blocks
	 * 
	 * @return an array containing block names
	 */
	public String[] getOutputBlockNames() {
		logger.trace("in:");
		final String[] returnValue = new String[this.outputBlocks.size()];
		int x = 0;
		for (final NamedType block : this.outputBlocks) {
			returnValue[x] = block.getName();
			x++;
		}
		logger.trace("return: names={}", returnValue);
		return returnValue;
	}

}
