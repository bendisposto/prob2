package de.prob.worksheet;

import java.util.ArrayList;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is the default implementation of the IWorksheetMenuNode
 * 
 * @author Rene
 * 
 */
public class WorksheetMenuNode {
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
		this.children = new ArrayList<WorksheetMenuNode>();
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
		this.children = new ArrayList<WorksheetMenuNode>();
		this.text = text;
		this.itemClass = itemClass;
		this.iconClass = iconClass;
	}

	/**
	 * Returns the visible text for this menupoint
	 * 
	 * @return the menutext
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * Set the visible text for this menupoint
	 * 
	 * @param text
	 *            to become menutext
	 */
	public void setText(final String text) {
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
		return this.click;
	}

	/**
	 * Sets a javascript function string to be executed when the user clicks the
	 * menupoint
	 * 
	 * @param function
	 *            string to be set for this menupoint
	 */
	public void setClick(final String click) {
		this.click = click;
	}

	/**
	 * Returns the CSS class for this menu item
	 * 
	 * @return CSS class name or names
	 */
	public String getItemClass() {
		return this.itemClass;
	}

	/**
	 * Sets the CSS class name(s) for this menu item
	 * 
	 * @param itemClass
	 *            for this menu item
	 */
	public void setItemClass(final String itemClass) {
		this.itemClass = itemClass;
	}

	/**
	 * Returns a CSS Class String for this menu items icon
	 * 
	 * @return CSS class name
	 */
	public String getIconClass() {
		return this.iconClass;
	}

	/**
	 * Sets a CSS Class String for this menuitems icon
	 * 
	 * @param iconClass
	 *            for this menu items icon
	 */
	public void setIconClass(final String iconClass) {
		this.iconClass = iconClass;
	}

	/**
	 * Returns an array of WorksheetMenuNodes containing the child menu items
	 * for this menu item
	 * 
	 * @return an array of WorksheetMenuNode
	 */
	public WorksheetMenuNode[] getChildren() {
		return this.children
				.toArray(new WorksheetMenuNode[this.children.size()]);
	}

	/**
	 * Sets an array of WorksheetMenuNode to be the child menu items for this
	 * menu item
	 * 
	 * @param children
	 *            of this node
	 */
	public void setChildren(final WorksheetMenuNode[] children) {
		this.children.clear();
		this.children.addAll(Arrays.asList(children));
	}

	/**
	 * Adds an WorksheetMenuNode to the child's of this menu item
	 * 
	 * @param child
	 *            to be added
	 */
	public void addChild(final WorksheetMenuNode child) {
		this.children.add(child);
	}

	/**
	 * Getter for the shortcut character of this MenuNode
	 * 
	 * @return the shortcut character
	 */
	public char getChar() {
		return this.character;
	}

	/**
	 * Setter for the shortcut character of this MenuNode
	 * 
	 * @param character
	 *            the character to use for the shortcut
	 */
	public void setChar(char character) {
		this.character = character;
	}

	/**
	 * Getter for a flag which tells if this menu node is a title node. Title
	 * nodes don't have a shortcut and don't perform an action if selected
	 * 
	 * @return if this menu node is a title node
	 */
	public boolean isTitle() {
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
		this.title = title;
	}
}
