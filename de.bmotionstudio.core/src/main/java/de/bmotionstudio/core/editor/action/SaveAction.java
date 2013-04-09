package de.bmotionstudio.core.editor.action;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.actions.ActionFactory;

import com.thoughtworks.xstream.XStream;

import de.bmotionstudio.core.BMotionImage;
import de.bmotionstudio.core.model.VisualizationView;
import de.bmotionstudio.core.util.BMotionUtil;

public class SaveAction extends Action {

	public static String ID = "de.bmotionstudio.core.actions.saveVisualizationViewAction";

	private VisualizationView visualizationView;

	private File visualizationFile;

	public SaveAction(VisualizationView visualizationView,
			File visualizationFile) {
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
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(this.visualizationFile);
			XStream xstream = new XStream();
			BMotionUtil.setAliases(xstream);
			xstream.toXML(visualizationView, fileWriter);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (fileWriter != null)
					fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
