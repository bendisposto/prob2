package de.prob.worksheet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "@type")
@JsonSubTypes({ @Type(value = WorksheetMenuNode.class, name = "default") })
public interface IWorksheetMenuNode {

	public abstract String getText();

	public abstract void setText(String text);

	@JsonProperty(value = "click")
	public abstract String getClick();

	public abstract void setClick(String click);

	public abstract String getItemClass();

	public abstract void setItemClass(String itemClass);

	public abstract String getIconClass();

	public abstract void setIconClass(String iconClass);

	public abstract IWorksheetMenuNode[] getChildren();

	public abstract void setChildren(IWorksheetMenuNode[] children);

	public void addChild(IWorksheetMenuNode child);

}