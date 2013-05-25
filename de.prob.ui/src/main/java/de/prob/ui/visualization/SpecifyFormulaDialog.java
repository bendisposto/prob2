package de.prob.ui.visualization;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.prob.animator.domainobjects.CSP;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EventB;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.eventb.EventBModel;
import de.prob.model.representation.AbstractModel;
import de.prob.scripting.CSPModel;

public class SpecifyFormulaDialog extends TitleAreaDialog {

	private Text formulaText;
	private Text timeExpressionText;
	private String expression;
	private String timeExpression;
	private final AbstractModel model;

	public SpecifyFormulaDialog(final Shell parentShell,
			final AbstractModel model) {
		super(parentShell);
		this.model = model;
	}

	@Override
	public void create() {
		super.create();
		// Set the title
		setTitle("Formula Input");
		// Set the message
		setMessage("Input formula for visualization.",
				IMessageProvider.INFORMATION);

	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		// layout.horizontalAlignment = GridData.FILL;
		parent.setLayout(layout);

		// The text fields will grow with the size of the dialog
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		Label label1 = new Label(parent, SWT.NONE);
		label1.setText("Input expression:");

		formulaText = new Text(parent, SWT.BORDER);
		formulaText.setLayoutData(gridData);

		Label label2 = new Label(parent, SWT.NONE);
		label2.setText("Input Time Expression (Optional):");
		// You should not re-use GridData
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		timeExpressionText = new Text(parent, SWT.BORDER);
		timeExpressionText.setLayoutData(gridData);
		return parent;
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = SWT.CENTER;

		parent.setLayoutData(gridData);
		// Create Add button
		// Own method as we need to overview the SelectionAdapter
		createOkButton(parent, OK, "Add", true);
		// Add a SelectionListener

		// Create Cancel button
		Button cancelButton = createButton(parent, CANCEL, "Cancel", false);
		// Add a SelectionListener
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				setReturnCode(CANCEL);
				close();
			}
		});
	}

	protected Button createOkButton(final Composite parent, final int id,
			final String label, final boolean defaultButton) {
		// increment the number of columns in the button bar
		((GridLayout) parent.getLayout()).numColumns++;
		Button button = new Button(parent, SWT.PUSH);
		button.setText(label);
		button.setFont(JFaceResources.getDialogFont());
		button.setData(new Integer(id));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (isValidInput()) {
					okPressed();
				}
			}
		});
		if (defaultButton) {
			Shell shell = parent.getShell();
			if (shell != null) {
				shell.setDefaultButton(button);
			}
		}
		setButtonLayoutData(button);
		return button;
	}

	private boolean isValidInput() {
		String message = "Please enter a valid formula!";
		boolean valid = true;
		if (formulaText.getText().length() == 0
				|| !isValidFormula(formulaText.getText())) {
			setErrorMessage(message);
			valid = false;
		}
		if (timeExpressionText.getText().length() > 0
				&& !isValidFormula(timeExpressionText.getText())) {
			setErrorMessage("Please maintain the last name");
			valid = false;
		}
		return valid;
	}

	private boolean isValidFormula(final String newText) {
		try {
			if (model instanceof ClassicalBModel) {
				new ClassicalB(newText);
			} else if (model instanceof EventBModel) {
				new EventB(newText);
			} else if (model instanceof CSPModel) {
				new CSP(newText, (CSPModel) model);
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	// Coyy textFields because the UI gets disposed
	// and the Text Fields are not accessible any more.
	private void saveInput() {
		expression = formulaText.getText();
		timeExpression = timeExpressionText.getText();
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public String getExpression() {
		return expression;
	}

	public String getTimeExpression() {
		return timeExpression;
	}
}