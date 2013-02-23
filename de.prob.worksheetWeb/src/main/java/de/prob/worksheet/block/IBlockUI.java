package de.prob.worksheet.block;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.prob.worksheet.WorksheetMenuNode;

public interface IBlockUI {


	@JsonProperty(value = "worksheetId")
	@XmlAttribute(name = "worksheetId")
	public abstract String getWorksheetId();

	@JsonProperty(value = "worksheetId")
	public abstract void setWorksheetId(String worksheetId);

	@JsonProperty(value = "hasMenu")
	@XmlTransient
	public abstract boolean getHasMenu();
	
	@JsonProperty(value = "hasMenu")
	public abstract void setHasMenu(boolean hasMenu);

	@JsonProperty(value = "menu")
	@XmlTransient
	public abstract WorksheetMenuNode[] getMenu();

	@JsonProperty(value = "menu")
	public abstract void setMenu(WorksheetMenuNode[] menu);

	@JsonProperty(value = "mark")
	@XmlAttribute(name = "mark")
	public abstract boolean getMark();

	@JsonProperty(value = "mark")
	public abstract void setMark(boolean b);



}