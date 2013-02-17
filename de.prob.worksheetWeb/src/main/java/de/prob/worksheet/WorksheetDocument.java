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
	Logger logger = LoggerFactory.getLogger(WorksheetDocument.class);
	private final ArrayList<IBlock> blocks;

	private boolean hasMenu;
	private boolean hasBody;
	private ArrayList<WorksheetMenuNode> menu;
	private String id;
	private int blockCounter = 0;

	public WorksheetDocument() {
		logger.trace("");
		this.hasMenu = false;
		this.hasBody = true;
		this.blocks = new ArrayList<IBlock>();
		this.menu = new ArrayList<WorksheetMenuNode>();
		final WorksheetMenuNode action = new WorksheetMenuNode("File", "", "");
		final WorksheetMenuNode evalThis = new WorksheetMenuNode("Open", "",
				"ui-icon-disk");
		evalThis.setClick("function() {alert('Open')}");
		action.addChild(evalThis);
		this.menu.add(action);
	}

	@XmlElements(value = { @XmlElement(name = "block") })
	public IBlock[] getBlocks() {
		logger.trace("{}", this.blocks.toArray(new IBlock[this.blocks.size()]));
		return this.blocks.toArray(new IBlock[this.blocks.size()]);
	}

	public void setBlocks(final IBlock[] blocks) {
		logger.trace("{}", blocks);
		this.blocks.clear();
		this.blocks.addAll(Arrays.asList(blocks));
		logger.debug("{}", this.blocks);
	}

	@XmlTransient
	public boolean getHasMenu() {
		logger.trace("{}", this.hasMenu);
		return this.hasMenu;
	}

	public void setHasMenu(final boolean hasMenu) {
		logger.trace("{}", hasMenu);
		this.hasMenu = hasMenu;
	}

	@XmlTransient
	public boolean getHasBody() {
		logger.trace("{}", this.hasBody);
		return this.hasBody;
	}

	public void setHasBody(final boolean hasBody) {
		logger.trace("{}", hasBody);
		this.hasBody = hasBody;
	}

	@XmlTransient
	public ArrayList<WorksheetMenuNode> getMenu() {
		logger.trace("{}", this.menu);
		return this.menu;
	}

	public void setMenu(final ArrayList<WorksheetMenuNode> menu) {
		logger.trace("{}", menu);
		this.menu = menu;
	}

	@XmlAttribute(name = "id")
	@XmlID
	public String getId() {
		logger.trace("{}", this.id);
		return this.id;
	}

	public void setId(final String id) {
		logger.trace("{}", id);
		this.id = id;
	}

	/*
	 * Inserts the block at index and shifts the rest
	 */
	@XmlAttribute(name = "blockCounter")
	public int getBlockCounter() {
		logger.trace("{}", blockCounter);
		return blockCounter;
	}

	public void setBlockCounter(int blockCounter) {
		logger.trace("{}", blockCounter);
		this.blockCounter = blockCounter;
	}

	/**
	 * 
	 * 
	 * Assigns a new Id to the inserted Block
	 * 
	 * @param index
	 * @param block
	 */
	public void insertBlock(final int index, final IBlock block) {
		logger.trace("index={}, block={}", index, block);
		assert (this.blockCounter < Integer.MAX_VALUE);

		this.blocks.add(index, block);
		this.blockCounter++;
		block.setId("ws-block-id-" + this.blockCounter);
		logger.debug("{}", this.blocks);
	}

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
		logger.trace("{}", block);
		final int blockIndex = this.getBlockIndex(block);
		final String[] outputIds = this.blocks.get(blockIndex)
				.getOutputBlockIds();
		for (final String outputId : outputIds) {
			final IBlock nblock = this.getBlockById(outputId);
			this.blocks.remove(nblock);
		}
		block.setOutputBlockIds(null);
		logger.debug("{}", this.blocks);
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
		logger.debug("{}", this.blocks);

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