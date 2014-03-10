/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

public class PerspectiveFactory implements IPerspectiveFactory {

	public static final String PROB_PERSPECTIVE = "de.prob.ui.perspective";

	@Override
	public void createInitialLayout(final IPageLayout layout) {
		final String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);

		// LEFT ---------------------------------
		// Place the project explorer to left of editor area.
		final IFolderLayout left = layout.createFolder("left",
				IPageLayout.LEFT, 0.15f, editorArea);
		left.addView("de.prob.ui.OperationView");

		final IFolderLayout leftb = layout.createFolder("leftb",
				IPageLayout.BOTTOM, 0.65f, "left");
		leftb.addView("fr.systerel.explorer.navigator.view");
		leftb.addView("org.eventb.ui.views.RodinProblemView");
		// ---------------------------------

		// MAIN ---------------------------------
		// Properties view + observer view + control panel
		IFolderLayout bottom1 = layout.createFolder("bottom1",
				IPageLayout.BOTTOM, 0.65f, editorArea);
		bottom1.addView("de.prob.ui.StateView");
		bottom1.addView("de.prob.ui.AnimationsView");
		bottom1.addView(IPageLayout.ID_PROP_SHEET);
		// bottom1.addView("de.prob.ui.EventErrorView");

		// Place the outline to right of editor area.
		final IFolderLayout main1 = layout.createFolder("main1",
				IPageLayout.BOTTOM, 0.5f, editorArea);
		main1.addView("de.prob.ui.HistoryView");
		// right1.addView("de.prob.ui.ltl.CounterExampleView");
		// Placeholder for new visualization views

		// ---------------------------------

	}

	public static void openPerspective() {
		try {
			final IWorkbench workbench = PlatformUI.getWorkbench();
			workbench.showPerspective(PROB_PERSPECTIVE,
					workbench.getActiveWorkbenchWindow());
			workbench.getActiveWorkbenchWindow().addPerspectiveListener(
					new PerspectiveListener());
		} catch (final WorkbenchException e) {
			// final String message = "Error opening ProB perspective.";
			// FIXME add Error handling
		}
	}

}
