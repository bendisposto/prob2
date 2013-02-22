/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.handler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import de.bmotionstudio.core.util.BMotionUtil;
import de.prob.ui.ProBConfiguration;

public class VisualizationViewDialog extends Dialog {

	private TableViewer view;
	
	private Object selection;

	protected VisualizationViewDialog(final Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(final Composite parent) {

		Composite container = (Composite) super.createDialogArea(parent);

		List<Object> content = new ArrayList<Object>();
		
		GridLayout gl = new GridLayout(1, true);
		gl.horizontalSpacing = 0;
		container.setLayout(gl);

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalIndent = 0;

		view = new TableViewer(container, SWT.FULL_SELECTION
				| SWT.V_SCROLL);
		view.getTable().setLayoutData(gd);

		view.setContentProvider(new ArrayContentProvider());

		view.getTable().setLinesVisible(true);
		view.getTable().setHeaderVisible(true);
		view.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				selection = sel.getFirstElement();
			}
			
		});
		
		final TableViewerColumn column1 = new TableViewerColumn(view,
				SWT.NONE);
		column1.getColumn().setText("Name");
		column1.getColumn().setWidth(390);
		column1.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				Object element = cell.getElement();
				if(element instanceof IResource) {
					cell.setText(((IResource) element).getName());
				} else if (element instanceof DummyObject) {
					cell.setText(((DummyObject) element).getName());
				}
			}
		});
		
		IFile modelFile = ProBConfiguration.getCurrentModelFile();
		
		if (modelFile != null) {
			IResource[] visualizationViewFiles = BMotionUtil
					.getVisualizationViewFiles(modelFile.getProject());
			
			java.util.Collections.addAll(content, visualizationViewFiles);
			content.add(new DummyObject());
			view.setInput(content);
		}

		return container;

	}

	@Override
	protected Point getInitialSize() {
		return new Point(500, 500);
	}

	@Override
	protected void configureShell(final Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("BMotion Studio - Add Visualization View Dialog");
	}
	
	public Object getSelection() {
		return selection;
	}

	class DummyObject {
		public String getName() {
			return "< New Visualization View >";
		}
	}

}
