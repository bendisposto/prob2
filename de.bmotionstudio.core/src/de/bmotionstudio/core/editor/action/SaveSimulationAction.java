/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.action;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.PlatformUI;

import com.thoughtworks.xstream.XStream;

import de.bmotionstudio.core.BMotionEditorPlugin;
import de.bmotionstudio.core.model.Simulation;
import de.bmotionstudio.core.util.PerspectiveUtil;

public class SaveSimulationAction extends Action {

	private Simulation simulation;
	
	private IFile projectFile;
	
	public SaveSimulationAction(Simulation simulation, IFile projectFile) {
		this.simulation = simulation;
		this.projectFile = projectFile;
	}

	@Override
	public void run() {

		if (this.simulation == null || this.projectFile == null)
			return;

		IPerspectiveRegistry perspectiveRegistry = PlatformUI.getWorkbench()
				.getPerspectiveRegistry();

		String perspectiveId = PerspectiveUtil
				.getPerspectiveIdFromFile(projectFile);
		IPerspectiveDescriptor perspectiveDescriptor = perspectiveRegistry
				.findPerspectiveWithId(perspectiveId);
		if (perspectiveDescriptor != null) {
			IFile perspectiveFile = projectFile.getProject().getFile(
					PerspectiveUtil.getPerspectiveFileName(projectFile));
			PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().savePerspectiveAs(perspectiveDescriptor);
			PerspectiveUtil.exportPerspective(perspectiveDescriptor,
					perspectiveFile);
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		OutputStreamWriter writer = null;
		try {
			// saveProperties();
			writer = new OutputStreamWriter(out, "UTF8");
			XStream xstream = new XStream();
			BMotionEditorPlugin.setAliases(xstream);
			xstream.toXML(simulation, writer);
			projectFile.setContents(
					new ByteArrayInputStream(out.toByteArray()), true, false,
					new NullProgressMonitor());
			// getCommandStack().markSaveLocation();
			simulation.setDirty(false);
		} catch (CoreException ce) {
			ce.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				out.close();
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
