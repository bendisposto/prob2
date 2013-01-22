package de.bmotionstudio.core.editor.wizard.observer;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.bmotionstudio.core.model.attribute.AbstractAttribute;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.observer.ExpressionObserver;
import de.bmotionstudio.core.model.observer.Observer;

public class ExpressionObserverWizard extends ObserverWizard {
	
	private final DataBindingContext dbc = new DataBindingContext();
	
	private Text nameText, expressionText;
	
	private ComboViewer attributeCombo;
	
	public ExpressionObserverWizard(Shell shell, BControl control,
			Observer observer) {
		super(shell, control, observer);
	}

	@Override
	public Point getSize() {
		return new Point(375, 300);
	}
	
	@Override
	public Control createDialogArea(Composite parent) {
		
		parent.setLayout(new GridLayout(1,true));
		
		GridLayout layout = new GridLayout(2,false);
		
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		container.setLayout(layout);
		
		GridData gridDataFill = new GridData(GridData.FILL_HORIZONTAL);
		
		GridData gridDataLabel = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gridDataLabel.widthHint = 75;
		gridDataLabel.heightHint = 25;

		Label label = new Label(container,SWT.NONE);
		label.setText("Name:");
		label.setLayoutData(gridDataLabel);
		
		nameText = new Text(container, SWT.BORDER);
		nameText.setLayoutData(gridDataFill);
			
		label = new Label(container,SWT.NONE);
		label.setText("Attribute:");
		label.setLayoutData(gridDataLabel);
		
		attributeCombo = new ComboViewer(container);
		attributeCombo.setContentProvider(new ArrayContentProvider());
		attributeCombo.setLabelProvider(new LabelProvider() {
			
			@Override
			public String getText(Object element) {
				AbstractAttribute atr = (AbstractAttribute) element;
				return atr.getName();
			}
			
		});
		attributeCombo.setInput(getControl().getAttributes().values());
		attributeCombo.getCombo().setLayoutData(gridDataFill);
		attributeCombo
				.addSelectionChangedListener(new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {

						ISelection selection = event.getSelection();
						if (selection instanceof StructuredSelection) {

							// StructuredSelection sel = (StructuredSelection)
							// selection;
							// AbstractAttribute atr = (AbstractAttribute) sel
							// .getFirstElement();
							//
							// String currentAttribute = ((ExpressionObserver)
							// getObserver())
							// .getAttribute();
							// if (currentAttribute == null
							// || (currentAttribute != null && !currentAttribute
							// .equals(atr.getID()))) {
							// ((ExpressionObserver) getObserver())
							// .setValue(atr.getValue());
							// }

						}

					}
				});
		
		label = new Label(container,SWT.NONE);
		label.setText("Expression:");
		label.setLayoutData(gridDataLabel);
			
		expressionText = new Text(container, SWT.BORDER);
		expressionText.setLayoutData(gridDataFill);

		initBindings(dbc);
		
		return container;
		
	}

	private void initBindings(DataBindingContext dbc) {

		dbc.bindValue(SWTObservables.observeText(nameText, SWT.Modify),
				BeansObservables.observeValue(
						(ExpressionObserver) getObserver(), "name"));

		dbc.bindValue(SWTObservables.observeText(expressionText, SWT.Modify),
				BeansObservables.observeValue(
						(ExpressionObserver) getObserver(), "expression"));
		
		IObservableValue typeSelection = ViewersObservables
				.observeSingleSelection(attributeCombo);
		IObservableValue myModelTypeObserveValue = BeansObservables
				.observeValue((ExpressionObserver) getObserver(), "attribute");
		
		dbc.bindValue(typeSelection, myModelTypeObserveValue,
				new UpdateValueStrategy() {

					@Override
					public Object convert(Object value) {
						return ((AbstractAttribute) value).getID();
					}

				}, new UpdateValueStrategy() {

					@Override
					public Object convert(Object value) {
						BControl control = getControl();
						return control.getAttribute(value.toString());
					}

				});

	}

}
