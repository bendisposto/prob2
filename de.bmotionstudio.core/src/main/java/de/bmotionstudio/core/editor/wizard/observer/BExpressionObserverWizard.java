package de.bmotionstudio.core.editor.wizard.observer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.draw2d.ColorConstants;
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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.google.inject.Injector;

import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.bmotionstudio.core.model.attribute.AbstractAttribute;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.observer.BExpressionObserver;
import de.bmotionstudio.core.model.observer.Observer;
import de.bmotionstudio.core.util.BMotionUtil;
import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.Trace;
import de.prob.webconsole.ServletContextListener;

public class BExpressionObserverWizard extends ObserverWizard {
	
	private final DataBindingContext dbc = new DataBindingContext();
	
	private Text nameText, expressionText, messageText;
	
	private ComboViewer attributeCombo;
	
	private Composite container;
	
	private Injector injector = ServletContextListener.INJECTOR;
	
	public BExpressionObserverWizard(Shell shell, BControl control,
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
		expressionText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {

				try {
					
					messageText.setText("");
					
					BParser.parse(BParser.EXPRESSION_PREFIX
							+ expressionText.getText());
					
					final AnimationSelector selector = injector
							.getInstance(AnimationSelector.class);
					Trace currentHistory = selector.getCurrentTrace();
					
					if(currentHistory != null) {
					
					EvaluationResult eval = currentHistory.evalCurrent(expressionText
							.getText());
						
						if (eval != null) {

							if (!eval.hasError()) {
								messageText.setText("Result: "
										+ eval.getValue());
								messageText
										.setForeground(ColorConstants.darkGreen);
							} else {
								messageText.setForeground(ColorConstants.red);
								messageText.setText("Error: "
										+ eval.getErrors());
							}

							messageText.redraw();

						}

					}
					
				} catch (BException e1) {
					messageText.setForeground(ColorConstants.red);
					messageText.setText("Error: " + e1.getMessage());
				} finally {
					messageText.redraw();
				}

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

				if (evt.getPropertyName().equals("expression")) {
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
						(BExpressionObserver) getObserver(), "name"));

		dbc.bindValue(SWTObservables.observeText(expressionText, SWT.Modify),
				BeansObservables.observeValue(
						(BExpressionObserver) getObserver(), "expression"));
		
		IObservableValue typeSelection = ViewersObservables
				.observeSingleSelection(attributeCombo);
		IObservableValue myModelTypeObserveValue = BeansObservables
				.observeValue((BExpressionObserver) getObserver(), "attribute");
		
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
