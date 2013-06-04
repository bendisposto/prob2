package de.bmotionstudio.core.editor.wizard.event;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.google.inject.Injector;

import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.event.Event;
import de.bmotionstudio.core.model.event.ExecuteBOperationEvent;
import de.prob.model.classicalb.ClassicalBMachine;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.classicalb.Operation;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.EventBModel;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.Trace;
import de.prob.webconsole.ServletContextListener;

public class BExecuteOperationWizard extends EventWizard {
	
	private final DataBindingContext dbc = new DataBindingContext();
	
	private Text nameText, predicateText, messageText;
	
	private ComboViewer operationCombo;
	
	private Composite container;
	
	private Injector injector = ServletContextListener.INJECTOR;
	
	public BExecuteOperationWizard(Shell shell, BControl control, Event event) {
		super(shell, control, event);
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
					List<de.prob.model.eventb.Event> events = eMachine.getEvents();
					for (de.prob.model.eventb.Event e : events)
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
		
		label = new Label(container,SWT.NONE);
		label.setText("Predicate:");
		label.setLayoutData(gridDataLabel);
		
		predicateText = new Text(container, SWT.BORDER);
		predicateText.setLayoutData(gridDataFill);
			
		label = new Label(container,SWT.NONE);
		label.setText("");
		label.setLayoutData(gridDataLabel);

		messageText = new Text(container,SWT.MULTI | SWT.WRAP);
		messageText.setText("");
		messageText.setForeground(ColorConstants.red);
		messageText.setBackground(ColorConstants.menuBackground);
		messageText.setLayoutData(new GridData(GridData.FILL_BOTH));
			
		initBindings(dbc);
		
		return container;
		
	}

	private void initBindings(DataBindingContext dbc) {

		dbc.bindValue(SWTObservables.observeText(nameText, SWT.Modify),
				BeansObservables.observeValue(
						(ExecuteBOperationEvent) getEvent(), "name"));

		dbc.bindValue(SWTObservables.observeText(predicateText, SWT.Modify),
				BeansObservables.observeValue(
						(ExecuteBOperationEvent) getEvent(), "predicate"));

		IObservableValue typeSelection = ViewersObservables
				.observeSingleSelection(operationCombo);
		IObservableValue myModelTypeObserveValue = BeansObservables
				.observeValue((ExecuteBOperationEvent) getEvent(), "operation");
		dbc.bindValue(typeSelection, myModelTypeObserveValue);

	}

}
