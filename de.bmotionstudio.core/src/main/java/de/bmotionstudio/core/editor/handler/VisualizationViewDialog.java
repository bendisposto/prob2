/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
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
import de.prob.model.representation.AbstractModel;

public class VisualizationViewDialog extends Dialog {

	private TableViewer view;
	
	private Object selection;
	
	private AbstractModel currentModel;

	public VisualizationViewDialog(Shell parentShell, AbstractModel currentModel) {
		super(parentShell);
		this.currentModel = currentModel;
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		
		Composite container = (Composite) super.createDialogArea(parent);

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
		view.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				selection = sel.getFirstElement();
				setReturnCode(Dialog.OK);
				close();
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
				if(element instanceof File) {
					cell.setText(((File) element).getName());
				} else if (element instanceof DummyObject) {
					cell.setText(((DummyObject) element).getName());
				}
			}
		});

		List<Object> content = new ArrayList<Object>();
		if (this.currentModel != null) {
			String language = BMotionUtil
					.getLanguageFromModel(this.currentModel);
			if (language != null) {
				File[] visualizationViewFiles = BMotionUtil
						.getVisualizationViewFiles(
								this.currentModel.getModelFile(), language);
				java.util.Collections.addAll(content, visualizationViewFiles);
				content.add(new DummyObject(language));
			}
		}
		view.setInput(content);
		
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

		private String language;

		public DummyObject(String language) {
			this.language = language;
		}

		public String getName() {
			return "< New Visualization View >";
		}

		public String getLanguage() {
			return language;
		}

	}

}
