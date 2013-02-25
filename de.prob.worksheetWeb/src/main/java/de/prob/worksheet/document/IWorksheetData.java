package de.prob.worksheet.document;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;

import de.prob.worksheet.block.impl.DefaultBlock;

public interface IWorksheetData {

	/**
	 * Getter for this documents blocks
	 * 
	 * @return an array containing all blocks of this document
	 */
	@XmlElements(value = { @XmlElement(name = "block") })
	public abstract DefaultBlock[] getBlocks();

	/**
	 * Setter for this documents blocks
	 * 
	 * @param blocks
	 *            array to set for this document
	 */
	public abstract void setBlocks(DefaultBlock[] blocks);

	/**
	 * Getter for the id of this document
	 * 
	 * @return id of this document
	 */
	@XmlAttribute(name = "id")
	@XmlID
	public abstract String getId();

	/**
	 * Setter for the id of this document
	 * 
	 * @param id
	 *            to be set for this document
	 */
	public abstract void setId(String id);

	/**
	 * Getter for the blockCounter of this document
	 * 
	 * @return the blockcounter
	 */
	@XmlAttribute(name = "blockCounter")
	public abstract int getBlockCounter();

	/**
	 * Setter for the blockCounter of this document
	 * 
	 * @param blockCounter
	 *            to be set
	 */
	public abstract void setBlockCounter(int blockCounter);

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
	public abstract void insertBlock(int index, DefaultBlock block);

	/**
	 * Returns the index of a specified block or -1 if the block is not found
	 * 
	 * @param block
	 *            to get the index for
	 * @return the index of the block
	 */
	public abstract int getBlockIndex(DefaultBlock block);

	/**
	 * Sets the block at index. The block which had been at index before setting
	 * is removed
	 * 
	 * @param index
	 * @param block
	 */
	public abstract void setBlock(int index, DefaultBlock block);

	/**
	 * Returns the block for the given id
	 * 
	 * @param id
	 *            to return the block for
	 * @return the block with the given id
	 */
	public abstract DefaultBlock getBlockById(String id);

	/**
	 * Returns the index of the block with given id or -1 if no block with the
	 * give id exists
	 * 
	 * @param id
	 *            of the block to retrieve the index for
	 * @return the index of the block
	 */
	public abstract int getBlockIndexById(String id);

	/**
	 * Appends the block to the end of the document
	 * 
	 * @param block
	 *            to append
	 */
	public abstract void appendBlock(DefaultBlock block);

	/**
	 * Sets the block with block.id to the new Block. If no block exists in the
	 * document with block id the new block is not set
	 * 
	 * @param block
	 *            to set
	 * @return the index of the set block or -1 if the block is not set
	 */
	public abstract int setBlock(DefaultBlock block);

	/**
	 * Returns whether the given block is the last one in the document.
	 * 
	 * @param block
	 *            to test
	 * @return true if the block is the last block of the document else false
	 */
	public abstract boolean isLastBlock(DefaultBlock block);

	/**
	 * Returns the first block in the document
	 * 
	 * @return
	 */
	public abstract DefaultBlock getFirst();

	public abstract void removeBlock(DefaultBlock block);

}