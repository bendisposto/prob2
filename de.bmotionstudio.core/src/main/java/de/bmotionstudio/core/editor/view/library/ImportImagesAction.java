/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.view.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;

import com.google.inject.Injector;

import de.bmotionstudio.core.BMotionImage;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.Trace;
import de.prob.webconsole.ServletContextListener;

public class ImportImagesAction extends AbstractLibraryAction {
	
	private Injector injector = ServletContextListener.INJECTOR;
	
	public ImportImagesAction(LibraryPage page) {
		super(page);
		setText("Import images...");
		setImageDescriptor(BMotionImage.getImageDescriptor(
				"org.eclipse.ui", "$nl$/icons/full/etool16/import_wiz.gif"));
	}

	@Override
	public void run() {

		// The file dialog
		FileDialog fd = new FileDialog(Display.getDefault().getActiveShell(),
				SWT.OPEN | SWT.MULTI);
		fd.setText("Open");
		fd.setFilterPath("C:/");
		String[] filterExt = { "*.jpg", "*.gif", "*.png", "*.*" };
		fd.setFilterExtensions(filterExt);

		fd.open();

		// Source folder path
		String sourceFolderPath = fd.getFilterPath();

		// Selected files
		String[] selectedFileNames = fd.getFileNames();

		try {

			final AnimationSelector selector = injector
					.getInstance(AnimationSelector.class);

			Trace currentTrace = selector.getCurrentTrace();
			if (currentTrace != null) {

				String imagePath = currentTrace.getModel().getModelFile()
						.getParent()
						+ "/images";

				NullProgressMonitor monitor = new NullProgressMonitor();

				// // Iterate the selected files
				for (String fileName : selectedFileNames) {

					String targetImagePath = imagePath + File.separator
							+ fileName;
					IPath path = new Path(targetImagePath);
					IFile targetFile = ResourcesPlugin.getWorkspace().getRoot()
							.getFileForLocation(path);
					if (targetFile == null)
						return;

					IContainer parent = targetFile.getParent();
					if (parent instanceof IFolder) {
						IFolder folder = (IFolder) parent;
						if (!folder.exists())
							folder.create(true, true, monitor);
					}

					File sourceFile = new File(sourceFolderPath
							+ File.separator + fileName);
					FileInputStream fileInputStream = new FileInputStream(
							sourceFile);

					if (!targetFile.exists()) {
						targetFile.create(fileInputStream, true, monitor);
					} else {
						// The file already exists; asks for confirmation
						MessageBox mb = new MessageBox(fd.getParent(),
								SWT.ICON_WARNING | SWT.YES | SWT.NO);
						mb.setMessage(fileName
								+ " already exists. Do you want to replace it?");
						// If they click Yes, we're done and we drop out. If
						// they click No, we redisplay the File Dialog
						if (mb.open() == SWT.YES)
							targetFile.setContents(fileInputStream, true,
									false, monitor);
					}

					try {
						IProject project = targetFile.getProject();
						project.refreshLocal(IResource.DEPTH_INFINITE,
								new NullProgressMonitor());
					} catch (CoreException e) {
						e.printStackTrace();
					}

				}

			}

		} catch (CoreException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		getPage().refresh();

	}

}
