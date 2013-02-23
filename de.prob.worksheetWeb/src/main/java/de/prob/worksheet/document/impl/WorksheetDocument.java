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
public class WorksheetDocument implements IWorksheetData, IWorksheetUI, IWorksheetEvaluate {

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

	/**
	 * A counter used to count how many blocks have been added to this document.
	 * It's used to set unique id's for the blocks
	 */
	private int blockCounter = 0;

	/**
	 * The Constructor of the Document
	 */
	public WorksheetDocument() {
		logger.trace("in:");
		this.hasMenu = false;
		this.hasBody = true;
		this.blocks = new ArrayList<DefaultBlock>();
		this.menu = new ArrayList<WorksheetMenuNode>();
		logger.trace("return:");
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.IWorksheetData#getBlocks()
	 */
	@Override
	@XmlElements(value = { @XmlElement(name = "block") })
	public IBlockData[] getBlocks() {
		logger.trace("in:");
		logger.trace("return: blocks=",
				this.blocks.toArray(new DefaultBlock[this.blocks.size()]));
		return this.blocks.toArray(new DefaultBlock[this.blocks.size()]);
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.IWorksheetData#setBlocks(de.prob.worksheet.block.impl.DefaultBlock[])
	 */
	@Override
	public void setBlocks(final DefaultBlock[] blocks) {
		logger.trace("in: blocks={}", blocks);
		this.blocks.clear();
		this.blocks.addAll(Arrays.asList(blocks));
		logger.trace("return:");
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.IWorksheetUI#getHasMenu()
	 */
	@Override
	@XmlTransient
	public boolean getHasMenu() {
		logger.trace("in:");
		logger.trace("return: hasMenu={}", this.hasMenu);
		return this.hasMenu;
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.IWorksheetUI#setHasMenu(boolean)
	 */
	@Override
	public void setHasMenu(final boolean hasMenu) {
		logger.trace("in: hasMenu{}", hasMenu);
		this.hasMenu = hasMenu;
		logger.trace("return:");
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.IWorksheetUI#getHasBody()
	 */
	@Override
	@XmlTransient
	public boolean getHasBody() {
		logger.trace("in:");
		logger.trace("return: hasBody={}", this.hasBody);
		return this.hasBody;
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.IWorksheetUI#setHasBody(boolean)
	 */
	@Override
	public void setHasBody(final boolean hasBody) {
		logger.trace("in: hasBody={}", hasBody);
		this.hasBody = hasBody;
		logger.trace("return:");
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.IWorksheetUI#getMenu()
	 */
	@Override
	@XmlTransient
	public ArrayList<WorksheetMenuNode> getMenu() {
		logger.trace("in:");
		logger.trace("return: menu={}", this.menu);
		return this.menu;
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.IWorksheetUI#setMenu(java.util.ArrayList)
	 */
	@Override
	public void setMenu(final ArrayList<WorksheetMenuNode> menu) {
		logger.trace("in: menu={}", menu);
		this.menu = menu;
		logger.trace("return:");
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.IWorksheetData#getId()
	 */
	@Override
	@XmlAttribute(name = "id")
	@XmlID
	public String getId() {
		logger.trace("in:");
		logger.trace("return: id={}", this.id);
		return this.id;
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.IWorksheetData#setId(java.lang.String)
	 */
	@Override
	public void setId(final String id) {
		logger.trace("in: id={}", id);
		this.id = id;
		logger.trace("return:");
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.IWorksheetData#getBlockCounter()
	 */
	@Override
	@XmlAttribute(name = "blockCounter")
	public int getBlockCounter() {
		logger.trace("in:");
		logger.trace("return: blockCounter={}", blockCounter);
		return blockCounter;
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.IWorksheetData#setBlockCounter(int)
	 */
	@Override
	public void setBlockCounter(int blockCounter) {
		logger.trace("in: blockCounter={}", blockCounter);
		this.blockCounter = blockCounter;
		logger.trace("return:");
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.IWorksheetData#insertBlock(int, de.prob.worksheet.block.impl.DefaultBlock)
	 */
	@Override
	public void insertBlock(final int index, final DefaultBlock block) {
		logger.trace("in: index={}, block={}", index, block);
		assert (this.blockCounter < Integer.MAX_VALUE);

		this.blocks.add(index, block);
		this.blockCounter++;
		block.setId("ws-block-id-" + this.blockCounter);
		logger.debug("Worksheet Blocks={}", this.blocks);
		logger.trace("return:");
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.IWorksheetData#getBlockIndex(de.prob.worksheet.block.IBlockData)
	 */
	@Override
	public int getBlockIndex(final IBlockData block) {
		logger.trace("in: block={}", block);
		final String id = block.getId();
		for (int x = 0; x < this.blocks.size(); x++) {
			if (this.blocks.get(x).getId().equals(id)) {
				logger.trace("return: index={}", x);
				return x;
			}
		}
		logger.trace("return: index={}", -1);
		return -1;
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.IWorksheetEvaluate#getBlocksFrom(int)
	 */
	@Override
	public DefaultBlock[] getBlocksFrom(final int index) {
		logger.trace("in: index={}", index);
		DefaultBlock[] blocks = this.blocks.subList(index, this.blocks.size())
				.toArray(new DefaultBlock[this.blocks.size() - index]);
		logger.trace("return: blocks={}", blocks);
		return blocks;
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.IWorksheetEvaluate#markAllAfter(int)
	 */
	@Override
	public void markAllAfter(final int index) {
		logger.trace("in: index={}", index);
		for (int x = index; x < this.blocks.size(); x++) {
			this.blocks.get(x).setMark(true);
		}
		logger.trace("return:");
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.IWorksheetData#setBlock(int, de.prob.worksheet.block.impl.DefaultBlock)
	 */
	@Override
	public void setBlock(final int index, final DefaultBlock block) {
		logger.trace("in: index={}, block={}", index, block);
		this.blocks.set(index, block);
		logger.debug("Worksheet Blocks={}", this.blocks);
		logger.trace("return:");
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.IWorksheetEvaluate#removeOutputBlocks(de.prob.worksheet.block.IBlockData)
	 */
	@Override
	public void removeOutputBlocks(final IBlockData block) {
		logger.trace("in: block={}", block);
		final int blockIndex = this.getBlockIndex(block);
		final String[] outputIds = this.blocks.get(blockIndex)
				.getOutputBlockIds();
		logger.debug("OutputIds of {} = {}", block.getId(), outputIds);
		for (final String outputId : outputIds) {
			final int index = this.getBlockIndexById(outputId);
			this.blocks.remove(index);
		}
		block.setOutputBlockIds(null);
		logger.debug("Worksheet Blocks={}", this.blocks);
		logger.trace("return:");
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.IWorksheetData#getBlockById(java.lang.String)
	 */
	@Override
	public DefaultBlock getBlockById(final String id) {
		logger.trace("in: id={}", id);
		for (int x = 0; x < this.blocks.size(); x++) {
			if (this.blocks.get(x).getId().equals(id)) {
				logger.trace("return: block={}", this.blocks.get(x));
				return this.blocks.get(x);
			}
		}
		logger.trace("return: block=null");
		return null;
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.IWorksheetData#getBlockIndexById(java.lang.String)
	 */
	@Override
	public int getBlockIndexById(final String id) {
		logger.trace("in: id={}", id);
		int x = 0;
		for (final IBlockData block : this.blocks) {
			if (block.getId().equals(id)) {
				logger.trace("return: index={}", x);
				return x;
			}
			x++;
		}
		logger.trace("return: index={}", -1);
		return -1;
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.IWorksheetData#appendBlock(de.prob.worksheet.block.impl.DefaultBlock)
	 */
	@Override
	public void appendBlock(final DefaultBlock block) {
		logger.trace("in: block={}", block);
		this.insertBlock(this.blocks.size(), block);
		logger.debug("Worksheet Blocks=={}", this.blocks);
		logger.trace("return:");
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.IWorksheetData#setBlock(de.prob.worksheet.block.impl.DefaultBlock)
	 */
	@Override
	public int setBlock(final DefaultBlock block) {
		logger.trace("in: block={}", block);
		// find block index
		final int index = this.getBlockIndexById(block.getId());
		if (index == -1) {
			logger.error("The Block is with the id=" + block.getId()
					+ " idoesn't exist in the document");
			logger.trace("return:");
			return -1;
		}
		// set Block
		this.blocks.set(index, block);
		logger.debug("Worksheet Blocks=={}", this.blocks);
		logger.trace("return:");
		return index;
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.IWorksheetEvaluate#moveBlockTo(java.lang.String, int)
	 */
	@Override
	public void moveBlockTo(final String id, final int index) {
		logger.trace("in: id={}, index={}", id, index);
		final int oldIndex = this.getBlockIndexById(id);
		this.blocks.add(index, this.blocks.remove(oldIndex));
		logger.debug("Worksheet Blocks=={}", this.blocks);
		logger.trace("return:");
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.IWorksheetEvaluate#moveBlocksTo(java.lang.String[], int)
	 */
	@Override
	public void moveBlocksTo(final String[] ids, final int index) {
		logger.trace("in: ids={},index={}", ids, index);
		final DefaultBlock[] blocks = new DefaultBlock[ids.length];
		for (int x = 0; x < ids.length; x++) {
			blocks[x] = this.blocks.remove(this.getBlockIndexById(ids[x]));
		}
		for (int x = blocks.length - 1; x >= 0; x--) {
			this.blocks.add(index, blocks[x]);
		}
		logger.debug("Worksheet Blocks=={}", this.blocks);
		logger.trace("return:");
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.IWorksheetEvaluate#switchBlockType(java.lang.String, de.prob.worksheet.block.impl.DefaultBlock)
	 */
	@Override
	public void switchBlockType(String id, DefaultBlock newBlock) {
		logger.trace("in: id={} type={}", id, newBlock);
		int index = this.getBlockIndexById(id);
		IBlockData oldBlock = this.getBlockById(id);
		newBlock.setId(oldBlock.getId());
		newBlock.setOutputBlockIds(oldBlock.getOutputBlockIds());
		this.blocks.set(index, newBlock);
		logger.debug("Worksheet Blocks=={}", this.blocks);
		logger.trace("return:");
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.IWorksheetEvaluate#insertOutputBlocks(de.prob.worksheet.block.impl.DefaultBlock, de.prob.worksheet.block.impl.DefaultBlock[])
	 */
	@Override
	public void insertOutputBlocks(DefaultBlock block, DefaultBlock[] blocks) {
		logger.trace("in: block={}, blocks={}", block, blocks);
		int index = this.getBlockIndex(block);

		if (block.isInputAndOutput()) {
		}

		for (final DefaultBlock outBlock : blocks) {
			if (block.isImmediateEvaluation()) {
				setBlock(index, outBlock);
				block = outBlock;
				continue;
			}
			index++;
			this.insertBlock(index, outBlock);
			block.addOutputId(outBlock.getId());
		}
		logger.debug("OutputIds of {} = {}", block.getId(),
				block.getOutputBlockIds());
		logger.debug("Worksheet Blocks={}", this.blocks);
		logger.trace("return:");

	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.IWorksheetData#isLastBlock(de.prob.worksheet.block.IBlockData)
	 */
	@Override
	public boolean isLastBlock(IBlockData block) {
		logger.trace("in block={}",
				block.equals(this.blocks.get(this.blocks.size() - 1)));
		logger.trace("return: blocksEqual={}",
				block.equals(this.blocks.get(this.blocks.size() - 1)));
		return block.equals(this.blocks.get(this.blocks.size() - 1));
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.IWorksheetData#getFirst()
	 */
	@Override
	public IBlockData getFirst() {
		logger.trace("in:");
		logger.trace("return: block={}", this.blocks.get(0));
		return this.blocks.get(0);
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.IWorksheetEvaluate#getBlocksFrom(de.prob.worksheet.block.IBlockData)
	 */
	@Override
	public IBlockData[] getBlocksFrom(IBlockData block) {
		logger.trace("in:");
		IBlockData[] blocks = getBlocksFrom(getBlockIndex(block));
		logger.trace("return: blocks={}", blocks);
		return blocks;
	}
}