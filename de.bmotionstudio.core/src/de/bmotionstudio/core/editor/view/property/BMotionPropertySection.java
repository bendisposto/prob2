package de.bmotionstudio.core.editor.view.property;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertySheetEntry;
import org.eclipse.ui.views.properties.tabbed.AdvancedPropertySection;

public class BMotionPropertySection extends AdvancedPropertySection {
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {

		super.setInput(part, selection);
		IPropertySheetEntry propertySheetEntry = (IPropertySheetEntry) part
				.getAdapter(IPropertySheetEntry.class);
		page.setRootEntry(propertySheetEntry);

	}

}
