/**
 * 
 */
package de.prob.worksheet.api;

/**
 * @author Rene
 * 
 */
public interface IWorksheetEvent {
	public int getId();

	public void setId(int id);

	public void setName(String name);

	public String getName();

	public String getDescription();

	public void setDescription(String description);
}
