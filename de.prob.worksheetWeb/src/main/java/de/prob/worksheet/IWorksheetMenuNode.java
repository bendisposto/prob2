package de.prob.worksheet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * This interface represents the central element of an hierarchical menu
 * 
 * @author Rene
 * 
 */
@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "@type")
@JsonSubTypes({ @Type(value = WorksheetMenuNode.class, name = "default") })
public interface IWorksheetMenuNode {

	/**
	 * Returns the visible text for this menupoint
	 * 
	 * @return the menutext
	 */
	public abstract String getText();

	/**
	 * Set the visible text for this menupoint
	 * 
	 * @param text
	 *            to become menutext
	 */
	public abstract void setText(String text);

	/**
	 * Returns a javascript string containing the function to execute when the
	 * users clicks the menupoint
	 * 
	 * @return a javascript function string
	 */
	@JsonProperty(value = "click")
	public abstract String getClick();

	/**
	 * Sets a javascript function string to be executed when the user clicks the
	 * menupoint
	 * 
	 * @param function
	 *            string to be set for this menupoint
	 */
	public abstract void setClick(String function);

	/**
	 * Returns the CSS class for this menu item
	 * 
	 * @return CSS class name or names
	 */
	public abstract String getItemClass();

	/**
	 * Sets the CSS class name(s) for this menu item
	 * 
	 * @param itemClass
	 *            for this menuitem
	 */
	public abstract void setItemClass(String itemClass);

	/**
	 * Returns a CSS Class String for this menuitems icon
	 * 
	 * @return CSS class name
	 */
	public abstract String getIconClass();

	/**
	 * Sets a CSS Class String for this menuitems icon
	 * 
	 * @param iconClass
	 *            for this menuitems icon
	 */
	public abstract void setIconClass(String iconClass);

	/**
	 * Returns an array of IWorksheetMenuNodes containing the child menuitems
	 * for this menuitem
	 * 
	 * @return an array of IWorksheetMenuNode
	 */
	public abstract IWorksheetMenuNode[] getChildren();

	/**
	 * Sets an array of IWorksheetMenuNode to be the child menuitems for this
	 * menuitem
	 * 
	 * @param children
	 *            of this node
	 */
	public abstract void setChildren(IWorksheetMenuNode[] children);

	/**
	 * Adds an IWorksheetMenuNode to the childs of this menuitem
	 * 
	 * @param child
	 *            to be added
	 */
	public void addChild(IWorksheetMenuNode child);

}