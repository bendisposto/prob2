/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;

import de.bmotionstudio.core.BMotionImage;

public class AddVisualizationViewAction extends Action {

	private TreeViewer viewer;

	public AddVisualizationViewAction(TreeViewer viewer) {
		this.viewer = viewer;
		setText("Add View");
		setImageDescriptor(BMotionImage.getImageDescriptor(
				"org.eclipse.ui", "$nl$/icons/full/etool16/new_wiz.gif"));
	}

	@Override
	public void run() {

		// TODO: Reimplement me!!!
//		IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
//		Object firstElement = sel.getFirstElement();
//		if (firstElement instanceof Simulation) {
//
//			Simulation simulation = (Simulation) firstElement;
//
//			PerspectiveUtil.openPerspective(simulation);
//
//			try {
//
//				String secId = UUID.randomUUID().toString();
//				// Create a new visualization
//				// String version = Platform
//				// .getBundle(BMotionEditorPlugin.PLUGIN_ID).getHeaders()
//				// .get("Bundle-Version");
//				Visualization visualization = new Visualization(simulation
//						.getProjectFile().getName(), "EventB");
//
//				VisualizationView visualizationView = new VisualizationView(
//						"New Visualization View", secId, visualization);
//				simulation.getVisualizationViews()
//						.put(secId, visualizationView);
//
//				VisualizationViewPart visualizationViewPart = PerspectiveUtil
//						.createVisualizationViewPart(secId,
//						visualizationView);
//				visualizationViewPart.init(simulation, visualizationView);
//				
//				simulation.setDirty(true);
//				viewer.refresh();
//
//			} catch (PartInitException e1) {
//				e1.printStackTrace();
//			}
//
//		}

	}

}
