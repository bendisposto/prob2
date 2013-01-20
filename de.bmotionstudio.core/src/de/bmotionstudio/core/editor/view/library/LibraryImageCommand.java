/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.view.library;

import java.io.File;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import de.bmotionstudio.core.AttributeConstants;
import de.bmotionstudio.core.BMotionStudio;
import de.bmotionstudio.core.editor.command.CreateCommand;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.control.BImage;

public class LibraryImageCommand extends AbstractLibraryCommand {

	private BControl newImageControl;

	public void execute() {

		attributeName = AttributeConstants.ATTRIBUTE_IMAGE;
		attributeValue = transferObject.getLibraryObject().getName();
		oldAttributeValue = getCastedModel().getAttributeValue(attributeName);

		if (getCastedModel().canHaveChildren()) {

			newImageControl = new BImage();
			newImageControl.setAttributeValue(attributeName, attributeValue);

			CreateCommand createCommand = new CreateCommand(newImageControl,
					getCastedModel());

			String imagePath = attributeValue.toString();

			org.eclipse.swt.graphics.Rectangle imageBounds = null;
			Image img = null;

			Rectangle fRect = new Rectangle(getDropLocation().x,
					getDropLocation().y, 100, 100);

			final String myPath = BMotionStudio.getImagePath() + File.separator
					+ imagePath;

			if (new File(myPath).exists() && imagePath.length() > 0) {
				img = new Image(Display.getCurrent(), myPath);
				imageBounds = img.getBounds();
			}

			if (imageBounds != null) {
				fRect.width = imageBounds.width;
				fRect.height = imageBounds.height;
			}

			createCommand.setLayout(fRect);
			createCommand.execute();

		} else {

			if (getCastedModel().hasAttribute(
					AttributeConstants.ATTRIBUTE_IMAGE))
				getCastedModel().setAttributeValue(attributeName,
						attributeValue);

		}

	}

	public void undo() {
		super.undo();
		getCastedModel().removeChild(newImageControl);
	}

}
