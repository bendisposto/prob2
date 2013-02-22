package de.bmotionstudio.core.editor.action;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.actions.ActionFactory;

import com.thoughtworks.xstream.XStream;

import de.bmotionstudio.core.BMotionEditorPlugin;
import de.bmotionstudio.core.BMotionImage;
import de.bmotionstudio.core.model.VisualizationView;

public class SaveAction extends Action {

	public static String ID = "de.bmotionstudio.core.actions.saveVisualizationViewAction";
	
	private VisualizationView visualizationView;
	
	private IFile visualizationFile;

	public SaveAction(VisualizationView visualizationView,
			IFile visualizationFile) {
		setId(ActionFactory.SAVE.getId());
		setText("Save");
		setToolTipText("Save");
		setImageDescriptor(BMotionImage.getImageDescriptor("org.eclipse.ui",
				"$nl$/icons/full/etool16/save_edit.gif"));
		this.visualizationView = visualizationView;
		this.visualizationFile = visualizationFile;
	}
	
	@Override
	public void run() {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		OutputStreamWriter writer = null;
		try {
			// saveProperties();
			writer = new OutputStreamWriter(out, "UTF8");
			XStream xstream = new XStream();
			BMotionEditorPlugin.setAliases(xstream);
			xstream.toXML(visualizationView, writer);
			visualizationFile.setContents(
					new ByteArrayInputStream(out.toByteArray()), true, false,
					new NullProgressMonitor());
			visualizationView.setDirty(false);
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
