package de.prob.worksheet;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.worksheet.block.impl.DocumentationBlock;
import de.prob.worksheet.block.impl.HTMLBlock;
import de.prob.worksheet.block.impl.HTMLErrorBlock;
import de.prob.worksheet.block.impl.InitializeStoreBlock;
import de.prob.worksheet.block.impl.JavascriptBlock;
import de.prob.worksheet.block.impl.StoreValuesBlock;
import de.prob.worksheet.editor.impl.CkEditorEditor;
import de.prob.worksheet.editor.impl.HTMLDiv;
import de.prob.worksheet.editor.impl.HTMLDivError;
import de.prob.worksheet.editor.impl.CodeMirrorJSEditor;

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
		WorksheetObjectMapper.logger.trace("in:");
		initEditorResolvers();
		initBlockResolvers();
		WorksheetObjectMapper.logger.trace("return:");
	}

	/**
	 * Adds named Editors to this WorksheetObjectMapper. If you add new Editors
	 * to the Worksheet Project they must be configured here
	 */
	private void initEditorResolvers() {
		WorksheetObjectMapper.logger.trace("in:");
		addEditorResolver(new NamedType(CodeMirrorJSEditor.class, "javascript"));
		addEditorResolver(new NamedType(HTMLDiv.class, "HTMLEditor"));
		addEditorResolver(new NamedType(HTMLDivError.class, "errorHtml"));
		addEditorResolver(new NamedType(CkEditorEditor.class, "Documentation"));
		WorksheetObjectMapper.logger.trace("return:");
	}

	/**
	 * Adds named Blocks to this WorksheetObjectMapper. If you add new Blocks to
	 * the Worksheet Project they must be configured here
	 */
	private void initBlockResolvers() {
		WorksheetObjectMapper.logger.trace("in:");
		addInputBlockResolver(new NamedType(JavascriptBlock.class, "Javascript"));
		addOutputBlockResolver(new NamedType(HTMLBlock.class, "HTML"));
		addOutputBlockResolver(new NamedType(HTMLErrorBlock.class, "Fehler"));
		addInputBlockResolver(new NamedType(InitializeStoreBlock.class,
				"Initialize State"));
		addInputBlockResolver(new NamedType(StoreValuesBlock.class,
				"State Values"));
		addInputBlockResolver(new NamedType(DocumentationBlock.class,
				"Documentation"));
		WorksheetObjectMapper.logger.trace("return:");
	}

	/**
	 * Adds an Named Editor to the editor list and registers it as a subtype
	 * 
	 * @param type
	 *            to add
	 */
	private void addEditorResolver(final NamedType type) {
		WorksheetObjectMapper.logger.trace("in: type={}", type);
		editors.add(type);
		this.registerSubtypes(type);
		WorksheetObjectMapper.logger.trace("return:");
	}

	/**
	 * Adds an Named Input Block to the input block list and registers it as a
	 * subtype
	 * 
	 * @param type
	 *            to add
	 */
	private void addInputBlockResolver(final NamedType type) {
		WorksheetObjectMapper.logger.trace("in: type={}", type);
		inputBlocks.add(type);
		this.registerSubtypes(type);
		WorksheetObjectMapper.logger.trace("return:");
	}

	/**
	 * Adds an Named Output Block to the Output block list and registers it as a
	 * subtype
	 * 
	 * @param type
	 *            to add
	 */
	private void addOutputBlockResolver(final NamedType type) {
		WorksheetObjectMapper.logger.trace("in: type={}", type);
		outputBlocks.add(type);
		this.registerSubtypes(type);
		WorksheetObjectMapper.logger.trace("return:");
	}

	/**
	 * Returns an array containing the names of all registered editors
	 * 
	 * @return an array containing editor names
	 */
	public String[] getEditorNames() {
		WorksheetObjectMapper.logger.trace("in:");
		final String[] returnValue = new String[editors.size()];
		int x = 0;
		for (final NamedType editor : editors) {
			returnValue[x] = editor.getName();
			x++;
		}
		WorksheetObjectMapper.logger.trace("return: names={}", returnValue);
		return returnValue;
	}

	/**
	 * Returns an array containing the names of all registered Input Blocks
	 * 
	 * @return an array containing block names
	 */
	public String[] getInputBlockNames() {
		WorksheetObjectMapper.logger.trace("in:");
		final String[] returnValue = new String[inputBlocks.size()];
		int x = 0;
		for (final NamedType block : inputBlocks) {
			returnValue[x] = block.getName();
			x++;
		}
		WorksheetObjectMapper.logger.trace("return: names={}", returnValue);
		return returnValue;
	}

	/**
	 * Returns an array containing the names of all registered output blocks
	 * 
	 * @return an array containing block names
	 */
	public String[] getOutputBlockNames() {
		WorksheetObjectMapper.logger.trace("in:");
		final String[] returnValue = new String[outputBlocks.size()];
		int x = 0;
		for (final NamedType block : outputBlocks) {
			returnValue[x] = block.getName();
			x++;
		}
		WorksheetObjectMapper.logger.trace("return: names={}", returnValue);
		return returnValue;
	}

}
