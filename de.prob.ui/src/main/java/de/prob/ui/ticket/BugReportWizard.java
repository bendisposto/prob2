package de.prob.ui.ticket;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.wizard.Wizard;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

public class BugReportWizard extends Wizard {

	Logger logger = LoggerFactory.getLogger(BugReportWizard.class);

	private WizardPage1 page1;
	private WizardPage2 page2;
	private WizardPage3 page3;

	private String email = "";
	private String summary = "";
	private Boolean addTrace = true;
	private String description = "";

	private boolean savedUsername = false;
	private String username = "";
	private String password = "";

	private static final Preferences TICKET_PREFS = Platform
			.getPreferencesService().getRootNode().node(InstanceScope.SCOPE)
			.node("prob_ticket_preferences");

	public BugReportWizard() {
		super();
		setNeedsProgressMonitor(true);
		email = TICKET_PREFS.get("email", "");
		savedUsername = TICKET_PREFS.getBoolean("saveUsr", false);
		username = savedUsername ? TICKET_PREFS.get("usr", "") : "";
		password = savedUsername ? TICKET_PREFS.get("pswd", "") : "";

	}

	public BugReportWizard(final String summary, final Boolean addTrace,
			final String description) {
		super();
		setNeedsProgressMonitor(true);

		email = TICKET_PREFS.get("email", "");
		savedUsername = TICKET_PREFS.getBoolean("saveUsr", false);
		username = savedUsername ? TICKET_PREFS.get("usr", "") : "";
		password = savedUsername ? TICKET_PREFS.get("pswd", "") : "";
		this.summary = summary;
		this.addTrace = addTrace;
		this.description = description;
	}

	@Override
	public void addPages() {
		page1 = new WizardPage1(email, summary, description, addTrace, true);
		page2 = new WizardPage2(username, password, savedUsername);
		page3 = new WizardPage3();
		addPage(page1);
		addPage(page2);
		addPage(page3);

	}

	@Override
	public boolean performFinish() {

		TICKET_PREFS.put("email", page1.getEmail());
		boolean saveUsr = page2.isSaveUsr();
		if (saveUsr) {
			TICKET_PREFS.putBoolean("saveUsr", true);
			TICKET_PREFS.put("usr", page2.getUsername());
			TICKET_PREFS.put("pswd", page2.getPassword());
		} else {
			TICKET_PREFS.remove("saveUsr");
			TICKET_PREFS.remove("usr");
			TICKET_PREFS.remove("pswd");
		}
		try {
			TICKET_PREFS.flush();
		} catch (BackingStoreException e) {
			logger.error(
					"Problem while storing preferences. "
							+ e.getLocalizedMessage(), e);
		}

		URI jiraServerUri;
		try {
			IssueInputBuilder issueBuilder = new IssueInputBuilder("PROBCORE",
					(long) 1, page1.getSummary());
			issueBuilder.setDescription("FROM: " + page1.getEmail() + "\n\n"
					+ page1.getDetailedDescription());

			if (page1.isSensitive()) {
				issueBuilder.setFieldValue("security",
						ComplexIssueInputFieldValue.with("id", "10000"));
			}

			String address = "http://jira.cobra.cs.uni-duesseldorf.de/";
			jiraServerUri = new URI(address);

			AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
			JiraRestClient client = factory.createWithBasicHttpAuthentication(
					jiraServerUri, page2.getUsername(), page2.getPassword());

			Promise<BasicIssue> createIssue = client.getIssueClient()
					.createIssue(issueBuilder.build());

			BasicIssue f = createIssue.get();

			String issueURI = "http://jira.cobra.cs.uni-duesseldorf.de/rest/api/2/issue/"
					+ f.getKey();

			if (page3.hasAttachments()) {
				URI attachmentUri = new URI(issueURI + "/attachments");
				client.getIssueClient().addAttachments(attachmentUri,
						page3.getAttachmentInputs());
			}

		} catch (URISyntaxException e) {
			logger.error("The website url was incorrect");
		} catch (InterruptedException e) {
			logger.error("Could not create issue. Connection interrupted.");
		} catch (ExecutionException e) {
			logger.error("Could not create issue. Execution failed.");
		}

		return true;
	}

}
