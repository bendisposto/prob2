package de.prob.worksheet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.prob.worksheet.block.IBlock;

@XmlRootElement(name="worksheet")
public class WorksheetDocument {

	private final ArrayList<IBlock> blocks;

	private boolean hasMenu;
	private boolean hasBody;
	private ArrayList<WorksheetMenuNode> menu;
	private String id;
	private int blockCounter = 0;

	public WorksheetDocument() {
		this.hasMenu = false;
		this.hasBody = true;
		this.blocks = new ArrayList<IBlock>();
		this.menu = new ArrayList<WorksheetMenuNode>();
		final WorksheetMenuNode action = new WorksheetMenuNode("File", "", "");
		final WorksheetMenuNode evalThis = new WorksheetMenuNode("Open", "", "ui-icon-disk");
		evalThis.setClick("function() {alert('Open')}");
		action.addChild(evalThis);
		this.menu.add(action);
	}
	@XmlElements(value={@XmlElement(name="block")})
	public IBlock[] getBlocks() {
		return this.blocks.toArray(new IBlock[this.blocks.size()]);
	}

	public void setBlocks(final IBlock[] blocks) {
		this.blocks.clear();
		this.blocks.addAll(Arrays.asList(blocks));
	}

	@XmlTransient
	public boolean getHasMenu() {
		return this.hasMenu;
	}

	public void setHasMenu(final boolean hasMenu) {
		this.hasMenu = hasMenu;
	}

	@XmlTransient
	public boolean getHasBody() {
		return this.hasBody;
	}

	public void setHasBody(final boolean hasBody) {
		this.hasBody = hasBody;
	}
	
	@XmlTransient
	public ArrayList<WorksheetMenuNode> getMenu() {
		return this.menu;
	}

	public void setMenu(final ArrayList<WorksheetMenuNode> menu) {
		this.menu = menu;
	}

	@XmlAttribute(name="id")
	@XmlID
	public String getId() {
		return this.id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	/*
	 * Inserts the block at index and shifts the rest
	 */
	@XmlAttribute(name="blockCounter")
	public int getBlockCounter() {
		return blockCounter;
	}


	public void setBlockCounter(int blockCounter) {
		this.blockCounter = blockCounter;
	}

	public void insertBlock(final int index, final IBlock block) {
		assert (this.blockCounter < Integer.MAX_VALUE);

		this.blocks.add(index, block);
		this.blockCounter++;
		if (block.getId() == null) {
			block.setId("ws-block-id-" + this.blockCounter);
		}
	}

	public int getBlockIndex(final IBlock block) {
		final String id = block.getId();
		for (int x = 0; x < this.blocks.size(); x++) {
			if (this.blocks.get(x).getId().equals(id))
				return x;
		}
		return -1;
	}

	public void undoFrom(final int index) {
		for (int x = this.blocks.size() - 1; x >= 0; x--) {
			if (this.blocks.get(x).getOutput()) {
				continue;
			}
			this.blocks.get(x).undo();
		}
	}

	public IBlock[] getBlocksFrom(final int index) {
		final List<IBlock> blocks = this.blocks.subList(index, this.blocks.size());
		return blocks.toArray(new IBlock[blocks.size()]);
	}

	public void markAllAfter(final int blockIndex) {
		for (int x = blockIndex; x < this.blocks.size(); x++) {
			this.blocks.get(x).setMark(true);
		}
	}

	public void setBlock(final int index, final IBlock block) {
		this.blocks.set(index, block);
	}

	public void removeOutputBlocks(final IBlock block) {
		final int blockIndex = this.getBlockIndex(block);
		final String[] outputIds = this.blocks.get(blockIndex).getOutputBlockIds();
		for (final String outputId : outputIds) {
			final IBlock nblock = this.getBlockById(outputId);
			this.blocks.remove(nblock);
		}
		block.setOutputBlockIds(null);
	}

	public IBlock getBlockById(final String id) {
		for (int x = 0; x < this.blocks.size(); x++) {
			if (this.blocks.get(x).getId().equals(id))
				return this.blocks.get(x);
		}
		return null;
	}

	public int getBlockIndexById(final String id) {
		int x = 0;
		for (final IBlock block : this.blocks) {
			if (block.getId().equals(id))
				return x;
			x++;
		}
		return -1;
	}

	/**
	 * @param block
	 */
	public void appendBlock(final IBlock block) {
		this.insertBlock(this.blocks.size(), block);
	}

	/**
	 * @param block
	 */
	public void setBlock(final IBlock block) {
		// find block index
		final int index = this.getBlockIndexById(block.getId());
		if (index == -1) {
			// TODO decide wehter to throw an error or add the new block;
			System.err.println("The block does not exist");
		}
		// set Block
		this.blocks.set(index, block);

		return;
	}

	/**
	 * @param id
	 * @param index
	 */
	public void moveBlockTo(final String id, final int index) {
		final int oldIndex = this.getBlockIndexById(id);
		this.blocks.add(index, this.blocks.remove(oldIndex));
	}

	/**
	 * @param ids
	 * @param index
	 */
	public void moveBlocksTo(final String[] ids, final int index) {
		final IBlock[] blocks = new IBlock[ids.length];
		for (int x = 0; x < ids.length; x++) {
			blocks[x] = this.blocks.remove(this.getBlockIndexById(ids[x]));
		}
		for (int x = blocks.length - 1; x >= 0; x--) {
			this.blocks.add(index, blocks[x]);
		}

	}

}