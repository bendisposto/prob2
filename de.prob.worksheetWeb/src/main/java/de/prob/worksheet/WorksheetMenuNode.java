package de.prob.worksheet;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This is the default implementation of the IWorksheetMenuNode
 * 
 * @author Rene
 * 
 */
public class WorksheetMenuNode implements IWorksheetMenuNode {
	private String text = "";
	private String click = null;
	private String itemClass = "";
	private String iconClass = "";
	private final ArrayList<IWorksheetMenuNode> children;

	/**
	 * Default Constructor
	 */
	public WorksheetMenuNode() {
		this.children = new ArrayList<IWorksheetMenuNode>();
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
		this.children = new ArrayList<IWorksheetMenuNode>();
		this.text = text;
		this.itemClass = itemClass;
		this.iconClass = iconClass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetMenuNode#getText()
	 */
	@Override
	public String getText() {
		return this.text;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetMenuNode#setText(java.lang.String)
	 */
	@Override
	public void setText(final String text) {
		this.text = text;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetMenuNode#getClick()
	 */
	@Override
	public String getClick() {
		return this.click;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetMenuNode#setClick(java.lang.String)
	 */
	@Override
	public void setClick(final String click) {
		this.click = click;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetMenuNode#getItemClass()
	 */
	@Override
	public String getItemClass() {
		return this.itemClass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetMenuNode#setItemClass(java.lang.String)
	 */
	@Override
	public void setItemClass(final String itemClass) {
		this.itemClass = itemClass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetMenuNode#getIconClass()
	 */
	@Override
	public String getIconClass() {
		return this.iconClass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetMenuNode#setIconClass(java.lang.String)
	 */
	@Override
	public void setIconClass(final String iconClass) {
		this.iconClass = iconClass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetMenuNode#getChildren()
	 */
	@Override
	public IWorksheetMenuNode[] getChildren() {
		return this.children
				.toArray(new WorksheetMenuNode[this.children.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetMenuNode#setChildren(de.prob.worksheet.
	 * WorksheetMenuNode[])
	 */
	@Override
	public void setChildren(final IWorksheetMenuNode[] children) {
		this.children.clear();
		this.children.addAll(Arrays.asList(children));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetMenuNode#addChild(de.prob.worksheet.
	 * IWorksheetMenuNode)
	 */
	@Override
	public void addChild(final IWorksheetMenuNode child) {
		this.children.add(child);
	}
}
