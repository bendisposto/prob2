/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.view.controlpanel;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;

import de.bmotionstudio.core.BMotionImage;

public class CloseSimulationAction extends Action {

	private TreeViewer viewer;

	public CloseSimulationAction(TreeViewer viewer) {
		this.viewer = viewer;
		setText("Close Simulation");
		setImageDescriptor(BMotionImage.getImageDescriptor(
				"org.eclipse.ui", "$nl$/icons/full/dlcl16/close_view.gif"));
	}

	@Override
	public void run() {

		// TODO: Reimplement me!!!
//		IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
//		Object firstElement = sel.getFirstElement();
//		if (firstElement instanceof Simulation) {
//
//			IPerspectiveRegistry perspectiveRegistry = PlatformUI
//					.getWorkbench().getPerspectiveRegistry();
//
//			Simulation simulation = (Simulation) firstElement;
//			String perspectiveId = PerspectiveUtil
//					.getPerspectiveIdFromFile(simulation.getProjectFile());
//			IPerspectiveDescriptor perspectiveDescriptor = perspectiveRegistry
//					.findPerspectiveWithId(perspectiveId);
//			if (perspectiveDescriptor != null) {
//				PerspectiveUtil.closePerspective(perspectiveDescriptor);
//				PerspectiveUtil.deletePerspective(perspectiveDescriptor);
//			}
//
//			// simulation.stop();
//			BMotionEditorPlugin.closeSimulation(simulation);
//
//		}

	}

}
