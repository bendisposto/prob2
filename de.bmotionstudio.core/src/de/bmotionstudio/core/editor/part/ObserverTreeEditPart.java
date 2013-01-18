/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.part;

import de.bmotionstudio.core.BMotionImage;
import de.bmotionstudio.core.model.observer.Observer;

public class ObserverTreeEditPart extends BMSAbstractTreeEditPart {

	@Override
	public void refreshVisuals() {
		Observer o = (Observer) getModel();
		setWidgetText(o.getName());
		setWidgetImage(BMotionImage
				.getImage(BMotionImage.IMG_ICON_OBSERVER));
	}

}
