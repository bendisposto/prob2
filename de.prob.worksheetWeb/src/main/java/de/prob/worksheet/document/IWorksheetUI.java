package de.prob.worksheet.document;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlTransient;

import de.prob.worksheet.document.impl.WorksheetMenuNode;

/**
 * This interface defines the methods necessary for the Worksheet User
 * Interface. It's default implementation is WorksheetDocument
 * 
 * @see de.prob.worksheet.document.impl.WorksheetDocument
 * @author Rene
 * 
 */
public interface IWorksheetUI {

	/**
	 * Getter for the hasMenu Flag
	 * 
	 * @return a boolean flag for hasMenu
	 */
	@XmlTransient
	public abstract boolean getHasMenu();

	/**
	 * Setter for the hasMenu flag
	 * 
	 * @param hasMenu
	 *            flag to be set
	 */
	public abstract void setHasMenu(boolean hasMenu);

	/**
	 * Getter for the hasBody flag
	 * 
	 * @return a boolean for the hasBody flag
	 */
	@XmlTransient
	public abstract boolean getHasBody();

	/**
	 * Setter for the hasBody flag
	 * 
	 * @param hasBody
	 *            flag to be set
	 */
	public abstract void setHasBody(boolean hasBody);

	/**
	 * Getter for the menu list
	 * 
	 * @return an array containing all nodes of this menu
	 */
	@XmlTransient
	public abstract ArrayList<WorksheetMenuNode> getMenu();

	/**
	 * Setter for the menu list
	 * 
	 * @param menu
	 *            array to be set for this document
	 */
	public abstract void setMenu(ArrayList<WorksheetMenuNode> menu);

}