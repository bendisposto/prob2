package de.prob.worksheet.document;

import de.prob.worksheet.block.IBlockData;
import de.prob.worksheet.block.impl.DefaultBlock;

public interface IWorksheetEvaluate {

	/**
	 * Returns all blocks from index to blocks.length
	 * 
	 * @param index
	 *            for the first block to return
	 * @return an array containing all blocks from index to blocks.length
	 */
	public abstract DefaultBlock[] getBlocksFrom(int index);

	/**
	 * Marks all blocks from index to blocks.length. A marked block isn't
	 * evaluated so his content could be incorrect
	 * 
	 * @param blockIndex
	 */
	public abstract void markAllAfter(int index);

	/**
	 * Removes all Output Blocks for the given Input BLock
	 * 
	 * @param block
	 *            to remove the output blocks for
	 */
	public abstract void removeOutputBlocks(IBlockData block);

	/**
	 * Moves the block with id to index
	 * 
	 * @param id
	 *            of the block to move
	 * @param index
	 *            to move the block to
	 */
	public abstract void moveBlockTo(String id, int index);

	/**
	 * Moves the blocks with the ids to the positions starting at index
	 * 
	 * @param ids
	 *            of the blocks to move
	 * @param index
	 *            of the starting pos to move the blocks to
	 */
	public abstract void moveBlocksTo(String[] ids, int index);

	/**
	 * Changes the type of the block with id to newBlock. The new block gets the
	 * id of the old block
	 * 
	 * @param id
	 *            of the block to switch
	 * @param newBlock
	 *            to set
	 */
	public abstract void switchBlockType(String id, DefaultBlock newBlock);

	/**
	 * Appends the output blocks to the given block and sets the output ids to
	 * the block
	 * 
	 * @param block
	 *            to append the output blocks to
	 * @param blocks
	 *            to append
	 */
	public abstract void insertOutputBlocks(DefaultBlock block,
			DefaultBlock[] blocks);

	/**
	 * Returns all blocks from block to the end of the document
	 * 
	 * @param block
	 *            which is the first one to be retrieve
	 * @return an array of blocks
	 */
	public abstract IBlockData[] getBlocksFrom(IBlockData block);

}