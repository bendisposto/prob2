/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.property;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.google.inject.Injector;

import de.bmotionstudio.core.BMotionImage;
import de.bmotionstudio.core.editor.view.library.LibraryImageObject;
import de.bmotionstudio.core.editor.view.library.LibraryObject;
import de.prob.statespace.AnimationSelector;
import de.prob.webconsole.ServletContextListener;

public class ImageDialog extends Dialog {

	private TableViewer tvLibrary;
	private Image previewImage;
	private Composite preContainer;
	private Canvas previewCanvas;
	private final ImageDialogCellEditor imageDialogCellEditor;

	private Injector injector = ServletContextListener.INJECTOR;
	
	protected ImageDialog(final Shell parentShell,
			final ImageDialogCellEditor imageDialogCellEditor) {
		super(parentShell);
		this.imageDialogCellEditor = imageDialogCellEditor;
	}

	@Override
	protected Control createDialogArea(final Composite parent) {

		Composite container = (Composite) super.createDialogArea(parent);

		GridLayout gl = new GridLayout(1, true);
		gl.horizontalSpacing = 0;
		container.setLayout(gl);

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalIndent = 0;

		preContainer = new Composite(container, SWT.NONE);
		preContainer.setLayoutData(gd);
		preContainer.setLayout(new FillLayout());

		previewCanvas = new Canvas(preContainer, SWT.BORDER);
		previewCanvas.addPaintListener(new PaintListener() {
			public void paintControl(final PaintEvent e) {
				if (previewImage == null) {
					e.gc.drawString("No image selected ...", 0, 0);
				} else {
					e.gc.drawImage(previewImage, 0, 0);
				}
			}
		});

		final Composite libContainer = new Composite(container, SWT.NONE);
		libContainer.setLayoutData(gd);
		libContainer.setLayout(new FillLayout());

		tvLibrary = new TableViewer(libContainer, SWT.FULL_SELECTION
				| SWT.V_SCROLL);
		tvLibrary.getTable().setLayoutData(gd);
		tvLibrary.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(final SelectionChangedEvent event) {

				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();

				LibraryObject obj = (LibraryObject) selection.getFirstElement();

				if (previewImage != null) {
					previewImage.dispose();
				}

				previewImage = null;

				if (obj != null) {
					String imageName = obj.getName();
					if (!imageName.equals("noimage")) {
						
						final AnimationSelector selector = injector
								.getInstance(AnimationSelector.class);

						String imagePath = selector.getCurrentTrace()
								.getModel().getModelFile().getParent()
								+ "/images";

						String myPath = imagePath + File.separator + imageName;
						previewImage = new Image(Display.getDefault(), myPath);

					}
				}

				previewCanvas.redraw();

			}

		});

		ObservableListContentProvider contentProvider = new ObservableListContentProvider();
		tvLibrary.setContentProvider(contentProvider);

		tvLibrary.getTable().setLinesVisible(true);
		tvLibrary.getTable().setHeaderVisible(true);

		final TableViewerColumn column1 = new TableViewerColumn(tvLibrary,
				SWT.NONE);
		column1.getColumn().setText("Name");
		column1.getColumn().setWidth(390);
		column1.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				cell.setText(((LibraryObject) cell.getElement()).getName());
				cell.setImage(((LibraryObject) cell.getElement()).getImage());
			}
		});

		final TableViewerColumn column2 = new TableViewerColumn(tvLibrary,
				SWT.NONE);
		column2.getColumn().setText("Type");
		column2.getColumn().setWidth(60);
		column2.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				cell.setText(((LibraryObject) cell.getElement()).getType());
			}
		});

		WritableList input = new WritableList(getLibraryObjects(),
				LibraryObject.class);
		tvLibrary.setInput(input);

		return container;

	}

	private List<LibraryObject> getLibraryObjects() {

		List<LibraryObject> tmpList = new ArrayList<LibraryObject>();
		tmpList.add(new LibraryObject("noimage", "", BMotionImage
				.getImageDescriptor("org.eclipse.ui",
						"$nl$/icons/full/etool16/delete_edit.gif")
				.createImage()));
				
		final AnimationSelector selector = injector
				.getInstance(AnimationSelector.class);
		
		String imagePath = selector.getCurrentTrace().getModel().getModelFile().getParent() + "/images";
		
		File dir = new File(imagePath);
		File[] fileList = dir.listFiles(new FilenameFilter() {
			public boolean accept(final File dir, final String name) {
				if (name.toLowerCase().endsWith(".jpg")
						|| name.toLowerCase().endsWith(".gif")
						|| name.toLowerCase().endsWith(".png")) {
					return true;
				}
				return false;
			}
		});
		if (fileList != null) {
			for (File f : fileList) {
				Image img = null;
				if (f.getName().toLowerCase().endsWith(".jpg")) {
					img = BMotionImage.getImage(BMotionImage.IMG_ICON_JPG);
				} else {
					img = BMotionImage.getImage(BMotionImage.IMG_ICON_GIF);
				}
				tmpList.add(new LibraryImageObject(f.getName(), "image", img));
			}
		}

		return tmpList;

	}

	LibraryObject getSelectedObject() {
		IStructuredSelection sel = (IStructuredSelection) tvLibrary
				.getSelection();
		LibraryObject lobj = (LibraryObject) sel.getFirstElement();
		return lobj;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(500, 500);
	}

	@Override
	protected void okPressed() {
		LibraryObject sel = getSelectedObject();
		if (sel != null) {
			if (!sel.getName().equals("noimage")) {
				this.imageDialogCellEditor.setValue(sel.getName());
			} else {
				this.imageDialogCellEditor.setValue("");
			}

		}
		close();
	}

	@Override
	protected void configureShell(final Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("BMotion Studio - Select image dialog");
	}

}
