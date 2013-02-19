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

import de.prob.worksheet.block.IBlock;

/**
 * @author Rene
 * 
 */
@XmlRootElement(name = "worksheet")
public class WorksheetDocument {

	/**
	 * The static slf4j Logger for this class
	 */
	private static Logger logger = LoggerFactory
			.getLogger(WorksheetDocument.class);

	/**
	 * The list containing the blocks of this document
	 */
	private final ArrayList<IBlock> blocks;

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
		logger.trace("");
		this.hasMenu = false;
		this.hasBody = true;
		this.blocks = new ArrayList<IBlock>();
		this.menu = new ArrayList<WorksheetMenuNode>();
	}

	/**
	 * Getter for this documents blocks
	 * 
	 * @return an array containing all blocks of this document
	 */
	@XmlElements(value = { @XmlElement(name = "block") })
	public IBlock[] getBlocks() {
		logger.trace("{}", this.blocks.toArray(new IBlock[this.blocks.size()]));
		return this.blocks.toArray(new IBlock[this.blocks.size()]);
	}

	/**
	 * Setter for this documents blocks
	 * 
	 * @param blocks
	 *            array to set for this document
	 */
	public void setBlocks(final IBlock[] blocks) {
		logger.trace("{}", blocks);
		this.blocks.clear();
		this.blocks.addAll(Arrays.asList(blocks));
		logger.debug("{}", this.blocks);
	}

	/**
	 * Getter for the hasMenu Flag
	 * 
	 * @return a boolean flag for hasMenu
	 */
	@XmlTransient
	public boolean getHasMenu() {
		logger.trace("{}", this.hasMenu);
		return this.hasMenu;
	}

	/**
	 * Setter for the hasMenu flag
	 * 
	 * @param hasMenu
	 *            flag to be set
	 */
	public void setHasMenu(final boolean hasMenu) {
		logger.trace("{}", hasMenu);
		this.hasMenu = hasMenu;
	}

	/**
	 * Getter for the hasBody flag
	 * 
	 * @return a boolean for the hasBody flag
	 */
	@XmlTransient
	public boolean getHasBody() {
		logger.trace("{}", this.hasBody);
		return this.hasBody;
	}

	/**
	 * Setter for the hasBody flag
	 * 
	 * @param hasBody
	 *            flag to be set
	 */
	public void setHasBody(final boolean hasBody) {
		logger.trace("{}", hasBody);
		this.hasBody = hasBody;
	}

	/**
	 * Getter for the menu list
	 * 
	 * @return an array containing all nodes of this menu
	 */
	@XmlTransient
	public ArrayList<WorksheetMenuNode> getMenu() {
		logger.trace("{}", this.menu);
		return this.menu;
	}

	/**
	 * Setter for the menu list
	 * 
	 * @param menu
	 *            array to be set for this document
	 */
	public void setMenu(final ArrayList<WorksheetMenuNode> menu) {
		logger.trace("{}", menu);
		this.menu = menu;
	}

	/**
	 * Getter for the id of this document
	 * 
	 * @return id of this document
	 */
	@XmlAttribute(name = "id")
	@XmlID
	public String getId() {
		logger.trace("{}", this.id);
		return this.id;
	}

	/**
	 * Setter for the id of this document
	 * 
	 * @param id
	 *            to be set for this document
	 */
	public void setId(final String id) {
		logger.trace("{}", id);
		this.id = id;
	}

	/**
	 * Getter for the blockCounter of this document
	 * 
	 * @return the blockcounter
	 */
	@XmlAttribute(name = "blockCounter")
	public int getBlockCounter() {
		logger.trace("{}", blockCounter);
		return blockCounter;
	}

	/**
	 * Setter for the blockCounter of this document
	 * 
	 * @param blockCounter
	 *            to be set
	 */
	public void setBlockCounter(int blockCounter) {
		logger.trace("{}", blockCounter);
		this.blockCounter = blockCounter;
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
	public void insertBlock(final int index, final IBlock block) {
		logger.trace("index={}, block={}", index, block);
		assert (this.blockCounter < Integer.MAX_VALUE);

		this.blocks.add(index, block);
		this.blockCounter++;
		block.setId("ws-block-id-" + this.blockCounter);
		logger.debug("{}", this.blocks);
	}

	/**
	 * Returns the index of a specified block or -1 if the block is not found
	 * 
	 * @param block
	 *            to get the index for
	 * @return the index of the block
	 */
	public int getBlockIndex(final IBlock block) {
		logger.trace("{}", block);
		final String id = block.getId();
		for (int x = 0; x < this.blocks.size(); x++) {
			if (this.blocks.get(x).getId().equals(id)) {
				logger.trace("return:{}", x);
				return x;
			}
		}
		logger.trace("return:{}", -1);
		return -1;
	}

	public IBlock[] getBlocksFrom(final int index) {
		logger.trace("{}", index);
		IBlock[] blocks = this.blocks.subList(index, this.blocks.size())
				.toArray(new IBlock[this.blocks.size() - index]);
		logger.trace("return:{}", blocks);
		return blocks;
	}

	public void markAllAfter(final int blockIndex) {
		logger.trace("{}", blockIndex);
		for (int x = blockIndex; x < this.blocks.size(); x++) {
			this.blocks.get(x).setMark(true);
		}
	}

	public void setBlock(final int index, final IBlock block) {
		logger.trace("index={}, block={}", index, block);
		this.blocks.set(index, block);
		logger.debug("{}", this.blocks);
	}

	public void removeOutputBlocks(final IBlock block) {
		logger.trace("in: block={}", block);
		final int blockIndex = this.getBlockIndex(block);
		final String[] outputIds = this.blocks.get(blockIndex)
				.getOutputBlockIds();
		for (final String outputId : outputIds) {
			final int index = this.getBlockIndexById(outputId);
			this.blocks.remove(index);
		}
		block.setOutputBlockIds(null);
		logger.debug("OutputIds of {} = {}", block.getId(),
				block.getOutputBlockIds());
		logger.debug("Worsheet Blocks={}", this.blocks);
		logger.trace("return:");
	}

	public IBlock getBlockById(final String id) {
		logger.trace("{}", id);
		for (int x = 0; x < this.blocks.size(); x++) {
			if (this.blocks.get(x).getId().equals(id))
				logger.trace("return:{}", this.blocks.get(x));
			return this.blocks.get(x);
		}
		logger.trace("return:null");
		return null;
	}

	public int getBlockIndexById(final String id) {
		logger.trace("{}", id);
		int x = 0;
		for (final IBlock block : this.blocks) {
			if (block.getId().equals(id)) {
				logger.trace("return:{}", x);
				return x;
			}
			x++;
		}
		logger.trace("return:{}", -1);
		return -1;
	}

	/**
	 * @param block
	 */
	public void appendBlock(final IBlock block) {
		logger.trace("{}", block);
		this.insertBlock(this.blocks.size(), block);
	}

	/**
	 * @param block
	 */
	public void setBlock(final IBlock block) {
		logger.trace("{}", block);
		// find block index
		final int index = this.getBlockIndexById(block.getId());
		if (index == -1) {
			// TODO decide wehter to throw an error or add the new block;
			System.err.println("The block does not exist");
		}
		// set Block
		this.blocks.set(index, block);
		logger.debug("{}", this.blocks);

		return;
	}

	/**
	 * @param id
	 * @param index
	 */
	public void moveBlockTo(final String id, final int index) {
		logger.trace("id={}, index={}", id, index);
		final int oldIndex = this.getBlockIndexById(id);
		this.blocks.add(index, this.blocks.remove(oldIndex));
		logger.debug("{}", this.blocks);
	}

	/**
	 * @param ids
	 * @param index
	 */
	public void moveBlocksTo(final String[] ids, final int index) {
		logger.trace("ids={},index={}", ids, index);
		final IBlock[] blocks = new IBlock[ids.length];
		for (int x = 0; x < ids.length; x++) {
			blocks[x] = this.blocks.remove(this.getBlockIndexById(ids[x]));
		}
		for (int x = blocks.length - 1; x >= 0; x--) {
			this.blocks.add(index, blocks[x]);
		}
		logger.debug("{}", this.blocks);
	}

	public void switchBlockType(String id, IBlock newBlock) {
		logger.trace("id={} type={{}}", id, newBlock);
		int index = this.getBlockIndexById(id);
		newBlock.setId(id);
		this.blocks.set(index, newBlock);
		logger.debug("{}", this.blocks);
		logger.trace("return");
	}

	public void insertOutputBlocks(IBlock block, IBlock[] blocks) {
		logger.trace("block={}, blocks={}", block, blocks);
		int index = this.getBlockIndex(block);

		if (block.isInputAndOutput()) {
		}

		for (final IBlock outBlock : blocks) {
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
		logger.debug("Worsheet Blocks={}", this.blocks);
		logger.trace("return:");

	}

	public boolean isLastBlock(IBlock newBlock) {
		logger.trace("{}",
				newBlock.equals(this.blocks.get(this.blocks.size() - 1)));
		return newBlock.equals(this.blocks.get(this.blocks.size() - 1));
	}

	public IBlock getFirst() {
		logger.trace("return:{}", this.blocks.get(0));
		return this.blocks.get(0);
	}

	public IBlock[] getBlocksFrom(IBlock block) {
		IBlock[] blocks = getBlocksFrom(getBlockIndex(block));
		logger.trace("return:{}", blocks);
		return blocks;
	}
}