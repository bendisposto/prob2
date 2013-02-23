package de.prob.worksheet;

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

/**
 * The WorksheetDocument stores all data including blocks of a worksheet
 * 
 * @author Rene
 * 
 */
@XmlRootElement(name = "worksheet")
public class WorksheetDocument {

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

	/**
	 * Getter for this documents blocks
	 * 
	 * @return an array containing all blocks of this document
	 */
	@XmlElements(value = { @XmlElement(name = "block") })
	public IBlockData[] getBlocks() {
		logger.trace("in:");
		logger.trace("return: blocks=",
				this.blocks.toArray(new DefaultBlock[this.blocks.size()]));
		return this.blocks.toArray(new DefaultBlock[this.blocks.size()]);
	}

	/**
	 * Setter for this documents blocks
	 * 
	 * @param blocks
	 *            array to set for this document
	 */
	public void setBlocks(final DefaultBlock[] blocks) {
		logger.trace("in: blocks={}", blocks);
		this.blocks.clear();
		this.blocks.addAll(Arrays.asList(blocks));
		logger.trace("return:");
	}

	/**
	 * Getter for the hasMenu Flag
	 * 
	 * @return a boolean flag for hasMenu
	 */
	@XmlTransient
	public boolean getHasMenu() {
		logger.trace("in:");
		logger.trace("return: hasMenu={}", this.hasMenu);
		return this.hasMenu;
	}

	/**
	 * Setter for the hasMenu flag
	 * 
	 * @param hasMenu
	 *            flag to be set
	 */
	public void setHasMenu(final boolean hasMenu) {
		logger.trace("in: hasMenu{}", hasMenu);
		this.hasMenu = hasMenu;
		logger.trace("return:");
	}

	/**
	 * Getter for the hasBody flag
	 * 
	 * @return a boolean for the hasBody flag
	 */
	@XmlTransient
	public boolean getHasBody() {
		logger.trace("in:");
		logger.trace("return: hasBody={}", this.hasBody);
		return this.hasBody;
	}

	/**
	 * Setter for the hasBody flag
	 * 
	 * @param hasBody
	 *            flag to be set
	 */
	public void setHasBody(final boolean hasBody) {
		logger.trace("in: hasBody={}", hasBody);
		this.hasBody = hasBody;
		logger.trace("return:");
	}

	/**
	 * Getter for the menu list
	 * 
	 * @return an array containing all nodes of this menu
	 */
	@XmlTransient
	public ArrayList<WorksheetMenuNode> getMenu() {
		logger.trace("in:");
		logger.trace("return: menu={}", this.menu);
		return this.menu;
	}

	/**
	 * Setter for the menu list
	 * 
	 * @param menu
	 *            array to be set for this document
	 */
	public void setMenu(final ArrayList<WorksheetMenuNode> menu) {
		logger.trace("in: menu={}", menu);
		this.menu = menu;
		logger.trace("return:");
	}

	/**
	 * Getter for the id of this document
	 * 
	 * @return id of this document
	 */
	@XmlAttribute(name = "id")
	@XmlID
	public String getId() {
		logger.trace("in:");
		logger.trace("return: id={}", this.id);
		return this.id;
	}

	/**
	 * Setter for the id of this document
	 * 
	 * @param id
	 *            to be set for this document
	 */
	public void setId(final String id) {
		logger.trace("in: id={}", id);
		this.id = id;
		logger.trace("return:");
	}

	/**
	 * Getter for the blockCounter of this document
	 * 
	 * @return the blockcounter
	 */
	@XmlAttribute(name = "blockCounter")
	public int getBlockCounter() {
		logger.trace("in:");
		logger.trace("return: blockCounter={}", blockCounter);
		return blockCounter;
	}

	/**
	 * Setter for the blockCounter of this document
	 * 
	 * @param blockCounter
	 *            to be set
	 */
	public void setBlockCounter(int blockCounter) {
		logger.trace("in: blockCounter={}", blockCounter);
		this.blockCounter = blockCounter;
		logger.trace("return:");
	}

	/**
	 * Inserts a block into the document at the specified index. Assigns a new
	 * Id to the inserted Block and shifts all blocks at and after the specified
	 * position to the left
	 * 
	 * @param index
	 *            to insert the block at
	 * @param block
	 *            to insert
	 */
	public void insertBlock(final int index, final DefaultBlock block) {
		logger.trace("in: index={}, block={}", index, block);
		assert (this.blockCounter < Integer.MAX_VALUE);

		this.blocks.add(index, block);
		this.blockCounter++;
		block.setId("ws-block-id-" + this.blockCounter);
		logger.debug("Worksheet Blocks={}", this.blocks);
		logger.trace("return:");
	}

	/**
	 * Returns the index of a specified block or -1 if the block is not found
	 * 
	 * @param block
	 *            to get the index for
	 * @return the index of the block
	 */
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

	/**
	 * Returns all blocks from index to blocks.length
	 * 
	 * @param index
	 *            for the first block to return
	 * @return an array containing all blocks from index to blocks.length
	 */
	public DefaultBlock[] getBlocksFrom(final int index) {
		logger.trace("in: index={}", index);
		DefaultBlock[] blocks = this.blocks.subList(index, this.blocks.size())
				.toArray(new DefaultBlock[this.blocks.size() - index]);
		logger.trace("return: blocks={}", blocks);
		return blocks;
	}

	/**
	 * Marks all blocks from index to blocks.length. A marked block isn't
	 * evaluated so his content could be incorrect
	 * 
	 * @param blockIndex
	 */
	public void markAllAfter(final int index) {
		logger.trace("in: index={}", index);
		for (int x = index; x < this.blocks.size(); x++) {
			this.blocks.get(x).setMark(true);
		}
		logger.trace("return:");
	}

	/**
	 * Sets the block at index. The block which had been at index before setting
	 * is removed
	 * 
	 * @param index
	 * @param block
	 */
	public void setBlock(final int index, final DefaultBlock block) {
		logger.trace("in: index={}, block={}", index, block);
		this.blocks.set(index, block);
		logger.debug("Worksheet Blocks={}", this.blocks);
		logger.trace("return:");
	}

	/**
	 * Removes all Output Blocks for the given Input BLock
	 * 
	 * @param block
	 *            to remove the output blocks for
	 */
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

	/**
	 * Returns the block for the given id
	 * 
	 * @param id
	 *            to return the block for
	 * @return the block with the given id
	 */
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

	/**
	 * Returns the index of the block with given id or -1 if no block with the
	 * give id exists
	 * 
	 * @param id
	 *            of the block to retrieve the index for
	 * @return the index of the block
	 */
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

	/**
	 * Appends the block to the end of the document
	 * 
	 * @param block
	 *            to append
	 */
	public void appendBlock(final DefaultBlock block) {
		logger.trace("in: block={}", block);
		this.insertBlock(this.blocks.size(), block);
		logger.debug("Worksheet Blocks=={}", this.blocks);
		logger.trace("return:");
	}

	/**
	 * Sets the block with block.id to the new Block. If no block exists in the
	 * document with block id the new block is not set
	 * 
	 * @param block
	 *            to set
	 * @return the index of the set block or -1 if the block is not set
	 */
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

	/**
	 * Moves the block with id to index
	 * 
	 * @param id
	 *            of the block to move
	 * @param index
	 *            to move the block to
	 */
	public void moveBlockTo(final String id, final int index) {
		logger.trace("in: id={}, index={}", id, index);
		final int oldIndex = this.getBlockIndexById(id);
		this.blocks.add(index, this.blocks.remove(oldIndex));
		logger.debug("Worksheet Blocks=={}", this.blocks);
		logger.trace("return:");
	}

	/**
	 * Moves the blocks with the ids to the positions starting at index
	 * 
	 * @param ids
	 *            of the blocks to move
	 * @param index
	 *            of the starting pos to move the blocks to
	 */
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

	/**
	 * Changes the type of the block with id to newBlock. The new block gets the
	 * id of the old block
	 * 
	 * @param id
	 *            of the block to switch
	 * @param newBlock
	 *            to set
	 */
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

	/**
	 * Appends the output blocks to the given block and sets the output ids to
	 * the block
	 * 
	 * @param block
	 *            to append the output blocks to
	 * @param blocks
	 *            to append
	 */
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

	/**
	 * Returns whether the given block is the last one in the document.
	 * 
	 * @param block
	 *            to test
	 * @return true if the block is the last block of the document else false
	 */
	public boolean isLastBlock(IBlockData block) {
		logger.trace("in block={}",
				block.equals(this.blocks.get(this.blocks.size() - 1)));
		logger.trace("return: blocksEqual={}",
				block.equals(this.blocks.get(this.blocks.size() - 1)));
		return block.equals(this.blocks.get(this.blocks.size() - 1));
	}

	/**
	 * Returns the first block in the document
	 * 
	 * @return
	 */
	public IBlockData getFirst() {
		logger.trace("in:");
		logger.trace("return: block={}", this.blocks.get(0));
		return this.blocks.get(0);
	}

	/**
	 * Returns all blocks from block to the end of the document
	 * 
	 * @param block
	 *            which is the first one to be retrieve
	 * @return an array of blocks
	 */
	public IBlockData[] getBlocksFrom(IBlockData block) {
		logger.trace("in:");
		IBlockData[] blocks = getBlocksFrom(getBlockIndex(block));
		logger.trace("return: blocks={}", blocks);
		return blocks;
	}
}