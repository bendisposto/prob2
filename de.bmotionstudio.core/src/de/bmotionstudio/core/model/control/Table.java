/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.model.control;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.geometry.Rectangle;

import de.bmotionstudio.core.editor.command.CreateCommand;
import de.bmotionstudio.core.model.attribute.AbstractAttribute;
import de.bmotionstudio.core.model.attribute.BAttributeColumns;
import de.bmotionstudio.core.model.attribute.BAttributeForegroundColor;
import de.bmotionstudio.core.model.attribute.BAttributeHeight;
import de.bmotionstudio.core.model.attribute.BAttributeRows;
import de.bmotionstudio.core.model.attribute.BAttributeSize;
import de.bmotionstudio.core.model.attribute.BAttributeWidth;

public class Table extends BControl {

	public Table() {

		super();
		
		int numberOfColumns = 1;
		int numberOfRows = 1;

		CreateCommand cmd;
		for (int i = 0; i < numberOfColumns; i++) {
			TableColumn bTableColumn = new TableColumn();
			cmd = new CreateCommand(bTableColumn, this);
			cmd.setLayout(new Rectangle(0, 0, 50, 25));
			cmd.execute();
			for (int z = 0; z < numberOfRows; z++) {
				cmd = new CreateCommand(new TableCell(),
						bTableColumn);
				cmd.setLayout(new Rectangle(0, 0, 50, 20));
				cmd.execute();
			}
		}

	}

	@Override
	public boolean canHaveChildren() {
		return true;
	}

	@Override
	protected void initAttributes() {

		BAttributeSize aSize = new BAttributeSize(null);
		aSize.setGroup(AbstractAttribute.ROOT);
		aSize.setShow(false);
		aSize.setEditable(false);
		initAttribute(aSize);

		BAttributeHeight aHeight = new BAttributeHeight(0);
		aHeight.setGroup(BAttributeSize.ID);
		aHeight.setShow(false);
		aHeight.setEditable(false);
		initAttribute(aHeight);

		BAttributeWidth aWidth = new BAttributeWidth(0);
		aWidth.setGroup(BAttributeSize.ID);
		aWidth.setShow(false);
		aWidth.setEditable(false);
		initAttribute(aWidth);

		initAttribute(new BAttributeForegroundColor(
				ColorConstants.black.getRGB()));
		initAttribute(new BAttributeColumns(1));
		initAttribute(new BAttributeRows(1));

	}
	
}
