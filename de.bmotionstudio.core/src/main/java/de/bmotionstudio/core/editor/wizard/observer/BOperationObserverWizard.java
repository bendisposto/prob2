package de.bmotionstudio.core.editor.wizard.observer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.google.inject.Injector;

import de.bmotionstudio.core.editor.edit.AttributeExpressionEdittingSupport;
import de.bmotionstudio.core.model.attribute.AbstractAttribute;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.observer.BOperationObserver;
import de.bmotionstudio.core.model.observer.Observer;
import de.bmotionstudio.core.util.BMotionUtil;
import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.model.classicalb.ClassicalBMachine;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.classicalb.Operation;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.EventBModel;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.Trace;
import de.prob.webconsole.ServletContextListener;

public class BOperationObserverWizard extends ObserverWizard {
	
	private final DataBindingContext dbc = new DataBindingContext();
	
	private TableViewer tableViewer;
	
	private Text nameText, predicateText, messageText;
	
	private ComboViewer operationCombo, attributeCombo;
	
	private Composite container;
	
	private Injector injector = ServletContextListener.INJECTOR;
	
	public BOperationObserverWizard(Shell shell, BControl control,
			Observer observer) {
		super(shell, control, observer);
	}

	@Override
	public Point getSize() {
		return new Point(375, 325);
	}
	
	@Override
	public Control createDialogArea(Composite parent) {
		
		parent.setLayout(new GridLayout(1,true));
		
		GridLayout layout = new GridLayout(2,false);
		
		container = new Composite(parent, SWT.NONE);
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
		label.setText("Operation:");
		label.setLayoutData(gridDataLabel);
		
		operationCombo = new ComboViewer(container);
		operationCombo.setContentProvider(new ArrayContentProvider());

		List<String> eventList = new ArrayList<String>();

		// Collect event of the active machine
		final AnimationSelector selector = injector
				.getInstance(AnimationSelector.class);
		Trace currentHistory = selector.getCurrentTrace();
		if (currentHistory != null) {
			AbstractModel model = currentHistory.getModel();
			if (model instanceof EventBModel) {
				EventBModel eventBModel = (EventBModel) model;
				AbstractElement mainComponent = eventBModel.getMainComponent();
				if (mainComponent instanceof EventBMachine) {
					EventBMachine eMachine = (EventBMachine) mainComponent;
					List<Event> events = eMachine.getEvents();
					for (Event e : events)
						eventList.add(e.getName());
				}
			} else if (model instanceof ClassicalBModel) {
				ClassicalBModel cModel = (ClassicalBModel) model;
				ClassicalBMachine mainMachine = cModel.getMainMachine();
				for (Operation e : mainMachine.getOperations())
					eventList.add(e.getName());
			}
		}

		operationCombo.setInput(eventList);
		operationCombo.getCombo().setLayoutData(gridDataFill);
		operationCombo
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
		label.setText("Predicate:");
		label.setLayoutData(gridDataLabel);
		
		predicateText = new Text(container, SWT.BORDER);
		predicateText.setLayoutData(gridDataFill);
			
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
							
							StructuredSelection sel = (StructuredSelection) selection;
							AbstractAttribute atr = (AbstractAttribute) sel
									.getFirstElement();

							String currentAttribute = ((BOperationObserver) getObserver())
									.getAttribute();
							if (currentAttribute == null
									|| (currentAttribute != null && !currentAttribute
											.equals(atr.getID()))) {
								((BOperationObserver) getObserver())
										.setValue(atr.getValue());
							}

							tableViewer.setInput(atr);
							tableViewer.refresh();
							
						}

					}
				});
		
		label = new Label(container,SWT.NONE);
		label.setText("Value:");
		label.setLayoutData(gridDataLabel);
			
		tableViewer = new TableViewer(container, SWT.NONE);		
		tableViewer.getTable().setHeaderVisible(false);
		tableViewer.getTable().setLinesVisible(false);
		tableViewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}

			@Override
			public Object[] getElements(Object inputElement) {
				return new Object[] {inputElement};
			}
			
		});

		tableViewer.getTable().setLayoutData(gridDataFill);
		tableViewer.getTable().addListener(SWT.EraseItem, new Listener() {
			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				event.gc.setBackground(ColorConstants.white);
				event.gc.fillRectangle(event.getBounds());
			}
		});
		
		TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setResizable(false);
		column.getColumn().setWidth(215);
		column.setEditingSupport(new AttributeExpressionEdittingSupport(
				tableViewer, getControl()) {

			@Override
			protected void setValue(Object element, Object value) {
				((BOperationObserver) getObserver()).setValue(value);
				tableViewer.refresh();
			}

			@Override
			protected Object getValue(Object element) {
				return ((BOperationObserver) getObserver()).getValue();
			}

		});
		column.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				Object value = ((BOperationObserver) getObserver()).getValue();
				if (value != null)
					cell.setText(value.toString());
			}
		});
		
		label = new Label(container,SWT.NONE);
		label.setText("");
		label.setLayoutData(gridDataLabel);

		messageText = new Text(container,SWT.MULTI | SWT.WRAP);
		messageText.setText("");
		messageText.setForeground(ColorConstants.red);
		messageText.setBackground(ColorConstants.menuBackground);
		messageText.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		getObserver().addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {

				if (evt.getPropertyName().equals("predicate")) {
					final AnimationSelector selector = injector
							.getInstance(AnimationSelector.class);
					Trace currentHistory = selector.getCurrentTrace();
					Map<String, EvaluationResult> evaluationResults = BMotionUtil
							.getEvaluationResults(
									currentHistory,
									getControl().prepareObserver(getObserver(),
											currentHistory));
					getObserver().check(currentHistory, getControl(),
							evaluationResults);
				}

			}

		});
		
		initBindings(dbc);
		
		return container;
		
	}

	private void initBindings(DataBindingContext dbc) {

		
		
		dbc.bindValue(SWTObservables.observeText(nameText, SWT.Modify),
				BeansObservables.observeValue(
						(BOperationObserver) getObserver(), "name"));

		dbc.bindValue(SWTObservables.observeText(predicateText, SWT.Modify),
				BeansObservables.observeValue(
						(BOperationObserver) getObserver(), "predicate"));
		
		IObservableValue typeSelection = ViewersObservables
				.observeSingleSelection(operationCombo);
		IObservableValue myModelTypeObserveValue = BeansObservables
				.observeValue((BOperationObserver) getObserver(), "operation");
		dbc.bindValue(typeSelection, myModelTypeObserveValue);
		
		IObservableValue typeSelection2 = ViewersObservables
				.observeSingleSelection(attributeCombo);
		IObservableValue myModelTypeObserveValue2 = BeansObservables
				.observeValue((BOperationObserver) getObserver(), "attribute");
		
		dbc.bindValue(typeSelection2, myModelTypeObserveValue2,
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
