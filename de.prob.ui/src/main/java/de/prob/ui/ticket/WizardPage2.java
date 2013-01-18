package de.prob.ui.ticket;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class WizardPage2 extends WizardPage {
	private Composite container;
	public boolean hasAccount;
	private Text textUser;
	private Text textPswd;
	private Button buttonAccount;
	private Button buttonSave;

	private final String username;
	private final String password;
	private boolean saveUsr;

	public WizardPage2(final String username, final String password,
			final boolean saveUsr) {
		super("Wizard Page 2");
		this.username = username;
		this.password = password;
		this.saveUsr = saveUsr;
		hasAccount = false;

		setTitle("Submit Bugreport");
		setDescription("Credentials");
	}

	@Override
	public void createControl(final Composite parent) {
		container = new Composite(parent, SWT.NULL);
		createPageForAccount();
		setControl(container);
		setPageComplete(checkPageComplete());
	}

	private void createPageForAccount() {
		GridLayout layout = new GridLayout();
		container.setLayout(layout);

		layout.numColumns = 2;

		// Private:
		Label labelAccount = new Label(container, SWT.NONE);
		labelAccount
				.setText("Select if you have an account with the ProB Bugtracker.\n"
						+ "If you often submit bugreports, go to the Bugtracker website\n"
						+ "(http://jira.cobra.cs.uni-duesseldorf.de/) to create an account.\n"
						+ "Otherwise, default values will be used.");
		buttonAccount = new Button(container, SWT.CHECK);
		buttonAccount.setSelection(this.hasAccount);

		buttonAccount.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
			}

			@Override
			public void widgetSelected(final SelectionEvent arg0) {
				hasAccount = buttonAccount.getSelection();
				setPageComplete(checkPageComplete());
			}
		});

		Label labelUserName = new Label(container, SWT.NULL);
		labelUserName.setText("Enter username:");
		textUser = new Text(container, SWT.BORDER | SWT.SINGLE);
		textUser.setText(username);
		textUser.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(final KeyEvent e) {
			}

			@Override
			public void keyReleased(final KeyEvent e) {
				setPageComplete(checkPageComplete());
			}

		});

		Label labelPswd = new Label(container, SWT.NULL);
		labelPswd.setText("Enter password:");
		textPswd = new Text(container, SWT.BORDER | SWT.PASSWORD);
		textPswd.setText(password);
		textPswd.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(final KeyEvent e) {
			}

			@Override
			public void keyReleased(final KeyEvent e) {
				setPageComplete(checkPageComplete());
			}

		});
		textPswd.setVisible(true);

		Label labelSaveUsr = new Label(container, SWT.NONE);
		labelSaveUsr.setText("Save my username and password");
		buttonSave = new Button(container, SWT.CHECK);
		buttonSave.setSelection(this.saveUsr);

		buttonSave.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
			}

			@Override
			public void widgetSelected(final SelectionEvent arg0) {
				saveUsr = buttonSave.getSelection();
			}
		});

		// Resize Text-Fields
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		textUser.setLayoutData(gd);
		textPswd.setLayoutData(gd);
	}

	private boolean checkPageComplete() {
		if (!hasAccount) {
			setErrorMessage(null);
			return true;
		}

		Boolean usrFilled = !textUser.getText().equals("");
		Boolean pswdFilled = !textPswd.getText().equals("");

		if (usrFilled && pswdFilled) {
			setErrorMessage(null);
			return true;
		} else {
			if (!usrFilled) {
				setErrorMessage("Please enter your username for the ProB Bugtracker.");
				return false;
			}
			if (!pswdFilled) {
				setErrorMessage("Please enter your password for the ProB Bugtracker.");
				return false;
			}
			return false;
		}
	}

	public boolean hasAccount() {
		return hasAccount;
	}

	public String getUsername() {
		if (hasAccount) {
			return textUser.getText();
		}
		return "prob_reporter";
	}

	public String getPassword() {
		if (hasAccount) {
			return textPswd.getText();
		}
		return "prob";
	}

	public boolean isSaveUsr() {
		return saveUsr;
	}

}
