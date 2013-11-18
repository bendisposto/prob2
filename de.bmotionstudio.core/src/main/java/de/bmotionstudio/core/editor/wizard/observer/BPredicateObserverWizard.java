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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.google.inject.Injector;

import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.bmotionstudio.core.editor.edit.AttributeExpressionEdittingSupport;
import de.bmotionstudio.core.model.attribute.AbstractAttribute;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.observer.BPredicateObserver;
import de.bmotionstudio.core.model.observer.Observer;
import de.bmotionstudio.core.util.BMotionUtil;
import de.prob.animator.domainobjects.ComputationNotCompletedResult;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.IEvalResult;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.Trace;
import de.prob.webconsole.ServletContextListener;

public class BPredicateObserverWizard extends ObserverWizard {

	private TableViewer tableViewer;

	private final DataBindingContext dbc = new DataBindingContext();

	private final Injector injector = ServletContextListener.INJECTOR;

	private Text predicateText, nameText, messageText;

	private ComboViewer attributeCombo;

	public BPredicateObserverWizard(final Shell shell, final BControl control,
			final Observer observer) {
		super(shell, control, observer);
	}

	@Override
	public Point getSize() {
		return new Point(375, 325);
	}

	@Override
	public Control createDialogArea(final Composite parent) {

		parent.setLayout(new GridLayout(1, true));

		GridLayout layout = new GridLayout(2, false);

		Composite container = new Composite(parent, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		container.setLayout(layout);

		GridData gridDataFill = new GridData(GridData.FILL_HORIZONTAL);

		GridData gridDataLabel = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gridDataLabel.widthHint = 75;
		gridDataLabel.heightHint = 25;

		Label label = new Label(container, SWT.NONE);
		label.setText("Name:");
		label.setLayoutData(gridDataLabel);

		nameText = new Text(container, SWT.BORDER);
		nameText.setLayoutData(gridDataFill);

		label = new Label(container, SWT.NONE);
		label.setText("Predicate:");
		label.setLayoutData(gridDataLabel);

		predicateText = new Text(container, SWT.BORDER);
		predicateText.setLayoutData(gridDataFill);
		predicateText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {

				try {

					messageText.setText("");

					BParser.parse(BParser.PREDICATE_PREFIX
							+ predicateText.getText());

					final AnimationSelector selector = injector
							.getInstance(AnimationSelector.class);
					Trace currentTrace = selector.getCurrentTrace();

					if (currentTrace != null) {

						IEvalResult eval = currentTrace
								.evalCurrent(predicateText.getText());

						if (eval != null) {

							if (eval instanceof EvalResult) {
								messageText.setText("Result: "
										+ ((EvalResult) eval).getValue());
								messageText
										.setForeground(ColorConstants.darkGreen);
							} else {
								messageText.setForeground(ColorConstants.red);
								messageText
										.setText("Error: "
												+ ((ComputationNotCompletedResult) eval)
														.getReason());
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

		label = new Label(container, SWT.NONE);
		label.setText("Attribute:");
		label.setLayoutData(gridDataLabel);

		attributeCombo = new ComboViewer(container);
		attributeCombo.setContentProvider(new ArrayContentProvider());
		attributeCombo.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(final Object element) {
				AbstractAttribute atr = (AbstractAttribute) element;
				return atr.getName();
			}

		});
		attributeCombo.setInput(getControl().getAttributes().values());
		attributeCombo.getCombo().setLayoutData(gridDataFill);
		attributeCombo
				.addSelectionChangedListener(new ISelectionChangedListener() {
					@Override
					public void selectionChanged(
							final SelectionChangedEvent event) {

						ISelection selection = event.getSelection();
						if (selection instanceof StructuredSelection) {

							StructuredSelection sel = (StructuredSelection) selection;
							AbstractAttribute atr = (AbstractAttribute) sel
									.getFirstElement();

							String currentAttribute = ((BPredicateObserver) getObserver())
									.getAttribute();
							if (currentAttribute == null
									|| (currentAttribute != null && !currentAttribute
											.equals(atr.getID()))) {
								((BPredicateObserver) getObserver())
										.setValue(atr.getValue());
							}

							tableViewer.setInput(atr);
							tableViewer.refresh();

						}

					}
				});

		label = new Label(container, SWT.NONE);
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
			public void inputChanged(final Viewer viewer,
					final Object oldInput, final Object newInput) {
			}

			@Override
			public Object[] getElements(final Object inputElement) {
				return new Object[] { inputElement };
			}

		});

		tableViewer.getTable().setLayoutData(gridDataFill);
		tableViewer.getTable().addListener(SWT.EraseItem, new Listener() {
			@Override
			public void handleEvent(final Event event) {
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
			protected void setValue(final Object element, final Object value) {
				((BPredicateObserver) getObserver()).setValue(value);
				tableViewer.refresh();
			}

			@Override
			protected Object getValue(final Object element) {
				return ((BPredicateObserver) getObserver()).getValue();
			}

		});
		column.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				Object value = ((BPredicateObserver) getObserver()).getValue();
				if (value != null) {
					cell.setText(value.toString());
				}
			}
		});

		label = new Label(container, SWT.NONE);
		label.setText("");
		label.setLayoutData(gridDataLabel);

		messageText = new Text(container, SWT.MULTI | SWT.WRAP);
		messageText.setText("");
		messageText.setForeground(ColorConstants.red);
		messageText.setBackground(ColorConstants.menuBackground);
		messageText.setLayoutData(new GridData(GridData.FILL_BOTH));

		getObserver().addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent evt) {

				if (evt.getPropertyName().equals("predicate")) {
					final AnimationSelector selector = injector
							.getInstance(AnimationSelector.class);
					Trace currentTrace = selector.getCurrentTrace();
					Map<String, IEvalResult> evaluationResults = BMotionUtil
							.getEvaluationResults(
									currentTrace,
									getControl().prepareObserver(getObserver(),
											currentTrace));
					getObserver().check(currentTrace, getControl(),
							evaluationResults);
				}

			}

		});

		initBindings(dbc);

		return container;

	}

	private void initBindings(final DataBindingContext dbc) {

		dbc.bindValue(SWTObservables.observeText(nameText, SWT.Modify),
				BeansObservables.observeValue(getObserver(), "name"));

		dbc.bindValue(SWTObservables.observeText(predicateText, SWT.Modify),
				BeansObservables.observeValue(getObserver(), "predicate"));

		IObservableValue typeSelection = ViewersObservables
				.observeSingleSelection(attributeCombo);
		IObservableValue myModelTypeObserveValue = BeansObservables
				.observeValue(getObserver(), "attribute");

		dbc.bindValue(typeSelection, myModelTypeObserveValue,
				new UpdateValueStrategy() {

					@Override
					public Object convert(final Object value) {
						return ((AbstractAttribute) value).getID();
					}

				}, new UpdateValueStrategy() {

					@Override
					public Object convert(final Object value) {
						BControl control = getControl();
						return control.getAttribute(value.toString());
					}

				});

	}

}
