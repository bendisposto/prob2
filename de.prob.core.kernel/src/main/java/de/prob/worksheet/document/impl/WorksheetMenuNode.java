package de.prob.worksheet.document.impl;

import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This Class represents a node of a menu which is used for blocks in the
 * worksheet user interface
 * 
 * @author Rene
 * 
 */
public class WorksheetMenuNode {

	/**
	 * The static instance of a slf4j Logger for this class
	 */
	public static final Logger logger = LoggerFactory
			.getLogger(WorksheetMenuNode.class);
	private String text = "";
	private String click = null;
	private String itemClass = "";
	private String iconClass = "";
	private final ArrayList<WorksheetMenuNode> children;
	private char character;
	private boolean title = false;

	/**
	 * Default Constructor
	 */
	public WorksheetMenuNode() {
		WorksheetMenuNode.logger.trace("in:");
		children = new ArrayList<WorksheetMenuNode>();
		WorksheetMenuNode.logger.trace("return:");
	}

	/**
	 * Constructor
	 * 
	 * @param text
	 *            for setText
	 * @param itemClass
	 *            for setItemClass
	 * @param iconClass
	 *            for setIconClass
	 */
	public WorksheetMenuNode(final String text, final String itemClass,
			final String iconClass) {
		WorksheetMenuNode.logger.trace(
				"in: text={}, itemClass={}, iconClass={}", new Object[] { text,
						itemClass, iconClass });
		children = new ArrayList<WorksheetMenuNode>();
		this.text = text;
		this.itemClass = itemClass;
		this.iconClass = iconClass;
		WorksheetMenuNode.logger.trace("return:");
	}

	/**
	 * Returns the visible text for this menupoint
	 * 
	 * @return the menutext
	 */
	public String getText() {
		WorksheetMenuNode.logger.trace("in:");
		WorksheetMenuNode.logger.trace("return: text={}", text);
		return text;
	}

	/**
	 * Set the visible text for this menupoint
	 * 
	 * @param text
	 *            to become menutext
	 */
	public void setText(final String text) {
		WorksheetMenuNode.logger.trace("in: text={}", this.text);
		WorksheetMenuNode.logger.trace("return:");
		this.text = text;
	}

	/**
	 * Returns a javascript string containing the function to execute when the
	 * users clicks the menupoint
	 * 
	 * @return a javascript function string
	 */
	@JsonProperty(value = "click")
	public String getClick() {
		WorksheetMenuNode.logger.trace("in:");
		WorksheetMenuNode.logger.trace("return: click={}", click);
		return click;
	}

	/**
	 * Sets a javascript function string to be executed when the user clicks the
	 * menupoint
	 * 
	 * @param click
	 *            string to be set for this menupoint
	 */
	public void setClick(final String click) {
		WorksheetMenuNode.logger.trace("in: click={}", click);
		WorksheetMenuNode.logger.trace("return:");
		this.click = click;
	}

	/**
	 * Returns the CSS class for this menu item
	 * 
	 * @return CSS class name or names
	 */
	public String getItemClass() {
		WorksheetMenuNode.logger.trace("in:");
		WorksheetMenuNode.logger.trace("return: itemClass={}", itemClass);
		return itemClass;
	}

	/**
	 * Sets the CSS class name(s) for this menu item
	 * 
	 * @param itemClass
	 *            for this menu item
	 */
	public void setItemClass(final String itemClass) {
		WorksheetMenuNode.logger.trace("in: itemClass={}", itemClass);
		this.itemClass = itemClass;
		WorksheetMenuNode.logger.trace("return:");
	}

	/**
	 * Returns a CSS Class String for this menu items icon
	 * 
	 * @return CSS class name
	 */
	public String getIconClass() {
		WorksheetMenuNode.logger.trace("in:");
		WorksheetMenuNode.logger.trace("return: iconClass={}", iconClass);
		return iconClass;
	}

	/**
	 * Sets a CSS Class String for this menuitems icon
	 * 
	 * @param iconClass
	 *            for this menu items icon
	 */
	public void setIconClass(final String iconClass) {
		WorksheetMenuNode.logger.trace("in: iconClass={}", iconClass);
		this.iconClass = iconClass;
		WorksheetMenuNode.logger.trace("return:");
	}

	/**
	 * Returns an array of WorksheetMenuNodes containing the child menu items
	 * for this menu item
	 * 
	 * @return an array of WorksheetMenuNode
	 */
	public WorksheetMenuNode[] getChildren() {
		WorksheetMenuNode.logger.trace("in:");
		WorksheetMenuNode.logger.trace("return: children={}", children);
		return children.toArray(new WorksheetMenuNode[children.size()]);
	}

	/**
	 * Sets an array of WorksheetMenuNode to be the child menu items for this
	 * menu item
	 * 
	 * @param children
	 *            of this node
	 */
	public void setChildren(final WorksheetMenuNode[] children) {
		WorksheetMenuNode.logger.trace("in: children={}", children);
		this.children.clear();
		this.children.addAll(Arrays.asList(children));
		WorksheetMenuNode.logger.trace("return:");
	}

	/**
	 * Adds an WorksheetMenuNode to the child's of this menu item
	 * 
	 * @param child
	 *            to be added
	 */
	public void addChild(final WorksheetMenuNode child) {
		WorksheetMenuNode.logger.trace("in: child={}", child);
		children.add(child);
		WorksheetMenuNode.logger.trace("return:");
	}

	/**
	 * Getter for the shortcut character of this MenuNode
	 * 
	 * @return the shortcut character
	 */
	public char getChar() {
		WorksheetMenuNode.logger.trace("in:");
		WorksheetMenuNode.logger.trace("return: shortcut Char={}", character);
		return character;
	}

	/**
	 * Setter for the shortcut character of this MenuNode
	 * 
	 * @param character
	 *            the character to use for the shortcut
	 */
	public void setChar(char character) {
		WorksheetMenuNode.logger.trace("in: shortcut char={}", character);
		this.character = character;
		WorksheetMenuNode.logger.trace("return:");
	}

	/**
	 * Getter for a flag which tells if this menu node is a title node. Title
	 * nodes don't have a shortcut and don't perform an action if selected
	 * 
	 * @return if this menu node is a title node
	 */
	public boolean isTitle() {
		WorksheetMenuNode.logger.trace("in:");
		WorksheetMenuNode.logger.trace("return: isTitle={}", title);
		return title;
	}

	/**
	 * Setter for a flag which tells if this menu node is a title node. Title
	 * nodes don't have a shortcut and don't perform an action if selected
	 * 
	 * @param title
	 *            flag for this node
	 */
	public void setTitle(boolean title) {
		WorksheetMenuNode.logger.trace("in: isTitle={}", title);
		this.title = title;
		WorksheetMenuNode.logger.trace("return:");
	}
}
