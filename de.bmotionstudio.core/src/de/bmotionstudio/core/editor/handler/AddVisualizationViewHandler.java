/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.handler;

import java.util.UUID;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PartInitException;

import de.bmotionstudio.core.BMotionStudio;
import de.bmotionstudio.core.editor.VisualizationViewPart;
import de.bmotionstudio.core.model.Simulation;
import de.bmotionstudio.core.model.VisualizationView;
import de.bmotionstudio.core.model.control.Visualization;
import de.bmotionstudio.core.util.PerspectiveUtil;

public class AddVisualizationViewHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {

		// TODO This handler should only be enabled if a simulation or
		// perspective respectively is open!!!

		Simulation simulation = BMotionStudio.getCurrentSimulation();
		IPerspectiveDescriptor perspective = BMotionStudio
				.getCurrentPerspective();

		if (simulation != null && perspective != null) {

			PerspectiveUtil.switchPerspective(perspective);

			try {

				String secId = UUID.randomUUID().toString();
				Visualization visualization = new Visualization();
				VisualizationView visualizationView = new VisualizationView(
						"New Visualization View", secId, visualization);
				simulation.addVisualizationView(visualizationView);
				
				VisualizationViewPart visualizationViewPart = PerspectiveUtil
						.createVisualizationViewPart(secId, visualizationView);
				visualizationViewPart.init(simulation, visualizationView);

				simulation.setDirty(true);

			} catch (PartInitException e1) {
				e1.printStackTrace();
			}

		} else {
			// TODO: Throw some exception!?!
		}

		return null;

	}
	
}
