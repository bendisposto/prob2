/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.edit;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.util.BMotionWizardUtil;

public class OperationValueEditingSupport extends EditingSupport {

	private ComboBoxViewerCellEditor cellEditor = null;

	private BControl control;

	public OperationValueEditingSupport(TableViewer cv, BControl control) {
		super(cv);
		this.control = control;
	}

	@Override
	protected boolean canEdit(Object element) {
		return BMotionWizardUtil.isEditElement(getViewer());
	}

	@Override
	protected Object getValue(Object element) {
		// TODO: Reimplement me!!!
//		return ((PredicateOperation) element).getOperationName();
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		// TODO: Reimplement me!!!
//		if (value != null)
//			((PredicateOperation) element).setOperationName(value.toString());
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		// TODO: Reimplement me!!!
//		if (cellEditor == null) {
//			cellEditor = new ComboBoxViewerCellEditor((Composite) getViewer()
//					.getControl(), SWT.READ_ONLY);
//			cellEditor.setContentProvider(new ObservableListContentProvider());
//			cellEditor.setInput(new ComputedList() {
//				@Override
//				protected List<String> calculate() {
//					ArrayList<String> tmpList = new ArrayList<String>();
//					for (MachineContentObject op : EventBHelper
//							.getOperations(control.getVisualization())) {
//						tmpList.add(((MachineOperation) op).getLabel());
//					}
//					return tmpList;
//				}
//			});
//		}
//		return cellEditor;
		return null;
	}

}
