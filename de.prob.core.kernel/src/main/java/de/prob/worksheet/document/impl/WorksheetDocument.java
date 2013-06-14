package de.prob.worksheet.document.impl;

import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.prob.worksheet.api.ContextHistory;
import de.prob.worksheet.api.evalStore.EvalStoreContext;
import de.prob.worksheet.block.IBlockData;
import de.prob.worksheet.block.impl.DefaultBlock;
import de.prob.worksheet.document.IWorksheetData;
import de.prob.worksheet.document.IWorksheetEvaluate;
import de.prob.worksheet.document.IWorksheetUI;

/**
 * The WorksheetDocument stores all data including blocks of a worksheet
 * 
 * @author Rene
 * 
 */
@XmlRootElement(name = "worksheet")
public class WorksheetDocument implements IWorksheetData, IWorksheetUI,
		IWorksheetEvaluate {

	/**
	 * The static slf4j Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(WorksheetDocument.class);

	/**
	 * The list containing the blocks of this document
	 */
	private final ArrayList<DefaultBlock> blocks;

	/**
	 * A flag which tells if this document has a menu
	 */
	private boolean hasMenu;

	/**
	 * A flag which tells if this document is initialized without a body
	 */
	private boolean hasBody;

	/**
	 * A list containing the menu nodes for this document
	 */
	private ArrayList<WorksheetMenuNode> menu;

	/**
	 * The id of this document (need's to be set to ws-id-1)
	 */
	private String id;

	@XmlTransient
	@JsonIgnore
	public ContextHistory history;

	/**
	 * A counter used to count how many blocks have been added to this document.
	 * It's used to set unique id's for the blocks
	 */
	private int blockCounter = 0;

	/**
	 * The Constructor of the Document
	 */
	public WorksheetDocument() {
		WorksheetDocument.logger.trace("in:");
		hasMenu = false;
		hasBody = true;
		blocks = new ArrayList<DefaultBlock>();
		menu = new ArrayList<WorksheetMenuNode>();
		history = new ContextHistory(new EvalStoreContext("root", null, null));
		WorksheetDocument.logger.trace("return:");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetData#getBlocks()
	 */
	@Override
	@XmlElements(value = { @XmlElement(name = "block") })
	public DefaultBlock[] getBlocks() {
		WorksheetDocument.logger.trace("in:");
		WorksheetDocument.logger.trace("return: blocks=",
				blocks.toArray(new DefaultBlock[blocks.size()]));
		return blocks.toArray(new DefaultBlock[blocks.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.IWorksheetData#setBlocks(de.prob.worksheet.block.impl
	 * .DefaultBlock[])
	 */
	@Override
	public void setBlocks(final DefaultBlock[] blocks) {
		WorksheetDocument.logger.trace("in: blocks={}", blocks);
		this.blocks.clear();
		this.blocks.addAll(Arrays.asList(blocks));

		// clear history and insert an empty context for any input block
		this.history.reset();
		for (DefaultBlock block : blocks) {
			if (!block.isOutput() && !block.isNeitherInNorOutput()) {
				String id = block.getId();
				history.addEmptyContext(id);
			}
		}
		WorksheetDocument.logger.trace("return:");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetUI#getHasMenu()
	 */
	@Override
	@XmlTransient
	public boolean getHasMenu() {
		WorksheetDocument.logger.trace("in:");
		WorksheetDocument.logger.trace("return: hasMenu={}", hasMenu);
		return hasMenu;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetUI#setHasMenu(boolean)
	 */
	@Override
	public void setHasMenu(final boolean hasMenu) {
		WorksheetDocument.logger.trace("in: hasMenu{}", hasMenu);
		this.hasMenu = hasMenu;
		WorksheetDocument.logger.trace("return:");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetUI#getHasBody()
	 */
	@Override
	@XmlTransient
	public boolean getHasBody() {
		WorksheetDocument.logger.trace("in:");
		WorksheetDocument.logger.trace("return: hasBody={}", hasBody);
		return hasBody;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetUI#setHasBody(boolean)
	 */
	@Override
	public void setHasBody(final boolean hasBody) {
		WorksheetDocument.logger.trace("in: hasBody={}", hasBody);
		this.hasBody = hasBody;
		WorksheetDocument.logger.trace("return:");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetUI#getMenu()
	 */
	@Override
	@XmlTransient
	public ArrayList<WorksheetMenuNode> getMenu() {
		WorksheetDocument.logger.trace("in:");
		WorksheetDocument.logger.trace("return: menu={}", menu);
		return menu;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetUI#setMenu(java.util.ArrayList)
	 */
	@Override
	public void setMenu(final ArrayList<WorksheetMenuNode> menu) {
		WorksheetDocument.logger.trace("in: menu={}", menu);
		this.menu = menu;
		WorksheetDocument.logger.trace("return:");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetData#getId()
	 */
	@Override
	@XmlAttribute(name = "id")
	@XmlID
	public String getId() {
		WorksheetDocument.logger.trace("in:");
		WorksheetDocument.logger.trace("return: id={}", id);
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetData#setId(java.lang.String)
	 */
	@Override
	public void setId(final String id) {
		WorksheetDocument.logger.trace("in: id={}", id);
		this.id = id;
		WorksheetDocument.logger.trace("return:");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetData#getBlockCounter()
	 */
	@Override
	@XmlAttribute(name = "blockCounter")
	public int getBlockCounter() {
		WorksheetDocument.logger.trace("in:");
		WorksheetDocument.logger.trace("return: blockCounter={}", blockCounter);
		return blockCounter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetData#setBlockCounter(int)
	 */
	@Override
	public void setBlockCounter(int blockCounter) {
		WorksheetDocument.logger.trace("in: blockCounter={}", blockCounter);
		this.blockCounter = blockCounter;
		WorksheetDocument.logger.trace("return:");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetData#insertBlock(int,
	 * de.prob.worksheet.block.impl.DefaultBlock)
	 */
	@Override
	public void insertBlock(final int index, final DefaultBlock block) {
		WorksheetDocument.logger.trace("in: index={}, block={}", index, block);
		assert (blockCounter < Integer.MAX_VALUE);
		blocks.add(index, block);
		blockCounter++;
		block.setId("ws-block-id-" + blockCounter);

		if (!block.isOutput() && !block.isNeitherInNorOutput()) {
			DefaultBlock previous = getPreviousInputBlock(index);
			if (previous != null) {
				history.insertEmptyContext(previous.getId(), block.getId());
			} else {
				assert (this.getBlockIndex(block) == 0);
				history.insertEmptyContext("root", block.getId());
			}
		}

		WorksheetDocument.logger.trace("return:");
	}

	private DefaultBlock getPreviousInputBlock(int index) {
		for (int x = index - 1; x >= 0; x--) {
			DefaultBlock block = blocks.get(x);
			if (!block.isOutput() && !block.isNeitherInNorOutput()) {
				return block;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.IWorksheetData#getBlockIndex(de.prob.worksheet.block
	 * .IBlockData)
	 */
	@Override
	public int getBlockIndex(final DefaultBlock block) {
		WorksheetDocument.logger.trace("in: block={}", block);
		final String id = block.getId();
		for (int x = 0; x < blocks.size(); x++) {
			if (blocks.get(x).getId().equals(id)) {
				WorksheetDocument.logger.trace("return: index={}", x);
				return x;
			}
		}
		WorksheetDocument.logger.trace("return: index={}", -1);
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetEvaluate#getBlocksFrom(int)
	 */
	@Override
	public DefaultBlock[] getBlocksFrom(final int index) {
		WorksheetDocument.logger.trace("in: index={}", index);
		DefaultBlock[] blocks = this.blocks.subList(index, this.blocks.size())
				.toArray(new DefaultBlock[this.blocks.size() - index]);
		WorksheetDocument.logger.trace("return: blocks={}", blocks);
		return blocks;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetEvaluate#markAllAfter(int)
	 */
	@Override
	public void markAllAfter(final int index) {
		WorksheetDocument.logger.trace("in: index={}", index);
		for (int x = index; x < blocks.size(); x++) {
			if (!blocks.get(x).isOutput()
					&& !blocks.get(x).isNeitherInNorOutput())
				blocks.get(x).setMark(true);
		}
		WorksheetDocument.logger.trace("return:");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetData#setBlock(int,
	 * de.prob.worksheet.block.impl.DefaultBlock)
	 */
	@Override
	public void setBlock(final int index, final DefaultBlock block) {
		WorksheetDocument.logger.trace("in: index={}, block={}", index, block);
		DefaultBlock oldBlock = blocks.get(index);
		blocks.set(index, block);
		if (!block.getId().equals(oldBlock.getId())) {
			logger.error("\nWhen setBlock is called the ID of the block should not change\n");
		}
		WorksheetDocument.logger.debug("Worksheet Blocks={}", blocks);
		WorksheetDocument.logger.trace("return:");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.IWorksheetEvaluate#removeOutputBlocks(de.prob.worksheet
	 * .block.IBlockData)
	 */
	@Override
	public void removeOutputBlocks(final DefaultBlock block) {
		WorksheetDocument.logger.trace("in: block={}", block);
		final int blockIndex = getBlockIndex(block);
		final String[] outputIds = blocks.get(blockIndex).getOutputBlockIds();
		WorksheetDocument.logger.debug("OutputIds of {} = {}", block.getId(),
				outputIds);
		for (final String outputId : outputIds) {
			final int index = getBlockIndexById(outputId);
			if (index != -1)
				blocks.remove(index);
		}
		block.setOutputBlockIds(null);
		WorksheetDocument.logger.debug("Worksheet Blocks={}", blocks);
		WorksheetDocument.logger.trace("return:");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetData#getBlockById(java.lang.String)
	 */
	@Override
	public DefaultBlock getBlockById(final String id) {
		WorksheetDocument.logger.trace("in: id={}", id);
		for (int x = 0; x < blocks.size(); x++) {
			if (blocks.get(x).getId().equals(id)) {
				WorksheetDocument.logger.trace("return: block={}",
						blocks.get(x));
				return blocks.get(x);
			}
		}
		WorksheetDocument.logger.trace("return: block=null");
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetData#getBlockIndexById(java.lang.String)
	 */
	@Override
	public int getBlockIndexById(final String id) {
		WorksheetDocument.logger.trace("in: id={}", id);
		int x = 0;
		for (IBlockData block : blocks) {
			if (block.getId().equals(id)) {
				WorksheetDocument.logger.trace("return: index={}", x);
				return x;
			}
			x++;
		}
		WorksheetDocument.logger.trace("return: index={}", -1);
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.IWorksheetData#appendBlock(de.prob.worksheet.block.
	 * impl.DefaultBlock)
	 */
	@Override
	public void appendBlock(final DefaultBlock block) {
		WorksheetDocument.logger.trace("in: block={}", block);
		insertBlock(blocks.size(), block);
		WorksheetDocument.logger.debug("Worksheet Blocks=={}", blocks);
		WorksheetDocument.logger.trace("return:");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.IWorksheetData#setBlock(de.prob.worksheet.block.impl
	 * .DefaultBlock)
	 */
	@Override
	public int setBlock(final DefaultBlock block) {
		WorksheetDocument.logger.trace("in: block={}", block);
		// find block index
		final int index = getBlockIndexById(block.getId());
		if (index == -1) {
			WorksheetDocument.logger.error("The Block is with the id="
					+ block.getId() + " idoesn't exist in the document");
			WorksheetDocument.logger.trace("return:");
			return -1;
		}
		// set Block
		blocks.set(index, block);
		WorksheetDocument.logger.debug("Worksheet Blocks=={}", blocks);
		WorksheetDocument.logger.trace("return:");
		return index;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetEvaluate#moveBlockTo(java.lang.String,
	 * int)
	 * 
	 * @unused
	 */
	@Override
	public void moveBlockTo(final String id, final int index) {
		WorksheetDocument.logger.trace("in: id={}, index={}", id, index);
		final int oldIndex = getBlockIndexById(id);
		blocks.add(index, blocks.remove(oldIndex));
		WorksheetDocument.logger.debug("Worksheet Blocks=={}", blocks);
		WorksheetDocument.logger.trace("return:");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.IWorksheetEvaluate#moveBlocksTo(java.lang.String[],
	 * int)
	 * 
	 * @unused
	 */
	@Override
	public void moveBlocksTo(final String[] ids, final int index) {
		WorksheetDocument.logger.trace("in: ids={},index={}", ids, index);
		final DefaultBlock[] blocks = new DefaultBlock[ids.length];
		for (int x = 0; x < ids.length; x++) {
			blocks[x] = this.blocks.remove(getBlockIndexById(ids[x]));
		}
		for (int x = blocks.length - 1; x >= 0; x--) {
			this.blocks.add(index, blocks[x]);
		}
		WorksheetDocument.logger.debug("Worksheet Blocks=={}", this.blocks);
		WorksheetDocument.logger.trace("return:");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.IWorksheetEvaluate#switchBlockType(java.lang.String,
	 * de.prob.worksheet.block.impl.DefaultBlock)
	 */
	@Override
	public void switchBlockType(String id, DefaultBlock newBlock) {
		WorksheetDocument.logger.trace("in: id={} type={}", id, newBlock);
		int index = getBlockIndexById(id);
		IBlockData oldBlock = getBlockById(id);
		newBlock.setId(oldBlock.getId());
		newBlock.setOutputBlockIds(oldBlock.getOutputBlockIds());
		newBlock.getEditor().setEditorContent(
				oldBlock.getEditor().getEditorContent());
		blocks.set(index, newBlock);
		WorksheetDocument.logger.debug("Worksheet Blocks=={}", blocks);
		WorksheetDocument.logger.trace("return:");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.IWorksheetEvaluate#insertOutputBlocks(de.prob.worksheet
	 * .block.impl.DefaultBlock, de.prob.worksheet.block.impl.DefaultBlock[])
	 */
	@Override
	public void insertOutputBlocks(DefaultBlock block, DefaultBlock[] blocks) {
		WorksheetDocument.logger
				.trace("in: block={}, blocks={}", block, blocks);
		int index = getBlockIndex(block);

		if (block.isInputAndOutput()) {
		}

		for (final DefaultBlock outBlock : blocks) {
			if (block.isInputAndOutput()) {
				setBlock(index, outBlock);
				block = outBlock;
				continue;
			}
			index++;
			insertBlock(index, outBlock);
			block.addOutputId(outBlock.getId());
		}
		WorksheetDocument.logger.debug("OutputIds of {} = {}", block.getId(),
				block.getOutputBlockIds());
		WorksheetDocument.logger.debug("Worksheet Blocks={}", this.blocks);
		WorksheetDocument.logger.trace("return:");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.IWorksheetData#isLastBlock(de.prob.worksheet.block.
	 * IBlockData)
	 */
	@Override
	public boolean isLastBlock(DefaultBlock block) {
		WorksheetDocument.logger.trace("in block={}",
				block.equals(blocks.get(blocks.size() - 1)));
		WorksheetDocument.logger.trace("return: blocksEqual={}",
				block.equals(blocks.get(blocks.size() - 1)));
		return block.equals(blocks.get(blocks.size() - 1));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetData#getFirst()
	 */
	@Override
	public DefaultBlock getFirst() {
		WorksheetDocument.logger.trace("in:");
		WorksheetDocument.logger.trace("return: block={}", blocks.get(0));
		return blocks.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.IWorksheetEvaluate#getBlocksFrom(de.prob.worksheet.
	 * block.IBlockData)
	 */
	@Override
	public DefaultBlock[] getBlocksFrom(DefaultBlock block) {
		WorksheetDocument.logger.trace("in:");
		DefaultBlock[] blocks = getBlocksFrom(getBlockIndex(block));
		WorksheetDocument.logger.trace("return: blocks={}", blocks);
		return blocks;
	}

	@Override
	public void removeBlock(DefaultBlock block) {
		int index = getBlockIndex(block);
		blocks.remove(index);
		if (!block.isOutput() && !block.isNeitherInNorOutput()) {
			history.remove(block.getId());
		}
	}

	public void setContexts(String id, ContextHistory blockHistory) {
		history.setContexts(id, blockHistory);
	}
}